package com.remast.baralga.server.web;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Transactional
@Controller
@RequestMapping(value = "/api/activities")
@RequiredArgsConstructor
public class ProjectsWebController {

    @GetMapping("/projects")
    public String showProjects() {
        return "projects";
    }
}
