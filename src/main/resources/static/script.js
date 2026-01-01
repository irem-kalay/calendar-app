// ====================================================================
// ÇEVİRİ İŞLEMLERİ VE GENEL AYARLAR (Bu bölüm değişmedi)
// ====================================================================

//let currentLanguage = 'TR'; // currentLanguage'ın nerede tanımlandığından emin olun, global olması gerekiyor.

// YENİ: Çeviri anahtarlarını veritabanındaki record_id'lere eşleyen sözlük.
// Bu sözlükteki ID'ler, veritabanına eklediğiniz ID'lerle aynı olmalıdır.

let currentSectionId = null; // Global tanımladım

const uiKeyToIdMap = {
    'nav.settings': 1,
    'nav.workday': 2,
    'nav.summary': 3,
    'nav.longweekend': 4,
    'nav.personalevent': 5,

    'page.title': 10,

    'settings.title': 11,
    'settings.description': 12,
    'region.label': 13,
    'persontype.label': 14,
    'language.label': 15,

    'language.option.tr': 16,
    'language.option.gb': 17,
    'language.option.ar': 18,
    'language.option.de': 19,

    'workday.title': 20,
    'workday.startDate.label': 21,
    'workday.endDate.label': 22,
    'workday.calculate.button': 23,
    'workday.csv.button': 24,
    'workday.calculating': 25,
    'workday.result.prefix': 26,
    'workday.result.suffix': 27,

    'holidaysummary.title': 30,
    'holidaysummary.get.button': 31,
    'holidaysummary.loading': 32,
    'holidaysummary.notfound': 33,

    'summary.duration.prefix': 34,
    'summary.duration.suffix': 35,
    'summary.type.prefix': 36,
    'summary.notes.prefix': 37,
    'summary.date.prefix': 38,

    'fixedholidays.title': 39,
    'fixedholidays.year.label': 40,
    'fixedholidays.year.placeholder': 41,
    'fixedholidays.get.button': 42,

    'longweekend.title': 50,
    'longweekend.year.label': 51,
    'longweekend.find.button': 52,
    'longweekend.loading': 53,
    'longweekend.notfound': 54,
    'longweekend.card.title.suffix': 55,
    'longweekend.reasons.title': 56,

    'weekend.reason': 99,

    'personalevent.title': 60,
    'personalevent.name.label': 61,
    'personalevent.date.label': 62,
    'personalevent.save.button': 63,
    'nav.fixed': 64,
    'anasayfa.label': 65,
    'anasayfa.aciklama': 66,
    'anasayfa2.aciklama': 67,
    'yardımcı.chat':68,
    'gönder':69,
    'longweekend.column.period':70,
    'longweekend.column.totalDays':71,
    'longweekend.column.holidayName':72,
    'warning':73,
};

// Global bir yerde tüm UI çevirilerini tutalım
let uiTranslations = {};
let currentLanguage = 'TR'; // currentLanguage başlangıçta TR


// Dil değiştirildiğinde tetiklenir
async function handleLanguageChange(selectElement) {
    currentLanguage = selectElement.value;

    // Yeni dil için UI çevirilerini başta bir kere çekip global değişkene atayalım
    try {
        const response = await fetch(`/api/translations/table/ui?langCode=${currentLanguage}`);
        uiTranslations = response.ok ? await response.json() : {};
    } catch (error) {
        console.error("UI çevirileri yüklenemedi:", error);
        uiTranslations = {};
    }

    // Tüm sayfayı yeni dilde yeniden çiz/çevir
    await applyAllTranslations();

    // 2. Dropdown içeriklerini yeniden çeviriyle doldur
    await populateDropdown('global_personTypeId', '/api/persontypes', 'id', 'name', currentLanguage, 'persontypes');
    await populateDropdown('global_regionId', '/api/regions', 'id', 'countryName', currentLanguage, 'regions');


    if (document.getElementById("fixed-holiday-result")) {
        getFixedHolidays();  
    }
    if(document.getElementById('holiday-summary-result')){
        getHolidaySummary();
    }

    if(document.getElementById('long-weekend-result'))
    {
        findLongWeekends();
    }
     if(document.getElementById('workday-count-result')){
        await getWorkdayCount();
    }
}

