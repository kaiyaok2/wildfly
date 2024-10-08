/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2018, Red Hat, Inc., and individual contributors
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

package org.wildfly.extension.clustering.web;

import java.util.EnumSet;

import org.jboss.as.clustering.subsystem.AdditionalInitialization;
import org.jboss.as.subsystem.test.AbstractSubsystemSchemaTest;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.wildfly.clustering.infinispan.client.service.InfinispanClientRequirement;
import org.wildfly.clustering.infinispan.service.InfinispanCacheRequirement;
import org.wildfly.clustering.infinispan.service.InfinispanDefaultCacheRequirement;

/**
 * Unit test for distributable-web subsystem.
 * @author Paul Ferraro
 */
@RunWith(value = Parameterized.class)
public class DistributableWebSubsystemTestCase extends AbstractSubsystemSchemaTest<DistributableWebSubsystemSchema> {

    @Parameters
    public static Iterable<DistributableWebSubsystemSchema> parameters() {
        return EnumSet.allOf(DistributableWebSubsystemSchema.class);
    }

    public DistributableWebSubsystemTestCase(DistributableWebSubsystemSchema schema) {
        super(DistributableWebExtension.SUBSYSTEM_NAME, new DistributableWebExtension(), schema, DistributableWebSubsystemSchema.CURRENT);
    }

    @Override
    protected org.jboss.as.subsystem.test.AdditionalInitialization createAdditionalInitialization() {
        return new AdditionalInitialization()
                .require(InfinispanDefaultCacheRequirement.CONFIGURATION, "foo")
                .require(InfinispanCacheRequirement.CONFIGURATION, "foo", "bar")
                .require(InfinispanClientRequirement.REMOTE_CONTAINER, "foo")
                ;
    }
}