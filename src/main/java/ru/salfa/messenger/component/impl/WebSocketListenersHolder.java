package ru.salfa.messenger.component.impl;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketListenersHolder {
    private final Map<String, WebSocketSession> listeners = new ConcurrentHashMap<>();

    public void addListener(String phone, WebSocketSession session) {
        listeners.put(phone, session);
    }

    public void removeListener(String phone) {
        listeners.remove(phone);
    }

    public int size() {
        return listeners.size();
    }

    public Map<String, WebSocketSession> getListeners() {
        return Collections.unmodifiableMap(listeners);
    }
}
