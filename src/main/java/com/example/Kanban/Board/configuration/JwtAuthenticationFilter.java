package com.example.Kanban.Board.configuration;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.Kanban.Board.exceptions.ForbiddenMethodException;
import com.example.Kanban.Board.model.User;
import com.example.Kanban.Board.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String SECRET =
        "my-super-secret-key-that-is-at-least-32-characters-long";

    private static final SecretKey SECRET_KEY =
        Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8));

    private final UserRepository userRepository;

    public JwtAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

     
        String skipInterceptor = request.getHeader("skip-interceptor");
        String requestURI = request.getRequestURI();
        if (!"true".equalsIgnoreCase(skipInterceptor) && !requestURI.startsWith("/ws") ) {
            try {
              String token = request.getHeader("token");
              validateToken(token);
            } catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write(e.getMessage());
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private User validateToken(String token) {
        if(token == null || token.isBlank()) {
            throw new ForbiddenMethodException("Token must be provided");
        }
        User user = userRepository.findByToken(token).orElseThrow(() -> new ForbiddenMethodException("Not valid token"));
        Date expirationDate = getExpirationDate(token);
        if (expirationDate.before(Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant()))) {
            throw new ForbiddenMethodException("Token has expired");
        }
        return user;
    }

    private Date getExpirationDate(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(SECRET_KEY)
            .build()
            .parseClaimsJws(token)
            .getBody();
        return claims.getExpiration();
    }

}
