package ru.salfa.messenger.dto.response;

public record TokensResponse(String accessToken, String refreshToken) {
    @Override
    public String toString() {
        return "Tokens: [Access: " + accessToken() + "; Refresh: " + refreshToken() + "]";
    }
}