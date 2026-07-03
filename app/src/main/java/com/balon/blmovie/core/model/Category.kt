package com.balon.blmovie.core.model

import kotlinx.serialization.Serializable

/**
 * 视频1类别
 * {
 *       "createdAt": "2026-06-30T16:22:18",
 *       "sort": 1,
 *       "status": 1,
 *       "typeId": 1,
 *       "typeName": "电影片",
 *       "updatedAt": "2026-06-30T16:22:18"
 * }
 */
@Serializable
data class Category(
    val createdAt: String? = null,
    val sort: Int = 1,
    val status: Int = 1,
    val typeId: Int = 1,
    val typeName: String? = null,
    val updatedAt: String? = null
)