package com.balon.blmovie.core.model

import kotlinx.serialization.Serializable

/**
 * {
 * "actorAlias": "",
 * "actorArea": "内地",
 * "actorEn": "zhaoliying",
 * "actorId": 45444,
 * "actorName": "赵丽颖",
 * "actorPic": "//imgwx3.2345.com/dianyingimg/star/img/d/0/388/photo_192x262.jpg",
 * "actorSex": "女",
 * "actorTime": "2025-08-25T16:35:11",
 * "typeId": 41,
 * "typeName": "演员"
 * }
 */
@Serializable
data class Actor(
    val actorAlias: String,
    val actorArea: String,
    val actorEn: String,
    val actorId: Int,
    val actorName: String,
    val actorPic: String,
    val actorSex: String,
    val actorTime: String,
    val typeId: Int,
    val typeName: String
)