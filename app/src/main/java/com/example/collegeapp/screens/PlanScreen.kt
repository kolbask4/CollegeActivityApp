package com.example.collegeapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.collegeapp.data.Goal
import com.example.collegeapp.data.GoalDao
import com.example.collegeapp.data.UserDao
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun PlanScreen(userDao: UserDao, goalDao: GoalDao, currentUserIin: String?) {
    var goals by remember { mutableStateOf<List<Goal>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var showAddGoalDialog by remember { mutableStateOf(false) }
    var selectedGoal by remember { mutableStateOf<Goal?>(null) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUserIin) {
        currentUserIin?.let { iin ->
            scope.launch {
                goals = goalDao.getGoalsByUser(iin)
                isLoading = false
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding()
            .navigationBarsPadding()
            .padding(bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Мой план развития",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        } else {
            items(goals) { goal ->
                GoalCard(
                    goal = goal,
                    onGoalClick = { selectedGoal = goal }
                )
            }

            item {
                Button(
                    onClick = { showAddGoalDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить цель"
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Добавить цель")
                }
            }
        }
    }

    if (showAddGoalDialog) {
        AddGoalDialog(
            onDismiss = { showAddGoalDialog = false },
            onAddGoal = { title, description, deadline ->
                currentUserIin?.let { iin ->
                    scope.launch {
                        val goal = Goal(
                            userIin = iin,
                            title = title,
                            description = description,
                            progress = 0,
                            deadline = deadline
                        )
                        goalDao.insertGoal(goal)
                        goals = goalDao.getGoalsByUser(iin)
                    }
                }
                showAddGoalDialog = false
            }
        )
    }

    selectedGoal?.let { goal ->
        GoalDetailsDialog(
            goal = goal,
            onDismiss = { selectedGoal = null },
            onUpdate = { updatedGoal ->
                scope.launch {
                    goalDao.updateGoal(updatedGoal)
                    goals = goalDao.getGoalsByUser(currentUserIin ?: return@launch)
                }
            },
            onDelete = { deletedGoal ->
                scope.launch {
                    goalDao.deleteGoal(deletedGoal.id)
                    goals = goalDao.getGoalsByUser(currentUserIin ?: return@launch)
                }
            }
        )
    }
}

@Composable
fun GoalCard(goal: Goal, onGoalClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onGoalClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = goal.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                if (goal.progress == 100) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Цель выполнена",
                        tint = MaterialTheme.colorScheme.primary
                    )
                } else {
                    LinearProgressIndicator(
                        progress = goal.progress / 100f,
                        modifier = Modifier
                            .width(100.dp)
                            .height(8.dp)
                            .clip(MaterialTheme.shapes.small),
                        trackColor = MaterialTheme.colorScheme.surfaceVariant,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = goal.description,
                fontSize = 14.sp
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Дедлайн: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(goal.deadline))}",
                fontSize = 12.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddGoalDialog(
    onDismiss: () -> Unit,
    onAddGoal: (String, String, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var deadline by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = deadline
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Новая цель") },
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
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Выбрать дату")
                }
                Text(
                    text = "Выбранная дата: ${SimpleDateFormat("dd.MM.yyyy", Locale.getDefault()).format(Date(deadline))}",
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (title.isNotBlank() && description.isNotBlank()) {
                        onAddGoal(title, description, deadline)
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

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            deadline = it
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            }
        ) {
            DatePicker(
                state = datePickerState,
                showModeToggle = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalDetailsDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onUpdate: (Goal) -> Unit,
    onDelete: (Goal) -> Unit
) {
    var title by remember { mutableStateOf(goal.title) }
    var description by remember { mutableStateOf(goal.description) }
    var progress by remember { mutableStateOf(goal.progress) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Детали цели") },
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
                Text("Прогресс: $progress%")
                Slider(
                    value = progress.toFloat(),
                    onValueChange = { progress = it.toInt() },
                    valueRange = 0f..100f,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(
                    onClick = {
                        onDelete(goal)
                        onDismiss()
                    }
                ) {
                    Text("Удалить", color = MaterialTheme.colorScheme.error)
                }
                TextButton(
                    onClick = {
                        onUpdate(goal.copy(
                            title = title,
                            description = description,
                            progress = progress
                        ))
                        onDismiss()
                    }
                ) {
                    Text("Сохранить")
                }
            }
        },
        dismissButton = {}
    )
} 