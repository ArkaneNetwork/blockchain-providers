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

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import org.bouncycastle.asn1.DERInteger;
import org.bouncycastle.asn1.DERSequenceGenerator;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.params.ECPrivateKeyParameters;
import org.bouncycastle.crypto.signers.ECDSASigner;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Helper functions to work with cryptography systems, especially as they relate
 * to Bitcoin
 */
public class Crypto {
    /**
     * Create a RIPEMD-160 hash of an SHA-256 hash of some input
     *
     * @param input the input
     * @return a hash
     */
    public static Ripemd160Hash ripeOfShaOfBytes(final ImmutableList<Byte> input) {
        try {
            final byte[] sha256 = MessageDigest.getInstance("SHA-256").digest(Bytes.toArray(input));
            final RIPEMD160Digest digest = new RIPEMD160Digest();
            digest.update(sha256, 0, sha256.length);
            final byte[] out = new byte[20];
            digest.doFinal(out, 0);
            return new Ripemd160Hash(ImmutableList.copyOf(Bytes.asList(out)));
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("Failed to access digest", nsae);
        }
    }

    /**
     * Create an SHA-256 hash of an SHA-256 hash of some input
     *
     * @param input the input
     * @return a hash
     */
    public static Sha256Hash shaOfShaOfBytes(final ImmutableList<Byte> input) {
        try {
            final MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] digest = sha256.digest(Bytes.toArray(input));
            digest = sha256.digest(digest);
            return new Sha256Hash(ImmutableList.copyOf(Bytes.asList(digest)));
        } catch (NoSuchAlgorithmException nsae) {
            throw new RuntimeException("Failed to access digest", nsae);
        }
    }

    /**
     * Confirm that the last four bytes of some content match the
     * first four bytes of the content's double SHA hash
     * (minus the checksum in the content, obviously)
     */
    public static boolean checksumMatches(final ImmutableList<Byte> content) {
        // Break the content in to two pieces; the payload and the checksum
        ImmutableList<Byte> payload = content.subList(0, content.size() - 4);
        ImmutableList<Byte> checksum = content.subList(content.size() - 4, content.size());

        ImmutableList<Byte> hash = shaOfShaOfBytes(payload).getBytes();
        return Arrays.equals(Bytes.toArray(checksum), Bytes.toArray(hash.subList(0, 4)));
    }

    /**
     * Sign some data using ECDSA
     *
     * @param data the data to sign
     * @param key  the key with which to sign
     * @return a signature
     */
    public static ECSignature sign(final ImmutableList<Byte> data, final ECKey key) {
        final ECDSASigner signer = new ECDSASigner();
        final ECPrivateKeyParameters privKey = new ECPrivateKeyParameters(key.getPrivKey(), ECKey.getEcParams());
        signer.init(true, privKey);
        BigInteger[] sigs = signer.generateSignature(Bytes.toArray(data));

        // DER-encode the signature
        try {
            // Usually 70-72 bytes.
            ByteArrayOutputStream baos = new ByteArrayOutputStream(72);
            DERSequenceGenerator seq = new DERSequenceGenerator(baos);
            seq.addObject(new DERInteger(sigs[0]));
            seq.addObject(new DERInteger(sigs[1]));
            seq.close();
            return new ECSignature(ImmutableList.copyOf(Bytes.asList(baos.toByteArray())));
        } catch (IOException e) {
            throw new RuntimeException(e);  // Cannot happen.
        }
    }
}
