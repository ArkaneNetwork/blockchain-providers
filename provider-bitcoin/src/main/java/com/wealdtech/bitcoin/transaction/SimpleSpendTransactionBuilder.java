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

import com.wealdtech.bitcoin.Address;
import com.wealdtech.bitcoin.Value;
import com.wealdtech.bitcoin.script.StandardOutputScript;

import java.util.ArrayList;
import java.util.List;

/**
 * A {@link Transaction} builder to spend coins from one or more previous
 * transactions. The transaction requires the following items:
 * <ul>
 * <li>The hash of one or more existing transactions, which contain the coin to spend
 * <li>One or more sets of outputs, containing the amount of coin to spend and the address to which to send it
 * <li>A map of {@link ECKey}
 * </ul>
 * There are a number of important notes
 */
public class SimpleSpendTransactionBuilder {
    private final Transaction.Builder builder;

    private final List<TransactionInput> inputs;
    private final List<TransactionOutput> outputs;

    public SimpleSpendTransactionBuilder() {
        this.builder = new Transaction.Builder();
        this.inputs = new ArrayList<TransactionInput>();
        this.outputs = new ArrayList<TransactionOutput>();
    }

    /**
     * Send an amount of bitcoins to a recipient as part of this transaction
     *
     * @param value     the value of the bitcoins to spend
     * @param recipient the recipient of the bitcoins
     */
    public void sendToRecipient(final Value value, final Address recipient) {
        TransactionOutput output = new TransactionOutput.Builder()
                .value(value)
                .script(StandardOutputScript.create(recipient))
                .build();
        this.outputs.add(output);
    }

    public Transaction build() {
        this.builder.inputs(this.inputs);
        this.builder.outputs(this.outputs);
        return this.builder.build();
    }
}
