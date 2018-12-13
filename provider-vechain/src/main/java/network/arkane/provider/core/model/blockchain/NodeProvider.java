package network.arkane.provider.core.model.blockchain;

import network.arkane.provider.clients.base.AbstractClient;
import network.arkane.provider.utils.StringUtils;

/**
 * The blockchain node provider pojo object.
 * Set and get url and connecting timeout(in milliseconds).
 */
public class NodeProvider {

    private static NodeProvider INSTANCE = new NodeProvider();

    public static NodeProvider getNodeProvider() {
        return INSTANCE;
    }

    private NodeProvider() {
        this.socketTimeout = 5000;
        this.connectTimeout = 5000;
    }

    /**
     * get provider url
     * @return the url of the nodes.
     */
    public String getProvider() {
        if (StringUtils.isBlank(this.provider) || !this.provider.startsWith("http")) {
            throw new RuntimeException("The blockchain provider url must be set.");
        }
        return provider;
    }

    /**
     * Set provider url
     * @param provider the completed url of the nodes
     */
    public void setProvider(String provider) {
        this.provider = provider;
    }

    /**
     * set socket timeout
     * @return milliseconds
     */
    public int getSocketTimeout() {
        return socketTimeout;
    }

    /**
     * Set  socket timeout
     * @param timeout milliseconds
     */
    public void setSocketTimeout(int timeout) {
        this.socketTimeout = timeout;
    }


    /**
     * Set connect timeout, socket timeout
     * @param timeout milliseconds
     */
    public  void setTimeout(int timeout){
        AbstractClient.setTimeout(timeout);
    }


    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public  int getConnectTimeout(){
        return this.connectTimeout;
    }


    private String provider;
    private int    socketTimeout;
    private int connectTimeout;

}
