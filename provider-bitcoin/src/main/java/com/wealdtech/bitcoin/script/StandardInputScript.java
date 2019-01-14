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
package com.wealdtech.bitcoin.script;

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.crypto.ECKey;
import com.wealdtech.bitcoin.transaction.TransactionInput;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard output script which confirms authority to spend funds of a previous transaction
 */
public class StandardInputScript {
    /**
     * Create a standard input script which confirms  authority to spend funds of a previous transaction.
     * Note that although this can be action can be carried out directly it is more common that this
     * is created automatically as part of signing an existing transaction.
     *
     * @param signatureWithHashType the signature for the transaction with the {@Link HashType} appended as a single byte
     * @param key                   the key of the recipient
     * @return a script suitable for insertion in to a {@link TransactionInput}
     */
    public static Script create(final ImmutableList<Byte> signatureWithHashType, final ECKey key) {
        List<Op> ops = new ArrayList<>();
        ops.add(new Op(signatureWithHashType));
        ops.add(new Op(ImmutableList.copyOf(Bytes.asList(key.getPubKey().toByteArray()))));

        return new Script(ops);
    }
}
