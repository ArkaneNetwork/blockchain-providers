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
import com.google.common.collect.Ordering;
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.Address;
import com.wealdtech.bitcoin.Network;
import com.wealdtech.bitcoin.generator.raw.Utils;
import com.wealdtech.bitcoin.utils.Base58;
import org.bouncycastle.asn1.sec.SECNamedCurves;
import org.bouncycastle.asn1.x9.X9ECParameters;
import org.bouncycastle.crypto.params.ECDomainParameters;
import org.bouncycastle.math.ec.ECPoint;

import java.io.Serializable;
import java.math.BigInteger;

import static com.google.common.base.Preconditions.checkNotNull;

public class ECKey implements Serializable, Comparable<ECKey> {
    private static final long serialVersionUID = 9161326405924378230L;

    private static final ECDomainParameters ecParams;

    private final BigInteger pubKey;
    private final BigInteger privKey;

    static {
        // Set up EC parameters
        final X9ECParameters params = SECNamedCurves.getByName("secp256k1");
        ecParams = new ECDomainParameters(params.getCurve(), params.getG(), params.getN(), params.getH());
    }

    /**
     * Create a key pair given the private key.
     * This calculates the public key at creation time so is slow.  If the public
     * key is available then it should be provided as well
     *
     * @param privKey the private key
     */
    public ECKey(final BigInteger privKey) {
        this(privKey, null);
    }

    public ECKey(final BigInteger privKey, final BigInteger pubKey) {
        checkNotNull(privKey, "Private key is required");
        this.privKey = privKey;

        if (pubKey == null) {
            // Calculate public key from the private key
            final ECPoint point = ecParams.getG().multiply(privKey);
            // Compression not enabled at current
            // ECPoint compressedPoint = new ECPoint.Fp(ecParams.getCurve(), point.getX(), point.getY(), true);
            //  this.pubKey = new BigInteger(1, compressedPoint.getEncoded());
            this.pubKey = new BigInteger(1, point.getEncoded());
        } else {
            this.pubKey = pubKey;
        }
    }

    /**
     * Create a key pair given the private key.
     * This calculates the public key at creation time so is slow.  If the public
     * key is available then it should be provided as well
     *
     * @param privKey a base58 encoded private key
     */
    public static ECKey fromBase58String(final String privKey) {
        return fromBase58String(privKey, null);
    }

    public static ECKey fromBase58String(final String privKey, final String pubKey) {
        BigInteger privBI = null;
        if (privKey != null) {
            final ImmutableList<Byte> decoded = Base58.decodeChecked2(privKey);
            final ImmutableList<Byte> priv = decoded.subList(1, decoded.size());
            privBI = new BigInteger(1, Bytes.toArray(priv));
        }

        BigInteger pubBI = null;
        if (pubKey != null) {
            final ImmutableList<Byte> decoded = Base58.decodeChecked2(pubKey);
            final ImmutableList<Byte> pub = decoded.subList(1, decoded.size());
            pubBI = new BigInteger(1, Bytes.toArray(pub));
        }

        return new ECKey(privBI, pubBI);
    }

    public BigInteger getPubKey() {
        return this.pubKey;
    }

    public BigInteger getPrivKey() {
        return this.privKey;
    }

    public static ECDomainParameters getEcParams() {
        return ecParams;
    }

    /**
     * Utility function to show the transition from a private key to a Bitcoin address.
     * <b>This function prints out private keys, do not use it except for your own debugging purposes</b>
     *
     * @param privKey a string containing a base58-encoded private key
     */
    public static void showKey(final String privKey) {
        System.err.println("================");
        System.err.println("Base-58 private key is " + privKey);
        System.err.println("Decoded private key is " + Utils.bytesToHexString(Base58.decodeChecked(privKey)));
        final ImmutableList<Byte> decoded = Base58.decodeChecked2(privKey);
        final ImmutableList<Byte> priv = decoded.subList(1, decoded.size());
        final ECKey key = new ECKey(new BigInteger(1, Bytes.toArray(priv)));
        System.err.println("Public key is " + Utils.bytesToHexString(key.getPubKey().toByteArray()));
        final Sha256Hash hash1 = Sha256Hash.create(ImmutableList.copyOf(Bytes.asList(key.getPubKey().toByteArray())));
        System.err.println("Hash1 (SHA256) is " + Utils.bytesToHexString(hash1.getBytes()));
        final Ripemd160Hash hash2 = Ripemd160Hash.create(hash1.getBytes());
        System.err.println("Hash2 (RIPEMD160) is " + Utils.bytesToHexString(hash2.getBytes()));
        final Address addr = new Address(Network.TEST, hash2);
        System.err.println("Address is " + addr);
        System.err.println("Decoded address is " + Utils.bytesToHexString(Base58.decode(addr.toString())));
        System.err.println("================");
    }

    // Standard object methods follow
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("pubKey", this.pubKey)
                      .add("privKey", this.privKey)
                      .omitNullValues()
                      .toString();
    }

    @Override
    public boolean equals(final Object that) {
        return (that instanceof ECKey) && (this.compareTo((ECKey) that) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.pubKey, this.privKey);
    }

    @Override
    public int compareTo(final ECKey that) {
        return ComparisonChain.start()
                              .compare(this.pubKey, that.pubKey, Ordering.natural().nullsFirst())
                              .compare(this.privKey, that.privKey)
                              .result();
    }
}
