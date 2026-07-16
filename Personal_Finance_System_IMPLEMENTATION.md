# Personal Finance Management System — Implementation Reference

This document describes the **as-built** structure of `Personal_Finance_Management_System.xlsx`, generated with `openpyxl` (Python). It is written so another AI model (or a human) can safely read, extend, debug, or regenerate this workbook without re-deriving the cross-sheet wiring from scratch.

Source build script pattern: a single Python script using `openpyxl`, built sheet-by-sheet with shared style helpers, saved and passed through a recalculation/error-check step after every 1–3 sheets to catch broken references early.

---

## 1. Design Philosophy

- **Settings sheet is the single source of truth.** Every editable number (salary, budget ceiling, allocation %, expected returns, inflation, tax rate, ages, bike loan terms, emergency fund months, FIRE multiplier) lives in `Settings` as a **named range**. No other sheet should contain a raw hardcoded input for these — they reference the name (e.g. `=Salary`, `=PctEquityMF`).
- **Color convention (applied via font color, consistent across every sheet):**
  - **Blue text, light-yellow fill** = editable input cell.
  - **Black text** = formula the user should not overwrite.
  - **Green text** = a live cross-sheet link (formula that just pulls a value from another sheet, e.g. `=Salary` or `='Monthly Budget'!B11`).
- **Blank-safe formulas.** Template rows are intentionally left empty (no fabricated sample data) so the workbook is "clean" for the user to fill in themselves. Because of this, most formulas are wrapped in `IF(x="","",...)` guards so blank rows show blank/dash instead of 0, `#VALUE!`, or misleading statuses (e.g. an empty Annual Expense row should not show "Overdue").
- **Excel Tables (ListObjects)** are used for every repeating/unlimited-row dataset (expense categories, annual expenses, sinking funds, debt list, portfolio holdings, goals, net worth history, monthly tracker, SIP log, maturity calendar). Typing into the row immediately below a table auto-expands it and carries formulas down — this is what makes the sheet "scale for 10+ years" without editing formulas.
- **One-directional dependency chain**, no circular references:
  `Settings → Monthly Expense Tracker / Annual Expenses / Loan Calculator → Debt Payoff Tracker → Monthly Budget (Income − Outflow = Investable) → Investment Planner (applies % to Investable) → Portfolio/Goal/FIRE/Wealth sheets → Dashboard/Reports (read-only aggregation)`.
  The only cell that reads "backward" is `Monthly Budget!B26`, an independent **check** cell (`Investable − Total Invested`, should be 0) — it does not feed anything else, so it can safely reference a sheet that itself depends on Monthly Budget without creating a cycle.

---

## 2. Sheet Order (24 sheets, in this exact order)

| # | Sheet Name | Tab Color |
|---|---|---|
| 1 | Dashboard | `1F3864` |
| 2 | Settings | `2E75B6` |
| 3 | Monthly Budget | `4472C4` |
| 4 | Monthly Expense Tracker | `548235` |
| 5 | Annual & One-Time Expenses | `7030A0` |
| 6 | Sinking Funds | `C55A11` |
| 7 | Loan Calculator | `BF8F00` |
| 8 | Debt Payoff Tracker | `833C0C` |
| 9 | Investment Planner | `0F9ED5` |
| 10 | Existing Investment Portfolio | `375623` |
| 11 | Portfolio Dashboard | `9C6500` |
| 12 | Investment Maturity Calendar | `C00000` |
| 13 | SIP Tracker | `1F4E78` |
| 14 | Passive Income | `2E7D32` |
| 15 | Emergency Fund | `6A1B9A` |
| 16 | Financial Goals | `AD1457` |
| 17 | Future Planning | `00695C` |
| 18 | Net Worth | `5D4037` |
| 19 | Monthly Tracker | `3949AB` |
| 20 | Expense Analysis | `00838F` |
| 21 | Financial Calendar | `F57F17` |
| 22 | Wealth Projection | `4E342E` |
| 23 | FIRE Tracker | `37474F` |
| 24 | Reports | `263238` |

