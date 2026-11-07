package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrivacyPolicyScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Privacy Policy", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PrivacyTip,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(36.dp)
                )
                Column {
                    Text(
                        text = "Mala Jaap Privacy Policy",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Last updated: October 2026",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.outlineVariant)

            PolicySection(
                title = "1. Introduction",
                content = "Welcome to Mala Jaap ('we', 'our', or 'us'). We respect your privacy and are committed to protecting your personal data. This privacy policy explains how our application collects, uses, and safeguards your information when you use our mobile application."
            )

            PolicySection(
                title = "2. Information We Collect",
                content = "• Local Chanting Data: Your Jaap counts, streak records, mala completion history, and mantra preferences are stored locally on your device using a secure local database (Room DB).\n• Device & Usage Data: Standard diagnostic data and non-personal analytics may be collected via Google Play Services and AdMob to ensure app stability and performance."
            )

            PolicySection(
                title = "3. How We Use Your Information",
                content = "The information collected is used solely to:\n• Provide, maintain, and improve your daily meditation and chanting experience.\n• Track your personal spiritual streaks and progress statistics.\n• Display non-intrusive advertisements (via Google AdMob) to support free access to the app."
            )

            PolicySection(
                title = "4. Data Security & Storage",
                content = "Your meditation logs and personal japa records remain securely stored on your device's internal storage sandbox. We do not sell, trade, or rent your personal data to third parties."
            )

            PolicySection(
                title = "5. Third-Party Services",
                content = "Our app integrates with Google Play Services and Google AdMob, which may collect anonymous identifiers and device data in accordance with their respective privacy policies."
            )

            PolicySection(
                title = "6. Children's Privacy",
                content = "Our app is designed for general audiences and does not knowingly collect any personal information from children under the age of 13."
            )

            PolicySection(
                title = "7. Contact Us",
                content = "If you have any questions or suggestions about our Privacy Policy, please feel free to contact us through the Google Play Store support page."
            )

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun PolicySection(title: String, content: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = content,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
