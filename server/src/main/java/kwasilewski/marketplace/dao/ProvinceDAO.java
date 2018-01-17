package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.dto.ProvinceData;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    public ProvinceData get(Long id) throws DataAccessException {
        TypedQuery<ProvinceData> query = this.em.createQuery("SELECT prv FROM ProvinceData prv WHERE prv.id = :id", ProvinceData.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public Long getUserNumber(Long id) throws DataAccessException {
        TypedQuery<Long> query = this.em.createQuery("SELECT COUNT(user) FROM UserData user WHERE user.prvId = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    public Long getAdsNumber(Long id) throws DataAccessException {
        TypedQuery<Long> query = this.em.createQuery("SELECT COUNT(ad) FROM AdData ad WHERE ad.prvId = :id", Long.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

}