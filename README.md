# Society Manager

A modern Android application designed for residential societies and gated communities to streamline notices, maintenance tickets, amenity reservations, and member profiles with role-based access control.

Developed as a technical submission for **Horizon Broadband**.

---

## Project Overview

**Society Manager** simplifies communication and daily workflows within a residential society (Horizon Heights Apartments). It provides tailored experiences for two primary personas:
1. **Residents**: View official announcements, raise maintenance tickets/complaints with status tracking, reserve society amenities (Clubhouse, Pool, Badminton Court, Gym), and manage their apartment profile.
2. **Society Administrators**: Publish circulars and event notices, review resident complaints, update ticket progress with administrative notes, and oversee amenity bookings.

---

## Features Implemented

- **Resident & Admin Login**:
  - Pre-configured demo accounts for both Resident and Society Administrator roles.
  - Form validation with inline error feedback.
  - 1-Tap quick login buttons for rapid switching and review.
  - In-memory reactive session management.

- **Interactive Dashboard**:
  - Personalized welcome banner showing resident flat number, block, and role tag.
  - Live metric counters for notices, open complaints, and facility reservations.
  - Quick action shortcuts to primary tasks.
  - Recent announcements preview feed.

- **Announcements & Events**:
  - Filterable feed (*All*, *Notices*, *Events*) with instant search.
  - Full details view displaying published date, author, venue, and priority badges.
  - Admin-only publication form with type, urgency, date, and venue selection.

- **Complaint Management (Helpdesk)**:
  - Multi-category support (*Plumbing*, *Electrical*, *Security*, *Maintenance*, *Noise*, *Other*).
  - Priority indicator (*Low*, *Medium*, *High*).
  - Status progression lifecycle: `OPEN` ➔ `IN_PROGRESS` ➔ `RESOLVED`.
  - Visual 3-step timeline tracker for residents.
  - Admin status management dialog with resolution note logging.
  - Role-based filtering (residents see their own tickets; admins see society-wide tickets).

- **Facility & Amenity Booking**:
  - Interactive reservations for 4 facilities: *Clubhouse Multi-Purpose Hall*, *Olympic Swimming Pool*, *Indoor Badminton Court*, and *Gymnasium*.
  - Date picker (*Today*, *Tomorrow*, *Upcoming*).
  - 2-hour fixed time slots with real-time conflict checking (already booked slots are disabled).
  - Active reservation summary with 1-tap cancellation support.

- **Profile & Society Information**:
  - Resident/Admin details including flat number, phone, email, and block.
  - Society association details and emergency security contacts.
  - Sign-out with confirmation dialog that clears active session state.

- **Role-Based Access Control**:
  - Dynamic UI adjustments based on logged-in role (`RESIDENT` vs `ADMIN`).

---

## Technology Stack

- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Declarative UI)
- **Design System**: Material 3 (Tonal elevation, Dynamic status chips, Color palettes)
- **Architecture**: MVVM (Model-View-ViewModel) + Repository Pattern
- **Dependency Injection**: Dagger Hilt
- **State Management**: Kotlin Coroutines & `StateFlow`
- **Navigation**: Navigation Compose (`NavHost`, typed routes, back stack management)
- **Testing**: JUnit 4, Kotlinx Coroutines Test, MockK, Turbine

---

## Architecture

The application adheres to clean MVVM and Repository patterns:

```
┌─────────────────────────────────────────────────────────┐
│                    Jetpack Compose UI                   │
│   (Screens, Components, BottomBar, Tonal M3 Cards)      │
└───────────────────────────▲─────────────────────────────┘
                            │ Collects StateFlow / Events
┌───────────────────────────┴─────────────────────────────┐
│                        ViewModel                        │
│   (StateFlow<UiState>, Form Validation, UI Logic)       │
└───────────────────────────▲─────────────────────────────┘
                            │ Calls Suspend Functions / Flows
┌───────────────────────────┴─────────────────────────────┐
│                       Repository                        │
│   (AuthRepository, ComplaintRepository, BookingRepo)    │
└───────────────────────────▲─────────────────────────────┘
                            │ Reads / Updates State
┌───────────────────────────┴─────────────────────────────┐
│                 In-Memory Local Data Store              │
│       (MutableStateFlow, Thread-Safe Memory State)      │
└─────────────────────────────────────────────────────────┘
```

