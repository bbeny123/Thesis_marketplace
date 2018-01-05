package kwasilewski.marketplace.controller;


import kwasilewski.marketplace.dto.CurrentUserData;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserControllerAdvice extends AbstractWebController {

    @ModelAttribute("currentUser")
    public CurrentUserData getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : (CurrentUserData) authentication.getPrincipal();
    }

}
