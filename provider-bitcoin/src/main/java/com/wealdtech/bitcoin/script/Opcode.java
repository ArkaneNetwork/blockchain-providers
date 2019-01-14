/*
 *    Copyright 2013 Weald Technology Trading Limited
 *
 *   Licensed under the Apache License, Version 2.0 (the "License")),
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * An opcode in a Bitcoin script
 */
public enum Opcode {
    // Push values
    OP_0(0x00),
    OP_FALSE(0x00),
    OP_PUSHDATA1(0x4c),
    OP_PUSHDATA2(0x4d),
    OP_PUSHDATA4(0x4e),
    OP_1NEGATE(0x4f),
    OP_RESERVED(0x50),
    OP_1(0x51),
    OP_TRUE(0x51),
    OP_2(0x52),
    OP_3(0x53),
    OP_4(0x54),
    OP_5(0x55),
    OP_6(0x56),
    OP_7(0x57),
    OP_8(0x58),
    OP_9(0x59),
    OP_10(0x5a),
    OP_11(0x5b),
    OP_12(0x5c),
    OP_13(0x5d),
    OP_14(0x5e),
    OP_15(0x5f),
    OP_16(0x60),

    // Control
    OP_NOP(0x61),
    OP_VER(0x62),
    OP_IF(0x63),
    OP_NOTIF(0x64),
    OP_VERIF(0x65),
    OP_VERNOTIF(0x66),
    OP_ELSE(0x67),
    OP_ENDIF(0x68),
    OP_VERIFY(0x69),
    OP_RETURN(0x6a),

    // Stack
    OP_TOALTSTACK(0x6b),
    OP_FROMALTSTACK(0x6c),
    OP_2DROP(0x6d),
    OP_2DUP(0x6e),
    OP_3DUP(0x6f),
    OP_2OVER(0x70),
    OP_2ROT(0x71),
    OP_2SWAP(0x72),
    OP_IFDUP(0x73),
    OP_DEPTH(0x74),
    OP_DROP(0x75),
    OP_DUP(0x76),
    OP_NIP(0x77),
    OP_OVER(0x78),
    OP_PICK(0x79),
    OP_ROLL(0x7a),
    OP_ROT(0x7b),
    OP_SWAP(0x7c),
    OP_TUCK(0x7d),

    // Splice
    OP_CAT(0x7e),
    OP_SUBSTR(0x7f),
    OP_LEFT(0x80),
    OP_RIGHT(0x81),
    OP_SIZE(0x82),

    // Bit logic
    OP_INVERT(0x83),
    OP_AND(0x84),
    OP_OR(0x85),
    OP_XOR(0x86),
    OP_EQUAL(0x87),
    OP_EQUALVERIFY(0x88),
    OP_RESERVED1(0x89),
    OP_RESERVED2(0x8a),

    // Numeric
    OP_1ADD(0x8b),
    OP_1SUB(0x8c),
    OP_2MUL(0x8d),
    OP_2DIV(0x8e),
    OP_NEGATE(0x8f),
    OP_ABS(0x90),
    OP_NOT(0x91),
    OP_0NOTEQUAL(0x92),
    OP_ADD(0x93),
    OP_SUB(0x94),
    OP_MUL(0x95),
    OP_DIV(0x96),
    OP_MOD(0x97),
    OP_LSHIFT(0x98),
    OP_RSHIFT(0x99),
    OP_BOOLAND(0x9a),
    OP_BOOLOR(0x9b),
    OP_NUMEQUAL(0x9c),
    OP_NUMEQUALVERIFY(0x9d),
    OP_NUMNOTEQUAL(0x9e),
    OP_LESSTHAN(0x9f),
    OP_GREATERTHAN(0xa0),
    OP_LESSTHANOREQUAL(0xa1),
    OP_GREATERTHANOREQUAL(0xa2),
    OP_MIN(0xa3),
    OP_MAX(0xa4),
    OP_WITHIN(0xa5),

    // Crypto
    OP_RIPEMD160(0xa6),
    OP_SHA1(0xa7),
    OP_SHA256(0xa8),
    OP_HASH160(0xa9),
    OP_HASH256(0xaa),
    OP_CODESEPARATOR(0xab),
    OP_CHECKSIG(0xac),
    OP_CHECKSIGVERIFY(0xad),
    OP_CHECKMULTISIG(0xae),
    OP_CHECKMULTISIGVERIFY(0xaf),

    // Expansion
    OP_NOP1(0xb0),
    OP_NOP2(0xb1),
    OP_NOP3(0xb2),
    OP_NOP4(0xb3),
    OP_NOP5(0xb4),
    OP_NOP6(0xb5),
    OP_NOP7(0xb6),
    OP_NOP8(0xb7),
    OP_NOP9(0xb8),
    OP_NOP10(0xb9),

    // Invalid
    OP_INVALIDOPCODE(0xff);

    private final byte code; // Numeric code

    Opcode(final int code) {
        this.code = (byte) code;
    }

    /**
     * Obtain the numeric value for this opcode
     *
     * @return
     */
    public byte getCode() {
        return this.code;
    }

    /**
     * Present the enum in a suitable output format
     */
    @Override
    @JsonValue
    public String toString() {
        return super.toString().toUpperCase(Locale.ENGLISH);
    }

    /**
     * Parse a string in to a suitable enum
     *
     * @param opcode the string
     * @return an opcode
     */
    @JsonCreator
    public static Opcode parse(final String opcode) {
        try {
            return valueOf(opcode.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException iae) {
            // N.B. we don't pass the iae as the cause of this exception because
            // this happens during invocation, and in that case the enum handler
            // will report the root cause exception rather than the one we throw.
            throw new RuntimeException("Opcode type \"" + opcode + "\" is invalid");
        }
    }
}
