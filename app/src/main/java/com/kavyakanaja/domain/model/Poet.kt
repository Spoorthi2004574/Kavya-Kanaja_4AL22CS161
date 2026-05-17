package com.kavyakanaja.domain.model

data class Poet(
    val id: String,
    val name: String,
    val bio: String,
    val bioEnglish: String? = null,
    val imageDrawableName: String?,
    val famousWorks: List<String>,
)
