package ru.salfa.messenger.utils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class SimpleObjectMapper {
    private static final ObjectMapper INSTANCE = new ObjectMapper();

    private SimpleObjectMapper() {
    }
    public static ObjectMapper getObjectMapper(){
        return INSTANCE;
    }

}