### Why MVVM and Repository Pattern?
1. **Separation of Concerns**: UI rendering is decoupled from business logic and state storage.
2. **Predictable Unidirectional Data Flow**: ViewModels expose immutable `StateFlow<UiState>` collected by Composables, guaranteeing deterministic state across recompositions.
3. **Testability**: Repositories and ViewModels can be tested in isolation using standard JUnit tests without Android framework dependencies.
4. **Maintainability & Scalability**: The in-memory data layer can easily be replaced by Room database or a remote REST API/Firebase in the future without modifying ViewModels or Composables.

---

## Project Structure

```
com.horizon.societymanager
├── core
│   ├── di
│   │   └── AppModule.kt             # Hilt Dependency Injection module
│   ├── navigation
│   │   ├── Screen.kt                # Sealed navigation routes & bottom nav items
│   │   ├── BottomNavBar.kt          # Material 3 bottom navigation bar
│   │   └── AppNavHost.kt            # NavHost graph with transitions
│   ├── session
│   │   └── SessionManager.kt        # Reactive user session manager
│   └── ui
│       └── Components.kt            # Reusable M3 TopBars, badges, stat cards & state views
├── data
│   ├── model
│   │   ├── User.kt                  # User & UserRole models
│   │   ├── Announcement.kt          # Announcement & AnnouncementType models
│   │   ├── Complaint.kt             # Complaint, Category, Status & Priority models
│   │   └── Facility.kt              # Facility, TimeSlot & Booking models
│   └── repository
│       ├── AuthRepository.kt        # Authentication & credentials repository
│       ├── AnnouncementRepository.kt# Announcement state & publishing repository
│       ├── ComplaintRepository.kt   # Complaints & resolution repository
│       └── BookingRepository.kt     # Amenities reservation repository
├── feature
│   ├── auth
│   │   ├── LoginViewModel.kt        # Login validation & auth state
│   │   └── LoginScreen.kt           # Sign-in UI with 1-tap demo access
│   ├── dashboard
│   │   ├── DashboardViewModel.kt    # Aggregated metrics state
│   │   └── DashboardScreen.kt       # Welcome banner, stats & quick actions
│   ├── announcement
│   │   ├── AnnouncementViewModel.kt # Filtering, search & creation logic
│   │   ├── AnnouncementListScreen.kt# Filterable notices list
│   │   ├── AnnouncementDetailScreen.kt# Announcement details view
│   │   └── AddAnnouncementScreen.kt # Admin publishing form
│   ├── complaint
│   │   ├── ComplaintViewModel.kt    # Ticket lifecycle & filtering
│   │   ├── ComplaintListScreen.kt   # Complaint helpdesk list
│   │   ├── ComplaintDetailScreen.kt # Progress stepper & admin resolution
│   │   └── CreateComplaintScreen.kt # Resident ticket filing form
│   ├── booking
│   │   ├── BookingViewModel.kt      # Facility slot booking & conflict resolution
│   │   └── FacilityBookingScreen.kt # Reservation UI & cancellation
│   └── profile
│       ├── ProfileViewModel.kt      # Profile metrics & logout
│       └── ProfileScreen.kt         # Account details & society info
├── ui
│   └── theme
│       ├── Color.kt                 # Horizon Navy, Teal & Status colors
│       ├── Theme.kt                 # M3 Light & Dark themes
│       └── Type.kt                  # Typography definitions
├── MainActivity.kt                  # Root Activity hosting AppNavHost
└── SocietyManagerApp.kt             # Application class with @HiltAndroidApp
```

---

## Application Flow

