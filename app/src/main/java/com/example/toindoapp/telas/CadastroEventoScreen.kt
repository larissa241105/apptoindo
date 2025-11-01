package com.example.toindoapp.telas

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.toindoapp.viewmodel.eventos.CadastroEventoViewModel
import com.example.toindoapp.viewmodel.eventos.SaveState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun CadastroEventoScreen(
    navController: NavController,
    vm: CadastroEventoViewModel = viewModel()
) {
    val uiState by vm.uiState.collectAsState()
    val saveState by vm.saveState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val categorias = listOf("Esporte", "Dança", "Lazer", "Churrasco", "Estudo", "Outro")
    var expanded by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerState = rememberDatePickerState()
    val timePickerState = rememberTimePickerState(is24Hour = true)

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        vm.onImagemSelected(uri?.toString())
    }

    LaunchedEffect(saveState) {
        when (val state = saveState) {
            is SaveState.Success -> {
                navController.popBackStack()
            }
            is SaveState.Error -> {
                snackbarHostState.showSnackbar(message = state.message)
                vm.resetSaveState()
            }
            else -> {}
        }
    }

    Scaffold(
        containerColor = Color(0xFFFFFFF),
        topBar = {
            TopAppBar(
                title = { Text("Cadastrar Novo Evento") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFFFFFF)
                )
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                        .clickable { imagePickerLauncher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    if (uiState.imagemUri != null) {
                        GlideImage(
                            model = uiState.imagemUri,
                            contentDescription = "Imagem do Evento",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Clique para adicionar uma imagem")
                    }
                }
            }

            item { OutlinedTextField(value = uiState.nome, onValueChange = vm::onNomeChange, label = { Text("Nome do Evento") }, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(value = uiState.descricao, onValueChange = vm::onDescricaoChange, label = { Text("Descrição") }, modifier = Modifier.fillMaxWidth()) }

            item {
                Box(modifier = Modifier.clickable { showDatePicker = true }) {
                    OutlinedTextField(
                        value = uiState.data,
                        onValueChange = {},
                        label = { Text("Data") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Filled.CalendarToday, contentDescription = "Selecionar Data") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            item {
                Box(modifier = Modifier.clickable { showTimePicker = true }) {
                    OutlinedTextField(
                        value = uiState.horario,
                        onValueChange = {},
                        label = { Text("Horário") },
                        readOnly = true,
                        enabled = false,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = { Icon(Icons.Filled.Schedule, contentDescription = "Selecionar Horário") },
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            item { OutlinedTextField(value = uiState.local, onValueChange = vm::onLocalChange, label = { Text("Local") }, modifier = Modifier.fillMaxWidth()) }

            item {
                Column {
                    Text("Visibilidade do Evento", style = MaterialTheme.typography.bodyLarge)
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = uiState.publico, onClick = { vm.onPublicoChange(true) })
                        Text("Público", modifier = Modifier.clickable { vm.onPublicoChange(true) })
                        Spacer(modifier = Modifier.width(16.dp))
                        RadioButton(selected = !uiState.publico, onClick = { vm.onPublicoChange(false) })
                        Text("Privado", modifier = Modifier.clickable { vm.onPublicoChange(false) })
                    }
                }
            }

            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = uiState.preco,
                        onValueChange = vm::onPrecoChange,
                        label = { Text("Preço (R$)") },
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isGratuito,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text("Gratuito")
                    Switch(checked = uiState.isGratuito, onCheckedChange = vm::onGratuitoChange)
                }
            }


            item {
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = uiState.categoria,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Categoria") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier.fillMaxWidth().menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        categorias.forEach { categoria ->
                            DropdownMenuItem(
                                text = { Text(categoria) },
                                onClick = {
                                    vm.onCategoriaChange(categoria)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }



                item {
                    Button(
                        onClick = { vm.salvarEvento() },
                        enabled = saveState !is SaveState.Loading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (saveState is SaveState.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("SALVAR EVENTO", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val dataFormatada = Instant.ofEpochMilli(millis)
                                .atZone(ZoneId.of("UTC"))
                                .toLocalDate()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                            vm.onDataChange(dataFormatada)
                        }
                        showDatePicker = false
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        TimePickerDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val horarioFormatado = String.format("%02d:%02d", timePickerState.hour, timePickerState.minute)
                        vm.onHorarioChange(horarioFormatado)
                        showTimePicker = false
                    }
                ) { Text("Confirmar") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancelar") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
    }
}

@Composable
fun TimePickerDialog(
    title: String = "Selecione o Horário",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit? = {},
    content: @Composable () -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(

            shape = MaterialTheme.shapes.extraLarge,
            tonalElevation = 6.dp,
            modifier = Modifier
                .width(IntrinsicSize.Min)
                .height(IntrinsicSize.Min)
                .background(shape = MaterialTheme.shapes.extraLarge, color = MaterialTheme.colorScheme.surface),
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
                    text = title,
                    style = MaterialTheme.typography.labelMedium
                )
                content()
                Row(modifier = Modifier.height(40.dp).fillMaxWidth()) {
                    Spacer(modifier = Modifier.weight(1f))
                    dismissButton()
                    confirmButton()
                }
            }
        }
    }
}