Note: `Financial Health Score` is **not** a standalone sheet — it lives inside **Reports** (rows 3–22), with a mirror/summary shown on **Dashboard**. This was a deliberate choice to keep the sheet count at exactly 24 as specified, since "Financial Health Score" was described as a feature, not listed in the numbered 24-sheet list.

---

## 3. Settings Sheet — Named Ranges (ground truth)

All named ranges point at `Settings!$B$<row>`. Row numbers are exact as built; **if you regenerate this sheet, re-derive rows programmatically rather than hardcoding**, since inserting/removing a Settings row shifts everything below it.

| Name | Cell | Meaning |
|---|---|---|
| `Salary` | B5 | Monthly in-hand salary |
| `AnnualBonus` | B6 | Annual bonus (divided by 12 where used monthly) |
| `FreelanceIncome` | B7 | Monthly freelance income |
| `RentalIncome` | B8 | Monthly rental income |
| `BusinessIncome` | B9 | Monthly business income |
| `OtherIncome` | B10 | Monthly other income |
| `MonthlyBudget` | B13 | Discretionary (Shopping/day-to-day) ceiling, default 40000 |
| `PctFDRD` | B16 | FD/RD allocation % (default 20%) |
| `PctDebtMF` | B17 | Debt MF allocation % (default 10%) |
| `PctGoldETF` | B18 | Gold ETF allocation % (default 10%) |
| `PctEquityMF` | B19 | Equity MF allocation % (default 60%) |
| `PctNifty50` | B23 | Nifty 50 % of Equity MF (default 60%) |
| `PctFlexicap` | B24 | Flexicap % of Equity MF (default 20%) |
| `PctMidcap` | B25 | Midcap % of Equity MF (default 10%) |
| `PctSmallcap` | B26 | Smallcap % of Equity MF (default 10%) |
| `ReturnEquity` | B30 | Expected annual equity return (default 12%) |
| `ReturnDebt` | B31 | Expected annual debt fund return (default 7%) |
| `ReturnGold` | B32 | Expected annual gold return (default 8%) |
| `ReturnFD` | B33 | Expected annual FD/RD return (default 7%) |
| `BlendedReturn` | B34 | Auto: weighted avg of the 4 returns above by allocation % |
| `InflationRate` | B37 | Annual inflation (default 6%) |
| `SalaryGrowth` | B38 | Annual salary growth (default 8%) |
| `TaxRate` | B39 | Approx. income tax rate, reference only (default 20%) |
| `CurrentAge` | B40 | Default 30 |
| `RetirementAge` | B41 | Default 60 |
| `EmergencyMonths` | B44 | Emergency fund target in months of expenses (default 6) |
| `FIREMultiplier` | B45 | FIRE corpus multiplier (default 25 = "4% rule") |
| `BikePrice` | B48 | On-road bike price (default 254530) |
| `BikeLoanAmt` | B49 | Bike loan principal (default 200000) |
| `BikeRate` | B50 | Bike loan annual interest rate (default 9%) |
| `BikeTenure` | B51 | Bike loan tenure in years (default 3) |

Two check rows also exist on Settings (not named): total of the 4 primary allocation % (should = 100%) and total of the 4 equity sub-allocation % (should = 100%), each with green/red conditional formatting.

---

## 4. Cross-Sheet Linkage Map (critical addresses)

These are the cells other sheets reference. **If you edit row/column structure on the source sheet, you must update every consumer below.**

