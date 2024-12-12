package com.example.usermanagement

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.usermanagement.Pages.CreateEmployeePage
import com.example.usermanagement.Pages.EmployeeHomePage
import com.example.usermanagement.Pages.HomePage
import com.example.usermanagement.Pages.Login
import com.example.usermanagement.Pages.SignUp
import com.example.usermanagement.Pages.UpdateEmployeePage

@Composable
fun Navigation(modifier: Modifier = Modifier, authViewModel: AuthViewModel){
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "login", builder = {
        composable("login"){
            Login(modifier, navController, authViewModel)
        }
        composable("signup"){
            SignUp(modifier, navController, authViewModel)
        }
        composable("home"){
            HomePage(modifier, navController, authViewModel)
        }

        composable("employee_home"){
            EmployeeHomePage(modifier, navController)
        }
        composable("update_employee/{employeeId}"){backStackEntry ->
            val employeeId = backStackEntry.arguments?.getString("employeeId")
            UpdateEmployeePage(modifier,navController,employeeId)
        }
        composable("create_employee"){
            CreateEmployeePage(modifier, navController)
        }



    } )
}