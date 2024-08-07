package ru.salfa.messenger.service;

public interface FileService {
    String save(String hash, byte[] fileBytes, String fileName);
    String getFileAsBase64(String id);
}
