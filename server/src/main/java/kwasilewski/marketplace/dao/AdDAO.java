package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.responses.AdSearchRequest;
import kwasilewski.marketplace.responses.FavouriteRequest;
import kwasilewski.marketplace.responses.UserAdsRequest;
import kwasilewski.marketplace.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;

@Repository
public class AdDAO {

    @PersistenceContext
    private EntityManager em;

    private final PhotoDAO photoDAO;

    @Autowired
    public AdDAO(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    @Transactional
    public void create(AdData ad) throws DataAccessException, MKTException {
        if (ad.getId() != null) throw new MKTException(MKTError.AD_ALREADY_EXISTS);
        this.em.persist(ad);
    }

    @Transactional
    public void modify(UserContext ctx, AdData ad) throws DataAccessException, MKTException {
        if (ad.getId() == null) throw new MKTException(MKTError.AD_NOT_EXISTS);
        else if (findNonActive(ctx, ad.getId()) == null) throw new MKTException(MKTError.NOT_AUTHORIZED);
        this.em.merge(ad);
    }

    @Transactional
    public void remove(UserContext ctx, Long id) throws DataAccessException, MKTException {
        if (id == null) throw new MKTException(MKTError.AD_NOT_EXISTS);
        AdData ad = findNonActive(ctx, id);
        if (ad == null) throw new MKTException(MKTError.NOT_AUTHORIZED);
        this.em.remove(ad);
    }


    public List<AdData> find(AdSearchRequest search) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad WHERE ad.active = TRUE AND ad.date >= :date";
        queryStr += search.getTitle() != null ? " AND upper(ad.title) LIKE :title" : "";
        queryStr += search.getPrvId() != null ? " AND ad.prvId = :prvId" : "";
        queryStr += search.getCatId() != null ? " AND (ad.catId = :catId OR ad.category.catId = :catId)" : "";
        queryStr += search.getPriceMin() != null ? " AND ad.price >= :priceMin" : "";
        queryStr += search.getPriceMax() != null ? " AND ad.price <= :priceMax" : "";
        queryStr += " ORDER BY ad." + AdData.getSortingMethod(search.getSortingMethod());
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("date", JwtTokenUtil.minimumTokenDate());
        if (search.getTitle() != null) query.setParameter("title", "%" + search.getTitle().toUpperCase() + "%");
        if (search.getPrvId() != null) query.setParameter("prvId", search.getPrvId());
        if (search.getCatId() != null) query.setParameter("catId", search.getCatId());
        if (search.getPriceMin() != null) query.setParameter("priceMin", search.getPriceMin());
        if (search.getPriceMax() != null) query.setParameter("priceMax", search.getPriceMax());
        query.setFirstResult(search.getOffset());
        query.setMaxResults(search.getMaxResults());
        List<AdData> ads = query.getResultList();
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ad.getId())));
        return ads;
    }

    public List<AdData> findUser(UserContext ctx, UserAdsRequest search) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad WHERE ad.usrId = :usrId";
        queryStr += getActiveQuery(search.isActive()) + " ORDER BY ad.date DESC";
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("usrId", ctx.getUserId());
        query.setParameter("date", JwtTokenUtil.minimumTokenDate());
        query.setFirstResult(search.getOffset());
        query.setMaxResults(search.getMaxResults());
        List<AdData> ads = query.getResultList();
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ad.getId())));
        return ads;
    }

    private String getActiveQuery(boolean active) {
        return active ? " AND ad.active = TRUE AND ad.date >= :date" : " AND (ad.active = FALSE OR ad.date < :date)";
    }

    public List<AdData> findNonActive(UserContext ctx, FavouriteRequest search) throws DataAccessException {
        if (!ctx.isAdmin()) return null;
        String queryStr = "SELECT ad FROM AdData ad WHERE ad.active = FALSE OR ad.date < :date ORDER BY ad.date DESC";
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("date", JwtTokenUtil.minimumTokenDate());
        query.setFirstResult(search.getOffset());
        query.setMaxResults(search.getMaxResults());
        List<AdData> ads = query.getResultList();
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ad.getId())));
        return ads;
    }

    public AdData find(Long id) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad JOIN PhotoData ad.photos WHERE ad.id = :id";
        queryStr += getActiveQuery(true);
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("id", id);
        query.setParameter("date", JwtTokenUtil.minimumTokenDate());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AdData findNonActive(UserContext ctx, Long id) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad JOIN PhotoData ad.photos WHERE ad.id = :id";
        queryStr += !ctx.isAdmin() ? " AND ad.usrId = :usrId" : "";
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("id", id);
        if (!ctx.isAdmin()) query.setParameter("usrId", ctx.getUserId());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

}
