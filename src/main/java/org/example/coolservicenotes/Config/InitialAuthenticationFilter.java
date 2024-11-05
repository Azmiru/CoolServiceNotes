package org.example.coolservicenotes.Config;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.coolservicenotes.DTO.UserDto;
import org.example.coolservicenotes.service.JwtService;
import org.hibernate.ObjectNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
public class InitialAuthenticationFilter extends OncePerRequestFilter {
    private JwtService jwtService;
    private  UsernamePasswordAuthenticationProvider authenticationProvider;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)throws IOException{
        if (request.getHeader("Authorization") != null) {
            String bodyJson = request.getReader().readLine();
            if (bodyJson != null) {
                ObjectMapper mapper = new ObjectMapper();
                UserDto userDto = mapper.readValue(bodyJson, UserDto.class);
                String username = userDto.getUsername();
                String password = userDto.getPassword();
                try {
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
                    authentication = (UsernamePasswordAuthenticationToken) authenticationProvider.authentication(authentication);
                    String jwt = jwtService.generatedJwt(authentication);
                }catch (BadCredentialsException | ObjectNotFoundException e) {
                    logger.error(e.getMessage());
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            }
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().equals("/login");
    }
}
