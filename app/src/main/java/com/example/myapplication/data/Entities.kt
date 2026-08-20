package com.example.myapplication.data

import androidx.room.*

@Entity(tableName = "debtors")
data class DebtorEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val telefono: String,
    val direccion: String,
    val observaciones: String = "",
    val fechaRegistro: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "loans",
    foreignKeys = [
        ForeignKey(
            entity = DebtorEntity::class,
            parentColumns = ["id"],
            childColumns = ["debtorId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("debtorId")]
)
data class LoanEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val debtorId: Int,
    val montoPrestado: Double,
    val montoTotalCobro: Double,
    val valorCuotaDiaria: Double,
    val nombrePeriodo: String = "",
    val fechaInicio: Long = System.currentTimeMillis(),
    val estado: String = "ACTIVO" // ACTIVO, FINALIZADO
)

@Entity(
    tableName = "installments",
    foreignKeys = [
        ForeignKey(
            entity = LoanEntity::class,
            parentColumns = ["id"],
            childColumns = ["loanId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("loanId")]
)
data class DailyInstallmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val loanId: Int,
    val numeroDia: Int, // 1-30
    val fechaProgramada: Long,
    val montoEsperado: Double,
    val montoPagado: Double = 0.0,
    val estadoPago: String = "PENDIENTE", // PENDIENTE, PAGADO, ATRASADO
    val fechaPagoReal: Long? = null
)
