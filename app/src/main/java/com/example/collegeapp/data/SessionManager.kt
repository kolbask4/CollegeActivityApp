package com.example.collegeapp.data

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private var prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    private var editor: SharedPreferences.Editor = prefs.edit()

    companion object {
        const val PREF_NAME = "CollegeAppSession"
        const val KEY_IS_LOGGED_IN = "isLoggedIn"
        const val KEY_USER_IIN = "userIin"
    }

    fun saveUserSession(iin: String) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.putString(KEY_USER_IIN, iin)
        editor.apply()
    }

    fun clearSession() {
        editor.clear()
        editor.apply()
    }

    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }

    fun getUserIin(): String? {
        return prefs.getString(KEY_USER_IIN, null)
    }
} 