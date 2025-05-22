package com.example.collegeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.collegeapp.data.AppDatabase
import com.example.collegeapp.data.GoalDao
import com.example.collegeapp.data.GradeDao
import com.example.collegeapp.data.PortfolioDao
import com.example.collegeapp.data.SessionManager
import com.example.collegeapp.data.UserDao
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
    private lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val db = AppDatabase.getDatabase(this)
        userDao = db.userDao()
        gradeDao = db.gradeDao()
        goalDao = db.goalDao()
        portfolioDao = db.portfolioDao()
        sessionManager = SessionManager(this)
        enableEdgeToEdge()
        setContent {
            CollegeAppTheme {
                AuthScreen(
                    userDao = userDao,
                    gradeDao = gradeDao,
                    goalDao = goalDao,
                    portfolioDao = portfolioDao,
                    sessionManager = sessionManager
                )
            }
        }
    }
}

@Composable
fun AuthScreen(
    userDao: UserDao,
    gradeDao: GradeDao,
    goalDao: GoalDao,
    portfolioDao: PortfolioDao,
    sessionManager: SessionManager
) {
    var isAuthenticated by remember { mutableStateOf(sessionManager.isLoggedIn()) }
    var showRegister by remember { mutableStateOf(false) }
    var currentUserIin by remember { mutableStateOf<String?>(sessionManager.getUserIin()) }

    if (!isAuthenticated) {
        if (showRegister) {
            RegisterScreen(
                onRegisterSuccess = { iin ->
                    isAuthenticated = true
                    showRegister = false
                    currentUserIin = iin
                    sessionManager.saveUserSession(iin)
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
                    sessionManager.saveUserSession(iin)
                },
                onNavigateToRegister = {
                    showRegister = true
                },
                userDao = userDao
            )
        }
    } else {
        MainScreen(
            userDao = userDao,
            gradeDao = gradeDao,
            goalDao = goalDao,
            portfolioDao = portfolioDao,
            currentUserIin = currentUserIin,
            onLogout = {
                isAuthenticated = false
                currentUserIin = null
                sessionManager.clearSession()
            }
        )
    }
}

@Composable
fun MainScreen(
    userDao: UserDao,
    gradeDao: GradeDao,
    goalDao: GoalDao,
    portfolioDao: PortfolioDao,
    currentUserIin: String?,
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val currentBackStack by navController.currentBackStackEntryAsState()
    val currentDestination = currentBackStack?.destination
    val currentRoute = currentDestination?.route

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (currentRoute == Screens.Home.route) {
                                    R.drawable.home
                                } else {
                                    R.drawable.nfhome
                                }
                            ), contentDescription = "Главная"
                        )
                    },
                    label = { Text("Главная") },
                    selected = currentRoute == Screens.Home.route,
                    onClick = {
                        if (currentRoute != Screens.Home.route) {
                            navController.navigate(Screens.Home.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (currentRoute == Screens.Plan.route) {
                                    R.drawable.plan
                                } else {
                                    R.drawable.nfplan
                                }
                            ), contentDescription = "План"
                        )
                    },
                    label = { Text("План") },
                    selected = currentDestination?.route == Screens.Plan.route,
                    onClick = {
                        if (currentDestination?.route != Screens.Plan.route) {
                            navController.navigate(Screens.Plan.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (currentRoute == Screens.Portfolio.route) {
                                    R.drawable.portf
                                } else {
                                    R.drawable.nfportf
                                }
                            ), contentDescription = "Портфолио"
                        )
                    },
                    label = { Text("Портфолио") },
                    selected = currentDestination?.route == Screens.Portfolio.route,
                    onClick = {
                        if (currentDestination?.route != Screens.Portfolio.route) {
                            navController.navigate(Screens.Portfolio.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
                        }
                    }
                )
                NavigationBarItem(
                    icon = {
                        Icon(
                            painterResource(
                                if (currentRoute == Screens.Profile.route) {
                                    R.drawable.profile
                                } else {
                                    R.drawable.nfprofile
                                }
                            ), contentDescription = "Профиль"
                        )
                    },
                    label = { Text("Профиль") },
                    selected = currentDestination?.route == Screens.Profile.route,
                    onClick = {
                        if (currentDestination?.route != Screens.Profile.route) {
                            navController.navigate(Screens.Profile.route) {
                                popUpTo(navController.graph.startDestinationId)
                                launchSingleTop = true
                            }
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
            currentUserIin = currentUserIin,
            onLogout = onLogout
        )
    }
}

@Composable
fun MainScreenPreview() {
    CollegeAppTheme {
        val db = AppDatabase.getDatabase(androidx.compose.ui.platform.LocalContext.current)
        MainScreen(
            userDao = db.userDao(),
            gradeDao = db.gradeDao(),
            goalDao = db.goalDao(),
            portfolioDao = db.portfolioDao(),
            currentUserIin = "123456789012",
            onLogout = {}
        )
    }
}