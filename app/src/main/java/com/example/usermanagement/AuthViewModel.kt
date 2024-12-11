package com.example.usermanagement

import android.util.Log
import androidx.core.app.NotificationCompat.MessagingStyle.Message
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if(auth.currentUser==null){
            _authState.value=AuthState.Unauthenticated
        }
        else{
            _authState.value=AuthState.Authenticated
        }
    }

    fun login(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value=AuthState.Error("Warning: Email or Password cannot be empty !")
            return
        }
        _authState.value=AuthState.Loading

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    _authState.value=AuthState.Authenticated
                }
                else{
                    _authState.value=AuthState.Error(task.exception?.message?:"something went wrong !!!")
                }
            }
    }

    fun signup(email : String, password : String){

        if(email.isEmpty() || password.isEmpty()){
            _authState.value=AuthState.Error("Warning: Email or Password cannot be empty !")
            return
        }
        if (password.length < 6) {
            _authState.value = AuthState.Error("Password must be at least 6 characters!")
            return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Invalid email format!")
            return
        }

        _authState.value=AuthState.Loading

        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener{task->
                if(task.isSuccessful){
                    _authState.value=AuthState.Authenticated
                }
                else{
                    Log.e("FirebaseAuth", "Error during signup: ${task.exception?.message}")
                    _authState.value=AuthState.Error(task.exception?.message?:"something went wrong !!!")
                }
            }
    }

    fun signout(){
        auth.signOut()
        _authState.value=AuthState.Unauthenticated
    }

}

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    data class Error(val message: String) : AuthState()
}