// Tüm görünür elementleri ve formları çevirir
function applyAllTranslations() {
    // 1. Statik metinleri (data-translate-key olanlar) çevir
    document.querySelectorAll('[data-translate-key]').forEach(element => {
        const key = element.getAttribute('data-translate-key');
        const id = uiKeyToIdMap[key]; // Örn: fixedholidays.title -> 12 gibi
        if (id && uiTranslations[id]) {
            element.textContent = uiTranslations[id];
        }
    });

    // 2. Dropdown verileri güncellenmeden sadece metinleri değiştir
    translateDropdownOptions('global_personTypeId', 'persontypes');
    translateDropdownOptions('global_regionId', 'regions');
}

// Açılır menüdeki seçenekleri yalnızca çeviriyle güncelle
function translateDropdownOptions(selectId, groupKey) {
    const select = document.getElementById(selectId);
    if (!select || !uiTranslations) return;

    for (let option of select.options) {
        const dataId = option.getAttribute('data-id'); // Veritabanı ID'si
        if (dataId && uiTranslations[`${groupKey}.${dataId}`]) {
            option.textContent = uiTranslations[`${groupKey}.${dataId}`];
        }
    }
}


//kişi ve region seçimlerinde mevcut sayfanın güncellenmesi
async function handleFilterChange() {
    const regionId = document.getElementById("global_regionId")?.value;
    const personTypeId = document.getElementById("global_personTypeId")?.value;

    // Seçili değerler yoksa bir şey yapma
    if (!regionId || !personTypeId) return;

    // Sayfa içeriğini yeni bilgilere göre güncelle
    if (document.getElementById("fixed-holiday-result")) {
        await getFixedHolidays(regionId, personTypeId);
    }
    if (document.getElementById("holiday-summary-result")) {
        await getHolidaySummary(regionId, personTypeId);
    }
    if (document.getElementById("long-weekend-result")) {
        await findLongWeekends(regionId, personTypeId);
    }

    if(document.getElementById('workday-count-result')){
        await getWorkdayCount();
    }
}



// ====================================================================
// ANA FONKSİYONLAR (ÇEVİRİ DESTEKLİ)
// ====================================================================

window.onload = function() {
    populateDropdown('global_personTypeId', '/api/persontypes', 'id', 'name', 'TR', 'persontypes');
    populateDropdown('global_regionId', '/api/regions', 'id', 'countryName', 'TR', 'regions');
};

async function populateDropdown(elementId, apiUrl, valueField, textField, langCode, translationTableName) {
    try {
        const [data, translations] = await Promise.all([
            fetch(apiUrl).then(res => res.json()),
            // Buradaki koşulu kaldırdım. Her zaman çevirileri çek.
            fetch(`/api/translations/table/${translationTableName}?langCode=${langCode}`).then(res => res.ok ? res.json() : {})
        ]);

        const selectElement = document.getElementById(elementId);
        if (selectElement) {
            const currentValue = selectElement.value;
            selectElement.innerHTML = '';
            data.forEach(item => {
                const option = document.createElement('option');
                option.value = item[valueField];
                // Çeviri varsa kullan, yoksa orijinal metin
                // "deneme" kontrolünü kaldırdım.
                option.textContent = (translations && translations[item[valueField]]) ? translations[item[valueField]] : item[textField];
                selectElement.appendChild(option);
            });
            if (currentValue) selectElement.value = currentValue;
        }
    } catch (error) {
        console.error(`${elementId} yüklenirken hata oluştu:`, error);
    }
}


