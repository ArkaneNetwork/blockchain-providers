package network.arkane.provider.sochain;

import network.arkane.provider.sochain.domain.BalanceResult;
import network.arkane.provider.sochain.domain.SendSignedTransactionResult;
import network.arkane.provider.sochain.domain.SoChainResult;
import network.arkane.provider.sochain.domain.SendSignedTransactionRequest;
import network.arkane.provider.sochain.domain.Transactions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.web.HttpMessageConvertersAutoConfiguration;
import org.springframework.cloud.netflix.feign.FeignAutoConfiguration;
import org.springframework.cloud.netflix.feign.ribbon.FeignRibbonClientAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import static org.assertj.core.api.Assertions.assertThat;

class SoChainClientTest {

    private static SoChainClient client;

    @Configuration
    @Component
    @ImportAutoConfiguration( {RibbonAutoConfiguration.class, FeignRibbonClientAutoConfiguration.class, FeignAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class})
    public static class TestContext {

    }

    @BeforeAll
    public static void onSetup() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(SoChainConfig.class, TestContext.class);
        context.setAllowBeanDefinitionOverriding(false);
        client = context.getBean(SoChainClient.class);
    }

    @Test
    void getBalance() {
        SoChainResult<BalanceResult> balance = client.getBalance("BTCTEST", "mwN1cqMbvLacFY6PuKR9whUp1cCktg9DNt");

        assertThat(balance.getData().getAddress()).isEqualTo("mwN1cqMbvLacFY6PuKR9whUp1cCktg9DNt");
        assertThat(balance.getData().getNetwork()).isEqualTo("BTCTEST");
    }

    @Test
    void getUnspentTransactions() {
        SoChainResult<Transactions> result = client.getUnspentTransactions("BTCTEST", "mwN1cqMbvLacFY6PuKR9whUp1cCktg9DNt");

        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getTransactions()).isNotEmpty();
    }

    @Test
    void getSpentTransactions() {
        SoChainResult<Transactions> result = client.getSpentTransactions("BTCTEST", "mwN1cqMbvLacFY6PuKR9whUp1cCktg9DNt");

        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getTransactions()).isNotEmpty();
    }

    @Test
    void getReceivedTransactions() {
        SoChainResult<Transactions> result = client.getReceivedTransactions("BTCTEST", "mwN1cqMbvLacFY6PuKR9whUp1cCktg9DNt");

        assertThat(result.getStatus()).isEqualTo("success");
        assertThat(result.getData()).isNotNull();
        assertThat(result.getData().getTransactions()).isNotEmpty();
    }

    @Test
    void send() {
        SendSignedTransactionRequest request = new SendSignedTransactionRequest(
                "01000000016c4f9fd0290b9e4c50b6f84544002fe6aaeac10fb377544dc274c7c3f4921b9b000000008a473044022039ff93a8af80cdc589a3c6dade2c57563cc14dcbfab1b7e95f71b01539c1032d022014aad0562835aa369c2766115708448b0487f9f0d7236342d3e7de0cc434114d81410496e59446aed552e60fb29b6a9c6c71d7c1b38b8396e67d9940dbb867a70e314c591b20a0e59e36259439e1d0b6ae16975ca7097103d8d1ac987a02d121b5cd16ffffffff0201000000000000001976a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588acc8edd900000000001976a91464d12bfa319ec47fe040bf6d2dbb3013d85f552588ac00000000");
        SoChainResult<SendSignedTransactionResult> result = client.sendSignedTransaction("BTCTEST", request);

        assertThat(result.getStatus()).isEqualTo("fail");
    }
}