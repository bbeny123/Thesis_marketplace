package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.ProvinceDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProvinceService {

    private final ProvinceDAO provinceDAO;

    @Autowired
    public ProvinceService(ProvinceDAO provinceDAO) {
        this.provinceDAO = provinceDAO;
    }

}
