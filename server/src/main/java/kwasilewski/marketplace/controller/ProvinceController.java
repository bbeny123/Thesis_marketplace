package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.dto.ProvinceData;
import kwasilewski.marketplace.service.HintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class ProvinceController extends AbstractWebController {

    private final HintService hintService;

    @Autowired
    public ProvinceController(HintService hintService) {
        super("provinces");
        this.hintService = hintService;
    }

    @RequestMapping(value = "/provinces", method = RequestMethod.GET)
    public String getProvinceList(Model model) {
        List<ProvinceData> provinces = hintService.getAllProvinces();
        provinces.forEach(prv -> {
            prv.setUsersNumber(hintService.getUserNumber(prv.getId()));
            prv.setAdsNumber(hintService.getAdsNumber(prv.getId()));
        });
        model.addAttribute("provinces", provinces);
        return url;
    }

}
