package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.services.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class CategoryController extends AbstractWebController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        super("categories");
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "/categories", method = RequestMethod.GET)
    public String getCategoryList(Model model) {
        List<CategoryData> categories = categoryService.getAllCategories();
        categories.forEach(cat -> cat.setAdsNumber(categoryService.getAdNumber(cat.getId())));
        model.addAttribute("categories", categories);
        return url;
    }

}
