package org.example.project.ui.goals

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.domain.model.CategoryIcon
import org.example.project.domain.model.SavingsGoal
import org.example.project.domain.usecase.goals.AddContributionUseCase
import org.example.project.domain.usecase.goals.AddSavingsGoalUseCase
import org.example.project.domain.usecase.goals.DeleteSavingsGoalUseCase
import org.example.project.domain.usecase.goals.GetSavingsGoalsUseCase
import org.example.project.domain.usecase.goals.UpdateSavingsGoalUseCase

// ── State ──────────────────────────────────────────────────────────────────────

data class GoalsUiState(
    val isLoading: Boolean = true,
    val goals: List<SavingsGoal> = emptyList(),
    val totalSaved: Double = 0.0,
    val totalTarget: Double = 0.0,
    val completedCount: Int = 0,
    val error: String? = null
) {
    val overallProgress: Float
        get() = if (totalTarget > 0) (totalSaved / totalTarget).toFloat().coerceIn(0f, 1f) else 0f
}

// ── Events (UI → ViewModel) ────────────────────────────────────────────────────

sealed interface GoalsEvent {
    data class SaveGoal(
        val id: Long,
        val name: String,
        val targetAmount: Double,
        val currentAmount: Double,
        val colorHex: String,
        val icon: CategoryIcon,
        val deadline: String?
    ) : GoalsEvent

    data class DeleteGoal(val goalId: Long) : GoalsEvent
    data class AddContribution(val goalId: Long, val amount: Double) : GoalsEvent
    data object ClearError : GoalsEvent
}

// ── Effects (ViewModel → UI one-shot) ─────────────────────────────────────────

sealed interface GoalsEffect {
    data object GoalSaved : GoalsEffect
    data object GoalDeleted : GoalsEffect
    data object ContributionAdded : GoalsEffect
    data class Error(val message: String) : GoalsEffect
}

// ── ViewModel ─────────────────────────────────────────────────────────────────

class GoalsViewModel(
    private val getGoalsUseCase: GetSavingsGoalsUseCase,
    private val addGoalUseCase: AddSavingsGoalUseCase,
    private val updateGoalUseCase: UpdateSavingsGoalUseCase,
    private val deleteGoalUseCase: DeleteSavingsGoalUseCase,
    private val addContributionUseCase: AddContributionUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(GoalsUiState())
    val uiState: StateFlow<GoalsUiState> = _uiState.asStateFlow()

    private val _effect = Channel<GoalsEffect>(Channel.BUFFERED)
    val effect: Flow<GoalsEffect> = _effect.receiveAsFlow()

    init {
        observeGoals()
    }

    private fun observeGoals() {
        viewModelScope.launch {
            getGoalsUseCase()
                .catch { e ->
                    _uiState.update { it.copy(isLoading = false, error = e.message) }
                }
                .collect { goals ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            goals = goals,
                            totalSaved = goals.sumOf { g -> g.currentAmount },
                            totalTarget = goals.sumOf { g -> g.targetAmount },
                            completedCount = goals.count { g -> g.isCompleted }
                        )
                    }
                }
        }
    }

    fun onEvent(event: GoalsEvent) {
        when (event) {
            is GoalsEvent.SaveGoal -> saveGoal(event)
            is GoalsEvent.DeleteGoal -> deleteGoal(event.goalId)
            is GoalsEvent.AddContribution -> addContribution(event.goalId, event.amount)
            is GoalsEvent.ClearError -> _uiState.update { it.copy(error = null) }
        }
    }

    private fun saveGoal(event: GoalsEvent.SaveGoal) {
        viewModelScope.launch {
            runCatching {
                val goal = SavingsGoal(
                    id = event.id,
                    name = event.name,
                    targetAmount = event.targetAmount,
                    currentAmount = event.currentAmount,
                    colorHex = event.colorHex,
                    icon = event.icon,
                    deadline = event.deadline.takeIf { !it.isNullOrBlank() }
                )
                if (event.id == 0L) {
                    addGoalUseCase(goal)
                } else {
                    updateGoalUseCase(goal)
                }
            }.onSuccess {
                _effect.send(GoalsEffect.GoalSaved)
            }.onFailure { e ->
                _uiState.update { it.copy(error = e.message) }
                _effect.send(GoalsEffect.Error(e.message ?: "Failed to save goal"))
            }
        }
    }

    private fun deleteGoal(goalId: Long) {
        viewModelScope.launch {
            runCatching { deleteGoalUseCase(goalId) }
                .onSuccess { _effect.send(GoalsEffect.GoalDeleted) }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                    _effect.send(GoalsEffect.Error(e.message ?: "Failed to delete goal"))
                }
        }
    }

    private fun addContribution(goalId: Long, amount: Double) {
        viewModelScope.launch {
            runCatching { addContributionUseCase(goalId, amount) }
                .onSuccess { _effect.send(GoalsEffect.ContributionAdded) }
                .onFailure { e ->
                    _uiState.update { it.copy(error = e.message) }
                    _effect.send(GoalsEffect.Error(e.message ?: "Failed to add contribution"))
                }
        }
    }
}
