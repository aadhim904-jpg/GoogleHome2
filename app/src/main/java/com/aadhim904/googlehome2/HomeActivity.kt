
package com.aadhim904.googlehome2

import android.app.Activity
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.provider.Settings
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.widget.*
import android.widget.FrameLayout.LayoutParams
import android.os.Handler
import android.os.Looper
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HomeActivity : Activity() {

    private lateinit var root: FrameLayout
    private lateinit var leftPanel: LinearLayout
    private lateinit var rightPanel: LinearLayout
    private lateinit var hero: TextView

    private val prefs by lazy {
        getSharedPreferences("GoogleHome2Prefs", MODE_PRIVATE)
    }

    private val panelWidth = 290

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 16, 28)
        window.navigationBarColor = Color.rgb(12, 16, 28)

        root = FrameLayout(this)
        root.setBackgroundColor(Color.rgb(12, 16, 28))
        setContentView(root)

        buildHome()
        buildPanels()
    }
     
private val clockHandler = Handler(Looper.getMainLooper())

private lateinit var clockTime: TextView
private lateinit var clockDate: TextView

private val clockUpdater = object : Runnable {
    override fun run() {
        if (::clockTime.isInitialized && ::clockDate.isInitialized) {
            val now = Date()

            clockTime.text =
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(now)

            clockDate.text =
                SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH).format(now)
        }

        clockHandler.postDelayed(this, 30000)
    }
}
 
    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun cardBackground(glass: Boolean): GradientDrawable {
        return GradientDrawable().apply {
            setColor(
                if (glass) Color.argb(220, 35, 45, 65)
                else Color.rgb(30, 36, 50)
            )
            cornerRadius = dp(20).toFloat()
            setStroke(dp(1), Color.argb(90, 210, 225, 255))
        }
    }

    private fun makeText(
        value: String,
        size: Float,
        color: Int = Color.WHITE
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = size
            setTextColor(color)
            gravity = Gravity.CENTER_VERTICAL
        }
    }

    private fun makeButton(
        value: String,
        action: () -> Unit
    ): TextView {
        return TextView(this).apply {
            text = value
            textSize = 16f
            setTextColor(Color.WHITE)
            gravity = Gravity.CENTER
            isFocusable = true
            isClickable = true
            setPadding(dp(14), dp(13), dp(14), dp(13))
            background = cardBackground(true)
            setOnClickListener { action() }
        }
    }

    private fun buildHome() {
        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(28), dp(20), dp(28), dp(20))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = makeText("Google TV", 23f)
        brand.typeface = Typeface.DEFAULT_BOLD

        header.addView(
            brand,
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )
        
        private val clockHandler = Handler(Looper.getMainLooper())

        private lateinit var clockTime: TextView
        private lateinit var clockDate: TextView

        private val clockUpdater = object : Runnable {
        override fun run() {
            if (::clockTime.isInitialized && ::clockDate.isInitialized) {
                val now = Date()

            clockTime.text =
                SimpleDateFormat("h:mm a", Locale.getDefault()).format(now)

            clockDate.text =
                SimpleDateFormat("EEE, dd MMM", Locale.ENGLISH).format(now)
        }

        clockHandler.postDelayed(this, 30000)
    }
}

        header.addView(
            makeButton("⚙ Settings") { showPanel(leftPanel) }
        )

        header.addView(
            makeButton("Source") { showPanel(rightPanel) }
        )

        page.addView(header)

        hero = makeText(
            "Welcome to Google TV",
            24f
        ).apply {
            setPadding(dp(22), dp(25), dp(22), dp(25))
            background = cardBackground(
                prefs.getBoolean("glass_enabled", true)
            )
        }

        val heroParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            dp(130)
        )
        heroParams.topMargin = dp(22)
        page.addView(hero, heroParams)

        val appTitle = makeText("Your Apps", 21f)
        appTitle.typeface = Typeface.DEFAULT_BOLD

        val titleParams = LinearLayout.LayoutParams(
            -1, dp(55)
        )
        titleParams.topMargin = dp(15)
        page.addView(appTitle, titleParams)

        val scroll = ScrollView(this)
        val appList = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
        }

        val apps = getInstalledApps()

        apps.chunked(3).forEach { group ->
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
            }

            
group.forEach { info ->

    val tile = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        isFocusable = true
        isClickable = true
        setPadding(dp(5), dp(5), dp(5), dp(5))

        background = GradientDrawable().apply {
            setColor(Color.TRANSPARENT)
            cornerRadius = dp(18).toFloat()
        }

        setOnClickListener {
            openApp(info)
        }

        setOnFocusChangeListener { view, focused ->
            view.background = if (focused) {
                cardBackground(true)
            } else {
                GradientDrawable().apply {
                    setColor(Color.TRANSPARENT)
                    cornerRadius = dp(18).toFloat()
                }
            }
        }
    }

    