| Producer Cell | Meaning | Consumed By |
|---|---|---|
| `'Monthly Expense Tracker'!B20` | Grand Total of all expense categories | `Monthly Budget!B20`, `Dashboard` |
| `'Monthly Expense Tracker'!B21` | Highest expense category (text) | `Expense Analysis!B25` |
| `'Annual & One-Time Expenses'!I19` | Total Monthly Reserve Required (all annual bills) | `Monthly Budget!B22`, `Monthly Tracker`, `Financial Calendar` |
| `'Loan Calculator'!B13` | Bike Loan EMI (`=PMT(rate/12, months, -principal)`) | `Debt Payoff Tracker!E5`, `Monthly Tracker!D*` |
| `'Loan Calculator'!B16` | Loan start date | `Debt Payoff Tracker!F5` |
| `'Debt Payoff Tracker'!E12` | Total EMI across all debts | `Monthly Budget!B21`, `Reports!C8`, `Dashboard` |
| `'Debt Payoff Tracker'!C12` | Total Outstanding Principal across all debts | `Dashboard` (Total Liabilities) |
| `'Monthly Budget'!B11` | Total Monthly Income | `Emergency Fund`, `Debt Payoff Tracker` (DTI), `Reports`, `Passive Income` |
| `'Monthly Budget'!B16` / `C16` | Discretionary Budget / Actual (auto-summed from categories) | `Reports!B5/C5` (Budget Discipline score) |
| `'Monthly Budget'!B23` | Total Monthly Outflow | `Emergency Fund!B4`, `FIRE Tracker!B4` |
| `'Monthly Budget'!B24` | Investable Amount (Income − Outflow) | `Investment Planner!B3`, `Wealth Projection!B6`, `FIRE Tracker!B9` |
| `'Monthly Budget'!B26` | Check cell (Investable − Invested, should be 0) | *(terminal — not consumed further)* |
| `'Investment Planner'!C11` | Total invested this month (should = Investable) | `Monthly Budget!B26` check, `Reports!C7`, `Dashboard` |
| `'Existing Investment Portfolio'!E22` | Total Invested (all holdings) | `Portfolio Dashboard!B5` |
| `'Existing Investment Portfolio'!F22` | Total Current Value (all holdings) | `Portfolio Dashboard!B6`, `Wealth Projection!B5`, `FIRE Tracker!B7` |
| `'Existing Investment Portfolio'!J5:J21` + `D5:D21` | XIRR cash-flow helper column + dates (negative invested amounts + one final positive "total value today" row) | `Portfolio Dashboard!B9` (`=XIRR(...)`) |
| `'Emergency Fund'!B5` / `B6` / `B8` | Target / Current / Progress % | `Future Planning!C5/D5`, `Reports!C9`, `Dashboard` |
| `'Passive Income'!B9` | Monthly Passive Income total | `Dashboard`, `Reports` |
| `'Financial Goals'!H5:H12` | Progress % range for all goals | `Reports!C10` (averaged with Future Planning) |
| `'Future Planning'!H5:H13` | Progress % range for all future goals | `Reports!C10` |
| `'Net Worth'!M16` | Latest (December) Net Worth | `Dashboard` |
| `'Reports'!B14` / `B15` | Financial Health Score / Rating text | `Dashboard!B12/B13` |
| `'Monthly Tracker'!B22` | Average Monthly Expense (forecast) | `Expense Analysis!B28` |

---

## 5. Per-Sheet Detail

### Dashboard (sheet 1)
Read-only executive view. Two-column "glance" section (rows 5–13) pulling ~12 headline metrics, then 9 charts in a 3×3 grid (row ~16 onward): Expense Breakdown (pie, from Monthly Expense Tracker), Investment Allocation (pie), Asset Allocation (pie, from Portfolio Dashboard buckets), Net Worth Growth (line), Savings Trend (line), Cash Flow Snapshot (bar, 3 small helper cells at J/K columns), Debt Reduction (bar), Goal Progress (bar), Portfolio Growth Invested-vs-Current (bar). No named ranges defined here; every cell is a same-workbook cross-sheet reference.

### Settings (sheet 2)
See §3. Structured as stacked sections (Income, Budget, Primary Allocation, Equity Sub-Allocation, Expected Returns, Rates & Personal, Emergency/FIRE, Bike Loan), each with a `section_row` banner. Two 100%-check cells with `CellIsRule` conditional formatting (green if `=1`, red otherwise).

### Monthly Budget (sheet 3)
Rows 5–10: income sources (all `=NamedRange` links) summed at B11. Row 16: single-row Budget/Actual/Saved/Overspent table — **Actual (C16) is not manually entered; it's auto-summed via 5 chained `SUMIF`s** against the Monthly Expense Tracker category column (Shopping + Entertainment + Travel + Family + Miscellaneous), so there is no duplicate data entry between the discretionary-ceiling concept and full category tracking. Rows 20–24: outflow summary (category expenses + EMI + annual reserve = total outflow; income − outflow = investable). Row 26: independent check cell.

