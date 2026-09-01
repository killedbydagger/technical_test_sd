package com.temp.demo.security;

import com.temp.demo.entity.Staff;
import com.temp.demo.service.StaffService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private StaffService staffService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authToken = request.getHeader("Authorization");
        if (!Objects.isNull(authToken) && authToken.startsWith("Bearer ")) {
            String jwtToken = authToken.substring(7);
            try {
                String usernameFromToken = jwtTokenUtil.getUsernameFromToken(jwtToken);
                Staff staff = staffService.getByUsername(usernameFromToken);
                List<SimpleGrantedAuthority> authorities = staffService.getStaffAuthority(staff);
                if (jwtTokenUtil.validateToken(jwtToken, staff)) {
                    UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(staff, null, authorities);
                    usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                }
            } catch (SignatureException e) {
                logger.warn("JWT_SIGNATURE_ERROR");
            } catch (IllegalArgumentException e) {
                logger.error("JWT_TOKEN_UNABLE_TO_GET_USERNAME");
            } catch (ExpiredJwtException e) {
                logger.warn("JWT_TOKEN_EXPIRED");
            } catch (Exception e) {
                logger.warn(String.format("UN CATCH Message (%s), URI (%s), METHOD (%s), Addresses (%s:%s)", e.getMessage(), request.getRequestURI(),
                        request.getMethod(), request.getRemoteAddr(), request.getLocalAddr()));
            }
        }
        filterChain.doFilter(request, response);
    }
}
