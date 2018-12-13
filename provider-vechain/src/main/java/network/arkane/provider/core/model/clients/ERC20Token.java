package network.arkane.provider.core.model.clients;

import network.arkane.provider.core.model.clients.base.AbstractToken;
import network.arkane.provider.core.model.exception.ClientArgumentException;
import org.apache.commons.lang3.StringUtils;

/**
 * The token which is follow the interface of ERC20 protocol.
 */
public final class ERC20Token extends AbstractToken {

    public static final ERC20Token VTHO = new ERC20Token("VTHO", Address.VTHO_Address);
    protected Address contractAddress;

    /**
     * Create {@link ERC20Token} object.
     *
     * @param name    token name.
     * @param address {@link Address} address.
     * @return
     */
    public static ERC20Token create(String name, Address address) {
        if (StringUtils.isBlank(name)) {
            throw ClientArgumentException.exception("Address create argument exception.");
        }
        return new ERC20Token(name, address);
    }

    private ERC20Token(String name, Address address) {
        super(name);
        this.contractAddress = address;
    }

    public Address getContractAddress() {
        return contractAddress;
    }
}
