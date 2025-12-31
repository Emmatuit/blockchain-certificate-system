package blockchain.ServiceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.StaticGasProvider;
import org.web3j.utils.Numeric;
import java.math.BigInteger;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import blockchain.Entity.Certificate;
import blockchain.Enum.CertificateStatus;
import blockchain.Repository.CertificateRepository;
import blockchain.Response.BlockchainIssueResponse;
import blockchain.Response.VerificationResponse;
import blockchain.Service.BlockchainService;
import blockchain.Service.HashingService;
import blockchain.contract.CertificateRegistry;
import jakarta.annotation.PostConstruct;

@Service
public class BlockchainServiceImpl implements BlockchainService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private static final BigInteger GAS_PRICE = BigInteger.valueOf(30_000_000_000L); // 30 Gwei

    private static final BigInteger GAS_LIMIT = BigInteger.valueOf(300_000L);

    @Autowired
    private Web3j web3j;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private HashingService hashingService;

    @Value("${blockchain.private.key}")
    private String privateKey;
    @Value("${blockchain.contract.address}")
    private String contractAddress;
    @Value("${blockchain.chain.id:80002}")
    private Long chainId;
    private Credentials credentials;
    
    private TransactionManager transactionManager;
    private CertificateRegistry contract;
    private ContractGasProvider gasProvider;

    // Helper method to compare byte arrays
    private boolean arraysEqual(byte[] a1, byte[] a2) {
        if (a1.length != a2.length) return false;
        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) return false;
        }
        return true;
    }

    // Deploy new contract
    private String deployNewContract() throws Exception {
        CertificateRegistry newContract = CertificateRegistry.deploy(
            web3j,
            transactionManager,
            gasProvider
        ).send();
        
        return newContract.getContractAddress();
    }

    // Additional method for public verification with full details
    public VerificationResponse getCertificateVerificationDetails(String certificateId) {
        Optional<Certificate> certOpt = certificateRepository.findByCertificateId(certificateId);
        
        if (certOpt.isEmpty() || certOpt.get().getHash() == null) {
            return VerificationResponse.builder()
                .valid(false)
                .issuer("")
                .issuedAt(0L)
                .revoked(true)
                .build();
        }

        Certificate certificate = certOpt.get();
        String storedHash = certificate.getHash();
        
        try {
            byte[] hashBytes = Numeric.hexStringToByteArray(storedHash);
            
            // Get full certificate details from blockchain
            CertificateRegistry.GetCertificateResponse response = 
                contract.getCertificate(certificateId).send();
            
            return VerificationResponse.builder()
                .valid(arraysEqual(response.hash, hashBytes) && !response.revoked)
                .issuer(response.issuer)
                .issuedAt(response.issuedAt.longValue())
                .revoked(response.revoked)
                .build();
            
        } catch (Exception e) {
            System.err.println("Failed to get verification details: " + e.getMessage());
            return VerificationResponse.builder()
                .valid(false)
                .issuer("")
                .issuedAt(0L)
                .revoked(true)
                .build();
        }
    }

    @PostConstruct
    public void init() throws Exception {
        // Create credentials from private key
        this.credentials = Credentials.create(privateKey);
        
        // Create transaction manager for Polygon Amoy
        this.transactionManager = new RawTransactionManager(
            web3j, 
            credentials, 
            chainId
        );
        
        // Create custom gas provider for Polygon
        this.gasProvider = new StaticGasProvider(GAS_PRICE, GAS_LIMIT);
        
        // Deploy contract if no address provided
        if (contractAddress == null || contractAddress.isEmpty() || contractAddress.equals("DEPLOY_NEW")) {
            contractAddress = deployNewContract();
            System.out.println("✅ New contract deployed at: " + contractAddress);
        }
        
        // Load the deployed contract
        this.contract = CertificateRegistry.load(
            contractAddress,
            web3j,
            transactionManager,
            gasProvider
        );
        
        System.out.println("✅ Smart contract loaded successfully at: " + contractAddress);
        System.out.println("✅ Connected to Polygon Amoy (Chain ID: " + chainId + ")");
    }
    
    // Method to check blockchain connection
    public boolean isBlockchainConnected() {
        try {
            return web3j.web3ClientVersion().send().getWeb3ClientVersion() != null;
        } catch (Exception e) {
            return false;
        }
    }
    
    @Override
    public BlockchainIssueResponse issueCertificate(String certificateId) {
        Optional<Certificate> certOpt = certificateRepository.findByCertificateId(certificateId);
        
        if (certOpt.isEmpty()) {
            throw new RuntimeException("Certificate not found: " + certificateId);
        }

        Certificate certificate = certOpt.get();
        
        // Generate hash
        String hashString = hashingService.generateHash(
            certificate.getCertificateId(),
            certificate.getStudentName(),
            certificate.getCourse(),
            certificate.getIssuedDate().format(DATE_FORMATTER)
        );
        
        try {
            // Convert hash to bytes32 for blockchain
            byte[] hashBytes = Numeric.hexStringToByteArray(hashString);
            
            // Send transaction to blockchain
            TransactionReceipt receipt = contract.issueCertificate(certificateId, hashBytes).send();
            
            // Check if transaction was successful
            if (!receipt.isStatusOK()) {
                throw new RuntimeException("Transaction failed: " + receipt.getStatus());
            }
            
            String transactionHash = receipt.getTransactionHash();
            
            // Update certificate in database
            certificate.setHash(hashString);
            certificate.setBlockchainTxHash(transactionHash);
            certificate.setStatus(CertificateStatus.ISSUED);
            certificateRepository.save(certificate);
            
            System.out.println("✅ Certificate issued on blockchain: " + certificateId);
            System.out.println("Transaction Hash: " + transactionHash);
            System.out.println("Block Number: " + receipt.getBlockNumber());
            System.out.println("Gas Used: " + receipt.getGasUsed());

            return BlockchainIssueResponse.builder()
                .status("ISSUED")
                .transactionHash(transactionHash)
                .build();
                
        } catch (Exception e) {
            throw new RuntimeException("Blockchain transaction failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public String revokeCertificate(String certificateId) {
        Optional<Certificate> certOpt = certificateRepository.findByCertificateId(certificateId);
        
        if (certOpt.isEmpty()) {
            throw new RuntimeException("Certificate not found: " + certificateId);
        }

        try {
            // Send revoke transaction
            TransactionReceipt receipt = contract.revokeCertificate(certificateId).send();
            
            if (!receipt.isStatusOK()) {
                throw new RuntimeException("Revocation transaction failed: " + receipt.getStatus());
            }
            
            String transactionHash = receipt.getTransactionHash();
            
            // Update certificate status
            Certificate certificate = certOpt.get();
            certificate.setStatus(CertificateStatus.REVOKED);
            certificateRepository.save(certificate);
            
            System.out.println("✅ Certificate revoked on blockchain: " + certificateId);
            System.out.println("Transaction Hash: " + transactionHash);

            return "Certificate revoked successfully. TX: " + transactionHash;
        } catch (Exception e) {
            throw new RuntimeException("Blockchain revocation failed: " + e.getMessage(), e);
        }
    }
    
    @Override
    public boolean verifyCertificateOnChain(String certificateId) {
        Optional<Certificate> certOpt = certificateRepository.findByCertificateId(certificateId);
        
        if (certOpt.isEmpty() || certOpt.get().getHash() == null) {
            return false;
        }

        Certificate certificate = certOpt.get();
        String storedHash = certificate.getHash();
        
        try {
            // Convert hash to bytes32
            byte[] hashBytes = Numeric.hexStringToByteArray(storedHash);
            
            // Call view function (doesn't cost gas)
            CertificateRegistry.VerifyCertificateResponse response = 
                contract.verifyCertificate(certificateId, hashBytes).send();
            
            return response.valid && !response.revoked;
            
        } catch (Exception e) {
            System.err.println("Verification failed: " + e.getMessage());
            return false;
        }
    }
}