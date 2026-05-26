////////////////////////////////////////////////////////////////
// DATE & TIME API (java.time — Java 8+)
////////////////////////////////////////////////////////////////

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

public class DateTimeAPI {
    public static void main(String[] args) {

        ////////////////////////////////////////////////////////////////
        // LOCALDATE — solo fecha (sin hora ni zona)
        ////////////////////////////////////////////////////////////////

        LocalDate today = LocalDate.now();
        LocalDate birthday = LocalDate.of(1995, 6, 23);
        LocalDate parsed = LocalDate.parse("2025-12-31");

        System.out.println("Hoy: " + today);
        System.out.println("Cumpleaños: " + birthday);

        // Consultas
        System.out.println("Año: " + today.getYear());
        System.out.println("Mes: " + today.getMonth());        // JANUARY, etc.
        System.out.println("Mes num: " + today.getMonthValue()); // 1..12
        System.out.println("Día del mes: " + today.getDayOfMonth());
        System.out.println("Día de la semana: " + today.getDayOfWeek()); // MONDAY, etc.
        System.out.println("Día del año: " + today.getDayOfYear());
        System.out.println("Bisiesto: " + today.isLeapYear());

        // Manipulación (inmutable — devuelve nueva instancia)
        LocalDate tomorrow = today.plusDays(1);
        LocalDate lastMonth = today.minusMonths(1);
        LocalDate nextYear = today.plusYears(1);
        LocalDate withDay = today.withDayOfMonth(1); // primer día del mes

        // Comparación
        System.out.println("Antes: " + birthday.isBefore(today));  // true
        System.out.println("Después: " + birthday.isAfter(today)); // false
        System.out.println("Igual: " + today.isEqual(LocalDate.now()));

        ////////////////////////////////////////////////////////////////
        // LOCALTIME — solo hora (sin fecha ni zona)
        ////////////////////////////////////////////////////////////////

        LocalTime now = LocalTime.now();
        LocalTime lunch = LocalTime.of(13, 30);
        LocalTime precise = LocalTime.of(14, 30, 45, 123456789); // h, m, s, ns

        System.out.println("Ahora: " + now);
        System.out.println("Hora: " + now.getHour());
        System.out.println("Minuto: " + now.getMinute());
        System.out.println("Segundo: " + now.getSecond());

        LocalTime inTwoHours = now.plusHours(2);
        LocalTime halfHourAgo = now.minusMinutes(30);

        ////////////////////////////////////////////////////////////////
        // LOCALDATETIME — fecha + hora (sin zona)
        ////////////////////////////////////////////////////////////////

        LocalDateTime dateTime = LocalDateTime.now();
        LocalDateTime specific = LocalDateTime.of(2025, 6, 15, 14, 30);
        LocalDateTime combined = LocalDateTime.of(today, lunch);

        System.out.println("Fecha y hora: " + dateTime);

        // Extraer partes
        LocalDate datePart = dateTime.toLocalDate();
        LocalTime timePart = dateTime.toLocalTime();

        ////////////////////////////////////////////////////////////////
        // ZONEDDATETIME — fecha + hora + zona horaria
        ////////////////////////////////////////////////////////////////

        ZonedDateTime zoned = ZonedDateTime.now();
        ZonedDateTime madrid = ZonedDateTime.now(ZoneId.of("Europe/Madrid"));
        ZonedDateTime tokyo = ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));

        System.out.println("Local: " + zoned);
        System.out.println("Madrid: " + madrid);
        System.out.println("Tokyo: " + tokyo);

        // Convertir entre zonas
        ZonedDateTime tokyoFromMadrid = madrid.withZoneSameInstant(ZoneId.of("Asia/Tokyo"));
        System.out.println("Madrid → Tokyo: " + tokyoFromMadrid);

        // Listar zonas disponibles
        // ZoneId.getAvailableZoneIds().forEach(System.out::println);

        ////////////////////////////////////////////////////////////////
        // INSTANT — momento exacto (epoch, para timestamps)
        ////////////////////////////////////////////////////////////////

        Instant instant = Instant.now();
        System.out.println("Instant: " + instant);
        System.out.println("Epoch seconds: " + instant.getEpochSecond());
        System.out.println("Epoch millis: " + instant.toEpochMilli());

        // Desde epoch
        Instant fromEpoch = Instant.ofEpochSecond(1_000_000_000);
        System.out.println("Epoch 1B: " + fromEpoch);

        ////////////////////////////////////////////////////////////////
        // DURATION — duración entre dos tiempos
        ////////////////////////////////////////////////////////////////

        Duration duration = Duration.between(lunch, now);
        System.out.println("Duración: " + duration);
        System.out.println("En minutos: " + duration.toMinutes());
        System.out.println("En horas: " + duration.toHours());

        // Crear duraciones
        Duration twoHours = Duration.ofHours(2);
        Duration thirtyMin = Duration.ofMinutes(30);
        Duration fiveSeconds = Duration.ofSeconds(5);

        ////////////////////////////////////////////////////////////////
        // PERIOD — período entre dos fechas
        ////////////////////////////////////////////////////////////////

        Period age = Period.between(birthday, today);
        System.out.println("Edad: " + age.getYears() + " años, "
            + age.getMonths() + " meses, "
            + age.getDays() + " días");

        // Crear períodos
        Period oneYear = Period.ofYears(1);
        Period twoMonths = Period.ofMonths(2);
        Period tenDays = Period.ofDays(10);

        // Aplicar período a una fecha
        LocalDate futureDate = today.plus(oneYear).plus(twoMonths);

        ////////////////////////////////////////////////////////////////
        // CHRONOUNIT — calcular diferencias exactas
        ////////////////////////////////////////////////////////////////

        long daysBetween = ChronoUnit.DAYS.between(birthday, today);
        long monthsBetween = ChronoUnit.MONTHS.between(birthday, today);
        long yearsBetween = ChronoUnit.YEARS.between(birthday, today);

        System.out.println("Días desde cumpleaños: " + daysBetween);
        System.out.println("Meses: " + monthsBetween);
        System.out.println("Años: " + yearsBetween);

        ////////////////////////////////////////////////////////////////
        // DATETIMEFORMATTER — formatear y parsear
        ////////////////////////////////////////////////////////////////

        // Formatos predefinidos
        System.out.println(today.format(DateTimeFormatter.ISO_LOCAL_DATE));
        // "2025-06-15"

        // Formato personalizado
        DateTimeFormatter spanish = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        System.out.println(today.format(spanish));  // "15/06/2025"

        DateTimeFormatter full = DateTimeFormatter.ofPattern("EEEE, dd 'de' MMMM 'de' yyyy");
        System.out.println(today.format(full));
        // "domingo, 15 de junio de 2025"

        DateTimeFormatter withTime = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println(dateTime.format(withTime));
        // "15/06/2025 14:30:00"

        // Parsear desde String con formato
        LocalDate parsedSpanish = LocalDate.parse("25/12/2025", spanish);
        System.out.println("Parseado: " + parsedSpanish);

        ////////////////////////////////////////////////////////////////
        // PATRONES COMUNES
        ////////////////////////////////////////////////////////////////

        // ¿Es fin de semana?
        DayOfWeek dow = today.getDayOfWeek();
        boolean isWeekend = dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY;
        System.out.println("¿Fin de semana? " + isWeekend);

        // Próximo lunes
        LocalDate nextMonday = today.with(java.time.temporal.TemporalAdjusters.next(DayOfWeek.MONDAY));
        System.out.println("Próximo lunes: " + nextMonday);

        // Primer día del mes
        LocalDate firstDay = today.with(java.time.temporal.TemporalAdjusters.firstDayOfMonth());
        System.out.println("Primer día del mes: " + firstDay);

        // Último día del mes
        LocalDate lastDay = today.with(java.time.temporal.TemporalAdjusters.lastDayOfMonth());
        System.out.println("Último día del mes: " + lastDay);
    }
}
