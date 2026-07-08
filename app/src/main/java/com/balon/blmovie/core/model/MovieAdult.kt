package com.balon.blmovie.core.model

import kotlinx.serialization.Serializable

@Serializable
data class MovieAdult(
    val typeId: Int,
    val typeName: String,
    val vodEn: String,
    val vodId: Long,
    val vodName: String,
    val vodPic: String,
    val vodPlayFrom: String,
    val vodRemarks: String,
    val vodTime: String
)