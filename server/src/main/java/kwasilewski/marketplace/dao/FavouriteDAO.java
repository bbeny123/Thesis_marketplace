package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.FavouriteData;
import kwasilewski.marketplace.dtoext.ad.ListSearchDataExt;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.util.DateTimeUtil;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class FavouriteDAO {

    @PersistenceContext
    private EntityManager em;

    @Transactional
    public void create(UserContext ctx, AdData ad) throws DataAccessException, MKTException {
        if (alreadyFavourite(ctx.getUserId(), ad.getId()))
            throw new MKTException(MKTError.FAVOURITE_ALREADY_EXISTS);
        if (ad.getUsrId().equals(ctx.getUserId()))
            throw new MKTException(MKTError.FAVOURITE_OWN_AD);
        FavouriteData fav = new FavouriteData();
        fav.setUsrId(ctx.getUserId());
        fav.setAdId(ad.getId());
        this.em.persist(fav);
    }

    @Transactional
    public void remove(UserContext ctx, Long adId) throws DataAccessException, MKTException {
        FavouriteData fav = adId != null ? find(ctx, adId) : null;
        if (fav == null) throw new MKTException(MKTError.FAVOURITE_NOT_EXISTS);
        this.em.remove(fav);
    }

    public List<AdData> find(UserContext ctx, ListSearchDataExt criteria) throws DataAccessException {
        String queryStr = "SELECT fav.ad FROM FavouriteData fav WHERE fav.usrId = :usrId AND fav.ad.active = TRUE AND fav.ad.date >= :date ORDER BY fav.ad.date DESC";
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("usrId", ctx.getUserId());
        query.setParameter("date", DateTimeUtil.getMinAdActiveDate());
        query.setFirstResult(criteria.getOffset());
        query.setMaxResults(criteria.getMaxResults());
        return query.getResultList();
    }

    public FavouriteData find(UserContext ctx, Long adId) throws DataAccessException {
        String queryStr = "SELECT fav FROM FavouriteData fav WHERE fav.adId = :adId AND fav.usrId = :usrId";
        TypedQuery<FavouriteData> query = this.em.createQuery(queryStr, FavouriteData.class);
        query.setParameter("adId", adId);
        query.setParameter("usrId", ctx.getUserId());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public boolean alreadyFavourite(Long usrId, Long adId) {
        String queryStr = "SELECT COUNT(fav) FROM FavouriteData fav WHERE fav.usrId = :usrId AND fav.adId = :adId";
        TypedQuery<Long> query = this.em.createQuery(queryStr, Long.class);
        query.setParameter("usrId", usrId);
        query.setParameter("adId", adId);
        return query.getSingleResult() == 1;
    }

}
