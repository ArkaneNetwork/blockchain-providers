package network.arkane.provider.bitcoin.unspent;

import network.arkane.provider.bitcoin.BitcoinEnv;
import network.arkane.provider.sochain.SoChainGateway;
import network.arkane.provider.sochain.domain.Network;
import network.arkane.provider.sochain.domain.Transaction;
import org.bitcoinj.core.Address;
import org.bitcoinj.params.TestNet3Params;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UnspentServiceTest {

    private SoChainGateway soChainGateway;
    private BitcoinEnv bitcoinEnv;
    private UnspentService unspentService;

    @BeforeEach
    void setUp() {
        soChainGateway = mock(SoChainGateway.class);
        bitcoinEnv = new BitcoinEnv(Network.BTCTEST, TestNet3Params.get());
        unspentService = new UnspentService(soChainGateway, bitcoinEnv);
    }

    @Test
    void getUnspents() {
        String address = "mhxqpVGP4AiYNsmFwDGqBPaNimNZkqCX8o";
        Transaction t = createExpectedTransaction();
        when(soChainGateway.getUnspentTransactions(bitcoinEnv.getNetwork(), address)).thenReturn(Collections.singletonList(t));

        List<Unspent> unspents = unspentService.getUnspentForAddress(Address.fromBase58(bitcoinEnv.getNetworkParameters(),
                                                                                        address));

        assertThat(unspents).hasSize(1);
        assertThat(unspents.get(0).getAmount()).isEqualTo(13753330);
        assertThat(unspents.get(0).getScriptPubKey()).isEqualTo(t.getHexScript());
        assertThat(unspents.get(0).getTxId()).isEqualTo(t.getTransactionid());
        assertThat(unspents.get(0).getVOut()).isEqualTo(t.getOutputNumber());

    }

    private Transaction createExpectedTransaction() {
        Transaction t = new Transaction();
        t.setHexScript("76a914add10b1fd3827683b0071ce3354590477d69d89688ac");
        t.setAsmScript("OP_DUP OP_HASH160 add10b1fd3827683b0071ce3354590477d69d896 OP_EQUALVERIFY OP_CHECKSIG");
        t.setValue(new BigDecimal("0.13753330"));
        t.setConfirmations(10L);
        t.setTime(1547553009L);
        t.setOutputNumber(0L);
        t.setTransactionid("6c6a27da3f5fa89a099061ad093a48e14d12a4055cb42502ffc61bd10e686bbf");
        return t;
    }
}