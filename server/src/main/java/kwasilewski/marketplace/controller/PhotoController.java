package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.service.PhotoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class PhotoController extends AbstractWebController {

    private final PhotoService photoService;

    @Autowired
    public PhotoController(PhotoService photoService) {
        super("photos");
        this.photoService = photoService;
    }

    @RequestMapping(value = "/photos", method = RequestMethod.GET)
    public String getCategoryList(Model model) {
        model.addAttribute("photos", photoService.getAllPhotos(getUserContext()));
        return url;
    }

}
