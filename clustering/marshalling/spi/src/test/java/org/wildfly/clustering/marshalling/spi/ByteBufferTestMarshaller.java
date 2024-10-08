/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2020, Red Hat, Inc., and individual contributors
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

package org.wildfly.clustering.marshalling.spi;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.OptionalInt;

import org.junit.Assert;
import org.wildfly.clustering.marshalling.TestMarshaller;

/**
 * A {@link ByteBufferMarshaller} based {@link TestMarshaller}.
 * @author Paul Ferraro
 */
public class ByteBufferTestMarshaller<T> implements TestMarshaller<T> {

    private final ByteBufferMarshaller marshaller;

    public ByteBufferTestMarshaller(ByteBufferMarshaller marshaller) {
        this.marshaller = marshaller;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T read(ByteBuffer buffer) throws IOException {
        return (T) this.marshaller.read(buffer);
    }

    @Override
    public ByteBuffer write(T object) throws IOException {
        ByteBuffer buffer = this.marshaller.write(object);
        OptionalInt size = this.marshaller.size(object);
        if (size.isPresent()) {
            Assert.assertEquals(buffer.remaining(), size.getAsInt());
        }
        return buffer;
    }
}
