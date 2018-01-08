package kwasilewski.marketplace.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class DateTimeUtil {

    private final static int expDays = 30;

    public static Date getMinAdActiveDate() {
        return Date.from(LocalDateTime.now().minusDays(expDays).atZone(ZoneId.systemDefault()).toInstant());
    }
}