async function getHolidaySummary() {
    const selectedPersonTypeId = document.getElementById('global_personTypeId').value;
    const regionId = document.getElementById('global_regionId').value;

    const yearInput = document.getElementById('hs_year');
    const year = yearInput ? yearInput.value : null;

    let summaryUrl = `/api/holidays/summary?regionId=${regionId}&personTypeId=${selectedPersonTypeId}`;
    if (year) {
        summaryUrl += `&year=${year}`;
    }

    const holidayTranslationUrl = `/api/translations/table/holidays?langCode=${currentLanguage}`;
    const holidayTypeTranslationUrl = `/api/translations/table/holiday_type?langCode=${currentLanguage}`;
    const holidayNotesTranslationUrl = `/api/translations/table/holidaynotes?langCode=${currentLanguage}`;

    const resultArea = document.getElementById('holiday-summary-result');
    resultArea.innerHTML = uiTranslations[uiKeyToIdMap['holidaysummary.loading']] || 'Tatil özeti yükleniyor...';

    try {
        const [
            summaries,
            holidayTranslations,
            holidayNotesTranslations,
            holidayTypeTranslations
        ] = await Promise.all([
            fetch(summaryUrl).then(res => res.json()),
            // Buradaki koşulları kaldırdım. Her zaman çevirileri çek.
            fetch(holidayTranslationUrl).then(res => res.ok ? res.json() : {}),
            fetch(holidayNotesTranslationUrl).then(res => res.ok ? res.json() : {}),
            fetch(holidayTypeTranslationUrl).then(res => res.ok ? res.json() : {})
        ]);

        if (!summaries || summaries.length === 0) {
            resultArea.innerHTML = uiTranslations[uiKeyToIdMap['holidaysummary.notfound']] || 'Bu kriterlere uygun tatil özeti bulunamadı.';
            return;
        }

        const groupedSummaries = {};
        summaries.forEach(summary => {
            const key = summary.definitionId || summary.holidayName;
            if (!groupedSummaries[key]) {
                groupedSummaries[key] = {
                    ...summary,
                    totalDuration: summary.durationDays || 0,
                    allMonthDays: [summary.monthDay],
                    allHolidayIds: [summary.holidayId]
                };
            } else {
                groupedSummaries[key].totalDuration += summary.durationDays || 0;

                if (summary.monthDay && !groupedSummaries[key].allMonthDays.includes(summary.monthDay)) {
                    groupedSummaries[key].allMonthDays.push(summary.monthDay);
                }

                if (summary.holidayId && !groupedSummaries[key].allHolidayIds.includes(summary.holidayId)) {
                    groupedSummaries[key].allHolidayIds.push(summary.holidayId);
                }
            }
        });

        let htmlContent = '<div class="holiday-grid">';
        Object.values(groupedSummaries).forEach(group => {
            const displayName = holidayTranslations[group.definitionId] || group.holidayName;
            const translatedType = holidayTypeTranslations[group.holidayTypeId] || group.holidayType || 'Bilinmiyor';

            const translatedNotes = group.allHolidayIds.map(id => holidayNotesTranslations[id] || null).filter(Boolean);
            const notesFormatted = translatedNotes.length > 0 ? translatedNotes.join('; ') : '-';
            const monthDaysFormatted = group.allMonthDays.sort().join(', ');

            const durationText = uiTranslations[uiKeyToIdMap['summary.duration.prefix']] || 'Süre:';
            const durationUnit = uiTranslations[uiKeyToIdMap['summary.duration.suffix']] || 'gün';
            const typeText = uiTranslations[uiKeyToIdMap['summary.type.prefix']] || 'Türü:';
            const notesText = uiTranslations[uiKeyToIdMap['summary.notes.prefix']] || 'Not:';
            const dateText = uiTranslations[uiKeyToIdMap['summary.date.prefix']] || 'Tarih:';

            htmlContent += `
                <div class="holiday-row">
                    <h3>${displayName}</h3>
                    <ul>
                        ${group.totalDuration > 0 ? `<li><strong>${durationText}</strong> ${group.totalDuration} ${durationUnit}</li>` : ''}
                        <li><strong>${typeText}</strong> ${translatedType}</li>
                        <li><strong>${dateText}</strong> ${monthDaysFormatted}</li>
                        <li><strong>${notesText}</strong> ${notesFormatted}</li>
                    </ul>
                </div>
            `;
        });

        htmlContent += '</div>';
        resultArea.innerHTML = htmlContent;

    } catch (error) {
        resultArea.innerHTML = `<p style="color:red;">Bir hata oluştu: ${error.message}</p>`;
    }
}


