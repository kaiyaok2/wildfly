/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2017, Red Hat, Inc., and individual contributors
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

package org.jboss.as.clustering.jgroups.subsystem;

import java.util.function.Consumer;

import org.jboss.as.clustering.controller.ResourceServiceConfigurator;
import org.jboss.as.controller.OperationContext;
import org.jboss.as.controller.OperationFailedException;
import org.jboss.as.controller.PathAddress;
import org.jboss.dmr.ModelNode;
import org.jboss.msc.Service;
import org.jboss.msc.service.ServiceBuilder;
import org.jboss.msc.service.ServiceController;
import org.jboss.msc.service.ServiceTarget;
import org.wildfly.clustering.service.ServiceConfigurator;

/**
 * @author Paul Ferraro
 */
public class ThreadPoolFactoryServiceConfigurator extends ThreadPoolServiceNameProvider implements ResourceServiceConfigurator, ThreadPoolConfiguration {

    private final ThreadPoolDefinition definition;

    private volatile int minThreads = 0;
    private volatile int maxThreads = Integer.MAX_VALUE;
    private volatile long keepAliveTime = 0;

    public ThreadPoolFactoryServiceConfigurator(ThreadPoolDefinition definition, PathAddress address) {
        super(address);
        this.definition = definition;
    }

    @Override
    public ServiceConfigurator configure(OperationContext context, ModelNode model) throws OperationFailedException {
        this.minThreads = this.definition.getMinThreads().resolveModelAttribute(context, model).asInt();
        this.maxThreads = this.definition.getMaxThreads().resolveModelAttribute(context, model).asInt();
        this.keepAliveTime = this.definition.getKeepAliveTime().resolveModelAttribute(context, model).asLong();
        return this;
    }

    @Override
    public ServiceBuilder<?> build(ServiceTarget target) {
        ServiceBuilder<?> builder = target.addService(this.getServiceName());
        Consumer<ThreadPoolConfiguration> factory = builder.provides(this.getServiceName());
        Service service = Service.newInstance(factory, this);
        return builder.setInstance(service).setInitialMode(ServiceController.Mode.ON_DEMAND);
    }

    @Override
    public int getMinThreads() {
        return this.minThreads;
    }

    @Override
    public int getMaxThreads() {
        return this.maxThreads;
    }

    @Override
    public long getKeepAliveTime() {
        return this.keepAliveTime;
    }
}
