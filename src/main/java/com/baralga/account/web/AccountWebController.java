package com.baralga.account.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Transactional
@Controller
@RequiredArgsConstructor
public class AccountWebController {

    @Transactional(readOnly = true)
    @GetMapping(value = "signup", headers = "Accept=text/html", produces = "text/html")
    public String showSignup(Model model, HttpServletRequest request, HttpServletResponse response) {
        model.addAttribute("signUp", new SignUpModel());
        return "account/signup"; // NOSONAR
    }

    @PostMapping(value = "/signup", headers = "Accept=text/vnd.turbo-stream.html", produces = "text/vnd.turbo-stream.html")
    public ModelAndView createActivityStream(@Valid @ModelAttribute("signUp") SignUpModel signUpModel, BindingResult bindingResult, Model model, HttpServletRequest request) {
        return doSignUp(true, signUpModel, bindingResult, model, request);
    }

    @PostMapping(value = "/signup", headers = "Accept=text/html", produces = "text/html")
    public ModelAndView createActivity(@Valid @ModelAttribute("signUp") SignUpModel signUpModel, BindingResult bindingResult, Model model, HttpServletRequest request) {
        return doSignUp(false, signUpModel, bindingResult, model, request);
    }

    private ModelAndView doSignUp(boolean isTurboStreamRequest, SignUpModel signUpModel, BindingResult bindingResult, Model model, HttpServletRequest request) {
        signUpModel.validatePassword().ifPresent(bindingResult::addError);
        if (bindingResult.hasErrors()) {
            if (isTurboStreamRequest) {
                model.addAttribute("template", "account/fragments/signupForm.html");
                model.addAttribute("turboAction", "replace");
                model.addAttribute("turboTarget", "b__signup");

                return new ModelAndView("turbo/turbo.stream.html", HttpStatus.UNPROCESSABLE_ENTITY); // NOSONAR
            }

            return new ModelAndView("account/signup");
        }
        //activityService.create(activityModel.map(), user);

        return new ModelAndView("redirect:/"); // NOSONAR
    }

}
