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
package com.wealdtech.bitcoin.script;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.generator.raw.Utils;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * An Op is a single operation.
 * It is either an opcode or a set of data.
 */
public class Op implements Serializable, Comparable<Op> {
    private static final long serialVersionUID = 8035542146562071509L;

    private final Opcode opcode;
    private final ImmutableList<Byte> data;

    public Op(final Opcode opcode) {
        this.opcode = opcode;
        this.data = null;
    }

    public Op(final ImmutableList<Byte> data) {
        this.opcode = null;
        this.data = data;
    }

    public Opcode getOpcode() {
        return this.opcode;
    }

    public ImmutableList<Byte> getData() {
        return this.data;
    }

    public boolean isOpcode() {
        return this.opcode != null;
    }

    // Standard object methods follow
    @Override
    public String toString() {
        if (this.opcode == null) {
            // Simple data
            return Utils.bytesToHexString(this.data);
        } else {
            // Opcode
            return this.opcode.toString();
        }
    }

    @Override
    public boolean equals(final Object that) {
        return (that instanceof Op) && (this.compareTo((Op) that) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.opcode, this.data);
    }

    @Override
    public int compareTo(final Op that) {
        // This is an arbitrary, but consistent, comparison
        int result = ComparisonChain.start()
                                    .compare(this.opcode, that.opcode, Ordering.natural().nullsFirst())
                                    .result();
        if (result == 0) {
            // Need to compare data
            if (this.data != that.data) {
                if (this.data == null) {
                    result = -1;
                } else if (that.data == null) {
                    result = 1;
                } else if (this.data == null) {
                    result = -1;
                } else if (that.data == null) {
                    result = 1;
                } else {
                    result = ComparisonChain.start()
                                            .compare(new BigInteger(1, Bytes.toArray(this.data)), new BigInteger(1, Bytes.toArray(that.data)))
                                            .result();
                }
            }
        }
        return result;
    }
}
