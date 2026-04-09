package bachtx.myapp.sso_service.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateTimeUtils {

    private static final DateTimeFormatter COMMON_FORMATTER =
            DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    /**
     * Format LocalDateTime theo dạng "HH:mm:ss dd-MM-yyyy"
     */
    public static String formatToCommon(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(COMMON_FORMATTER);
    }

    /**
     * Format LocalDateTime theo dạng "yyyy-MM-dd'T'HH:mm:ss"
     */
    public static String formatToIso(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(ISO_FORMATTER);
    }

    /**
     * Convert từ chuỗi dạng "HH:mm:ss dd-MM-yyyy" sang LocalDateTime (common → object)
     */
    public static LocalDateTime parseCommonToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeString, COMMON_FORMATTER);
    }

    /**
     * Convert từ chuỗi dạng "yyyy-MM-dd'T'HH:mm:ss" sang LocalDateTime (iso → object)
     */
    public static LocalDateTime parseIsoToLocalDateTime(String dateTimeString) {
        if (dateTimeString == null || dateTimeString.isEmpty()) return null;
        return LocalDateTime.parse(dateTimeString, ISO_FORMATTER);
    }

    /**
     * Convert LocalDateTime từ dạng common sang dạng ISO (LocalDateTime → LocalDateTime)
     */
    public static LocalDateTime convertCommonToIso(LocalDateTime commonDateTime) {
        if (commonDateTime == null) return null;
        String isoString = commonDateTime.format(ISO_FORMATTER);
        return LocalDateTime.parse(isoString, ISO_FORMATTER);
    }

    /**
     * Convert LocalDateTime từ dạng ISO sang dạng common (LocalDateTime → LocalDateTime)
     */
    public static LocalDateTime convertIsoToCommon(LocalDateTime isoDateTime) {
        if (isoDateTime == null) return null;
        String commonString = isoDateTime.format(COMMON_FORMATTER);
        return LocalDateTime.parse(commonString, COMMON_FORMATTER);
    }
}
