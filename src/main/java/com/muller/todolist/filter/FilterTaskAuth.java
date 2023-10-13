package com.muller.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.muller.todolist.user.UserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;
import java.util.Objects;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    UserRepository userRepository;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var servletPath = request.getServletPath();
        if (servletPath.contains("/tasks")) {
            //pegar autenticação
            var authorization = request.getHeader("Authorization");
            var base64 = authorization.substring("Basic".length()).trim();
            var base64Decodificada = Base64.getDecoder().decode(base64);
            var auth = new String(base64Decodificada).split(":");
            var username = auth[0];
            var password = auth[1];
            var user = userRepository.findByUsername(username);
            if (Objects.isNull(user)) {
                response.sendError(401);
            } else {
                var passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerified.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
