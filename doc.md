Here's a comprehensive prompt you can use with an AI coding agent (such as ChatGPT, Claude, Gemini, Cursor, Windsurf, etc.) to generate a production-ready Kotlin Multiplatform expense tracker application.

---

# Money Tracker - Kotlin Multiplatform (Offline First)

## Project Goal

Build a **production-ready Kotlin Multiplatform (KMP)** application that works completely **offline**.

The application should help users manage their personal finances by tracking:

* Income
* Expenses
* Savings
* Budget
* Categories
* Statistics
* Reports

The application should have a beautiful modern UI with smooth animations, colorful charts, circular progress indicators, Material 3 design, and should feel like a premium finance application.

---

# Technology Stack

Use only modern technologies.

## Kotlin Multiplatform

* Kotlin 2.x
* Compose Multiplatform
* Material 3
* Navigation Safe Args with data class not string routes Compose
* Kotlinx Serialization
* Coroutines
* Flow
* StateFlow
* MVVM
* Clean Architecture
* Repository Pattern
* UseCases
* Dependency Injection (Koin preferred)
*  Room KMP
* DataStore Preferences
* Kotlin DateTime

Project structure should be modular.

```
shared
    core
    database
    domain
    data
    ui

androidApp

iosApp

```

Everything must work offline.

No Firebase.

No Backend.

No Login.

---

# Theme

Provide

* Light Theme
* Dark Theme
* Dynamic Color (Android)

Beautiful gradients.

Rounded cards.

Glassmorphism where suitable.

Premium finance dashboard.

Smooth animations.

---

# Home Dashboard

The dashboard should immediately show financial health.

Top section:

```
Current Balance

₹ 45,200
```

Below it

Three beautiful cards

Income

```
₹85,000
```

Expense

```
₹39,800
```

Savings

```
₹45,200
```

---

# Monthly Overview

Display a colorful circular progress indicator.

Example

```
Monthly Budget

₹40,000

Spent

₹28,500

Remaining

₹11,500

Progress

71%
```

Use gradient progress colors

Green

Yellow

Orange

Red

depending on spending.

---

# Category Spending

Show cards for every category.

Each category has

Icon

Color

Amount

Percentage

Progress bar

Example

```
Shopping

₹8,450

████████░░

42%
```

Categories include

* Food
* Shopping
* Grocery
* Fuel
* Home
* Beauty
* Medical
* Education
* Investment
* Bills
* Travel
* Entertainment
* Gifts
* Pets
* EMI
* Rent
* Salary
* Freelancing
* Business
* Others

User can create custom categories.

Each category has

* Color
* Icon
* Monthly total
* Yearly total

---

# Expense Screen

Show all expenses.

Each expense should contain

Icon

Category

Amount

Date

Time

Payment method

Notes

Receipt image (optional)

Example

```
Fuel

₹1,850

Today

UPI
```

Search

Filter

Sort

Grouping

By

* Day
* Week
* Month
* Year

---

# Income Screen

Track

Salary

Freelance

Business

Investment

Rental Income

Bonus

Interest

Gift

Other

Same filtering options.

---

# Statistics Screen

This should be the strongest feature.

Include

## Daily Spending

Graph

Last 7 days

Last 30 days

Last 90 days

---

## Weekly Spending

Bar Chart

---

## Monthly Spending

Line Chart

---

## Yearly Spending

Bar Chart

---

## Income vs Expense

Beautiful dual line chart.

---

## Savings Trend

Area chart.

---

## Expense Distribution

Pie Chart

Donut Chart

Category percentages

Example

```
Shopping

32%

Fuel

18%

Home

14%

Food

12%
```

---

# Reports

Generate

Daily Report

Weekly Report

Monthly Report

Yearly Report

Each report should display

Total Income

Total Expense

Total Savings

Highest Expense Category

Lowest Expense Category

Highest Single Expense

Average Daily Spend

Average Monthly Spend

Total Transactions

Budget Utilization

---

# Analytics

Show

Most expensive month

Most profitable month

Highest earning day

Highest spending day

Longest saving streak

Largest transaction

Average income

Average expense

Average savings

Monthly comparison

Year comparison

---

# Calendar View

Tap any day.

See

Income

Expense

Balance

Notes

Transactions

---

# Budget Feature

User can create monthly budgets.

Example

```
Shopping

₹10,000
```

Fuel

```
₹5,000
```

Food

```
₹7,000
```

Show progress.

```
78%

█████████░
```

If budget exceeds

Progress becomes

Orange

Then

Red

---

# Transaction Screen

Each transaction contains

* Amount
* Category
* Type
* Date
* Time
* Payment Method
* Note
* Image
* Location (optional)

Actions

Add

Edit

Delete

Duplicate

Favorite

---

# Filters

Date Range

Category

Income

Expense

Amount

Payment Method

Tags

---

# Search

Search by

Amount

Category

Note

Payment

Date

---

# Payment Methods

Cash

UPI

Card

Bank

Wallet

Cheque

Other

---

# Dashboard Widgets

Cards

```
Today's Expense

Today's Income

This Week Expense

This Month Expense

Current Savings

Upcoming Bills

Budget Remaining
```

---

# Goals

Savings Goal

Example

```
Buy Car

₹5,00,000

Current

₹2,10,000

42%

████████░░
```

---

# Insights

Generate insights automatically.

Examples

```
You spent 18% more than last month.

Fuel spending increased by 12%.

Shopping decreased by 8%.

You saved ₹5,600 more this month.

Home expenses are your biggest spending category.
```

---

# Charts

Use colorful animated charts.

Include

* Pie Chart
* Donut Chart
* Line Chart
* Area Chart
* Bar Chart
* Stacked Bar Chart
* Horizontal Bar Chart
* Circular Progress Charts
* Radial Indicators

Use gradients.

Smooth animations.

---

# Beautiful Progress Indicators

Every screen should use colorful progress.

Examples

Circular

```
█████████░

85%
```

Linear

```
████████░░

72%
```

Gradient

Green

Blue

Purple

Orange

Pink

Red

---

# Settings

Currency

Theme

Dark Mode

Backup

Restore

Export

Import

Language

First Day of Week

Notifications (future)

---

# Data Export

CSV

Excel

JSON

PDF

---

# Backup

Local backup only.

Restore from local file.

---

# Performance

Support

100,000+ transactions

Fast filtering

Pagination

Lazy loading

Efficient database queries

---

# Architecture

Implement full Clean Architecture.

```
Presentation

MVVM

↓

Use Cases

↓

Repository

↓

SQLDelight

↓

Local Database
```

Every feature must have

* UI
* ViewModel
* State
* Event
* Effect
* UseCases
* Repository
* DAO
* Mapper
* Models

---

# UX

Include

* Swipe actions
* Pull to refresh (reload from local DB)
* Empty states
* Loading animations
* Smooth transitions
* Floating Action Button
* Bottom Navigation
* Adaptive layouts
* Tablet support
* Desktop responsive layout

---

# Deliverables

Generate:

* Complete KMP project structure
* Database schema
* SQLDelight models
* Repository layer
* Domain models
* UseCases
* ViewModels
* Navigation graph
* Material 3 Compose UI
* Reusable UI components
* Color system
* Typography
* Charts
* Animated progress indicators
* Sample data
* Unit tests for business logic
* A scalable, maintainable codebase following Kotlin Multiplatform and Clean Architecture best practices.

The generated code should be production-ready, fully offline, modular, and optimized for performance and maintainability, with a polished, premium user experience comparable to modern personal finance apps.
