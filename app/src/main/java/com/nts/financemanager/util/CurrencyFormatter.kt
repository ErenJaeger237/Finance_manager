package com.nts.financemanager.util

import java.text.NumberFormat
import java.util.Locale

/**
 * Formats a Double amount as FCFA currency string.
 * Example: 15000.0 → "15 000 FCFA"
 */
object CurrencyFormatter {
    private val formatter = NumberFormat.getNumberInstance(Locale.FRANCE).apply {
        minimumFractionDigits = 0
        maximumFractionDigits = 0
    }

    fun format(amount: Double): String {
        return "${formatter.format(amount)} FCFA"
    }

    fun formatCompact(amount: Double): String {
        return when {
            amount >= 1_000_000 -> "${(amount / 1_000_000).toInt()}M"
            amount >= 1_000 -> "${(amount / 1_000).toInt()}k"
            else -> amount.toInt().toString()
        }
    }
}