val icon = ImageView(this).apply {
    setImageDrawable(info.loadIcon(packageManager))
    scaleType = ImageView.ScaleType.FIT_CENTER
    setPadding(dp(7), dp(7), dp(7), dp(7))

    background = GradientDrawable().apply {
        shape = GradientDrawable.OVAL
        setColor(Color.rgb(42, 49, 65))
    }

    clipToOutline = true
}


    tile.addView(
        icon,
        LinearLayout.LayoutParams(dp(68), dp(68))
    )

    val label = makeText(
        info.loadLabel(packageManager).toString(),
        12f
    ).apply {
        gravity = Gravity.CENTER
        maxLines = 1
        ellipsize = android.text.TextUtils.TruncateAt.END
    }

    tile.addView(
        label,
        LinearLayout.LayoutParams(-1, dp(30))
    )

    val params = LinearLayout.LayoutParams(
        0, dp(112), 1f
    )

    params.setMargins(dp(5), dp(5), dp(5), dp(5))
    row.addView(tile, params)
}


            appList.addView(row)
        }

        scroll.addView(appList)
        page.addView(
            scroll,
            LinearLayout.LayoutParams(-1, 0, 1f)
        )

        root.addView(
            page,
            LayoutParams(-1, -1)
        )
    }

    private fun getInstalledApps(): List<ResolveInfo> {
        val result = LinkedHashMap<String, ResolveInfo>()

        val categories = listOf(
            Intent.CATEGORY_LEANBACK_LAUNCHER,
            Intent.CATEGORY_LAUNCHER
        )

        categories.forEach { category ->
            val intent = Intent(Intent.ACTION_MAIN)
            intent.addCategory(category)

            packageManager.queryIntentActivities(intent, 0)
                .forEach { info ->
                    val packageName = info.activityInfo.packageName

                    if (packageName != this.packageName) {
                        result[packageName] = info
                    }
                }
        }

        return result.values.sortedBy {
            it.loadLabel(packageManager).toString().lowercase()
        }
    }

    private fun openApp(info: ResolveInfo) {
        try {
            val intent = Intent(Intent.ACTION_MAIN)
            intent.setClassName(
                info.activityInfo.packageName,
                info.activityInfo.name
            )
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        } catch (e: Exception) {
            Toast.makeText(this, "App cannot be opened", Toast.LENGTH_SHORT).show()
        }
    }

    private fun buildPanels() {
        leftPanel = createPanel("Settings", true)
        rightPanel = createPanel("Source", false)

        val paramsLeft = LayoutParams(
            dp(panelWidth),
            -1,
            Gravity.LEFT
        )

        val paramsRight = LayoutParams(
            dp(panelWidth),
            -1,
            Gravity.RIGHT
        )

        root.addView(leftPanel, paramsLeft)
        root.addView(rightPanel, paramsRight)

        leftPanel.translationX = -dp(panelWidth).toFloat()
        rightPanel.translationX = dp(panelWidth).toFloat()

        leftPanel.visibility = View.GONE
        rightPanel.visibility = View.GONE
    }

    

        if (isSettings) {
            val glassSwitch = Switch(this).apply {
                text = "Liquid Glass"
                textSize = 17f
                setTextColor(Color.WHITE)
                isChecked = prefs.getBoolean("glass_enabled", true)

                setOnCheckedChangeListener { _, checked ->
                    prefs.edit()
                        .putBoolean("glass_enabled", checked)
                        .apply()

                    hero.background = cardBackground(checked)
                }
            }

            panel.addView(
                glassSwitch,
                LinearLayout.LayoutParams(-1, dp(65))
            )

            panel.addView(
                makeButton("Default Home Settings") {
                    try {
                        startActivity(
                            Intent(Settings.ACTION_HOME_SETTINGS)
                        )
                    } catch (e: Exception) {
                        startActivity(Intent(Settings.ACTION_SETTINGS))
                    }
                }
            )

        
private fun createPanel(
    title: String,
    isSettings: Boolean
): LinearLayout {

    val panel = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        setPadding(dp(18), dp(25), dp(18), dp(20))
        background = cardBackground(true)
        isFocusableInTouchMode = true
    }

    val heading = makeText(title, 24f)
    heading.typeface = Typeface.DEFAULT_BOLD
    panel.addView(heading, LinearLayout.LayoutParams(-1, dp(55)))

    panel.addView(
        makeButton("✕ Close") {
            hidePanel(panel)
        }
    )

    val scroll = ScrollView(this)
    val content = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
    }

    if (isSettings) {

        addSection(content, "Display & Picture")

        content.addView(
            makeButton("Hi-Res Mode  ·  Picture Settings") {
                openSystemSettings(Settings.ACTION_DISPLAY_SETTINGS)
            }
        )

        addSection(content, "Sound")

        content.addView(
            makeButton("Dolby Digital+ Profile  ·  Sound Settings") {
                openSystemSettings(Settings.ACTION_SOUND_SETTINGS)
            }
        )

        addSection(content, "Hardware")

        content.addView(
            makeText(hardwareInfoText(), 14f).apply {
                setPadding(dp(8), dp(12), dp(8), dp(18))
            }
        )

        content.addView(
            makeButton("Wi-Fi Settings") {
                openSystemSettings(Settings.ACTION_WIFI_SETTINGS)
            }
        )

        content.addView(
            makeButton("Bluetooth Settings") {
                openSystemSettings(Settings.ACTION_BLUETOOTH_SETTINGS)
            }
        )

        content.addView(
            makeButton("Remote & HDMI Input") {
                openInputSettings()
            }
        )

    } else {

        content.addView(
            makeButton("HDMI / AV / TV Inputs") {
                openInputSettings()
            }
        )

        content.addView(
            makeText(
                "Google TV Source",
                16f
            ).apply {
                setPadding(dp(8), dp(18), dp(8), dp(10))
            }
        )
    }

    scroll.addView(content)

    panel.addView(
        scroll,
        LinearLayout.LayoutParams(-1, 0, 1f)
    )

    return panel
}

