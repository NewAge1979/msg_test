package ru.salfa.messenger.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import ru.salfa.messenger.exception.UserTokenException;
import ru.salfa.messenger.exception.UserUnauthorizedException;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    //private final String ACCESS_HEADER = "access-token";
    //private final String REFRESH_HEADER = "refresh-token";
    private final String AUTHORIZATION = "Authorization";
    private final String BEARER_PREFIX = "Bearer ";
    @Value("${websocket.endpoint}")
    private String endpointWS;

    private final JwtService jwtService;
    private final UserDetailServiceImpl userDetailService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws IOException, ServletException {
        var authorization = request.getRequestURI().contains(endpointWS)
                ? request.getParameter(AUTHORIZATION) : request.getHeader(AUTHORIZATION);

        log.debug("Authorization: {}", authorization);
        if (StringUtils.isNotEmpty(authorization) && StringUtils.startsWith(authorization, BEARER_PREFIX)) {
            String phone = null;
            if (request.getRequestURI().contains("refreshTokens")) {
                log.debug("RToken 1");
                try {
                    if (jwtService.refreshTokenExistsInCache(authorization)) {
                        phone = jwtService.getPhoneFromRefreshToken(authorization);
                        addUserToSecurityContext(phone);
                        filterChain.doFilter(request, response);
                    } else {
                        sendException(request, response, new UserUnauthorizedException("User not authentication."));
                    }
                } catch (UserTokenException e) {
                    sendException(request, response, new UserUnauthorizedException("User not authentication."));
                }
            } else {
                try {
                    if (jwtService.accessTokenExistsInCache(authorization)) {
                        phone = jwtService.getPhoneFromAccessToken(authorization);
                        addUserToSecurityContext(phone);
                        filterChain.doFilter(request, response);
                    } else {
                        if (jwtService.anotherAccessTokenExistsInCache(authorization)) {
                            sendException(request, response, new UserUnauthorizedException("Token error: Invalid token."));
                        } else {
                            sendException(request, response, new UserUnauthorizedException("User not authentication."));
                        }
                    }
                } catch (UserTokenException e) {
                    sendException(request, response, e);
                }
            }
        } else {
            log.debug("Unauthorized request!");
            filterChain.doFilter(request, response);
        }
    }

    private void addUserToSecurityContext(String phone) {
        if (StringUtils.isNotEmpty(phone) && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailService.loadUserByUsername(phone);
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, "", userDetails.getAuthorities()
            );
            context.setAuthentication(authToken);
            SecurityContextHolder.setContext(context);
        }
    }

    private void sendException(HttpServletRequest request, HttpServletResponse response, Exception e) {
        handlerExceptionResolver.resolveException(request, response, null, e);
    }
}