package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.dto.ProvinceData;
import kwasilewski.marketplace.services.ProvinceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class ProvinceController extends AbstractWebController {

    private final ProvinceService provinceService;

    @Autowired
    public ProvinceController(ProvinceService provinceService) {
        super("provinces");
        this.provinceService = provinceService;
    }

    @RequestMapping(value = "/provinces", method = RequestMethod.GET)
    public String getProvinceList(Model model) {
        List<ProvinceData> provinces = provinceService.getAllProvinces();
        provinces.forEach(prv -> {
            prv.setUsersNumber(provinceService.getUserNumber(prv.getId()));
            prv.setAdsNumber(provinceService.getAdsNumber(prv.getId()));
        });
        model.addAttribute("provinces", provinces);
        return url;
    }

}
