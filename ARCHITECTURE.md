# Kharcha Tracker - Architecture Documentation

## Tech Stack

### Core Technologies
- **Kotlin 2.1.21** - Language
- **Kotlin Multiplatform** - Cross-platform foundation (Android + iOS)
- **Compose Multiplatform 1.8.2** - UI framework
- **Material 3** - Design system

### Data Layer
- **Room 2.7.1** - Local database (KMP)
- **DataStore Preferences** - Key-value preferences storage
- **Kotlinx Serialization** - JSON serialization
- **Kotlinx DateTime** - Date/time handling
- **Kotlinx Coroutines** - Asynchronous programming

### Dependency Injection
- **Koin 4.0.4** - Dependency injection (`koin-core`, `koin-compose`, `koin-compose-viewmodel`)

### Navigation
- **Jetpack Navigation Compose** - Type-safe navigation using `@Serializable` data objects/classes

## Project Structure

```
shared/
├── App.kt                    # Compose entry point (calls MainNavigation)
├── Platform.kt               # Platform interface
├── core/
│   ├── data/
│   │   ├── DefaultCategories.kt   # 26 built-in categories
│   │   └── SeedDataManager.kt     # First-launch seeding logic
│   └── util/
│       ├── CurrencyFormatter.kt
│       └── DateTimeUtils.kt
├── database/
│   ├── TrackerDatabase.kt
│   ├── TrackerDatabaseConstructor.kt
│   ├── dao/
│   │   ├── BudgetDao.kt
│   │   ├── CategoryDao.kt
│   │   ├── SavingsGoalDao.kt
│   │   └── TransactionDao.kt
│   └── entity/
│       ├── BudgetEntity.kt
│       ├── CategoryEntity.kt
│       ├── SavingsGoalEntity.kt
│       └── TransactionEntity.kt
├── domain/
│   ├── model/
│   │   ├── Budget.kt
│   │   ├── Category.kt
│   │   ├── CategoryIcon.kt
│   │   ├── FinancialSummary.kt
│   │   ├── PaymentMethod.kt
│   │   ├── SavingsGoal.kt
│   │   ├── Transaction.kt
│   │   └── TransactionType.kt
│   ├── repository/
│   │   ├── BudgetRepository.kt
│   │   ├── CategoryRepository.kt
│   │   ├── SavingsGoalRepository.kt
│   │   └── TransactionRepository.kt
│   └── usecase/
│       ├── budget/
│       │   ├── AddBudgetUseCase.kt
│       │   └── GetBudgetsWithSpendingUseCase.kt
│       ├── goals/
│       │   ├── AddContributionUseCase.kt
│       │   ├── AddSavingsGoalUseCase.kt
│       │   ├── DeleteSavingsGoalUseCase.kt
│       │   ├── GetSavingsGoalsUseCase.kt
│       │   └── UpdateSavingsGoalUseCase.kt
│       ├── summary/
│       │   ├── GetCategorySummaryUseCase.kt
│       │   └── GetFinancialSummaryUseCase.kt
│       └── transaction/
│           ├── AddTransactionUseCase.kt
│           ├── DeleteTransactionUseCase.kt
│           ├── GetTransactionsUseCase.kt
│           └── UpdateTransactionUseCase.kt
├── data/
│   ├── mapper/
│   │   ├── BudgetMapper.kt
│   │   ├── CategoryMapper.kt
│   │   ├── SavingsGoalMapper.kt
│   │   └── TransactionMapper.kt
│   └── repository/
│       ├── BudgetRepositoryImpl.kt
│       ├── CategoryRepositoryImpl.kt
│       ├── SavingsGoalRepositoryImpl.kt
│       └── TransactionRepositoryImpl.kt
├── di/
│   ├── AppModule.kt
│   ├── DatabaseModule.kt
│   ├── RepositoryModule.kt
│   ├── UseCaseModule.kt
│   └── ViewModelModule.kt
└── ui/
    ├── components/
    │   ├── CategoryIcon.kt
    │   ├── Charts.kt
    │   ├── FinancialCard.kt
    │   ├── ProgressIndicators.kt
    │   └── TransactionItem.kt
    ├── theme/
    │   ├── Color.kt
    │   ├── Theme.kt
    │   └── Type.kt
    ├── navigation/
    │   ├── MainNavigation.kt
    │   └── NavRoutes.kt
    └── screens/
        ├── home/
        ├── transactions/
        ├── addtransaction/
        ├── budget/
        ├── statistics/
        └── goals/

androidApp/
├── MainActivity.kt            # Activity entry point
└── TrackerApplication.kt      # Koin setup for Android

iosApp/
└── MainViewController.kt      # iOS entry point with Koin setup
```

