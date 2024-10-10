package ru.salfa.messenger.utils.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.dto.model.MessageDto;
import ru.salfa.messenger.entity.postgres.Messages;
import ru.salfa.messenger.utils.mapper.helper.AttachmentMapperDocument;

@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD, uses = AttachmentMapperDocument.class)
public interface MessageMapper {
    @Mapping(source = "entity.senderId.id", target = "sender") // Исправлено: обращение через entity
    @Mapping(source = "entity.attachments", target = "attachments", qualifiedByName = "attachmentsToDocument") // Исправлено: использование entity
    @Mapping(source = "entity.created", target = "created", dateFormat = "yyyy-MM-dd'T'HH:mm:ss") // Исправлено: использование entity
    @Mapping(target = "isMyMessage", expression = "java(isMyMessage(entity, currentUserId))")
    MessageDto toMessageDto(Messages entity, Long currentUserId); // Используется entity как параметр


    default Boolean isMyMessage(Messages message, Long currentUserId) {
        if (message.getSenderId() != null && message.getSenderId().getId() != null) {
            return message.getSenderId().getId().equals(currentUserId);
        }
        return false;
    }
}
