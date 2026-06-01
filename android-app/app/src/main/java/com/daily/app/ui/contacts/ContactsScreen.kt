package com.daily.app.ui.contacts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.daily.app.domain.model.Relationship
import com.daily.app.ui.common.EmptyState
import com.daily.app.ui.common.ErrorView
import com.daily.app.ui.common.LoadingIndicator
import com.daily.app.ui.contacts.components.ContactCard

/**
 * 联系人页面 — 紧急联系人列表.
 *
 * @param viewModel 联系人 ViewModel
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(
    viewModel: ContactsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "紧急联系人",
                        fontWeight = FontWeight.W600,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                scrollBehavior = scrollBehavior
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddDialog() },
                enabled = uiState.contacts.size < com.daily.app.util.Constants.MAX_CONTACTS
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "添加联系人"
                )
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                uiState.loading && uiState.contacts.isEmpty() -> {
                    LoadingIndicator()
                }
                uiState.error != null && uiState.contacts.isEmpty() -> {
                    ErrorView(
                        errorMessage = uiState.error!!,
                        onRetry = { viewModel.loadContacts() }
                    )
                }
                uiState.contacts.isEmpty() -> {
                    EmptyState(
                        icon = Icons.AutoMirrored.Default.List,
                        message = "暂无紧急联系人，点击右下角 + 添加"
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(
                            horizontal = 16.dp,
                            vertical = 8.dp
                        ),
                        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.contacts, key = { it.contactId }) { contact ->
                            ContactCard(
                                contact = contact,
                                onDeleteClick = { viewModel.showDeleteConfirmation(contact) }
                            )
                        }
                    }
                }
            }
        }
    }

    // 添加联系人对话框
    if (uiState.showAddDialog) {
        AddContactDialog(
            formState = uiState.addFormState,
            onDismiss = { viewModel.dismissAddDialog() },
            onNameChange = { name -> viewModel.updateFormName(name) },
            onRelationshipChange = { rel -> viewModel.updateFormRelationship(rel) },
            onPhoneChange = { phone -> viewModel.updateFormPhone(phone) },
            onSmsCodeChange = { code -> viewModel.updateFormSmsCode(code) },
            onSendCode = { viewModel.sendSmsCode() },
            onConfirm = { viewModel.addContact() }
        )
    }

    // 删除确认对话框
    uiState.showDeleteDialog?.let { contact ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { viewModel.dismissDeleteDialog() },
            title = { Text("确认删除") },
            text = {
                Text("确定要删除联系人「${contact.name}」吗？")
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteContact(contact) },
                    modifier = Modifier.padding(end = 8.dp)
                ) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.dismissDeleteDialog() }) {
                    Text("取消")
                }
            }
        )
    }
}
