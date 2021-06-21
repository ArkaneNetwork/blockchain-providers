package network.arkane.provider.nonfungable;

import network.arkane.provider.chain.SecretType;
import network.arkane.provider.gateway.EthereumWeb3JGateway;
import network.arkane.provider.nonfungible.domain.NonFungibleAsset;
import network.arkane.provider.nonfungible.domain.NonFungibleAssetBalance;
import network.arkane.provider.nonfungible.domain.NonFungibleContract;
import network.arkane.provider.opensea.OpenSeaGateway;
import network.arkane.provider.opensea.domain.Asset;
import network.arkane.provider.opensea.domain.AssetContract;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EthereumNonFungibleGatewayTest {

    private EthereumOpenSeaNonFungibleStrategy openseaStrategy;
    private OpenSeaGateway openSeaGateway;

    @BeforeEach
    void setUp() {
        openSeaGateway = mock(OpenSeaGateway.class);
        EthereumWeb3JGateway ethereumWeb3JGateway = mock(EthereumWeb3JGateway.class);
        openseaStrategy = new EthereumOpenSeaNonFungibleStrategy(ethereumWeb3JGateway, openSeaGateway);
    }

    @Test
    void getSecretType() {
        assertThat(openseaStrategy.getSecretType()).isEqualTo(SecretType.ETHEREUM);
    }

    @Test
    void listNonFungibles() {
        final String walletId = "gfhjafsfdgfhg";
        final List<Asset> openSeaTokens = new ArrayList<>();
        final ArrayList<NonFungibleAssetBalance> expected = new ArrayList<>();

        when(openSeaGateway.listAssets(walletId)).thenReturn(openSeaTokens);

        final List<NonFungibleAssetBalance> result = openseaStrategy.listNonFungibles(walletId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void listNonFungibles_withContractAddressesFilter() {
        final String walletId = "gfhjafsfdgfhg";
        final List<Asset> openSeaTokens = new ArrayList<>();
        final ArrayList<NonFungibleAsset> expected = new ArrayList<>();
        final String contractAddress1 = "afd`sd";
        final String contractAddress2 = "afdsgd";

        when(openSeaGateway.listAssets(walletId, contractAddress1, contractAddress2)).thenReturn(openSeaTokens);

        final List<NonFungibleAssetBalance> result = openseaStrategy.listNonFungibles(walletId, contractAddress1, contractAddress2);

        assertThat(result).isNotNull();
    }

    @Test
    void getNonFungible() {
        final String contractAddress = "afdsgd";
        final String tokenId = "131";
        final Asset openSeaToken = Asset.builder().build();
        final NonFungibleAsset expected = NonFungibleAsset.builder().attributes(new ArrayList<>()).build();

        when(openSeaGateway.getAsset(contractAddress, tokenId)).thenReturn(openSeaToken);

        final NonFungibleAsset result = openseaStrategy.getNonFungible(contractAddress, tokenId);

        assertThat(result).isEqualTo(expected);
    }

    @Test
    void getNonFungibleContract() {
        final String contractAddress = "42d91cf8-c41a-4285-8aa8-fdf3082882eb";
        final AssetContract assetContract = new AssetContract(null, null, null, null, null, null);
        final NonFungibleContract nonFungibleContract = new NonFungibleContract(null, null, null, null, null, null, null, null, "ERC_721", false, false, null);

        when(openSeaGateway.getContract(contractAddress)).thenReturn(assetContract);

        final NonFungibleContract result = openseaStrategy.getNonFungibleContract(contractAddress);

        assertThat(result).isEqualTo(nonFungibleContract);
    }
}
