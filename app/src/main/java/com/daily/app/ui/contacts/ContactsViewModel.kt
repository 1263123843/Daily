package com.daily.app.ui.contacts

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.daily.app.domain.model.EmergencyContact
import com.daily.app.domain.model.Relationship
import com.daily.app.domain.usecase.IAddContactUseCase
import com.daily.app.domain.usecase.IDeleteContactUseCase
import com.daily.app.domain.usecase.IGetContactsUseCase
import com.daily.app.domain.usecase.ISendSmsCodeUseCase
import com.daily.app.domain.usecase.IVerifySmsCodeUseCase
import com.daily.app.domain.usecase.AddContactParams
import com.daily.app.domain.usecase.DeleteContactParams
import com.daily.app.domain.usecase.GetContactsParams
import com.daily.app.domain.usecase.SendSmsCodeParams
import com.daily.app.domain.usecase.VerifySmsCodeParams
import com.daily.app.util.Constants
import com.daily.app.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 添加联系人表单状态.
 */
data class AddContactFormState(
    val name: String = "",
    val relationship: Relationship = Relationship.PARENT,
    val phone: String = "",
    val smsCode: String = "",
    val smsCodeSent: Boolean = false,
    val sendCodeCountdown: Int = 0,
    val verifying: Boolean = false
)

/**
 * 联系人页面 UI 状态.
 */
data class ContactsUiState(
    val contacts: List<EmergencyContact> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
    val showAddDialog: Boolean = false,
    val addFormState: AddContactFormState = AddContactFormState(),
    val showDeleteDialog: EmergencyContact? = null
)

/**
 * 联系人页面 ViewModel.
 *
 * 负责加载联系人列表、添加/删除联系人、发送和验证短信验证码。
 */
@HiltViewModel
class ContactsViewModel @Inject constructor(
    private val getContactsUseCase: IGetContactsUseCase,
    private val addContactUseCase: IAddContactUseCase,
    private val deleteContactUseCase: IDeleteContactUseCase,
    private val sendSmsCodeUseCase: ISendSmsCodeUseCase,
    private val verifySmsCodeUseCase: IVerifySmsCodeUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContactsUiState())
    val uiState: StateFlow<ContactsUiState> = _uiState.asStateFlow()

    init {
        loadContacts()
    }

    /**
     * 加载联系人列表.
     */
    fun loadContacts() {
        viewModelScope.launch {
            _uiState.update { it.copy(loading = true, error = null) }

            when (val result = getContactsUseCase(GetContactsParams(userId = "local_user"))) {
                is Result.Success -> {
                    _uiState.update { it.copy(loading = false, contacts = result.data) }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(loading = false, error = result.message ?: "加载联系人失败")
                    }
                }
                is Result.Loading -> { /* 忽略 */ }
            }
        }
    }

    /**
     * 显示添加联系人对话框.
     */
    fun showAddDialog() {
        _uiState.update {
            it.copy(showAddDialog = true, addFormState = AddContactFormState())
        }
    }

    /**
     * 关闭添加联系人对话框.
     */
    fun dismissAddDialog() {
        _uiState.update { it.copy(showAddDialog = false) }
    }

    /**
     * 更新表单姓名.
     */
    fun updateFormName(name: String) {
        _uiState.update {
            it.copy(addFormState = it.addFormState.copy(name = name))
        }
    }

    /**
     * 更新表单关系类型.
     */
    fun updateFormRelationship(relationship: Relationship) {
        _uiState.update {
            it.copy(addFormState = it.addFormState.copy(relationship = relationship))
        }
    }

    /**
     * 更新表单手机号.
     */
    fun updateFormPhone(phone: String) {
        _uiState.update {
            it.copy(addFormState = it.addFormState.copy(phone = phone))
        }
    }

    /**
     * 更新表单验证码.
     */
    fun updateFormSmsCode(code: String) {
        _uiState.update {
            it.copy(addFormState = it.addFormState.copy(smsCode = code))
        }
    }

    /**
     * 发送短信验证码（带 60 秒倒计时）.
     */
    fun sendSmsCode() {
        val formState = _uiState.value.addFormState
        if (formState.phone.isBlank()) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(addFormState = formState.copy(sendCodeCountdown = Constants.SMS_COOLDOWN_SECONDS))
            }

            var countdown = Constants.SMS_COOLDOWN_SECONDS
            while (countdown > 0) {
                delay(1000)
                countdown--
                _uiState.update {
                    it.copy(addFormState = it.addFormState.copy(sendCodeCountdown = countdown))
                }
            }

            when (val result = sendSmsCodeUseCase(SendSmsCodeParams(phone = formState.phone))) {
                is Result.Success -> {
                    _uiState.update {
                        it.copy(addFormState = it.addFormState.copy(smsCodeSent = true))
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message ?: "发送验证码失败")
                    }
                }
                is Result.Loading -> { /* 忽略 */ }
            }
        }
    }

    /**
     * 添加联系人（验证短信后添加）.
     */
    fun addContact() {
        val formState = _uiState.value.addFormState
        if (formState.name.isBlank() || formState.phone.isBlank() || formState.smsCode.length != Constants.SMS_CODE_LENGTH) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(addFormState = formState.copy(verifying = true))
            }

            val verifyResult = verifySmsCodeUseCase(
                VerifySmsCodeParams(phone = formState.phone, code = formState.smsCode)
            )

            when (verifyResult) {
                is Result.Success -> {
                    if (verifyResult.data) {
                        val addResult = addContactUseCase(
                            AddContactParams(
                                userId = "local_user",
                                name = formState.name,
                                relationship = formState.relationship,
                                phone = formState.phone,
                                smsCode = formState.smsCode
                            )
                        )

                        when (addResult) {
                            is Result.Success -> {
                                _uiState.update { it.copy(showAddDialog = false) }
                                loadContacts()
                            }
                            is Result.Error -> {
                                _uiState.update {
                                    it.copy(
                                        addFormState = it.addFormState.copy(verifying = false),
                                        error = addResult.message ?: "添加联系人失败"
                                    )
                                }
                            }
                            is Result.Loading -> { /* 忽略 */ }
                        }
                    } else {
                        _uiState.update {
                            it.copy(
                                addFormState = it.addFormState.copy(verifying = false),
                                error = "验证码不正确"
                            )
                        }
                    }
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(
                            addFormState = it.addFormState.copy(verifying = false),
                            error = verifyResult.message ?: "验证失败"
                        )
                    }
                }
                is Result.Loading -> { /* 忽略 */ }
            }
        }
    }

    /**
     * 显示删除确认对话框.
     */
    fun showDeleteConfirmation(contact: EmergencyContact) {
        _uiState.update { it.copy(showDeleteDialog = contact) }
    }

    /**
     * 关闭删除确认对话框.
     */
    fun dismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteDialog = null) }
    }

    /**
     * 删除联系人.
     */
    fun deleteContact(contact: EmergencyContact) {
        viewModelScope.launch {
            _uiState.update { it.copy(showDeleteDialog = null) }

            when (val result = deleteContactUseCase(
                DeleteContactParams(contactId = contact.contactId, userId = "local_user")
            )) {
                is Result.Success -> {
                    loadContacts()
                }
                is Result.Error -> {
                    _uiState.update {
                        it.copy(error = result.message ?: "删除联系人失败")
                    }
                }
                is Result.Loading -> { /* 忽略 */ }
            }
        }
    }
}
