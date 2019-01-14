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


import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import java.io.Serializable;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A script is a sequence of operations.
 */
public class Script implements Serializable, Comparable<Script> {
    private static final long serialVersionUID = -4548052402380963962L;

    private final ImmutableList<Op> ops;

    /**
     * Create a new script, containing a number of operations.
     *
     * @param ops a list of operations
     */
    public Script(final List<Op> ops) {
        checkNotNull(ops, "Script must contain operations");
        checkArgument(ops.size() != 0, "Script must contain operations");
        this.ops = ImmutableList.copyOf(ops);
    }

    public ImmutableList<Op> getOps() {
        return this.ops;
    }

    // Standard object methods follow
    @Override
    public String toString() {
        String prefix = "";
        final StringBuilder sb = new StringBuilder(128);
        for (Op op : this.ops) {
            sb.append(prefix);
            prefix = " ";
            sb.append(op.toString());
        }
        return sb.toString();
    }

    @Override
    public boolean equals(final Object that) {
        return (that instanceof Script) && (this.compareTo((Script) that) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.ops);
    }

    @Override
    public int compareTo(Script that) {
        return ComparisonChain.start()
                              .compare(this.ops, that.ops, Ordering.<Op>natural().lexicographical())
                              .result();
    }
}