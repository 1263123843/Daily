package com.daily.app.util

/**
 * 全局常量定义.
 *
 * 集中管理应用中使用的所有魔法数字和字符串常量，
 * 便于统一调整和避免硬编码。
 */
object Constants {

    // ==================== 短信验证码 ====================

    /** 短信验证码有效期（秒）*/
    const val SMS_VALIDITY_SECONDS = 300

    /** 短信验证码冷却时间（秒）- 发送后需等待的时间 */
    const val SMS_COOLDOWN_SECONDS = 60

    /** 短信验证码长度 */
    const val SMS_CODE_LENGTH = 6

    // ==================== 签到 ====================

    /** 签到超时阈值（毫秒）- 24 小时 */
    val CHECKIN_THRESHOLD_MS = 24L * 60 * 60 * 1000

    /** 连续签到断签判定阈值（毫秒）- 允许跨天 1 小时内签到仍算连续 */
    val CHECKIN_GRACE_PERIOD_MS = 1L * 60 * 60 * 1000

    // ==================== 联系人 ====================

    /** 最大紧急联系人数量 */
    const val MAX_CONTACTS = 3

    // ==================== 网络与同步 ====================

    /** 单条记录最大重试次数 */
    const val RETRY_LIMIT = 3

    /** 同步操作超时时间（毫秒）*/
    const val SYNC_TIMEOUT_MS = 30_000L

    /** 签到历史默认分页大小 */
    const val DEFAULT_PAGE_SIZE = 20

    // ==================== 数据库 ====================

    /** 本地签到记录保留上限 */
    const val MAX_LOCAL_CHECKIN_RECORDS = 500

    /** 数据库名称 */
    const val DATABASE_NAME = "daily_database"

    // ==================== DataStore ====================

    /** DataStore 偏好文件名称 */
    const val DATASTORE_NAME = "user_preferences"
}
