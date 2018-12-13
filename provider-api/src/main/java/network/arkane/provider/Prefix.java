package network.arkane.provider;

/**
 * A prefix enumeration.
 */
public enum Prefix {

    /**
     * "VX" prefix string
     */
    VeChainX("VX"),

    /**
     * "0x" prefix string
     */
    ZeroLowerX("0x");

    private final String prefixString;

    Prefix(String prefixString){
        this.prefixString = prefixString;
    }

    /**
     * Get prefix string.
     * @return prefix string "0x" or "VX"
     */
    public String getPrefixString(){
        return this.prefixString;
    }
}
