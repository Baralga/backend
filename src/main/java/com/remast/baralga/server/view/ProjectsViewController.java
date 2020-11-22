package com.remast.baralga.server.view;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectsViewController {

    @GetMapping("/projects")
    public String showProjects() {
        return "projects";
    }
}
