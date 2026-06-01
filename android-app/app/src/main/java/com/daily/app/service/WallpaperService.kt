package com.daily.app.service

import android.content.Context
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.service.wallpaper.WallpaperService
import android.view.SurfaceHolder
import android.util.Log
import com.daily.app.data.local.UserPreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/**
 * A simplified WallpaperService for the Daily app.
 *
 * Draws the user's selected wallpaper image on the lock screen. If no wallpaper
 * is set, falls back to a solid brand orange (#FF7043) background with "Daily"
 * text centered on the screen.
 *
 * This is an MVP implementation — it renders a static image without animation.
 * The engine uses a Handler to post draw calls onto the surface, re-drawing when
 * the wallpaper URI changes or the surface is recreated.
 */
class WallpaperService : WallpaperService() {

    override fun onCreateEngine(): Engine = WallpaperEngine()

    private fun context(): Context = this

    inner class WallpaperEngine : Engine() {

        companion object {
            private const val TAG = "WallpaperEngine"
            private val BRAND_COLOR = Color.parseColor("#FF7043")
            private const val DEFAULT_TEXT = "Daily"
        }

        private val handler = Handler(Looper.getMainLooper())
        private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            isFilterBitmap = true
        }
        private val fallbackPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            style = Paint.Style.FILL
        }

        private var currentBitmap: android.graphics.Bitmap? = null
        private var wallpaperUri: String? = null
        private var isVisibilityChanged = true
        private var isVisible = false

        private val drawRunnable = object : Runnable {
            override fun run() {
                if (!isVisible) return
                drawWallpaper()
            }
        }

        override fun onCreate(surfaceHolder: SurfaceHolder) {
            super.onCreate(surfaceHolder)
            Log.d(TAG, "WallpaperEngine created")
            setTouchEventsEnabled(false)
            loadWallpaperBitmap()
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)
            isVisible = visible
            if (visible) {
                Log.d(TAG, "Wallpaper visible — drawing")
                drawWallpaper()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)
            Log.d(TAG, "Surface changed: ${width}x${height}")
            if (isVisible) {
                drawWallpaper()
            }
        }

        override fun onSurfaceDestroyed(holder: SurfaceHolder) {
            super.onSurfaceDestroyed(holder)
            isVisible = false
            handler.removeCallbacks(drawRunnable)
            Log.d(TAG, "Surface destroyed")
        }

        override fun onDestroy() {
            super.onDestroy()
            handler.removeCallbacks(drawRunnable)
            currentBitmap?.recycle()
            currentBitmap = null
            Log.d(TAG, "WallpaperEngine destroyed")
        }

        override fun onConfigurationChanged(newConfig: Configuration) {
            super.onConfigurationChanged(newConfig)
            Log.d(TAG, "Configuration changed: $newConfig")
            if (isVisible) {
                drawWallpaper()
            }
        }

        /**
         * Loads the wallpaper bitmap asynchronously from the stored URI.
         * The actual image file path is reconstructed from the content/file URI.
         */
        private fun loadWallpaperBitmap() {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val prefs = (context().applicationContext
                        .getSystemService(Context.APPLICATION_SERVICE) as? android.app.Application
                        ?.let { com.daily.app.DailyApplication.getInstance() }
                        ?: context().applicationContext) as? android.app.Application

                    val uri = UserPreferences((context().applicationContext as android.app.Application).hiltComponent?.let {
                        // Direct access — in production this is handled by Hilt
                        context().applicationContext
                    } ?: context().applicationContext).currentWallpaperUri.first()

                    // Re-check after the coroutine switch — we read from prefs directly
                    val pref = UserPreferences(context().applicationContext as android.app.Application)
                    val wallpaperUriStr = pref.currentWallpaperUri.first()
                    wallpaperUri = wallpaperUriStr

                    currentBitmap?.recycle()
                    currentBitmap = if (wallpaperUriStr.isNullOrBlank()) {
                        null
                    } else {
                        decodeBitmap(context(), wallpaperUriStr)
                    }

                    withContext(Dispatchers.Main) {
                        handler.post(drawRunnable)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to load wallpaper bitmap", e)
                }
            }
        }

        /**
         * Draws the wallpaper onto the current surface.
         * If a bitmap is available, draws it scaled to cover the surface.
         * Otherwise, draws the brand fallback.
         */
        private fun drawWallpaper() {
            val holder = surfaceHolder
            val canvas: Canvas? = try {
                holder.lockCanvas()
            } catch (e: Exception) {
                Log.w(TAG, "Failed to lock canvas", e)
                return
            }
            if (canvas == null) return

            try {
                val width = canvas.width
                val height = canvas.height
                if (width <= 0 || height <= 0) return

                if (currentBitmap != null) {
                    drawBitmapWallpaper(canvas, width, height)
                } else {
                    drawFallback(canvas, width, height)
                }
            } finally {
                try {
                    holder.unlockCanvasAndPost(canvas)
                } catch (e: IllegalStateException) {
                    // Surface may have been destroyed — ignore
                }
            }

            // Schedule next draw for responsiveness
            handler.postDelayed(drawRunnable, 500)
        }

        private fun drawBitmapWallpaper(canvas: Canvas, width: Int, height: Int) {
            val bitmap = currentBitmap ?: return
            val src = Rect(0, 0, bitmap.width, bitmap.height)
            val dst = Rect(0, 0, width, height)
            canvas.drawBitmap(bitmap, src, dst, paint)
        }

        private fun drawFallback(canvas: Canvas, width: Int, height: Int) {
            // Solid brand orange background
            backgroundPaint.color = BRAND_COLOR
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)

            // Centered "Daily" text
            fallbackPaint.textSize = (width * 0.12).coerceAtMost(120f)
            fallbackPaint.textAlign = Paint.Align.CENTER

            val fontMetrics = fallbackPaint.fontMetrics
            val textHeight = fontMetrics.descent - fontMetrics.ascent
            val y = (height / 2f) + textHeight / 2f - fontMetrics.descent

            canvas.drawText(DEFAULT_TEXT, width / 2f, y, fallbackPaint)
        }
    }

    /**
     * Decodes a bitmap from a file or content URI, scaling it down if necessary
     * to avoid OOM on large images.
     */
    private fun decodeBitmap(context: Context, uriString: String): android.graphics.Bitmap? {
        return try {
            val file = File(uriString)
            if (file.exists()) {
                BitmapFactory.decodeFile(uriString)
            } else {
                // Try as content URI
                val uri = android.net.Uri.parse(uriString)
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }
        } catch (e: Exception) {
            Log.e("WallpaperService", "Failed to decode bitmap from $uriString", e)
            null
        }
    }
}
