package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.CategoryDAO;
import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.SubcategoryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryDAO categoryDAO;

    @Autowired
    public CategoryService(CategoryDAO categoryDAO) {
        this.categoryDAO = categoryDAO;
    }

    public List<CategoryData> getAllCategories() throws DataAccessException {
        return categoryDAO.getAll();
    }

    public List<SubcategoryData> getAllSubcategories() throws DataAccessException {
        return categoryDAO.getAllSubcategories();
    }

    public Long getAdNumber(Long id) throws DataAccessException {
        return categoryDAO.getAdsNumber(id);
    }

}
