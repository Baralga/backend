package com.remast.baralga.server.web;

import com.remast.baralga.server.ActivityRepository;
import com.remast.baralga.server.ActivityService;
import com.remast.baralga.server.ProjectRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Collectors;

@Transactional
@Controller
@RequiredArgsConstructor
public class WebController {

    private final @NonNull ActivityRepository activityRepository;

    private final @NonNull ActivityService activityService;

    private final @NonNull ProjectRepository projectRepository;

    @GetMapping("/projects")
    public String showProjects(Model model) {
        model.addAttribute("projects", projectRepository.findByOrderByTitle());
        return "projects";
    }

    @GetMapping("/")
    public String showHome(Model model, HttpServletRequest request, Principal principal) {
        var activities = request.isUserInRole("ROLE_ADMIN") ? activityService.readAll(null, null) : activityService.read(null, null, principal);
        model.addAttribute("activities", activities.getFirst());
        model.addAttribute("projects", activities.getSecond().stream().collect(Collectors.toMap(p -> p.getId(), p -> p)));
        return "index";
    }
}
