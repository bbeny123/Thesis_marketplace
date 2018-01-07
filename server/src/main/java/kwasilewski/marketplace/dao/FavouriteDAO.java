package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.FavouriteData;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.responses.FavouriteRequest;
import kwasilewski.marketplace.util.JwtTokenUtil;
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
    public void create(FavouriteData favouriteData) throws MKTException {
        if (favouriteData.getId() != null) throw new MKTException(MKTError.FAVOURITE_ALREADY_EXISTS);
        this.em.persist(favouriteData);
    }

    @Transactional
    public void remove(UserContext ctx, Long id) throws MKTException {
        if (id == null) throw new MKTException(MKTError.FAVOURITE_NOT_EXISTS);
        FavouriteData favouriteData = find(ctx, id);
        if (favouriteData == null) throw new MKTException(MKTError.NOT_AUTHORIZED);
        this.em.remove(favouriteData);
    }

    public List<AdData> find(UserContext ctx, FavouriteRequest search) throws DataAccessException {
        String queryStr = "SELECT fav.ad FROM FavouriteData fav WHERE fav.usrId = :usrId AND fav.ad.active = TRUE AND fav.ad.date >= :date ORDER BY fav.ad.date DESC";
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("usrId", ctx.getUserId());
        query.setParameter("date", JwtTokenUtil.minimumTokenDate());
        query.setFirstResult(search.getOffset());
        query.setMaxResults(search.getMaxResults());
        return query.getResultList();
    }

    public FavouriteData find(UserContext ctx, Long id) throws DataAccessException {
        String queryStr = "SELECT fav FROM FavouriteData fav WHERE fav.id = :id AND fav.usrId = :usrId";
        TypedQuery<FavouriteData> query = this.em.createQuery(queryStr, FavouriteData.class);
        query.setParameter("id", id);
        query.setParameter("usrId", ctx.getUserId());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