### Monthly Expense Tracker (sheet 4)
`ExpenseTable` (A4:C19), 13 default category rows (Rent, Food, Fuel, Internet, Electricity, Mobile, Insurance, Medical, Shopping, Entertainment, Travel, Family, Miscellaneous) + 2 blank spare rows, all amounts blank. Below table: Grand Total (B20, plain `SUM`, **not** a table-total-row — kept as a plain row directly below so the table can auto-expand into it), Highest Expense Category (`INDEX/MATCH` against `MAX`), Remaining Cash (Income − Grand Total).

### Annual & One-Time Expenses (sheet 5)
`AnnualExpenseTable` (A4:L18). Columns: Expense, Category, Amount, Frequency (dropdown: Monthly/Quarterly/Half-Yearly/Yearly), Due Month, Due Date, Last Paid, Next Due, **Monthly Reserve Required**, Amount Saved, Amount Remaining, Status. 12 category-name rows prefilled (Health Insurance, LIC Premium, Vehicle Insurance, Bike Service, Pollution Certificate, Income Tax, Property Tax, Festival Expenses, Vacation, Birthdays, Gifts, Domain/Software Renewals) + 2 spare rows, all numeric/date fields blank.

**Key formula — Monthly Reserve Required (col I):**
```
=C{r}/IF(D{r}="Monthly",1,IF(D{r}="Quarterly",3,IF(D{r}="Half-Yearly",6,12)))
```
This is a **steady-state** reserve (Amount ÷ recurrence-interval-in-months) — e.g. ₹12,000 Yearly → ₹1,000/month — deliberately **not** based on days-until-next-due, because an early implementation that divided by "months until due date" caused unrealistic spikes whenever a due date happened to be only weeks away. Keep it this way.

Status formula (col L), blank-guarded:
```
=IF(OR(C{r}="",H{r}=""),"",IF(J{r}>=C{r},"Paid",IF(H{r}<TODAY(),"Overdue",IF(H{r}-TODAY()<=30,"Due","Upcoming"))))
```
Conditional formatting: Overdue = red, Due = yellow, Paid = green (row-level `FormulaRule` keyed off column L).

### Sinking Funds (sheet 6)
`SinkingFundTable` (A4:G14). 8 category rows (Insurance, Bike Maintenance, Mobile Upgrade, Laptop Upgrade, Travel, Festivals, Gifts, Home Appliances) + 2 spare. Completion % = Current/Target; Est. Months Left = `ROUNDUP(MAX(Target-Current,0)/Monthly,0)`. **Explicitly not wired into the outflow total** — a note clarifies these are informal pots; if a fund should be guaranteed, put it in Monthly Expense Tracker or Annual Expenses instead.

### Loan Calculator (sheet 7)
Bike-loan-only calculator. All 4 inputs (Price/Loan/Rate/Tenure) are **green links to Settings**, not blue inputs, per the single-source-of-truth rule — edit them on Settings. EMI = `PMT(B8/12,B10,-B6)`. Full 36-row amortization table (rows 23–58) with opening/interest/principal/closing columns; principal formula guards against overpaying in the final month (`MIN($B$13-Interest, OpeningBalance)`).

### Debt Payoff Tracker (sheet 8)
5 debt rows (Bike Loan pre-linked to Loan Calculator; Personal Loan/Credit Card/Home Loan/Education Loan blank) + 1 spare = 6 data rows (5–10... actually **rows 5–11**, total row **12**). Columns include Remaining Tenure (`NPER`), New Closure Date (`EDATE(TODAY(), tenure)`), Interest Saved estimate (comparing NPER with vs without Extra Payment). DTI ratio (row 16) = Total EMI ÷ Monthly Budget income, red if >40%. Snowball-vs-Avalanche section (rows ~21+) uses an **array formula** (`LARGE`/`IF`/`MATCH` combination) to rank debts by interest rate descending — flagged as fragile, edit with care.

