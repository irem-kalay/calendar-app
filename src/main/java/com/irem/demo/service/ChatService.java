package com.irem.demo.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import com.irem.demo.dto.*;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;


//calculate workdays için regex ile ayıklama
//import java.time.format.DateTimeFormatter;
//import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class ChatService {
    private final ChatClient chatClient;

    private final HolidayService holidayService;
    private final RegionService regionService;
    private final PersonTypeService personTypeService;
    private final WorkdayService workdayService;

  

public ChatService(HolidayService holidayService,
                   RegionService regionService,
                   PersonTypeService personTypeService,
                   WorkdayService workdayService,
                   OllamaChatModel ollamaChatModel) {
    this.holidayService = holidayService;
    this.regionService = regionService;
    this.personTypeService = personTypeService;
    this.workdayService = workdayService;
    this.chatClient = ChatClient.create(ollamaChatModel);
   
}

    private static final Map<String, Integer> monthMap = Map.ofEntries(
    Map.entry("ocak", 1),
    Map.entry("şubat", 2),
    Map.entry("mart", 3),
    Map.entry("nisan", 4),
    Map.entry("mayıs", 5),
    Map.entry("haziran", 6),
    Map.entry("temmuz", 7),
    Map.entry("ağustos", 8),
    Map.entry("eylül", 9),
    Map.entry("ekim", 10),
    Map.entry("kasım", 11),
    Map.entry("aralık", 12)
);

private String getTurkishDayOfWeek(DayOfWeek dayOfWeek) {
    return switch (dayOfWeek) {
        case MONDAY -> "Pazartesi";
        case TUESDAY -> "Salı";
        case WEDNESDAY -> "Çarşamba";
        case THURSDAY -> "Perşembe";
        case FRIDAY -> "Cuma";
        case SATURDAY -> "Cumartesi";
        case SUNDAY -> "Pazar";
    };
}



private String getDayOfWeek(String message) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    LocalDate parsedDate = null;

    // Tam tarih varsa (örn: 01/05/2025)
    Pattern fullDatePattern = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4})");
    Matcher fullDateMatcher = fullDatePattern.matcher(message);

    if (fullDateMatcher.find()) {
        try {
            parsedDate = LocalDate.parse(fullDateMatcher.group(1), formatter);
        } catch (DateTimeParseException e) {
            return "Tarih formatı hatalı, lütfen 'gg/aa/yyyy' şeklinde yazınız.";
        }
    } else {
        // Yazıyla ay: "1 mayıs"
        Pattern dayMonthPattern = Pattern.compile("(\\d{1,2})\\s+([a-zçğıöşü]+)", Pattern.CASE_INSENSITIVE);
        Matcher dayMonthMatcher = dayMonthPattern.matcher(message);

        if (dayMonthMatcher.find()) {
            int day = Integer.parseInt(dayMonthMatcher.group(1));
            String monthName = dayMonthMatcher.group(2).toLowerCase();
            Integer month = monthMap.get(monthName);
            if (month != null) {
                parsedDate = LocalDate.of(2025, month, day); // Yıl sabit (gerekirse genişletilebilir)
            } else {
                return "Ay ismini anlayamadım: " + monthName;
            }
        }
    }

    if (parsedDate == null) {
        return "Lütfen tarih belirtiniz. Örn: '1 mayıs' veya '01/05/2025'";
    }

    return parsedDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
            " tarihi " + getTurkishDayOfWeek(parsedDate.getDayOfWeek()) + " gününe denk gelmektedir.";
}


private Optional<Integer> extractMonthFromMessage(String message) {
    for (var entry : monthMap.entrySet()) {
        if (message.contains(entry.getKey())) {
            return Optional.of(entry.getValue());
        }
    }
    return Optional.empty();
}


