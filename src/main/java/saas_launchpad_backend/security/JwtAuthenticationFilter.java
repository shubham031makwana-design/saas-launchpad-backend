package saas_launchpad_backend.security;

import java.io.IOException;
import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import saas_launchpad_backend.entity.User;
import saas_launchpad_backend.repository.UserRepository;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

        System.out.println("=================================");
        System.out.println("Request: " + request.getMethod() + " " + path);

        String header = request.getHeader("Authorization");

        System.out.println("Authorization Header: " + header);

        if (header != null && header.startsWith("Bearer ")) {

            String token = header.substring(7);

            try {

                String email = jwtUtil.extractEmail(token);

                System.out.println("JWT Email: " + email);

                if (!jwtUtil.isTokenExpired(token)) {

                    User user = userRepository.findByEmail(email)
                            .orElse(null);

                    if (user != null) {

                        String role = user.getRole();

                        System.out.println("User Role: " + role);

                        UsernamePasswordAuthenticationToken authentication =
                                new UsernamePasswordAuthenticationToken(
                                        email,
                                        null,
                                        Collections.singletonList(
                                                new SimpleGrantedAuthority(
                                                        "ROLE_" + role
                                                )
                                        )
                                );

                        SecurityContextHolder
                                .getContext()
                                .setAuthentication(authentication);

                        System.out.println(
                                "JWT Authentication successful"
                        );

                    } else {

                        System.out.println(
                                "User not found for JWT email"
                        );
                    }

                } else {

                    System.out.println("JWT Token expired");
                }

            } catch (Exception e) {

                System.out.println(
                        "Invalid JWT Token: " + e.getMessage()
                );
            }
        }

        filterChain.doFilter(request, response);
    }
}