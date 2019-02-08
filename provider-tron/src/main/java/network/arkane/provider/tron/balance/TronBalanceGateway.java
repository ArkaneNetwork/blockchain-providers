package network.arkane.provider.tron.balance;

import com.google.protobuf.ByteString;
import lombok.extern.slf4j.Slf4j;
import network.arkane.provider.PrecisionUtil;
import network.arkane.provider.balance.BalanceGateway;
import network.arkane.provider.balance.domain.Balance;
import network.arkane.provider.balance.domain.TokenBalance;
import network.arkane.provider.chain.SecretType;
import network.arkane.provider.exceptions.ArkaneException;
import network.arkane.provider.token.TokenDiscoveryService;
import network.arkane.provider.token.TokenInfo;
import network.arkane.provider.tron.grpc.GrpcClient;
import org.springframework.stereotype.Component;
import org.tron.protos.Protocol;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class TronBalanceGateway implements BalanceGateway {
    private final GrpcClient rpcCli;
    private TokenDiscoveryService tokenDiscoveryService;

    public TronBalanceGateway(final GrpcClient grpcClient,
                              final TokenDiscoveryService tokenDiscoveryService) {

        this.rpcCli = grpcClient;
        this.tokenDiscoveryService = tokenDiscoveryService;
    }

    @Override
    public SecretType type() {
        return SecretType.ETHEREUM;
    }

    @Override
    public Balance getBalance(final String account) {
        try {
            final byte[] bytes = GrpcClient.decodeFromBase58Check(account);
            Protocol.Account result = this.rpcCli.getBlockingStubSolidity()
                                                 .getAccount(Protocol.Account.newBuilder().setAddress(ByteString.copyFrom(bytes)).build());
            return Balance.builder()
                          .rawBalance(String.valueOf(result.getBalance()))
                          .rawGasBalance(String.valueOf(result.getBalance()))
                          .secretType(SecretType.TRON)
                          .gasBalance(PrecisionUtil.toDecimal(BigInteger.valueOf(result.getBalance()), 6))
                          .balance(PrecisionUtil.toDecimal(BigInteger.valueOf(result.getBalance()), 6))
                          .symbol("TRX")
                          .gasSymbol("TRX")
                          .decimals(18)
                          .build();
        } catch (final Exception ex) {
            throw ArkaneException.arkaneException()
                                 .message(String.format("Unable to get the balance for the specified account (%s)", account))
                                 .errorCode("web3.internal-error")
                                 .build();
        }
    }

    @Override
    public TokenBalance getTokenBalance(final String walletAddress,
                                        final String tokenAddress) {
        final TokenInfo tokenInfo = tokenDiscoveryService.getTokenInfo(SecretType.TRON, tokenAddress).orElseThrow(IllegalArgumentException::new);
        return getTokenBalance(walletAddress, tokenInfo);
    }

    private TokenBalance getTokenBalance(final String walletAddress, final TokenInfo tokenInfo) {
        final byte[] bytes = GrpcClient.decodeFromBase58Check(walletAddress);
        final Protocol.Account result = this.rpcCli.getBlockingStubSolidity().getAccount(Protocol.Account.newBuilder().setAddress(ByteString.copyFrom(bytes)).build());
        final Long tokenBalance = result.getAssetV2OrDefault(tokenInfo.getAddress(), 0);
        return TokenBalance.builder()
                           .tokenAddress(tokenInfo.getAddress())
                           .rawBalance(tokenBalance.toString())
                           .balance(calculateBalance(tokenBalance, tokenInfo))
                           .decimals(tokenInfo.getDecimals())
                           .symbol(tokenInfo.getSymbol())
                           .logo(tokenInfo.getLogo())
                           .build();
    }

    @Override
    public List<TokenBalance> getTokenBalances(final String walletAddress) {
        return getTokenBalances(walletAddress, tokenDiscoveryService.getTokens(SecretType.TRON));
    }

    private List<TokenBalance> getTokenBalances(final String walletAddress, final List<TokenInfo> tokenInfo) {
        final byte[] bytes = GrpcClient.decodeFromBase58Check(walletAddress);
        final Protocol.Account result = this.rpcCli.getBlockingStubSolidity().getAccount(Protocol.Account.newBuilder().setAddress(ByteString.copyFrom(bytes)).build());
        List<Long> balances = tokenInfo.stream()
                                       .map(ti -> result.getAssetV2OrDefault(ti.getAddress(), 0)).collect(Collectors.toList());
        final List<TokenBalance> results = new ArrayList<>();
        IntStream.range(0, balances.size()).forEachOrdered(i -> {
            final TokenInfo token = tokenInfo.get(i);
            results.add(TokenBalance.builder()
                                    .tokenAddress(token.getAddress())
                                    .rawBalance(balances.get(i).toString())
                                    .balance(calculateBalance(balances.get(i), token))
                                    .decimals(token.getDecimals())
                                    .symbol(token.getSymbol())
                                    .logo(token.getLogo())
                                    .build());
        });
        return results;
    }

    private double calculateBalance(final Long tokenBalance, final TokenInfo tokenInfo) {
        final BigDecimal rawBalance = new BigDecimal(tokenBalance);
        final BigDecimal divider = BigDecimal.valueOf(10).pow(tokenInfo.getDecimals());
        return rawBalance.divide(divider, 6, RoundingMode.HALF_DOWN).doubleValue();
    }
}