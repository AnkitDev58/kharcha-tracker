package org.example.project.domain.model

enum class ExpenseFrequency(val intervalMonths: Int, val label: String) {
    MONTHLY(1, "Monthly"),
    QUARTERLY(3, "Quarterly"),
    HALF_YEARLY(6, "Half-Yearly"),
    YEARLY(12, "Yearly")
}

data class AnnualExpense(
    val id: Long = 0,
    val name: String,
    val category: String,
    val amount: Double,
    val frequency: ExpenseFrequency = ExpenseFrequency.YEARLY,
    val dueMonth: Int = 1,
    val lastPaidDate: String? = null,
    val notes: String = ""
) {
    /** Steady-state monthly reserve = amount / frequency interval months.
     *  Deliberately NOT based on proximity to next due date (see implementation reference). */
    val monthlyReserve: Double
        get() = amount / frequency.intervalMonths

    val nextDueMonth: String
        get() = when (dueMonth) {
            1 -> "Jan"; 2 -> "Feb"; 3 -> "Mar"; 4 -> "Apr"
            5 -> "May"; 6 -> "Jun"; 7 -> "Jul"; 8 -> "Aug"
            9 -> "Sep"; 10 -> "Oct"; 11 -> "Nov"; 12 -> "Dec"
            else -> "—"
        }
}
