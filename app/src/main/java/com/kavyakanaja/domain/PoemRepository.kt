package com.kavyakanaja.domain

import com.kavyakanaja.domain.model.Catalog
import com.kavyakanaja.domain.model.Poem
import com.kavyakanaja.domain.model.Poet
import java.time.LocalDate

interface PoemRepository {
    suspend fun getCatalog(): Catalog

    suspend fun getPoemOfTheDay(date: LocalDate = LocalDate.now()): Poem

    suspend fun getPoemById(id: String): Poem?

    suspend fun getPoetById(id: String): Poet?
}
