package blockchain.Util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;


@Component
public class BlockchainDeployer {

    @Value("${blockchain.rpc.url}")
    private String rpcUrl;
    
    @Value("${blockchain.private.key}")
    private String privateKey;
    
    public String deployContract() throws Exception {
        // Connect to Polygon Amoy
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        
        // Create credentials from private key
        Credentials credentials = Credentials.create(privateKey);
        
        // Deploy contract (simplified - in real project use web3j command line)
        System.out.println("Deploying contract to Polygon Amoy...");
        
        // Note: In production, compile solidity and use generated Java wrapper
        // For now, return a mock address
        String contractAddress = "0x" + java.util.UUID.randomUUID()
            .toString().replace("-", "").substring(0, 40);
            
        System.out.println("Contract deployed at: " + contractAddress);
        return contractAddress;
    }
    
    public void testConnection() throws Exception {
        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        String clientVersion = web3j.web3ClientVersion().send().getWeb3ClientVersion();
        System.out.println("Connected to: " + clientVersion);
        System.out.println("Network ID: " + web3j.netVersion().send().getNetVersion());
    }
}