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
package com.wealdtech.bitcoin.transaction;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.Locale;

/**
 * A Hash type defines the way in which signatures are calculated for transactions.
 */
public enum HashType {
    /**
     * SIGHASH_ALL
     */
    ALL(0x00000001),

    /**
     * SIGHASH_NONE
     */
    NONE(0x00000002),

    /**
     * SIGHASH_SINGLE
     */
    SINGLE(0x00000003),

    /**
     * SIGHASH_ANYONECANPAY
     */
    ANYONECANPAY(0x00000080),

    /**
     * SIGHASH_ALL_ANYONECANPAY
     */
    ALL_ANYONECANPAY(0x00000081),

    /**
     * SIGHASH_NONE_ANYONECANPAY
     */
    NONE_ANYONECANPAY(0x00000082),

    /**
     * SIGHASH_SINGLE_ANYONECANPAY
     */
    SINGLE_ANYONECANPAY(0x00000083);

    private final int id;

    HashType(final int id) {
        this.id = id;
    }

    /**
     * Obtain the ID of this hash type
     *
     * @return the ID of this hash type
     */
    public int getId() {
        return this.id;
    }

    /**
     * Confirm if a hash type is of a particular type, flags notwithstanding.
     * This is required because hash types are a combination of integer values and flags.
     *
     * @param type the type
     * @return <code>true<code> if this is matches the type, <code>false</code> otherwise.
     */
    public boolean isKindOf(final HashType type) {
        HashType baseType = HashType.fromId((this.id & ~ANYONECANPAY.id));
        return baseType == type;
    }

    public static HashType fromId(final int id) {
        for (HashType type : HashType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        throw new RuntimeException("Hash ID \"" + id + "\" is invalid");
    }

    /**
     * Parse a string in to a suitable enum
     *
     * @param hashType the string
     * @return a network
     */
    @JsonCreator
    public static HashType parse(final String hashType) {
        try {
            return valueOf(hashType.toUpperCase(Locale.ENGLISH));
        } catch (IllegalArgumentException iae) {
            // N.B. we don't pass the iae as the cause of this exception because
            // this happens during invocation, and in that case the enum handler
            // will report the root cause exception rather than the one we throw.
            throw new RuntimeException("Hash type \"" + hashType + "\" is invalid");
        }
    }

    public boolean isAnyoneCanPay() {
        if ((this.id & ANYONECANPAY.id) != 0) {
            return true;
        }
        return false;
    }

    // Standard object methods follow

    /**
     * Present the enum in a suitable output format
     */
    @Override
    @JsonValue
    public String toString() {
        return super.toString().toUpperCase(Locale.ENGLISH);
    }

    // Other methods are hard-coded in enum
}
