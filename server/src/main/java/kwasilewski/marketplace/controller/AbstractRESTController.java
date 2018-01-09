package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class AbstractRESTController extends AbstractController {

    public AbstractRESTController() {
        super();
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<?> handleException(Exception ex) {
        logger.warn(ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    @ExceptionHandler(MKTException.class)
    @ResponseBody
    public ResponseEntity<?> handleRBCException(MKTException ex) {
        int ec = ex.getErrorCode();
        logger.warn(ex.getMessage());
        if (ec == MKTError.USER_INVALID_LOGIN_OR_PASSWORD.getCode() || ec == MKTError.NOT_AUTHORIZED.getCode()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (ec == MKTError.USER_ALREADY_EXISTS.getCode()) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
