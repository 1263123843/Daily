package com.daily.app.ui.contacts.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.daily.app.domain.model.EmergencyContact
import com.daily.app.domain.model.Relationship
import com.daily.app.util.PhoneUtil
import com.daily.app.ui.theme.Primary

/**
 * 联系人卡片 — 显示联系人信息.
 *
 * @param contact 联系人数据
 * @param onDeleteClick 删除按钮点击回调
 * @param modifier 修饰符
 */
@Composable
fun ContactCard(
    contact: EmergencyContact,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 头像图标
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Primary,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
            )

            // 联系人信息
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // 姓名
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )

                // 关系标签 + 手机号
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // 关系标签
                    Text(
                        text = contact.relationship.displayName,
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                contact.relationship.badgeColor.copy(alpha = 0.12f)
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = contact.relationship.badgeColor
                    )

                    // 脱敏手机号
                    Text(
                        text = PhoneUtil.maskPhone(contact.phone),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 删除按钮
            IconButton(
                onClick = onDeleteClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "删除联系人",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

/** 关系类型显示名称 */
val Relationship.displayName: String
    get() = when (this) {
        Relationship.PARENT -> "父母"
        Relationship.PARTNER -> "伴侣"
        Relationship.FRIEND -> "朋友"
        Relationship.ROOMMATE -> "室友"
        Relationship.OTHER -> "其他"
    }

/** 关系类型标签颜色 */
val Relationship.badgeColor: androidx.compose.ui.graphics.Color
    get() = when (this) {
        Relationship.PARENT -> Primary
        Relationship.PARTNER -> androidx.compose.ui.graphics.Color(0xFFEC407A)
        Relationship.FRIEND -> androidx.compose.ui.graphics.Color(0xFF5C6BC0)
        Relationship.ROOMMATE -> androidx.compose.ui.graphics.Color(0xFF26A69A)
        Relationship.OTHER -> androidx.compose.ui.graphics.Color(0xFF78909C)
    }
