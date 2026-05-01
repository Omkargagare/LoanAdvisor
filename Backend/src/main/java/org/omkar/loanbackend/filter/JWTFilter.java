package org.omkar.loanbackend.filter;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.omkar.loanbackend.repo.BlacklistTokenRepo;
import org.omkar.loanbackend.service.JWTService;
import org.omkar.loanbackend.service.MyUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private final JWTService jwtService;

    private final MyUserDetailsService userDetailsService;

    private final BlacklistTokenRepo blacklistTokenRepo;

    private static final Logger logger = LoggerFactory.getLogger(JWTFilter.class);

    public JWTFilter(JWTService service, MyUserDetailsService userDetailsService, BlacklistTokenRepo blacklistTokenRepo) {
        this.jwtService = service;
        this.userDetailsService = userDetailsService;
        this.blacklistTokenRepo = blacklistTokenRepo;
    }

    public void sendError(HttpServletResponse response,String message) {
        try {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write(
                    "{\"message\": \"" + message + "\", \"data\": null, \"success\": false}"
            );
        } catch (IOException e) {
            logger.error("Failed to write error response", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                if(jwtService.validateTokenSignature(token)){
                    sendError(response,"Invalid token");
                    return;
                }

                username = jwtService.extractUsername(token);
                String jti = jwtService.extractJtiFromToken(token);

                if(blacklistTokenRepo.existsById(jti)){
                    sendError(response,"Token revoked");
                    return;
                }
            } catch (ExpiredJwtException e) {
                logger.warn("JWT expired: {}", e.getMessage());

                sendError(response,"Token expired");
                return;
            } catch (Exception e) {
                logger.warn("JWT invalid: {}", e.getMessage());

                sendError(response,"Invalid token");
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtService.validateTokenWithUser(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
                        null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
