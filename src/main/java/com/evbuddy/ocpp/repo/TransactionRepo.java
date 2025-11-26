package com.evbuddy.ocpp.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.evbuddy.ocpp.domain.Transaction;
import com.evbuddy.ocpp.domain.User;

@Repository
public interface TransactionRepo extends JpaRepository<Transaction, Long> {

    //  Existing methods
    Optional<Transaction> findByStation_StationIdAndTransactionIdOcpp(String stationId, Integer txId);
    List<Transaction> findByStartTimeBetween(Instant from, Instant to);
    List<Transaction> findByUser(User user);
    List<Transaction> findByUserAndState(User user, String state);

    @Query("SELECT t FROM Transaction t WHERE t.user.userId = :userId ORDER BY t.startTime DESC")
    List<Transaction> findByUserIdOrderByStartTimeDesc(@Param("userId") String userId);

    //  NEW METHODS for Meter Value Tracking + Reporting

    // 1 Find ongoing transaction for a station + connector
    Optional<Transaction> findByStation_StationIdAndConnectorIdAndEndTimeIsNull(String stationId, Integer connectorId);

    // 2️ Find all transactions from a station
    List<Transaction> findByStation_StationId(String stationId);

    // 3️ Find transactions that ended within a given range
    List<Transaction> findByEndTimeBetween(Instant from, Instant to);

    // 4️ Find all currently active transactions
    List<Transaction> findByEndTimeIsNull();
}
