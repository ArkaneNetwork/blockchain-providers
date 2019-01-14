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


import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.wealdtech.bitcoin.Value;
import com.wealdtech.bitcoin.script.Script;

import java.io.Serializable;

import static com.google.common.base.Preconditions.checkState;

/**
 * An output to a {@link Transaction}
 */
public class TransactionOutput implements Serializable, Comparable<TransactionOutput> {
    private static final long serialVersionUID = -7128860410659508134L;

    private final Value value;
    private final Script script;

    /**
     * Create a transaction output.  Note that this is a private constructor;
     * all transaction outputs should be created using the supplied builder.
     */
    private TransactionOutput(final Value value, final Script script) {
        this.value = value;
        this.script = script;
        validate();
    }

    private void validate() {
        checkState(this.value.getSatoshis() > 0, "Value must be greater than 0");
    }

    public Value getValue() {
        return this.value;
    }

    public Script getScript() {
        return this.script;
    }

    // Standard object methods follow
    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                      .add("value", this.value)
                      .add("script", this.script)
                      .toString();
    }

    @Override
    public boolean equals(final Object that) {
        return (that instanceof TransactionOutput) && (this.compareTo((TransactionOutput) that) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.value, this.script);
    }

    @Override
    public int compareTo(final TransactionOutput that) {
        return ComparisonChain.start()
                              .compare(this.value, that.value)
                              .compare(this.script, that.script)
                              .result();
    }

    /**
     * Builder for transaction outputs.  This is the only way
     * in which a transaction output should be created
     */
    public static class Builder {
        private Value value;
        private Script script;

        /**
         * Generate a new builder.
         */
        public Builder() {
        }

        /**
         * Generate a builder based on a prior object.
         *
         * @param prior the prior object
         */
        public Builder(final TransactionOutput prior) {
            this.value = prior.value;
            this.script = prior.script;
        }

        /**
         * Set the value
         *
         * @param sequence the value
         * @return
         */
        public Builder value(final Value value) {
            this.value = value;
            return this;
        }

        /**
         * Set the script
         *
         * @param script the script
         * @return
         */
        public Builder script(final Script script) {
            this.script = script;
            return this;
        }

        /**
         * Build the transaction output
         *
         * @return a new transaction output
         */
        public TransactionOutput build() {
            return new TransactionOutput(this.value, this.script);
        }
    }
}
