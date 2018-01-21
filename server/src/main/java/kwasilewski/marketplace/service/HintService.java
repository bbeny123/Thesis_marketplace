package kwasilewski.marketplace.service;

import kwasilewski.marketplace.dao.CategoryDAO;
import kwasilewski.marketplace.dao.ProvinceDAO;
import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.ProvinceData;
import kwasilewski.marketplace.dto.SubcategoryData;
import kwasilewski.marketplace.dtoext.hint.ComboHintDataExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HintService {

    private final ProvinceDAO provinceDAO;
    private final CategoryDAO categoryDAO;

    @Autowired
    public HintService(ProvinceDAO provinceDAO, CategoryDAO categoryDAO) {
        this.provinceDAO = provinceDAO;
        this.categoryDAO = categoryDAO;
    }

    public List<ProvinceData> getAllProvinces() throws DataAccessException {
        return provinceDAO.getAll();
    }

    public ProvinceData getProvince(Long id) throws DataAccessException {
        return provinceDAO.get(id);
    }

    public Long getUserNumber(Long id) throws DataAccessException {
        return provinceDAO.getUserNumber(id);
    }

    public Long getAdsNumber(Long id) throws DataAccessException {
        return provinceDAO.getAdsNumber(id);
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
