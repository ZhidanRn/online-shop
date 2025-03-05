package com.example.onlineshop.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlineshop.data.repository.AuthRepository
import kotlinx.coroutines.launch

class AuthViewModel(private val authRepository: AuthRepository) : ViewModel() {
    var email by mutableStateOf("")
    var password by mutableStateOf("")
    var confirmPassword by mutableStateOf("")
    var role by mutableStateOf("user")
    var isRegister by mutableStateOf(false)
    var passwordVisible by mutableStateOf(false)
    var confirmPasswordVisible by mutableStateOf(false)
    var snackbarMessage by mutableStateOf("")
    var showSnackbar by mutableStateOf(false)

    fun togglePasswordVisibility() {
        passwordVisible = !passwordVisible
    }

    fun toggleConfirmPasswordVisibility() {
        confirmPasswordVisible = !confirmPasswordVisible
    }

    fun toggleAuthMode() {
        isRegister = !isRegister
    }

    fun authenticate(onLoginSuccess: (String) -> Unit) {
        if (email.isBlank() || password.isBlank() || (isRegister && confirmPassword.isBlank())) {
            showSnackbar("Please fill all fields")
            return
        }
        if (isRegister && password != confirmPassword) {
            showSnackbar("Passwords do not match")
            return
        }

        viewModelScope.launch {
            if (isRegister) {
                authRepository.registerUser(email, password, role) { success, message ->
                    if (success) {
                        snackbarMessage = "Registration Successful"
                        showSnackbar = true

                        viewModelScope.launch {
                            kotlinx.coroutines.delay(1000)
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
                    if (success) {
                        snackbarMessage = "Login Successful"
                        showSnackbar = true
                        userRole?.let { onLoginSuccess(it) }
                    } else {
                        showSnackbar(message ?: "Login Failed")
                    }
                }
            }
        }
    }

    private fun resetFields() {
        email = ""
        password = ""
        confirmPassword = ""
        role = "user"
        isRegister = false
    }

    private fun showSnackbar(message: String) {
        snackbarMessage = message
        showSnackbar = true
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(viewModel: AuthViewModel, onLoginSuccess: (String) -> Unit, param: (Any) -> Unit) {
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

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Card(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (viewModel.isRegister) "Create Account" else "Welcome Back",
                    fontSize = 24.sp,
                    style = MaterialTheme.typography.headlineMedium
                )
                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = viewModel.email,
                    onValueChange = { viewModel.email = it },
                    label = { Text("Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = viewModel.password,
                    onValueChange = { viewModel.password = it },
                    label = { Text("Password") },
                    visualTransformation = if (viewModel.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { viewModel.togglePasswordVisibility() }) {
                            Icon(
                                imageVector = if (viewModel.passwordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                contentDescription = if (viewModel.passwordVisible) "Hide password" else "Show password"
                            )
                        }
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                AnimatedVisibility(visible = viewModel.isRegister) {
                    Column {
                        OutlinedTextField(
                            value = viewModel.confirmPassword,
                            onValueChange = { viewModel.confirmPassword = it },
                            label = { Text("Confirm Password") },
                            visualTransformation = if (viewModel.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { viewModel.toggleConfirmPasswordVisibility() }) {
                                    Icon(
                                        imageVector = if (viewModel.confirmPasswordVisible) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                        contentDescription = if (viewModel.confirmPasswordVisible) "Hide password" else "Show password"
                                    )
                                }
                            },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = viewModel.role,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Select Role") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth()
                            )

                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                DropdownMenuItem(text = { Text("User") }, onClick = {
                                    viewModel.role = "user"
                                    expanded = false
                                })
                                DropdownMenuItem(text = { Text("Admin") }, onClick = {
                                    viewModel.role = "admin"
                                    expanded = false
                                })
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }

                Button(
                    onClick = {
                        viewModel.authenticate(onLoginSuccess)

                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(if (viewModel.isRegister) "Sign Up" else "Sign In")
                }
                Spacer(modifier = Modifier.height(8.dp))

                TextButton(onClick = { viewModel.toggleAuthMode() }) {
                    Text(if (viewModel.isRegister) "Already have an account? Sign In" else "Don't have an account? Sign Up")
                }
            }
        }
        SnackbarHost(hostState = snackbarHostState)
    }
}
