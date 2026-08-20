package com.example.myapplication.repository

import com.example.myapplication.data.DailyInstallmentEntity
import com.example.myapplication.data.DebtorEntity
import com.example.myapplication.data.LoanDao
import com.example.myapplication.data.LoanEntity
import kotlinx.coroutines.flow.Flow
import java.util.*

class LoanRepository(private val loanDao: LoanDao) {

    // Debtors
    val allDebtors: Flow<List<DebtorEntity>> = loanDao.getAllDebtors()

    suspend fun insertDebtor(debtor: DebtorEntity) {
        loanDao.insertDebtor(debtor)
    }

    suspend fun updateDebtor(debtor: DebtorEntity) {
        loanDao.updateDebtor(debtor)
    }

    suspend fun deleteDebtor(debtor: DebtorEntity) {
        loanDao.deleteDebtor(debtor)
    }

    suspend fun getDebtorById(id: Int) = loanDao.getDebtorById(id)

    // Loans
    fun getLoansForDebtor(debtorId: Int): Flow<List<LoanEntity>> = loanDao.getLoansForDebtor(debtorId)

    suspend fun getLoanById(id: Int) = loanDao.getLoanById(id)

    suspend fun createLoanWithInstallments(loan: LoanEntity) {
        val loanId = loanDao.insertLoan(loan).toInt()
        
        val installments = mutableListOf<DailyInstallmentEntity>()
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = loan.fechaInicio

        for (i in 1..30) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            installments.add(
                DailyInstallmentEntity(
                    loanId = loanId,
                    numeroDia = i,
                    fechaProgramada = calendar.timeInMillis,
                    montoEsperado = loan.valorCuotaDiaria
                )
            )
        }
        loanDao.insertInstallments(installments)
    }

    // Installments
    fun getInstallmentsForLoan(loanId: Int): Flow<List<DailyInstallmentEntity>> =
        loanDao.getInstallmentsForLoan(loanId)

    suspend fun updateInstallment(installment: DailyInstallmentEntity) {
        loanDao.updateInstallment(installment)
    }
}
