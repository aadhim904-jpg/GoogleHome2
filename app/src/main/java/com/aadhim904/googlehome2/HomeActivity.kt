
package com.aadhim904.googlehome2

import android.app.Activity
import android.os.Bundle
import android.content.Intent
import android.content.pm.ResolveInfo
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.view.Gravity
import android.view.View
import android.widget.*

class HomeActivity : Activity() {

    private fun dp(v: Int): Int =
        (v * resources.displayMetrics.density).toInt()

    private fun glass(focused: Boolean = false): GradientDrawable {
        return GradientDrawable(
            GradientDrawable.Orientation.TL_BR,
            intArrayOf(
                if (focused) Color.argb(105, 120, 170, 255)
                else Color.argb(65, 255, 255, 255),
                if (focused) Color.argb(65, 80, 110, 190)
                else Color.argb(22, 180, 205, 255),
                Color.argb(35, 255, 255, 255)
            )
        ).apply {
            cornerRadius = dp(22).toFloat()
            setStroke(
                dp(1),
                if (focused) Color.argb(220, 220, 235, 255)
                else Color.argb(90, 255, 255, 255)
            )
        }
    }

    private fun label(
        value: String,
        size: Float,
        color: Int,
        bold: Boolean = false
    ) = TextView(this).apply {
        text = value
        textSize = size
        setTextColor(color)
        if (bold) setTypeface(null, Typeface.BOLD)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(12, 15, 29)
        window.navigationBarColor = Color.rgb(12, 15, 29)

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(42), dp(25), dp(42), dp(30))
            background = GradientDrawable(
                GradientDrawable.Orientation.TL_BR,
                intArrayOf(
                    Color.rgb(13, 19, 39),
                    Color.rgb(24, 24, 48),
                    Color.rgb(9, 13, 27)
                )
            )
        }

        // TOP BAR
        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val brand = label("GoogleHome2", 27f, Color.WHITE, true)

        val topInfo = label(
            "HOME     ⚙",
            15f,
            Color.LTGRAY,
            true
        ).apply {
            gravity = Gravity.CENTER
            background = glass()
            setPadding(dp(22), dp(12), dp(22), dp(12))
        }

        header.addView(
            brand,
            LinearLayout.LayoutParams(0, dp(58), 1f)
        )
        header.addView(topInfo)
        page.addView(header)

        // HERO GLASS PANEL
        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(32), dp(24), dp(32), dp(24))
            background = glass()
            elevation = dp(8).toFloat()
        }

        val tag = label(
            "YOUR ENTERTAINMENT SPACE",
            12f,
            Color.rgb(170, 200, 255),
            true
        )

        val heroTitle = label(
            "Everything you love.",
            34f,
            Color.WHITE,
            true
        )

        val heroSubtitle = label(
            "All your entertainment in one place",
            16f,
            Color.LTGRAY
        )

        hero.addView(tag)
        hero.addView(heroTitle)
        hero.addView(heroSubtitle)

        val heroParams = LinearLayout.LayoutParams(-1, dp(190))
        heroParams.topMargin = dp(28)
        page.addView(hero, heroParams)

        // APPS TITLE
        val appsTitle = label(
            "Your Apps",
            24f,
            Color.WHITE,
            true
        )

        val titleParams = LinearLayout.LayoutParams(-1, -2)
        titleParams.topMargin = dp(32)
        titleParams.bottomMargin = dp(18)
        page.addView(appsTitle, titleParams)

        // INSTALLED APPS
        val appScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isFocusable = false
        }

        val appRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val apps = getInstalledApps()

        if (apps.isEmpty()) {
            appRow.addView(
                label("No apps found", 16f, Color.LTGRAY)
            )
        }

        for (info in apps) {

            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(12), dp(14), dp(12), dp(12))
                background = glass()
                isFocusable = true
                isClickable = true
                elevation = dp(5).toFloat()
            }

            val icon = ImageView(this).apply {
                setImageDrawable(info.loadIcon(packageManager))
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            card.addView(
                icon,
                LinearLayout.LayoutParams(dp(66), dp(66))
            )

            val appName = label(
                info.loadLabel(packageManager).toString(),
                13f,
                Color.WHITE,
                true
            ).apply {
                gravity = Gravity.CENTER
                maxLines = 2
            }

            val nameParams = LinearLayout.LayoutParams(
                dp(112), dp(42)
            )
            nameParams.topMargin = dp(10)
            card.addView(appName, nameParams)

            card.setOnFocusChangeListener { view, focused ->
                view.background = glass(focused)
                view.scaleX = if (focused) 1.07f else 1f
                view.scaleY = if (focused) 1.07f else 1f
            }

            card.setOnClickListener {
                val pkg = info.activityInfo.packageName
                val launch =
                    packageManager.getLeanbackLaunchIntentForPackage(pkg)
                        ?: packageManager.getLaunchIntentForPackage(pkg)

                if (launch != null) startActivity(launch)
            }

            val cardParams = LinearLayout.LayoutParams(
                dp(145), dp(155)
            )
            cardParams.marginEnd = dp(18)
            appRow.addView(card, cardParams)
        }

        appScroll.addView(appRow)
        page.addView(
            appScroll,
            LinearLayout.LayoutParams(-1, dp(170))
        )

        val footer = label(
            "Select an app to continue",
            13f,
            Color.GRAY
        )

        val footerParams = LinearLayout.LayoutParams(-1, -2)
        footerParams.topMargin = dp(25)
        page.addView(footer, footerParams)

        val screen = ScrollView(this).apply {
            setFillViewport(true)
            addView(page)
        }

        setContentView(screen)
    }

    private fun getInstalledApps(): List<ResolveInfo> {
        val found = LinkedHashMap<String, ResolveInfo>()

        val categories = listOf(
            Intent.CATEGORY_LEANBACK_LAUNCHER,
            Intent.CATEGORY_LAUNCHER
        )

        for (category in categories) {
            val query = Intent(Intent.ACTION_MAIN).apply {
                addCategory(category)
            }

            val results = packageManager.queryIntentActivities(query, 0)

            for (info in results) {
                val pkg = info.activityInfo.packageName
                if (pkg != packageName) {
                    found[pkg] = info
                }
            }
        }

        return found.values.sortedBy {
            it.loadLabel(packageManager).toString().lowercase()
        }
    }
}


