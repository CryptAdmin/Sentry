package com.cryptAdmin.sentry.admin

import android.app.admin.DeviceAdminReceiver
import android.content.Context
import android.content.Intent
import android.os.UserHandle

import com.cryptAdmin.sentry.Monitor
import com.cryptAdmin.sentry.NotificationManager
import com.cryptAdmin.sentry.Preferences

class DeviceAdminReceiver : DeviceAdminReceiver() {
    override fun onPasswordFailed(context: Context, intent: Intent, user: UserHandle) {
        super.onPasswordFailed(context, intent, user)
        val prefs = Preferences(context)
        if (!prefs.isEnabled) return
        if (prefs.monitor.and(Monitor.PASSWORD.value) != 0)
            NotificationManager(context).notifyPassword()
        if (prefs.isMaxFailedPasswordAttemptsDefaultApiChecked) return
        val maxFailedPasswordAttempts = prefs.maxFailedPasswordAttempts
        if (maxFailedPasswordAttempts <= 0) return
        val admin = DeviceAdminManager(context)
        if (admin.getCurrentFailedPasswordAttempts() >= maxFailedPasswordAttempts)
            try { admin.wipeData() } catch (_: SecurityException) {}
    }
}