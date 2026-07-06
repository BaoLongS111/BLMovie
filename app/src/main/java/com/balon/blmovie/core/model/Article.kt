package com.balon.blmovie.core.model

import kotlinx.serialization.Serializable

@Serializable
data class Article(
    val artAuthor: String,
    val artBlurb: String,
    val artFrom: String,
    val artId: Int,
    val artName: String,
    val artPic: String,
    val artRemarks: String,
    val artTime: String,
    val typeId: Int,
    val typeName: String
)