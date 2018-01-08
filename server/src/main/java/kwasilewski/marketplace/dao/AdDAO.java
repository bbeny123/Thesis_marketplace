package kwasilewski.marketplace.dao;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dto.AdData;
import kwasilewski.marketplace.dto.requests.AdSearchData;
import kwasilewski.marketplace.dto.requests.UserAdsData;
import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import kwasilewski.marketplace.util.DateTimeUtil;
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
        if (ad.getId() == null || find(ctx, ad.getId()) == null) throw new MKTException(MKTError.NOT_AUTHORIZED);
        this.em.merge(ad);
    }

    @Transactional
    public void changeStatus(UserContext ctx, Long id) throws DataAccessException, MKTException {
        AdData ad = id != null ? find(ctx, id) : null;
        if (ad == null) throw new MKTException(MKTError.AD_NOT_EXISTS);
        ad.setActive(!ad.isActive());
        this.em.merge(ad);
    }

    @Transactional
    public void remove(UserContext ctx, Long id) throws DataAccessException, MKTException {
        AdData ad = id != null ? get(ctx, id) : null;
        if (ad == null) throw new MKTException(MKTError.NOT_AUTHORIZED);
        this.em.remove(ad);
    }

    public List<AdData> getAll(UserContext ctx) throws DataAccessException {
        if (!ctx.isAdmin()) return null;
        TypedQuery<AdData> query = this.em.createQuery("SELECT ad FROM AdData ad", AdData.class);
        return query.getResultList();
    }

    public AdData get(UserContext ctx, Long id) throws DataAccessException {
        if (!ctx.isAdmin()) return null;
        TypedQuery<AdData> query = this.em.createQuery("SELECT ad FROM AdData ad WHERE ad.id = :id", AdData.class);
        query.setParameter("id", id);
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AdData findWithNoPhotos(Long id) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad WHERE ad.id = :id";
        queryStr += getActiveQuery(true);
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("id", id);
        query.setParameter("date", DateTimeUtil.getMinAdActiveDate());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AdData find(Long id) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad JOIN PhotoData ad.photos WHERE ad.id = :id";
        queryStr += getActiveQuery(true);
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("id", id);
        query.setParameter("date", DateTimeUtil.getMinAdActiveDate());
        try {
            return query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
    }

    public AdData find(UserContext ctx, Long id) throws DataAccessException {
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

    public List<AdData> find(AdSearchData criteria) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad WHERE ad.active = TRUE AND ad.date >= :date";
        queryStr += criteria.getTitle() != null ? " AND upper(ad.title) LIKE :title" : "";
        queryStr += criteria.getPrvId() != null ? " AND ad.prvId = :prvId" : "";
        queryStr += criteria.getCatId() != null ? " AND (ad.catId = :catId OR ad.category.catId = :catId)" : "";
        queryStr += criteria.getPriceMin() != null ? " AND ad.price >= :priceMin" : "";
        queryStr += criteria.getPriceMax() != null ? " AND ad.price <= :priceMax" : "";
        queryStr += " ORDER BY ad." + AdData.getSortingMethod(criteria.getSortingMethod());
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("date", DateTimeUtil.getMinAdActiveDate());
        if (criteria.getTitle() != null) query.setParameter("title", "%" + criteria.getTitle().toUpperCase() + "%");
        if (criteria.getPrvId() != null) query.setParameter("prvId", criteria.getPrvId());
        if (criteria.getCatId() != null) query.setParameter("catId", criteria.getCatId());
        if (criteria.getPriceMin() != null) query.setParameter("priceMin", criteria.getPriceMin());
        if (criteria.getPriceMax() != null) query.setParameter("priceMax", criteria.getPriceMax());
        query.setFirstResult(criteria.getOffset());
        query.setMaxResults(criteria.getMaxResults());
        List<AdData> ads = query.getResultList();
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ad.getId())));
        return ads;
    }

    public List<AdData> find(UserContext ctx, UserAdsData criteria) throws DataAccessException {
        String queryStr = "SELECT ad FROM AdData ad WHERE ad.usrId = :usrId";
        queryStr += getActiveQuery(criteria.isActive()) + " ORDER BY ad.date DESC";
        TypedQuery<AdData> query = this.em.createQuery(queryStr, AdData.class);
        query.setParameter("usrId", ctx.getUserId());
        query.setParameter("date", DateTimeUtil.getMinAdActiveDate());
        query.setFirstResult(criteria.getOffset());
        query.setMaxResults(criteria.getMaxResults());
        List<AdData> ads = query.getResultList();
        ads.forEach(ad -> ad.setMiniature(photoDAO.findMiniature(ctx, ad.getId())));
        return ads;
    }

    private String getActiveQuery(boolean active) {
        return active ? " AND ad.active = TRUE AND ad.date >= :date" : " AND (ad.active = FALSE OR ad.date < :date)";
    }

}
