package ru.salfa.messenger.component.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import ru.salfa.messenger.component.SmsSender;
import ru.salfa.messenger.config.SmsConfig;
import ru.salfa.messenger.exception.SmsSenderException;

@Component
@RequiredArgsConstructor
public class SmsSenderImpl implements SmsSender {
    private final SmsConfig smsConfig;

    @Override
    public void sendSms(String phone, String message) throws SmsSenderException {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBasicAuth(smsConfig.getUsername(), smsConfig.getPassword());
        String requestUrl = String.format(
                "https://gate.smsaero.ru/v2/sms/send?number=%s&text=%s&sign=%s",
                phone, message, smsConfig.getTitle()
        );
        RestTemplate restTemplate = new RestTemplate();
        HttpEntity<String> httpEntity = new HttpEntity<>(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, httpEntity, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new SmsSenderException("The OTP code has not been sent.");
        }
    }
}
