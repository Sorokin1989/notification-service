package org.example.mapper;

import org.example.dto.NotificationEventResponse;
import org.example.entity.NotificationEvent;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationEventMapper {

    NotificationEventResponse toNotificationEventResponse(NotificationEvent notificationEvent);

}
