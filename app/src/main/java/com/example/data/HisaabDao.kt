package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface HisaabDao {

    // --- Transactions ---
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction)

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    @Query("DELETE FROM transactions WHERE id = :id")
    suspend fun deleteTransactionById(id: Int)

    // --- Budgets ---
    @Query("SELECT * FROM budgets WHERE month = :month")
    fun getBudgetsForMonth(month: String): Flow<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudget(budget: Budget)

    @Update
    suspend fun updateBudget(budget: Budget)

    @Delete
    suspend fun deleteBudget(budget: Budget)

    @Query("SELECT * FROM budgets WHERE category = :category AND month = :month LIMIT 1")
    suspend fun getBudgetByCategoryAndMonth(category: String, month: String): Budget?
}