### Investment Planner (sheet 9)
Mirrors the old "Investment Allocation" design but every % cell is now a **green link to Settings** (`=PctFDRD`, etc.) instead of a blue input — percentages are edited only on Settings now. B3 = Investable Amount linked from Monthly Budget. Primary allocation rows 7–10, total row 11 (`C11` is the address every other sheet calls "Total Invested"). Equity sub-allocation rows 15–18, total row 19.

### Existing Investment Portfolio (sheet 10)
`PortfolioTable` (A4:Q20), 16 blank rows, Type dropdown (Savings/FD/RD/PPF/EPF/NPS/LIC/Mutual Funds/Stocks/ETFs/Gold/Bonds/SGB/Real Estate/Crypto/Others). Per-row CAGR: `(Current/Invested)^(365/days held)-1`. Column J is a **helper column**: `-Invested Amount` (negative cash flow). **Row 21** (just below the table, not part of it) is a synthetic "TOTAL (for XIRR calc)" row: date = `TODAY()`, J = total current value (positive). Row 22 is the real totals row (Invested/Current/Gain/Return%). The XIRR array used elsewhere is `J5:J21` + `D5:D21` — i.e., every holding's negative invested amount plus one final positive "sell everything today" cash flow, which is the standard way to compute a portfolio's money-weighted return with Excel's `XIRR`.

### Portfolio Dashboard (sheet 11)
Read-only. Rows 5–9: Total Invested, Portfolio Value, Unrealized Gain, Absolute Return %, and Overall XIRR (`=IFERROR(XIRR(...),"-")` referencing the helper range above). Rows 13–28: per-Type allocation via `SUMIF` against the portfolio's Type column. Rows 32–36: 5 buckets (Equity / Debt / Gold / Cash / Other) built by **summing multiple SUMIFs** per bucket (e.g. Equity = SUMIF(Mutual Funds)+SUMIF(Stocks)+SUMIF(ETFs)). Two charts: Asset Allocation pie (from the 5-bucket table) and Invested-vs-Current bar.

### Investment Maturity Calendar (sheet 12)
`MaturityTable` (A4:G16), Type dropdown (FD/RD/LIC/Bonds/SGB), Days Remaining = `MaturityDate - TODAY()`, 3-tier conditional formatting at 30/60/90 days.

### SIP Tracker (sheet 13)
`SIPTable` (A4:F28), logs individual SIP installments (Date, Fund Name, Amount) with a running Cumulative Invested column and a Cash Flow helper column (`=-Amount`). Below the table, a synthetic total row holds `=TODAY()` (col A) and a blue input for "Current Value of SIP Investments" (col C) whose helper cash flow is `+CurrentValue`. SIP XIRR = `XIRR(cash-flow-range-including-total-row, date-range-including-total-row)`.

### Passive Income (sheet 14)
5 sources (Interest, Dividends — blank inputs; Rental/Business/Freelance — green links to Settings). Total at **B9**, Annual at B10. "% of Total Income" cross-check at bottom.

### Emergency Fund (sheet 15)
Target (B5) = Months (`=EmergencyMonths`) × Monthly Outflow (`={Monthly Budget outflow cell}`). Current (B6) is the only blue input on this sheet. Progress (B8) = `MIN(Current/Target,1)`.

### Financial Goals (sheet 16)
`GoalTable` (A4:H12). 6 named goals (Bike, Car, House, Marriage, Vacation, Gadgets) + 2 spare. Completion Date formula uses `NPER` with expected return to account for compounding, not simple division:
```
=IF(D>=C,"Achieved",IF(E<=0,"Set contribution",TEXT(EDATE(TODAY(),NPER(F/12,-E,-D,C)),"mmm-yyyy")))
```

### Future Planning (sheet 17)
`FuturePlanTable` (A4:H13). Row 5 is **Emergency Fund**, with Target/Current cross-linked from the Emergency Fund sheet (so that number is only maintained in one place). Rows 6–13: House Down Payment, Retirement, Business Fund, International Trip, Child Education (optional), Car Upgrade + 2 spare. Same NPER-based completion-date formula as Financial Goals.