## Architecture Layers

### 1. Presentation Layer (UI)
- **Pattern**: MVVM
- **Components**:
  - Composables (screens and reusable UI)
  - ViewModels (StateFlow-based state management)
  - UiState data classes per screen
- **Tools**: Compose Multiplatform, StateFlow, `koin-compose-viewmodel`

### 2. Domain Layer
- **Models**: Pure Kotlin data classes — `Transaction`, `Category`, `Budget`, `SavingsGoal`, `FinancialSummary`, `CategoryIcon`, `TransactionType`, `PaymentMethod`
- **Use Cases**: Single-responsibility business logic (15 use cases across 4 groups)
- **Repository Interfaces**: Abstractions over data sources

### 3. Data Layer
- **Repositories**: Implement domain interfaces using DAOs
- **Mappers**: Bidirectional Entity ↔ Domain model conversion
- **Entities**: Room-annotated database models

### 4. Database Layer
- **Room Database**: SQLite with full KMP support
- **DAOs**: Type-safe suspend/Flow queries
- **KSP**: Code generation for Android, iosArm64, iosSimulatorArm64

## Key Features

### 1. Home Dashboard
- Current balance display
- Income/Expense/Savings summary cards
- Monthly budget progress indicator
- Category breakdown
- Recent transactions list
- AI-like financial insights

### 2. Transactions
- Full transaction list
- Filter by type (Income / Expense / All)
- Search by note or amount
- Sort by date or amount
- Swipe-to-delete/edit actions
- Favorite toggle per transaction

### 3. Add/Edit Transaction
- Amount input with numeric keyboard
- Category selection grid
- Type toggle (Income / Expense)
- Payment method selector
- Notes field
- Date/time picker
- Favorite toggle

### 4. Budget Management
- Set monthly budgets per category
- Overall budget circular progress indicator
- Per-category budget tracking with linear progress bars
- Color-coded progress (Green → Yellow → Orange → Red)
- Over-budget warnings

### 5. Statistics & Charts
- Daily spending bar chart
- Category breakdown donut chart
- Period selector (7 days / 30 days / year)
- Income vs Expense comparison
- Category-level summaries

### 6. Savings Goals ✅
- Create goals with name, target amount, icon, color, and optional deadline
- Track progress with animated circular progress indicators
- Add contributions to a goal
- Active / Completed goal partitioning
- Summary card with total saved and overall progress
- Edit and delete goals

## UI Components

### Reusable Components
- **AnimatedCircularProgress** (`ProgressIndicators.kt`): Gradient circular progress indicator
- **AnimatedLinearProgress** (`ProgressIndicators.kt`): Gradient linear progress bar
- **GradientCard** (`FinancialCard.kt`): Card with a linear gradient background
- **SummaryMiniCard** (`FinancialCard.kt`): Income/Expense/Savings mini cards
- **GlassCard** (`FinancialCard.kt`): Semi-transparent glass-effect card
- **AppCard** (`FinancialCard.kt`): Standard card with padding
- **TransactionItem** (`TransactionItem.kt`): Transaction list row
- **CategoryIcon** (`CategoryIcon.kt`): Renders icons for categories
- **DonutChart** (`Charts.kt`): Animated donut/pie chart
- **BarChart** (`Charts.kt`): Vertical bar chart
- **LineChart** (`Charts.kt`): Line chart with gradient fill
- **ChartLegend** (`Charts.kt`): Color legend for charts

### Theme System
- **Light/Dark Mode**: Automatic theme switching via `Theme.kt`
- **Extended Colors**: Income green, expense red, savings blue
- **Progress Colors**: Green → Yellow → Orange → Red based on budget usage
- **Gradient Pairs**: Pre-defined gradient combinations per category/feature
- **Material 3**: Full Material Design 3 implementation

## Navigation

Type-safe navigation using `@Serializable` data objects/classes defined in `NavRoutes.kt`.

