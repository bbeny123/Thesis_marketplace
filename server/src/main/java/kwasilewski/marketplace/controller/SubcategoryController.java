package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.dto.SubcategoryData;
import kwasilewski.marketplace.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class SubcategoryController extends AbstractWebController {

    private final CategoryService categoryService;

    @Autowired
    public SubcategoryController(CategoryService categoryService) {
        super("subcategories");
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "/subcategories", method = RequestMethod.GET)
    public String getSubcategoryList(Model model) {
        List<SubcategoryData> subcategories = categoryService.getAllSubcategories();
        subcategories.forEach(sct -> sct.setAdsNumber(categoryService.getAdNumber(sct.getId())));
        model.addAttribute("subcategories", subcategories);
        return url;
    }

}
