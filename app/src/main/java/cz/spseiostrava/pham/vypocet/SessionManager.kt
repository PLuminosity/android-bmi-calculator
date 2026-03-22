package cz.spseiostrava.pham.vypocet

import android.content.Context
import androidx.core.content.edit

/**
 * Lightweight session store backed by SharedPreferences.
 * Call [saveSession] after a successful login/register,
 * [clear] on logout, and [userId] / [isLoggedIn] anywhere you need them.
 */
object SessionManager {
    private const val PREF_NAME = "session"
    private const val KEY_USER_ID = "userId"
    private const val KEY_LOGGED_IN = "isLoggedIn"

    fun saveSession(context: Context, userId: Long) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit {
            putLong(KEY_USER_ID, userId)
            putBoolean(KEY_LOGGED_IN, true)
        }
    }

    fun userId(context: Context): Long =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getLong(KEY_USER_ID, -1L)

    fun isLoggedIn(context: Context): Boolean =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getBoolean(KEY_LOGGED_IN, false)

    fun clear(context: Context) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
