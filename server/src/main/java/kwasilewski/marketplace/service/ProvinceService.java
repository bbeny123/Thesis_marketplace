package kwasilewski.marketplace.service;

import kwasilewski.marketplace.dao.ProvinceDAO;
import kwasilewski.marketplace.dto.ProvinceData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProvinceService {

    private final ProvinceDAO provinceDAO;

    @Autowired
    public ProvinceService(ProvinceDAO provinceDAO) {
        this.provinceDAO = provinceDAO;
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

}
