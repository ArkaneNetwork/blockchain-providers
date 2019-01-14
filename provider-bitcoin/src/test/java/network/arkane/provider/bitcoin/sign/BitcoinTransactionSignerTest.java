package network.arkane.provider.bitcoin.sign;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.apache.commons.codec.binary.Base64;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.util.HashMap;

class BitcoinTransactionSignerTest {

    @Test
    void blah() throws Throwable {
        //        JsonRpcClient client = new JsonRpcClient(new Transport() {
        //            CloseableHttpClient httpClient = HttpClients.createDefault();
        //            @NotNull
        //            @Override
        //            public String pass(@NotNull String request) throws IOException {
        //                // Used Apache HttpClient 4.3.1 as an example
        //                HttpPost post = new HttpPost("");
        //                post.setEntity(new StringEntity(request, Charsets.UTF_8));
        //                post.setHeader(HttpHeaders.CONTENT_TYPE, MediaType.JSON_UTF_8.toString());
        //                try (CloseableHttpResponse httpResponse = httpClient.execute(post)) {
        //                    return EntityUtils.toString(httpResponse.getEntity(), Charsets.UTF_8);
        //                }
        //            }
        //        });
        //
        //        client.createRequest()
        //
        //              .id(100)
        //              .method("getblockchaininfo")
        //              .returnAsMap(HashMap.class, String.class)
        //              .execute();


        HashMap<String, String> headers = new HashMap<>();
        String auth = new String(Base64.encodeBase64("arkane:dk36EsfUu2ze7PjQdj5zTFY9fEMMPb4v63c9W2azctq4dQtERP".getBytes()));
        headers.put("Authorization", "Basic " + auth);
        JsonRpcHttpClient client = new JsonRpcHttpClient(new ObjectMapper(),
                                                         new URL("http://95.216.159.193:18332"), headers);

        client.invoke("importprivkey", new Object[] {"Kxt4AVyCSCXnpDWhRS5JpRxw2PMkGn25yY4JxCJL3KzwUj2JJhrE", "davy_test"});

    }
}