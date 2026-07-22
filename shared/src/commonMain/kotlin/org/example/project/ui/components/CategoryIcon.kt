package org.example.project.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import org.example.project.domain.model.CategoryIcon

fun CategoryIcon.toIcon(): ImageVector = when (this) {
    CategoryIcon.FOOD -> Icons.Filled.Fastfood
    CategoryIcon.SHOPPING -> Icons.Filled.ShoppingBag
    CategoryIcon.GROCERY -> Icons.Filled.ShoppingCart
    CategoryIcon.FUEL -> Icons.Filled.LocalGasStation
    CategoryIcon.HOME -> Icons.Filled.Home
    CategoryIcon.BEAUTY -> Icons.Filled.Face
    CategoryIcon.MEDICAL -> Icons.Filled.LocalHospital
    CategoryIcon.EDUCATION -> Icons.Filled.School
    CategoryIcon.INVESTMENT -> Icons.AutoMirrored.Filled.TrendingUp
    CategoryIcon.BILLS -> Icons.Filled.Receipt
    CategoryIcon.TRAVEL -> Icons.Filled.Flight
    CategoryIcon.ENTERTAINMENT -> Icons.Filled.Movie
    CategoryIcon.GIFTS -> Icons.Filled.CardGiftcard
    CategoryIcon.PETS -> Icons.Filled.Pets
    CategoryIcon.EMI -> Icons.Filled.CreditCard
    CategoryIcon.RENT -> Icons.Filled.House
    CategoryIcon.SALARY -> Icons.Filled.AccountBalance
    CategoryIcon.FREELANCING -> Icons.Filled.Work
    CategoryIcon.BUSINESS -> Icons.Filled.Business
    CategoryIcon.OTHERS -> Icons.Filled.Category
    CategoryIcon.SAVINGS -> Icons.Filled.Savings
    CategoryIcon.TRANSPORT -> Icons.Filled.DirectionsBus
    CategoryIcon.SPORTS -> Icons.Filled.SportsBasketball
    CategoryIcon.TECH -> Icons.Filled.Devices
    CategoryIcon.RESTAURANT -> Icons.Filled.Restaurant
}
