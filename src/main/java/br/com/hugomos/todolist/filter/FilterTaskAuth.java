package br.com.hugomos.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.hugomos.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var servletPath = request.getServletPath();

        if(servletPath.startsWith("/tasks")){
            //Pegar do Header da requisição a autenticação (usuario e senha)
            var authorization = request.getHeader("Authorization"); // Basic aHVnb21vczpwYXNzd29yZA==

            var authEncoded = authorization.substring("Basic".length()).trim(); // aHVnb21vczpwYXNzd29yZA==
            byte[] authDecoded = Base64.getDecoder().decode(authEncoded); // [B@1d532f1c

            String authDecodedString = new String(authDecoded); // hugomos:password (username):(senha)
            String[] credentials = authDecodedString.split(":"); // ["hugomos", "password"]

            var username = credentials[0];
            var password = credentials[1];

            //validar usuario
            var user = this.userRepository.findByUsername(username);
            if(user == null){
                response.sendError(HttpStatus.UNAUTHORIZED.value());
            } else {
                // validar senha
                boolean passwordVerified = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword()).verified;
                if(!passwordVerified) {
                    response.sendError(HttpStatus.UNAUTHORIZED.value());
                }

                request.setAttribute("userId", user.getId());
                filterChain.doFilter(request, response);
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
