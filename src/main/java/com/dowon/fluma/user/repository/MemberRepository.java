package com.dowon.fluma.user.repository;

import com.dowon.fluma.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByUsername(String email);
    Optional<Member> findByName(String name);
    boolean existsByUsername(String email);
}
