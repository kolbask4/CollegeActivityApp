package com.example.collegeapp.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.collegeapp.data.Grade
import com.example.collegeapp.data.User
import com.example.collegeapp.data.UserDao
import com.example.collegeapp.data.GradeDao
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun LoginScreen(
    onLoginSuccess: (String) -> Unit,
    onNavigateToRegister: () -> Unit,
    userDao: UserDao
) {
    var iin by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Вход",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = iin,
            onValueChange = { iin = it },
            label = { Text("ИИН") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                scope.launch {
                    val user = userDao.getUser(iin, password)
                    if (user != null) {
                        onLoginSuccess(iin)
                    } else {
                        error = "Неверный ИИН или пароль"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Войти")
        }
        
        TextButton(
            onClick = onNavigateToRegister,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Зарегистрироваться")
        }
    }
}

@Composable
fun RegisterScreen(
    onRegisterSuccess: (String) -> Unit,
    onNavigateToLogin: () -> Unit,
    userDao: UserDao,
    gradeDao: GradeDao
) {
    var name by remember { mutableStateOf("") }
    var iin by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Регистрация",
            style = MaterialTheme.typography.headlineLarge
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Имя") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = iin,
            onValueChange = { iin = it },
            label = { Text("ИИН") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        if (error != null) {
            Text(
                text = error!!,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = {
                scope.launch {
                    if (name.isBlank() || iin.isBlank() || password.isBlank()) {
                        error = "Все поля должны быть заполнены"
                        return@launch
                    }
                    
                    val existingUser = userDao.getUserByIin(iin)
                    if (existingUser != null) {
                        error = "Пользователь с таким ИИН уже существует"
                        return@launch
                    }
                    
                    val course = Random.nextInt(1, 5)
                    val user = User(iin = iin, name = name, password = password, course = course)
                    userDao.insertUser(user)
                    
                    for (courseNum in 1..course) {
                        val grade = Grade(
                            userIin = iin,
                            score = Random.nextInt(0, 101),
                            course = courseNum
                        )
                        gradeDao.insertGrade(grade)
                    }
                    
                    onRegisterSuccess(iin)
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Зарегистрироваться")
        }
        
        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Уже есть аккаунт? Войти")
        }
    }
} 