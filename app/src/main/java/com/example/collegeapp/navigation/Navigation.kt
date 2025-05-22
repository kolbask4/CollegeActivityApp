package com.example.collegeapp.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import com.example.collegeapp.AuthScreen
import com.example.collegeapp.data.UserDao
import com.example.collegeapp.data.GradeDao
import com.example.collegeapp.data.GoalDao
import com.example.collegeapp.data.PortfolioDao
import com.example.collegeapp.screens.*

@Composable
fun Navigation(
    navController: NavHostController,
    userDao: UserDao,
    gradeDao: GradeDao,
    goalDao: GoalDao,
    portfolioDao: PortfolioDao,
    currentUserIin: String?,
    onLogout: () -> Unit
) {
    NavHost(
        navController = navController,
        startDestination = Screens.Home.route
    ) {
        composable(Screens.Home.route) {
            HomeScreen(userDao = userDao, gradeDao = gradeDao, currentUserIin = currentUserIin)
        }
        composable(Screens.Plan.route) {
            PlanScreen(userDao = userDao, goalDao = goalDao, currentUserIin = currentUserIin)
        }
        composable(Screens.Portfolio.route) {
            PortfolioScreen(userDao = userDao, portfolioDao = portfolioDao, currentUserIin = currentUserIin)
        }
        composable(Screens.Profile.route) {
            ProfileScreen(
                userDao = userDao,
                currentUserIin = currentUserIin,
                navController = navController,
                onLogout = onLogout
            )
        }
    }
}

sealed class Screens(val route: String) {
    object Home : Screens("home")
    object Plan : Screens("plan")
    object Portfolio : Screens("portfolio")
    object Profile : Screens("profile")
} 