```
[ Launch App ]
      │
      ▼
┌──────────────┐
│ Login Screen │ ◄─── (Resident / Admin Credentials or 1-Tap Access)
└──────┬───────┘
       │ On Successful Login
       ▼
┌─────────────────────────────────────────────────────────────┐
│                      Main App Scaffold                      │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────┐  │
│  │  Dashboard   │  │ Announcements│  │    Complaints    │  │
│  │   (Home)     │  │   (Notices)  │  │    (Helpdesk)    │  │
│  └──────┬───────┘  └──────┬───────┘  └────────┬─────────┘  │
│         │                 │                   │             │
│         ├─ Quick Actions  ├─ Detail View      ├─ Detail/Admin
│         │                 └─ Admin Publish    └─ Raise Ticket
│         ▼                                                   │
│  ┌──────────────┐  ┌──────────────┐                         │
│  │ Facility     │  │   Profile    │                         │
│  │ Booking      │  │ & Sign Out   │ ────────────────────────┘
│  └──────────────┘  └──────┬───────┘
│                           │ (Sign Out)
└───────────────────────────┼─────────────────────────────────┘
                            ▼
                     [ Login Screen ]
```

---

## Demo Credentials

| Role | Email | Password | Scope / Permissions |
| :--- | :--- | :--- | :--- |
| **Resident** | `resident@horizon.com` | `resident123` | Flat A-402, view notices, raise complaints, book amenities |
| **Admin** | `admin@horizon.com` | `admin123` | Admin Office, publish notices, resolve complaints, view all bookings |

> **Note**: You can also tap the **Resident** or **Admin** quick-login chips on the login screen to sign in immediately.

---

## Setup Instructions

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 or 21 (bundled with Android Studio JBR)
- Android SDK 34 (Android 14)

### Clone & Run

1. **Clone the repository**:
   ```bash
   git clone https://github.com/poorani2406/society-manager.git
   cd society-manager
   ```

2. **Open in Android Studio**:
   - Launch Android Studio.
   - Click **Open** and select the `society-manager` directory.
   - Allow Gradle to sync dependencies.

3. **Run on Device / Emulator**:
   - Select an emulator or connected physical Android device (API 26+).
   - Click the green **Run** button (`Shift + F10`) or run:
     ```bash
     ./gradlew assembleDebug
     ```

---

## Testing

The project includes verified unit tests covering core repositories and business logic:

| Test Class | Scope Tested | Status |
| :--- | :--- | :--- |
| `AuthRepositoryTest` | Resident/Admin authentication, credential validation, session state, logout | Passed |
| `ComplaintRepositoryTest` | Ticket creation, priority setting, admin status updates, note logging | Passed |
| `BookingRepositoryTest` | Facility slot reservation, slot conflict prevention, booking cancellation | Passed |
| `ExampleUnitTest` | Baseline JVM test sanity | Passed |

Run unit tests via command line:
```bash
./gradlew testDebugUnitTest
```

---

## Screenshots

*(Screenshots can be added here)*

| Login Screen | Dashboard | Announcements |
|:---:|:---:|:---:|
| *(Add Login Screenshot)* | *(Add Dashboard Screenshot)* | *(Add Notices Screenshot)* |

| Helpdesk / Complaints | Ticket Detail & Status | Facility Reservations |
|:---:|:---:|:---:|
| *(Add Helpdesk Screenshot)* | *(Add Status Workflow Screenshot)* | *(Add Bookings Screenshot)* |

---

## Known Limitations

- **In-Memory Mock Data**: Data is maintained in-memory via reactive `StateFlow` stores. Changes (new tickets, newly booked slots, new announcements) persist across navigation during a session, but reset when the application process is terminated.
- **Mock Authentication**: Authentication validates against demo credentials locally rather than verifying JWT tokens against a remote identity provider.

---

## Future Improvements

- **Persistent Local Database**: Integration with Android Room for offline caching.
- **Backend API / Cloud Integration**: REST API or Firebase Cloud Firestore integration for real-time multi-user synchronization.
- **Push Notifications**: Firebase Cloud Messaging (FCM) for critical announcements and ticket status updates.
- **Maintenance Fee Payments**: Payment gateway integration (Razorpay / Stripe / UPI) for society dues.
- **Visitor & Gate Management**: Digital visitor approval passes, delivery entry codes, and vehicle parking tracking.
