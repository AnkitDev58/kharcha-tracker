package org.example.project.domain.model

data class SavingsGoal(
    val id: Long = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val colorHex: String = "#6C63FF",
    val icon: CategoryIcon = CategoryIcon.SAVINGS,
    val deadline: String? = null
) {
    val progress: Float get() = if (targetAmount > 0) (currentAmount / targetAmount).toFloat().coerceIn(0f, 1f) else 0f
    val remaining: Double get() = (targetAmount - currentAmount).coerceAtLeast(0.0)
    val isCompleted: Boolean get() = currentAmount >= targetAmount
}
