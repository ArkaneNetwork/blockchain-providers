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

import com.wealdtech.bitcoin.Address;
import com.wealdtech.bitcoin.transaction.TransactionOutput;

import java.util.ArrayList;
import java.util.List;

import static com.wealdtech.bitcoin.script.Opcode.OP_CHECKSIG;
import static com.wealdtech.bitcoin.script.Opcode.OP_DUP;
import static com.wealdtech.bitcoin.script.Opcode.OP_EQUALVERIFY;
import static com.wealdtech.bitcoin.script.Opcode.OP_HASH160;

/**
 * Standard output script which sends the funds to a single recipient.
 */
public class StandardOutputScript {
    /**
     * Create a standard output script which sends the funds to a single recipient
     *
     * @param recipient the {@link Address} of the recipient
     * @return a script suitable for insertion in to a {@link TransactionOutput}
     */
    public static Script create(final Address recipient) {
        List<Op> ops = new ArrayList<>();
        ops.add(new Op(OP_DUP));
        ops.add(new Op(OP_HASH160));
        ops.add(new Op(recipient.getHash().getBytes()));
        ops.add(new Op(OP_EQUALVERIFY));
        ops.add(new Op(OP_CHECKSIG));

        return new Script(ops);
    }
}
