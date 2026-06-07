package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "INCOME" or "EXPENSE" or "TRANSFER"
    val amount: Double,
    val category: String, // e.g. "Food", "Transportation", "Shopping", "Bills", "Medical", "Education", "Entertainment", "Travel", "Rent", "Salary", "Business", "Freelancing", "Investment", "Gift", "Other"
    val date: Long, // timestamp
    val account: String, // e.g. "Cash Wallet", "City Bank", "bKash", "Nagad", "Rocket", "Upay"
    val toAccount: String? = null, // only used for "TRANSFER"
    val note: String = "",
    val attachmentPath: String? = null // local path or simulated URI for receipt
)
