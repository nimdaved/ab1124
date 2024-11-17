package org.nimdaved.toolrent.service;

import jakarta.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.apache.commons.collections4.CollectionUtils;
import org.nimdaved.toolrent.domain.Holiday;
import org.nimdaved.toolrent.repository.HolidayRepository;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
public class CalendarService {

    private final HolidayRepository holidayRepository;

    List<Holiday> holidays;

    public CalendarService(HolidayRepository holidayRepository) {
        this.holidayRepository = holidayRepository;
    }

    public DayCounts getDayCounts(@NonNull LocalDate startDate, int dayCount) {
        var weekends = getWeekends(startDate, dayCount);
        var holidays = getHolidays(startDate, dayCount);
        var intersection = CollectionUtils.intersection(weekends, holidays);

        int weekendsAndHolidays = intersection.size();
        int weekendsNonHolidays = weekends.size() - intersection.size();
        int holidaysNonWeekends = holidays.size() - intersection.size();
        int weekdays = dayCount - weekendsNonHolidays - holidaysNonWeekends + weekendsAndHolidays;

        return new DayCounts(weekdays, weekendsNonHolidays, holidaysNonWeekends, weekendsAndHolidays);
    }

    @PostConstruct
    public void populateHolidays() {
        holidays = holidayRepository.findAll();
    }

    private Set<LocalDate> getHolidays(LocalDate startDate, int dayCount) {
        var firstDay = startDate.plusDays(1);
        var lastDay = startDate.plusDays(dayCount);

        return Stream.of(firstDay, lastDay)
            .map(LocalDate::getYear)
            .distinct()
            .flatMap(year -> holidays.stream().map(h -> h.toLocalDate(year)))
            .filter(
                holiday ->
                    holiday.isEqual(firstDay) || holiday.isEqual(lastDay) || (holiday.isAfter(startDate) && holiday.isBefore(lastDay))
            )
            .collect(Collectors.toSet());
    }

    private Set<LocalDate> getWeekends(LocalDate startDate, int dayCount) {
        int fistDay = startDate.getDayOfWeek().getValue() + 1;
        int afterLastDay = fistDay + dayCount;

        return IntStream.range(fistDay, afterLastDay)
            .filter(CalendarService::isWeekend)
            .mapToObj(i -> startDate.plusDays(i))
            .collect(Collectors.toSet());
    }

    private static boolean isWeekend(int day) {
        return day % DayOfWeek.SATURDAY.getValue() == 0 || day % DayOfWeek.SUNDAY.getValue() == 0;
    }

    public record DayCounts(int weekdays, int weekendsNonHolidays, int holidaysNonWeekends, int weekendsAndHolidays) {}
}
