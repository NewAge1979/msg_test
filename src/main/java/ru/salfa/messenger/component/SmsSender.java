package ru.salfa.messenger.component;

import ru.salfa.messenger.exception.SmsSenderException;

public interface SmsSender {
    void sendSms(String phone, String message) throws SmsSenderException;
}