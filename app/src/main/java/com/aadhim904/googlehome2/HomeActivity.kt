
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

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()

    private fun panel(color: Int): GradientDrawable {
        return GradientDrawable().apply {
            setColor(color)
            cornerRadius = dp(18).toFloat()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.statusBarColor = Color.rgb(10, 13, 22)
        window.navigationBarColor = Color.rgb(10, 13, 22)

        val page = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(32), dp(24), dp(32), dp(24))
            setBackgroundColor(Color.rgb(10, 13, 22))
        }

        val header = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
        }

        val logo = TextView(this).apply {
            text = "GoogleHome2"
            textSize = 28f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
        }

        val headerInfo = TextView(this).apply {
            text = "ANDROID TV"
            textSize = 13f
            setTextColor(Color.LTGRAY)
            gravity = Gravity.CENTER_VERTICAL
        }

        header.addView(
            logo,
            LinearLayout.LayoutParams(0, dp(55), 1f)
        )
        header.addView(headerInfo)
        page.addView(header)

        val hero = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            gravity = Gravity.CENTER_VERTICAL
            setPadding(dp(28), dp(22), dp(28), dp(22))
            background = panel(Color.rgb(30, 39, 62))
        }

        val heroTitle = TextView(this).apply {
            text = "Welcome to your entertainment"
            textSize = 27f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
        }

        val heroSubtitle = TextView(this).apply {
            text = "Your apps. Your screen. Your experience."
            textSize = 15f
            setTextColor(Color.LTGRAY)
        }

        hero.addView(heroTitle)
        hero.addView(heroSubtitle)

        val heroParams = LinearLayout.LayoutParams(
            -1, dp(155)
        )
        heroParams.topMargin = dp(25)
        page.addView(hero, heroParams)

        val appsTitle = TextView(this).apply {
            text = "Your apps"
            textSize = 23f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.WHITE)
        }

        val titleParams = LinearLayout.LayoutParams(
            -1, -2
        )
        titleParams.topMargin = dp(30)
        titleParams.bottomMargin = dp(15)
        page.addView(appsTitle, titleParams)

        val appScroll = HorizontalScrollView(this).apply {
            isHorizontalScrollBarEnabled = false
            isFocusable = false
        }

        val appRow = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
        }

        val apps = getInstalledApps()

        if (apps.isEmpty()) {
            val empty = TextView(this).apply {
                text = "No apps found"
                textSize = 16f
                setTextColor(Color.LTGRAY)
            }
            appRow.addView(empty)
        }

        for (info in apps) {
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                gravity = Gravity.CENTER
                setPadding(dp(12), dp(15), dp(12), dp(12))
                background = panel(Color.rgb(35, 40, 55))
                isFocusable = true
                isClickable = true
            }

            val icon = ImageView(this).apply {
                setImageDrawable(info.loadIcon(packageManager))
                scaleType = ImageView.ScaleType.FIT_CENTER
            }

            card.addView(
                icon,
                LinearLayout.LayoutParams(dp(68), dp(68))
            )

            val name = TextView(this).apply {
                text = info.loadLabel(packageManager).toString()
                textSize = 13f
                setTextColor(Color.WHITE)
                gravity = Gravity.CENTER
                maxLines = 2
            }

            val nameParams = LinearLayout.LayoutParams(
                dp(105), dp(42)
            )
            nameParams.topMargin = dp(8)
            card.addView(name, nameParams)

            card.setOnFocusChangeListener { view, focused ->
                view.background = panel(
                    if (focused) Color.rgb(65, 91, 145)
                    else Color.rgb(35, 40, 55)
                )
                view.scaleX = if (focused) 1.06f else 1f
                view.scaleY = if (focused) 1.06f else 1f
            }

            card.setOnClickListener {
                val pkg = info.activityInfo.packageName
                val launch = packageManager
                    .getLeanbackLaunchIntentForPackage(pkg)
                    ?: packageManager.getLaunchIntentForPackage(pkg)

                if (launch != null) startActivity(launch)
            }

            val cardParams = LinearLayout.LayoutParams(
                dp(130), dp(150)
            )
            cardParams.marginEnd = dp(15)
            appRow.addView(card, cardParams)
        }

        appScroll.addView(appRow)

        page.addView(
            appScroll,
            LinearLayout.LayoutParams(-1, dp(165))
        )

        val footer = TextView(this).apply {
            text = "Select an app to open"
            textSize = 13f
            setTextColor(Color.GRAY)
        }

        val footerParams = LinearLayout.LayoutParams(-1, -2)
        footerParams.topMargin = dp(20)
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

