package com.example.nevil_watch.auth

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.ContactsContract
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import android.util.Patterns
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import com.example.nevil_watch.R
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.nevil_watch.viewmodel.UserProfileViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    userProfileViewModel: UserProfileViewModel = viewModel(),
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var contactNumber by remember { mutableStateOf("") }
    
    // Validation state
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var contactNumberError by remember { mutableStateOf<String?>(null) }
    
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsStateWithLifecycle()
    
    // Validation functions
    fun validateName(input: String): Boolean {
        return when {
            input.isBlank() -> {
                nameError = "Name is required"
                false
            }
            input.length < 2 -> {
                nameError = "Name must be at least 2 characters"
                false
            }
            !input.matches(Regex("^[a-zA-Z\\s]*$")) -> {
                nameError = "Name can only contain letters and spaces"
                false
            }
            else -> {
                nameError = null
                true
            }
        }
    }

    fun validateEmail(input: String): Boolean {
        return when {
            input.isBlank() -> {
                emailError = "Email is required"
                false
            }
            !Patterns.EMAIL_ADDRESS.matcher(input).matches() -> {
                emailError = "Invalid email format"
                false
            }
            else -> {
                emailError = null
                true
            }
        }
    }

    fun validatePassword(input: String): Boolean {
        return when {
            input.isBlank() -> {
                passwordError = "Password is required"
                false
            }
            input.length < 8 -> {
                passwordError = "Password must be at least 8 characters"
                false
            }
            !input.matches(Regex(".*[A-Z].*")) -> {
                passwordError = "Password must contain at least one uppercase letter"
                false
            }
            !input.matches(Regex(".*[a-z].*")) -> {
                passwordError = "Password must contain at least one lowercase letter"
                false
            }
            !input.matches(Regex(".*\\d.*")) -> {
                passwordError = "Password must contain at least one number"
                false
            }
            !input.matches(Regex(".*[!@#\$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) -> {
                passwordError = "Password must contain at least one special character"
                false
            }
            else -> {
                passwordError = null
                true
            }
        }
    }

    fun validateConfirmPassword(input: String): Boolean {
        return when {
            input != password -> {
                confirmPasswordError = "Passwords do not match"
                false
            }
            else -> {
                confirmPasswordError = null
                true
            }
        }
    }

    fun validateContactNumber(input: String): Boolean {
        return when {
            input.isBlank() -> {
                contactNumberError = "Contact number is required"
                false
            }
            !input.matches(Regex("^\\+?[0-9]{10,15}$")) -> {
                contactNumberError = "Invalid contact number format"
                false
            }
            else -> {
                contactNumberError = null
                true
            }
        }
    }
    
    // Contact picker launcher
    val contactPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                val cursor = context.contentResolver.query(
                    uri,
                    arrayOf(
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
                    ),
                    null,
                    null,
                    null
                )
                cursor?.use {
                    if (it.moveToFirst()) {
                        val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                        val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                        
                        // Get and format the phone number
                        val phoneNumber = it.getString(numberIndex)?.replace("[^0-9+]".toRegex(), "") ?: ""
                        contactNumber = phoneNumber
                        validateContactNumber(phoneNumber)
                        
                        // If name field is empty, get it from contacts
                        if (name.isEmpty()) {
                            val contactName = it.getString(nameIndex)
                            if (!contactName.isNullOrEmpty()) {
                                name = contactName
                                validateName(contactName)
                            }
                        }
                    }
                }
            }
        }
    }
    
    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            contactPickerLauncher.launch(intent)
        }
    }
    
    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            // Save user profile data locally when registration is successful
            userProfileViewModel.saveUserProfile(name, contactNumber)
            onRegisterSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        AuthHeader()

        OutlinedTextField(
            value = name,
            onValueChange = { 
                name = it
                validateName(it)
            },
            label = { Text("Full Name") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            isError = nameError != null,
            supportingText = { nameError?.let { Text(it) } }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                validateEmail(it)
            },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            isError = emailError != null,
            supportingText = { emailError?.let { Text(it) } }
        )

        OutlinedTextField(
            value = password,
            onValueChange = { 
                password = it
                validatePassword(it)
                if (confirmPassword.isNotEmpty()) {
                    validateConfirmPassword(confirmPassword)
                }
            },
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            isError = passwordError != null,
            supportingText = { passwordError?.let { Text(it) } }
        )

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { 
                confirmPassword = it
                validateConfirmPassword(it)
            },
            label = { Text("Confirm Password") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            isError = confirmPasswordError != null,
            supportingText = { confirmPasswordError?.let { Text(it) } }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = contactNumber,
                onValueChange = { input -> 
                    // Only allow digits, plus sign, and spaces
                    val filteredInput = input.filter { it.isDigit() || it == '+' || it == ' ' }
                    contactNumber = filteredInput
                    validateContactNumber(filteredInput)
                },
                label = { Text("Contact Number") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                isError = contactNumberError != null,
                supportingText = { contactNumberError?.let { Text(it) } },
                placeholder = { Text("+1 234 567 8900") }
            )
            
            IconButton(
                onClick = {
                    when (PackageManager.PERMISSION_GRANTED) {
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.READ_CONTACTS
                        ) -> {
                            val intent = Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
                            contactPickerLauncher.launch(intent)
                        }
                        else -> {
                            permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .padding(4.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.phone),
                    contentDescription = "Pick contact from phone book",
                    modifier = Modifier.size(24.dp)
                )
            }
        }

        Button(
            onClick = {
                if (validateName(name) && 
                    validateEmail(email) && 
                    validatePassword(password) && 
                    validateConfirmPassword(confirmPassword) &&
                    validateContactNumber(contactNumber)) {
                    viewModel.register(name, email, password, contactNumber)
                }
            },
            enabled = name.isNotEmpty() && 
                     email.isNotEmpty() && 
                     password.isNotEmpty() && 
                     confirmPassword.isNotEmpty() &&
                     contactNumber.isNotEmpty() &&
                     nameError == null &&
                     emailError == null &&
                     passwordError == null &&
                     confirmPasswordError == null &&
                     contactNumberError == null,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
        ) {
            if (authState is AuthState.Loading) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text("Register")
            }
        }

        TextButton(
            onClick = onNavigateToLogin,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text("Already have an account? Sign In")
        }

        if (authState is AuthState.Error) {
            Text(
                text = (authState as AuthState.Error).message,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 16.dp)
            )
        }
    }
} 