### Bottom Navigation (5 tabs)
| Tab | Route | ViewModel |
|-----|-------|-----------|
| Home | `HomeRoute` | `HomeViewModel` |
| Transactions | `TransactionsRoute` | `TransactionsViewModel` |
| Statistics | `StatisticsRoute` | `StatisticsViewModel` |
| Budget | `BudgetRoute` | `BudgetViewModel` |
| Goals | `GoalsRoute` | `GoalsViewModel` |

### Full-screen Routes
| Route | Description |
|-------|-------------|
| `AddEditTransactionRoute(transactionId: Long)` | Add or edit a transaction |

### Declared (Not Yet Wired)
Routes defined in `NavRoutes.kt` but not connected to a screen yet:
- `SettingsRoute`
- `AddEditCategoryRoute`
- `CategoryDetailRoute(categoryId: Long)`
- `AddEditGoalRoute(goalId: Long)`
- `CalendarRoute`
- `ReportsRoute`

## Data Flow

```
UI (Composable)
    ↓ collectAsState
ViewModel (StateFlow / UiState)
    ↓ invoke
UseCase
    ↓ call
Repository interface
    ↓ delegate
RepositoryImpl
    ↓ query
DAO (Room)
    ↓
SQLite Database
```

## Dependency Injection

### Koin Modules (all in `shared/di/`)
| Module | Provides |
|--------|----------|
| `DatabaseModule` | `TrackerDatabase`, all DAOs; platform-specific DB builder |
| `RepositoryModule` | All 4 `RepositoryImpl` bindings |
| `UseCaseModule` | All 15 use cases as `factory` |
| `ViewModelModule` | All 6 `ViewModel` factories via `viewModelOf` |
| `AppModule` | `SeedDataManager` |

### Platform-Specific DI
- **Android** (`TrackerApplication.kt`): Starts Koin with `androidContext`, provides context-aware DB builder
- **iOS** (`MainViewController.kt`): Starts Koin with iOS-specific DB builder

## Database Schema

### Tables
1. **categories**: `id`, `name`, `icon`, `colorHex`, `type`, `isDefault`, `isArchived`
2. **transactions**: `id`, `amount`, `type`, `categoryId`, `note`, `dateTime`, `paymentMethod`, `imagePath`, `isFavorite`, `tags`
3. **budgets**: `id`, `categoryId`, `amount`, `month`, `year`
4. **savings_goals**: `id`, `name`, `targetAmount`, `currentAmount`, `colorHex`, `icon`, `deadline`

### Relationships
- `transactions.categoryId` → `categories.id` (foreign key)
- `budgets.categoryId` → `categories.id` (foreign key)

## Offline-First

All features work 100% offline:
- ✅ Local SQLite database (Room)
- ✅ No network calls
- ✅ No Firebase
- ✅ No backend
- ✅ No authentication

## Sample Data

On first launch, `SeedDataManager` seeds:
- **26 default categories** (20 expense, 6 income) with icons and colors
- **5 sample transactions** to populate the home screen

## Platform Targets

| Platform | Target | Entry Point |
|----------|--------|-------------|
| Android | `androidLibrary` | `TrackerApplication.kt` + `MainActivity.kt` |
| iOS (device) | `iosArm64` | `MainViewController.kt` |
| iOS (simulator) | `iosSimulatorArm64` | `MainViewController.kt` |

> Desktop is **not** a configured build target.

## Performance

- **Lazy loading**: `LazyColumn` for transaction and goal lists
- **Flow-based**: Reactive Room queries push updates automatically
- **Indexed queries**: Room entities use indexed columns for fast lookups
- **Smooth animations**: 60 FPS compose animations throughout

## Build & Run

### Android
```bash
./gradlew :androidApp:installDebug
```

### iOS
Open `iosApp/iosApp.xcodeproj` in Xcode and run.

## Testing

- Unit tests for ViewModels
- Use case tests with fake repositories
- Repository tests with in-memory DAOs

## Future Enhancements

Routes are already declared in `NavRoutes.kt` for these planned features:
- [ ] Calendar view (`CalendarRoute`)
- [ ] Reports / PDF/CSV export (`ReportsRoute`)
- [ ] Category management (`AddEditCategoryRoute`, `CategoryDetailRoute`)
- [ ] Add/Edit goal dedicated screen (`AddEditGoalRoute`)

Other potential enhancements:
- [ ] Recurring transactions
- [ ] Multi-currency support
- [ ] Biometric authentication
- [ ] Backup / restore
- [ ] Widgets
- [ ] Push notifications
