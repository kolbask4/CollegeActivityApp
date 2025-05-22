package com.example.collegeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegeapp.data.PortfolioItem
import com.example.collegeapp.data.PortfolioType
import com.example.collegeapp.data.PortfolioDao
import com.example.collegeapp.data.UserDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PortfolioScreen(userDao: UserDao, portfolioDao: PortfolioDao, currentUserIin: String?) {
    var items by remember { mutableStateOf<List<PortfolioItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf<PortfolioItem?>(null) }
    var selectedType by remember { mutableStateOf<PortfolioType?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUserIin) {
        currentUserIin?.let { iin ->
            scope.launch {
                val testItems = listOf(
                    PortfolioItem(
                        userIin = iin,
                        title = "Мобильное приложение для колледжа",
                        description = "Разработка мобильного приложения для управления учебным процессом",
                        type = PortfolioType.PROJECT,
                        imageUrl = "https://example.com/project1.jpg",
                        date = System.currentTimeMillis(),
                        tags = listOf("Android", "Kotlin", "Jetpack Compose")
                    ),
                    PortfolioItem(
                        userIin = iin,
                        title = "Сертификат по программированию",
                        description = "Сертификат о прохождении курса по основам программирования",
                        type = PortfolioType.CERTIFICATE,
                        imageUrl = "https://example.com/cert1.jpg",
                        date = System.currentTimeMillis() - 86400000,
                        tags = listOf("Программирование", "Основы")
                    ),
                    PortfolioItem(
                        userIin = iin,
                        title = "Дипломная работа",
                        description = "Дипломная работа по разработке приложения",
                        type = PortfolioType.DIPLOMA,
                        imageUrl = "https://example.com/pres1.jpg",
                        date = System.currentTimeMillis() - 172800000,
                        tags = listOf("Диплом", "Проект")
                    )
                )
                items = testItems
                isLoading = false
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 80.dp)
    ) {
        Text(
            text = "Мое портфолио",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Фильтры
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedType == null,
                    onClick = { selectedType = null },
                    label = { Text("Все") }
                )
            }
            item {
                FilterChip(
                    selected = selectedType == PortfolioType.PROJECT,
                    onClick = { selectedType = PortfolioType.PROJECT },
                    label = { Text("Проекты") }
                )
            }
            item {
                FilterChip(
                    selected = selectedType == PortfolioType.CERTIFICATE,
                    onClick = { selectedType = PortfolioType.CERTIFICATE },
                    label = { Text("Сертификаты") }
                )
            }
            item {
                FilterChip(
                    selected = selectedType == PortfolioType.DIPLOMA,
                    onClick = { selectedType = PortfolioType.DIPLOMA },
                    label = { Text("Диплом") }
                )
            }
        }

        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(items.filter { selectedType == null || it.type == selectedType }) { item ->
                    PortfolioItemCard(
                        item = item,
                        onClick = { selectedItem = item }
                    )
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        FloatingActionButton(
            onClick = { showAddDialog = true },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.BottomEnd)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Добавить материал"
            )
        }
    }

    // Диалог просмотра
    selectedItem?.let { item ->
        PortfolioItemDialog(
            item = item,
            onDismiss = { selectedItem = null }
        )
    }

    // Диалог добавления
    if (showAddDialog) {
        AddPortfolioItemDialog(
            onDismiss = { showAddDialog = false },
            onAdd = { title, description, type, imageUrl ->
                currentUserIin?.let { iin ->
                    scope.launch {
                        val newItem = PortfolioItem(
                            userIin = iin,
                            title = title,
                            description = description,
                            type = type,
                            imageUrl = imageUrl,
                            date = System.currentTimeMillis(),
                            tags = emptyList()
                        )
                        portfolioDao.insertItem(newItem)
                        items = portfolioDao.getItemsByUser(iin)
                    }
                }
                showAddDialog = false
            }
        )
    }
}

@Composable
fun PortfolioItemCard(item: PortfolioItem, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Text("Изображение будет здесь")
            }
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = item.description,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = SimpleDateFormat("dd.MM.yyyy", Locale.getDefault())
                            .format(Date(item.date)),
                        fontSize = 12.sp
                    )
                    Text(
                        text = when (item.type) {
                            PortfolioType.PROJECT -> "Проект"
                            PortfolioType.CERTIFICATE -> "Сертификат"
                            PortfolioType.DIPLOMA -> "Диплом"
                        },
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PortfolioItemDialog(item: PortfolioItem, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(item.title) },
        text = {
            Column {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Изображение будет здесь")
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = item.description,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Дата: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(item.date))}",
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Тип: ${
                        when (item.type) {
                            PortfolioType.PROJECT -> "Проект"
                            PortfolioType.CERTIFICATE -> "Сертификат"
                            PortfolioType.DIPLOMA -> "Диплом"
                        }
                    }",
                    fontSize = 14.sp
                )
                if (item.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Теги: ${item.tags.joinToString(", ")}",
                        fontSize = 14.sp
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // TODO: функционал share
                    onDismiss()
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = "Поделиться"
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Поделиться")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPortfolioItemDialog(
    onDismiss: () -> Unit,
    onAdd: (String, String, PortfolioType, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var type by remember { mutableStateOf(PortfolioType.PROJECT) }
    var imageUrl by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Добавить материал") },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Название") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Описание") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                ExposedDropdownMenuBox(
                    expanded = false,
                    onExpandedChange = { }
                ) {
                    OutlinedTextField(
                        value = when (type) {
                            PortfolioType.PROJECT -> "Проект"
                            PortfolioType.CERTIFICATE -> "Сертификат"
                            PortfolioType.DIPLOMA -> "Диплом"
                        },
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Тип") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = false,
                        onDismissRequest = { }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Проект") },
                            onClick = { type = PortfolioType.PROJECT }
                        )
                        DropdownMenuItem(
                            text = { Text("Сертификат") },
                            onClick = { type = PortfolioType.CERTIFICATE }
                        )
                        DropdownMenuItem(
                            text = { Text("Диплом") },
                            onClick = { type = PortfolioType.DIPLOMA }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text("URL изображения") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank() && imageUrl.isNotBlank()) {
                        onAdd(title, description, type, imageUrl)
                    }
                }
            ) {
                Text("Добавить")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена")
            }
        }
    )
} 