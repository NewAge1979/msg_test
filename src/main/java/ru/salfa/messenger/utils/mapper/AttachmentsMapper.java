package ru.salfa.messenger.utils.mapper;

import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.dto.model.AttachmentsDto;
import ru.salfa.messenger.entity.Attachments;

import java.util.List;
@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.FIELD)
public interface AttachmentsMapper {
    AttachmentsDto toAttachmentsDto(Attachments entity);

    Attachments toAttachments(AttachmentsDto dto);  // Метод для преобразования из AttachmentsDto в Attachments

    List<AttachmentsDto> toAttachmentsDtoList(List<Attachments> list);

    List<Attachments> toAttachmentsList(List<AttachmentsDto> list);
}
