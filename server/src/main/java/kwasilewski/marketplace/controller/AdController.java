package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.service.AdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class AdController extends AbstractWebController {

    private final AdService adService;

    @Autowired
    public AdController(AdService adService) {
        super("ads");
        this.adService = adService;
    }

    @RequestMapping(value = "/ads", method = RequestMethod.GET)
    public String getUsersList(Model model) {
        model.addAttribute("ads", adService.getAllAds(getUserContext()));
        return url;
    }

    @RequestMapping(value = "ads/changeStatus", method = RequestMethod.PATCH)
    public String promoteUser(Long id) throws Exception {
        adService.changeStatus(getUserContext(), id);
        return success;
    }

    @RequestMapping(value = "ads/delete", method = RequestMethod.DELETE)
    public String deleteUser(Long id) throws Exception {
        adService.removeAd(getUserContext(), id);
        return success;
    }

}
