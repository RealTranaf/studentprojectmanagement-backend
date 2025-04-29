package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.Room;
import com.ld.springsecurity.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    Optional<User> findByVerificationCode(String verificationCode);

    List<User> findByUsernameIn(List<String> usernames);

    List<User> findTop5ByUsernameContainingIgnoreCase(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);
}
