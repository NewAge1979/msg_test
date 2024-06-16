package ru.salfa.messenger.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtFilter extends OncePerRequestFilter {
    //private final String ACCESS_HEADER = "access-token";
    //private final String REFRESH_HEADER = "refresh-token";
    private final String AUTHORIZATION = "Authorization";
    private final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailServiceImpl userDetailService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws IOException, ServletException {//}, UserTokenException {
        var authorization = request.getHeader(AUTHORIZATION);
        log.debug("Authorization: {}", authorization);
        if (StringUtils.isNotEmpty(authorization) && StringUtils.startsWith(authorization, BEARER_PREFIX)) {
            var phone = jwtService.getPhoneFromAccessToken(authorization);
            if (StringUtils.isNotEmpty(phone) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailService.loadUserByUsername(phone);
                if (jwtService.accessTokenIsValid(authorization, userDetails)) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails, "", userDetails.getAuthorities()
                    );
                    context.setAuthentication(authenticationToken);
                    SecurityContextHolder.setContext(context);
                }
            }
            filterChain.doFilter(request, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}