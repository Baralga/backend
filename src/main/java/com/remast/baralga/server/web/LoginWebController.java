package com.remast.baralga.server.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;

@Transactional
@Controller
@RequiredArgsConstructor
public class LoginWebController {

    @Transactional(readOnly = true)
    @GetMapping("/login")
    public String login(HttpServletResponse response) {
        return "login";
    }
}
