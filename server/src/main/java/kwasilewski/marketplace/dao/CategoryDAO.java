package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.dto.CategoryData;
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
        TypedQuery<CategoryData> query = this.em.createQuery("SELECT cat FROM CategoryData cat JOIN SubcategoryData cat.subcategories", CategoryData.class);
        return query.getResultList();
    }

}