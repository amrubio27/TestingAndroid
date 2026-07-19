package com.amrubio27.cursotestingandroid.checkout.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.amrubio27.cursotestingandroid.core.presentation.components.MarketTopAppBar

@Composable
fun CheckOutScreen(
    onBack: () -> Unit,
    viewModel: CheckOutViewModel = hiltViewModel()
) {
    val uiState: CheckOutUiState by viewModel.uiState.collectAsState()
    val snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(Unit) {
        viewModel.event.collect { event ->
            when (event) {
                is CheckOutEvent.ShowMessage -> snackbarHostState.showSnackbar(event.message)
            }
        }
    }

    CheckOutContent(
        uiState = uiState,
        snackbarHostState = snackbarHostState,
        onBack = onBack,
        onNameChange = { viewModel.updateName(it) },
        onEmailChange = { viewModel.updateEmail(it) },
        onAddressChange = { viewModel.updateAddress(it) },
        onConfirm = { viewModel.confirmOrder() },
        onRetry = { viewModel.retry() }
    )
}

@Composable
fun CheckOutContent(
    uiState: CheckOutUiState,
    snackbarHostState: SnackbarHostState = SnackbarHostState(),
    onBack: () -> Unit,
    onRetry: () -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = { MarketTopAppBar(title = "Checkout") { onBack() } },
    ) { paddingValues ->
        Box(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (uiState) {
                CheckOutUiState.Loading -> {
                    CircularProgressIndicator()
                }

                is CheckOutUiState.Error -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(uiState.message)
                        Button(onClick = { onRetry() }) {
                            Text("Reintentar")
                        }
                    }
                }

                is CheckOutUiState.Success -> {
                    Column(
                        Modifier
                            .fillMaxSize()
                            .padding(all = 16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text("Pedido confirmado: ${uiState.confirmation.orderId}")
                        Text("Tiempo estimado: ${uiState.confirmation.etaMinutes}")
                        Text("Precio: ${uiState.confirmation.total}")
                    }
                }

                is CheckOutUiState.Idle -> {
                    CheckOutContentIdle(
                        uiState = uiState,
                        onNameChange = onNameChange,
                        onEmailChange = onEmailChange,
                        onAddressChange = onAddressChange,
                        onConfirm = onConfirm
                    )
                }
            }
        }
    }
}

@Composable
fun CheckOutContentIdle(
    uiState: CheckOutUiState.Idle,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onAddressChange: (String) -> Unit,
    onConfirm: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(state = rememberScrollState())
            .padding(all = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Total: ${uiState.summary.finalTotal}", style = MaterialTheme.typography.titleLarge)
        OutlinedTextField(
            value = uiState.form.name,
            onValueChange = onNameChange,
            label = { Text("Nombre") },
            isError = uiState.errors.nameError != null,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.form.address,
            onValueChange = onAddressChange,
            label = { Text("Dirección") },
            isError = uiState.errors.addressError != null,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = uiState.form.email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            isError = uiState.errors.emailError != null,
            modifier = Modifier.fillMaxWidth()
        )

        if (uiState.isCartEmpty) {
            Text("Tu carrito está vacío")
        }

        Button(
            onClick = onConfirm,
            enabled = uiState.canSubmit,
            modifier = Modifier.fillMaxWidth()
        ) { Text(if (uiState.isSubmitting) "Procesando el pago..." else "Confirmar pedido") }
    }
}
