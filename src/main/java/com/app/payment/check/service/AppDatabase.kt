package com.app.payment.check.service

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import java.util.Arrays


class AppDatabase(context: Context) {
    var preferences: SharedPreferences

    init {
        preferences = context.getSharedPreferences("shared", Context.MODE_PRIVATE)
    }


    fun getString(key: String): String? {
        return preferences.getString(key, "")
    }

    fun putString(key: String, value: String) {
        preferences.edit().putString(key, value).apply()
    }

    fun putListString(key: String, stringList: String) {

        var myStringList = getListString(key)
        myStringList?.add(stringList)

        preferences.edit().putString(key, myStringList?.let { TextUtils.join("‚‗‚", it) }).apply()
    }

    fun getListString(key: String?): ArrayList<String> {
        return ArrayList(Arrays.asList(*TextUtils.split(preferences.getString(key, ""), "‚‗‚")))
    }
}