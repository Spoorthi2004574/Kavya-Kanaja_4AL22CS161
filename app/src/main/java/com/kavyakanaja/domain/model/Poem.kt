package com.kavyakanaja.domain.model

data class Poem(
    val id: String,
    val title: String,
    val poetId: String,
    val text: String,
    val meaning: String,
    val meaningEnglish: String? = null,
    val audioFileName: String,
    val difficultWords: List<DifficultWord>,
)
