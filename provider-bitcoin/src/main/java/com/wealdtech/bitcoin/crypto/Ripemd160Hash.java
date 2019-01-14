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
import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.generator.raw.Utils;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;

import java.util.Arrays;

import static com.google.common.base.Preconditions.checkArgument;

public class Ripemd160Hash implements Hash {
    private static final long serialVersionUID = 289792938400776250L;

    /**
     * The bytes which make up this hash
     */
    private final ImmutableList<Byte> bytes;

    /**
     * Instantiates a pre-computed RIPEMD-160 hash.
     *
     * @param rawHashBytes the pre-computed hash
     */
    public Ripemd160Hash(final ImmutableList<Byte> rawHashBytes) {
        checkArgument(rawHashBytes.size() == 20, "Input must be 20 bytes long");
        this.bytes = rawHashBytes;
    }

    /**
     * Instantiates a pre-computed RIPEMD-160 hash.
     *
     * @param hexString the hash in the form of a hex string
     */
    public static Ripemd160Hash fromHexString(final String hexString) {
        checkArgument(hexString.length() == 40, "Input must be 40 characters long");
        return new Ripemd160Hash(Utils.hexStringToBytes(hexString));
    }

    /**
     * Instantiate a new RIPEMD-160 hash for a given set of data
     *
     * @param contents the data for which to compute the hash
     */
    public static Ripemd160Hash create(ImmutableList<Byte> contents) {
        final RIPEMD160Digest digest = new RIPEMD160Digest();
        digest.update(Bytes.toArray(contents), 0, contents.size());
        final byte[] out = new byte[20];
        digest.doFinal(out, 0);
        return new Ripemd160Hash(ImmutableList.copyOf(Bytes.asList(out)));
    }

    @Override
    public ImmutableList<Byte> getBytes() {
        return this.bytes;
    }

    public Ripemd160Hash duplicate() {
        return new Ripemd160Hash(this.bytes);
    }

    // Standard object methods follow
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("hash", Utils.bytesToHexString(this.bytes))
                      .toString();
    }

    @Override
    public boolean equals(final Object that) {
        return (that instanceof Ripemd160Hash) && (this.compareTo((Ripemd160Hash) that) == 0);
    }

    /**
     * Hash code of the byte array as calculated by {@link Arrays#hashCode()}.
     * Note the difference between a SHA256 secure bytes and the type of
     * quick/dirty bytes used by the Java hashCode method which is designed for
     * use in bytes tables.
     */
    @Override
    public int hashCode() {
        // Use the last 4 bytes, not the first 4 which are often zeros in Bitcoin.
        return (this.bytes.get(31) & 0xFF) |
               ((this.bytes.get(30) & 0xFF) << 8) |
               ((this.bytes.get(29) & 0xFF) << 16) |
               ((this.bytes.get(28) & 0xFF) << 24);
    }

    @Override
    public int compareTo(final Hash that) {
        if (!(that instanceof Ripemd160Hash)) {
            return -1;
        }
        for (int i = 0; i < 20; i++) {
            if (this.bytes.get(i) < that.getBytes().get(i)) {
                return -1;
            }
            if (this.bytes.get(i) > that.getBytes().get(i)) {
                return 1;
            }
        }
        return 0;
    }
}