### Net Worth (sheet 18)
`NetWorthTable` (A4:M16), 12 month rows. Asset columns (Savings/FD/RD/Gold/MF/Stocks/EPF-NPS/Real Estate) summed to Total Assets (J); Liabilities (K→L); Net Worth (M) = J−L. Line chart plots column M across all 12 months. `M16` (December) is the "latest net worth" reference used elsewhere.

### Monthly Tracker (sheet 19)
`MonthlyTrackerTable` (A4:J16), 12 months. EMI and Annual Reserve columns are green links (same value repeated each month); Income/Expenses/Investments/Emergency Fund/Net Worth are blue inputs; Savings = Income−Expenses−EMI−Reserve−Investments. Below the table: Yearly Total row (17), then an "Expense Forecast" section (rows ~22–25) using `AVERAGEIF($B$5:$B$16,"<>",...)` pattern — averaging only over months where the Income cell (col B) is non-blank, to avoid diluting the average with unfilled future months.

### Expense Analysis (sheet 20)
Read-only mirror/breakdown of Monthly Expense Tracker: per-category % of total (rows 6–20), a horizontal bar chart, Highest Expense Category, and the Average Monthly Expense pulled from Monthly Tracker.

### Financial Calendar (sheet 21)
Three stacked blocks: (1) rows 5–16, 12 rows mirroring Annual & One-Time Expenses' Name/Category/Next Due Date; (2) rows 17–28, 12 rows mirroring Investment Maturity Calendar's Name/Maturity Date; (3) rows 29–32, 4 manual entries (Bike Loan EMI date, SIP date, Credit Card due date, Income Tax filing — blue inputs). All rows compute Days Until Due and a 3-state Status (Overdue/Upcoming/Later) with red/yellow conditional formatting keyed off column D.

### Wealth Projection (sheet 22)
Inputs (rows 5–9, all green links): existing portfolio value, monthly SIP (= Investable Amount), Blended Return, Inflation Rate, Salary Growth Rate. Projection table (rows 13–17) for 5/10/15/20 years + "At Retirement" (`RetirementAge - CurrentAge`). **Growing-annuity formula** (SIP assumed to grow yearly at Salary Growth Rate, compounded at Blended Return):
```
= ExistingValue*(1+Return)^Years
  + IF(Return=SalaryGrowth,
       SIP*12*Years*(1+Return)^(Years-1),
       SIP*12*(((1+Return)^Years-(1+SalaryGrowth)^Years)/(Return-SalaryGrowth)))
```
The `IF` branch guards the classic growing-annuity division-by-zero when `Return == SalaryGrowth`. Inflation-adjusted column divides nominal by `(1+Inflation)^Years`. Line chart plots both series across the 5 horizons.

### FIRE Tracker (sheet 23)
Annual Expenses = Monthly Outflow × 12. Target Corpus = Annual Expenses × `FIREMultiplier`. Current Corpus = Portfolio current value. Completion % with green fill if ≥100%. Estimated Years Remaining uses `NPER(Return/12, -SIP, -CurrentCorpus, TargetCorpus)/12`, guarded for already-achieved (`Current>=Target → 0`) and zero-SIP (`"Increase SIP to project"`) cases. Small bar chart: Current Corpus vs Remaining-to-Target.

### Reports (sheet 24)
Houses the **Financial Health Score** (not a separate sheet — see §2 note):
- Rows 5–10: six weighted components (Budget Discipline 20, Savings Rate 20, Investment Consistency 15, Debt Ratio 15, Emergency Fund Progress 15, Goal Progress 15 — weights sum to 100).
- Row 14: Total Score `=SUM(D5:D10)`. Row 15: Rating text via nested `IF` thresholds (≥85 Excellent, ≥70 Good, ≥50 Fair, else Needs attention).
- Rows 18–24: seven conditional one-line suggestions (each an `IF(condition, "message", "")`, so unmet conditions render as a blank row rather than being hidden/deleted — simplest robust pattern for "only show relevant advice").
- Rows 27–34: Executive Summary snapshot (Income, Outflow, Investable, EMI, Passive Income, Emergency Progress, Portfolio Value, FIRE Progress) — all green links, meant as a printable one-page summary.

