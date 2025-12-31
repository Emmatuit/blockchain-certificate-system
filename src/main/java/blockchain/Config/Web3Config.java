package blockchain.Config;

// Make sure it's in a package

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class Web3Config {
    
    private static final Logger logger = LoggerFactory.getLogger(Web3Config.class);
    
    @Value("${blockchain.rpc.url}")  // Make sure this property exists
    private String rpcUrl;
    
    @Bean
    public Web3j web3j() {
        logger.info("Initializing Web3j with RPC URL: {}", rpcUrl);
        return Web3j.build(new HttpService(rpcUrl));
    }
}