// SPDX-License-Identifier: MIT
pragma solidity ^0.8.19;

contract CertificateRegistry {
    
    struct Certificate {
        bytes32 hash;
        address issuer;
        uint256 issuedAt;
        bool revoked;
    }
    
    mapping(string => Certificate) private certificates;
    address public owner;
    
    event CertificateIssued(string indexed certificateId, bytes32 hash, address issuer, uint256 timestamp);
    event CertificateRevoked(string indexed certificateId, address revoker, uint256 timestamp);
    
    constructor() {
        owner = msg.sender;
    }
    
    modifier onlyOwner() {
        require(msg.sender == owner, "Only owner can call this function");
        _;
    }
    
    function issueCertificate(string memory certificateId, bytes32 hash) public onlyOwner {
        require(certificates[certificateId].issuedAt == 0, "Certificate already issued");
        
        certificates[certificateId] = Certificate({
            hash: hash,
            issuer: msg.sender,
            issuedAt: block.timestamp,
            revoked: false
        });
        
        emit CertificateIssued(certificateId, hash, msg.sender, block.timestamp);
    }
    
    function revokeCertificate(string memory certificateId) public onlyOwner {
        require(certificates[certificateId].issuedAt != 0, "Certificate does not exist");
        require(!certificates[certificateId].revoked, "Certificate already revoked");
        
        certificates[certificateId].revoked = true;
        
        emit CertificateRevoked(certificateId, msg.sender, block.timestamp);
    }
    
    function verifyCertificate(string memory certificateId, bytes32 hash) public view returns (
        bool valid,
        address issuer,
        uint256 issuedAt,
        bool revoked
    ) {
        Certificate memory cert = certificates[certificateId];
        
        if (cert.issuedAt == 0) {
            return (false, address(0), 0, false);
        }
        
        return (
            cert.hash == hash && !cert.revoked,
            cert.issuer,
            cert.issuedAt,
            cert.revoked
        );
    }
    
    function getCertificate(string memory certificateId) public view returns (
        bytes32 hash,
        address issuer,
        uint256 issuedAt,
        bool revoked
    ) {
        Certificate memory cert = certificates[certificateId];
        return (cert.hash, cert.issuer, cert.issuedAt, cert.revoked);
    }
}