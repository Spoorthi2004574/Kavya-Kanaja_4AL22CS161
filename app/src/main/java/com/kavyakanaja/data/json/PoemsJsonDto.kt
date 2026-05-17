package com.kavyakanaja.data.json

import kotlinx.serialization.Serializable

@Serializable
data class PoemsFileDto(
    val poems: List<PoemDto> = emptyList(),
    val poets: List<PoetDto> = emptyList(),
)

@Serializable
data class PoemDto(
    val id: String,
    val title: String,
    val poetId: String,
    val text: String,
    val meaning: String,
    val meaningEnglish: String? = null,
    val audioFileName: String,
    val difficultWords: List<DifficultWordDto> = emptyList(),
)

@Serializable
data class DifficultWordDto(
    val surface: String,
    val meaning: String,
)

@Serializable
data class PoetDto(
    val id: String,
    val name: String,
    val bio: String,
    val bioEnglish: String? = null,
    val imageDrawableName: String? = null,
    val famousWorks: List<String> = emptyList(),
)