async function findLongWeekends() { 
    const personTypeId = document.getElementById('global_personTypeId').value;
    const regionId = document.getElementById('global_regionId').value;
    const year = document.getElementById('lw_year').value;

    const personTypeIds = personTypeId.includes(',') ? personTypeId : personTypeId;
    const url = `/api/holiday-blocks?startDate=${year}-01-01&endDate=${year}-12-31&regionId=${regionId}&personTypeIds=${personTypeIds}`;

    const holidayTranslationUrl = `/api/translations/table/holidays?langCode=${currentLanguage}`;

    const resultArea = document.getElementById('long-weekend-result');
    resultArea.innerHTML = uiTranslations[uiKeyToIdMap['longweekend.loading']] || 'Uzun hafta sonu fırsatları aranıyor...';

    try {
        const [longWeekends, holidayTranslations] = await Promise.all([
            fetch(url).then(res => res.json()),
            // Buradaki koşulu kaldırdım. Her zaman çevirileri çek.
            fetch(holidayTranslationUrl).then(res => res.ok ? res.json() : {})
        ]);

        const filteredWeekends = longWeekends.filter(w => w.totalDays > 1);

        if (filteredWeekends.length === 0) {
            resultArea.innerHTML = uiTranslations[uiKeyToIdMap['longweekend.notfound']] || 'Bu kriterlere uygun uzun hafta sonu fırsatı bulunamadı.';
            return;
        }

        // Çeviri metinleri
        const periodText = uiTranslations[uiKeyToIdMap['longweekend.column.period']] || 'Tarih Aralığı';
        const totalDaysText = uiTranslations[uiKeyToIdMap['longweekend.column.totalDays']] || 'Toplam Gün';
        const holidayNameText = uiTranslations[uiKeyToIdMap['longweekend.column.holidayName']] || 'Tatil Adı';

        let htmlContent = `
            <style>
                .long-weekend-table {
                    width: 100%;
                    border-collapse: collapse;
                }
                .long-weekend-table th, .long-weekend-table td {
                    padding: 10px;
                    text-align: left;
                    border-bottom: 1px solid #ddd;
                }
                .long-weekend-table tr:hover {
                    background-color: #f5f5f5;
                    cursor: pointer;
                }
                .long-weekend-table th {
                    background-color: #f0f0f0;
                }
            </style>
            <table class="long-weekend-table">
                <thead>
                    <tr>
                        <th>${periodText}</th>
                        <th>${totalDaysText}</th>
                        <th>${holidayNameText}</th>
                    </tr>
                </thead>
                <tbody>
        `;

        const options = { day: 'numeric', month: 'long' };
        const locale = currentLanguage === 'TR' ? 'tr-TR' : 'en-GB';

        filteredWeekends.forEach(weekend => {
            const startDate = new Date(weekend.startDate).toLocaleDateString(locale, options);
            const endDate = new Date(weekend.endDate).toLocaleDateString(locale, options);
            const totalDays = weekend.totalDays;

            const translatedHolidayName = holidayTranslations[weekend.definitionId] || weekend.holidayName;

            htmlContent += `
                <tr>
                    <td>${startDate} - ${endDate}</td>
                    <td>${totalDays}</td>
                    <td>${translatedHolidayName}</td>
                </tr>
            `;
        });

        htmlContent += `
                </tbody>
            </table>
        `;

        resultArea.innerHTML = htmlContent;

    } catch (error) {
        resultArea.innerHTML = `<p style="color:red;">Bir hata oluştu: ${error.message}</p>`;
    }
}


