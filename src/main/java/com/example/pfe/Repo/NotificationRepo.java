package com.example.pfe.Repo;

import com.example.pfe.Domain.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;

@Repository
public interface NotificationRepo  extends JpaRepository<Notification, Long> {
    @Query("SELECT new com.example.pfe.Domain.Notification (n.id, n.title, n.timestamp ) FROM Notification n  ")
    Page<Notification> findBy(Pageable pageable);
    void deleteByTimestampBefore(Instant oneMonthAgo);
}

