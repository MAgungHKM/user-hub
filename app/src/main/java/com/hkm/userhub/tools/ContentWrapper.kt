package com.hkm.userhub.tools

import android.content.Context
import android.content.ContextWrapper
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import java.util.*


class ContentWrapper {
    companion object {
        @Suppress("DEPRECATION")
        fun changeLang(mContext: Context, lang_code: String): ContextWrapper? {
            var context = mContext
            val rs: Resources = context.resources
            val config: Configuration = rs.configuration
            val locale = Locale(lang_code)
            Locale.setDefault(locale)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
                config.setLocale(locale)
            else
                config.locale = locale

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                context = context.createConfigurationContext(config)
            else
                context.resources.updateConfiguration(config, context.resources.displayMetrics)

            return ContextWrapper(context)
        }
    }
}