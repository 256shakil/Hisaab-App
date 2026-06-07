package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "budgets")
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val category: String, // e.g. "Food", "Shopping", "Total" (for overall budget)
    val month: String, // "YYYY-MM" e.g. "2026-06"
    val amountLimit: Double,
    val amountSpent: Double = 0.0
)
