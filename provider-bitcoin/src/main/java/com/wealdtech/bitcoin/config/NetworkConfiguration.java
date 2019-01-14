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
package com.wealdtech.bitcoin.config;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.collect.ComparisonChain;
import com.wealdtech.bitcoin.Network;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Configuration providing Bitcoin network information
 */
public class NetworkConfiguration implements Comparable<NetworkConfiguration> {
    private Network network = Network.PRODUCTION;

    /**
     * Create a network with default values;
     */
    public NetworkConfiguration() {
        // 0-configuration defaults
    }

    /**
     * Create a configuration with specified values for all options.
     * Note that this should not be called directly, and the Builder should be
     * used for instantiation.
     *
     * @param network the name of the Bitcoin network to use
     */
    @JsonCreator
    private NetworkConfiguration(@JsonProperty("network") final Network network) {
        if (network != null) {
            this.network = network;
        }
        validate();
    }

    private void validate() {
        checkNotNull(this.network, "network setting is required");
    }

    public Network getNetwork() {
        return this.network;
    }

    // Standard object methods follow
    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                          .add("network", this.getNetwork())
                          .toString();
    }

    @Override
    public boolean equals(final Object that) {
        return (that instanceof NetworkConfiguration) && (this.compareTo((NetworkConfiguration) that) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.getNetwork());
    }

    @Override
    public int compareTo(final NetworkConfiguration that) {
        return ComparisonChain.start()
                              .compare(this.getNetwork(), that.getNetwork())
                              .result();
    }
}
