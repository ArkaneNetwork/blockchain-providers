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

import com.google.common.collect.Lists;
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.generator.Generator;
import com.wealdtech.bitcoin.script.Script;
import com.wealdtech.bitcoin.transaction.Transaction;
import com.wealdtech.bitcoin.transaction.TransactionInput;
import com.wealdtech.bitcoin.transaction.TransactionOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TransactionGeneratorRawImpl extends BaseGeneratorRawImpl<Transaction> implements Generator<Transaction> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionGeneratorRawImpl.class);

    public TransactionGeneratorRawImpl() {
    }

    @Override
    public void startGen(final ByteArrayOutputStream baos) {
        super.startGen(baos);
    }

    @Override
    public void generate(final Transaction transaction, final boolean includeLength) {
        ByteArrayOutputStream transBaos;
        if (includeLength) {
            // We need to prepend the length so write this to a temporary baos
            // to allow us to do this
            transBaos = new ByteArrayOutputStream();
        } else {
            transBaos = this.baos;
        }

        Generator<Script> scriptGen = new ScriptGeneratorRawImpl();

        try {
            transBaos.write(Utils.longToUint32LE(transaction.getVersion()));
            transBaos.write(Utils.longToVarintLE(transaction.getInputs().size()));
            for (TransactionInput input : transaction.getInputs()) {
                transBaos.write(Bytes.toArray(Lists.reverse(input.getTxHash().getBytes())));
                transBaos.write(Utils.longToUint32LE(input.getTxIndex()));
                scriptGen.startGen(transBaos);
                scriptGen.generate(input.getScript().orNull(), true);
                transBaos.write(Utils.longToUint32LE(input.getSequence()));
            }
            transBaos.write(Utils.longToVarintLE(transaction.getOutputs().size()));
            for (TransactionOutput output : transaction.getOutputs()) {
                transBaos.write(Utils.longToUint64LE(output.getValue().getSatoshis()));
                scriptGen.startGen(transBaos);
                scriptGen.generate(output.getScript(), true);
            }
            transBaos.write(Utils.longToUint32LE(transaction.getLockTime()));

            if (includeLength) {
                Utils.writeBytesWithLength(this.baos, transBaos.toByteArray());
            }
        } catch (IOException ioe) {
            LOGGER.warn("I/O error whilst generating transaction: " + ioe);
            throw new RuntimeException("Failed to generate transaction", ioe);
        }
    }
}
