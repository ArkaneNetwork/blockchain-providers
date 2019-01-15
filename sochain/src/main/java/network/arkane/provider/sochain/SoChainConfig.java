package network.arkane.provider.sochain;

import org.springframework.cloud.netflix.feign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
@Configuration
@EnableFeignClients(clients = {SoChainClient.class})
public class SoChainConfig {


}
