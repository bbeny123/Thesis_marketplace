package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.configuration.context.annotation.ServiceContext;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.dto.requests.*;
import kwasilewski.marketplace.dto.responses.AdResponse;
import kwasilewski.marketplace.dto.responses.LoginResponse;
import kwasilewski.marketplace.services.AdService;
import kwasilewski.marketplace.services.FavouriteService;
import kwasilewski.marketplace.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest")
public class RESTController extends AbstractRestController {

    private final UserService userService;
    private final AdService adService;
    private final FavouriteService favouriteService;

    @Autowired
    public RESTController(UserService userService, AdService adService, FavouriteService favouriteService) {
        super();
        this.userService = userService;
        this.adService = adService;
        this.favouriteService = favouriteService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createUser(@RequestBody RegisterRequest request) throws Exception {
        userService.createUser(request.getUserData());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) throws Exception {
        UserData user = userService.loginUser(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(new LoginResponse(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<LoginResponse> checkToken(@ServiceContext UserContext ctx) {
        return new ResponseEntity<>(new LoginResponse(ctx.getUser()), HttpStatus.OK);
    }

    @RequestMapping(value = "/user/ads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUserAds(@ServiceContext UserContext ctx, UserAdsRequest request) {
        List<AdResponse> ads = adService.findAds(ctx, request).stream().map(AdResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/ads/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdResponse> findUserAds(@ServiceContext UserContext ctx, @PathVariable Long id) {
        return new ResponseEntity<>(new AdResponse(adService.findAd(ctx, id)), HttpStatus.OK);
    }

    @RequestMapping(value = "/user/favourites", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findFavourites(@ServiceContext UserContext ctx, UserAdsRequest request) {
        List<AdResponse> ads = favouriteService.findFavourites(ctx, request).stream().map(AdResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    @RequestMapping(value = "/ads", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAd(@ServiceContext UserContext ctx, AdRequest request) throws Exception {
        adService.createAd(request.getAdData(ctx));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/ads/{id}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyAd(@ServiceContext UserContext ctx, @PathVariable Long id, AdRequest request) throws Exception {
        adService.modifyAd(ctx, request.getAdData(ctx));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/ads/{id}/status", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeAdStatus(@ServiceContext UserContext ctx, @PathVariable Long id) throws Exception {
        adService.changeStatus(ctx, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/ads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAds(AdSearchRequest request) {
        List<AdResponse> ads = adService.findAds(request).stream().map(AdResponse::new).collect(Collectors.toList());
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    @RequestMapping(value = "/ads/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdResponse> findAd(@PathVariable Long id) {
        return new ResponseEntity<>(new AdResponse(adService.findAd(id)), HttpStatus.OK);
    }

    @RequestMapping(value = "/ads/{id}/favourite", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addFavourite(@ServiceContext UserContext ctx, @PathVariable Long id) throws Exception {
        favouriteService.createFavourite(ctx, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/ads/{id}/favourite", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeFavourite(@ServiceContext UserContext ctx, @PathVariable Long id) throws Exception {
        favouriteService.removeFavourite(ctx, id);
        return ResponseEntity.ok().build();
    }
}
