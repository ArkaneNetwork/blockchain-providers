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


import com.fasterxml.jackson.annotation.JsonCreator;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static com.google.common.base.Preconditions.checkNotNull;

/*
 * Common, and not-so-common, units used with Bitcoin.
 */
public enum BTCUnit {
    /**
     * Satoshi
     */
    SATOSHI(1L, new DecimalFormat("################"), true),

    /**
     * Microbitcoin
     */
    MICROBTC(100L, new DecimalFormat("#.## µBTC"), true),

    /**
     * Millibitcoin
     */
    MILLIBTC(100000L, new DecimalFormat("#.##### mBTC"), true),

    /**
     * Centibitcoin
     */
    CENTIBTC(1000000L, new DecimalFormat("#.###### cBTC"), false),

    /**
     * Decibitcoin
     */
    DECIBTC(10000000L, new DecimalFormat("#.####### dBTC"), false),

    /**
     * Bitcoin
     */
    BTC(100000000L, new DecimalFormat("#.######## BTC"), true),

    /**
     * Decabitcoin
     */
    DECABTC(1000000000L, new DecimalFormat("#.######### daBTC"), false),

    /**
     * Hectobitcoin
     */
    HECTOBTC(10000000000L, new DecimalFormat("#.########## hBTC"), false),

    /**
     * Kilobitcoin
     */
    KILOBTC(100000000000L, new DecimalFormat("#.########### kBTC"), true),

    /**
     * Megabitcoin
     */
    MEGABTC(100000000000000L, new DecimalFormat("#.############## MBTC"), true);

    private final BigDecimal satoshis; // Number of satoshis in this unit
    private final DecimalFormat format; // Canonical string representation of amounts of this unit
    private final boolean common; // If this is a unit in common use

    BTCUnit(final long satoshis, final DecimalFormat format, final boolean common) {
        this.satoshis = new BigDecimal(satoshis);
        this.format = format;
        this.common = common;
    }

    // Private method to carry out conversion using BigDecimal
    private BigDecimal convert(final BigDecimal amount, final BTCUnit unit) {
        return amount.multiply(unit.satoshis).divide(this.satoshis);
    }

    /**
     * Calculate the number of satoshis for a given amount and unit
     *
     * @param amount the amount of bitcoins
     * @param unit   the unit corresponding to the amount
     * @return the number of satoshis
     */
    public static long toSatoshis(final long amount, final BTCUnit unit) {
        return toSatoshis(new BigDecimal(amount), unit);
    }

    /**
     * Calculate the number of satoshis for a given amount and unit
     *
     * @param amount the amount of bitcoins
     * @param unit   the unit corresponding to the amount
     * @return the number of satoshis
     */
    public static long toSatoshis(final BigDecimal amount, final BTCUnit unit) {
        return SATOSHI.convert(amount, unit).longValue();
    }

    /**
     * Return the best for a given amount of satoshis and only using common units.
     * Best is, of course, subjective.  We will attempt to find the unit which
     * fits gives us up to (but no more than) 3 digits before the decimal point.
     *
     * @param amount the amount
     * @return the most suitable unit
     */
    public static BTCUnit getBest(final long amount) {
        return getBest(new BigDecimal(amount), true);
    }

    /**
     * Return the best for a given amount of satoshis.
     * Best is, of course, subjective.  We choose the unit which gives the
     * least number of digits before the decimal place, given that there should
     * be at least 1.
     *
     * @param amount the amount
     * @param common if <code>true</code> then only use the most common units
     * @return the most suitable unit
     */
    public static BTCUnit getBest(final long amount, final boolean common) {
        return getBest(new BigDecimal(amount), common);
    }

    /**
     * Return the best for a given amount of satoshis.
     * Best is, of course, subjective.  We choose the unit which gives the
     * least number of digits before the decimal place, given that there should
     * be at least 1.
     *
     * @param amount the amount
     * @param common if <code>true</code> then only use the most common units
     * @return the most suitable unit
     */
    public static BTCUnit getBest(final BigDecimal amount, final boolean common) {
        BTCUnit best = null;

        // Special case: 0 BTC
        if (amount.equals(BigDecimal.ZERO)) {
            return BTCUnit.BTC;
        }

        // Work through the units to find the closest
        for (BTCUnit candidate : BTCUnit.values()) {
            if (candidate.common || common == false) {
                long digits = (int) (Math.log10(amount.divide(candidate.satoshis).doubleValue()) + 1);
                if (digits <= 0) {
                    // The last one was as good as we were going to get
                    return best;
                } else {
                    best = candidate;
                }
            }
        }
        return best;
    }

    public String toPrettyString(final long amount) {
        return toPrettyString(new BigDecimal(amount));
    }

    public String toPrettyString(final BigDecimal amount) {
        return this.format.format(amount.divide(this.satoshis).doubleValue());
    }

    // Standard object methods follow

    /**
     * Parse a string in to a suitable enum
     *
     * @param btcunit the string
     * @return a Bitcoin unit
     */
    @JsonCreator
    public static BTCUnit parse(final String btcunit) {
        checkNotNull(btcunit, "Bitcoin unit is missing");
        try {
            return valueOf(btcunit);
        } catch (IllegalArgumentException iae) {
            // N.B. we don't pass the iae as the cause of this exception because
            // this happens during invocation, and in that case the enum handler
            // will report the root cause exception rather than the one we throw.
            throw new RuntimeException("Bitcoin unit \"" + btcunit + "\" is invalid");
        }
    }
}
