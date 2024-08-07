package ru.salfa.messenger.service.impl;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;
import com.mongodb.client.model.Filters;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.stereotype.Service;
import ru.salfa.messenger.entity.FileDocument;
import ru.salfa.messenger.repository.FileDocumentRepository;
import ru.salfa.messenger.service.FileService;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Optional;

@Service
public class FileServiceImpl implements FileService {
    private final MongoDatabaseFactory mongoDatabaseFactory;
    private final FileDocumentRepository fileDocumentRepository;
    private GridFSBucket gridFSBucket;

    public FileServiceImpl(MongoDatabaseFactory mongoDatabaseFactory, FileDocumentRepository fileDocumentRepository) {
        this.fileDocumentRepository = fileDocumentRepository;
        this.mongoDatabaseFactory = mongoDatabaseFactory;
    }

    @PostConstruct
    private void init() {
        gridFSBucket = GridFSBuckets.create(mongoDatabaseFactory.getMongoDatabase());
    }

    @Override
    @Transactional
    public String save(String hash, byte[] fileBytes, String fileName) {
        Optional<FileDocument> existingFile = fileDocumentRepository.findById(hash);
        if (existingFile.isPresent()) {
            return existingFile.get().getUrl();
        } else {
            var fileId = gridFSBucket.uploadFromStream(fileName, new ByteArrayInputStream(fileBytes),
                    new GridFSUploadOptions()
                            .metadata(new Document("hash", hash)));

            String fileUrl = "files/" + fileId.toHexString();
            FileDocument fileMetadata = new FileDocument(hash, fileUrl);
            fileDocumentRepository.save(fileMetadata);
            return fileUrl;
        }
    }

    @Override
    public String getFileAsBase64(String id) {
        GridFSFile gridFSFile = gridFSBucket.find(Filters.eq("_id", new ObjectId(id))).first();
        if (gridFSFile == null) {
            return "File not found";
        }
        try (InputStream inputStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            byte[] fileBytes = outputStream.toByteArray();
            return Base64.getEncoder().encodeToString(fileBytes);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read file", e);
        }
    }
}