private String getHolidaySummaryForMonth(Long regionId, Long personTypeId, int month) {
    // HolidayService’den tüm tatilleri al
    List<HolidaySummary> summaries = holidayService.getHolidaySummary(regionId, personTypeId);

    // Ay filtresi yap (monthDay formatını kontrol etmen lazım, örn "07-15")
    List<HolidaySummary> filtered = summaries.stream()
        .filter(h -> {
            String monthDay = h.getMonthDay();
            String[] parts = monthDay.split("-");
            if (parts.length == 2) {
                try {
                    int holidayMonth = Integer.parseInt(parts[0]); // ayın başta olduğunu varsayıyoruz
                    return holidayMonth == month;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return false;
        })
        .collect(Collectors.toList());

    if (filtered.isEmpty()) return month + ". ayda tatil özeti bulunamadı.";

    return filtered.stream()
            .map(h -> h.getHolidayName() + " - " + h.getHolidayType() + " (" + h.getMonthDay() + ")")
            .collect(Collectors.joining(", "));
}

private String checkIfDateIsHoliday(String message, Long regionId, Long personTypeId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    LocalDate parsedDate = null;

    // Önce 25/04/2025 gibi tam tarih kontrolü
    Pattern fullDatePattern = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4})");
    Matcher fullDateMatcher = fullDatePattern.matcher(message);

    if (fullDateMatcher.find()) {
        try {
            parsedDate = LocalDate.parse(fullDateMatcher.group(1), formatter);
        } catch (DateTimeParseException e) {
            return "Tarih formatı hatalı, lütfen 'gg/aa/yyyy' şeklinde yazınız.";
        }
    } else {
        // "15 temmuz" gibi ifadeyi yakala
        Pattern dayMonthPattern = Pattern.compile("(\\d{1,2})\\s+([a-zçğıöşü]+)");
        Matcher dayMonthMatcher = dayMonthPattern.matcher(message);

        if (dayMonthMatcher.find()) {
            try {
                int day = Integer.parseInt(dayMonthMatcher.group(1));
                String monthName = dayMonthMatcher.group(2);
                Integer month = monthMap.get(monthName);

                if (month != null) {
                    parsedDate = LocalDate.of(2025, month, day); // yıl sabit
                } else {
                    return "Ay ismini anlayamadım: " + monthName;
                }
            } catch (Exception e) {
                return "Tarihi anlayamadım, lütfen örnek gibi yazınız: '15 temmuz' veya '15/07/2025'.";
            }
        }
    }

    if (parsedDate == null) {
        return "Lütfen bir tarih belirtiniz (örn: 25/04/2025 veya 15 temmuz).";
    }

    final LocalDate date = parsedDate;

    // 1. Hafta sonu kontrolü
    DayOfWeek dayOfWeek = date.getDayOfWeek();
    if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
        return date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " tarihi hafta sonuna denk gelmektedir (hafta sonu tatilidir).";
    }

    // 2. Resmî tatil kontrolü
    List<HolidaySummary> summaries = holidayService.getHolidaySummary(regionId, personTypeId);

    return summaries.stream()
        .filter(h -> {
            try {
                String[] parts = h.getMonthDay().split("-");
                if (parts.length != 2) return false;
                int month = Integer.parseInt(parts[0]);
                int day = Integer.parseInt(parts[1]);
                return date.getMonthValue() == month && date.getDayOfMonth() == day;
            } catch (Exception e) {
                return false;
            }
        })
        .findFirst()
        .map(h -> date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                " tarihi tatildir: " + h.getHolidayName() + " (" + h.getHolidayType() + ")")
        .orElse(date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " tarihi bir tatil değildir.");
}




    private String getRegions() {
        List<RegionResponse> regions = regionService.getAllRegions();
        return regions.stream()
                .map(r -> r.getCountryName() + " (" + r.getCountryCode() + ")")
                .collect(Collectors.joining(", "));
    }

    private String getPersonTypes() {
        List<PersonTypeResponse> personTypes = personTypeService.getAllPersonTypes();
        return personTypes.stream()
                .map(pt -> pt.getId() + ": " + pt.getName())
                .collect(Collectors.joining(", "));
    }

    private String getFixedHolidays(Long regionId, List<Long> personTypeIds) {
        List<FixedHolidayResponse> holidays = holidayService.getFixedHolidays(regionId, personTypeIds, null);
        return holidays.stream()
                .map(h -> h.getHolidayName() + " (" + h.getMonthDay() + ")")
                .collect(Collectors.joining(", "));
    }

    private String getHolidaySummary(Long regionId, Long personTypeId) {
        List<HolidaySummary> summaries = holidayService.getHolidaySummary(regionId, personTypeId);
        return summaries.stream()
                .map(h -> h.getHolidayName() + " - " + h.getHolidayType() + " (" + h.getMonthDay() + ")")
                .collect(Collectors.joining(", "));
    }

    
