package com.example.usermanagement.Pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun UpdateEmployeePage(
    modifier: Modifier = Modifier,
    navController: NavController,
    employeeId: String?
) {

    if (employeeId == null) {
        // Handle case where employeeId is null
        Text("Invalid Employee ID", modifier = Modifier.padding(16.dp))
        return
    }
    val firestore = FirebaseFirestore.getInstance()

    var name by remember {
        mutableStateOf("")
    }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(employeeId) {
        firestore.collection("employees").document(employeeId).get()
            .addOnSuccessListener { document ->
                name = document.getString("name") ?: ""
                age = document.getString("age") ?: ""
                phone = document.getString("phone") ?: ""
                designation = document.getString("designation") ?: ""
            }
            .addOnFailureListener {
                Toast.makeText(context, "Failed to fetch employee details", Toast.LENGTH_LONG).show()
            }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
       // Text(text = "Update Employee", style=MaterialTheme.typography.labelLarge,modifier = Modifier.padding(bottom = 16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phone,
            onValueChange = { if (it.length <= 10) phone = it },
            label = { Text("Phone") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = designation,
            onValueChange = { designation = it },
            label = { Text("Designation") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (employeeId != null) {
                val updatedEmployee = mapOf(
                    "name" to name,
                    "age" to age,
                    "phone" to phone,
                    "designation" to designation
                )

                firestore.collection("employees").document(employeeId).update(updatedEmployee)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Employee updated successfully!", Toast.LENGTH_SHORT).show()
                        navController.popBackStack()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update employee: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            }
        }) {
            Text("Save")
        }
    }
}