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
    @Mapping(source = "senderId.id", target = "sender")
    @Mapping(source = "attachments", target = "attachments", qualifiedByName = "attachmentsToDocument")
    @Mapping(source = "created", target = "created", dateFormat = "yyyy-MM-dd'T'HH:mm:ss")
    MessageDto toMessageDto(Messages entity);
}
