package com.example.ocpp.repo;
import com.example.ocpp.domain.UserToken;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface UserTokenRepo extends JpaRepository<UserToken, Long> {
    Optional<UserToken> findByIdTag(String idTag);
}
