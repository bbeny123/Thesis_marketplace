package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.configuration.context.annotation.ServiceContext;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.responses.login.LoginRequest;
import kwasilewski.marketplace.responses.login.LoginResponse;
import kwasilewski.marketplace.responses.login.TokenResponse;
import kwasilewski.marketplace.responses.user.UserRequest;
import kwasilewski.marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/rest")
public class RESTController extends AbstractRestController {

    @Autowired
    private final UserService userService;


    public RESTController(UserService userService) {
        super();
        this.userService = userService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody UserRequest request) throws Exception {
        userService.createUser(request.getUser());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {
        UserData user = userService.loginUser(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(new LoginResponse(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TokenResponse> checkToken(@ServiceContext UserContext ctx) {
        UserData user = userService.findUser(ctx.getUserId());
        return new ResponseEntity<>(new TokenResponse(user.getAvatarDate()), HttpStatus.OK);
    }


}
