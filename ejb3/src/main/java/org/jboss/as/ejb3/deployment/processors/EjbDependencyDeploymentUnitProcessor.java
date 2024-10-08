/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2011, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.as.ejb3.deployment.processors;

import static org.jboss.as.server.deployment.EjbDeploymentMarker.isEjbDeployment;

import org.jboss.as.ee.structure.DeploymentType;
import org.jboss.as.ee.structure.DeploymentTypeMarker;
import org.jboss.as.server.deployment.Attachments;
import org.jboss.as.server.deployment.DeploymentPhaseContext;
import org.jboss.as.server.deployment.DeploymentUnit;
import org.jboss.as.server.deployment.DeploymentUnitProcessingException;
import org.jboss.as.server.deployment.DeploymentUnitProcessor;
import org.jboss.as.server.deployment.module.ModuleDependency;
import org.jboss.as.server.deployment.module.ModuleSpecification;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoader;
import org.wildfly.iiop.openjdk.deployment.IIOPDeploymentMarker;

/**
 * Responsible for adding appropriate Jakarta EE {@link org.jboss.as.server.deployment.module.ModuleDependency module dependencies}
 * <p/>
 * Author : Jaikiran Pai
 */
public class EjbDependencyDeploymentUnitProcessor implements DeploymentUnitProcessor {

    /**
     * Needed for timer handle persistence
     * TODO: restrict visibility
     */
    private static final String EJB_SUBSYSTEM = "org.jboss.as.ejb3";
    private static final String EJB_CLIENT = "org.jboss.ejb-client";
    private static final String EJB_NAMING_CLIENT = "org.wildfly.naming-client";
    private static final String EJB_IIOP_CLIENT = "org.jboss.iiop-client";
    private static final String IIOP_OPENJDK = "org.wildfly.iiop-openjdk";
    private static final String EJB_API = "jakarta.ejb.api";
    private static final String HTTP_EJB = "org.wildfly.http-client.ejb";
    private static final String HTTP_TRANSACTION = "org.wildfly.http-client.transaction";
    private static final String HTTP_NAMING = "org.wildfly.http-client.naming";
    private static final String CLUSTERING_EJB_CLIENT = "org.wildfly.clustering.ejb.client";

    /**
     * Adds Jakarta EE module as a dependency to any deployment unit which is an Jakarta Enterprise Beans deployment
     *
     * @param phaseContext the deployment unit context
     * @throws DeploymentUnitProcessingException
     *
     */
    @Override
    public void deploy(DeploymentPhaseContext phaseContext) throws DeploymentUnitProcessingException {


        // get hold of the deployment unit
        DeploymentUnit deploymentUnit = phaseContext.getDeploymentUnit();

        final ModuleLoader moduleLoader = Module.getBootModuleLoader();
        final ModuleSpecification moduleSpecification = deploymentUnit.getAttachment(Attachments.MODULE_SPECIFICATION);

        //always add EE API
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, EJB_API, false, false, true, false));
        //we always give them the Jakarta Enterprise Beans client
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, EJB_CLIENT, false, false, true, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, EJB_NAMING_CLIENT, false, false, true, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, EJB_IIOP_CLIENT, false, false, false, false));

        //we always have to add this, as even non-ejb deployments may still lookup IIOP ejb's
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, EJB_SUBSYSTEM, false, false, true, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, HTTP_EJB, false, false, true, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, HTTP_NAMING, false, false, true, false));
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, HTTP_TRANSACTION, false, false, true, false));
        // Marshalling support for EJB SessionIDs
        // TODO Move this to distributable-ejb subsystem
        moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, CLUSTERING_EJB_CLIENT, false, false, true, false));

        if (IIOPDeploymentMarker.isIIOPDeployment(deploymentUnit)) {
            //needed for dynamic IIOP stubs
            moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, IIOP_OPENJDK, false, false, false, false));
        }

        // fetch the EjbJarMetaData
        //TODO: remove the app client bit after the next Jakarta Enterprise Beans release
        if (!isEjbDeployment(deploymentUnit) && !DeploymentTypeMarker.isType(DeploymentType.APPLICATION_CLIENT, deploymentUnit)) {
            // nothing to do
            return;
        }


        // FIXME: still not the best way to do it
        //this must be the first dep listed in the module
        if (Boolean.getBoolean("org.jboss.as.ejb3.EMBEDDED"))
            moduleSpecification.addSystemDependency(new ModuleDependency(moduleLoader, "Classpath", false, false, false, false));

    }
}
