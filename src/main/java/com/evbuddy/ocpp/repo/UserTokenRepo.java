package com.evbuddy.ocpp.repo;
import org.springframework.data.jpa.repository.JpaRepository;

import com.evbuddy.ocpp.domain.UserToken;

import java.util.Optional;
public interface UserTokenRepo extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByIdTag(String idTag);
}
