package kwasilewski.marketplace.dao;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class PhotoDAO {

    @PersistenceContext
    private EntityManager em;

}
