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
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLongs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    /**
     * Turn a uint32-size value in to a little-endian byte array
     *
     * @param val the value
     * @return the byte array representing the value
     */
    public static byte[] longToUint32LE(final long val) {
        byte[] res = new byte[4];
        res[0] = (byte) (0xFF & (val >> 0));
        res[1] = (byte) (0xFF & (val >> 8));
        res[2] = (byte) (0xFF & (val >> 16));
        res[3] = (byte) (0xFF & (val >> 24));
        return res;
    }

    /**
     * Turn a uint32-size value in to a big-endian byte array
     *
     * @param val the value
     * @return the byte array representing the value
     */
    public static byte[] longToUint32BE(final long val) {
        byte[] res = new byte[4];
        res[0] = (byte) (0xFF & (val >> 24));
        res[1] = (byte) (0xFF & (val >> 16));
        res[2] = (byte) (0xFF & (val >> 8));
        res[3] = (byte) (0xFF & (val >> 0));
        return res;
    }

    /**
     * Turn a uint64-size value in to a little-endian byte array
     *
     * @param val the value
     * @return the byte array representing the value
     */
    public static byte[] longToUint64LE(final long val) {
        byte[] res = new byte[8];
        res[0] = (byte) (0xFF & (val >> 0));
        res[1] = (byte) (0xFF & (val >> 8));
        res[2] = (byte) (0xFF & (val >> 16));
        res[3] = (byte) (0xFF & (val >> 24));
        res[4] = (byte) (0xFF & (val >> 32));
        res[5] = (byte) (0xFF & (val >> 40));
        res[6] = (byte) (0xFF & (val >> 48));
        res[7] = (byte) (0xFF & (val >> 56));
        return res;
    }

    /**
     * Convert a long to a Bitcoin VarInt hex string
     *
     * @param val a long
     * @return a byte array containing the characters for the hex string
     */
    public static byte[] longToVarintLE(long val) {
        if (isLessThanUnsigned(val, 253)) {
            return new byte[] {(byte) val};
        } else if (isLessThanUnsigned(val, 65536)) {
            return new byte[] {(byte) 253,
                               (byte) (val),
                               (byte) (val >> 8)};
        } else if (isLessThanUnsigned(val, UnsignedInteger.MAX_VALUE.longValue())) {
            byte[] bytes = new byte[5];
            bytes[0] = (byte) 254;
            bytes[1] = (byte) (0xFF & (val >> 0));
            bytes[2] = (byte) (0xFF & (val >> 8));
            bytes[3] = (byte) (0xFF & (val >> 16));
            bytes[4] = (byte) (0xFF & (val >> 24));
            return bytes;
        } else {
            byte[] bytes = new byte[9];
            bytes[0] = (byte) 255;
            bytes[1] = (byte) (0xFF & (val >> 0));
            bytes[2] = (byte) (0xFF & (val >> 8));
            bytes[3] = (byte) (0xFF & (val >> 16));
            bytes[4] = (byte) (0xFF & (val >> 24));
            bytes[5] = (byte) (0xFF & (val >> 32));
            bytes[6] = (byte) (0xFF & (val >> 40));
            bytes[7] = (byte) (0xFF & (val >> 48));
            bytes[8] = (byte) (0xFF & (val >> 56));
            return bytes;
        }
    }

    /**
     * Work around lack of unsigned types in Java.
     */
    public static boolean isLessThanUnsigned(final long n1, final long n2) {
        return UnsignedLongs.compare(n1, n2) < 0;
    }


    /**
     * Convert a hex value in a string to a list of bytes
     *
     * @param hexStr the hex value
     * @return a list of bytes
     */
    public static ImmutableList<Byte> hexStringToBytes(final String hexStr) {
        List<Byte> list = new ArrayList<>(hexStr.length() / 2);
        for (int i = 0, len = hexStr.length(); i < len; i += 2) {
            list.add((byte) ((Character.digit(hexStr.charAt(i), 16) << 4)
                             + Character.digit(hexStr.charAt(i + 1), 16)));
        }
        return ImmutableList.copyOf(list);
    }

    /**
     * Convert a list of bytes to a hex value in a string
     */
    public static String bytesToHexString(final ImmutableList<Byte> bytes) {
        StringBuffer sb = new StringBuffer(bytes.size() * 2);
        for (byte b : bytes) {
            String s = Integer.toString(0xFF & b, 16);
            if (s.length() == 1) {
                sb.append('0');
            }
            sb.append(s);
        }
        return sb.toString();
    }

    public static byte[] hexStringToByteArray(final String input) {
        int len = input.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(input.charAt(i), 16) << 4)
                                  + Character.digit(input.charAt(i + 1), 16));
        }
        return data;
    }

    private static final MessageDigest digest;

    static {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e); // Can't happen.
        }
    }

    //  /**
    //   * See {@link Utils#doubleDigest(byte[], int, int)}.
    //   */
    //  public static byte[] doubleDigest(byte[] input)
    //  {
    //    return doubleDigest(input, 0, input.length);
    //  }
    //
    //  /**
    //   * Calculates the SHA-256 hash of the given byte range, and then hashes the
    //   * resulting hash again. This is standard procedure in Bitcoin. The resulting
    //   * hash is in big endian form.
    //   */
    //  public static byte[] doubleDigest(byte[] input, int offset, int length)
    //  {
    //    synchronized (digest)
    //    {
    //      digest.reset();
    //      digest.update(input, offset, length);
    //      byte[] first = digest.digest();
    //      return digest.digest(first);
    //    }
    //  }

    /**
     * Returns the given byte array hex encoded.
     */
    public static String bytesToHexString(byte[] bytes) {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for (byte b : bytes) {
            String s = Integer.toString(0xFF & b, 16);
            if (s.length() == 1) {
                sb.append('0');
            }
            sb.append(s);
        }
        return sb.toString();
    }

    /**
     * Returns a copy of the given byte array in reverse order.
     */
    public static byte[] reverseBytes(byte[] bytes) {
        // We could use the XOR trick here but it's easier to understand if we
        // don't. If we find this is really a
        // performance issue the matter can be revisited.
        byte[] buf = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            buf[i] = bytes[bytes.length - 1 - i];
        }
        return buf;
    }

    /**
     * Write out an array of bytes, prepending the length using a VarInt
     *
     * @param baos the stream to which to write the bytes
     * @param data the bytes
     */
    public static void writeBytesWithLength(final ByteArrayOutputStream baos, final byte[] data) throws IOException {
        baos.write(Utils.longToVarintLE(data.length));
        baos.write(data);
    }
}
