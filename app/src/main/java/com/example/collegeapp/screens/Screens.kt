package com.example.collegeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.collegeapp.data.Grade
import com.example.collegeapp.data.GradeDao
import com.example.collegeapp.data.UserDao
import kotlinx.coroutines.launch
import kotlin.random.Random

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Plan : Screens("plan")
    object Portfolio : Screens("portfolio")
    object Profile : Screens("profile")
}

@Composable
fun HomeScreen(userDao: UserDao, gradeDao: GradeDao, currentUserIin: String?) {
    var userName by remember { mutableStateOf("") }
    var grades by remember { mutableStateOf<List<Grade>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    fun loadGrades() {
        scope.launch {
            currentUserIin?.let { iin ->
                grades = gradeDao.getGradesByUser(iin)
            }
        }
    }

    LaunchedEffect(currentUserIin) {
        if (currentUserIin == null) {
            error = "Пользователь не авторизован"
            isLoading = false
            return@LaunchedEffect
        }

        try {
            scope.launch {
                userDao.getUserByIin(currentUserIin)?.let { user ->
                    userName = user.name
                } ?: run {
                    error = "Пользователь не найден"
                }
                loadGrades()
                isLoading = false
            }
        } catch (e: Exception) {
            error = "Ошибка при загрузке данных: ${e.message}"
            isLoading = false
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
            when {
                isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = error!!,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
                else -> {
                    GreetingSection(userName = userName)
                    Spacer(Modifier.height(16.dp))
                    GradesSection(
                        grades = grades,
                        gradeDao = gradeDao,
                        onGradeUpdated = { loadGrades() }
                    )
                    Spacer(Modifier.height(16.dp))
                    NotificationsSection()
                    Spacer(Modifier.height(16.dp))
                    AssignmentsSection()
                }
            }
        }
    }
}

@Composable
fun ProfileScreen(
    userDao: UserDao,
    currentUserIin: String?,
    navController: NavHostController,
    onLogout: () -> Unit
) {
    var userName by remember { mutableStateOf("") }
    var userCourse by remember { mutableStateOf(0) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(currentUserIin) {
        currentUserIin?.let { iin ->
            scope.launch {
                userDao.getUserByIin(iin)?.let { user ->
                    userName = user.name
                    userCourse = user.course
                }
            }
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .systemBarsPadding(),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(20.dp),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .background(
                                MaterialTheme.colorScheme.primary,
                                shape = CircleShape
                            )
                            .padding(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Профиль",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Имя: $userName",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "ИИН: ${currentUserIin ?: "Не указан"}",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Группа: УИБ-2-24",
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Курс: $userCourse",
                        fontSize = 18.sp
                    )
                }
            }
        }

        item {
            Button(
                onClick = onLogout,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Выйти",
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Выйти", color = Color.White)
            }
        }
    }
}

@Composable
fun GreetingSection(userName: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Добро пожаловать, $userName!",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Хорошего дня!",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun GradesSection(grades: List<Grade>, gradeDao: GradeDao, onGradeUpdated: () -> Unit) {
    var editingGradeId by remember { mutableStateOf<Int?>(null) }
    var editingScore by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
        ) {
            Text(
                text = "Ваши оценки",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            grades.forEach { grade ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${grade.course} курс",
                        fontSize = 16.sp
                    )
                    if (editingGradeId == grade.id) {
                        OutlinedTextField(
                            value = editingScore,
                            onValueChange = { 
                                if (it.isEmpty() || (it.toIntOrNull() != null && it.toInt() in 0..100)) {
                                    editingScore = it
                                }
                            },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.width(80.dp),
                            singleLine = true
                        )
                        IconButton(
                            onClick = {
                                editingScore.toIntOrNull()?.let { score ->
                                    scope.launch {
                                        gradeDao.updateGradeScore(grade.id, score)
                                        editingGradeId = null
                                        onGradeUpdated()
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Check, contentDescription = "Сохранить")
                        }
                        IconButton(
                            onClick = { editingGradeId = null }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Отмена")
                        }
                    } else {
                        Surface(
                            shape = MaterialTheme.shapes.small,
                            color = when {
                                grade.score >= 90 -> MaterialTheme.colorScheme.primaryContainer
                                grade.score >= 75 -> MaterialTheme.colorScheme.secondaryContainer
                                grade.score >= 60 -> MaterialTheme.colorScheme.tertiaryContainer
                                else -> MaterialTheme.colorScheme.errorContainer
                            },
                            modifier = Modifier
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .clickable {
                                    editingGradeId = grade.id
                                    editingScore = grade.score.toString()
                                }
                        ) {
                            Text(
                                text = grade.score.toString(),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    grade.score >= 90 -> MaterialTheme.colorScheme.onPrimaryContainer
                                    grade.score >= 75 -> MaterialTheme.colorScheme.onSecondaryContainer
                                    grade.score >= 60 -> MaterialTheme.colorScheme.onTertiaryContainer
                                    else -> MaterialTheme.colorScheme.onErrorContainer
                                },
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun NotificationsSection() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Уведомления",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Новых уведомлений нет",
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun AssignmentsSection() {
    val subjects = listOf("Математика", "Русский язык", "Английский язык", "Информатика")
    val random = remember { Random }

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Задания",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            subjects.forEach { subject ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = subject,
                        fontSize = 16.sp
                    )
                    Text(
                        text = "стр. ${random.nextInt(50, 181)}, упр. ${random.nextInt(1, 5)}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}