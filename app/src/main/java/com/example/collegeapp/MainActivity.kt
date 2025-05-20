package com.example.collegeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.collegeapp.data.AppDatabase
import com.example.collegeapp.data.UserDao
import com.example.collegeapp.data.GradeDao
import com.example.collegeapp.data.GoalDao
import com.example.collegeapp.data.PortfolioDao
import com.example.collegeapp.navigation.Navigation
import com.example.collegeapp.navigation.Screens
import com.example.collegeapp.screens.auth.LoginScreen
import com.example.collegeapp.screens.auth.RegisterScreen
import com.example.collegeapp.ui.theme.CollegeAppTheme

class MainActivity : ComponentActivity() {
    private lateinit var userDao: UserDao
    private lateinit var gradeDao: GradeDao
    private lateinit var goalDao: GoalDao
    private lateinit var portfolioDao: PortfolioDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        gradeDao = db.gradeDao()
        goalDao = db.goalDao()
        portfolioDao = db.portfolioDao()
        enableEdgeToEdge()
        setContent {
            CollegeAppTheme {
                AuthScreen(userDao = userDao, gradeDao = gradeDao, goalDao = goalDao, portfolioDao = portfolioDao)
            }
        }
    }
}

@Composable
fun AuthScreen(userDao: UserDao, gradeDao: GradeDao, goalDao: GoalDao, portfolioDao: PortfolioDao) {
    var isAuthenticated by remember { mutableStateOf(false) }
    var showRegister by remember { mutableStateOf(false) }
    var currentUserIin by remember { mutableStateOf<String?>(null) }

    if (!isAuthenticated) {
        if (showRegister) {
            RegisterScreen(
                onRegisterSuccess = { iin ->
                    isAuthenticated = true
                    showRegister = false
                    currentUserIin = iin
                },
                onNavigateToLogin = {
                    showRegister = false
                },
                userDao = userDao,
                gradeDao = gradeDao
            )
        } else {
            LoginScreen(
                onLoginSuccess = { iin ->
                    isAuthenticated = true
                    currentUserIin = iin
                },
                onNavigateToRegister = {
                    showRegister = true
                },
                userDao = userDao
            )
        }
    } else {
        MainScreen(userDao = userDao, gradeDao = gradeDao, goalDao = goalDao, portfolioDao = portfolioDao, currentUserIin = currentUserIin)
    }
}

@Composable
fun MainScreen(userDao: UserDao, gradeDao: GradeDao, goalDao: GoalDao, portfolioDao: PortfolioDao, currentUserIin: String?) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = "Главная") },
                    label = { Text("Главная") },
                    selected = currentRoute == Screens.Home.route,
                    onClick = {
                        navController.navigate(Screens.Home.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.round_folder_24), contentDescription = "План") },
                    label = { Text("План") },
                    selected = currentRoute == Screens.Plan.route,
                    onClick = {
                        navController.navigate(Screens.Plan.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.round_folder_24), contentDescription = "Портфолио") },
                    label = { Text("Портфолио") },
                    selected = currentRoute == Screens.Portfolio.route,
                    onClick = {
                        navController.navigate(Screens.Portfolio.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
                NavigationBarItem(
                    icon = { Icon(painterResource(R.drawable.round_folder_24), contentDescription = "Профиль") },
                    label = { Text("Профиль") },
                    selected = currentRoute == Screens.Profile.route,
                    onClick = {
                        navController.navigate(Screens.Profile.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        Navigation(
            navController = navController,
            userDao = userDao,
            gradeDao = gradeDao,
            goalDao = goalDao,
            portfolioDao = portfolioDao,
            currentUserIin = currentUserIin
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MainScreenPreview() {
    CollegeAppTheme {
        val db = AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
        MainScreen(
            userDao = db.userDao(),
            gradeDao = db.gradeDao(),
            goalDao = db.goalDao(),
            portfolioDao = db.portfolioDao(),
            currentUserIin = "123456789012"
        )
    }
}