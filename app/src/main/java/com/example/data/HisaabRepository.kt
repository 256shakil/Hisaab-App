package com.example.data

import kotlinx.coroutines.flow.Flow

class HisaabRepository(private val hisaabDao: HisaabDao) {

    val allTransactions: Flow<List<Transaction>> = hisaabDao.getAllTransactions()

    fun getBudgetsForMonth(month: String): Flow<List<Budget>> {
        return hisaabDao.getBudgetsForMonth(month)
    }

    suspend fun insertTransaction(transaction: Transaction) {
        hisaabDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        hisaabDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        hisaabDao.deleteTransaction(transaction)
    }

    suspend fun deleteTransactionById(id: Int) {
        hisaabDao.deleteTransactionById(id)
    }

    suspend fun insertBudget(budget: Budget) {
        hisaabDao.insertBudget(budget)
    }

    suspend fun updateBudget(budget: Budget) {
        hisaabDao.updateBudget(budget)
    }

    suspend fun deleteBudget(budget: Budget) {
        hisaabDao.deleteBudget(budget)
    }

    suspend fun getBudgetByCategoryAndMonth(category: String, month: String): Budget? {
        return hisaabDao.getBudgetByCategoryAndMonth(category, month)
    }
}
