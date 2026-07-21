package org.example.project.ui.loan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.example.project.core.util.DateTimeUtils
import org.example.project.domain.model.Loan
import org.example.project.domain.model.LoanType
import org.example.project.domain.usecase.loan.*

data class LoanUiState(
    val isLoading: Boolean = true,
    val loans: List<Loan> = emptyList(),
    val totalOutstanding: Double = 0.0,
    val totalEmi: Double = 0.0,
    val error: String? = null,
    val loanSize: Int? = 0
)

sealed interface LoanEffect {
    data object Saved : LoanEffect
    data object Deleted : LoanEffect
    data class Error(val message: String) : LoanEffect
}

class LoanViewModel(
    private val getLoans: GetLoansUseCase,
    private val addLoan: AddLoanUseCase,
    private val updateLoan: UpdateLoanUseCase,
    private val deleteLoan: DeleteLoanUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    private val _effect = Channel<LoanEffect>(Channel.BUFFERED)
    val effect: Flow<LoanEffect> = _effect.receiveAsFlow()

    init {
        viewModelScope.launch {
            getLoans().collect { list ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        loans = list,
                        loanSize = list.count { !it.isLoanComplete },
                        totalOutstanding = list.sumOf { l -> l.outstandingBalance },
                        totalEmi = list.sumOf { l -> if (l.isLoanComplete) 0.0 else l.emi }
                    )
                }
            }
        }
    }

    fun save(
        id: Long,
        name: String,
        principal: Double,
        outstanding: Double,
        rate: Double,
        tenureMonths: Int,
        paidMonths: Int,
        loanType: LoanType,
        notes: String
    ) {
        viewModelScope.launch {
            runCatching {
                val today = DateTimeUtils.today().toString()
                val loan = Loan(
                    id = id, name = name, principal = principal,
                    outstandingBalance = outstanding, interestRatePercent = rate,
                    tenureMonths = tenureMonths, paidMonths = paidMonths,
                    startDate = today, loanType = loanType, notes = notes
                )
                if (id == 0L) addLoan(loan) else updateLoan(loan)
            }.onSuccess { _effect.send(LoanEffect.Saved) }
                .onFailure { _effect.send(LoanEffect.Error(it.message ?: "Failed")) }
        }
    }

    fun markEmiPaid(loan: Loan) {
        viewModelScope.launch {
            val newPaid = (loan.paidMonths + 1).coerceAtMost(loan.tenureMonths)
            val newOutstanding = (loan.outstandingBalance - loan.emi).coerceAtLeast(0.0)
            runCatching {
                updateLoan(
                    loan.copy(
                        paidMonths = newPaid,
                        outstandingBalance = newOutstanding
                    )
                )
            }
                .onSuccess { _effect.send(LoanEffect.Saved) }
                .onFailure { _effect.send(LoanEffect.Error(it.message ?: "Failed")) }
        }
    }

    fun delete(id: Long) {
        viewModelScope.launch {
            runCatching { deleteLoan(id) }
                .onSuccess { _effect.send(LoanEffect.Deleted) }
                .onFailure { _effect.send(LoanEffect.Error(it.message ?: "Failed")) }
        }
    }
}
