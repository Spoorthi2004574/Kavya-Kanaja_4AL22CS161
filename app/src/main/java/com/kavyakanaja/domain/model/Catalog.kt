package com.kavyakanaja.domain.model

data class Catalog(
    val poems: List<Poem>,
    val poetsById: Map<String, Poet>,
)
