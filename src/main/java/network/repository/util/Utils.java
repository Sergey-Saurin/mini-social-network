package network.repository.util;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.time.*;

public class Utils {
  public static Pageable getPageable(long offset, int itemPerPage) {
    return getPageable(offset, itemPerPage, null);
  }

  public static Pageable getPageable(long offset, int itemPerPage, Sort sort) {
    itemPerPage = itemPerPage == 0 ? 1 : itemPerPage;
    int page = (int) (offset / itemPerPage);
    if (sort == null || sort.isEmpty()) {
      return PageRequest.of(page, itemPerPage);
    } else {
      return PageRequest.of(page, itemPerPage, sort);
    }
  }

  public static LocalDateTime getLocalDateTimeFromLong(long date) {
    return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC);
  }

  public static LocalDate getLocalDateFromLong(long date) {
    return Instant.ofEpochMilli(date).atZone(ZoneOffset.UTC).toLocalDate();
  }

  public static Long getLongFromLocalDate(LocalDate date){
    try{
      return date.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
    catch (NullPointerException ex){
      return null;
    }
  }

  public static Long getLongFromLocalDateTime(LocalDateTime date) {
    return date.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
  }

}