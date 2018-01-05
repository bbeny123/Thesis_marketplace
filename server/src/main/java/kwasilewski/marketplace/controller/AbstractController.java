package kwasilewski.marketplace.controller;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class AbstractController {

    public static final String ISO_DATE_FORMAT_LONG = "yyyy-MM-dd HH:mm:ss";
    final Logger logger;

    public AbstractController() {
        this.logger = LogManager.getLogger(this.getClass());
    }

    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT_LONG);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

}
