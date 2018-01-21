package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PhotoController extends AbstractWebController {

    private final AdService adService;

    @Autowired
    public PhotoController(AdService adService) {
        super("photos");
        this.adService = adService;
    }

    @RequestMapping(value = "/photos", method = RequestMethod.GET)
    public String getCategoryList(Model model) {
        model.addAttribute("photos", adService.getAllPhotos(getUserContext()));
        return url;
    }

}
