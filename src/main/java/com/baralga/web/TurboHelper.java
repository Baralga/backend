package com.baralga.web;

import javax.servlet.http.HttpServletRequest;

public class TurboHelper {

    private TurboHelper() {
        throw new IllegalStateException("Utility class");
    }

    static boolean isReferedFromLogin(HttpServletRequest request) {
        return request.getHeader("Referer") != null
                && request.getHeader("Referer").endsWith("/login");
    }
}