// --- GÜNCELLENEN FONKSİYON: displayCalendarView ---
async function displayCalendarView(startDate, endDate, personTypeIds, regionId) {
    const year = new Date(startDate).getFullYear();

    const idsArray = typeof personTypeIds === 'string' ? personTypeIds.split(',') : [personTypeIds];
    const uniqueIds = [...new Set(idsArray)];

    const translationUrl = `/api/translations/table/holidays?langCode=${currentLanguage}`;
    const notesTranslationUrl = `/api/translations/table/holidaynotes?langCode=${currentLanguage}`;

    // Tatil adı ve açıklama (notes) çevirilerini paralel çek
    const translationsPromise = fetch(translationUrl).then(res => res.ok ? res.json() : {});
    const notesTranslationsPromise = fetch(notesTranslationUrl).then(res => res.ok ? res.json() : {});

    try {
        // Tüm holiday summary istekleri
        const holidayRequests = uniqueIds.map(id =>
            fetch(`/api/holidays/summary?regionId=${regionId}&personTypeId=${id}&year=${year}`)
                .then(res => res.json())
        );

        const [translations, notesTranslations, ...holidayDataArrays] = await Promise.all([
            translationsPromise,
            notesTranslationsPromise,
            ...holidayRequests
        ]);

        const combinedHolidays = holidayDataArrays.flat();

        const holidayMap = {};
        combinedHolidays.forEach(h => {
            const date = `${year}-${h.monthDay}`;
            const name = translations[h.definitionId] || h.holidayName;

            // Not çevirisini holidayId ile dene, yoksa doğrudan notes kullan
            const translatedNote = h.holidayId ? (notesTranslations[h.holidayId] || h.notes || '') : (h.notes || '');

            if (!holidayMap[date]) holidayMap[date] = [];

            const alreadyAdded = holidayMap[date].some(e => e.name === name && e.notes === translatedNote);
            if (!alreadyAdded) {
                holidayMap[date].push({ name, notes: translatedNote });
            }
        });

        renderCalendarWithSummary(startDate, endDate, holidayMap);
    } catch (error) {
        document.getElementById('calendar-view-container').innerHTML = `<p style="color: red;">Takvim yüklenemedi: ${error.message}</p>`;
    }
}



function renderCalendarWithSummary(startDateStr, endDateStr, holidayMap) {
    const container = document.getElementById('calendar-view-container');
    container.innerHTML = '';

    const start = new Date(startDateStr);
    const end = new Date(endDateStr);

    const grid = document.createElement('div');
    grid.className = 'calendar-grid';

    // 🔁 Gün isimlerini dile göre ayarla
    const lang = currentLanguage; //currentLanguage global variable
    const daysOfWeek = lang === 'ENG'
        ? ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
        : ['Pzt', 'Sal', 'Çar', 'Per', 'Cum', 'Cmt', 'Paz'];

    daysOfWeek.forEach(day => {
        const header = document.createElement('div');
        header.className = 'calendar-header';
        header.textContent = day;
        grid.appendChild(header);
    });

    let current = new Date(start);
    let isoStartDay = current.getDay();
    if (isoStartDay === 0) isoStartDay = 7;
    grid.style.gridTemplateColumns = 'repeat(7, 1fr)';

    while (current < end) { //end dahil değil
        const cell = document.createElement('div');
        const dayStr = current.toISOString().split('T')[0]; // YYYY-MM-DD

        const isWeekend = current.getDay() === 6 || current.getDay() === 0;
        const holidayInfo = holidayMap[dayStr];

        cell.classList.add('calendar-day');
        if (holidayInfo) {
            cell.classList.add('holiday');
        } else if (isWeekend) {
            cell.classList.add('weekend');
        } else {
            cell.classList.add('workday');
        }

        if (current.getTime() === start.getTime()) {
            cell.style.gridColumnStart = isoStartDay;
        }

        const dayNumber = document.createElement('div');
        dayNumber.className = 'day-number';
        dayNumber.textContent = current.getDate();
        cell.appendChild(dayNumber);

        if (holidayInfo) {
            const holidayDiv = document.createElement('div');
            holidayDiv.className = 'holiday-name';
            holidayDiv.innerHTML = holidayInfo.map(h => `<div>${h.name}${h.notes ? ` - ${h.notes}` : ''}</div>`).join('');
            cell.appendChild(holidayDiv);
        }

        grid.appendChild(cell);
        current.setDate(current.getDate() + 1);
    }

    container.appendChild(grid);
}




