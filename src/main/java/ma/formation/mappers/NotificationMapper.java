package ma.formation.mappers;

import ma.formation.dtos.NotificationDTO;
import ma.formation.entities.Notification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "patient.id", target = "patientId")
    NotificationDTO toDTO(Notification notification);

    List<NotificationDTO> toDTOs(List<Notification> notifications);

    @Mapping(target = "patient", ignore = true)
    Notification toEntity(NotificationDTO dto);
}