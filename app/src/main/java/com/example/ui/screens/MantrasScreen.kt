package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.MantraEntity
import com.example.ui.MalaViewModel
import com.example.ui.components.AddEditMantraDialog

private val MANTRA_CATEGORIES = listOf(
    "All",
    "Shiva",
    "Krishna & Vishnu",
    "Hanuman & Ganesh",
    "Devi & Shakti",
    "Vedic & Peace"
)

@Composable
fun MantrasScreen(
    viewModel: MalaViewModel,
    mantras: List<MantraEntity>,
    activeMantra: MantraEntity?,
    onMantraSelected: (MantraEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var editingMantra by remember { mutableStateOf<MantraEntity?>(null) }
    var deletingMantraId by remember { mutableStateOf<Long?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    val filteredMantras = remember(mantras, searchQuery, selectedCategory) {
        mantras.filter { mantra ->
            val matchesCategory = when (selectedCategory) {
                "Shiva" -> mantra.name.contains("Shiva", ignoreCase = true) ||
                        mantra.name.contains("Mrityunjaya", ignoreCase = true)
                "Krishna & Vishnu" -> mantra.name.contains("Krishna", ignoreCase = true) ||
                        mantra.name.contains("Vasudeva", ignoreCase = true)
                "Hanuman & Ganesh" -> mantra.name.contains("Hanuman", ignoreCase = true) ||
                        mantra.name.contains("Ganesh", ignoreCase = true)
                "Devi & Shakti" -> mantra.name.contains("Lakshmi", ignoreCase = true) ||
                        mantra.name.contains("Durga", ignoreCase = true) ||
                        mantra.name.contains("Saraswati", ignoreCase = true)
                "Vedic & Peace" -> mantra.name.contains("Gayatri", ignoreCase = true) ||
                        mantra.name.contains("Surya", ignoreCase = true) ||
                        mantra.name.contains("Shanti", ignoreCase = true) ||
                        mantra.name.contains("Mani", ignoreCase = true)
                else -> true
            }

            val q = searchQuery.trim().lowercase()
            val matchesQuery = if (q.isEmpty()) {
                true
            } else {
                mantra.name.lowercase().contains(q) ||
                        mantra.transliteration.lowercase().contains(q) ||
                        mantra.sanskritText.lowercase().contains(q) ||
                        mantra.meaning.lowercase().contains(q)
            }

            matchesCategory && matchesQuery
        }
    }

    if (showAddDialog) {
        AddEditMantraDialog(
            initialMantra = null,
            onDismiss = { showAddDialog = false },
            onSave = { name, sanskrit, transliteration, meaning, goalMalas, beadsPerMala, colorHex ->
                viewModel.addCustomMantra(
                    name = name,
                    sanskrit = sanskrit,
                    transliteration = transliteration,
                    meaning = meaning,
                    goalMalas = goalMalas,
                    beadsPerMala = beadsPerMala,
                    colorHex = colorHex
                )
                showAddDialog = false
            }
        )
    }

    if (editingMantra != null) {
        AddEditMantraDialog(
            initialMantra = editingMantra,
            onDismiss = { editingMantra = null },
            onSave = { name, sanskrit, transliteration, meaning, goalMalas, beadsPerMala, colorHex ->
                val current = editingMantra
                if (current != null) {
                    viewModel.updateMantra(
                        current.copy(
                            name = name,
                            sanskritText = sanskrit,
                            transliteration = transliteration,
                            meaning = meaning,
                            dailyGoalMalas = goalMalas,
                            beadsPerMala = beadsPerMala,
                            colorHex = colorHex
                        )
                    )
                }
                editingMantra = null
            }
        )
    }

    if (deletingMantraId != null) {
        AlertDialog(
            onDismissRequest = { deletingMantraId = null },
            title = { Text("Delete Mantra") },
            text = { Text("Are you sure you want to remove this mantra? Its chanting history will remain in your logs.") },
            confirmButton = {
                Button(
                    onClick = {
                        deletingMantraId?.let { viewModel.deleteMantra(it) }
                        deletingMantraId = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { deletingMantraId = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Mantra Library & Goals",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                        Text(
                            text = "Select active mantra, customize daily target malas, or read verses.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    TextButton(
                        onClick = { viewModel.restoreDefaultMantras() }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Restore Defaults",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore 13", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Search by mantra, deity, or meaning...") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear search")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mantra_search_bar")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Chips
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MANTRA_CATEGORIES.forEach { category ->
                        val isSelected = selectedCategory == category
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedCategory = category },
                            label = { Text(category) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "${filteredMantras.size} ${if (filteredMantras.size == 1) "Mantra" else "Mantras"} Available",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            items(filteredMantras, key = { it.id }) { mantra ->
                val isActive = mantra.id == activeMantra?.id
                MantraGoalCard(
                    mantra = mantra,
                    isActive = isActive,
                    onSelect = {
                        viewModel.selectMantra(mantra.id)
                        onMantraSelected(mantra)
                    },
                    onEdit = { editingMantra = mantra },
                    onDelete = { deletingMantraId = mantra.id }
                )
            }

            item {
                Spacer(modifier = Modifier.height(84.dp))
            }
        }

        // FAB to add new custom mantra
        ExtendedFloatingActionButton(
            onClick = { showAddDialog = true },
            icon = { Icon(Icons.Default.Add, contentDescription = "Add Mantra") },
            text = { Text("New Custom Mantra") },
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
                .testTag("add_mantra_fab")
        )
    }
}

@Composable
private fun MantraGoalCard(
    mantra: MantraEntity,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val themeColor = Color(mantra.colorHex)
    var isExpanded by remember { mutableStateOf(false) }

    val cardBorder = if (isActive) {
        androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
    } else {
        null
    }

    Card(
        shape = RoundedCornerShape(20.dp),
        border = cardBorder,
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isActive) 3.dp else 1.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("mantra_item_${mantra.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Title Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelect() },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(16.dp)
                            .background(themeColor, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = mantra.name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                if (isActive) {
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            if (mantra.transliteration.isNotBlank()) {
                Text(
                    text = mantra.transliteration,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontStyle = FontStyle.Italic
                    ),
                    maxLines = if (isExpanded) Int.MAX_VALUE else 2,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Goal Info Pill and Lifetime Progress
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "Goal: ${mantra.dailyGoalMalas} Mala/Day (${mantra.dailyGoalMalas * mantra.beadsPerMala} Jaaps)",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }

                Text(
                    text = "${mantra.totalMalasCompleted} Malas done",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
            }

            // Expandable details: Sanskrit Script & Spiritual Contemplation
            AnimatedVisibility(
                visible = isExpanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    if (mantra.sanskritText.isNotBlank()) {
                        Surface(
                            color = themeColor.copy(alpha = 0.08f),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = mantra.sanskritText,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    lineHeight = 22.sp,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    if (mantra.meaning.isNotBlank()) {
                        Text(
                            text = "Contemplation & Meaning:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        )
                        Text(
                            text = mantra.meaning,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = { isExpanded = !isExpanded }
                ) {
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = if (isExpanded) "Hide details" else "Show details",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isExpanded) "Hide Verse" else "View Sacred Verse",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (!isActive) {
                        OutlinedButton(
                            onClick = onSelect,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.padding(end = 6.dp)
                        ) {
                            Text("Set Active", style = MaterialTheme.typography.labelMedium)
                        }
                    }

                    IconButton(onClick = onEdit, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Goal",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(36.dp)) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Delete Mantra",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}
