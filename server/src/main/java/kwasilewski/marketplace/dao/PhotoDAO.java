package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.PhotoData;
import kwasilewski.marketplace.util.DateTimeUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

@Repository
public class PhotoDAO {

    @PersistenceContext
    private EntityManager em;

    public PhotoData findMiniature(Long adId) {
        String queryStr = "SELECT pht FROM PhotoData pht WHERE pht.adId = :adId AND pht.miniature = TRUE AND pht.ad.active = TRUE and pht.ad.date >= :date";
        TypedQuery<PhotoData> query = this.em.createQuery(queryStr, PhotoData.class);
        query.setParameter("adId", adId);
        query.setParameter("date", DateTimeUtil.getMinAdActiveDate());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public PhotoData findMiniature(UserContext ctx, Long adId) throws DataAccessException {
        String queryStr = "SELECT pht FROM PhotoData pht WHERE pht.adId = :adId AND pht.miniature = TRUE";
        queryStr += !ctx.isAdmin() ? " AND pht.ad.usrId = :usrId" : "";
        TypedQuery<PhotoData> query = this.em.createQuery(queryStr, PhotoData.class);
        query.setParameter("adId", adId);
        if (!ctx.isAdmin()) query.setParameter("usrId", ctx.getUserId());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
