package org.example.repository;

import org.example.entity.NotificationEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.management.Notification;

@Repository
public interface EventRepository extends JpaRepository<NotificationEvent,Long> {
}