// --- GÜNCELLENEN FONKSİYON: getWorkdayCount ---
async function getWorkdayCount() {
    const selectedPersonTypeId = document.getElementById('global_personTypeId').value;
    const regionId = document.getElementById('global_regionId').value;
    const startDate = document.getElementById('wg_startDate').value;
    const endDate = document.getElementById('wg_endDate').value;
    

 //   if (!startDate || !endDate) {
 //       alert('Lütfen tarih aralığını seçin.');
 //       return;
 //   }
    
 if(startDate > endDate)
 {
    alert(uiTranslations[uiKeyToIdMap['warning']] ||'Başlangıç Tarihini Bitiş Tarihinden Sonra olarak seçtiniz');
    return;
 }

    let personTypeIdsToFetch = selectedPersonTypeId;

    // Eğer seçilen kişi tipi "Herkes" değilse (varsayılan ID'si 1 ise), "Herkes"i de ekle
    // Not: "Herkes"in ID'sinin 1 olduğundan emin olun.
    if (selectedPersonTypeId !== '1') {
        personTypeIdsToFetch += ',1'; // Mevcut seçime "1" (Herkes) ID'sini ekle
    }

    const resultArea = document.getElementById('workday-count-result');
    resultArea.textContent = uiTranslations[uiKeyToIdMap['workday.calculating']] || 'Hesaplanıyor...';

    const url = `/api/workdays?startDate=${startDate}&endDate=${endDate}&regionId=${regionId}&personTypeIds=${personTypeIdsToFetch}`;

    try {
        const response = await fetch(url);
        const data = await response.json();

        const prefix = uiTranslations[uiKeyToIdMap['workday.result.prefix']] || 'Seçilen aralıkta toplam';
        const suffix = uiTranslations[uiKeyToIdMap['workday.result.suffix']] || 'iş günü bulunmaktadır.';
        
        resultArea.textContent = `${prefix} ${data.totalWorkdays} ${suffix}`;
        resultArea.style.color = 'blue';
    } catch (error) {
        resultArea.textContent = 'Hata: ' + error.message;
        resultArea.style.color = 'red';
    }

    // displayCalendarView için de aynı mantığı uygula
    // Eğer /api/workdays API'niz sadece totalWorkdays döndürüyor ve calendarDays döndürmüyorsa,
    // bu kısım takvim görünümünü doldurmayacaktır.
    displayCalendarView(startDate, endDate, personTypeIdsToFetch, regionId);
}


async function getFixedHolidays() {
    const regionId = document.getElementById('global_regionId').value;
    const personTypeId = document.getElementById('global_personTypeId').value;
    const year = 2025;

    const resultArea = document.getElementById('fixed-holiday-result');
    resultArea.innerHTML = uiTranslations?.[uiKeyToIdMap['fixedholidays.loading']] || 'Sabit tatiller yükleniyor...';

    const personTypeIds = personTypeId === '1' ? '1' : `${personTypeId}&personTypeIds=1`;
    const url = `/api/holidays/fixed?regionId=${regionId}&personTypeIds=${personTypeIds}&year=${year}`;

    const holidayTranslationUrl = `/api/translations/table/holidays?langCode=${currentLanguage}`;
    const holidayTypeTranslationUrl = `/api/translations/table/holiday_type?langCode=${currentLanguage}`;

    try {
        const [holidayData, holidayTranslations, holidayTypeTranslations] = await Promise.all([
            fetch(url).then(res => res.json()),
            // Buradaki koşulları kaldırdım. Her zaman çevirileri çek.
            fetch(holidayTranslationUrl).then(res => res.ok ? res.json() : {}),
            fetch(holidayTypeTranslationUrl).then(res => res.ok ? res.json() : {})
        ]);

        if (!holidayData || holidayData.length === 0) {
            resultArea.innerHTML = uiTranslations?.[uiKeyToIdMap['fixedholidays.notfound']] || 'Belirtilen kriterlere göre sabit tatil bulunamadı.';
            return;
        }

        let htmlContent = '<div class="holiday-grid">';
        holidayData.forEach(holiday => {
            const translatedName = holidayTranslations[holiday.definitionId] || holiday.holidayName;
            const translatedType = holidayTypeTranslations[holiday.holidayTypeId] || holiday.holidayType; //jsonda holidayTypeId şeklindeydi

            const typeLabel = uiTranslations?.[uiKeyToIdMap['summary.type.prefix']] || 'Türü:';
            const dateLabel = uiTranslations?.[uiKeyToIdMap['summary.date.prefix']] || 'Tarih:';
            const notesLabel = uiTranslations?.[uiKeyToIdMap['summary.notes.prefix']] || 'Not:';

            htmlContent += `
                <div class="holiday-row">
                    <h3>${translatedName}</h3>
                    <ul>
                        <li><strong>${typeLabel}</strong> ${translatedType || 'Bilinmiyor'}</li>
                        <li><strong>${dateLabel}</strong> ${holiday.monthDay}</li>
                        <li><strong>${notesLabel}</strong> ${holiday.notes || '-'}</li>
                    </ul>
                </div>
            `;
        });
        htmlContent += '</div>';
        resultArea.innerHTML = htmlContent;

    } catch (err) {
        resultArea.innerHTML = `<p style="color:red;">Hata oluştu: ${err.message}</p>`;
    }
}




