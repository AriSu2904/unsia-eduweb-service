package com.unsia.edu.security;

import com.unsia.edu.services.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            String jwtToken = jwtService.parseJwt(request);
            if(jwtToken != null) {
                String generatedEmail = jwtService.extractEmail(jwtToken);
                if (jwtService.isTokenValid(jwtToken, userDetailsService.loadUserByUsername(generatedEmail))) {
                    UserDetails userDetails = userDetailsService.loadUserByUsername(generatedEmail);
                    UsernamePasswordAuthenticationToken authenticateToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

                    authenticateToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authenticateToken);
                }
            }
        }catch (Exception error) {
            System.out.println("ARI Error occured: " + error);
        }
        filterChain.doFilter(request, response);
    }
}
