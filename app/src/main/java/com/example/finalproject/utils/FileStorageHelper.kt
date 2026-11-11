package com.example.finalproject.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.net.Uri
import java.io.File
import java.io.FileOutputStream

/**
 * Helper untuk menyimpan dan membaca struk transaksi (PDF/CSV) serta gambar pakaian lokal.
 */
object FileStorageHelper {

    fun saveCsvReceipt(context: Context, fileName: String, csvContent: String): File {
        val dir = File(context.filesDir, "receipts/csv").apply { mkdirs() }
        val file = File(dir, if (fileName.endsWith(".csv")) fileName else "$fileName.csv")
        file.writeText(csvContent)
        return file
    }

    fun readCsvReceipt(file: File): String {
        return if (file.exists()) file.readText() else ""
    }

    fun saveSimplePdfReceipt(context: Context, fileName: String, lines: List<String>): File {
        val dir = File(context.filesDir, "receipts/pdf").apply { mkdirs() }
        val file = File(dir, if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf")

        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(300, (lines.size * 24 + 60).coerceAtLeast(400), 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas
        val paint = Paint().apply {
            textSize = 11f
            isAntiAlias = true
        }

        var yPosition = 30f
        for (line in lines) {
            canvas.drawText(line, 16f, yPosition, paint)
            yPosition += 20f
        }

        pdfDocument.finishPage(page)
        FileOutputStream(file).use { outputStream ->
            pdfDocument.writeTo(outputStream)
        }
        pdfDocument.close()
        return file
    }

    fun saveBitmapToInternalStorage(context: Context, bitmap: Bitmap, fileName: String): File {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val file = File(dir, if (fileName.endsWith(".jpg")) fileName else "$fileName.jpg")
        FileOutputStream(file).use { outputStream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
        }
        return file
    }

    fun copyUriToInternalFile(context: Context, uri: Uri, fileName: String): File? {
        val dir = File(context.filesDir, "images").apply { mkdirs() }
        val destFile = File(dir, fileName)
        return try {
            context.contentResolver.openInputStream(uri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            destFile
        } catch (e: Exception) {
            null
        }
    }
}
