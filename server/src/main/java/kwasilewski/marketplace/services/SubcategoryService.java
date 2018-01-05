package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.SubcategoryDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubcategoryService {

    private final SubcategoryDAO subcategoryDAO;

    @Autowired
    public SubcategoryService(SubcategoryDAO subcategoryDAO) {
        this.subcategoryDAO = subcategoryDAO;
    }

}
