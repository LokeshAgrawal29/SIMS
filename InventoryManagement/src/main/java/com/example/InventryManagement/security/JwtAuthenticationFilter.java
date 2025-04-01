package com.example.InventryManagement.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.util.AntPathMatcher;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        System.out.println("Checking path: " + path);
        
        // Define public paths that don't need JWT authentication
        String[] publicPaths = {
            "/api/users/register",
            "/api/users/login",
            "/api/users/check-email",
            "/api/test/**"
        };
        
        // Check if the current path matches any of the public paths
        for (String pattern : publicPaths) {
            if (pathMatcher.match(pattern, path)) {
                System.out.println("Path " + path + " matches public pattern " + pattern);
                return true;
            }
        }
        
        System.out.println("Path requires authentication: " + path);
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        try {
            String path = request.getServletPath();
            System.out.println("Processing request for path: " + path);

            // Skip JWT check for public endpoints
            if (shouldNotFilter(request)) {
                System.out.println("Skipping JWT check for public endpoint: " + path);
                chain.doFilter(request, response);
                return;
            }

            // Fetch Authorization header
            String authHeader = request.getHeader("Authorization");
            System.out.println("Received Authorization header: " + (authHeader != null ? "present" : "absent"));

            // Reject if no token is provided
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                System.out.println("No token provided or invalid format");
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"No token provided or invalid format\",\"status\":\"error\"}");
                return;
            }

            // Extract JWT token
            String jwt = authHeader.substring(7);
            String userEmail = null;

            try {
                userEmail = jwtUtil.extractUsername(jwt);
                System.out.println("Extracted user email: " + userEmail);

                if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
                    System.out.println("Loaded user details for: " + userDetails.getUsername());

                    if (jwtUtil.validateToken(jwt, userDetails)) {
                        System.out.println("Token validated successfully");
                        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                        System.out.println("Authentication set in SecurityContext");
                    }
                }
            } catch (Exception e) {
                System.out.println("Error processing JWT: " + e.getMessage());
                response.setContentType("application/json");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("{\"error\":\"" + e.getMessage() + "\",\"status\":\"error\"}");
                return;
            }

            chain.doFilter(request, response);
        } catch (Exception e) {
            System.out.println("Global filter error: " + e.getMessage());
            e.printStackTrace();
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"error\":\"Internal server error\",\"status\":\"error\"}");
        }
    }
}
