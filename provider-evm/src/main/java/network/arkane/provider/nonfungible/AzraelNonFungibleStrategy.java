package network.arkane.provider.nonfungible;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import network.arkane.blockchainproviders.azrael.AzraelClient;
import network.arkane.blockchainproviders.azrael.dto.ContractType;
import network.arkane.blockchainproviders.azrael.dto.TokenBalance;
import network.arkane.blockchainproviders.azrael.dto.token.erc1155.Erc1155TokenBalances;
import network.arkane.blockchainproviders.azrael.dto.token.erc721.Erc721TokenBalances;
import network.arkane.provider.contract.EvmContractService;
import network.arkane.provider.nonfungible.domain.Attribute;
import network.arkane.provider.nonfungible.domain.NonFungibleAsset;
import network.arkane.provider.nonfungible.domain.NonFungibleAssetBalance;
import network.arkane.provider.nonfungible.domain.NonFungibleContract;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.cache.CacheManager;
import org.springframework.util.CollectionUtils;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public abstract class AzraelNonFungibleStrategy implements EvmNonFungibleStrategy, DisposableBean {

    private final AzraelClient azraelClient;
    private final MetaDataParser metadataParser;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(25, new ThreadFactoryBuilder().setNameFormat("azrael-nft-strategy-%d").build());


    public AzraelNonFungibleStrategy(AzraelClient azraelClient,
                                     EvmContractService contractService,
                                     Optional<CacheManager> cacheManager) {
        this.azraelClient = azraelClient;
        this.metadataParser = new MetaDataParser(contractService, cacheManager);
    }

    @Override
    @SneakyThrows
    public List<NonFungibleAssetBalance> listNonFungibles(final String walletAddress,
                                                          final String... contractAddresses) {
        Set<String> contracts = contractAddresses == null ? new HashSet<>() : Arrays.stream(contractAddresses).map(String::toLowerCase).collect(Collectors.toSet());
        List<TokenBalance> tokens = azraelClient.getTokens(walletAddress, Arrays.asList(ContractType.ERC_721, ContractType.ERC_1155));
        if (CollectionUtils.isEmpty(tokens)) return Collections.emptyList();

        List<Callable<NonFungibleAssetBalance>> calls = tokens
                .stream()
                .filter(x -> contractAddresses == null || contractAddresses.length == 0 || contracts.contains(x.getAddress().toLowerCase()))
                .map(t -> t.getType() == ContractType.ERC_721
                          ? mapERC721((Erc721TokenBalances) t)
                          : mapERC1155((Erc1155TokenBalances) t))
                .flatMap(Collection::stream)
                .collect(Collectors.toList());


        List<NonFungibleAssetBalance> result = executorService.invokeAll(calls).stream().map(x -> {
            try {
                return x.get();
            } catch (InterruptedException | ExecutionException e) {
                return null;
            }
        }).collect(Collectors.toList());
        return result;
    }

    protected List<Callable<NonFungibleAssetBalance>> mapERC721(Erc721TokenBalances token) {
        NonFungibleContract contract = createContract(token);
        return token.getTokens() == null
               ? Collections.emptyList()
               : token.getTokens()
                      .stream()
                      .filter(x -> x.getBalance() != null && x.getBalance().compareTo(BigInteger.ZERO) > 0)
                      .map(tb -> (Callable<NonFungibleAssetBalance>) () ->
                              NonFungibleAssetBalance.from(getNonFungibleAsset(tb.getTokenId().toString(), contract, tb.getMetadata()), tb.getBalance())
                          )
                      .collect(Collectors.toList());

    }

    protected List<Callable<NonFungibleAssetBalance>> mapERC1155(Erc1155TokenBalances token) {
        NonFungibleContract contract = createContract(token);
        return token.getTokens() == null
               ? Collections.emptyList()
               : token.getTokens()
                      .stream()
                      .filter(x -> x.getBalance() != null && x.getBalance().compareTo(BigInteger.ZERO) > 0)
                      .map(tb -> (Callable<NonFungibleAssetBalance>) () ->
                              NonFungibleAssetBalance.from(getNonFungibleAsset(tb.getTokenId().toString(), contract, tb.getMetadata()), tb.getBalance()))
                      .collect(Collectors.toList());

    }

    protected NonFungibleContract createContract(Erc721TokenBalances token) {
        return NonFungibleContract.builder()
                                  .address(token.getAddress())
                                  .type(token.getType().name())
                                  .name(token.getName())
                                  .symbol(token.getSymbol())
                                  .build();
    }

    protected NonFungibleContract createContract(Erc1155TokenBalances token) {
        return NonFungibleContract.builder()
                                  .address(token.getAddress())
                                  .name(token.getName())
                                  .symbol(token.getSymbol())
                                  .type(token.getType().name())
                                  .build();
    }

    @Override
    @SneakyThrows
    public NonFungibleAsset getNonFungible(final String contractAddress,
                                           final String tokenId) {
        return azraelClient.getToken(contractAddress, tokenId)
                           .map(t -> getNonFungibleAsset(tokenId, mapToContract(contractAddress, t.getContract()), t.getMetadata()))
                           .orElse(null);
    }

    protected NonFungibleAsset getNonFungibleAsset(String tokenId,
                                                   NonFungibleContract contract,
                                                   String strMetaData) {
        if (StringUtils.isNotBlank(strMetaData)) {
            NonFungibleMetaData metaData = metadataParser.parseMetaData(getSecretType(), tokenId, contract.getType(), contract.getAddress(), strMetaData);
            String imageUrl = metaData.getImage().map(i -> i.replaceAll(" ", "%20")).orElse(null);
            return NonFungibleAsset.builder()
                                   .name(metaData.getName())
                                   .imageUrl(imageUrl)
                                   .imagePreviewUrl(imageUrl)
                                   .imageThumbnailUrl(imageUrl)
                                   .id(tokenId)
                                   .contract(enrichContract(contract, metaData))
                                   .description(metaData.getDescription())
                                   .url(metaData.getExternalUrl().orElse(null))
                                   .maxSupply(metaData.getMaxSupply().map(BigInteger::new).orElse(null))
                                   .animationUrl(metaData.getAnimationUrl().orElse(null))
                                   .animationUrls(metaData.getAnimationUrls())
                                   .attributes(enrichAttributes(metaData))
                                   .build();
        }
        return NonFungibleAsset.builder()
                               .id(tokenId)
                               .contract(contract)
                               .name(tokenId)
                               .description(contract.getAddress())
                               .build();
    }

    private NonFungibleContract enrichContract(NonFungibleContract contract,
                                               NonFungibleMetaData metaData) {
        NonFungibleContract result = contract.toBuilder().build();
        metaData.getContract().ifPresent(c -> {
            if (StringUtils.isNotBlank(c.getDescription())) {
                result.setDescription(c.getDescription());
            }
            if (StringUtils.isNotBlank(c.getSymbol())) {
                result.setSymbol(c.getSymbol());
            }
            if (StringUtils.isNotBlank(c.getName())) {
                result.setName(c.getName());
            }
            if (StringUtils.isNotBlank(c.getType())) {
                result.setType(c.getType());
            }
            if (c.getMedia() != null) {
                result.setMedia(c.getMedia());
            }
            if (StringUtils.isNotBlank(c.getImageUrl())) {
                result.setImageUrl(c.getImageUrl());
            }
            if (StringUtils.isNotBlank(c.getUrl())) {
                result.setUrl(c.getUrl());
            }
            if (StringUtils.isNotBlank(c.getBanner())) {
                result.setBanner(c.getBanner());
            }
        });
        return result;
    }

    private List<Attribute> enrichAttributes(NonFungibleMetaData metaData) {
        return metaData.getAttributes()
                       .stream()
                       .filter(Objects::nonNull)
                       .map(attribute -> {
                           if (StringUtils.isNotBlank(attribute.getDisplayType())) {
                               if ("number".equalsIgnoreCase(attribute.getDisplayType())) {
                                   attribute.setType("stat");
                               } else if ("boost_number".equalsIgnoreCase(attribute.getDisplayType())) {
                                   attribute.setType("boost");
                               } else if ("boost_percentage".equalsIgnoreCase(attribute.getDisplayType())) {
                                   attribute.setType("boost");
                               }
                           }
                           if (StringUtils.isBlank(attribute.getType())) {
                               attribute.setType("property");
                           }
                           return attribute;
                       })
                       .collect(Collectors.toList());
    }


    @Override
    public NonFungibleContract getNonFungibleContract(final String contractAddress) {
        return azraelClient.getContract(contractAddress)
                           .map(token -> mapToContract(contractAddress, token))
                           .orElse(null);
    }

    private NonFungibleContract mapToContract(String contractAddress,
                                              network.arkane.blockchainproviders.azrael.dto.contract.ContractDto token) {
        return NonFungibleContract.builder()
                                  .type(token.getContractType().name())
                                  .address(contractAddress)
                                  .name(token.getName())
                                  .symbol(token.getSymbol())
                                  .build();
    }


    public void destroy() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

}
