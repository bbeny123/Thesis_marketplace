package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.UserData;
import kwasilewski.marketplace.dtoext.ad.*;
import kwasilewski.marketplace.dtoext.hint.HintDataExt;
import kwasilewski.marketplace.dtoext.user.LoginDataExt;
import kwasilewski.marketplace.dtoext.user.PasswordDataExt;
import kwasilewski.marketplace.dtoext.user.UserDataExt;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.security.context.UserContext;
import kwasilewski.marketplace.security.context.annotation.ServiceContext;
import kwasilewski.marketplace.service.AdService;
import kwasilewski.marketplace.service.HintService;
import kwasilewski.marketplace.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/rest")
public class RESTController extends AbstractRESTController {

    private final HintService hintService;
    private final UserService userService;
    private final AdService adService;

    @Autowired
    public RESTController(HintService hintService, UserService userService, AdService adService) {
        super();
        this.hintService = hintService;
        this.userService = userService;
        this.adService = adService;
    }

    @RequestMapping(value = "/provinces", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getProvinces() {
        List<HintDataExt> provinces = hintService.getAllProvinces().stream().map(HintDataExt::new).collect(Collectors.toList());
        return new ResponseEntity<>(provinces, HttpStatus.OK);
    }

    @RequestMapping(value = "/hints", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getComboHints() {
        return new ResponseEntity<>(hintService.getAllHints(), HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> register(@RequestBody UserDataExt request) throws Exception {
        userService.createUser(request.getUserData());
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDataExt> login(@RequestBody LoginDataExt request) throws Exception {
        UserData user = userService.loginUser(request.getEmail(), request.getPassword());
        return new ResponseEntity<>(new UserDataExt(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/token", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDataExt> checkToken(@ServiceContext UserContext ctx) throws Exception {
        if (ctx.getUser() == null) throw new MKTException(MKTError.WRONG_TOKEN);
        return new ResponseEntity<>(new UserDataExt(ctx.getUser()), HttpStatus.OK);
    }

    @RequestMapping(value = "/user", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<UserDataExt> modifyUser(@ServiceContext UserContext ctx, @RequestBody UserDataExt request) throws Exception {
        UserData user = userService.modifyUser(ctx, request.getUserData(ctx));
        user.setProvince(hintService.getProvince(user.getPrvId()));
        return new ResponseEntity<>(new UserDataExt(user), HttpStatus.OK);
    }

    @RequestMapping(value = "/user/password", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changePassword(@ServiceContext UserContext ctx, @RequestBody PasswordDataExt request) throws Exception {
        userService.changeUserPassword(ctx, ctx.getUserId(), request);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user/ads", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> createAd(@ServiceContext UserContext ctx, @RequestBody AdDataExt request) throws Exception {
        adService.createAd(request.getAdData(ctx));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user/ads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUserAds(@ServiceContext UserContext ctx, AdSearchDataExt request) {
        List<AdMinimalDataExt> ads = adService.findAds(ctx, request).stream().map(AdMinimalDataExt::new).collect(Collectors.toList());
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    @RequestMapping(value = "/user/ads/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdDetailsDataExt> getUserAd(@ServiceContext UserContext ctx, @PathVariable Long id) throws MKTException {
        AdData ad = adService.findAd(ctx, id);
        return new ResponseEntity<>(new AdDetailsDataExt(ad), HttpStatus.OK);
    }

    @RequestMapping(value = "/user/ads/{id}", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> modifyUserAd(@ServiceContext UserContext ctx, @PathVariable Long id, @RequestBody AdDataExt request) throws Exception {
        adService.modifyAd(ctx, request.getAdData(ctx, id));
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user/ads/{id}/status", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> changeUserAdStatus(@ServiceContext UserContext ctx, @PathVariable Long id) throws Exception {
        adService.changeStatus(ctx, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user/ads/{id}/refresh", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> refreshUserAd(@ServiceContext UserContext ctx, @PathVariable Long id) throws Exception {
        adService.refreshAd(ctx, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/user/favourites", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findUserFavourites(@ServiceContext UserContext ctx, ListSearchDataExt request) {
        List<AdMinimalDataExt> ads = adService.findFavourites(ctx, request).stream().map(AdMinimalDataExt::new).collect(Collectors.toList());
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    @RequestMapping(value = "/ads", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> findAds(AdSearchDataExt request) {
        List<AdMinimalDataExt> ads = adService.findAds(request).stream().map(AdMinimalDataExt::new).collect(Collectors.toList());
        return new ResponseEntity<>(ads, HttpStatus.OK);
    }

    @RequestMapping(value = "/ads/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AdDetailsDataExt> getAd(@PathVariable Long id) throws MKTException {
        AdData ad = adService.findAd(id);
        if (ad == null) throw new MKTException(MKTError.AD_NOT_EXISTS);
        return new ResponseEntity<>(new AdDetailsDataExt(ad), HttpStatus.OK);
    }

    @RequestMapping(value = "/ads/{id}/favourite", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addFavourite(@ServiceContext UserContext ctx, @PathVariable Long id) throws Exception {
        adService.createFavourite(ctx, id);
        return ResponseEntity.ok().build();
    }

    @RequestMapping(value = "/ads/{id}/favourite", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> removeFavourite(@ServiceContext UserContext ctx, @PathVariable Long id) throws Exception {
        adService.removeFavourite(ctx, id);
        return ResponseEntity.ok().build();
    }

}