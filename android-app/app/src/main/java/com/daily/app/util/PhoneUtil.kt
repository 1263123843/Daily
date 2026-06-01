package com.daily.app.util

import java.util.regex.Pattern

/**
 * 手机号工具类.
 *
 * 提供手机号脱敏、格式校验、格式化等常用功能。
 * 所有手机号操作均针对中国大陆 11 位手机号。
 */
object PhoneUtil {

    /** 中国大陆手机号正则表达式 - 11 位，以 1 开头，第二位为 3-9 */
    private val PHONE_PATTERN: Pattern = Pattern.compile("^1[3-9]\\d{9}$")

    /**
     * 手机号脱敏显示.
     *
     * 将中间 4 位替换为星号，保留前 3 位和后 4 位。
     * 示例: "13800138000" -> "138****8000"
     *
     * @param phone 11 位手机号
     * @return 脱敏后的手机号字符串，非法号码原样返回
     */
    fun maskPhone(phone: String): String {
        if (!isValidPhone(phone)) {
            return phone
        }
        return phone.substring(0, 3) + "****" + phone.substring(7)
    }

    /**
     * 校验手机号是否合法.
     *
     * 规则:
     * - 必须为 11 位数字
     * - 以 1 开头
     * - 第二位为 3-9
     *
     * @param phone 待校验的手机号
     * @return true 表示合法
     */
    fun isValidPhone(phone: String): Boolean {
        return phone.isNotEmpty() && PHONE_PATTERN.matcher(phone).matches()
    }

    /**
     * 格式化手机号 - 移除所有非数字字符并验证.
     *
     * 输入: "+86 138-0013-8000" -> 输出: "13800138000"
     * 输入: "138 0013 8000" -> 输出: "13800138000"
     *
     * @param phone 原始手机号字符串
     * @return 格式化后的纯数字手机号，如果结果不是合法手机号则返回空字符串
     */
    fun formatPhone(phone: String): String {
        val digits = phone.replace(Regex("[^0-9]"), "")

        return when {
            // 以 86 开头的 13 位号码，去掉前缀
            digits.length == 13 && digits.startsWith("86") -> {
                val withoutPrefix = digits.substring(2)
                if (isValidPhone(withoutPrefix)) withoutPrefix else ""
            }
            // 正好 11 位
            digits.length == 11 -> {
                if (isValidPhone(digits)) digits else ""
            }
            else -> ""
        }
    }
}
