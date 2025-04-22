package ma.formation.mappers;

import ma.formation.dtos.NotificationDTO;
import ma.formation.entities.Notification;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {
    NotificationDTO toDTO(Notification notif);

    List<NotificationDTO> toDTOs(List<Notification> notifs);

    Notification toEntity(NotificationDTO dto);
}