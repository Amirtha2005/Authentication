package com.example.usermanagement.Pages

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.usermanagement.Employee
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun CreateEmployeePage(modifier: Modifier = Modifier, navController: NavHostController) {
    var name by remember {
        mutableStateOf("")
    }
    var age by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var phoneError by remember { mutableStateOf(false) }
    var designation by remember { mutableStateOf("") }

    val firestore = FirebaseFirestore.getInstance()

    Column(
        modifier = Modifier
            .padding(18.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Add New Employee", style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name: ") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = age,
            onValueChange = { age = it },
            label = { Text("Age: ") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = phone,
            onValueChange = {
                if (it.length <= 10) {
                    phone = it
                    phoneError = false
                } else {
                    phoneError = true
                }
            },
            label = { Text("Phone: ") },
            modifier = Modifier.fillMaxWidth(),
            isError = phoneError
        )

        if (phoneError) {
            Text("Phone number can't exceed 10 digits", color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = designation,
            onValueChange = { designation = it },
            label = { Text("Designation: ") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            val docRef = firestore.collection("employees").document()
            
            val newEmployee = Employee(
                id=docRef.id,
                name=name,
                age=age,
                phone=phone,
                designation=designation
            )
            
            docRef.set(newEmployee)
                .addOnSuccessListener { 
                    navController.popBackStack()
                }
                .addOnFailureListener{
                    Toast.makeText(
                        navController.context,
                        "Failed to add employee: ${it.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }, modifier=Modifier.fillMaxWidth()
        ) {
            Text(text = "Save")
        }

    }
}