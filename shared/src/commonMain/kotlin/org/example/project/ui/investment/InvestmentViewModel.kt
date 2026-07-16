package org.example.project.ui.investment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.AssetBucket
import org.example.project.domain.model.Investment
import org.example.project.domain.model.InvestmentType
import org.example.project.domain.usecase.investment.*

data class BucketSummary(
    val bucket: AssetBucket,
    val currentValue: Double,
    val invested: Double,
    val percentage: Float
)

data class InvestmentUiState(
    val isLoading: Boolean = true,
    val investments: List<Investment> = emptyList(),
    val totalInvested: Double = 0.0,
    val totalCurrentValue: Double = 0.0,
    val buckets: List<BucketSummary> = emptyList(),
    val error: String? = null
) {
    val totalGain: Double get() = totalCurrentValue - totalInvested
    val totalGainPct: Double get() = if (totalInvested > 0) (totalGain / totalInvested) * 100 else 0.0
    val isProfit: Boolean get() = totalGain >= 0
}

sealed interface InvestmentEffect {
    data object Saved : InvestmentEffect
    data object Deleted : InvestmentEffect
    data class Error(val message: String) : InvestmentEffect
}

class InvestmentViewModel(
    private val getInvestments: GetInvestmentsUseCase,
    private val addInvestment: AddInvestmentUseCase,
    private val updateInvestment: UpdateInvestmentUseCase,
    private val deleteInvestment: DeleteInvestmentUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvestmentUiState())
    val uiState: StateFlow<InvestmentUiState> = _uiState.asStateFlow()

    private val _effect = Channel<InvestmentEffect>(Channel.BUFFERED)
    val effect: Flow<InvestmentEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            getInvestments().collect { list ->
                val totalInvested = list.sumOf { it.investedAmount }
                val totalCurrent  = list.sumOf { it.currentValue }
                val buckets = AssetBucket.entries.map { bucket ->
                    val inBucket = list.filter { it.investmentType.bucket == bucket }
                    val cv = inBucket.sumOf { it.currentValue }
                    val inv = inBucket.sumOf { it.investedAmount }
                    BucketSummary(
                        bucket = bucket,
                        currentValue = cv,
                        invested = inv,
                        percentage = if (totalCurrent > 0) (cv / totalCurrent * 100).toFloat() else 0f
                    )
                }.filter { it.invested > 0 }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        investments = list,
                        totalInvested = totalInvested,
                        totalCurrentValue = totalCurrent,
                        buckets = buckets
                    )
                }
            }
        }
    }

    fun save(
        id: Long,
        name: String,
        type: InvestmentType,
        invested: Double,
        current: Double,
        investDate: String,
        maturityDate: String?,
        isSIP: Boolean,
        sipAmount: Double,
        notes: String
    ) {
        viewModelScope.launch {
            runCatching {
                val inv = Investment(
                    id = id, name = name, investmentType = type,
                    investedAmount = invested, currentValue = current,
                    investDate = investDate, maturityDate = maturityDate?.takeIf { it.isNotBlank() },
                    isSIP = isSIP, sipMonthlyAmount = sipAmount, notes = notes
                )
                if (id == 0L) addInvestment(inv) else updateInvestment(inv)
            }.onSuccess { _effect.send(InvestmentEffect.Saved) }
             .onFailure { _effect.send(InvestmentEffect.Error(it.message ?: "Failed")) }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            runCatching { deleteInvestment(id) }
                .onSuccess { _effect.send(InvestmentEffect.Deleted) }
                .onFailure { _effect.send(InvestmentEffect.Error(it.message ?: "Failed")) }
        }
    }

    fun daysHeld(investDate: String): Int {
        return try {
            val start = LocalDate.parse(investDate)
            val today = DateTimeUtils.today()
            (today.toEpochDays() - start.toEpochDays()).toInt().coerceAtLeast(1)
        } catch (e: Exception) { 1 }
    }
}