public String calculateWorkdays(String message, Long regionId, Long personTypeId) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d/M/yyyy");
    LocalDate startDate = null;
    LocalDate endDate = null;

    // 1. Doğrudan 2 tarih varsa: "15/07/2025 ile 20/07/2025"
    Pattern fullDatePattern = Pattern.compile("(\\d{1,2}/\\d{1,2}/\\d{4}).*?(\\d{1,2}/\\d{1,2}/\\d{4})");
    Matcher fullDateMatcher = fullDatePattern.matcher(message);

    if (fullDateMatcher.find()) {
        try {
            startDate = LocalDate.parse(fullDateMatcher.group(1), formatter);
            endDate = LocalDate.parse(fullDateMatcher.group(2), formatter);
        } catch (DateTimeParseException e) {
            return "Tarih formatını doğru giriniz (örn: 25/06/2025).";
        }
    } else {
        // 2. Yazıyla ay ismi varsa: "15 temmuz ile 20 temmuz"
        Pattern dayMonthPattern = Pattern.compile("(\\d{1,2})\\s+([a-zçğıöşü]+)(?:\\s+(\\d{4}))?.*?(\\d{1,2})\\s+([a-zçğıöşü]+)(?:\\s+(\\d{4}))?", Pattern.CASE_INSENSITIVE);
        Matcher dayMonthMatcher = dayMonthPattern.matcher(message);

        if (dayMonthMatcher.find()) {
            try {
                int day1 = Integer.parseInt(dayMonthMatcher.group(1));
                String monthName1 = dayMonthMatcher.group(2).toLowerCase();
                Integer year1 = dayMonthMatcher.group(3) != null ? Integer.parseInt(dayMonthMatcher.group(3)) : 2025;

                int day2 = Integer.parseInt(dayMonthMatcher.group(4));
                String monthName2 = dayMonthMatcher.group(5).toLowerCase();
                Integer year2 = dayMonthMatcher.group(6) != null ? Integer.parseInt(dayMonthMatcher.group(6)) : 2025;

                Integer month1 = monthMap.get(monthName1);
                Integer month2 = monthMap.get(monthName2);

                if (month1 != null && month2 != null) {
                    startDate = LocalDate.of(year1, month1, day1);
                    endDate = LocalDate.of(year2, month2, day2);
                } else {
                    return "Ay ismi anlaşılamadı. Lütfen ayları doğru yazınız.";
                }
            } catch (Exception e) {
                return "Tarihleri anlayamadım. Örn: '15 temmuz 2025 ile 20 temmuz 2025' veya '15/07/2025 ile 20/07/2025'";
            }
        }
    }

    if (startDate == null || endDate == null) {
        return "Lütfen başlangıç ve bitiş tarihlerini '15/07/2025 ile 20/07/2025' veya '15 temmuz ile 20 temmuz' gibi belirtiniz.";
    }

    // Swap if necessary
    if (endDate.isBefore(startDate)) {
        LocalDate temp = startDate;
        startDate = endDate;
        endDate = temp;
    }

    double total = workdayService.calculateWorkdays(startDate, endDate, regionId, List.of(personTypeId));
    return String.format("%s ile %s arasında toplam iş günü sayısı: %.0f",
            startDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            endDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
            total);
}



   private String getHolidayBlocks(Long regionId, Long personTypeId) {
    LocalDate start = LocalDate.of(2025, 1, 1);
    LocalDate end = LocalDate.of(2025, 12, 31);
    List<HolidayBlockResponse> blocks = workdayService.findHolidayBlocks(start, end, regionId, List.of(personTypeId));
    if (blocks.isEmpty()) return "Uzayan tatil bulunamadı.";
    return blocks.stream()
            .map(b -> b.getStartDate() + " - " + b.getEndDate() + " (" + b.getTotalDays() + " gün)")
            .collect(Collectors.joining("; "));
}


 public String getReply(String message, Long regionId, Long personTypeId) {
    message = message.toLowerCase();

    try {
        String rawResponse = chatClient.prompt(
                 "Kullanıcıdan gelen mesajı analiz et ve aşağıdaki methodlardan " +
    "hangisinin çağrılması gerektiğini döndür. Yalnızca method adını yaz. " +
    "Her methodun ne yaptığı aşağıda açıklanmıştır:\n\n" +

    "- checkIfDateIsHoliday: Girilen tarihin tatil olup olmadığını kontrol eder.\n" +
    "- getFixedHolidays: Belirli bölge ve kişi türü için sabit- (değişmeyen) tatilleri listeler.\n" +
    "- getHolidaySummaryForMonth: Belirtilen ay için tatil özetini verir.\n" +
    "- getHolidaySummary: Tüm yılın tatil özetini verir.\n" +
    "- getRegions: Mevcut bölgelerin listesini döner.\n" +
    "- getPersonTypes: Kişi türlerinin listesini döner.\n" +
    "- calculateWorkdays: Belirtilen tarih aralığında iş günü sayısını hesaplar.\n" +
    "- getHolidayBlocks: Uzayan tatil bloklarını döner.\n\n" +

    "Eğer mesaj takvim veya tatil ile ilgili değilse 'unknown' döndür.\n\n" +
    "Kullanıcının mesajı Türkçe ise isTurkish = true, değilse false olmalıdır.\n\n" +

    "- getHolidayBlocks: Uzayan tatil bloklarını döner.\n" +
"- getDayOfWeek: Girilen bir tarihin haftanın hangi gününe denk geldiğini belirtir.\n\n" +

    "Mesaj: \"" + message + "\"\n\n" +

    "Sadece method adını döndür, başka bilgi verme.")
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getText()
                .trim();

        // Regex ile method adını ayıkla
        Pattern methodPattern = Pattern.compile("(checkIfDateIsHoliday|getFixedHolidays|getHolidaySummaryForMonth|getHolidaySummary|getRegions|getPersonTypes|calculateWorkdays|getHolidayBlocks|getDayOfWeek)");

        Matcher matcher = methodPattern.matcher(rawResponse);
        String functionCall = matcher.find() ? matcher.group(1) : "unknown";

//eğer aranan cevap yoksa ai cevap verip opsiyon sunacak
 if ("unknown".equals(functionCall)) {
           // Takvim/tatil dışı soru: normal sohbet cevabı verme, sadece yönlendir
            return "Üzgünüm, sadece tatil ve takvim ile ilgili soruları yanıtlayabiliyorum. Lütfen aşağıdaki konulardan birini sorunuz:\n" +
                    "- Belirli bir gün tatil mi?\n" +
                    "- Bir tarih hangi güne denk geliyor\n" +
                    "- İş günü hesaplama\n" +
                    "- Sabit tatiller\n" +
                    "- Tatil özeti\n" +
                    "- Bölge bilgisi\n" +
                    "- Kişi türü\n" +
                    "- Tatil bloğu/Birleşmiş tatiller\n\n";
        }

        Optional<Integer> monthOpt = extractMonthFromMessage(message);

        return switch (functionCall) {
            case "checkIfDateIsHoliday" -> checkIfDateIsHoliday(message, regionId, personTypeId);
            case "getFixedHolidays" -> getFixedHolidays(regionId, List.of(1L, personTypeId));
            case "getHolidaySummaryForMonth" -> monthOpt.map(month -> getHolidaySummaryForMonth(regionId, personTypeId, month))
                                                        .orElse("Ay bilgisi bulunamadı, lütfen 'temmuz' gibi ay ismi veriniz.");
            case "getHolidaySummary" -> getHolidaySummary(regionId, personTypeId);
            case "getRegions" -> getRegions();
            case "getPersonTypes" -> getPersonTypes();
            case "calculateWorkdays" -> calculateWorkdays(message, regionId, personTypeId);
            case "getHolidayBlocks" -> getHolidayBlocks(regionId, personTypeId);
            case "getDayOfWeek" -> getDayOfWeek(message);

            default -> "🤖 Yapay zekadan geçerli bir method ismi alınamadı.";
        };
    } catch (Exception e) {
        return "🤖 Sorunuzu anlayamadım. Lütfen tekrar deneyin.";
    }
}



}