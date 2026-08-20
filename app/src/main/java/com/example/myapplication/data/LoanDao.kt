package com.example.myapplication.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface LoanDao {
    // Debtor operations
    @Insert
    suspend fun insertDebtor(debtor: DebtorEntity)

    @Update
    suspend fun updateDebtor(debtor: DebtorEntity)

    @Delete
    suspend fun deleteDebtor(debtor: DebtorEntity)

    @Query("SELECT * FROM debtors ORDER BY nombre ASC")
    fun getAllDebtors(): Flow<List<DebtorEntity>>

    @Query("SELECT * FROM debtors WHERE id = :id")
    suspend fun getDebtorById(id: Int): DebtorEntity?

    // Loan operations
    @Insert
    suspend fun insertLoan(loan: LoanEntity): Long

    @Query("SELECT * FROM loans WHERE debtorId = :debtorId")
    fun getLoansForDebtor(debtorId: Int): Flow<List<LoanEntity>>

    @Query("SELECT * FROM loans WHERE id = :id")
    suspend fun getLoanById(id: Int): LoanEntity?

    // Installment operations
    @Insert
    suspend fun insertInstallments(installments: List<DailyInstallmentEntity>)

    @Query("SELECT * FROM installments WHERE loanId = :loanId ORDER BY numeroDia ASC")
    fun getInstallmentsForLoan(loanId: Int): Flow<List<DailyInstallmentEntity>>

    @Update
    suspend fun updateInstallment(installment: DailyInstallmentEntity)

    @Query("SELECT COUNT(*) FROM installments WHERE loanId = :loanId AND estadoPago != 'PAGADO'")
    suspend fun getUnpaidInstallmentsCount(loanId: Int): Int

    @Update
    suspend fun updateLoan(loan: LoanEntity)
}
