<div dir="rtl">

# 📚 HS+ אפליקציית סיכומים לבגרות

<p align="center">
  <img src="app/src/main/res/drawable/applogo.png" width="120" alt="App Logo"/>
</p>

## 🎯 מה זה?

> 🎓 **פרויקט בגרות במדעי המחשב** | ציון סופי: **💯 100**

אפליקציית אנדרואיד לשיתוף **סיכומים לבגרות** בין תלמידי תיכון בישראל! 🇮🇱

תלמידים יכולים:
- 📝 **להעלות סיכומים** שהם כתבו לכל מקצוע
- 📖 **לצפות בסיכומים** של תלמידים אחרים
- ❤️ **לתת לייקים** לסיכומים טובים
- 🔔 **לקבל התראות** כשהסיכום שלך מקבל 5+ לייקים
- 👤 **לנהל פרופיל** עם תמונה וכיתה

---

## 🏗️ מבנה האפליקציה

### 📱 מסכים (Activities)

| מסך | תיאור |
|-----|-------|
| `LoadingActivity` | מסך טעינה ראשוני |
| `MainActivity` | מסך התחברות (Login) |
| `RegisterActivity` | מסך הרשמה למשתמשים חדשים |
| `HomepageActivity` | דף הבית הראשי |
| `SummariesSubjectsActivity` | רשימת כל המקצועות |
| `ViewSummariesOnSubjectActivity` | צפייה בסיכומים לפי מקצוע |
| `ViewSummaryActivity` | צפייה בסיכום בודד (כולל PDF) |
| `AddSummaryActivity` | העלאת סיכום חדש |
| `EditSummaryActivity` | עריכת סיכום קיים |
| `ProfileActivity` | צפייה בפרופיל משתמש |
| `SettingsUserActivity` | הגדרות המשתמש |

### 🗂️ מודלים (Data Models)

| מודל | תיאור |
|------|-------|
| `User` | משתמש - שם, אימייל, סיסמה, כיתה, תמונת פרופיל |
| `Summary` | סיכום - כותרת, תיאור, מקצוע, לייקים, קובץ PDF |
| `Subject` | מקצוע - שם המקצוע ורשימת הסיכומים |

### 🔥 Firebase

האפליקציה משתמשת ב:
- **Firebase Realtime Database** - לשמירת משתמשים וסיכומים
- **Firebase Storage** - לאחסון קבצי PDF ותמונות פרופיל

---

## 🚀 איך להריץ?

### דרישות מקדימות
- ✅ Android Studio (Hedgehog או חדש יותר)
- ✅ Java 21
- ✅ טלפון/אמולטור עם Android 6.0+ (API 23+)

### שלבים

```bash
# 1. שכפול הפרויקט
git clone https://github.com/EladBG-code/SchoolProject.git

# 2. פתיחה ב-Android Studio
# File → Open → בחר את התיקייה

# 3. סנכרון Gradle
# File → Sync Project with Gradle Files

# 4. הרצה
# Run → Run 'app' או Shift+F10
```

---

## 📋 דרישות מערכת

| רכיב | גרסה |
|------|------|
| Gradle | 8.5 |
| Android Gradle Plugin | 8.2.2 |
| compileSdk | 34 |
| minSdk | 23 (Android 6.0) |
| targetSdk | 34 (Android 14) |
| Java | 21 |

---

</div>

---

# 📚 HS+ Bagrut Summary Sharing App

<p align="center">
  <img src="app/src/main/res/drawable/applogo.png" width="120" alt="App Logo"/>
</p>

## 🎯 What is this?

> 🎓 **Computer Science Bagrut Project** | Final Score: **💯 100/100**

An Android app for sharing **Bagrut (Israeli matriculation exam) summaries** among high school students! 🇮🇱

Students can:
- 📝 **Upload summaries** they wrote for any subject
- 📖 **View summaries** from other students
- ❤️ **Like** good summaries
- 🔔 **Get notifications** when your summary gets 5+ likes
- 👤 **Manage profile** with photo and grade level

---

## 🏗️ App Architecture

### 📱 Screens (Activities)

| Screen | Description |
|--------|-------------|
| `LoadingActivity` | Initial loading screen |
| `MainActivity` | Login screen |
| `RegisterActivity` | New user registration |
| `HomepageActivity` | Main home page |
| `SummariesSubjectsActivity` | List of all subjects |
| `ViewSummariesOnSubjectActivity` | View summaries by subject |
| `ViewSummaryActivity` | View single summary (with PDF) |
| `AddSummaryActivity` | Upload new summary |
| `EditSummaryActivity` | Edit existing summary |
| `ProfileActivity` | View user profile |
| `SettingsUserActivity` | User settings |

### 🗂️ Data Models

| Model | Description |
|-------|-------------|
| `User` | User - name, email, password, grade, profile picture |
| `Summary` | Summary - title, description, subject, likes, PDF file |
| `Subject` | Subject - name and list of summaries |

### 🔥 Firebase Integration

The app uses:
- **Firebase Realtime Database** - For storing users and summaries
- **Firebase Storage** - For storing PDF files and profile pictures

---

## 🚀 How to Run?

### Prerequisites
- ✅ Android Studio (Hedgehog or newer)
- ✅ Java 21
- ✅ Phone/Emulator with Android 6.0+ (API 23+)

### Steps

```bash
# 1. Clone the repository
git clone https://github.com/EladBG-code/SchoolProject.git

# 2. Open in Android Studio
# File → Open → Select the folder

# 3. Sync Gradle
# File → Sync Project with Gradle Files

# 4. Run
# Run → Run 'app' or Shift+F10
```

---

## 📋 System Requirements

| Component | Version |
|-----------|---------|
| Gradle | 8.5 |
| Android Gradle Plugin | 8.2.2 |
| compileSdk | 34 |
| minSdk | 23 (Android 6.0) |
| targetSdk | 34 (Android 14) |
| Java | 21 |

---

## 📁 Project Structure

```
SchoolProject/
├── app/
│   ├── src/main/
│   │   ├── java/com/theproject/schoolproject/
│   │   │   ├── MainActivity.java          # Login
│   │   │   ├── RegisterActivity.java      # Registration
│   │   │   ├── HomepageActivity.java      # Home
│   │   │   ├── SummariesSubjectsActivity.java
│   │   │   ├── ViewSummariesOnSubjectActivity.java
│   │   │   ├── ViewSummaryActivity.java
│   │   │   ├── AddSummaryActivity.java
│   │   │   ├── EditSummaryActivity.java
│   │   │   ├── ProfileActivity.java
│   │   │   ├── SettingsUserActivity.java
│   │   │   ├── User.java                  # User model
│   │   │   ├── Summary.java               # Summary model
│   │   │   ├── Subject.java               # Subject model
│   │   │   ├── NotificationService.java   # Like notifications
│   │   │   └── GlobalAcross.java          # Global state
│   │   ├── res/
│   │   │   ├── layout/                    # XML layouts
│   │   │   └── drawable/                  # Images & icons
│   │   └── AndroidManifest.xml
│   └── build.gradle
├── build.gradle
├── settings.gradle
└── README.md
```

---

## 🛠️ Tech Stack

| Technology | Purpose |
|------------|---------|
| **Java** | Primary language |
| **Firebase Realtime Database** | Cloud database |
| **Firebase Storage** | File storage |
| **Firebase UI** | RecyclerView adapters |
| **Material Design** | UI components |
| **PDF Viewer** | Display PDF summaries |

---

## 👨‍💻 Author

Created by **Elad BG** 🎓

---

## 📄 License

This project is for educational purposes.

