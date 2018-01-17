package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserController extends AbstractWebController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        super("users");
        this.userService = userService;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public String getUsersList(Model model) throws Exception {
        model.addAttribute("users", userService.getAllUsers(getUserContext()));
        return url;
    }

    @RequestMapping(value = "users/promote", method = RequestMethod.PATCH)
    public String promoteUser(Long id) throws Exception {
        userService.promoteUser(getUserContext(), id);
        return success;
    }


    @RequestMapping(value = "users/delete", method = RequestMethod.DELETE)
    public String deleteUser(Long id) throws Exception {
        userService.removeUser(getUserContext(), id);
        return success;
    }

}
