package kwasilewski.marketplace.controller;


import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.CurrentUserData;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;

public abstract class AbstractWebController extends AbstractController {

    final String url;
    final String success;
    private final String error;

    public AbstractWebController() {
        super();
        this.url = "";
        this.success = "redirect:/" + url + "?success";
        this.error = "redirect:/" + url + "?error";
    }

    public AbstractWebController(String url) {
        super();
        this.url = url;
        this.success = "redirect:/" + url + "?success";
        this.error = "redirect:/" + url + "?error";
    }

    UserContext getUserContext() {
        CurrentUserData currentUser = new CurrentUserControllerAdvice().getCurrentUser(SecurityContextHolder.getContext().getAuthentication());
        return new UserContext(currentUser);
    }

    @ExceptionHandler(Exception.class)
    public String handleException(Exception ex) {
        logger.warn(ex.getMessage());
        return error;
    }

}
