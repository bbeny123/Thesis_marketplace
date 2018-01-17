package kwasilewski.marketplace.service;

import kwasilewski.marketplace.configuration.context.UserContext;
import kwasilewski.marketplace.dao.PhotoDAO;
import kwasilewski.marketplace.dto.PhotoData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PhotoService {

    private final PhotoDAO photoDAO;

    @Autowired
    public PhotoService(PhotoDAO photoDAO) {
        this.photoDAO = photoDAO;
    }

    public List<PhotoData> getAllPhotos(UserContext ctx) throws DataAccessException {
        return photoDAO.getAll(ctx);
    }

}
