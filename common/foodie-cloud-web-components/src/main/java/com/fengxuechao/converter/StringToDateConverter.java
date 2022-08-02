package com.fengxuechao.converter;

import org.springframework.core.convert.converter.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

/**
 * @author fengxc
 * @date 2022/8/1
 */
public class StringToDateConverter implements Converter<String, Date> {

    @Override
    public Date convert(String source) {
        String value = source.trim();
        if ("".equals(value)) {
            return null;
        }
        if (source.matches("^\\d{4}-\\d{1,2}$")) {
            return parseDate(source, "yyyy-MM");
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
            return parseDate(source, "yyyy-MM-dd");
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}$")) {
            return parseDateTime(source, "yyyy-MM-dd HH:mm");
        } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} {1}\\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
            return parseDateTime(source, "yyyy-MM-dd HH:mm:ss");
        } else {
            throw new IllegalArgumentException("Invalid boolean value '" + source + "'");
        }
    }

    /**
     * 格式化日期
     *
     * @param dateStr String 字符型日期
     * @param format  String 格式
     * @return Date 日期
     */
    public Date parseDate(String dateStr, String format) {
        LocalDateTime localDateTime = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern(format, Locale.CHINA)).atStartOfDay();
        return Date.from(localDateTime.toInstant(ZoneOffset.ofHours(8)));
    }

    public Date parseDateTime(String dateStr, String format) {
        LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(format, Locale.CHINA));
        return Date.from(localDateTime.toInstant(ZoneOffset.ofHours(8)));
    }
}
