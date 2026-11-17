package com.aadhim904.googlehome2

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast

class HomeActivity : Activity() {
    private val bg = Color.rgb(13, 17, 27)
    private val panel = Color.rgb(29, 37, 52)
    private val accent = Color.rgb(105, 155, 255)
    private lateinit var root: LinearLayout
    private var currentPage = "Home"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.statusBarColor = bg
        window.navigationBarColor = bg
        showHome()
    }

    private fun base(title: String, subtitle: String): LinearLayout {
        root = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(42, 28, 42, 24)
            setBackgroundColor(bg)
            isFocusable = true
            isFocusableInTouchMode = true
        }
        val top = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }
        val brand = text("GoogleHome2", 25, Color.WHITE, true)
        top.addView(brand, LinearLayout.LayoutParams(0, -2, 1f))
        fun nav(label: String, action: () -> Unit) {
            val item = text(label, 14, Color.WHITE, true).apply {
                setPadding(14, 10, 14, 10)
                isFocusable = true
                isClickable = true
                background = rounded(panel)
                setOnClickListener { action() }
                setOnFocusChangeListener { v, focused ->
                    v.background = rounded(if (focused) accent else panel)
                }
            }
            top.addView(item)
        }
        nav("HOME") { showHome() }
        nav("SOURCE") { openSource() }
        nav("SETTINGS") { showSettings() }
        root.addView(top)
        root.addView(text(title, 30, Color.WHITE, true).apply { setPadding(0, 28, 0, 3) })
        root.addView(text(subtitle, 15, Color.LTGRAY, false).apply { setPadding(0, 0, 0, 20) })
        return root
    }

    private fun text(value: String, size: Int, color: Int, bold: Boolean): TextView =
        TextView(this).apply {
            text = value
            textSize = size.toFloat()
            setTextColor(color)
            if (bold) setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
        }

    private fun card(label: String, detail: String = "", action: () -> Unit): TextView {
        return TextView(this).apply {
            text = if (detail.isBlank()) label else "$label\n$detail"
            textSize = 17f
            setTextColor(Color.WHITE)
            setTypeface(typeface, Typeface.BOLD)
            gravity = Gravity.CENTER_VERTICAL
            setPadding(22, 12, 22, 12)
            background = rounded(panel)
            isFocusable = true
            isFocusableInTouchMode = true
            setOnClickListener { action() }
            setOnFocusChangeListener { v, focused ->
                v.scaleX = if (focused) 1.045f else 1f
                v.scaleY = if (focused) 1.045f else 1f
                v.background = rounded(if (focused) accent else panel)
            }
        }
    }

    private fun rounded(color: Int): android.graphics.drawable.Drawable {
        val d = android.graphics.drawable.GradientDrawable()
        d.setColor(color)
        d.cornerRadius = 18f
        return d
    }

    private fun addRow(parent: LinearLayout, items: List<Pair<String, () -> Unit>>) {
        val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
        items.forEach { (name, action) ->
            val c = card(name, "", action)
            val lp = LinearLayout.LayoutParams(0, 82, 1f)
            lp.setMargins(0, 0, 14, 0)
            row.addView(c, lp)
        }
        parent.addView(row)
    }

    private fun showHome() {
        currentPage = "Home"
        val page = base("Welcome to your TV", "Your entertainment, all in one place")
        page.addView(text("Quick access", 19, Color.WHITE, true).apply { setPadding(0, 8, 0, 12) })
        addRow(page, listOf(
            "Apps" to { showApps() },
            "Movies" to { showApps() },
            "Live TV" to { openSource() },
            "Source" to { openSource() }
        ))
        page.addView(text("Your apps", 20, Color.WHITE, true).apply { setPadding(0, 26, 0, 12) })
        val apps = installedApps()
        if (apps.isEmpty()) {
            page.addView(text("No launchable apps found yet.", 16, Color.LTGRAY, false))
        } else {
            val rows = apps.chunked(4)
            rows.take(3).forEach { group ->
                val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
                group.forEach { app ->
                    val v = card(app.first, "", app.second)
                    val lp = LinearLayout.LayoutParams(0, 72, 1f)
                    lp.setMargins(0, 0, 12, 12)
                    row.addView(v, lp)
                }
                while (row.childCount < 4) row.addView(View(this), LinearLayout.LayoutParams(0, 72, 1f))
                page.addView(row)
            }
        }
        page.addView(text("Use the TV remote: arrows to move · OK to open · Back to return", 13, Color.GRAY, false).apply { setPadding(0, 16, 0, 0) })
        setContentView(page)
        page.post { page.requestFocus() }
    }

    private fun installedApps(): List<Pair<String, () -> Unit>> {
        val pm = packageManager
        val intents = listOf(
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LEANBACK_LAUNCHER),
            Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)
        )
        val found = linkedMapOf<String, Intent>()
        intents.forEach { intent ->
            pm.queryIntentActivities(intent, 0).forEach { info ->
                val pkg = info.activityInfo.packageName
                if (pkg != packageName) {
                    val launch = pm.getLaunchIntentForPackage(pkg) ?: intent.setClassName(pkg, info.activityInfo.name)
                    found.putIfAbsent(info.loadLabel(pm).toString(), launch)
                }
            }
        }
        return found.entries.sortedBy { it.key.lowercase() }.map { entry ->
            entry.key to { try { startActivity(entry.value) } catch (_: Exception) { Toast.makeText(this, "App could not be opened", Toast.LENGTH_SHORT).show() } }
        }
    }

    private fun showApps() {
        val page = base("Apps", "Open an app installed on this TV")
        val apps = installedApps()
        apps.chunked(4).forEach { group ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            group.forEach { app ->
                val v = card(app.first, "", app.second)
                val lp = LinearLayout.LayoutParams(0, 78, 1f); lp.setMargins(0, 0, 12, 12)
                row.addView(v, lp)
            }
            page.addView(row)
        }
        setContentView(page)
        page.post { if (page.childCount > 0) page.getChildAt(0).requestFocus() }
    }

    private fun openSource() {
        val options = listOf(
            "TV input settings" to Intent("android.settings.TV_INPUT"),
            "Display settings" to Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS)
        )
        try {
            startActivity(options[0].second)
        } catch (_: Exception) {
            Toast.makeText(this, "Use the TV remote's Source/Input button to change HDMI or AV.", Toast.LENGTH_LONG).show()
        }
    }

    private fun showSettings() {
        currentPage = "Settings"
        val page = base("Settings", "Google TV-inspired settings")
        val settings = listOf(
            "Network & Internet" to Intent(android.provider.Settings.ACTION_WIFI_SETTINGS),
            "Display & Sound" to Intent(android.provider.Settings.ACTION_DISPLAY_SETTINGS),
            "Apps" to Intent(android.provider.Settings.ACTION_APPLICATION_SETTINGS),
            "Bluetooth & Accessories" to Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS),
            "System" to Intent(android.provider.Settings.ACTION_SETTINGS),
            "About" to Intent(android.provider.Settings.ACTION_DEVICE_INFO_SETTINGS)
        )
        settings.chunked(3).forEach { group ->
            val row = LinearLayout(this).apply { orientation = LinearLayout.HORIZONTAL }
            group.forEach { (name, intent) ->
                val v = card(name, "Open settings", {
                    try { startActivity(intent) } catch (_: Exception) {
                        Toast.makeText(this, "This settings page is not available on this TV.", Toast.LENGTH_SHORT).show()
                    }
                })
                val lp = LinearLayout.LayoutParams(0, 88, 1f); lp.setMargins(0, 0, 14, 14)
                row.addView(v, lp)
            }
            page.addView(row)
        }
        setContentView(page)
        page.post { if (page.childCount > 0) page.getChildAt(0).requestFocus() }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_MENU) { showSettings(); return true }
        if (keyCode == KeyEvent.KEYCODE_BACK && currentPage != "Home") { showHome(); return true }
        return super.onKeyDown(keyCode, event)
    }
}