private fun addSection(
    parent: LinearLayout,
    name: String
) {
    val heading = makeText(name, 19f)
    heading.typeface = Typeface.DEFAULT_BOLD

    val params = LinearLayout.LayoutParams(-1, dp(55))
    params.topMargin = dp(12)

    parent.addView(heading, params)
}

private fun openSystemSettings(action: String) {
    try {
        startActivity(Intent(action))
    } catch (e: Exception) {
        startActivity(Intent(Settings.ACTION_SETTINGS))
    }
}

private fun hardwareInfoText(): String {
    val manager =
        getSystemService(ACTIVITY_SERVICE) as android.app.ActivityManager

    val memory = android.app.ActivityManager.MemoryInfo()
    manager.getMemoryInfo(memory)

    val storage = android.os.StatFs(
        android.os.Environment.getDataDirectory().path
    )

    return """
        Device: ${android.os.Build.MODEL}
        Android: ${android.os.Build.VERSION.RELEASE}
        Hardware: ${android.os.Build.HARDWARE}
        Total RAM: ${formatBytes(memory.totalMem)}
        Available RAM: ${formatBytes(memory.availMem)}
        Total Storage: ${formatBytes(storage.totalBytes)}
        Free Storage: ${formatBytes(storage.availableBytes)}
    """.trimIndent()
}

private fun formatBytes(bytes: Long): String {
    val gb = bytes / (1024.0 * 1024.0 * 1024.0)
    return String.format("%.1f GB", gb)
}
  

            val info = makeText(
                "Input switching is controlled by your TV system. Use the remote SOURCE / INPUT button if the TV does not provide an input settings screen.",
                14f
            )

            info.setPadding(dp(5), dp(20), dp(5), dp(10))
            panel.addView(info)
        }

        return panel
    }

    private fun openInputSettings() {
        try {
            startActivity(
                Intent("android.settings.TV_INPUT_SETTINGS")
            )
        } catch (e: Exception) {
            Toast.makeText(
                this,
                "Use the TV remote SOURCE / INPUT button",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun showPanel(panel: View) {
        val other = if (panel === leftPanel) rightPanel else leftPanel
        hidePanel(other)

        panel.visibility = View.VISIBLE
        root.bringChildToFront(panel)

        val start = if (panel === leftPanel) -dp(panelWidth).toFloat()
                    else dp(panelWidth).toFloat()

        panel.translationX = start

        panel.animate()
            .translationX(0f)
            .setDuration(250)
            .setInterpolator(DecelerateInterpolator())
            .start()

        panel.requestFocus()
    }

    private fun hidePanel(panel: View) {
        if (panel.visibility != View.VISIBLE) return

        val end = if (panel === leftPanel) -dp(panelWidth).toFloat()
                  else dp(panelWidth).toFloat()

        panel.animate()
            .translationX(end)
            .setDuration(200)
            .withEndAction {
                panel.visibility = View.GONE
            }
            .start()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_MENU,
            KeyEvent.KEYCODE_SETTINGS -> {
                showPanel(leftPanel)
                return true
            }

            KeyEvent.KEYCODE_TV_INPUT -> {
                showPanel(rightPanel)
                return true
            }

            KeyEvent.KEYCODE_BACK -> {
                if (leftPanel.visibility == View.VISIBLE) {
                    hidePanel(leftPanel)
                    return true
                }

                if (rightPanel.visibility == View.VISIBLE) {
                    hidePanel(rightPanel)
                    return true
                }
            }
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onBackPressed() {
        if (leftPanel.visibility == View.VISIBLE) {
            hidePanel(leftPanel)
        } else if (rightPanel.visibility == View.VISIBLE) {
            hidePanel(rightPanel)
        } else {
            super.onBackPressed()
        }
    }
}



