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


import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.crypto.Hash;
import com.wealdtech.bitcoin.crypto.Ripemd160Hash;
import com.wealdtech.bitcoin.utils.Base58;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A Bitcoin address is a 27-34 byte printable string.  It is most
 * commonly used as the recipient of funds during a transaction.
 */
public class Address extends BitcoinKey {
    private static final long serialVersionUID = 5971866491649330989L;

    /**
     * Instantiate an address given a network and a hash.
     *
     * @param network the network on which this address lives
     * @param hash    the hash of the address
     */
    public Address(final Network network, final Hash hash) {
        super(network, hash);
    }

    /**
     * Instantiate an address given a standard Bitcoin-format string
     *
     * @param input the address string representation
     * @return an address
     */
    public static Address fromAddressString(final String input) {
        checkNotNull(input, "Address must be supplied");
        final byte[] tmp = Base58.decodeChecked(input);
        final Network network = Network.fromVersion(tmp[0]);
        final byte[] out = new byte[tmp.length - 1];
        System.arraycopy(tmp, 1, out, 0, tmp.length - 1);
        final Hash hash = new Ripemd160Hash(ImmutableList.copyOf(Bytes.asList(out)));
        return new Address(network, hash);
    }

    // Standard object methods come from the superclass
}
