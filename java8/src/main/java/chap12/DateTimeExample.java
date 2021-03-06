package chap12;

import org.omg.CORBA.DATA_CONVERSION;
import sun.util.resources.cldr.en.TimeZoneNames_en_Dsrt;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.chrono.JapaneseDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalAdjuster;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;
import static java.time.temporal.TemporalAdjusters.nextOrSame;

public class DateTimeExample {

    private static final ThreadLocal<DateFormat> formatters = new ThreadLocal<DateFormat>() {

        protected DateFormat initialValue() {
            return new SimpleDateFormat("dd-MM-yyyy");
        }
    };

    public static void main(String[] args) {
        //useOldDate();
        //useLocalDate();
        //useTemporalAdjuster();
        //useDateFormatter();
        zoneInfo();
    }

    private static void useOldDate() {
        Date date = new Date(114, 2, 18);
        System.out.println(date);

        System.out.println(formatters.get().format(date));

        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.FEBRUARY, 18);
        System.out.println(calendar);
    }

    private static void useLocalDate() {
        LocalDate date = LocalDate.of(2014, 3, 18);
        int year = date.getYear();  // 2014
        Month month = date.getMonth();  // March
        int day = date.getDayOfMonth(); // 18
        DayOfWeek dow = date.getDayOfWeek();    //TUESDAY
        int len = date.lengthOfMonth(); // 31 (days in March)
        boolean leap = date.isLeapYear(); //false
        System.out.println(date);
        System.out.println("month: " + month + " dow: " + dow);

        int y = date.get(ChronoField.YEAR);
        int m = date.get(ChronoField.MONTH_OF_YEAR);
        int d = date.get(ChronoField.DAY_OF_MONTH);

        System.out.println("y: " + y + " m: " + m + " d: " + d);

        LocalTime time = LocalTime.of(13, 45, 20); //13:45:20
        int hor = time.getHour();
        int minute = time.getMinute();
        int second = time.getSecond();
        System.out.println(time);

        LocalDateTime dt1 = LocalDateTime.of(2014, Month.MARCH, 18, 13, 45, 20);
        LocalDateTime dt2 = LocalDateTime.of(date, time);
        LocalDateTime dt3 = date.atTime(13, 45, 20);
        LocalDateTime dt4 = date.atTime(time);
        LocalDateTime dt5 = time.atDate(date);
        System.out.println(dt1);

        LocalDate date1 = dt1.toLocalDate();
        System.out.println(date1);
        LocalTime time1 = dt1.toLocalTime();
        System.out.println(time1);

        // 机器的时间
        Instant instant = Instant.ofEpochSecond(44 * 365 * 86400);
        Instant now = Instant.now();

        System.out.println(now);

        // Duration: 用于以秒和纳秒衡量时间的长短
        Duration d1 = Duration.between(LocalTime.of(13,45,10),time);
        Duration d2 = Duration.between(instant, now);
        System.out.println(d1.getSeconds());
        System.out.println(d2.getSeconds());

        Duration threeMinutes = Duration.of(3, ChronoUnit.MINUTES);
        System.out.println(threeMinutes);

        // Period: 用于以年、月、日的方式对多个时间单位建模
        Period tendays = Period.between(LocalDate.of(2014,3,8),LocalDate.of(2014,3,18));
        Period tenDays = Period.ofDays(10);
        System.out.println(tendays.getDays());

        JapaneseDate japaneseDate = JapaneseDate.from(date);
        System.out.println(japaneseDate);
    }

    private static void useTemporalAdjuster(){
        LocalDate date = LocalDate.of(2014,3,18);
       // nextOrSame: 创建一个新的日期，并将其值设定为日期调整后或者调整前，第一个符合指定星期几要求的日期
        date = date.with(nextOrSame(DayOfWeek.SUNDAY));
        System.out.println(date);           //2014-3-18
        //lastDatOfMonth():创建一个新的日期，它的值为下月的最后一天
        date = date.with(lastDayOfMonth());
        System.out.println(date);           //2014-3-31

        date = date.with(nextOrSame(DayOfWeek.FRIDAY));   //2014-4-04
        System.out.println(date);
        date = date.with(temporal ->{
           DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
           int dayToAdd = 1;
           if(dow == DayOfWeek.FRIDAY) dayToAdd =3;
           if(dow == DayOfWeek.SATURDAY) dayToAdd = 2;
           return temporal.plus(dayToAdd,ChronoUnit.DAYS);
        });
        System.out.println(date);   // 2014-4-07
    }

    private static class NextWorkingDay implements TemporalAdjuster{

        @Override
        public Temporal adjustInto(Temporal temporal) {
            DayOfWeek dow = DayOfWeek.of(temporal.get(ChronoField.DAY_OF_WEEK));
            int dayToAdd = 1;
            if (dow == DayOfWeek.FRIDAY) dayToAdd = 3;
            if(dow == DayOfWeek.SATURDAY) dayToAdd = 2;
            return temporal.plus(dayToAdd,ChronoUnit.DAYS);
        }
    }

    private static void useDateFormatter() {
        LocalDate date = LocalDate.of(2014,3,18);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter italianFormatter = DateTimeFormatter.ofPattern("d. MMMM yyyy", Locale.ITALIAN);
        System.out.println(date.format(DateTimeFormatter.ISO_LOCAL_DATE));
        System.out.println(date.format(formatter));
        System.out.println(date.format(italianFormatter));

        DateTimeFormatter complexFormatter = new DateTimeFormatterBuilder()
                .appendText(ChronoField.DAY_OF_MONTH)
                .appendLiteral(". ")
                .appendText(ChronoField.MONTH_OF_YEAR)
                .appendLiteral(" ")
                .appendText(ChronoField.YEAR)
                .parseCaseInsensitive()
                .toFormatter(Locale.ITALIAN);

        System.out.println(date.format(complexFormatter));
    }

    private  static void zoneInfo(){
        ZoneId shZone = ZoneId.of("Asia/Shanghai");
        LocalDate date = LocalDate.of(2014,3,18);
        ZonedDateTime zd1 = date.atStartOfDay(shZone);
        System.out.println(zd1);

        LocalDateTime dateTime = LocalDateTime.of(2014,3,18,13,18,45);
        ZonedDateTime zd2 = dateTime.atZone(shZone);
        System.out.println(zd2);

        Instant instant = Instant.now();
        ZonedDateTime zdt3 = instant.atZone(shZone);
        System.out.println(zdt3);

    }
}
