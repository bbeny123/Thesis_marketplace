package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.dto.SubcategoryData;
import kwasilewski.marketplace.service.HintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class SubcategoryController extends AbstractWebController {

    private final HintService hintService;

    @Autowired
    public SubcategoryController(HintService hintService) {
        super("subcategories");
        this.hintService = hintService;
    }

    @RequestMapping(value = "/subcategories", method = RequestMethod.GET)
    public String getSubcategoryList(Model model) {
        List<SubcategoryData> subcategories = hintService.getAllSubcategories();
        subcategories.forEach(sct -> sct.setAdsNumber(hintService.getAdNumber(sct.getId())));
        model.addAttribute("subcategories", subcategories);
        return url;
    }

}
