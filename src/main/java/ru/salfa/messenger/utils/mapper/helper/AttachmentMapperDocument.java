package ru.salfa.messenger.utils.mapper.helper;

import lombok.AllArgsConstructor;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import ru.salfa.messenger.dto.model.Document;
import ru.salfa.messenger.entity.postgres.Attachments;
import ru.salfa.messenger.service.FileService;
import ru.salfa.messenger.service.impl.FileServiceImpl;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

@Component
@AllArgsConstructor
public class AttachmentMapperDocument {
    private static FileService fileService;

    @Autowired
    public AttachmentMapperDocument(@Lazy FileServiceImpl fileService) {
        AttachmentMapperDocument.fileService = fileService;
    }

    @Named("attachmentsToDocument")
    public static List<Document> toDocuments(List<Attachments> attachments) throws FileNotFoundException {
        List<Document> documents = new ArrayList<>();
        for (Attachments attachment : attachments) {
            documents.add(attachmentsToDocument(attachment));
        }
        return documents;
    }


    public static Document attachmentsToDocument(Attachments attachments) {
        if (attachments == null) {
            return null;
        }

        Document document = new Document();
        document.setName(attachments.getFilename());
        document.setData(fileService.getFileAsBase64(attachments.getUrl()
                .substring(attachments.getUrl()
                        .lastIndexOf("/") + 1)));
        document.setType(attachments.getType());

        return document;
    }
}
