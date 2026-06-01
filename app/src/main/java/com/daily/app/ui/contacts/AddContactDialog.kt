package com.daily.app.ui.contacts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.daily.app.domain.model.Relationship
import com.daily.app.ui.contacts.components.displayName

/**
 * 添加联系人底部对话框.
 *
 * @param formState 表单状态
 * @param onDismiss 关闭回调
 * @param onNameChange 姓名变更
 * @param onRelationshipChange 关系变更
 * @param onPhoneChange 手机号变更
 * @param onSmsCodeChange 验证码变更
 * @param onSendCode 发送验证码
 * @param onConfirm 确认添加
 * @param modifier 修饰符
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddContactDialog(
    formState: AddContactFormState,
    onDismiss: () -> Unit,
    onNameChange: (String) -> Unit,
    onRelationshipChange: (Relationship) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSmsCodeChange: (String) -> Unit,
    onSendCode: () -> Unit,
    onConfirm: () -> Unit,
    modifier: Modifier = Modifier
) {
    var relationshipExpanded by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 标题
            Text(
                text = "添加紧急联系人",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.W600
            )

            // 姓名
            TextField(
                value = formState.name,
                onValueChange = onNameChange,
                label = { Text("姓名") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = ExposedDropdownMenuDefaults.textFieldColors()
            )

            // 关系类型下拉
            ExposedDropdownMenuBox(
                expanded = relationshipExpanded,
                onExpandedChange = { relationshipExpanded = !relationshipExpanded }
            ) {
                TextField(
                    value = formState.relationship.displayName,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("关系") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = relationshipExpanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = relationshipExpanded,
                    onDismissRequest = { relationshipExpanded = false }
                ) {
                    Relationship.entries.forEach { relationship ->
                        DropdownMenuItem(
                            text = { Text(relationship.displayName) },
                            onClick = {
                                onRelationshipChange(relationship)
                                relationshipExpanded = false
                            }
                        )
                    }
                }
            }

            // 手机号
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                TextField(
                    value = formState.phone,
                    onValueChange = onPhoneChange,
                    label = { Text("手机号") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = KeyboardType.Phone
                    ),
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )

                // 发送验证码按钮
                val isCountingDown = formState.sendCodeCountdown > 0
                Button(
                    onClick = onSendCode,
                    enabled = !isCountingDown && formState.phone.isNotBlank() && !formState.smsCodeSent,
                    modifier = Modifier.padding(bottom = 4.dp)
                ) {
                    Text(if (isCountingDown) "${formState.sendCodeCountdown}s" else "发送验证码")
                }
            }

            // 验证码（发送验证码后显示）
            if (formState.smsCodeSent) {
                TextField(
                    value = formState.smsCode,
                    onValueChange = onSmsCodeChange,
                    label = { Text("验证码") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = androidx.compose.ui.text.input.KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true,
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
            }

            // 底部按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("取消")
                }
                Button(
                    onClick = onConfirm,
                    modifier = Modifier.weight(1f),
                    enabled = formState.name.isNotBlank() &&
                        formState.phone.isNotBlank() &&
                        formState.smsCode.length == 6 &&
                        !formState.verifying
                ) {
                    Text(if (formState.verifying) "验证中..." else "确认添加")
                }
            }
        }
    }
}
