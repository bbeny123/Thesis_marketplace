package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.dto.CategoryData;
import kwasilewski.marketplace.dto.SubcategoryData;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class CategoryDAO {

    @PersistenceContext
    private EntityManager em;

    public List<CategoryData> getAll() throws DataAccessException {
        TypedQuery<CategoryData> query = this.em.createQuery("SELECT cat FROM CategoryData cat", CategoryData.class);
        return query.getResultList();
    }

    public List<SubcategoryData> getAllSubcategories() throws DataAccessException {
        TypedQuery<SubcategoryData> query = this.em.createQuery("SELECT sct FROM SubcategoryData sct", SubcategoryData.class);
        return query.getResultList();
    }

    public Long getAdsNumber(Long id) throws DataAccessException {
        TypedQuery<Long> query = this.em.createQuery("SELECT COUNT(ad) FROM AdData ad WHERE ad.catId = :id OR ad.category.catId = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

}