//sayfa açıldığında ilk bölümü göstermesi için


//button holding için
function showSection(sectionId, btn = null) {
    // Welcome mesajını gizle
    const welcome = document.getElementById('welcome-section');
    if (welcome) welcome.style.display = 'none';

    // Diğer bölümleri göster/gizle
    const sections = document.querySelectorAll('.api-section');
    sections.forEach(sec => {
        sec.style.display = (sec.id === sectionId) ? 'block' : 'none';
    });

    // Aktif butonu vurgula
    const navButtons = document.querySelectorAll('.nav-btn');
    navButtons.forEach(b => b.classList.remove('active'));
    if (btn) btn.classList.add('active');
}


// İlk açılışta ilk bölüm görünsün:
document.addEventListener('DOMContentLoaded', () => {
    const sections = document.querySelectorAll('.api-section');
    sections.forEach(sec => sec.style.display = 'none');

    const navButtons = document.querySelectorAll('.nav-btn');
    navButtons.forEach(b => b.classList.remove('active'));

    getHolidaySummary();        // veriyi otomatik çek
});


document.getElementById('nav-tatil-ozeti').addEventListener('click', () => {
    showSection('tatil-ozeti');
    getHolidaySummary();
});


document.getElementById('nav-sabit-tatiller').addEventListener('click', () => {
    showSection('sabit-tatiller');
    getFixedHolidays();
});

//chatbox hareket edebilmesi için
const chatContainer = document.getElementById('chat-container');
const chatHeader = document.getElementById('chat-header');

// Yeni tutacaklar
const resizeHandleE = document.getElementById('resize-handle-e');
const resizeHandleW = document.getElementById('resize-handle-w');
const resizeHandleN = document.getElementById('resize-handle-n');
const resizeHandleS = document.getElementById('resize-handle-s');
const resizeHandleSE = document.getElementById('resize-handle-se'); // sağ-alt köşe

let isDragging = false;
let dragOffsetX = 0;
let dragOffsetY = 0;

let isResizing = false;
let resizeDirection = null; // 'e', 'w', 'n', 's', 'se'
let resizeStartWidth = 0;
let resizeStartHeight = 0;
let resizeStartX = 0;
let resizeStartY = 0;
let resizeStartLeft = 0;
let resizeStartTop = 0;

// --- Sürükleme ---
chatHeader.addEventListener('mousedown', (e) => {
    isDragging = true;
    dragOffsetX = e.clientX - chatContainer.getBoundingClientRect().left;
    dragOffsetY = e.clientY - chatContainer.getBoundingClientRect().top;
    document.body.style.userSelect = 'none';
});

document.addEventListener('mouseup', () => {
    isDragging = false;
    isResizing = false;
    resizeDirection = null;
    document.body.style.userSelect = 'auto';
});

