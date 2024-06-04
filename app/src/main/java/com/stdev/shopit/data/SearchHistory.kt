package com.stdev.shopit.data

import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.stdev.shopit.data.model.CartItem
import com.stdev.shopit.data.model.Product
import com.stdev.shopit.data.model.ShopItem

class SearchHistory(private val sharedPreferences: SharedPreferences) {
    fun write(input: ShopItem) {
        var currentSearchHistory = read()
        currentSearchHistory = currentSearchHistory.filter { it.id != input.id }.toMutableList()
        if (currentSearchHistory.size == 10) {
            currentSearchHistory.removeAt(currentSearchHistory.lastIndex)
        }
        currentSearchHistory.add(0, input)
        clear()
        sharedPreferences
            .edit()
            .putString(SEARCH_HISTORY_KEY, Gson().toJson(currentSearchHistory))
            .apply()
    }

    fun clear() {
        sharedPreferences
            .edit()
            .remove(SEARCH_HISTORY_KEY)
            .apply()
    }

    fun read(): List<ShopItem> {
        val json = sharedPreferences.getString(SEARCH_HISTORY_KEY, null) ?: return emptyList()
        return Gson().fromJson(json, object : TypeToken<List<ShopItem>>() {}.type)
    }

    companion object {
        const val SEARCH_HISTORY_KEY = "search_history_key"
    }
}