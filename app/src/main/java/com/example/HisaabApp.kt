package com.example

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import com.example.data.Budget
import com.example.data.Transaction
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HisaabApp(viewModel: HisaabViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsStateWithLifecycle()
    val isDarkTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
    val lang by viewModel.currentLanguage.collectAsStateWithLifecycle()

    MyApplicationTheme(darkTheme = isDarkTheme == "Dark") {
        if (!isLoggedIn) {
            AuthScreen(viewModel = viewModel, lang = lang)
        } else {
            MainContainer(viewModel = viewModel, lang = lang)
        }
    }
}

// --- Local Translation Helper ---
fun getWord(key: String, lang: String): String {
    val translations = mapOf(
        "app_title" to mapOf("EN" to "Hisaab", "BN" to "হিসাব"),
        "tagline" to mapOf("EN" to "Track Every Taka, Build Your Future", "BN" to "প্রতিটি টাকা ট্র্যাক করুন, ভবিষ্যৎ গড়ুন"),
        "welcome" to mapOf("EN" to "Welcome Back", "BN" to "স্বাগতম"),
        "login" to mapOf("EN" to "Login", "BN" to "লগইন"),
        "email" to mapOf("EN" to "Email Address", "BN" to "ইমেইল এড্রেস"),
        "password" to mapOf("EN" to "Password", "BN" to "পাসওয়ার্ড"),
        "name_field" to mapOf("EN" to "Full Name", "BN" to "পুরো নাম"),
        "forgot_password" to mapOf("EN" to "Forgot Password?", "BN" to "পাসওয়ার্ড ভুলে গেছেন?"),
        "dont_have_account" to mapOf("EN" to "New to Hisaab? Create Account", "BN" to "হিসাবে নতুন? অ্যাকাউন্ট তৈরি করুন"),
        "already_have_account" to mapOf("EN" to "Already have an account? Login", "BN" to "ইতিমধ্যে অ্যাকাউন্ট আছে? লগইন করুন"),
        "total_balance" to mapOf("EN" to "Total Balance", "BN" to "মোট ব্যালেন্স"),
        "income" to mapOf("EN" to "Income", "BN" to "আয়"),
        "expense" to mapOf("EN" to "Expense", "BN" to "ব্যয়"),
        "transfer" to mapOf("EN" to "Transfer", "BN" to "স্থানান্তর"),
        "savings_goal" to mapOf("EN" to "Monthly Savings Goal", "BN" to "মাসিক সঞ্চয় লক্ষ্য"),
        "quick_actions" to mapOf("EN" to "Quick Actions", "BN" to "ঝটপট কাজ"),
        "recent_activity" to mapOf("EN" to "Recent Activity", "BN" to "সাম্প্রতিক কর্মকাণ্ড"),
        "view_all" to mapOf("EN" to "View All", "BN" to "সব দেখুন"),
        "dashboard" to mapOf("EN" to "Dashboard", "BN" to "ড্যাশবোর্ড"),
        "transactions" to mapOf("EN" to "Transactions", "BN" to "লেনদেন"),
        "reports" to mapOf("EN" to "Reports", "BN" to "রিপোর্ট"),
        "budget" to mapOf("EN" to "Budget", "BN" to "বাজেট"),
        "profile" to mapOf("EN" to "Profile", "BN" to "প্রোফাইল"),
        "add_transaction" to mapOf("EN" to "Add Transaction", "BN" to "লেনদেন যোগ করুন"),
        "add_income" to mapOf("EN" to "Add Income", "BN" to "আয় যোগ করুন"),
        "add_expense" to mapOf("EN" to "Add Expense", "BN" to "ব্যয় যোগ করুন"),
        "add_budget" to mapOf("EN" to "Set Budget", "BN" to "বাজেট সেট করুন"),
        "amount" to mapOf("EN" to "AMOUNT", "BN" to "পরিমাণ"),
        "category" to mapOf("EN" to "Category", "BN" to "ক্যাটেগরি"),
        "date" to mapOf("EN" to "Date", "BN" to "তারিখ"),
        "account" to mapOf("EN" to "Account", "BN" to "অ্যাকাউন্ট"),
        "note" to mapOf("EN" to "Note", "BN" to "নোট"),
        "attachment" to mapOf("EN" to "Attachment", "BN" to "সংযুক্তি"),
        "upload_receipt" to mapOf("EN" to "Upload Receipt", "BN" to "রসিদ আপলোড করুন"),
        "save" to mapOf("EN" to "Save", "BN" to "সংরক্ষণ করুন")
    )
    return translations[key]?.get(lang) ?: key
}