---

## 6. Charts Inventory

| Sheet | Chart Type | Data Source |
|---|---|---|
| Dashboard | Pie | Expense Breakdown (Monthly Expense Tracker categories) |
| Dashboard | Pie | Investment Allocation (Investment Planner primary rows) |
| Dashboard | Pie | Asset Allocation (Portfolio Dashboard 5-bucket table) |
| Dashboard | Line | Net Worth Growth (Net Worth sheet, col M × 12 months) |
| Dashboard | Line | Savings Trend (Monthly Tracker, col G × 12 months) |
| Dashboard | Bar (col) | Cash Flow Snapshot (Income/Expenses/Investments, 3 helper cells) |
| Dashboard | Bar (bar) | Debt Reduction (Debt Payoff Tracker Outstanding column) |
| Dashboard | Bar (bar) | Goal Progress (Financial Goals Progress % column) |
| Dashboard | Bar (col) | Portfolio Growth (Invested vs Current, from Portfolio Dashboard) |
| Portfolio Dashboard | Pie | Asset Allocation (5-bucket table) |
| Portfolio Dashboard | Bar (col) | Invested vs Current Value |
| Net Worth | Line | Net Worth by month |
| Expense Analysis | Bar (bar) | Expense Breakdown by Category |
| Wealth Projection | Line | Nominal vs Inflation-Adjusted, 5 horizons |
| FIRE Tracker | Bar (bar) | Current Corpus vs Remaining to Target |

---

## 7. Known Fragile / Complex Formulas (handle with care)

1. **Debt Payoff Tracker — Avalanche ranking** (array formula using `LARGE`/`IF`/`MATCH` against the interest-rate column). Don't reorder columns C–D without updating it.
2. **Portfolio XIRR / SIP XIRR** — both depend on a synthetic "final row" trick (today's date + total current value as one extra positive cash flow appended to a range of negative invested amounts). If you add/remove holdings, the range bounds (`J5:J21`, `D5:D21` for Portfolio; similar for SIP Tracker) must still include that final synthetic row.
3. **Wealth Projection growing-annuity formula** — has a divide-by-zero guard for `Return == SalaryGrowth`; don't simplify it away.
4. **Annual & One-Time Expenses — Monthly Reserve Required** is intentionally `Amount / interval_months`, **not** based on proximity to the next due date. This was a deliberate fix after an earlier version caused unrealistic reserve spikes; don't "improve" it back to a due-date-driven calculation without re-checking that the aggregate reserve stays sane across many near-term-due rows.
5. **Monthly Budget's discretionary Actual (C16)** is derived via 5 chained `SUMIF`s against specific category name strings ("Shopping", "Entertainment", "Travel", "Family", "Miscellaneous") in Monthly Expense Tracker. If a user renames or removes one of those exact category labels, this SUMIF chain silently stops counting that category — there's no error, just an undercount. Worth flagging to the user if they rename default categories.

---

## 8. Extension Guidelines

- **Adding a row to any Excel Table:** type into the blank row immediately below the table (or right-click → Insert Table Row Above the Total row, where one exists). The table auto-expands and Excel copies calculated-column formulas from the row above automatically.
- **Adding a new Settings value:** append at the bottom of Settings (after row 51), define a named range (`Settings!$B$<newrow>`), then reference it by name elsewhere — never by raw cell address.
- **Do not hardcode example financial data** into template rows; this workbook was explicitly built "clean" (structure + formulas only, categories/labels as scaffolding) so the end user populates their own numbers.
- **Recalculation check:** after any structural edit, re-open in Excel (or run a LibreOffice headless recalculation) and confirm no `#REF!`, `#NAME?`, or `#VALUE!` errors before handing back to the user — this was done after every 1–3 sheets during the original build and caught several off-by-one row references.
