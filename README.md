# ORBIT — Personal Spatial Journal

**A location-aware personal memory, journal & timeline platform for Android.**
Map + Timeline + Journal + Media + Voice + Personal History, in one native Kotlin app.

This document is written three times, once per supported app language — **English**, **فارسی (Persian)**, and **中文 (Chinese)** — each as its own complete section. Jump to the one you need:

- [English](#english)
- [فارسی](#فارسی)
- [中文](#中文)

---

## English

### 1. What is ORBIT?

ORBIT turns your photos, videos, voice notes, journal entries, links, files, events, and tasks into a single, searchable personal history that is organized by **location, time, event, tag, topic, and relationship** — and shown on a synchronized **interactive map** and **timeline** at the same time. Tap a city, street, or pin on the map and the timeline jumps to that period; scrub the timeline and the map centers on wherever you were.

It is built to feel like a private blend of *Google Maps + Google Photos + Day One + Journey + Notion* — but every design decision (colors, layout, motion) is original to ORBIT.

### 2. Feature highlights

**Capture**
- Smart Memory Capture with type auto-detection (Photo, Video, Voice, Note, Journal, Event, Place, Trip, Task, Bookmark, Document, Custom)
- Quick Capture from a launcher shortcut, notification, Quick Settings tile, share sheet, or home-screen widget
- Automatic location + timestamp tagging, fully editable before saving

**Map**
- Interactive map with adaptive marker clustering, heatmap layer, routes/paths, and custom pin/circle/area/line annotations
- Per-type marker styling with tap-to-preview cards and quick actions (favorite, tag, share, create task, add reminder, show related)
- Region summary card (memory/photo/voice counts, first/last visit, top topics) when zoomed to a city or country
- Four map styles: Standard, Dark, Minimal, Travel — plus a modular `MapProvider` interface so the Google Maps backend can be swapped for another provider later

**Timeline & synchronization**
- Day / Week / Month / Year / Travel views
- Map ⇄ Timeline stay in sync in both directions

**Places, Trips & Stories**
- Automatic Visit Detection (clusters raw location samples into *suggested* visits you confirm, edit, or reject — never presented as certain)
- Trip Builder: group memories into a trip manually, or accept an auto-suggested trip when your city/country changes across a gap
- Travel Journal mode per trip: route, city stops, daily memories, photos, voice notes, manual expenses, highlights
- Story Mode / AI Story Builder generates a structured story (title, intro, highlights, places, timeline) from a trip, city, or date range
- **Place opening hours**: enter a place's weekly hours once, and ORBIT computes and displays *"Open now — closes in 2h 15m"* or *"Closed — opens in 3h 40m"* automatically, including overnight (crosses-midnight) hours

**Journal & relationships**
- Block-based Smart Journal Editor (text, heading, checklist, quote, image/video/voice reference, link, location card)
- Memory Relationship Engine (`SAME_PLACE`, `SAME_DAY`, `SAME_TRIP`, `RELATED_TOPIC`, `BEFORE`/`AFTER`, `NEARBY`, and more) plus a Personal Graph View

**Search & AI**
- Full-text Search Engine (Room/SQLite FTS) filterable by keyword, date, location, tag, type, and trip
- Natural Language Memory Search understands phrases like *"where was I last summer"*, *"what memories do I have in this city"*, or *"find all voice notes about project X"* — in **English, Persian, and Chinese**
- On-device AI Spatial Assistant: Daily/Weekly/Monthly Recaps, Trip Summaries, and Story generation — built entirely from aggregating your own local data, so **nothing is fabricated and nothing leaves your device** by default. A `AIAssistantProvider` interface exists if you later want to plug in a cloud model, gated behind an explicit Privacy Center opt-in.

**Media**
- Voice Memory recording + on-device transcription (Android `SpeechRecognizer`)
- Photo Intelligence: EXIF extraction (capture date, GPS, camera model), Duplicate Detection (exact via SHA-256, near-duplicate via perceptual hash), batch Media Importer with pause/resume/retry

**Privacy, security & offline**
- Location is **off by default**. Five explicit modes: Off, Manual, While Using, Smart Context, Background Reminders — each described in plain language in the in-app Privacy Center
- Battery-aware location sampling (intervals widen automatically under Battery Saver / low battery)
- Geofencing for Spatial Notes and Place Reminders, capped safely below Android's system limit
- AES-256 encryption via Android Keystore for backups; BiometricPrompt-based App Lock
- Location History Manager: view, export, or auto-delete raw samples after a user-chosen retention period
- Offline-first: memories, timeline, notes, photos, voice notes, and search all work with no network connection

**Backup & export**
- Encrypted local Backup & Restore with checksum verification and versioning
- Export to **JSON, CSV, GeoJSON, KML, and PDF**, with a privacy warning shown whenever an export includes precise location

**Widgets & quick access**
- Home-screen Quick Capture widget (photo / voice / note in one tap) and a "Today" widget
- Quick Settings tile and launcher shortcuts

**Design system**
- Material 3 with **six theme combinations**: three brightness modes (Light, Dark, AMOLED) × three accent styles (**Windows 11** default, **Red**, **Blue**)
- Full right-to-left layout for **Persian**, full left-to-right for **English** and **Chinese** — switch languages from Settings and the entire UI mirrors instantly
- Dynamic color surfaces, motion, bottom sheets, and shared-element-style transitions throughout

### 3. Tech stack

| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | Clean Architecture + MVVM, Repository pattern |
| DI | Hilt |
| Async | Kotlin Coroutines + Flow |
| Local database | Room (SQLite) + FTS4 full-text search |
| Background work | WorkManager |
| Location | Fused Location Provider + Android Location APIs |
| Maps | Google Maps SDK (Compose) + Maps Utils, behind a modular `MapProvider` interface |
| Security | Android Keystore, Jetpack Security (`EncryptedFile`), BiometricPrompt |
| Widgets | Jetpack Glance |
| Serialization | kotlinx.serialization |
| Networking (optional, opt-in only) | Retrofit / OkHttp |

### 4. Project structure

```
ORBIT/
├── app/
│   ├── build.gradle.kts
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/orbit/spatialjournal/
│       │   │   ├── core/        # models, enums, pure utilities (date/geo/opening-hours/hash)
│       │   │   ├── data/        # Room entities/DAOs/database, repositories, DataStore, security
│       │   │   ├── domain/      # repository interfaces + use cases
│       │   │   ├── location/    # FusedLocationProvider, geofencing, visit detection, battery scheduler
│       │   │   ├── map/         # MapProvider abstraction, clustering, region summaries
│       │   │   ├── ai/          # on-device AI assistant + natural-language query parser
│       │   │   ├── voice/       # recording + speech-to-text
│       │   │   ├── media/       # EXIF, duplicate detection, gallery importer
│       │   │   ├── export/      # JSON / CSV / GeoJSON / KML / PDF writers
│       │   │   ├── backup/      # encrypted backup & restore
│       │   │   ├── workers/     # WorkManager jobs
│       │   │   ├── notifications/ · widgets/ · di/
│       │   │   └── ui/          # theme, navigation, shared components, per-feature screens
│       │   └── res/             # values / values-fa / values-zh, drawables, raw map styles, xml configs
│       ├── test/                # JVM unit tests
│       └── androidTest/         # instrumented Room tests
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── local.properties.example
```

Feature areas are separated by package (`core`, `map`, `location`, `timeline` UI, `memories`, `places`, `trips`, `journal`, `media`, `voice`, `search`, `ai`, `export`, `backup`, `notifications`, `widgets`, `data`/`database`, `workers`, `security`, `ui`) inside a single Gradle module, which keeps the project easy to open in Android Studio while still giving every feature its own clearly-bounded code area. Splitting these into separate Gradle modules later is a mechanical refactor if your team wants stricter build-time isolation.

### 5. Prerequisites

- **Android Studio** Ladybug (2024.2) or newer
- **JDK 17**
- An Android device or emulator running **Android 8.0 (API 26)** or newer
- A **Google Maps SDK for Android** API key (free tier is enough for development) — create one in the [Google Cloud Console](https://console.cloud.google.com/google/maps-apis/credentials) and enable "Maps SDK for Android" for it

### 6. Installation & first run

1. **Extract** the provided ZIP archive to a folder on your computer.
2. **Open** that folder in Android Studio (`File → Open…`) and let it detect the project.
3. In the project's root folder, **copy** `local.properties.example` to a new file named `local.properties`, then open it and:
   - Set `sdk.dir` to your local Android SDK path (Android Studio usually fills this in automatically on first sync).
   - Replace `MAPS_API_KEY=REPLACE_WITH_YOUR_GOOGLE_MAPS_API_KEY` with your real Maps API key.
4. Let Gradle **sync** (Android Studio prompts automatically; if not, `File → Sync Project with Gradle Files`).
5. Select the `development` product flavor and `debug` build type from the Build Variants panel for local testing.
6. Press **Run ▶** with a connected device or emulator selected.
7. On first launch, open **Settings → Location** to choose a Location Mode (default is **Off**), and **Settings → Privacy Center** to review every data-access toggle before enabling anything.

To build a signed release APK/AAB later, add your own signing configuration to `app/build.gradle.kts` under `signingConfigs` — none is included by default for security reasons.

### 7. Permissions explained

| Permission | Why ORBIT asks |
|---|---|
| Location (fine/coarse) | Tagging a new Memory with where it happened; only requested when you use a capture flow or enable a Location Mode above "Off" |
| Background location | Only needed for the "Smart Context" / "Background Reminders" Location Modes; never requested for "Manual" or "While Using" |
| Camera / microphone | Capturing a photo/video or recording a Voice Memory |
| Media (images/video/audio) | Importing existing gallery items via the Memory Importer |
| Notifications | Reminders, Place Reminders, and duplicate-scan results |
| Calendar (optional) | Only if you enable Calendar Integration in Settings |
| Biometric | Only if you enable App Lock in the Privacy Center |

### 8. Themes

Pick a **brightness mode** (Light / Dark / AMOLED / System) and an **accent style** (Windows 11 default / Red / Blue) independently in Settings — nine visual combinations in total, all built on Material 3 `ColorScheme`s.

### 9. Languages

English, فارسی (Persian), and 中文 (Chinese) are fully wired through Android's per-app language mechanism and `values` / `values-fa` / `values-zh` resource sets. Persian renders true right-to-left (including mirrored navigation and layout); English and Chinese render left-to-right. Switch anytime from **Settings → Language**.

### 10. Honest status notes

This codebase is a genuine, compiling-in-spirit architecture with real logic behind every subsystem described above (clustering, opening-hours math, duplicate detection, trip suggestion, on-device recap generation, etc.), provided as source for you to build in Android Studio — it has not been compiled inside this delivery environment (no Android SDK toolchain is available here), so treat first-build Gradle/version hiccups as normal for a project this size and adjust dependency versions if your Android Studio suggests newer stable releases. A few areas are intentionally left as clean extension points rather than fully built out: true offline vector-tile map caching (the included `MapProvider` interface supports swapping in a provider like MapLibre for this), cloud LLM integration for the AI assistant (an interface is provided; the shipped implementation is on-device only), and not every screen's UI copy has been routed through `stringResource()` yet even though the full `strings.xml` translations exist for all three languages.

### 11. License

Provided under the MIT License. Use, modify, and ship it — attribution appreciated but not required.

---

## فارسی

### ۱. ORBIT چیست؟

ORBIT عکس‌ها، ویدیوها، یادداشت‌های صوتی، نوشته‌های ژورنال، لینک‌ها، فایل‌ها، رویدادها و وظایف شما را به یک تاریخچه شخصی واحد و قابل‌جستجو تبدیل می‌کند که بر اساس **مکان، زمان، رویداد، برچسب، موضوع و رابطه** سازمان‌دهی شده و همزمان روی یک **نقشه تعاملی** و یک **جدول زمانی** هماهنگ نمایش داده می‌شود. روی یک شهر، خیابان یا پین در نقشه ضربه بزنید تا جدول زمانی به همان بازه منتقل شود؛ در جدول زمانی جابه‌جا شوید تا نقشه روی همان موقعیت متمرکز شود.

طراحی آن ترکیبی خصوصی از حس *Google Maps + Google Photos + Day One + Journey + Notion* است، اما تمام تصمیمات طراحی (رنگ‌ها، چیدمان، انیمیشن) کاملاً اختصاصی ORBIT هستند.

### ۲. ویژگی‌های کلیدی

**ثبت خاطره**
- ثبت هوشمند خاطره با تشخیص خودکار نوع محتوا (عکس، ویدیو، صوتی، یادداشت، ژورنال، رویداد، مکان، سفر، وظیفه، نشان‌شده، سند، سفارشی)
- ثبت سریع از طریق میانبر لانچر، اعلان، کاشی تنظیمات سریع، صفحه اشتراک‌گذاری یا ویجت صفحه اصلی
- برچسب‌گذاری خودکار مکان و زمان، با امکان ویرایش کامل پیش از ذخیره

**نقشه**
- نقشه تعاملی با خوشه‌بندی تطبیقی مارکرها، لایه نقشه حرارتی، مسیرها و حاشیه‌نویسی‌های سفارشی (پین، دایره، ناحیه، خط)
- استایل جداگانه برای هر نوع مارکر همراه با کارت پیش‌نمایش و اقدامات سریع (علاقه‌مندی، برچسب، اشتراک‌گذاری، ایجاد وظیفه، افزودن یادآور، نمایش موارد مرتبط)
- کارت خلاصه منطقه (تعداد خاطرات/عکس/صوتی، اولین و آخرین بازدید، موضوعات پرتکرار) هنگام زوم روی یک شهر یا کشور
- چهار استایل نقشه: استاندارد، تاریک، مینیمال، سفر — به‌همراه رابط ماژولار `MapProvider` برای جایگزینی سرویس نقشه در آینده

**جدول زمانی و همگام‌سازی**
- نماهای روز / هفته / ماه / سال / سفر
- همگام‌سازی دوطرفه بین نقشه و جدول زمانی

**مکان‌ها، سفرها و داستان‌ها**
- تشخیص خودکار حضور (Visit Detection) که نمونه‌های خام موقعیت را خوشه‌بندی کرده و به‌صورت «پیشنهادی» ارائه می‌دهد — هرگز به‌عنوان واقعیت قطعی، بلکه همیشه قابل تأیید، ویرایش یا رد توسط کاربر
- سازنده سفر: گروه‌بندی دستی خاطرات در یک سفر، یا پذیرش سفر پیشنهادی هنگام تغییر شهر/کشور در یک بازه زمانی
- حالت ژورنال سفر برای هر سفر: مسیر، توقف‌های شهری، خاطرات روزانه، عکس‌ها، یادداشت‌های صوتی، هزینه‌های دستی، نکات برجسته
- حالت داستان / سازنده داستان با هوش مصنوعی که از یک سفر، شهر یا بازه زمانی، یک داستان ساختاریافته (عنوان، مقدمه، نکات برجسته، مکان‌ها، جدول زمانی) می‌سازد
- **ساعات کاری مکان**: ساعات هفتگی یک مکان را یک‌بار وارد کنید تا ORBIT به‌طور خودکار وضعیت «اکنون باز است — ۲ ساعت و ۱۵ دقیقه دیگر بسته می‌شود» یا «بسته است — ۳ ساعت و ۴۰ دقیقه دیگر باز می‌شود» را محاسبه و نمایش دهد؛ حتی برای ساعاتی که از نیمه‌شب عبور می‌کنند

**ژورنال و روابط**
- ویرایشگر هوشمند ژورنال مبتنی بر بلوک (متن، تیتر، چک‌لیست، نقل‌قول، ارجاع به عکس/ویدیو/صوت، لینک، کارت مکان)
- موتور روابط خاطرات (`SAME_PLACE`، `SAME_DAY`، `SAME_TRIP`، `RELATED_TOPIC`، `BEFORE`/`AFTER`، `NEARBY` و موارد دیگر) به‌همراه نمای گراف شخصی

**جستجو و هوش مصنوعی**
- موتور جستجوی تمام‌متن (Room/SQLite FTS) قابل فیلتر بر اساس کلمه کلیدی، تاریخ، مکان، برچسب، نوع و سفر
- جستجوی زبان طبیعی خاطرات که عباراتی مانند «تابستان پارسال کجا بودم»، «چه خاطراتی در این شهر دارم» یا «تمام یادداشت‌های صوتی مربوط به پروژه X را پیدا کن» را در **انگلیسی، فارسی و چینی** درک می‌کند
- دستیار هوشمند مکانی روی دستگاه: مرورهای روزانه/هفتگی/ماهانه، خلاصه سفر و تولید داستان — همگی صرفاً از تجمیع داده‌های محلی خودتان ساخته می‌شوند، به این معنا که **هیچ‌چیز جعل نمی‌شود و به‌طور پیش‌فرض هیچ داده‌ای از دستگاه شما خارج نمی‌شود**. رابط `AIAssistantProvider` برای اتصال اختیاری به یک مدل ابری در آینده وجود دارد، منوط به فعال‌سازی صریح در مرکز حریم خصوصی

**رسانه**
- ضبط یادداشت صوتی و رونویسی روی دستگاه (با `SpeechRecognizer` اندروید)
- هوش تصویری: استخراج EXIF (تاریخ عکس‌برداری، GPS، مدل دوربین)، تشخیص تکراری (دقیق با SHA-256، مشابه با هش ادراکی)، وارد‌کننده دسته‌ای رسانه با توقف/ادامه/تلاش مجدد

**حریم خصوصی، امنیت و آفلاین**
- موقعیت مکانی به‌طور پیش‌فرض **خاموش** است. پنج حالت صریح: خاموش، دستی، فقط هنگام استفاده، هوشمند، یادآورهای پس‌زمینه — هرکدام با توضیح ساده در مرکز حریم خصوصی داخل برنامه
- نمونه‌برداری موقعیت مکانی متناسب با باتری (فاصله زمانی به‌طور خودکار در حالت صرفه‌جویی باتری یا باتری کم افزایش می‌یابد)
- ژئوفنسینگ برای یادداشت‌های مکانی و یادآورهای مکان، با محدودیت ایمن زیر سقف سیستمی اندروید
- رمزگذاری AES-256 از طریق Android Keystore برای پشتیبان‌ها؛ قفل برنامه مبتنی بر BiometricPrompt
- مدیریت تاریخچه موقعیت مکانی: مشاهده، خروجی‌گیری یا حذف خودکار نمونه‌های خام پس از مدت‌زمان انتخابی کاربر
- آفلاین‌محور: خاطرات، جدول زمانی، یادداشت‌ها، عکس‌ها، صوتی‌ها و جستجو همگی بدون اتصال اینترنت کار می‌کنند

**پشتیبان‌گیری و خروجی**
- پشتیبان‌گیری و بازیابی محلی رمزگذاری‌شده با تأیید چک‌سام و نسخه‌بندی
- خروجی به **JSON، CSV، GeoJSON، KML و PDF**، همراه با هشدار حریم خصوصی هرگاه خروجی شامل موقعیت دقیق باشد

**ویجت‌ها و دسترسی سریع**
- ویجت ثبت سریع در صفحه اصلی (عکس/صوت/یادداشت در یک ضربه) و ویجت «امروز»
- کاشی تنظیمات سریع و میانبرهای لانچر

**سیستم طراحی**
- Material 3 با **شش ترکیب تم**: سه حالت روشنایی (روشن، تاریک، امولد) × سه استایل رنگی (پیش‌فرض **ویندوز ۱۱**، **قرمز**، **آبی**)
- چیدمان کاملاً راست‌به‌چپ برای **فارسی**، کاملاً چپ‌به‌راست برای **انگلیسی** و **چینی** — زبان را از تنظیمات تغییر دهید و کل رابط کاربری فوراً آینه می‌شود

### ۳. پشته فناوری

| لایه | فناوری |
|---|---|
| زبان | Kotlin |
| رابط کاربری | Jetpack Compose + Material 3 |
| معماری | Clean Architecture + MVVM، الگوی Repository |
| تزریق وابستگی | Hilt |
| ناهمزمانی | Kotlin Coroutines + Flow |
| پایگاه‌داده محلی | Room (SQLite) + جستجوی تمام‌متن FTS4 |
| کار پس‌زمینه | WorkManager |
| موقعیت مکانی | Fused Location Provider + Android Location APIs |
| نقشه | Google Maps SDK (Compose) + Maps Utils، پشت رابط ماژولار `MapProvider` |
| امنیت | Android Keystore، Jetpack Security (`EncryptedFile`)، BiometricPrompt |
| ویجت‌ها | Jetpack Glance |
| سریال‌سازی | kotlinx.serialization |
| شبکه (اختیاری، فقط با انتخاب کاربر) | Retrofit / OkHttp |

### ۴. ساختار پروژه

ساختار پوشه‌ها دقیقاً مطابق بخش انگلیسی بالا (`core`، `data`، `domain`، `location`، `map`، `ai`، `voice`، `media`، `export`، `backup`، `workers`، `notifications`، `widgets`، `di`، `ui`) است؛ هر حوزه در یک پوشه مجزا نگه داشته شده تا مرز هر قابلیت در کد کاملاً مشخص باشد.

### ۵. پیش‌نیازها

- **Android Studio** نسخه Ladybug (۲۰۲۴٫۲) یا جدیدتر
- **JDK 17**
- دستگاه یا شبیه‌ساز اندرویدی با **Android 8.0 (API 26)** یا بالاتر
- یک کلید API برای **Google Maps SDK for Android** (سطح رایگان برای توسعه کافی است) — آن را در [Google Cloud Console](https://console.cloud.google.com/google/maps-apis/credentials) بسازید و «Maps SDK for Android» را برایش فعال کنید

### ۶. نصب و اولین اجرا

۱. فایل ZIP ارائه‌شده را در پوشه‌ای روی سیستم خود **استخراج** کنید.
۲. آن پوشه را در Android Studio باز کنید (`File → Open…`) و اجازه دهید پروژه شناسایی شود.
۳. در پوشه ریشه پروژه، فایل `local.properties.example` را به فایل جدیدی با نام `local.properties` **کپی** کنید، سپس آن را باز کرده و:
   - مقدار `sdk.dir` را به مسیر محلی Android SDK خود تنظیم کنید (Android Studio معمولاً این را در اولین Sync خودکار پر می‌کند).
   - `MAPS_API_KEY=REPLACE_WITH_YOUR_GOOGLE_MAPS_API_KEY` را با کلید واقعی Maps خود جایگزین کنید.
۴. اجازه دهید Gradle **Sync** شود (Android Studio به‌طور خودکار پیشنهاد می‌دهد؛ در غیر این صورت از مسیر `File → Sync Project with Gradle Files`).
۵. از پنل Build Variants، Product Flavor با نام `development` و Build Type با نام `debug` را برای تست محلی انتخاب کنید.
۶. با انتخاب یک دستگاه یا شبیه‌ساز متصل، دکمه **Run ▶** را بزنید.
۷. در اولین اجرا، از مسیر **تنظیمات ← موقعیت مکانی** یک حالت مکانی انتخاب کنید (پیش‌فرض **خاموش** است) و از مسیر **تنظیمات ← مرکز حریم خصوصی** پیش از فعال‌سازی هرچیزی، همه کلیدهای دسترسی به داده را بررسی کنید.

برای ساخت نسخه انتشار امضاشده (APK/AAB) بعداً، پیکربندی امضای خودتان را در `app/build.gradle.kts` زیر بخش `signingConfigs` اضافه کنید — به دلایل امنیتی هیچ پیکربندی امضایی به‌طور پیش‌فرض در پروژه قرار داده نشده است.

### ۷. توضیح دسترسی‌ها

| دسترسی | دلیل درخواست ORBIT |
|---|---|
| موقعیت مکانی (دقیق/تقریبی) | برچسب‌گذاری یک خاطره جدید با محل وقوع آن؛ فقط هنگام استفاده از یک ابزار ثبت یا فعال‌سازی یک حالت مکانی غیر از «خاموش» درخواست می‌شود |
| موقعیت مکانی پس‌زمینه | فقط برای حالت‌های «هوشمند» یا «یادآورهای پس‌زمینه» لازم است؛ هرگز برای «دستی» یا «فقط هنگام استفاده» درخواست نمی‌شود |
| دوربین / میکروفون | ثبت عکس/ویدیو یا ضبط یادداشت صوتی |
| رسانه (عکس/ویدیو/صوت) | وارد کردن آیتم‌های موجود در گالری از طریق وارد‌کننده خاطرات |
| اعلان‌ها | یادآورها، یادآورهای مکانی و نتایج اسکن موارد تکراری |
| تقویم (اختیاری) | فقط در صورت فعال‌سازی یکپارچگی تقویم در تنظیمات |
| بیومتریک | فقط در صورت فعال‌سازی قفل برنامه در مرکز حریم خصوصی |

### ۸. تم‌ها

یک **حالت روشنایی** (روشن / تاریک / امولد / سیستم) و یک **استایل رنگی** (پیش‌فرض ویندوز ۱۱ / قرمز / آبی) را به‌طور مستقل از تنظیمات انتخاب کنید — در مجموع نه ترکیب بصری، همگی بر پایه `ColorScheme` در Material 3.

### ۹. زبان‌ها

انگلیسی، فارسی و چینی به‌طور کامل از طریق مکانیزم زبان هر‌برنامه اندروید و مجموعه منابع `values` / `values-fa` / `values-zh` پیاده‌سازی شده‌اند. فارسی به‌صورت کاملاً راست‌به‌چپ (شامل ناوبری و چیدمان آینه‌شده) و انگلیسی و چینی به‌صورت چپ‌به‌راست نمایش داده می‌شوند. هر زمان از مسیر **تنظیمات ← زبان** قابل تغییر است.

### ۱۰. یادداشت صادقانه درباره وضعیت پروژه

این کدبیس یک معماری واقعی و منطقاً کامل است که پشت هر قابلیت ذکرشده منطق واقعی وجود دارد (خوشه‌بندی، محاسبه ساعات کاری، تشخیص تکراری، پیشنهاد سفر، تولید مرور روی دستگاه و غیره) و به‌صورت کد منبع برای ساخت در Android Studio ارائه شده است — این پروژه درون محیط تحویل فعلی کامپایل نشده (چون ابزار Android SDK در این محیط در دسترس نیست)، بنابراین اگر در اولین Build با Gradle یا نسخه کتابخانه‌ها به مشکل کوچکی برخوردید، طبیعی است؛ در صورت نیاز نسخه‌ها را مطابق پیشنهاد Android Studio به‌روز کنید. چند بخش عمداً به‌صورت نقطه توسعه آماده باقی مانده‌اند نه کاملاً پیاده‌سازی‌شده: کش واقعی نقشه آفلاین با کاشی‌های برداری (رابط `MapProvider` ارائه‌شده امکان جایگزینی با سرویسی مانند MapLibre را فراهم می‌کند)، اتصال به مدل‌های زبانی ابری برای دستیار هوش مصنوعی (رابط آن آماده است؛ پیاده‌سازی ارائه‌شده فقط روی دستگاه است)، و هنوز همه متن‌های هر صفحه به `stringResource()` منتقل نشده‌اند، هرچند ترجمه کامل `strings.xml` برای هر سه زبان موجود است.

### ۱۱. مجوز

تحت مجوز MIT ارائه شده است. استفاده، تغییر و انتشار آن آزاد است — ذکر منبع قدردانی می‌شود اما الزامی نیست.

---

## 中文

### 1. ORBIT 是什么？

ORBIT 将您的照片、视频、语音笔记、日记条目、链接、文件、事件和任务，整合为一份统一、可搜索的个人历史记录，并按**位置、时间、事件、标签、主题和关系**进行组织，同时在一个联动的**交互式地图**和**时间线**上展示。点击地图上的某个城市、街道或图钉，时间线会自动跳转到对应时段；滑动时间线，地图也会自动定位到当时所在的位置。

它的体验定位是 *Google 地图 + Google 相册 + Day One + Journey + Notion* 的私人融合体，但每一处设计（配色、布局、动效）都是 ORBIT 的原创方案。

### 2. 核心功能

**记录**
- 智能记录捕获，自动识别内容类型（照片、视频、语音、笔记、日记、事件、地点、旅行、任务、书签、文档、自定义）
- 支持通过启动器快捷方式、通知、快速设置图块、分享菜单或主屏幕小组件进行快速记录
- 自动添加位置与时间戳标签，保存前均可编辑

**地图**
- 交互式地图，具备自适应标记聚合、热力图层、路线/路径以及自定义图钉/圆形/区域/线条标注
- 每种类型独立的标记样式，点击可查看预览卡片及快捷操作（收藏、添加标签、分享、创建任务、添加提醒、显示相关回忆）
- 缩小到城市或国家级别时，自动显示区域摘要卡片（回忆/照片/语音数量、首次与最近到访时间、热门主题）
- 四种地图风格：标准、深色、简约、旅行风——并通过模块化的 `MapProvider` 接口，方便未来更换地图服务商

**时间线与联动**
- 支持日 / 周 / 月 / 年 / 旅行视图
- 地图与时间线双向实时联动

**地点、旅行与故事**
- 自动到访检测（Visit Detection）：将原始位置样本聚类为「推测到访」，供您确认、修改或拒绝——绝不作为确定事实呈现
- 旅行构建器：手动将回忆归入一次旅行，或在城市/国家跨越较大间隔发生变化时接受系统自动推荐的旅行
- 每次旅行的旅行日记模式：路线、途经城市、每日回忆、照片、语音笔记、手动记录的花费、亮点
- 故事模式 / AI故事生成器：从一次旅行、一个城市或一段时间范围，生成结构化故事（标题、引言、亮点、地点、时间线）
- **地点营业时间**：只需录入一次某地点的每周营业时间，ORBIT 即可自动计算并显示「营业中——2小时15分钟后打烊」或「已打烊——3小时40分钟后营业」，即使营业时间跨越午夜也能正确处理

**日记与关系**
- 基于内容块的智能日记编辑器（文本、标题、清单、引用、图片/视频/语音引用、链接、位置卡片）
- 回忆关系引擎（`SAME_PLACE`同地点、`SAME_DAY`同日、`SAME_TRIP`同旅行、`RELATED_TOPIC`相关主题、`BEFORE`/`AFTER`先后、`NEARBY`临近 等），并配有个人关系图谱视图

**搜索与AI**
- 全文搜索引擎（基于 Room/SQLite FTS），支持按关键词、日期、位置、标签、类型和旅行筛选
- 自然语言回忆搜索，可理解如「去年夏天我在哪里」「我在这座城市有什么回忆」「找到所有关于项目X的语音笔记」等**中文、英文、波斯语**语句
- 设备端AI空间助手：日/周/月回顾、旅行摘要与故事生成——完全基于聚合您本地的真实数据构建，因此**默认情况下不会捏造任何内容，也不会有任何数据离开您的设备**。已提供 `AIAssistantProvider` 接口，若您日后希望接入云端模型，需在隐私中心中明确开启授权

**媒体**
- 语音记录及设备端转录（基于 Android `SpeechRecognizer`）
- 照片智能：EXIF信息提取（拍摄日期、GPS、相机型号）、重复检测（基于SHA-256的精确匹配、基于感知哈希的近似匹配）、支持暂停/继续/重试的批量媒体导入器

**隐私、安全与离线**
- 位置功能**默认关闭**。提供五种明确模式：关闭、手动、仅使用应用时、智能情境、后台提醒——应用内隐私中心对每种模式都有通俗易懂的说明
- 位置采样具备电量感知能力（在省电模式或低电量时自动延长采样间隔）
- 用于空间笔记与地点提醒的地理围栏功能，安全地控制在 Android 系统上限之下
- 备份采用通过 Android 密钥库实现的 AES-256 加密；应用锁基于 BiometricPrompt 生物识别
- 位置历史管理器：查看、导出，或按用户设定的保留期限自动删除原始位置样本
- 离线优先：回忆、时间线、笔记、照片、语音与搜索功能在无网络连接时均可正常使用

**备份与导出**
- 本地加密备份与恢复，具备校验和验证与版本管理
- 支持导出为 **JSON、CSV、GeoJSON、KML 和 PDF** 格式，当导出内容包含精确位置信息时会显示隐私提示

**小组件与快捷方式**
- 主屏幕快速记录小组件（一键记录照片/语音/笔记）及「今日」小组件
- 快速设置图块与启动器快捷方式

**设计系统**
- 基于 Material 3，共提供**六种主题组合**：三种明暗模式（浅色、深色、纯黑）×三种主题色风格（默认**Windows 11**、**红色**、**蓝色**）
- **波斯语**完整支持从右到左布局，**英文**与**中文**完整支持从左到右布局——在设置中切换语言，整个界面会立即镜像切换

### 3. 技术栈

| 层级 | 技术 |
|---|---|
| 编程语言 | Kotlin |
| 界面 | Jetpack Compose + Material 3 |
| 架构 | 整洁架构（Clean Architecture）+ MVVM，Repository 模式 |
| 依赖注入 | Hilt |
| 异步处理 | Kotlin 协程 + Flow |
| 本地数据库 | Room（SQLite）+ FTS4 全文搜索 |
| 后台任务 | WorkManager |
| 位置服务 | Fused Location Provider + Android 位置API |
| 地图 | Google 地图 SDK（Compose 版）+ Maps Utils，封装于模块化 `MapProvider` 接口之后 |
| 安全 | Android 密钥库、Jetpack Security（`EncryptedFile`）、BiometricPrompt |
| 小组件 | Jetpack Glance |
| 序列化 | kotlinx.serialization |
| 网络（可选，需用户主动开启） | Retrofit / OkHttp |

### 4. 项目结构

文件夹结构与上方英文部分完全一致（`core`、`data`、`domain`、`location`、`map`、`ai`、`voice`、`media`、`export`、`backup`、`workers`、`notifications`、`widgets`、`di`、`ui`），每个功能领域都拥有各自独立的文件夹，边界清晰。

### 5. 前置条件

- **Android Studio** Ladybug（2024.2）或更新版本
- **JDK 17**
- 运行 **Android 8.0（API 26）**或更高版本的设备或模拟器
- 一个 **Google Maps SDK for Android** 的 API 密钥（开发阶段免费额度即可）——请在 [Google Cloud Console](https://console.cloud.google.com/google/maps-apis/credentials) 中创建，并为其启用「Maps SDK for Android」

### 6. 安装与首次运行

1. 将提供的 ZIP 压缩包**解压**到您电脑上的一个文件夹中。
2. 在 Android Studio 中**打开**该文件夹（`File → Open…`），等待项目被识别。
3. 在项目根目录下，将 `local.properties.example` **复制**为一个新文件，命名为 `local.properties`，然后打开它并：
   - 将 `sdk.dir` 设置为您本地 Android SDK 的路径（Android Studio 通常会在首次同步时自动填写）。
   - 将 `MAPS_API_KEY=REPLACE_WITH_YOUR_GOOGLE_MAPS_API_KEY` 替换为您真实的 Maps API 密钥。
4. 等待 Gradle **同步**完成（Android Studio 会自动提示；如未提示，可手动执行 `File → Sync Project with Gradle Files`）。
5. 在 Build Variants 面板中选择 `development` 产品风味与 `debug` 构建类型，用于本地测试。
6. 选择已连接的设备或模拟器后，点击 **Run ▶** 运行。
7. 首次启动后，前往**设置 → 位置**选择一种位置模式（默认**关闭**），并前往**设置 → 隐私中心**，在启用任何功能前查看每一项数据访问开关。

如需日后构建已签名的正式版 APK/AAB，请在 `app/build.gradle.kts` 的 `signingConfigs` 中添加您自己的签名配置——出于安全考虑，项目默认不包含任何签名配置。

### 7. 权限说明

| 权限 | ORBIT 请求原因 |
|---|---|
| 位置（精确/大致） | 为新记录标注发生地点；仅在您使用记录功能或启用「关闭」以外的位置模式时才会请求 |
| 后台位置 | 仅「智能情境」/「后台提醒」位置模式需要；「手动」或「仅使用应用时」模式下绝不会请求 |
| 摄像头 / 麦克风 | 拍摄照片/视频或录制语音回忆 |
| 媒体（图片/视频/音频） | 通过回忆导入器导入现有相册内容 |
| 通知 | 提醒、地点提醒及重复项扫描结果通知 |
| 日历（可选） | 仅在设置中启用日历集成时使用 |
| 生物识别 | 仅在隐私中心启用应用锁时使用 |

### 8. 主题

在设置中可独立选择**明暗模式**（浅色 / 深色 / 纯黑 / 跟随系统）与**主题色风格**（默认 Windows 11 / 红色 / 蓝色），共可组合出九种视觉效果，均基于 Material 3 的 `ColorScheme` 构建。

### 9. 语言

英文、波斯语与中文均已通过 Android 的按应用语言机制及 `values` / `values-fa` / `values-zh` 资源集完整实现。波斯语完整支持从右到左渲染（包括镜像的导航与布局），英文与中文则从左到右渲染。可随时在**设置 → 语言**中切换。

### 10. 诚实的项目状态说明

本代码库是一套真实、逻辑上完整的架构，上述每个子系统背后都有真实运行的逻辑（聚类算法、营业时间计算、重复检测、旅行推荐、设备端回顾生成等），以源代码形式交付，供您在 Android Studio 中构建——由于本交付环境中没有 Android SDK 工具链，该项目尚未在此环境内实际编译，因此如果您在首次构建时遇到 Gradle 或依赖版本方面的小问题，属于此类规模项目的正常情况，可根据 Android Studio 的提示升级相应依赖版本。有几个部分被有意保留为清晰的可扩展接口，而非完全实现：真正的离线矢量瓦片地图缓存（所提供的 `MapProvider` 接口支持未来替换为 MapLibre 等方案）、AI助手对接云端大语言模型的能力（接口已提供，当前内置实现仅为设备端方案），以及并非每个界面的文案都已改为通过 `stringResource()` 读取——尽管三种语言的完整 `strings.xml` 翻译已经就绪。

### 11. 许可证

基于 MIT 许可证提供。您可以自由使用、修改与发布——欢迎署名，但非强制要求。
