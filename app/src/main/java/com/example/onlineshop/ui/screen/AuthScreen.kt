package com.example.onlineshop.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.lib.DataStoreManager
import com.example.onlineshop.data.repository.AuthRepository
import com.example.onlineshop.ui.theme.Purple40
import com.example.onlineshop.ui.theme.PurpleGrey40
import com.example.onlineshop.ui.theme.PurpleGrey80
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository,
    private val dataStoreManager: DataStoreManager
) : ViewModel() {
    var name by mutableStateOf("")
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var phone by mutableStateOf("")
    var address by mutableStateOf("")
    var role by mutableStateOf("user")
    var profileImage by mutableStateOf("")
    var isRegister by mutableStateOf(false)
    var passwordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)
    var snackbarMessage by mutableStateOf("")
    var showSnackbar by mutableStateOf(false)
    var isLoading by mutableStateOf(false)

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    fun toggleAuthMode() {
        isRegister = !isRegister
    }

    fun authenticate(onLoginSuccess: (String, Any?) -> Unit) {
        if (email.isBlank() || password.isBlank() || (isRegister && confirmPassword.isBlank())) {
            showSnackbar("Please fill all fields")
            return
        }
        if (isRegister && password != confirmPassword) {
            showSnackbar("Passwords do not match")
            return
        }

        viewModelScope.launch {
            isLoading = true

            if (isRegister) {
                authRepository.registerUser(email, password, name, phone, address, role, profileImage) { success, message ->
                    isLoading = false

                    if (success) {
                        snackbarMessage = "Registration Successful"
                        showSnackbar = true

                        viewModelScope.launch {
                            delay(1000)
                            resetFields()
                            isRegister = false
                            showSnackbar = false
                        }
                    } else {
                        showSnackbar(message ?: "Registration Failed")
                    }
                }
            } else {
                authRepository.loginUser(email, password) { success, userRole, message ->
                    isLoading = false

                    if (success) {
                        snackbarMessage = "Login Successful"
                        showSnackbar = true

                        viewModelScope.launch {
                            delay(1000)
                            saveSession(email, (userRole ?: "user").toString())
                            onLoginSuccess((userRole ?: "user").toString(), null)
                        }
                    } else {
                        showSnackbar(message ?: "Login Failed")
                    }
                }
            }
        }
    }

    private fun resetFields() {
        name = ""
        email = ""
        password = ""
        confirmPassword = ""
        phone = ""
        address = ""
        role = "user"
        isRegister = false
    }

    private fun showSnackbar(message: String) {
        snackbarMessage = message
        showSnackbar = true
    }

    private fun saveSession(email: String, role: String) {
        viewModelScope.launch {
            dataStoreManager.saveUserSession(email, role)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: AuthViewModel, onLoginSuccess: (String, Any?) -> Unit) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(viewModel.showSnackbar) {
        if (viewModel.showSnackbar) {
            scope.launch {
                snackbarHostState.showSnackbar(viewModel.snackbarMessage)
            }
            viewModel.showSnackbar = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (viewModel.isRegister) "Create Account" else "Welcome Back",
                fontSize = 24.sp,
                color = Purple40
            )
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = viewModel.email,
                onValueChange = { viewModel.email = it },
                label = { Text("Email", color = PurpleGrey80) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = viewModel.password,
                onValueChange = { viewModel.password = it },
                label = { Text("Password", color = PurpleGrey80) },
                visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                        Icon(
                            imageVector = if (viewModel.passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                            contentDescription = "Toggle Password Visibility",
                            tint = Purple40
                        )
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            )
            Spacer(modifier = Modifier.height(8.dp))

            AnimatedVisibility(visible = viewModel.isRegister) {
                Column {
                    OutlinedTextField(
                        value = viewModel.confirmPassword,
                        onValueChange = { viewModel.confirmPassword = it },
                        label = { Text("Confirm Password", color = PurpleGrey80) },
                        visualTransformation = if (viewModel.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                                Icon(
                                    imageVector = if (viewModel.confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = "Toggle Confirm Password Visibility",
                                    tint = Purple40
                                )
                            }
                        },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.name,
                        onValueChange = { viewModel.name = it },
                        label = { Text("Name", color = PurpleGrey80) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.phone,
                        onValueChange = { viewModel.phone = it },
                        label = { Text("Phone", color = PurpleGrey80) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = viewModel.address,
                        onValueChange = { viewModel.address = it },
                        label = { Text("Address", color = PurpleGrey80) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            Button(
                onClick = { viewModel.authenticate(onLoginSuccess) },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Text(
                        text = if (viewModel.isRegister) "Sign Up" else "Sign In",
                        color = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = { viewModel.toggleAuthMode() }) {
                Text(
                    text = if (viewModel.isRegister) "Already have an account? Sign In" else "Don't have an account? Sign Up",
                    color = PurpleGrey40
                )
            }
        }

        SnackbarHost(hostState = snackbarHostState)
    }
}