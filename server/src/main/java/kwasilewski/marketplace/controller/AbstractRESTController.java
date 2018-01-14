package kwasilewski.marketplace.controller;

import kwasilewski.marketplace.errors.MKTError;
import kwasilewski.marketplace.errors.MKTException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;

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
        logger.warn(ex.getMessage());
        int ec = ex.getErrorCode();
        if (Arrays.asList(MKTError.USER_INVALID_CREDENTIALS.getCode(), MKTError.NOT_AUTHORIZED.getCode(), MKTError.WRONG_TOKEN.getCode()).contains(ec)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } else if (Arrays.asList(MKTError.USER_ALREADY_EXISTS.getCode(), MKTError.AD_ALREADY_EXISTS.getCode(), MKTError.FAVOURITE_ALREADY_EXISTS.getCode()).contains(ec)) {
            return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).build();
        } else if (Arrays.asList(MKTError.USER_NOT_EXISTS.getCode(), MKTError.AD_NOT_EXISTS.getCode(), MKTError.FAVOURITE_NOT_EXISTS.getCode()).contains(ec)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

}
