package com.example.myapplication.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.AppDatabase
import com.example.myapplication.data.DailyInstallmentEntity
import com.example.myapplication.data.DebtorEntity
import com.example.myapplication.data.LoanEntity
import com.example.myapplication.repository.LoanRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoanViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: LoanRepository
    private val _allDebtors = MutableStateFlow<List<DebtorEntity>>(emptyList())
    val allDebtors: StateFlow<List<DebtorEntity>> = _allDebtors
    
    private val _selectedDebtorLoans = MutableStateFlow<List<LoanEntity>>(emptyList())
    val selectedDebtorLoans: StateFlow<List<LoanEntity>> = _selectedDebtorLoans

    private val _selectedLoanInstallments = MutableStateFlow<List<DailyInstallmentEntity>>(emptyList())
    val selectedLoanInstallments: StateFlow<List<DailyInstallmentEntity>> = _selectedLoanInstallments

    init {
        val loanDao = AppDatabase.getDatabase(application).loanDao()
        repository = LoanRepository(loanDao)
        
        viewModelScope.launch {
            repository.allDebtors.collect {
                _allDebtors.value = it
            }
        }
    }

    // Debtor actions
    fun addDebtor(nombre: String, telefono: String, direccion: String, observaciones: String = "") {
        viewModelScope.launch {
            repository.insertDebtor(DebtorEntity(nombre = nombre, telefono = telefono, direccion = direccion, observaciones = observaciones))
        }
    }

    fun updateDebtor(debtor: DebtorEntity) {
        viewModelScope.launch {
            repository.updateDebtor(debtor)
        }
    }

    fun deleteDebtor(debtor: DebtorEntity) {
        viewModelScope.launch {
            repository.deleteDebtor(debtor)
        }
    }

    // Loan actions
    fun loadLoansForDebtor(debtorId: Int) {
        viewModelScope.launch {
            repository.getLoansForDebtor(debtorId).collect {
                _selectedDebtorLoans.value = it
            }
        }
    }

    fun addLoan(debtorId: Int, montoPrestado: Double, montoTotalCobro: Double, valorCuotaDiaria: Double, nombrePeriodo: String) {
        viewModelScope.launch {
            val loan = LoanEntity(
                debtorId = debtorId,
                montoPrestado = montoPrestado,
                montoTotalCobro = montoTotalCobro,
                valorCuotaDiaria = valorCuotaDiaria,
                nombrePeriodo = nombrePeriodo
            )
            repository.createLoanWithInstallments(loan)
        }
    }

    // Installment actions
    fun loadInstallmentsForLoan(loanId: Int) {
        viewModelScope.launch {
            repository.getInstallmentsForLoan(loanId).collect {
                _selectedLoanInstallments.value = it
            }
        }
    }

    fun payInstallment(installment: DailyInstallmentEntity, amount: Double) {
        viewModelScope.launch {
            val updatedInstallment = installment.copy(
                montoPagado = amount,
                estadoPago = if (amount >= installment.montoEsperado) "PAGADO" else "ATRASADO",
                fechaPagoReal = System.currentTimeMillis()
            )
            repository.updateInstallment(updatedInstallment)
        }
    }
}
