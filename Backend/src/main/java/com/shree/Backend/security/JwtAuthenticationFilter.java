package com.shree.Backend.security;

import com.shree.Backend.repository.UserRepository;
import com.shree.Backend.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + authHeader);
        String token = null;
        String userId = null;


        if(authHeader != null && authHeader.startsWith("Bearer ")){
            token = authHeader.substring(7);
            try{
                userId = jwtUtil.getUserIdFromToken(token);
            }catch (Exception e){
                log.error("Token is not valid or available{}", e.getMessage());
            }
        }


        if(userId != null && SecurityContextHolder.getContext().getAuthentication() == null){
            try{
                if(jwtUtil.validateToken(token) && !jwtUtil.isTokenExpired(token)){
                    userRepository.findById(userId).orElseThrow(() -> new UsernameNotFoundException("User not found"));
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userId, null, new ArrayList<>());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    System.out.println(SecurityContextHolder.getContext().getAuthentication());
                }
            }catch (Exception e){
                log.error("exception while validating token");
            }
        }
        filterChain.doFilter(request, response);

    }
}
