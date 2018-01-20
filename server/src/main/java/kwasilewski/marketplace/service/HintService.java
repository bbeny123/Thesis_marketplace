package kwasilewski.marketplace.service;

import kwasilewski.marketplace.dao.CategoryDAO;
import kwasilewski.marketplace.dao.ProvinceDAO;
import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.SubcategoryData;
import kwasilewski.marketplace.dtoext.ComboHintDataExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HintService {

    private final CategoryDAO categoryDAO;
    private final ProvinceDAO provinceDAO;

    @Autowired
    public HintService(CategoryDAO categoryDAO, ProvinceDAO provinceDAO) {
        this.categoryDAO = categoryDAO;
        this.provinceDAO = provinceDAO;
    }

    public ComboHintDataExt getAllHints() throws DataAccessException {
        return new ComboHintDataExt(provinceDAO.getAll(), categoryDAO.getAll());
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
