/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */
package com.wealdtech.bitcoin.generator.raw;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.generator.Generator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;


public abstract class BaseGeneratorRawImpl<T> implements Generator<T> {
    protected ByteArrayOutputStream baos;

    @Override
    public void startGen() {
        startGen(null);
    }

    @Override
    public void startGen(final ByteArrayOutputStream baos) {
        if (baos == null) {
            this.baos = new ByteArrayOutputStream();
        } else {
            this.baos = baos;
        }
    }

    @Override
    public void generate(final T input) {
        generate(input, false);
    }

    @Override
    public void addRawBytes(final byte[] bytes) {
        try {
            this.baos.write(bytes);
        } catch (IOException ioe) {
            throw new RuntimeException("Failed to add raw bytes");
        }
    }

    @Override
    public ImmutableList<Byte> finishGen() {
        return ImmutableList.copyOf(Bytes.asList(this.baos.toByteArray()));
    }
}
