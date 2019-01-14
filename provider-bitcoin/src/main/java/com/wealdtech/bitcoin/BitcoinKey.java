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
package com.wealdtech.bitcoin;

import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.wealdtech.bitcoin.crypto.Crypto;
import com.wealdtech.bitcoin.crypto.Hash;
import com.wealdtech.bitcoin.utils.Base58;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * In Bitcoin the following format is often used to represent some type of key:
 * </p>
 * <p/>
 *
 * <pre>
 * [one version byte] [hash] [4 checksum bytes]
 * </pre>
 * <p/>
 * <p>
 * and the result is then Base58 encoded. This format is used for addresses, and
 * private keys exported using the dumpprivkey command.
 * </p>
 */
public class BitcoinKey implements Serializable, Comparable<BitcoinKey> {
    private static final long serialVersionUID = 4123495307269792060L;

    protected Network network;
    protected Hash hash;

    /**
     * Instantiate a Bitcoin key given a network and a hash.
     *
     * @param network the network on which this address lives
     * @param hash    the hash of the address
     */
    public BitcoinKey(final Network network, final Hash hash) {
        this.network = network;
        this.hash = hash;
    }

    /**
     * Check to see if this address is for a specific network
     *
     * @param network the network
     * @return <code>true</code> if this address is valid on the given network, otherwise <code>false</code>
     */
    public boolean isForNetwork(final Network network) {
        boolean result;
        if (this.network.equals(network)) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }

    /**
     * Obtain the network on which this Bitcoin key lives
     *
     * @return the network
     */
    public Network getNetwork() {
        return this.network;
    }

    /**
     * Obtain the hash of this Bitcon key
     *
     * @return the key
     */
    public Hash getHash() {
        return this.hash;
    }

    // Standard object methods follow
    @Override
    public String toString() {
        List<Byte> out = new ArrayList<>();
        out.add((byte) this.network.getAddressVersion());
        out.addAll(this.hash.getBytes());
        out.addAll(Crypto.shaOfShaOfBytes(ImmutableList.copyOf(out)).getBytes().subList(0, 4));
        return Base58.encode(ImmutableList.copyOf(out));
    }

    @Override
    public boolean equals(final Object that) {
        return (that instanceof BitcoinKey) && (this.compareTo((BitcoinKey) that) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.network, this.hash);
    }

    @Override
    public int compareTo(final BitcoinKey that) {
        return ComparisonChain.start()
                              .compare(this.network, that.network)
                              .compare(this.hash, that.hash)
                              .result();
    }
}
