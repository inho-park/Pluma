package com.dowon.fluma.user.repository;



import com.dowon.fluma.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByUsername(String username);
    boolean existsByName(String name);
    Optional<User> findByUsername(String username);
    Optional<User> findByName(String name);
}
