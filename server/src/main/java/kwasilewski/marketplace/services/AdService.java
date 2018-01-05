package kwasilewski.marketplace.services;

import kwasilewski.marketplace.dao.AdDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdService {

    private final AdDAO adDAO;

    @Autowired
    public AdService(AdDAO adDAO) {
        this.adDAO = adDAO;
    }

}
