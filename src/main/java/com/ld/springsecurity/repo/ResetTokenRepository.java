package com.ld.springsecurity.repo;

import com.ld.springsecurity.model.ResetToken;
import com.ld.springsecurity.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, String> {
    Optional<ResetToken> findByToken(String token);

    boolean existsByUser(User user);

    @Transactional
    void deleteByUser(User user);
}
