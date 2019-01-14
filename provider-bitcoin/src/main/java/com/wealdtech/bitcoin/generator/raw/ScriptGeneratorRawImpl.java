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
import com.google.common.primitives.Bytes;
import com.wealdtech.bitcoin.generator.Generator;
import com.wealdtech.bitcoin.script.Op;
import com.wealdtech.bitcoin.script.Opcode;
import com.wealdtech.bitcoin.script.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ScriptGeneratorRawImpl extends BaseGeneratorRawImpl<Script> implements Generator<Script> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptGeneratorRawImpl.class);

    public ScriptGeneratorRawImpl() {
    }

    @Override
    public void startGen(final ByteArrayOutputStream baos) {
        super.startGen(baos);
    }

    @Override
    public void generate(final Script script, final boolean includeLength) {
        ByteArrayOutputStream scriptBaos;
        if (includeLength) {
            // We need to include the length so write this to a temporary baos
            // to allow us to do this
            scriptBaos = new ByteArrayOutputStream();
        } else {
            scriptBaos = this.baos;
        }

        try {
            if (script != null) {
                // Add each individual operation
                for (Op op : script.getOps()) {
                    if (op.isOpcode()) {
                        // Simple opcode
                        scriptBaos.write(op.getOpcode().getCode());
                    } else {
                        // Data; exact output depends on length of the data
                        ImmutableList<Byte> data = op.getData();
                        if (data.size() < Opcode.OP_PUSHDATA1.getCode()) {
                            scriptBaos.write((byte) data.size());
                            scriptBaos.write(Bytes.toArray(data));
                        } else if (data.size() < 256) {
                            scriptBaos.write(Opcode.OP_PUSHDATA1.getCode());
                            scriptBaos.write((byte) data.size());
                            scriptBaos.write(Bytes.toArray(data));
                        } else if (data.size() < 65536) {
                            scriptBaos.write(Opcode.OP_PUSHDATA2.getCode());
                            scriptBaos.write((byte) (0xFF & (data.size())));
                            scriptBaos.write((byte) (0xFF & (data.size() >> 8)));
                            scriptBaos.write(Bytes.toArray(data));
                        } else {
                            throw new RuntimeException("Unimplemented");
                        }
                    }
                }
            }
            if (includeLength) {
                Utils.writeBytesWithLength(this.baos, scriptBaos.toByteArray());
            }
        } catch (IOException ioe) {
            LOGGER.warn("I/O error whilst generating script: " + ioe);
            throw new RuntimeException("Failed to generate script", ioe);
        }
    }
}
