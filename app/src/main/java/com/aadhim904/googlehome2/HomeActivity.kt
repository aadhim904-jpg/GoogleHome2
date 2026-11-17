
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
        setPadding(dp(10), dp(10), dp(10), dp(10))

        background = GradientDrawable().apply {
            setColor(Color.rgb(42, 49, 65))
            cornerRadius = dp(19).toFloat()
        }
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


        val subHeading = makeText(title, 23f)
        subHeading.typeface = Typeface.DEFAULT_BOLD

        val headingParams = LinearLayout.LayoutParams(-1, dp(60))
        headingParams.topMargin = dp(12)
        panel.addView(subHeading, headingParams)

        panel.addView(
            makeButton("✕ Close") {
                hidePanel(panel)
            }
        )

        val space = Space(this)
        panel.addView(
            space,
            LinearLayout.LayoutParams(1, dp(18))
        )

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

        } else {
            panel.addView(
                makeButton("HDMI / AV / TV Inputs") {
                    openInputSettings()
                }
            )

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



