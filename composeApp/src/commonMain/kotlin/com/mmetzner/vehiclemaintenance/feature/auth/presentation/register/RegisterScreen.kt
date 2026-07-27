package com.mmetzner.vehiclemaintenance.feature.auth.presentation.register

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import com.mmetzner.vehiclemaintenance.core.ui.preview.PreviewAuthRepository
import com.mmetzner.vehiclemaintenance.core.ui.theme.VehicleMaintenanceTheme
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.components.AuthErrorMessage
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.components.AuthFieldGroup
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.components.AuthPasswordVisibilityButton
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.components.AuthPrimaryButton
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.components.AuthScreenLayout
import com.mmetzner.vehiclemaintenance.feature.auth.presentation.components.AuthTextField
import org.jetbrains.compose.resources.stringResource
import vehiclemaintenance.composeapp.generated.resources.Res
import vehiclemaintenance.composeapp.generated.resources.action_continue
import vehiclemaintenance.composeapp.generated.resources.action_login_sentence
import vehiclemaintenance.composeapp.generated.resources.register_email_or_phone
import vehiclemaintenance.composeapp.generated.resources.register_email_placeholder
import vehiclemaintenance.composeapp.generated.resources.register_existing_account
import vehiclemaintenance.composeapp.generated.resources.register_full_name
import vehiclemaintenance.composeapp.generated.resources.register_full_name_placeholder
import vehiclemaintenance.composeapp.generated.resources.register_password
import vehiclemaintenance.composeapp.generated.resources.register_password_placeholder
import vehiclemaintenance.composeapp.generated.resources.register_subtitle
import vehiclemaintenance.composeapp.generated.resources.register_title

@Composable
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onAuthenticated: () -> Unit,
    onBackToLogin: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onAuthenticated()
        }
    }

    AuthScreenLayout(
        title = stringResource(Res.string.register_title),
        subtitle = stringResource(Res.string.register_subtitle),
        footerPrompt = stringResource(Res.string.register_existing_account),
        footerAction = stringResource(Res.string.action_login_sentence),
        onFooterAction = onBackToLogin,
        modifier = modifier
    ) {
        AuthFieldGroup(label = stringResource(Res.string.register_full_name)) {
            AuthTextField(
                value = state.fullName,
                onValueChange = viewModel::onFullNameChanged,
                placeholder = stringResource(Res.string.register_full_name_placeholder),
                leadingIcon = {
                    Icon(Icons.Default.PersonOutline, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
            )
        }

        AuthFieldGroup(label = stringResource(Res.string.register_email_or_phone)) {
            AuthTextField(
                value = state.emailOrPhone,
                onValueChange = viewModel::onEmailOrPhoneChanged,
                placeholder = stringResource(Res.string.register_email_placeholder),
                leadingIcon = {
                    Icon(Icons.Default.MailOutline, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                )
            )
        }

        AuthFieldGroup(label = stringResource(Res.string.register_password)) {
            AuthTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChanged,
                placeholder = stringResource(Res.string.register_password_placeholder),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    AuthPasswordVisibilityButton(
                        isVisible = state.isPasswordVisible,
                        onToggle = viewModel::togglePasswordVisibility
                    )
                },
                visualTransformation = if (state.isPasswordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        keyboardController?.hide()
                        viewModel.createAccount()
                    }
                )
            )
        }

        AuthErrorMessage(state.errorMessage)

        AuthPrimaryButton(
            text = stringResource(Res.string.action_continue),
            isLoading = state.isLoading,
            onClick = {
                keyboardController?.hide()
                viewModel.createAccount()
            }
        )
    }
}

@Preview
@Composable
private fun RegisterScreenPreview() {
    val viewModel = remember {
        RegisterViewModel(PreviewAuthRepository).apply {
            onFullNameChanged("Alex Driver")
            onEmailOrPhoneChanged("driver@example.com")
            onPasswordChanged("password")
        }
    }

    VehicleMaintenanceTheme {
        RegisterScreen(
            viewModel = viewModel,
            onAuthenticated = {},
            onBackToLogin = {}
        )
    }
}
