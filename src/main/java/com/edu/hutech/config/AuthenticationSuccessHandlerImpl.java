package com.edu.hutech.config;

import com.edu.hutech.entities.User;
import com.edu.hutech.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class AuthenticationSuccessHandlerImpl implements AuthenticationSuccessHandler {


    @Autowired
    HttpSession session; //autowiring session

    @Autowired
    UserRepository repository; //autowire the user repo

    @Override
    public void onAuthenticationSuccess(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Authentication authentication) throws IOException, ServletException {
        User userName = ((User)authentication.getPrincipal());
        //HttpSession session = request.getSession();
        session.setAttribute("userId", userName.getId());
    }
}
