package blockchain.Repository;


import org.springframework.data.jpa.repository.JpaRepository;

import blockchain.Entity.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}