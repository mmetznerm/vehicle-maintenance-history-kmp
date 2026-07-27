package com.mmetzner.vehiclemaintenance.feature.auth.presentation.login

import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
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
import vehiclemaintenance.composeapp.generated.resources.action_create_account
import vehiclemaintenance.composeapp.generated.resources.action_login
import vehiclemaintenance.composeapp.generated.resources.login_email_or_phone
import vehiclemaintenance.composeapp.generated.resources.login_email_or_phone_placeholder
import vehiclemaintenance.composeapp.generated.resources.login_no_account
import vehiclemaintenance.composeapp.generated.resources.login_password
import vehiclemaintenance.composeapp.generated.resources.login_password_placeholder
import vehiclemaintenance.composeapp.generated.resources.login_tagline
import vehiclemaintenance.composeapp.generated.resources.login_title

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onAuthenticated: () -> Unit,
    onCreateAccount: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) {
            onAuthenticated()
        }
    }

    AuthScreenLayout(
        title = stringResource(Res.string.login_title),
        subtitle = stringResource(Res.string.login_tagline),
        footerPrompt = stringResource(Res.string.login_no_account),
        footerAction = stringResource(Res.string.action_create_account),
        onFooterAction = onCreateAccount,
        modifier = modifier
    ) {
        AuthFieldGroup(label = stringResource(Res.string.login_email_or_phone)) {
            AuthTextField(
                value = state.email,
                onValueChange = viewModel::onEmailChanged,
                placeholder = stringResource(Res.string.login_email_or_phone_placeholder),
                leadingIcon = {
                    Icon(Icons.Default.MailOutline, contentDescription = null)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Next
                )
            )
        }

        AuthFieldGroup(label = stringResource(Res.string.login_password)) {
            AuthTextField(
                value = state.password,
                onValueChange = viewModel::onPasswordChanged,
                placeholder = stringResource(Res.string.login_password_placeholder),
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null)
                },
                trailingIcon = {
                    AuthPasswordVisibilityButton(
                        isVisible = passwordVisible,
                        onToggle = { passwordVisible = !passwordVisible }
                    )
                },
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { viewModel.login() }
                )
            )
        }

        AuthErrorMessage(state.errorMessage)

        AuthPrimaryButton(
            text = stringResource(Res.string.action_login),
            isLoading = state.isLoading,
            onClick = viewModel::login
        )
    }
}

@Preview
@Composable
private fun LoginScreenPreview() {
    val viewModel = remember {
        LoginViewModel(PreviewAuthRepository).apply {
            onEmailChanged("driver@example.com")
            onPasswordChanged("password")
        }
    }

    VehicleMaintenanceTheme {
        LoginScreen(
            viewModel = viewModel,
            onAuthenticated = {},
            onCreateAccount = {}
        )
    }
}
