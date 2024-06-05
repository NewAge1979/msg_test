package ru.salfa.messenger.dto.response;

public record SignInResponse(String accessToken, String refreshToken, boolean isNewUser) {
    @Override
    public String toString() {
        return "Tokens: [Access: " + accessToken() + "; Refresh: " + refreshToken() + "; isNewUser: " + isNewUser() + "]";
    }
}