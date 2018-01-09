package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.dto.ProvinceData;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class ProvinceDAO {

    @PersistenceContext
    private EntityManager em;

    public List<ProvinceData> getAll() throws DataAccessException {
        TypedQuery<ProvinceData> query = this.em.createQuery("SELECT prv FROM ProvinceData prv", ProvinceData.class);
        return query.getResultList();
    }

}