package com.biomech.feature.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.biomech.core.resource.AppResources
import com.biomech.core.validation.Validator
import com.biomech.core.validation.rules.EmailRule
import com.biomech.core.validation.rules.MatchRule
import com.biomech.core.validation.rules.PasswordRule

@Composable
fun LoginScreen(
    isLoading: Boolean,
    error: String?,
    onLogin: (email: String, password: String) -> Unit,
    onRegister: (email: String, password: String) -> Unit,
    onSkip: (() -> Unit)? = null,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isRegisterMode by remember { mutableStateOf(false) }

    val emailValidator = remember { Validator<String>().apply { addRule(EmailRule()) } }
    val passwordValidator = remember { Validator<String>().apply { addRule(PasswordRule()) } }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var valid = true

        val emailResult = emailValidator.validate(email)
        if (emailResult is com.biomech.core.validation.ValidationResult.Invalid) {
            emailError = emailResult.errors.first()
            valid = false
        } else {
            emailError = null
        }

        val passwordResult = passwordValidator.validate(password)
        if (passwordResult is com.biomech.core.validation.ValidationResult.Invalid) {
            passwordError = passwordResult.errors.first()
            valid = false
        } else {
            passwordError = null
        }

        if (isRegisterMode) {
            val matchResult = MatchRule({ confirmPassword }, AppResources.strings.passwordsDoNotMatch).validate(password)
            if (matchResult is com.biomech.core.validation.ValidationResult.Invalid) {
                confirmError = matchResult.errors.first()
                valid = false
            } else {
                confirmError = null
            }
        }

        return valid
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = AppResources.strings.appName,
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(48.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it; emailError = null },
            label = { Text(AppResources.strings.email) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            singleLine = true,
            enabled = !isLoading,
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it; passwordError = null },
            label = { Text(AppResources.strings.password) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            singleLine = true,
            enabled = !isLoading,
            isError = passwordError != null,
            supportingText = passwordError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )

        if (isRegisterMode) {
            Spacer(Modifier.height(16.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmError = null },
                label = { Text(AppResources.strings.confirmPassword) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                enabled = !isLoading,
                isError = confirmError != null,
                supportingText = confirmError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (error != null) {
            Spacer(Modifier.height(16.dp))
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = {
                if (validate()) {
                    if (isRegisterMode) onRegister(email, password) else onLogin(email, password)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.width(8.dp))
            }
            Text(if (isRegisterMode) AppResources.strings.register else AppResources.strings.login)
        }

        Spacer(Modifier.height(8.dp))

        TextButton(
            onClick = {
                isRegisterMode = !isRegisterMode
                confirmPassword = ""
                confirmError = null
            },
            enabled = !isLoading,
        ) {
            Text(if (isRegisterMode) AppResources.strings.alreadyHaveAccount else AppResources.strings.dontHaveAccount)
        }

        if (onSkip != null) {
            Spacer(Modifier.height(24.dp))
            TextButton(
                onClick = onSkip,
            ) {
                Text(
                    AppResources.strings.skipExplore,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
