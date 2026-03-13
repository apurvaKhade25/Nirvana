package com.healthapp.Nirvana.Config;

import com.healthapp.Nirvana.Service.JwtService;
import com.healthapp.Nirvana.Service.MyUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {
    @Autowired
    private JwtService jwtService; //to validate token

    @Autowired
    private MyUserDetailsService userDetailsService; //to load user from db

    @Autowired
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;

    @Autowired
    private ServletContext servletContext;

    //heart of this class
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //get authorization header
        String Authheader = request.getHeader("Authorization");
        String token = null;
        String username = null;

        //check if header contains bearer token
        if (Authheader != null && Authheader.startsWith("Bearer ")) {
            token = Authheader.substring(7);
            username = jwtService.extractUsername(token);
        }

        //if user exists but not authenticated
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //validate the token: does username inside token match db username
            if (jwtService.validateToken(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities() //name,password,role
                );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                //set authentication in spring boot
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

        }
        //continue filter chain
        filterChain.doFilter(request, response);


    }
}


