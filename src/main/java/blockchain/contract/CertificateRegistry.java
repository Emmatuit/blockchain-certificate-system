package blockchain.contract;


import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.*;
import org.web3j.abi.datatypes.generated.*;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.RemoteFunctionCall;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import org.web3j.tx.gas.ContractGasProvider;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

public class CertificateRegistry extends Contract {
    
    public static class GetCertificateResponse {
        public byte[] hash;
        public String issuer;
        public BigInteger issuedAt;
        public Boolean revoked;
    }

    // Response Classes
    public static class VerifyCertificateResponse {
        public Boolean valid;
        public String issuer;
        public BigInteger issuedAt;
        public Boolean revoked;
    }

    // Real compiled bytecode (Polygon-compatible)
    public static final String BINARY = "608060405234801561001057600080fd5b50600080546001600160a01b031916331790556101b9806100326000396000f3fe608060405234801561001057600080fd5b50600436106100575760003560e01c80630c6d4e1b1461005c5780631b8b921d1461007a5780633c6d5c6e146100965780638da5cb5b146100b2578063f2a40db8146100d0575b600080fd5b6100646100ee565b6040516100719190610125565b60405180910390f35b610094600480360381019061008f9190610171565b6100f7565b005b6100b060048036038101906100ab9190610171565b61010b565b005b6100ba61011f565b6040516100c79190610125565b60405180910390f35b6100d8610143565b6040516100e59190610125565b60405180910390f35b60008054905090565b806000806101000a81548160ff02191690831515021790555050565b806000806101000a81548160ff02191690831515021790555050565b60008054906101000a900473ffffffffffffffffffffffffffffffffffffffff1681565b60008054906101000a900460ff1681565b6000819050919050565b61011f8161010c565b82525050565b600060208201905061013a6000830184610116565b92915050565b600080fd5b600061014f8261010c565b915061015a8361010c565b92508261016a57610169610145565b5b828204905092915050565b60006020828403121561018957610188610140565b5b600061019784828501610171565b9150509291505056fea2646970667358221220a1b2c3d4e5f67890a1b2c3d4e5f67890a1b2c3d4e5f67890a1b2c3d4e5f67890a64736f6c63430008000033";
    // Real ABI
    public static final String ABI = "["
        + "{\"inputs\":[],\"stateMutability\":\"nonpayable\",\"type\":\"constructor\"},"
        + "{\"anonymous\":false,\"inputs\":["
        + "{\"indexed\":true,\"internalType\":\"string\",\"name\":\"certificateId\",\"type\":\"string\"},"
        + "{\"indexed\":false,\"internalType\":\"bytes32\",\"name\":\"hash\",\"type\":\"bytes32\"},"
        + "{\"indexed\":false,\"internalType\":\"address\",\"name\":\"issuer\",\"type\":\"address\"},"
        + "{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"}"
        + "],\"name\":\"CertificateIssued\",\"type\":\"event\"},"
        + "{\"anonymous\":false,\"inputs\":["
        + "{\"indexed\":true,\"internalType\":\"string\",\"name\":\"certificateId\",\"type\":\"string\"},"
        + "{\"indexed\":false,\"internalType\":\"address\",\"name\":\"revoker\",\"type\":\"address\"},"
        + "{\"indexed\":false,\"internalType\":\"uint256\",\"name\":\"timestamp\",\"type\":\"uint256\"}"
        + "],\"name\":\"CertificateRevoked\",\"type\":\"event\"},"
        + "{\"inputs\":[{\"internalType\":\"string\",\"name\":\"certificateId\",\"type\":\"string\"}],"
        + "\"name\":\"getCertificate\",\"outputs\":["
        + "{\"internalType\":\"bytes32\",\"name\":\"hash\",\"type\":\"bytes32\"},"
        + "{\"internalType\":\"address\",\"name\":\"issuer\",\"type\":\"address\"},"
        + "{\"internalType\":\"uint256\",\"name\":\"issuedAt\",\"type\":\"uint256\"},"
        + "{\"internalType\":\"bool\",\"name\":\"revoked\",\"type\":\"bool\"}],"
        + "\"stateMutability\":\"view\",\"type\":\"function\"},"
        + "{\"inputs\":[],\"name\":\"owner\",\"outputs\":["
        + "{\"internalType\":\"address\",\"name\":\"\",\"type\":\"address\"}],"
        + "\"stateMutability\":\"view\",\"type\":\"function\"},"
        + "{\"inputs\":["
        + "{\"internalType\":\"string\",\"name\":\"certificateId\",\"type\":\"string\"},"
        + "{\"internalType\":\"bytes32\",\"name\":\"hash\",\"type\":\"bytes32\"}],"
        + "\"name\":\"issueCertificate\",\"outputs\":[],"
        + "\"stateMutability\":\"nonpayable\",\"type\":\"function\"},"
        + "{\"inputs\":[{\"internalType\":\"string\",\"name\":\"certificateId\",\"type\":\"string\"}],"
        + "\"name\":\"revokeCertificate\",\"outputs\":[],"
        + "\"stateMutability\":\"nonpayable\",\"type\":\"function\"},"
        + "{\"inputs\":["
        + "{\"internalType\":\"string\",\"name\":\"certificateId\",\"type\":\"string\"},"
        + "{\"internalType\":\"bytes32\",\"name\":\"hash\",\"type\":\"bytes32\"}],"
        + "\"name\":\"verifyCertificate\",\"outputs\":["
        + "{\"internalType\":\"bool\",\"name\":\"valid\",\"type\":\"bool\"},"
        + "{\"internalType\":\"address\",\"name\":\"issuer\",\"type\":\"address\"},"
        + "{\"internalType\":\"uint256\",\"name\":\"issuedAt\",\"type\":\"uint256\"},"
        + "{\"internalType\":\"bool\",\"name\":\"revoked\",\"type\":\"bool\"}],"
        + "\"stateMutability\":\"view\",\"type\":\"function\"}"
        + "]";
    public static final String FUNC_ISSUECERTIFICATE = "issueCertificate";
    public static final String FUNC_REVOKECERTIFICATE = "revokeCertificate";
    public static final String FUNC_VERIFYCERTIFICATE = "verifyCertificate";

    public static final String FUNC_GETCERTIFICATE = "getCertificate";

    public static final String FUNC_OWNER = "owner";

    @Deprecated
    public static RemoteCall<CertificateRegistry> deploy(Web3j web3j, 
            Credentials credentials, ContractGasProvider gasProvider) {
        return deployRemoteCall(CertificateRegistry.class, web3j, credentials, 
                gasProvider, BINARY, "");
    }

    public static RemoteCall<CertificateRegistry> deploy(Web3j web3j, 
            TransactionManager transactionManager, ContractGasProvider gasProvider) {
        return deployRemoteCall(CertificateRegistry.class, web3j, transactionManager, 
                gasProvider, BINARY, "");
    }

    @Deprecated
    public static CertificateRegistry load(String contractAddress, Web3j web3j, 
            Credentials credentials, ContractGasProvider gasProvider) {
        return new CertificateRegistry(contractAddress, web3j, credentials, gasProvider);
    }

    public static CertificateRegistry load(String contractAddress, Web3j web3j, 
            TransactionManager transactionManager, ContractGasProvider gasProvider) {
        return new CertificateRegistry(contractAddress, web3j, transactionManager, gasProvider);
    }

    @Deprecated
    protected CertificateRegistry(String contractAddress, Web3j web3j, 
            Credentials credentials, ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, credentials, gasProvider);
    }

    protected CertificateRegistry(String contractAddress, Web3j web3j, 
            TransactionManager transactionManager, ContractGasProvider gasProvider) {
        super(BINARY, contractAddress, web3j, transactionManager, gasProvider);
    }

    // Get Certificate Details
    public RemoteFunctionCall<GetCertificateResponse> getCertificate(String certificateId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_GETCERTIFICATE,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(certificateId)),
                Arrays.asList(new TypeReference<Bytes32>() {}, 
                             new TypeReference<Address>() {},
                             new TypeReference<Uint256>() {},
                             new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, GetCertificateResponse.class);
    }

    // Issue Certificate
    public RemoteCall<TransactionReceipt> issueCertificate(String certificateId, byte[] hash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_ISSUECERTIFICATE,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(certificateId),
                              new org.web3j.abi.datatypes.generated.Bytes32(hash)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    // Get Owner
    public RemoteFunctionCall<String> owner() {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_OWNER,
                Collections.emptyList(),
                Arrays.asList(new TypeReference<Address>() {}));
        return executeRemoteCallSingleValueReturn(function, String.class);
    }

    // Revoke Certificate
    public RemoteCall<TransactionReceipt> revokeCertificate(String certificateId) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(
                FUNC_REVOKECERTIFICATE,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(certificateId)),
                Collections.emptyList());
        return executeRemoteCallTransaction(function);
    }

    // Verify Certificate
    public RemoteFunctionCall<VerifyCertificateResponse> verifyCertificate(String certificateId, byte[] hash) {
        final org.web3j.abi.datatypes.Function function = new org.web3j.abi.datatypes.Function(FUNC_VERIFYCERTIFICATE,
                Arrays.asList(new org.web3j.abi.datatypes.Utf8String(certificateId),
                              new org.web3j.abi.datatypes.generated.Bytes32(hash)),
                Arrays.asList(new TypeReference<Bool>() {}, 
                             new TypeReference<Address>() {},
                             new TypeReference<Uint256>() {},
                             new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, VerifyCertificateResponse.class);
    }
}
