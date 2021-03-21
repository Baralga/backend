package com.baralga.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;

@Controller
@RequiredArgsConstructor
public class LoginWebController {

    @GetMapping(value = "/login", headers = "Accept=text/html", produces = "text/html")
    public String login(HttpServletResponse response) {
        response.setHeader(HttpHeaders.CACHE_CONTROL,
                CacheControl.maxAge(Duration.ofSeconds(0))
                        .cachePrivate()
                        .mustRevalidate()
                        .getHeaderValue());
        return "login";
    }
}
