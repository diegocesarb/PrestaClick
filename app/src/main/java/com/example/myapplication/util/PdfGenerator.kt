package com.example.myapplication.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Environment
import android.widget.Toast
import com.example.myapplication.data.DailyInstallmentEntity
import com.example.myapplication.data.DebtorEntity
import com.example.myapplication.data.LoanEntity
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

object PdfGenerator {

    fun generateLoanReport(
        context: Context,
        debtor: DebtorEntity,
        loan: LoanEntity,
        installments: List<DailyInstallmentEntity>
    ) {
        val pdfDocument = PdfDocument()
        val titlePaint = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
        }
        val headerPaint = Paint().apply {
            textSize = 14f
            isFakeBoldText = true
        }
        val contentPaint = Paint().apply {
            textSize = 12f
        }

        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas

        var y = 50f
        canvas.drawText("Reporte de Préstamo", 200f, y, titlePaint)
        
        y += 40f
        canvas.drawText("Deudor: ${debtor.nombre}", 50f, y, contentPaint)
        y += 20f
        canvas.drawText("Teléfono: ${debtor.telefono}", 50f, y, contentPaint)
        y += 20f
        canvas.drawText("Dirección: ${debtor.direccion}", 50f, y, contentPaint)
        
        y += 40f
        canvas.drawText("Detalles del Préstamo", 50f, y, headerPaint)
        if (loan.nombrePeriodo.isNotEmpty()) {
            y += 20f
            canvas.drawText("Periodo: ${loan.nombrePeriodo}", 50f, y, contentPaint)
        }
        y += 20f
        canvas.drawText("Monto Prestado: $${loan.montoPrestado}", 50f, y, contentPaint)
        y += 20f
        canvas.drawText("Monto Total a Cobrar: $${loan.montoTotalCobro}", 50f, y, contentPaint)
        y += 20f
        canvas.drawText("Cuota Diaria: $${loan.valorCuotaDiaria}", 50f, y, contentPaint)
        
        y += 40f
        canvas.drawText("Historial de Pagos (30 días)", 50f, y, headerPaint)
        y += 30f
        
        canvas.drawText("Día", 50f, y, headerPaint)
        canvas.drawText("Fecha", 100f, y, headerPaint)
        canvas.drawText("Esperado", 250f, y, headerPaint)
        canvas.drawText("Pagado", 350f, y, headerPaint)
        canvas.drawText("Estado", 450f, y, headerPaint)
        
        y += 20f
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        
        installments.forEach { installment ->
            canvas.drawText(installment.numeroDia.toString(), 50f, y, contentPaint)
            canvas.drawText(sdf.format(Date(installment.fechaProgramada)), 100f, y, contentPaint)
            canvas.drawText("$${installment.montoEsperado}", 250f, y, contentPaint)
            canvas.drawText("$${installment.montoPagado}", 350f, y, contentPaint)
            canvas.drawText(installment.estadoPago, 450f, y, contentPaint)
            y += 20f
        }

        pdfDocument.finishPage(page)

        val fileName = "Reporte_${debtor.nombre.replace(" ", "_")}_${System.currentTimeMillis()}.pdf"
        val filePath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), fileName)

        try {
            pdfDocument.writeTo(FileOutputStream(filePath))
            Toast.makeText(context, "PDF generado en: ${filePath.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdfDocument.close()
        }
    }
}
