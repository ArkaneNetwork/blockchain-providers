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
package com.wealdtech.bitcoin.crypto;


import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;

import java.io.Serializable;
import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A signature created using ECDSA.
 * These signatures are used to validate transactions
 */
public class ECSignature implements Serializable, Comparable<ECSignature> {
    private static final long serialVersionUID = 9161326405924378230L;

    private final ImmutableList<Byte> bytes;

    public ECSignature(final ImmutableList<Byte> bytes) {
        checkNotNull(bytes, "Signature cannot be NULL");
        this.bytes = bytes;
    }

    public ImmutableList<Byte> getBytes() {
        return this.bytes;
    }

    // Standard object methods follow
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("bytes", this.bytes)
                      .toString();
    }

    /**
     * ECDSA signatures contain a random element, which means that signing the same
     * data twice will result in different signatures.  This means that code such as
     * <code>sign(data).equals(sign(data))</code> will always return <code>false</code>.
     */
    @Override
    public boolean equals(final Object that) {
        return (that instanceof ECSignature) && (this.compareTo((ECSignature) that) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.bytes);
    }

    @Override
    public int compareTo(final ECSignature that) {
        return ComparisonChain.start()
                              .compare(new BigInteger(1, Bytes.toArray(this.getBytes())), new BigInteger(1, Bytes.toArray(that.getBytes())))
                              .result();
    }
}