document.addEventListener('mousemove', (e) => {
    if (isDragging) {
        let newLeft = e.clientX - dragOffsetX;
        let newTop = e.clientY - dragOffsetY;

        const maxLeft = window.innerWidth - chatContainer.offsetWidth;
        const maxTop = window.innerHeight - chatContainer.offsetHeight;
        newLeft = Math.min(Math.max(0, newLeft), maxLeft);
        newTop = Math.min(Math.max(0, newTop), maxTop);

        chatContainer.style.left = newLeft + 'px';
        chatContainer.style.top = newTop + 'px';

        chatContainer.style.right = 'auto';
        chatContainer.style.bottom = 'auto';
        chatContainer.style.position = 'fixed';
    } else if (isResizing) {
        if (!resizeDirection) return;

        let rect = chatContainer.getBoundingClientRect();

        if (resizeDirection === 'e') {
            let newWidth = resizeStartWidth + (e.clientX - resizeStartX);
            newWidth = Math.max(250, Math.min(newWidth, window.innerWidth - resizeStartLeft));
            chatContainer.style.width = newWidth + 'px';
        } else if (resizeDirection === 'w') {
            let newWidth = resizeStartWidth - (e.clientX - resizeStartX);
            let newLeft = resizeStartLeft + (e.clientX - resizeStartX);
            if (newWidth >= 250 && newLeft >= 0) {
                chatContainer.style.width = newWidth + 'px';
                chatContainer.style.left = newLeft + 'px';
                chatContainer.style.right = 'auto';
            }
        } else if (resizeDirection === 'n') {
            let newHeight = resizeStartHeight - (e.clientY - resizeStartY);
            let newTop = resizeStartTop + (e.clientY - resizeStartY);
            if (newHeight >= 200 && newTop >= 0) {
                chatContainer.style.height = newHeight + 'px';
                chatContainer.style.top = newTop + 'px';
                chatContainer.style.bottom = 'auto';
            }
        } else if (resizeDirection === 's') {
            let newHeight = resizeStartHeight + (e.clientY - resizeStartY);
            newHeight = Math.max(200, Math.min(newHeight, window.innerHeight - resizeStartTop));
            chatContainer.style.height = newHeight + 'px';
        } else if (resizeDirection === 'se') {
            // Sağ-alt köşe resize (hem genişlik hem yükseklik)
            let newWidth = resizeStartWidth + (e.clientX - resizeStartX);
            let newHeight = resizeStartHeight + (e.clientY - resizeStartY);

            newWidth = Math.max(250, Math.min(newWidth, window.innerWidth - resizeStartLeft));
            newHeight = Math.max(200, Math.min(newHeight, window.innerHeight - resizeStartTop));

            chatContainer.style.width = newWidth + 'px';
            chatContainer.style.height = newHeight + 'px';
        }
    }
});

// Resize başlatma fonksiyonu
function startResize(e, direction) {
    isResizing = true;
    resizeDirection = direction;

    resizeStartWidth = chatContainer.offsetWidth;
    resizeStartHeight = chatContainer.offsetHeight;
    resizeStartX = e.clientX;
    resizeStartY = e.clientY;

    const rect = chatContainer.getBoundingClientRect();
    resizeStartLeft = rect.left;
    resizeStartTop = rect.top;

    document.body.style.userSelect = 'none';
    e.preventDefault();
}

// Tutacaklara event listener ekle
resizeHandleE.addEventListener('mousedown', e => startResize(e, 'e'));
resizeHandleW.addEventListener('mousedown', e => startResize(e, 'w'));
resizeHandleN.addEventListener('mousedown', e => startResize(e, 'n'));
resizeHandleS.addEventListener('mousedown', e => startResize(e, 's'));
if(resizeHandleSE) {
    resizeHandleSE.addEventListener('mousedown', e => startResize(e, 'se'));
}


  const toggleBtn = document.getElementById('theme-toggle');
  const userTheme = localStorage.getItem('theme');

  // Tema durumunu yükle
  if (userTheme === 'dark') {
    document.documentElement.classList.add('dark');
    toggleBtn.textContent = '☀️';
  } else {
    document.documentElement.classList.remove('dark');
    toggleBtn.textContent = '🌙';
  }

  // Butona tıklayınca tema değiştir
  toggleBtn.addEventListener('click', () => {
    const isDark = document.documentElement.classList.toggle('dark');
    localStorage.setItem('theme', isDark ? 'dark' : 'light');
    toggleBtn.textContent = isDark ? '☀️' : '🌙';
  });



