package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.service.HintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class CategoryController extends AbstractWebController {

    private final HintService hintService;

    @Autowired
    public CategoryController(HintService hintService) {
        super("categories");
        this.hintService = hintService;
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public String getCategoryList(Model model) {
        List<CategoryData> categories = hintService.getAllCategories();
        categories.forEach(cat -> cat.setAdsNumber(hintService.getAdNumber(cat.getId())));
        model.addAttribute("categories", categories);
        return url;
    }

}
