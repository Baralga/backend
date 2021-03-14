package com.remast.baralga.server.web;

import javax.servlet.http.HttpServletRequest;

public class TurboHelper {

    static boolean isReferedFromLogin(HttpServletRequest request) {
        return request.getHeader("Referer") != null
                && request.getHeader("Referer").endsWith("/login");
    }
}
