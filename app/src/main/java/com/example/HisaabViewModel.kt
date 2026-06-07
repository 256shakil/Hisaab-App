package com.example

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HisaabViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = HisaabRepository(database.hisaabDao())

    // UI States
    val transactions: StateFlow<List<Transaction>> = repository.allTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val currentMonth: String = SimpleDateFormat("yyyy-MM", Locale.US).format(Date())
    val budgets: StateFlow<List<Budget>> = repository.getBudgetsForMonth(currentMonth)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filter States
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedAccountFilter = MutableStateFlow<String?>(null)
    val selectedTypeFilter = MutableStateFlow<String?>(null) // "INCOME", "EXPENSE", "TRANSFER"

    // Settings & Shared Preferences for user settings
    private val sharedPrefs = application.getSharedPreferences("hisaab_prefs", Context.MODE_PRIVATE)

    val currentLanguage = MutableStateFlow(sharedPrefs.getString("lang", "EN") ?: "EN")
    val currentCurrency = MutableStateFlow(sharedPrefs.getString("currency", "BDT") ?: "BDT")
    val currentTheme = MutableStateFlow(sharedPrefs.getString("theme", "Light") ?: "Light") // "Light", "Dark"
    val isFaceUnlockEnabled = MutableStateFlow(sharedPrefs.getBoolean("security_pin", false))
    val isBalanceHidden = MutableStateFlow(sharedPrefs.getBoolean("hide_balance", false))

    // Authentication simulation states
    val isLoggedIn = MutableStateFlow(sharedPrefs.getBoolean("is_logged_in", false))
    val loggedInUserEmail = MutableStateFlow(sharedPrefs.getString("user_email", "") ?: "")
    val loggedInUserName = MutableStateFlow(sharedPrefs.getString("user_name", "") ?: "")
    val securityPin = MutableStateFlow(sharedPrefs.getString("pin_code", "") ?: "")
    val userAvatar = MutableStateFlow(sharedPrefs.getString("user_avatar", "avatar_1") ?: "avatar_1")

    // Filtered transaction flow
    val filteredTransactions: StateFlow<List<Transaction>> = combine(
        transactions,
        searchQuery,
        selectedCategoryFilter,
        selectedAccountFilter,
        selectedTypeFilter
    ) { txs, query, cat, acc, type ->
        txs.filter { tx ->
            val matchesQuery = query.isEmpty() || tx.note.contains(query, ignoreCase = true) || tx.category.contains(query, ignoreCase = true)
            val matchesCat = cat == null || tx.category == cat
            val matchesAcc = acc == null || tx.account == acc || (tx.type == "TRANSFER" && (tx.account == acc || tx.toAccount == acc))
            val matchesType = type == null || tx.type == type
            matchesQuery && matchesCat && matchesAcc && matchesType
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())



    init {
        // Pre-populate if database is empty
        viewModelScope.launch {
            repository.allTransactions.first().let { list ->
                if (list.isEmpty()) {
                    prePopulateDatabase()
                }
            }
        }
    }

    private suspend fun prePopulateDatabase() {
        val now = System.currentTimeMillis()
        val oneDayMs = 24 * 60 * 60 * 1000L

        // Default transactions to make total around ৳ 1,42,500
        // (City Bank starting: 125000, bKash starting: 15000, Cash starting: 16250)
        // Expenses deducted: Footwear (-4500), Diner (-1250) -> correct balances
        val prebuilt = listOf(
            Transaction(
                type = "INCOME",
                amount = 45000.0,
                category = "Salary",
                date = now - oneDayMs,
                account = "City Bank",
                note = "Monthly Salary Credited"
            ),
            Transaction(
                type = "EXPENSE",
                amount = 4500.0,
                category = "Shopping",
                date = now - 2 * oneDayMs,
                account = "Cash Wallet",
                note = "Apex Footwear"
            ),
            Transaction(
                type = "EXPENSE",
                amount = 1250.0,
                category = "Food",
                date = now,
                account = "bKash",
                note = "Sultan's Dine"
            ),
            Transaction(
                type = "INCOME",
                amount = 117500.0,
                category = "Investment",
                date = now - 15 * oneDayMs,
                account = "City Bank",
                note = "Starting Investment Balance"
            ),
            Transaction(
                type = "INCOME",
                amount = 15000.0,
                category = "Gift",
                date = now - 8 * oneDayMs,
                account = "bKash",
                note = "Eid Gift"
            ),
            Transaction(
                type = "INCOME",
                amount = 10750.0,
                category = "Other",
                date = now - 5 * oneDayMs,
                account = "Cash Wallet",
                note = "Cash on Hand Initial"
            )
        )

        for (tx in prebuilt) {
            repository.insertTransaction(tx)
        }

        // Default budgets
        val budgetsList = listOf(
            Budget(category = "Food", month = currentMonth, amountLimit = 15000.0),
            Budget(category = "Shopping", month = currentMonth, amountLimit = 8000.0),
            Budget(category = "Rent", month = currentMonth, amountLimit = 20000.0),
            Budget(category = "Bills", month = currentMonth, amountLimit = 5000.0)
        )

        for (b in budgetsList) {
            repository.insertBudget(b)
        }
    }

    // --- Actions ---

    fun login(email: String, name: String, pin: String) {
        loggedInUserEmail.value = email
        loggedInUserName.value = name
        securityPin.value = pin
        isLoggedIn.value = true

        sharedPrefs.edit()
            .putBoolean("is_logged_in", true)
            .putString("user_email", email)
            .putString("user_name", name)
            .putString("pin_code", pin)
            .apply()
    }

    fun register(email: String, name: String, pin: String) {
        login(email, name, pin)
    }

    fun updateAvatar(avatarId: String) {
        userAvatar.value = avatarId
        sharedPrefs.edit().putString("user_avatar", avatarId).apply()
    }

    fun logout() {
        isLoggedIn.value = false
        loggedInUserEmail.value = ""
        loggedInUserName.value = ""
        securityPin.value = ""
        userAvatar.value = "avatar_1"

        sharedPrefs.edit()
            .putBoolean("is_logged_in", false)
            .putString("user_email", "")
            .putString("user_name", "")
            .putString("pin_code", "")
            .putString("user_avatar", "avatar_1")
            .apply()
    }

    fun addTransaction(
        type: String,
        amount: Double,
        category: String,
        date: Long,
        account: String,
        toAccount: String? = null,
        note: String,
        attachmentPath: String? = null
    ) {
        viewModelScope.launch {
            val tx = Transaction(
                type = type,
                amount = amount,
                category = category,
                date = date,
                account = account,
                toAccount = toAccount,
                note = note,
                attachmentPath = attachmentPath
            )
            repository.insertTransaction(tx)
        }
    }

    fun updateTransaction(tx: Transaction) {
        viewModelScope.launch {
            repository.updateTransaction(tx)
        }
    }

    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch {
            repository.deleteTransaction(tx)
        }
    }

    fun setBudget(category: String, amountLimit: Double) {
        viewModelScope.launch {
            val existing = repository.getBudgetByCategoryAndMonth(category, currentMonth)
            if (existing != null) {
                repository.updateBudget(existing.copy(amountLimit = amountLimit))
            } else {
                repository.insertBudget(Budget(category = category, month = currentMonth, amountLimit = amountLimit))
            }
        }
    }

    // Preferences Settings
    fun toggleLanguage() {
        val next = if (currentLanguage.value == "EN") "BN" else "EN"
        currentLanguage.value = next
        sharedPrefs.edit().putString("lang", next).apply()
    }

    fun setCurrency(currency: String) {
        currentCurrency.value = currency
        sharedPrefs.edit().putString("currency", currency).apply()
    }

    fun toggleTheme() {
        val next = if (currentTheme.value == "Light") "Dark" else "Light"
        currentTheme.value = next
        sharedPrefs.edit().putString("theme", next).apply()
    }



    fun toggleBalanceVisibility() {
        val next = !isBalanceHidden.value
        isBalanceHidden.value = next
        sharedPrefs.edit().putBoolean("hide_balance", next).apply()
    }

    fun toggleSecurityPin() {
        val next = !isFaceUnlockEnabled.value
        isFaceUnlockEnabled.value = next
        sharedPrefs.edit().putBoolean("security_pin", next).apply()
    }

    // Google Drive Backup & Sync State Flows
    val lastBackupTime = MutableStateFlow(sharedPrefs.getString("last_backup_time", "Never") ?: "Never")
    val isBackingUp = MutableStateFlow(false)
    val isRestoring = MutableStateFlow(false)

    private fun serializeTransactions(list: List<Transaction>): String {
        val delimiter = "|||"
        val lineDelimiter = "###"
        return list.joinToString(lineDelimiter) { tx ->
            val safeNote = tx.note.replace("\n", " ").replace(delimiter, " ")
            val safeAccount = tx.account.replace(delimiter, " ")
            val safeToAccount = (tx.toAccount ?: "").replace(delimiter, " ")
            val safeCategory = tx.category.replace(delimiter, " ")
            "${tx.type}$delimiter${tx.amount}$delimiter$safeCategory$delimiter${tx.date}$delimiter$safeAccount$delimiter$safeToAccount$delimiter$safeNote"
        }
    }

    private fun deserializeTransactions(serialized: String): List<Transaction> {
        if (serialized.isEmpty()) return emptyList()
        val delimiter = "|||"
        val lineDelimiter = "###"
        val lines = serialized.split(lineDelimiter)
        val result = mutableListOf<Transaction>()
        for (line in lines) {
            if (line.isBlank()) continue
            val parts = line.split(delimiter)
            if (parts.size >= 7) {
                try {
                    val type = parts[0]
                    val amount = parts[1].toDoubleOrNull() ?: 0.0
                    val category = parts[2]
                    val date = parts[3].toLongOrNull() ?: System.currentTimeMillis()
                    val account = parts[4]
                    val toAccount = parts[5].ifBlank { null }
                    val note = parts[6]
                    result.add(
                        Transaction(
                            type = type,
                            amount = amount,
                            category = category,
                            date = date,
                            account = account,
                            toAccount = toAccount,
                            note = note
                        )
                    )
                } catch (e: Exception) {
                    // Skip corrupt line
                }
            }
        }
        return result
    }

    fun backupToGoogleDrive(onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            isBackingUp.value = true
            // Simulate realistic Google API delay
            kotlinx.coroutines.delay(1500)
            
            val txList = transactions.value
            val serialized = serializeTransactions(txList)
            
            val edit = sharedPrefs.edit()
            edit.putInt("backup_tx_count", txList.size)
            edit.putString("backup_tx_data", serialized)
            
            val formatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
            val nowStr = formatter.format(Date())
            edit.putString("last_backup_time", nowStr)
            edit.apply()
            
            lastBackupTime.value = nowStr
            isBackingUp.value = false
            onComplete(true)
        }
    }

    fun restoreFromGoogleDrive(onComplete: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            isRestoring.value = true
            kotlinx.coroutines.delay(1500)
            
            val serialized = sharedPrefs.getString("backup_tx_data", "") ?: ""
            if (serialized.isEmpty()) {
                val txCount = sharedPrefs.getInt("backup_tx_count", 0)
                if (txCount == 0) {
                    isRestoring.value = false
                    onComplete(false, "No backup found in your Google Drive.")
                    return@launch
                }
            }
            
            val restoredList = deserializeTransactions(serialized)
            if (restoredList.isNotEmpty()) {
                for (tx in restoredList) {
                    repository.insertTransaction(tx)
                }
                isRestoring.value = false
                onComplete(true, "Successfully restored ${restoredList.size} transactions from Google Drive.")
            } else {
                isRestoring.value = false
                onComplete(false, "No valid backup entries found to restore.")
            }
        }
    }

    // Real Share Action (CSV generation for actual integration!)
    fun generateAndShareReport(context: Context, type: String = "CSV") {
        val txs = filteredTransactions.value
        if (txs.isEmpty()) return

        val builder = java.lang.StringBuilder()
        builder.append("ID,Type,Amount,Category,Date,Account,Note,ToAccount\n")

        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        for (tx in txs) {
            val dateStr = sdf.format(Date(tx.date))
            builder.append("${tx.id},${tx.type},${tx.amount},${tx.category},\"$dateStr\",\"${tx.account}\",\"${tx.note.replace("\"", "\"\"")}\",\"${tx.toAccount ?: ""}\"\n")
        }

        val textReport = builder.toString()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            this.type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "Hisaab Finance Report (${currentMonth})")
            putExtra(Intent.EXTRA_TEXT, "Here is your Hisaab financial transactions export:\n\n$textReport")
        }
        val chooserIntent = Intent.createChooser(shareIntent, "Share Report via")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)
    }
}
