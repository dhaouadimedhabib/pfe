package com.example.pfe.Service;

import com.example.pfe.Domain.Notification;
import com.example.pfe.Repo.NotificationRepo;
import org.springframework.data.domain.Page;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import org.springframework.data.domain.Pageable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

@Service
public class NotificationService {
    private final NotificationRepo notificationRepository;

    public NotificationService(NotificationRepo notificationRepository) {
        this.notificationRepository = notificationRepository;
    }


    @Scheduled(cron = "0 0 0 1 * ?") // This cron expression mean
    @Transactional
    public void deleteOldNotifications() {
        LocalDateTime oneMonthAgo = LocalDateTime.now().minus(1, ChronoUnit.MONTHS);
        Instant oneMonthAgoInstant = oneMonthAgo.toInstant(ZoneOffset.UTC);
        notificationRepository.deleteByTimestampBefore(oneMonthAgoInstant);
    }
    public Page<Notification> getNotifications(Pageable pageable) {
        return notificationRepository.findBy(pageable);
    }
}
