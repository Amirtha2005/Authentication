package com.example.usermanagement.Pages

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.usermanagement.AuthState
import com.example.usermanagement.AuthViewModel
import com.example.usermanagement.Employee
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot

@Composable
fun HomePage(modifier: Modifier = Modifier, navController: NavController, authViewModel: AuthViewModel){

    val authState=authViewModel.authState.observeAsState()
    val context = LocalContext.current

    val firestore = FirebaseFirestore.getInstance()
    val employees = remember {
        mutableStateListOf<Employee>()
    }

    LaunchedEffect(true) {
        firestore.collection("employees").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                employees.clear()
                for (document in snapshot.documents) {
                    val employee = document.toObject(Employee::class.java)
                    if (employee != null) {
                        employees.add(employee)
                    }
                }
            }
        }
    }

    LaunchedEffect(authState.value) {
        when(authState.value){
            is AuthState.Unauthenticated -> navController.navigate("login")
            else -> Unit
        }
    }

    Column(
        modifier=Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        TextButton(onClick = {
            authViewModel.signout()
        }) {
            Text(text = "Sign Out")
        }

        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            horizontalArrangement = Arrangement.SpaceAround,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Employees", style = MaterialTheme.typography.headlineLarge)
            Button(onClick = {
                navController.navigate("create_employee")
            }) {
                Text(text = "Add User")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(employees){ employee ->
                EmployeeRow(employee=employee, navController=navController, employees = employees)
            }
        }

    }
}

@Composable
fun EmployeeRow(employee: Employee, navController: NavController ,employees: MutableList<Employee>) {

    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            confirmButton = {
                Button(onClick = {
                    deleteEmployee(employee.id, employees) {
                        Toast.makeText(context, "Employee deleted successfully", Toast.LENGTH_SHORT).show()
                    }
                    showDeleteDialog = false
                }) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            },
            title = { Text("Delete Employee") },
            text = { Text("Are you sure you want to delete ${employee.name}?") }
        )
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .border(BorderStroke(1.dp, Color.Black))
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = "ID: ${employee.id}")
            Text(text = "Name: ${employee.name}")
            Text(text = "Designation: ${employee.designation}")
        }
        Spacer(modifier = Modifier.width(8.dp))
        Row {
            IconButton(onClick = { navController.navigate("update_employee/${employee.id}") }) {
                Icon(Icons.Filled.Edit, contentDescription = "Update")
            }
            IconButton( onClick = { showDeleteDialog = true }) {
                Icon(Icons.Filled.Delete, contentDescription = "Delete")
            }
        }
    }
}

fun deleteEmployee(employeeId: String, employees: MutableList<Employee>, onSuccess: () -> Unit) {
    val firestore = FirebaseFirestore.getInstance()
    firestore.collection("employees").document(employeeId).delete()
        .addOnSuccessListener {
            employees.removeIf { it.id == employeeId }
            onSuccess()
        }
}