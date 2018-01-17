package kwasilewski.marketplace.service;


import kwasilewski.marketplace.dto.CurrentUserData;
import kwasilewski.marketplace.dto.UserData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserService implements UserDetailsService {

    private final UserService userService;

    @Autowired
    public CurrentUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public CurrentUserData loadUserByUsername(String email) throws UsernameNotFoundException {
        UserData user = userService.getUser(email);
        if (user == null || !user.isAdmin()) {
            throw new UsernameNotFoundException(String.format("User with email=%s was not found", email));
        }
        return new CurrentUserData(user);
    }

}