// --- Auth Section ---
@Composable
fun AuthScreen(viewModel: HisaabViewModel, lang: String) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var isGoogleLoading by remember { mutableStateOf(false) }
    var showGoogleChooser by remember { mutableStateOf(false) }
    var showCustomGoogleInput by remember { mutableStateOf(false) }
    var customGoogleEmail by remember { mutableStateOf("") }
    var customGoogleName by remember { mutableStateOf("") }
    var isSignUp by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
            .verticalScroll(rememberScrollState()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 440.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .size(160.dp)
                    .padding(12.dp)
                    .testTag("app_logo_image")
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.hisaab_logo),
                        contentDescription = "Hisaab Logo",
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = getWord("app_title", lang),
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = getWord("tagline", lang),
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp, bottom = 32.dp)
            )

            // Form container
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    // Google sign in simulation
                    Button(
                        onClick = {
                            if (!isGoogleLoading) {
                                showGoogleChooser = true
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color(0xFF1F1F1F)
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(24.dp),
                        enabled = !isGoogleLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("google_login_button")
                    ) {
                        if (isGoogleLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Connecting to Google...", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_google_logo),
                                contentDescription = "Google Logo",
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Sign in with Google", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HorizontalDivider(modifier = Modifier.weight(1.0f))
                        Text(
                            "OR",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                        HorizontalDivider(modifier = Modifier.weight(1.0f))
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Name (only for SignUp)
                    if (isSignUp) {
                        OutlinedTextField(
                            value = name,
                            onValueChange = { name = it },
                            label = { Text(getWord("name_field", lang)) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("name_input"),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    // Email Address
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text(getWord("email", lang)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("username_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text(getWord("password", lang)) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("password_input"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    if (!isSignUp) {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Text(
                                text = getWord("forgot_password", lang),
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier
                                    .clickable { }
                                    .padding(4.dp)
                                    .testTag("forgot_password_btn")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Buttons
                    Button(
                        onClick = {
                            val trimmedEmail = email.trim()
                            val trimmedPassword = password.trim()
                            val trimmedName = name.trim()

                            if (trimmedEmail.isEmpty() || trimmedPassword.isEmpty()) {
                                val msg = if (lang == "BN") "অনুগ্রহ করে ইমেল এবং পাসওয়ার্ড উভয়ই লিখুন।" else "Please enter both Email and Password."
                                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                            } else if (!trimmedEmail.contains("@") || trimmedEmail.length < 5) {
                                val msg = if (lang == "BN") "একটি সঠিক ইমেল ঠিকানা লিখুন।" else "Please enter a valid Email address."
                                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                            } else if (trimmedPassword.length < 4) {
                                val msg = if (lang == "BN") "পাসওয়ার্ড অন্তত ৪ অক্ষরের হতে হবে।" else "Password must be at least 4 characters."
                                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                            } else {
                                if (isSignUp) {
                                    if (trimmedName.isEmpty()) {
                                        val msg = if (lang == "BN") "অনুগ্রহ করে আপনার পুরো নাম লিখুন।" else "Please enter your full name."
                                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                                    } else {
                                        viewModel.register(trimmedEmail, trimmedName, "1234")
                                        val msg = if (lang == "BN") "অ্যাকাউন্ট সফলভাবে তৈরি হয়েছে!" else "Account successfully created!"
                                        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    // Extract display name or use fallback
                                    val displayName = if (trimmedEmail.equals("2026shakil@gmail.com", ignoreCase = true)) "Shakil Ahmed" else trimmedEmail.substringBefore("@")
                                    viewModel.login(trimmedEmail, displayName, "1234")
                                    val msg = if (lang == "BN") "লগইন সফল হয়েছে!" else "Login successful!"
                                    android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("submit_button")
                    ) {
                        Text(
                            text = if (isSignUp) "Create Account" else getWord("login", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isSignUp) getWord("already_have_account", lang) else getWord("dont_have_account", lang),
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isSignUp = !isSignUp }
                            .padding(8.dp)
                            .testTag("auth_toggle_btn")
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))
        }
    }

    if (showGoogleChooser) {
        AlertDialog(
            onDismissRequest = { showGoogleChooser = false },
            title = {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_google_logo),
                        contentDescription = "Google",
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Choose an account", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                    Text("to continue to Hisaab", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Existing Account 1
                    Card(
                        onClick = {
                            showGoogleChooser = false
                            isGoogleLoading = true
                            coroutineScope.launch {
                                delay(1200)
                                viewModel.login("2026shakil@gmail.com", "Shakil Ahmed", "1234")
                                isGoogleLoading = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE8F0FE)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("SA", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8), fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Shakil Ahmed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("2026shakil@gmail.com", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Existing Account 2
                    Card(
                        onClick = {
                            showGoogleChooser = false
                            isGoogleLoading = true
                            coroutineScope.launch {
                                delay(1200)
                                viewModel.login("shakil.dev@gmail.com", "Shakil Dev", "1234")
                                isGoogleLoading = false
                            }
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFEDFBF3)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("SD", fontWeight = FontWeight.Bold, color = Color(0xFF0F9D58), fontSize = 14.sp)
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text("Shakil Dev", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text("shakil.dev@gmail.com", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    // Custom Account Choice
                    Card(
                        onClick = {
                            showGoogleChooser = false
                            showCustomGoogleInput = true
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = Icons.Default.Add, contentDescription = "Add Account", modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Use another Google account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showGoogleChooser = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (showCustomGoogleInput) {
        AlertDialog(
            onDismissRequest = { showCustomGoogleInput = false },
            title = { Text("Sign in with Google", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Enter the Google account details you wish to sign in with:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = customGoogleName,
                        onValueChange = { customGoogleName = it },
                        label = { Text("Full Name") },
                        placeholder = { Text("e.g. Shakil Chowdhury") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = customGoogleEmail,
                        onValueChange = { customGoogleEmail = it },
                        label = { Text("Google Email") },
                        placeholder = { Text("username@gmail.com") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customGoogleEmail.isNotBlank()) {
                            showCustomGoogleInput = false
                            isGoogleLoading = true
                            coroutineScope.launch {
                                delay(1500)
                                val finalName = customGoogleName.ifBlank { customGoogleEmail.substringBefore("@") }
                                viewModel.login(customGoogleEmail, finalName, "1234")
                                isGoogleLoading = false
                            }
                        }
                    },
                    enabled = customGoogleEmail.contains("@") && customGoogleEmail.length > 5
                ) {
                    Text("Sign In")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCustomGoogleInput = false }) {
                    Text("Back")
                }
            }
        )
    }
}

// --- Main Container with bottom tabs ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContainer(viewModel: HisaabViewModel, lang: String) {
    var activeTab by remember { mutableStateOf(0) } // 0: Dashboard, 1: Transactions, 2: Reports, 3: Budget, 4: Profile
    var showAddSheet by remember { mutableStateOf(false) }
    val themeState by viewModel.currentTheme.collectAsStateWithLifecycle()

    val currencySymbol = when (viewModel.currentCurrency.collectAsStateWithLifecycle().value) {
        "BDT" -> "৳"
        "USD" -> "$"
        "INR" -> "₹"
        "EUR" -> "€"
        "GBP" -> "£"
        else -> "৳"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "S",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            getWord("app_title", lang),
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 22.sp
                        )
                    }
                },
                actions = {
                    // Header controls
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                            .clickable { viewModel.toggleLanguage() }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = if (lang == "EN") "EN | বাং" else "বাংলা | EN",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(
                        onClick = { viewModel.toggleTheme() },
                        modifier = Modifier.testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (themeState == "Light") Icons.Default.Add else Icons.Default.Add,
                            contentDescription = "Theme",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                val items = listOf("dashboard", "transactions", "reports", "budget", "profile")
                val icons = listOf(
                    Icons.Default.Dashboard,
                    Icons.Default.ReceiptLong,
                    Icons.Default.BarChart,
                    Icons.Default.AccountBalanceWallet,
                    Icons.Default.Person
                )

                items.forEachIndexed { index, item ->
                    NavigationBarItem(
                        selected = activeTab == index,
                        onClick = { activeTab = index },
                        icon = {
                            Icon(
                                imageVector = icons[index],
                                contentDescription = getWord(item, lang)
                            )
                        },
                        label = {
                            Text(
                                text = getWord(item, lang),
                                fontSize = 10.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = MaterialTheme.colorScheme.onSecondaryContainer,
                            selectedTextColor = MaterialTheme.colorScheme.onSurface,
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            indicatorColor = MaterialTheme.colorScheme.secondaryContainer
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddSheet = true },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .offset(y = 10.dp)
                    .testTag("fab_add_transaction")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Transaction",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            var editingTransaction by remember { mutableStateOf<Transaction?>(null) }

            when (activeTab) {
                0 -> DashboardScreen(
                    viewModel = viewModel,
                    currency = currencySymbol,
                    lang = lang,
                    onAddClick = { showAddSheet = true },
                    onTxClick = { editingTransaction = it }
                )
                1 -> TransactionsScreen(
                    viewModel = viewModel,
                    currency = currencySymbol,
                    lang = lang,
                    onTxClick = { editingTransaction = it }
                )
                2 -> ReportsScreen(viewModel = viewModel, currency = currencySymbol, lang = lang)
                3 -> BudgetScreen(viewModel = viewModel, currency = currencySymbol, lang = lang)
                4 -> ProfileScreen(viewModel = viewModel, currency = currencySymbol, lang = lang)
            }

            if (showAddSheet) {
                AddTransactionSheet(
                    viewModel = viewModel,
                    currency = currencySymbol,
                    lang = lang,
                    onDismiss = { showAddSheet = false }
                )
            }

            if (editingTransaction != null) {
                EditTransactionSheet(
                    tx = editingTransaction!!,
                    viewModel = viewModel,
                    currency = currencySymbol,
                    lang = lang,
                    onDismiss = { editingTransaction = null }
                )
            }
        }
    }
}

// --- Dynamic Interactive Dialog / Modal Sheet for Adding transaction ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionSheet(
    viewModel: HisaabViewModel,
    currency: String,
    lang: String,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("EXPENSE") } // "INCOME", "EXPENSE", "TRANSFER"
    var category by remember { mutableStateOf("Food") }
    var account by remember { mutableStateOf("Cash Wallet") }
    var toAccount by remember { mutableStateOf("City Bank") }
    val calendarState = remember { Calendar.getInstance() }
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendarState.time)

    val incomeCategories = listOf("Salary", "Business", "Freelancing", "Investment", "Gift", "Other")
    val expenseCategories = listOf("Food", "Transportation", "Shopping", "Bills", "Medical", "Education", "Entertainment", "Travel", "Rent", "Other")
    val accounts = listOf("Cash Wallet", "City Bank", "bKash", "Nagad", "Rocket", "Upay")

    // Icons map helper
    val catIcons = mapOf(
        "Salary" to Icons.Default.Work,
        "Business" to Icons.Default.Dashboard,
        "Freelancing" to Icons.Default.ReceiptLong,
        "Investment" to Icons.Default.BarChart,
        "Gift" to Icons.Default.Person,
        "Food" to Icons.Default.Restaurant,
        "Transportation" to Icons.Default.Dashboard,
        "Shopping" to Icons.Default.ShoppingBag,
        "Bills" to Icons.Default.ReceiptLong,
        "Medical" to Icons.Default.Add,
        "Education" to Icons.Default.Settings,
        "Rent" to Icons.Default.Person,
        "Travel" to Icons.Default.Dashboard,
        "Entertainment" to Icons.Default.BarChart,
        "Other" to Icons.Default.Add
    )

    // Trigger categories list based on type
    val activeCategories = if (type == "INCOME") incomeCategories else expenseCategories

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        calendarState.timeInMillis = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        confirmButton = {},
        dismissButton = {},
        text = {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Close", modifier = Modifier.size(24.dp))
                        }
                        Text(
                            text = if (type == "INCOME") getWord("add_income", lang) else getWord("add_expense", lang),
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "History", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Type Toggle button bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(4.dp)
                    ) {
                        val types = listOf("EXPENSE", "INCOME", "TRANSFER")
                        types.forEach { t ->
                            val isSel = type == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        type = t
                                        category = if (t == "INCOME") "Salary" else "Food"
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    t,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Value Area
                    Text(
                        getWord("amount", lang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(currency, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            placeholder = { Text("0.00", fontSize = 24.sp) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier
                                .width(180.dp)
                                .testTag("amount_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Category Selector grid
                    if (type != "TRANSFER") {
                        Text(
                            getWord("category", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activeCategories.forEach { cat ->
                                val isSel = category == cat
                                val brushIcon = catIcons[cat] ?: Icons.Default.Add
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isSel) MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        .border(
                                            1.dp,
                                            if (isSel) MaterialTheme.colorScheme.secondary
                                            else Color.Transparent,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { category = cat }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = brushIcon,
                                            contentDescription = cat,
                                            tint = if (isSel) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            cat,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Account selections & date selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date picker
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                getWord("date", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { showDatePicker = true }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(formattedDateString, fontSize = 14.sp)
                                    Icon(imageVector = Icons.Default.BarChart, contentDescription = "Date", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // From Account picker
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (type == "TRANSFER") "From Account" else getWord("account", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            var showAccMenu by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { showAccMenu = true }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(account, fontSize = 14.sp)
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Drop", modifier = Modifier.size(16.dp))
                                }
                                DropdownMenu(expanded = showAccMenu, onDismissRequest = { showAccMenu = false }) {
                                    accounts.forEach { acc ->
                                        DropdownMenuItem(
                                            text = { Text(acc) },
                                            onClick = {
                                                account = acc
                                                showAccMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // To Account Picker (only for transfers)
                    if (type == "TRANSFER") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "To Account",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            var showToAccMenu by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { showToAccMenu = true }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(toAccount, fontSize = 14.sp)
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Drop", modifier = Modifier.size(16.dp))
                                }
                                DropdownMenu(expanded = showToAccMenu, onDismissRequest = { showToAccMenu = false }) {
                                    accounts.forEach { acc ->
                                        DropdownMenuItem(
                                            text = { Text(acc) },
                                            onClick = {
                                                toAccount = acc
                                                showToAccMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Note content field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            getWord("note", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("What was this for?") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Simulated receipt attachment click
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            getWord("attachment", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.outlineVariant,
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { }
                                .padding(12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Attachment",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    getWord("upload_receipt", lang),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save transaction trigger button
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull() ?: 0.0
                            if (amt > 0.0) {
                                viewModel.addTransaction(
                                    type = type,
                                    amount = amt,
                                    category = if (type == "TRANSFER") "Transfer" else category,
                                    date = calendarState.timeInMillis,
                                    account = account,
                                    toAccount = if (type == "TRANSFER") toAccount else null,
                                    note = note.ifEmpty { if (type == "TRANSFER") "Transfer to $toAccount" else category }
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_expense_button")
                    ) {
                        Text(getWord("save", lang) + " " + if (type == "INCOME") getWord("income", lang) else getWord("expense", lang), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

// --- Dynamic Interactive Dialog / Modal Sheet for Editing transaction ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditTransactionSheet(
    tx: Transaction,
    viewModel: HisaabViewModel,
    currency: String,
    lang: String,
    onDismiss: () -> Unit
) {
    var amount by remember { mutableStateOf(tx.amount.toString()) }
    var note by remember { mutableStateOf(tx.note) }
    var type by remember { mutableStateOf(tx.type) } // "INCOME", "EXPENSE", "TRANSFER"
    var category by remember { mutableStateOf(tx.category) }
    var account by remember { mutableStateOf(tx.account) }
    var toAccount by remember { mutableStateOf(tx.toAccount ?: "City Bank") }
    val calendarState = remember { Calendar.getInstance().apply { timeInMillis = tx.date } }
    var showDatePicker by remember { mutableStateOf(false) }

    val formattedDateString = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendarState.time)

    val incomeCategories = listOf("Salary", "Business", "Freelancing", "Investment", "Gift", "Other")
    val expenseCategories = listOf("Food", "Transportation", "Shopping", "Bills", "Medical", "Education", "Entertainment", "Travel", "Rent", "Other")
    val accounts = listOf("Cash Wallet", "City Bank", "bKash", "Nagad", "Rocket", "Upay")

    // Icons map helper
    val catIcons = mapOf(
        "Salary" to Icons.Default.Work,
        "Business" to Icons.Default.Dashboard,
        "Freelancing" to Icons.Default.ReceiptLong,
        "Investment" to Icons.Default.BarChart,
        "Gift" to Icons.Default.Person,
        "Food" to Icons.Default.Restaurant,
        "Transportation" to Icons.Default.Dashboard,
        "Shopping" to Icons.Default.ShoppingBag,
        "Bills" to Icons.Default.ReceiptLong,
        "Medical" to Icons.Default.Add,
        "Education" to Icons.Default.Settings,
        "Rent" to Icons.Default.Person,
        "Travel" to Icons.Default.Dashboard,
        "Entertainment" to Icons.Default.BarChart,
        "Other" to Icons.Default.Add
    )

    // Trigger categories list based on type
    val activeCategories = if (type == "INCOME") incomeCategories else expenseCategories

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = tx.date)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        calendarState.timeInMillis = it
                    }
                    showDatePicker = false
                }) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        confirmButton = {},
        dismissButton = {},
        text = {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onDismiss) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "Close", modifier = Modifier.size(24.dp))
                        }
                        Text(
                            text = "Edit Transaction",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        IconButton(onClick = {}) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = "History", tint = MaterialTheme.colorScheme.primary)
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Type Toggle button bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.surfaceContainer,
                                RoundedCornerShape(20.dp)
                            )
                            .padding(4.dp)
                    ) {
                        val types = listOf("EXPENSE", "INCOME", "TRANSFER")
                        types.forEach { t ->
                            val isSel = type == t
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else Color.Transparent)
                                    .clickable {
                                        type = t
                                        category = if (t == "INCOME") "Salary" else "Food"
                                    }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    t,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Input Value Area
                    Text(
                        getWord("amount", lang),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(currency, fontSize = 36.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        OutlinedTextField(
                            value = amount,
                            onValueChange = { amount = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            placeholder = { Text("0.00", fontSize = 24.sp) },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            modifier = Modifier
                                .width(180.dp)
                                .testTag("amount_edit_input")
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Category Selector grid
                    if (type != "TRANSFER") {
                        Text(
                            getWord("category", lang),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            activeCategories.forEach { cat ->
                                val isSel = category == cat
                                val brushIcon = catIcons[cat] ?: Icons.Default.Add
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(
                                            if (isSel) MaterialTheme.colorScheme.secondaryContainer
                                            else MaterialTheme.colorScheme.surfaceContainer
                                        )
                                        .border(
                                            1.dp,
                                            if (isSel) MaterialTheme.colorScheme.secondary
                                            else Color.Transparent,
                                            RoundedCornerShape(16.dp)
                                        )
                                        .clickable { category = cat }
                                        .padding(horizontal = 14.dp, vertical = 10.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = brushIcon,
                                            contentDescription = cat,
                                            tint = if (isSel) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.primary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            cat,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSel) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(24.dp))
                    }

                    // Account selections & date selectors
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Date picker
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                getWord("date", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { showDatePicker = true }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(formattedDateString, fontSize = 14.sp)
                                    Icon(imageVector = Icons.Default.BarChart, contentDescription = "Date", modifier = Modifier.size(16.dp))
                                }
                            }
                        }

                        // From Account picker
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                if (type == "TRANSFER") "From Account" else getWord("account", lang),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            var showAccMenu by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { showAccMenu = true }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(account, fontSize = 14.sp)
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Drop", modifier = Modifier.size(16.dp))
                                }
                                DropdownMenu(expanded = showAccMenu, onDismissRequest = { showAccMenu = false }) {
                                    accounts.forEach { acc ->
                                        DropdownMenuItem(
                                            text = { Text(acc) },
                                            onClick = {
                                                account = acc
                                                showAccMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // To Account Picker (only for transfers)
                    if (type == "TRANSFER") {
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Text(
                                "To Account",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(bottom = 4.dp)
                            )
                            var showToAccMenu by remember { mutableStateOf(false) }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceContainer)
                                    .clickable { showToAccMenu = true }
                                    .padding(vertical = 12.dp, horizontal = 12.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(toAccount, fontSize = 14.sp)
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Drop", modifier = Modifier.size(16.dp))
                                }
                                DropdownMenu(expanded = showToAccMenu, onDismissRequest = { showToAccMenu = false }) {
                                    accounts.forEach { acc ->
                                        DropdownMenuItem(
                                            text = { Text(acc) },
                                            onClick = {
                                                toAccount = acc
                                                showToAccMenu = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Note content field
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            getWord("note", lang),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                        OutlinedTextField(
                            value = note,
                            onValueChange = { note = it },
                            placeholder = { Text("What was this for?") },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Save transaction trigger button
                    Button(
                        onClick = {
                            val amt = amount.toDoubleOrNull() ?: 0.0
                            if (amt > 0.0) {
                                viewModel.updateTransaction(
                                    tx.copy(
                                        type = type,
                                        amount = amt,
                                        category = if (type == "TRANSFER") "Transfer" else category,
                                        date = calendarState.timeInMillis,
                                        account = account,
                                        toAccount = if (type == "TRANSFER") toAccount else null,
                                        note = note.ifEmpty { if (type == "TRANSFER") "Transfer to $toAccount" else category }
                                    )
                                )
                                onDismiss()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(24.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("save_expense_button")
                    ) {
                        Text("Save Changes", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    )
}

// --- Dashboard Screen tab ---
@Composable
fun DashboardScreen(
    viewModel: HisaabViewModel,
    currency: String,
    lang: String,
    onAddClick: () -> Unit,
    onTxClick: (Transaction) -> Unit
) {
    val txs by viewModel.transactions.collectAsStateWithLifecycle()
    val isBalanceHidden by viewModel.isBalanceHidden.collectAsStateWithLifecycle()

    // Calculate dynamic balance state!
    var totalIncome = 0.0
    var totalExpense = 0.0
    txs.forEach { tx ->
        if (tx.type == "INCOME") {
            totalIncome += tx.amount
        } else if (tx.type == "EXPENSE") {
            totalExpense += tx.amount
        }
    }
    val totalBalance = totalIncome - totalExpense

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Balance Summary card
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        getWord("total_balance", lang),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )

                    IconButton(onClick = { viewModel.toggleBalanceVisibility() }) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Default.Dashboard else Icons.Default.Dashboard,
                            contentDescription = "Hide",
                            tint = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                Text(
                    text = if (isBalanceHidden) "$currency ••••••" else "$currency ${formatMoney(totalBalance)}",
                    color = Color.White,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Income mini metrics
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.ArrowDownward, contentDescription = "Down", tint = GreenSecondary, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(getWord("income", lang), color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("$currency ${formatMoney(totalIncome)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }

                    // Expense mini metrics
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.ArrowUpward, contentDescription = "Up", tint = RedExpense, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(getWord("expense", lang), color = Color.White.copy(alpha = 0.7f), fontSize = 11.sp)
                            Text("$currency ${formatMoney(totalExpense)}", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Bento savings goal card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1.0f)) {
                    Text(getWord("savings_goal", lang), color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                    Text("$currency 15,000 / $currency 20,000", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                }

                // Custom Canvas progress circle loader representation
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .testTag("savings_circular_progress"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Color.LightGray.copy(alpha = 0.3f), style = Stroke(width = 4.dp.toPx()))
                        drawArc(
                            color = GreenSecondary,
                            startAngle = -90f,
                            sweepAngle = 270f,
                            useCenter = false,
                            style = Stroke(width = 4.dp.toPx())
                        )
                    }
                    Text("75%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = GreenSecondary)
                }
            }
        }



        Spacer(modifier = Modifier.height(20.dp))

        // Quick action bento cards
        Text(
            getWord("quick_actions", lang),
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            val actions = listOf(
                Triple("add_income", Icons.Default.Add, MaterialTheme.colorScheme.primaryContainer),
                Triple("add_expense", Icons.Default.Add, MaterialTheme.colorScheme.errorContainer),
                Triple("transfer", Icons.Default.BarChart, MaterialTheme.colorScheme.secondaryContainer)
            )

            actions.forEach { act ->
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceContainerLowest)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(16.dp))
                        .clickable { onAddClick() }
                        .padding(12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(act.third),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = act.second, contentDescription = act.first, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(getWord(act.first, lang), fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Recent Activity container
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                getWord("recent_activity", lang),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                getWord("view_all", lang),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { }
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (txs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No recent transaction items", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            txs.take(5).forEach { tx ->
                TransactionRowItem(tx = tx, currency = currency, onClick = { onTxClick(tx) })
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

// Custom Transaction design row
@Composable
fun TransactionRowItem(
    tx: Transaction,
    currency: String,
    onClick: () -> Unit = {},
    onDelete: (() -> Unit)? = null
) {
    val isExpense = tx.type == "EXPENSE"
    val isTransfer = tx.type == "TRANSFER"

    val iconBg = if (isExpense) MaterialTheme.colorScheme.errorContainer
    else if (isTransfer) MaterialTheme.colorScheme.secondaryContainer
    else MaterialTheme.colorScheme.primaryContainer

    val amountColor = if (isExpense) RedExpense else if (isTransfer) AmberAccent else GreenSecondary
    val amountPrefix = if (isExpense) "- " else if (isTransfer) "" else "+ "

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() },
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .padding(12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(iconBg),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isExpense) Icons.Default.ShoppingBag else Icons.Default.Work,
                        contentDescription = tx.category,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(tx.note.ifEmpty { tx.category }, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(
                        "${tx.category} • ${tx.account}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$amountPrefix$currency ${formatMoney(tx.amount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = amountColor
                )

                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = onDelete) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Delete", tint = RedExpense, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// --- Transactions List with Advanced Search Filters tab ---
@Composable
fun TransactionsScreen(
    viewModel: HisaabViewModel,
    currency: String,
    lang: String,
    onTxClick: (Transaction) -> Unit
) {
    val txs by viewModel.filteredTransactions.collectAsStateWithLifecycle()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selCat by viewModel.selectedCategoryFilter.collectAsStateWithLifecycle()
    val selAcc by viewModel.selectedAccountFilter.collectAsStateWithLifecycle()
    val selType by viewModel.selectedTypeFilter.collectAsStateWithLifecycle()

    val accountsList = listOf("Cash Wallet", "City Bank", "bKash", "Nagad", "Rocket")
    val queryCategories = listOf("Salary", "Food", "Shopping", "Bills", "Rent", "Investment")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Query search box
        OutlinedTextField(
            value = query,
            onValueChange = { viewModel.searchQuery.value = it },
            placeholder = { Text("Search transactions...") },
            leadingIcon = { Icon(imageVector = Icons.Default.Add, contentDescription = "Search") },
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Horizontal visual scrolls of filters
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Type filters
            val types = listOf("EXPENSE", "INCOME", "TRANSFER")
            types.forEach { t ->
                val isSel = selType == t
                FilterChip(
                    selected = isSel,
                    onClick = { viewModel.selectedTypeFilter.value = if (isSel) null else t },
                    label = { Text(t) }
                )
            }

            // Separator line
            Spacer(modifier = Modifier.width(1.dp))

            // Cat filters
            queryCategories.forEach { cat ->
                val isSel = selCat == cat
                FilterChip(
                    selected = isSel,
                    onClick = { viewModel.selectedCategoryFilter.value = if (isSel) null else cat },
                    label = { Text(cat) }
                )
            }

            // Account filters
            accountsList.forEach { acc ->
                val isSel = selAcc == acc
                FilterChip(
                    selected = isSel,
                    onClick = { viewModel.selectedAccountFilter.value = if (isSel) null else acc },
                    label = { Text(acc) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Clear filter button
        if (query.isNotEmpty() || selCat != null || selAcc != null || selType != null) {
            TextButton(
                onClick = {
                    viewModel.searchQuery.value = ""
                    viewModel.selectedCategoryFilter.value = null
                    viewModel.selectedAccountFilter.value = null
                    viewModel.selectedTypeFilter.value = null
                },
                modifier = Modifier.align(Alignment.End)
            ) {
                Text("Clear Filters", color = MaterialTheme.colorScheme.primary, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // List display
        if (txs.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                Text("No matching transactions", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1.0f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(txs) { tx ->
                    TransactionRowItem(tx = tx, currency = currency, onClick = { onTxClick(tx) }, onDelete = {
                        viewModel.deleteTransaction(tx)
                    })
                }
            }
        }
    }
}

// --- Reports & Analytics Screen with Custom Canvas charts tab ---
@Composable
fun ReportsScreen(viewModel: HisaabViewModel, currency: String, lang: String) {
    val txs by viewModel.transactions.collectAsStateWithLifecycle()
    val localContext = LocalContext.current

    // Categorization split logic
    var diningOutTotal = 0.0
    var groceriesTotal = 0.0
    var utilitiesTotal = 0.0
    var salaryTotal = 0.0

    txs.forEach { tx ->
        when (tx.category) {
            "Food" -> diningOutTotal += tx.amount
            "Shopping" -> groceriesTotal += tx.amount
            "Bills", "Rent" -> utilitiesTotal += tx.amount
            "Salary" -> salaryTotal += tx.amount
        }
    }

    val totalSpent = diningOutTotal + groceriesTotal + utilitiesTotal
    val maxSpent = maxOf(diningOutTotal, groceriesTotal, utilitiesTotal, 1.0)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Frequency filter tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(12.dp))
                .padding(4.dp)
        ) {
            val terms = listOf("Monthly", "Weekly", "Daily", "Yearly")
            terms.forEachIndexed { i, term ->
                val isSel = i == 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) MaterialTheme.colorScheme.surface else Color.Transparent)
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(term, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Document exports buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = { viewModel.generateAndShareReport(localContext, "CSV") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "PDF", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export PDF", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
            }

            Button(
                onClick = { viewModel.generateAndShareReport(localContext, "CSV") },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = "Excel", tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Export Excel", color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // CHART 1: Income vs Expense bar charts using Canvas
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Income vs Expense", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Last 6 months overview", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(24.dp))

                // Gradients & Bar Pairs drawing with Canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .testTag("income_vs_expense_chart")
                ) {
                    val months = listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun")
                    val incomes = listOf(0.6f, 0.75f, 0.85f, 0.65f, 0.9f, 0.8f)
                    val expenses = listOf(0.45f, 0.5f, 0.6f, 0.7f, 0.55f, 0.4f)

                    val spacing = size.width / 6f
                    val barWidth = 12.dp.toPx()

                    for (i in 0..5) {
                        val centerX = i * spacing + spacing / 2
                        val hMax = size.height - 20.dp.toPx()

                        // Income Bar (Green)
                        val barIncomeHeight = incomes[i] * hMax
                        drawRect(
                            color = GreenSecondary,
                            topLeft = Offset(centerX - barWidth - 2.dp.toPx(), size.height - barIncomeHeight),
                            size = Size(barWidth, barIncomeHeight)
                        )

                        // Expense Bar (Blue/Red)
                        val barExpenseHeight = expenses[i] * hMax
                        drawRect(
                            color = BluePrimary,
                            topLeft = Offset(centerX + 2.dp.toPx(), size.height - barExpenseHeight),
                            size = Size(barWidth, barExpenseHeight)
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun").forEach {
                        Text(it, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(36.dp), textAlign = TextAlign.Center)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CHART 2: Category Split Donut Chart representation
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column {
                        Text("Category Split", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("Highest spending area", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .testTag("category_donut_chart"),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(color = Color.LightGray.copy(alpha = 0.2f), style = Stroke(width = 12.dp.toPx()))

                        // Dining out section
                        drawArc(
                            color = BluePrimary,
                            startAngle = -90f,
                            sweepAngle = 180f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx())
                        )

                        // Groceries section
                        drawArc(
                            color = GreenSecondary,
                            startAngle = 90f,
                            sweepAngle = 100f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx())
                        )

                        // Utilities
                        drawArc(
                            color = AmberAccent,
                            startAngle = 190f,
                            sweepAngle = 80f,
                            useCenter = false,
                            style = Stroke(width = 12.dp.toPx())
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("45%", fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text("Dining", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                // Split metrics
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val splits = listOf(
                        Triple("Dining Out", diningOutTotal, BluePrimary),
                        Triple("Groceries", groceriesTotal, GreenSecondary),
                        Triple("Utilities", utilitiesTotal, AmberAccent)
                    )

                    splits.forEach { split ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(10.dp).background(split.third, CircleShape))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(split.first, fontSize = 12.sp)
                            }
                            Text("$currency ${formatMoney(split.second)}", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Card 3: AI report suggestions
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Box(
                    modifier = Modifier
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text("AI ANALYSIS", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("Your savings are 12% higher than last month!", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                Text(
                    "By reducing 'Dining Out' expenses in the third week, you've successfully saved an additional ৳4,500. You are on track for your New Year's trip goal.",
                    color = Color.White.copy(alpha = 0.8f),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Projected Savings", color = Color.White.copy(alpha = 0.7f), fontSize = 10.sp)
                        Text("$currency 28,450", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }

                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text("82% reached", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CHART 3: Net Worth Velocity using Canvas
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            modifier = Modifier.fillMaxWidth(),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Net Worth Velocity", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text("Asset vs Liability Growth", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(16.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("net_worth_velocity_chart")
                ) {
                    val path = Path()
                    val points = listOf(0.4f, 0.55f, 0.5f, 0.65f, 0.75f, 0.9f, 1.0f)
                    val stepX = size.width / 6f

                    path.moveTo(0f, size.height - points[0] * size.height)
                    for (i in 1..6) {
                        path.lineTo(i * stepX, size.height - points[i] * size.height)
                    }

                    drawPath(
                        path = path,
                        color = BluePrimary,
                        style = Stroke(width = 3.dp.toPx())
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Dec 2023", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("Today", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// --- Budgets Planner Screen tab ---
@Composable
fun BudgetScreen(viewModel: HisaabViewModel, currency: String, lang: String) {
    val txs by viewModel.transactions.collectAsStateWithLifecycle()
    val budgets by viewModel.budgets.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }
    var dialogCategory by remember { mutableStateOf("Food") }
    var dialogLimit by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Monthly Budgets",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Button(
                onClick = { showAddDialog = true },
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Set Budget")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (budgets.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No budgets set. Click Set Budget to start!", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            // Display set budgets list dynamically calculating actual expenditure!
            budgets.forEach { budget ->
                val spent = txs.filter { it.category == budget.category && it.type == "EXPENSE" }.sumOf { it.amount }
                val progress = if (budget.amountLimit > 0.0) spent / budget.amountLimit else 0.0
                val color = if (progress >= 1.0) RedExpense else if (progress >= 0.8) AmberAccent else GreenSecondary

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(budget.category, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            Text(
                                "$currency ${formatMoney(spent)} / $currency ${formatMoney(budget.amountLimit)}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        LinearProgressIndicator(
                            progress = { progress.toFloat().coerceIn(0.0f, 1.0f) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(CircleShape),
                            color = color,
                            trackColor = MaterialTheme.colorScheme.surfaceContainer
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (progress >= 1.0) "Budget Exceeded!" else if (progress >= 0.8) "80% Used" else "${(progress * 100).toInt()}% Used",
                                color = color,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )

                            val remaining = (budget.amountLimit - spent).coerceAtLeast(0.0)
                            Text(
                                "Remaining: $currency ${formatMoney(remaining)}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }

        if (showAddDialog) {
            AlertDialog(
                onDismissRequest = { showAddDialog = false },
                title = { Text("Set Category Budget") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        var expanded by remember { mutableStateOf(false) }
                        val categories = listOf("Food", "Transportation", "Shopping", "Bills", "Medical", "Education", "Entertainment", "Travel", "Rent", "Other")

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                                .clickable { expanded = true }
                                .padding(12.dp)
                        ) {
                            Text(dialogCategory)
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat) },
                                        onClick = {
                                            dialogCategory = cat
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = dialogLimit,
                            onValueChange = { dialogLimit = it },
                            label = { Text("Monthly Limit Amount") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val limit = dialogLimit.toDoubleOrNull() ?: 0.0
                            if (limit > 0.0) {
                                viewModel.setBudget(dialogCategory, limit)
                                showAddDialog = false
                            }
                        }
                    ) {
                        Text("Save")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showAddDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// --- Profile & Settings Screen tab ---
@Composable
fun ProfileScreen(viewModel: HisaabViewModel, currency: String, lang: String) {
    val email by viewModel.loggedInUserEmail.collectAsStateWithLifecycle()
    val name by viewModel.loggedInUserName.collectAsStateWithLifecycle()
    val txs by viewModel.transactions.collectAsStateWithLifecycle()
    val userAvatar by viewModel.userAvatar.collectAsStateWithLifecycle()
    
    val lastBackupTime by viewModel.lastBackupTime.collectAsStateWithLifecycle()
    val isBackingUp by viewModel.isBackingUp.collectAsStateWithLifecycle()
    val isRestoring by viewModel.isRestoring.collectAsStateWithLifecycle()
    val context = LocalContext.current

    var showFamilyInviteDialog by remember { mutableStateOf(false) }
    var familyEmail by remember { mutableStateOf("") }
    var familyStatusMessage by remember { mutableStateOf("") }

    var showSwitchGoogleDialog by remember { mutableStateOf(false) }
    var showCustomGoogleInput by remember { mutableStateOf(false) }
    var customGoogleName by remember { mutableStateOf("") }
    var customGoogleEmail by remember { mutableStateOf("") }
    var showAvatarChooser by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    val totalSavings = txs.filter { it.type == "INCOME" }.sumOf { it.amount } - txs.filter { it.type == "EXPENSE" }.sumOf { it.amount }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Upper Profile card
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Interactive Dynamic profile avatar
                Box(
                    modifier = Modifier
                        .size(88.dp)
                        .testTag("change_profile_picture_trigger")
                        .clickable { showAvatarChooser = true },
                    contentAlignment = Alignment.BottomEnd
                ) {
                    RenderAvatar(userAvatar, name.ifEmpty { "Shakil Ahmed" }, size = 80.dp, iconSize = 44.dp)
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Profile Picture",
                            tint = Color.White,
                            modifier = Modifier.size(13.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(name.ifEmpty { "Shakil Ahmed" }, fontWeight = FontWeight.Bold, fontSize = 20.sp, color = MaterialTheme.colorScheme.onSurface)
                Text(email.ifEmpty { "2026shakil@gmail.com" }, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { showSwitchGoogleDialog = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.height(36.dp).padding(horizontal = 16.dp)
                ) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = "Switch Account", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Change Google Account", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Total Savings", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("$currency ${formatMoney(totalSavings)}", fontWeight = FontWeight.Black, fontSize = 16.sp, color = GreenSecondary)
                    }

                    VerticalDivider(modifier = Modifier.height(40.dp))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Joined Date", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("June 7, 2026", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // BackUp status simulator row
        Text("Backup & Sync", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.CloudQueue, contentDescription = "Cloud", tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Google Drive Auto Sync", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Last backup: $lastBackupTime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    var isSyncOn by remember { mutableStateOf(true) }
                    Switch(checked = isSyncOn, onCheckedChange = { isSyncOn = it })
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.backupToGoogleDrive { success ->
                                if (success) {
                                    android.widget.Toast.makeText(context, "Data successfully backed up to Google Drive!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = !isBackingUp && !isRestoring,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isBackingUp) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.5.dp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Syncing...", fontSize = 11.sp)
                        } else {
                            Text("Backup Now", fontSize = 12.sp)
                        }
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.restoreFromGoogleDrive { success, msg ->
                                android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_LONG).show()
                            }
                        },
                        enabled = !isBackingUp && !isRestoring,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isRestoring) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.5.dp, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Restoring...", fontSize = 11.sp)
                        } else {
                            Text("Restore Data", fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Family Sharing controls
        Text("Family Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Family Group: Ahmed Family", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Role: Admin • Members: 3", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    Button(onClick = { showFamilyInviteDialog = true }) {
                        Text("Invite")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Advanced preferences list
        Text("Preferences", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Language selector list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleLanguage() }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Language", fontWeight = FontWeight.Medium)
                    Text(if (lang == "EN") "English" else "বাংলা", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }

                HorizontalDivider()

                // Currency options selector list
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { }
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Currency", fontWeight = FontWeight.Medium)
                    var showCurrDialog by remember { mutableStateOf(false) }
                    val currentCurrencyState by viewModel.currentCurrency.collectAsStateWithLifecycle()
                    Text(
                        currentCurrencyState,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showCurrDialog = true }
                    )

                    if (showCurrDialog) {
                        val curs = listOf("BDT", "USD", "INR", "EUR", "GBP")
                        var tempExpanded by remember { mutableStateOf(true) }
                        DropdownMenu(expanded = tempExpanded, onDismissRequest = { showCurrDialog = false }) {
                            curs.forEach { c ->
                                DropdownMenuItem(
                                    text = { Text(c) },
                                    onClick = {
                                        viewModel.setCurrency(c)
                                        showCurrDialog = false
                                        tempExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider()



                // Security setting options
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Enable Security App PIN", fontWeight = FontWeight.Medium)
                    val lockOn by viewModel.isFaceUnlockEnabled.collectAsStateWithLifecycle()
                    Switch(checked = lockOn, onCheckedChange = { viewModel.toggleSecurityPin() })
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Logout
        Button(
            onClick = { viewModel.logout() },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("logout_button")
        ) {
            Text("Logout", fontWeight = FontWeight.Bold, color = Color.White)
        }

        if (showFamilyInviteDialog) {
            AlertDialog(
                onDismissRequest = { showFamilyInviteDialog = false },
                title = { Text("Invite Family Member") },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Add their emails to invite them to track shared budget plans.", fontSize = 12.sp)
                        OutlinedTextField(
                            value = familyEmail,
                            onValueChange = { familyEmail = it },
                            label = { Text("Member Email") },
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (familyStatusMessage.isNotEmpty()) {
                            Text(familyStatusMessage, color = GreenSecondary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (familyEmail.isNotEmpty()) {
                                familyStatusMessage = "Invitation sent successfully to $familyEmail"
                                familyEmail = ""
                            }
                        }
                    ) {
                        Text("Send Invite")
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        showFamilyInviteDialog = false
                        familyStatusMessage = ""
                    }) {
                        Text("Close")
                    }
                }
            )
        }

        if (showSwitchGoogleDialog) {
            AlertDialog(
                onDismissRequest = { showSwitchGoogleDialog = false },
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_google_logo),
                            contentDescription = "Google",
                            modifier = Modifier.size(32.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Switch Google account", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text("connected to Hisaab", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        // Account 1
                        Card(
                            onClick = {
                                showSwitchGoogleDialog = false
                                coroutineScope.launch {
                                    viewModel.login("2026shakil@gmail.com", "Shakil Ahmed", "1234")
                                    android.widget.Toast.makeText(context, "Switched Google Account to Shakil Ahmed!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFE8F0FE)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("SA", fontWeight = FontWeight.Bold, color = Color(0xFF1A73E8), fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Shakil Ahmed", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("2026shakil@gmail.com", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        // Account 2
                        Card(
                            onClick = {
                                showSwitchGoogleDialog = false
                                coroutineScope.launch {
                                    viewModel.login("shakil.dev@gmail.com", "Shakil Dev", "1234")
                                    android.widget.Toast.makeText(context, "Switched Google Account to Shakil Dev!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(Color(0xFFEDFBF3)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("SD", fontWeight = FontWeight.Bold, color = Color(0xFF0F9D58), fontSize = 14.sp)
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Shakil Dev", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text("shakil.dev@gmail.com", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        // Use another Google account
                        Card(
                            onClick = {
                                showSwitchGoogleDialog = false
                                showCustomGoogleInput = true
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier.size(36.dp).clip(CircleShape).background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Use another Google account", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showSwitchGoogleDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        if (showCustomGoogleInput) {
            AlertDialog(
                onDismissRequest = { showCustomGoogleInput = false },
                title = { Text("Sign in with Google", fontWeight = FontWeight.Bold) },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text("Enter the Google account details you wish to sign in with:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedTextField(
                            value = customGoogleName,
                            onValueChange = { customGoogleName = it },
                            label = { Text("Full Name") },
                            placeholder = { Text("e.g. Shakil Chowdhury") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = customGoogleEmail,
                            onValueChange = { customGoogleEmail = it },
                            label = { Text("Google Email") },
                            placeholder = { Text("username@gmail.com") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (customGoogleEmail.isNotBlank()) {
                                showCustomGoogleInput = false
                                coroutineScope.launch {
                                    val finalName = customGoogleName.ifBlank { customGoogleEmail.substringBefore("@") }
                                    viewModel.login(customGoogleEmail, finalName, "1234")
                                    android.widget.Toast.makeText(context, "Switched Google Account to $finalName!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = customGoogleEmail.contains("@") && customGoogleEmail.length > 5
                    ) {
                        Text("Connect")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCustomGoogleInput = false }) {
                        Text("Back")
                    }
                }
            )
        }

        if (showAvatarChooser) {
            AlertDialog(
                onDismissRequest = { showAvatarChooser = false },
                title = {
                    Text(
                        text = if (lang == "BN") "প্রোফাইল পিকচার সিলেক্ট করুন" else "Select Profile Picture",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(
                            text = if (lang == "BN") "আপনার স্টাইল অনুযায়ী একটি চমৎকার আইকন বা মনোগ্রাম ব্যাকগ্রাউন্ড বেছে নিন:" else "Select an icon gradient or customized monogram background theme:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        val items = listOf(
                            "avatar_1" to (if (lang == "BN") "ক্লাসিক" else "Classic"),
                            "avatar_2" to (if (lang == "BN") "ফাইন্যান্স" else "Finance"),
                            "avatar_3" to (if (lang == "BN") "ট্রেন্ডস" else "Trends"),
                            "avatar_4" to (if (lang == "BN") "সেভিংস" else "Savings"),
                            "avatar_5" to (if (lang == "BN") "ভায়োলেট" else "Violet"),
                            "avatar_6" to (if (lang == "BN") "টিল" else "Teal"),
                            "avatar_7" to (if (lang == "BN") "সানসেট" else "Sunset"),
                            "avatar_8" to (if (lang == "BN") "ওশান" else "Ocean"),
                            "avatar_9" to (if (lang == "BN") "ষ্টার" else "Star")
                        )
                        val chunked = items.chunked(3)
                        
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            chunked.forEach { rowItems ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    rowItems.forEach { (id, label) ->
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (userAvatar == id) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                                                .border(
                                                    1.5.dp,
                                                    if (userAvatar == id) MaterialTheme.colorScheme.primary else Color.Transparent,
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable { 
                                                    viewModel.updateAvatar(id)
                                                }
                                                .padding(8.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                RenderAvatar(id, name.ifBlank { "Shakil Ahmed" }, size = 44.dp, iconSize = 24.dp, textSize = 15.sp)
                                                Spacer(modifier = Modifier.height(4.dp))
                                                Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                                            }
                                        }
                                    }
                                    repeat(3 - rowItems.size) {
                                        Spacer(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showAvatarChooser = false },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(if (lang == "BN") "সম্পন্ন" else "Done")
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(100.dp))
    }
}

// Format Money Utility Helper representation
fun formatMoney(amount: Double): String {
    return String.format(Locale.US, "%,.0f", amount)
}

@Composable
fun RenderAvatar(
    avatarId: String,
    name: String,
    size: androidx.compose.ui.unit.Dp = 80.dp,
    iconSize: androidx.compose.ui.unit.Dp = 48.dp,
    textSize: androidx.compose.ui.unit.TextUnit = 24.sp
) {
    val initials = if (name.isNotBlank()) {
        name.trim().split("\\s+".toRegex()).take(2).map { it.firstOrNull()?.uppercase() ?: "" }.joinToString("")
    } else {
        "H"
    }

    val themeColor = MaterialTheme.colorScheme.primary

    when (avatarId) {
        "avatar_1" -> { // Classic Person Blue
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF4285F4), Color(0xFF1967D2)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize))
            }
        }
        "avatar_2" -> { // Green Paid / Finance
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF34A853), Color(0xFF137333)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Paid, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize))
            }
        }
        "avatar_3" -> { // Indigo Trends
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF673AB7), Color(0xFF512DA8)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.TrendingUp, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize))
            }
        }
        "avatar_4" -> { // Orange Savings
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFFF9800), Color(0xFFE65100)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Savings, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize))
            }
        }
        "avatar_5" -> { // Violet Text Monogram
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF9C27B0), Color(0xFF7B1FA2)))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = Color.White, fontSize = textSize, fontWeight = FontWeight.Bold)
            }
        }
        "avatar_6" -> { // Teal Text Monogram
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF009688), Color(0xFF00796B)))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = Color.White, fontSize = textSize, fontWeight = FontWeight.Bold)
            }
        }
        "avatar_7" -> { // Sunset Text Monogram
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFFF5722), Color(0xFFD84315)))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = Color.White, fontSize = textSize, fontWeight = FontWeight.Bold)
            }
        }
        "avatar_8" -> { // Ocean Blue Text Monogram
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFF03A9F4), Color(0xFF0288D1)))),
                contentAlignment = Alignment.Center
            ) {
                Text(text = initials, color = Color.White, fontSize = textSize, fontWeight = FontWeight.Bold)
            }
        }
        "avatar_9" -> { // Gold Star
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(Color(0xFFFBC02D), Color(0xFFF57F17)))),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(iconSize))
            }
        }
        else -> { // Dynamic Fallback
            Box(
                modifier = Modifier
                    .size(size)
                    .clip(CircleShape)
                    .background(themeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = themeColor, modifier = Modifier.size(iconSize))
            }
        }
    }
}
