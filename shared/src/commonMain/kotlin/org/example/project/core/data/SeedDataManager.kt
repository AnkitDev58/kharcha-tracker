package org.example.project.core.data

import kotlinx.coroutines.flow.first
import kotlinx.datetime.DatePeriod
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.minus
import kotlinx.datetime.todayIn
import org.example.project.domain.model.PaymentMethod
import org.example.project.domain.model.Transaction
import org.example.project.domain.model.TransactionType
import org.example.project.domain.repository.CategoryRepository
import org.example.project.domain.repository.TransactionRepository
import kotlin.time.Clock

class SeedDataManager(
    private val categoryRepository: CategoryRepository,
    private val transactionRepository: TransactionRepository
) {
    suspend fun seedIfEmpty() {

        seedTransactions()
        return
        if (categoryRepository.getCategoryCount() > 0) return

        // Insert default categories
        categoryRepository.insertCategories(DefaultCategories.all)
        return
        // Seed some sample transactions
        val allCats = categoryRepository.getAllCategories().first()
        val salarycat = allCats.firstOrNull { it.name == "Salary" } ?: return
        val foodCat = allCats.firstOrNull { it.name == "Food" } ?: return
        val fuelCat = allCats.firstOrNull { it.name == "Fuel" } ?: return
        val shopCat = allCats.firstOrNull { it.name == "Shopping" } ?: return
        val freelanceCat = allCats.firstOrNull { it.name == "Freelancing" } ?: return

        val sampleTransactions = listOf(
            Transaction(
                amount = 85000.0,
                type = TransactionType.INCOME,
                categoryId = salarycat.id,
                note = "Monthly Salary",
                dateTime = LocalDateTime(2025, 7, 1, 9, 0),
                paymentMethod = PaymentMethod.BANK_TRANSFER
            ),
            Transaction(
                amount = 12000.0,
                type = TransactionType.INCOME,
                categoryId = freelanceCat.id,
                note = "Freelance project",
                dateTime = LocalDateTime(2025, 7, 5, 14, 0),
                paymentMethod = PaymentMethod.UPI
            ),
            Transaction(
                amount = 1850.0,
                type = TransactionType.EXPENSE,
                categoryId = fuelCat.id,
                note = "Fuel refill",
                dateTime = LocalDateTime(2025, 7, 6, 10, 30),
                paymentMethod = PaymentMethod.UPI
            ),
            Transaction(
                amount = 3200.0,
                type = TransactionType.EXPENSE,
                categoryId = foodCat.id,
                note = "Grocery shopping",
                dateTime = LocalDateTime(2025, 7, 6, 18, 0),
                paymentMethod = PaymentMethod.CASH
            ),
            Transaction(
                amount = 8450.0,
                type = TransactionType.EXPENSE,
                categoryId = shopCat.id,
                note = "New clothes",
                dateTime = LocalDateTime(2025, 7, 7, 15, 0),
                paymentMethod = PaymentMethod.CARD
            )
        )
        transactionRepository.insertTransaction(sampleTransactions[0])
        transactionRepository.insertTransaction(sampleTransactions[1])
        transactionRepository.insertTransaction(sampleTransactions[2])
        transactionRepository.insertTransaction(sampleTransactions[3])
        transactionRepository.insertTransaction(sampleTransactions[4])
    }


    private suspend fun seedTransactions() {
        if (categoryRepository.getCategoryCount() > 0) {
            return
        }

        categoryRepository.insertCategories(DefaultCategories.all)

        return
        val categories = categoryRepository.getAllCategories().first()

        fun category(name: String, type: TransactionType) =
            categories.first {
                it.name == name && it.type == type
            }

        val salary = category("Salary", TransactionType.INCOME)
        val freelance = category("Freelancing", TransactionType.INCOME)
        val business = category("Business", TransactionType.INCOME)

        val food = category("Food", TransactionType.EXPENSE)
        val grocery = category("Grocery", TransactionType.EXPENSE)
        val fuel = category("Fuel", TransactionType.EXPENSE)
        val shopping = category("Shopping", TransactionType.EXPENSE)
        val home = category("Home", TransactionType.EXPENSE)
        val beauty = category("Beauty", TransactionType.EXPENSE)
        val medical = category("Medical", TransactionType.EXPENSE)
        val education = category("Education", TransactionType.EXPENSE)
        val bills = category("Bills", TransactionType.EXPENSE)
        val travel = category("Travel", TransactionType.EXPENSE)
        val entertainment = category("Entertainment", TransactionType.EXPENSE)
        val gifts = category("Gifts", TransactionType.EXPENSE)
        val pets = category("Pets", TransactionType.EXPENSE)
        val emi = category("EMI", TransactionType.EXPENSE)
        val rent = category("Rent", TransactionType.EXPENSE)
        val transport = category("Transport", TransactionType.EXPENSE)
        val restaurant = category("Restaurant", TransactionType.EXPENSE)
        val others = category("Others", TransactionType.EXPENSE)

        val expenseCategories = listOf(
            food,
            grocery,
            fuel,
            shopping,
            home,
            beauty,
            medical,
            education,
            bills,
            travel,
            entertainment,
            gifts,
            pets,
            emi,
            rent,
            transport,
            restaurant,
            others
        )

        val paymentMethods = PaymentMethod.entries

        val notes = mapOf(
            "Food" to listOf("Lunch", "Dinner", "Snacks"),
            "Shopping" to listOf("Clothes", "Amazon", "Mall Shopping"),
            "Fuel" to listOf("Fuel Refill"),
            "Grocery" to listOf("Weekly Grocery"),
            "Bills" to listOf("Electricity Bill", "Internet Bill"),
            "Home" to listOf("Home Items"),
            "Restaurant" to listOf("Family Dinner"),
            "Entertainment" to listOf("Movie", "Netflix"),
            "Travel" to listOf("Trip"),
            "Medical" to listOf("Medicines"),
            "Beauty" to listOf("Salon"),
            "Education" to listOf("Course"),
            "EMI" to listOf("Bike EMI"),
            "Rent" to listOf("House Rent"),
            "Transport" to listOf("Auto Fare"),
            "Pets" to listOf("Pet Food"),
            "Gifts" to listOf("Birthday Gift"),
            "Others" to listOf("Misc Expense")
        )

        val transactions = mutableListOf<Transaction>()

        val currentMonth = Clock.System.todayIn(TimeZone.currentSystemDefault())

        repeat(12) { monthOffset ->

            val monthDate = currentMonth.minus(DatePeriod(months = monthOffset))

            //---------------- Salary ----------------//

            transactions += Transaction(
                amount = (70000..90000).random().toDouble(),
                type = TransactionType.INCOME,
                categoryId = salary.id,
                note = "Monthly Salary",
                paymentMethod = PaymentMethod.BANK_TRANSFER,
                dateTime = LocalDateTime(
                    monthDate.year,
                    monthDate.month,
                    1,
                    9,
                    0
                )
            )

            //---------------- Freelancing ----------------//

            repeat((0..2).random()) {

                val cat = if ((0..1).random() == 0) freelance else business

                transactions += Transaction(
                    amount = (5000..35000).random().toDouble(),
                    type = TransactionType.INCOME,
                    categoryId = cat.id,
                    note = cat.name,
                    paymentMethod = paymentMethods.random(),
                    dateTime = LocalDateTime(
                        monthDate.year,
                        monthDate.month,
                        (3..26).random(),
                        (9..20).random(),
                        listOf(0, 15, 30, 45).random()
                    )
                )
            }

            //---------------- Expenses ----------------//

            repeat((20..35).random()) {

                val cat = expenseCategories.random()

                val amount = when (cat.name) {

                    "Fuel" -> (800..2500)

                    "Food" -> (150..1000)

                    "Restaurant" -> (500..3500)

                    "Shopping" -> (1000..15000)

                    "Rent" -> (8000..18000)

                    "Bills" -> (500..4500)

                    "EMI" -> (2500..12000)

                    "Travel" -> (2000..25000)

                    "Medical" -> (200..5000)

                    else -> (100..3000)
                }

                transactions += Transaction(
                    amount = amount.random().toDouble(),
                    type = TransactionType.EXPENSE,
                    categoryId = cat.id,
                    note = notes[cat.name]?.random() ?: cat.name,
                    paymentMethod = paymentMethods.random(),
                    dateTime = LocalDateTime(
                        monthDate.year,
                        monthDate.month,
                        (1..28).random(),
                        (8..22).random(),
                        listOf(0, 15, 30, 45).random()
                    )
                )
            }
        }

        transactions
            .sortedBy { it.dateTime }
            .forEach {
                transactionRepository.insertTransaction(it)
            }
    }
}
