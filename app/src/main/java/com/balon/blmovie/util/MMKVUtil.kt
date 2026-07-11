package com.balon.blmovie.util

import com.tencent.mmkv.MMKV
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MMKVUtil @Inject constructor(
    private val json: Json
) {

    private val mmkv: MMKV by lazy {
        MMKV.mmkvWithID(STORAGE_ID)
    }

    /**
     * 获取所有搜索历史。
     */
    fun getHistory(): List<String> {
        val content = mmkv.decodeString(KEY_SEARCH_HISTORY)
            ?: return emptyList()

        return runCatching {
            json.decodeFromString<List<String>>(content)
        }.getOrDefault(emptyList())
    }

    /**
     * 添加搜索记录。
     *
     * 相同关键词会先删除，再移动到列表最前面。
     */
    fun addHistory(keyword: String): List<String> {
        val realKeyword = keyword.trim()

        if (realKeyword.isBlank()) {
            return getHistory()
        }

        val newHistory = getHistory()
            .filterNot {
                it.equals(
                    other = realKeyword,
                    ignoreCase = true
                )
            }
            .toMutableList()
            .apply {
                add(
                    index = 0,
                    element = realKeyword
                )
            }
            .take(MAX_HISTORY_SIZE)

        saveHistory(newHistory)

        return newHistory
    }

    /**
     * 删除一条历史记录。
     */
    fun removeHistory(keyword: String): List<String> {
        val newHistory = getHistory()
            .filterNot {
                it == keyword
            }

        saveHistory(newHistory)

        return newHistory
    }

    /**
     * 清空搜索历史。
     */
    fun clearHistory() {
        mmkv.removeValueForKey(KEY_SEARCH_HISTORY)
    }

    private fun saveHistory(
        history: List<String>
    ) {
        mmkv.encode(
            KEY_SEARCH_HISTORY,
            json.encodeToString(history)
        )
    }

    private companion object {
        const val STORAGE_ID = "search_history_storage"
        const val KEY_SEARCH_HISTORY = "search_history"
        const val MAX_HISTORY_SIZE = 20
    }
}