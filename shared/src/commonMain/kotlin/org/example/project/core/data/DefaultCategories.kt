package org.example.project.core.data

import org.example.project.domain.model.Category
import org.example.project.domain.model.CategoryIcon
import org.example.project.domain.model.TransactionType

object DefaultCategories {
    val expense = listOf(
        Category(name = "Food", icon = CategoryIcon.FOOD, colorHex = "#FF6B6B", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Shopping", icon = CategoryIcon.SHOPPING, colorHex = "#4ECDC4", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Grocery", icon = CategoryIcon.GROCERY, colorHex = "#95E1D3", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Fuel", icon = CategoryIcon.FUEL, colorHex = "#F38181", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Home", icon = CategoryIcon.HOME, colorHex = "#AA96DA", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Beauty", icon = CategoryIcon.BEAUTY, colorHex = "#FCBAD3", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Medical", icon = CategoryIcon.MEDICAL, colorHex = "#FF8787", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Education", icon = CategoryIcon.EDUCATION, colorHex = "#6C5CE7", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Investment", icon = CategoryIcon.INVESTMENT, colorHex = "#00B894", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Bills", icon = CategoryIcon.BILLS, colorHex = "#FDCB6E", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Travel", icon = CategoryIcon.TRAVEL, colorHex = "#74B9FF", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Entertainment", icon = CategoryIcon.ENTERTAINMENT, colorHex = "#A29BFE", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Gifts", icon = CategoryIcon.GIFTS, colorHex = "#FD79A8", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Pets", icon = CategoryIcon.PETS, colorHex = "#FAB1A0", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "EMI", icon = CategoryIcon.EMI, colorHex = "#E17055", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Rent", icon = CategoryIcon.RENT, colorHex = "#81ECEC", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Transport", icon = CategoryIcon.TRANSPORT, colorHex = "#0984E3", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Sports", icon = CategoryIcon.SPORTS, colorHex = "#00CEC9", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Restaurant", icon = CategoryIcon.RESTAURANT, colorHex = "#E84393", type = TransactionType.EXPENSE, isDefault = true),
        Category(name = "Others", icon = CategoryIcon.OTHERS, colorHex = "#636E72", type = TransactionType.EXPENSE, isDefault = true)
    )

    val income = listOf(
        Category(name = "Salary", icon = CategoryIcon.SALARY, colorHex = "#00D2D3", type = TransactionType.INCOME, isDefault = true),
        Category(name = "Freelancing", icon = CategoryIcon.FREELANCING, colorHex = "#55EFC4", type = TransactionType.INCOME, isDefault = true),
        Category(name = "Business", icon = CategoryIcon.BUSINESS, colorHex = "#A29BFE", type = TransactionType.INCOME, isDefault = true),
        Category(name = "Investment", icon = CategoryIcon.INVESTMENT, colorHex = "#6C5CE7", type = TransactionType.INCOME, isDefault = true),
        Category(name = "Gifts", icon = CategoryIcon.GIFTS, colorHex = "#FD79A8", type = TransactionType.INCOME, isDefault = true),
        Category(name = "Others", icon = CategoryIcon.OTHERS, colorHex = "#636E72", type = TransactionType.INCOME, isDefault = true)
    )

    val all = expense + income
}
