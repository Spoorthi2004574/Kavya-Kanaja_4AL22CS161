package com.kavyakanaja.data

import com.kavyakanaja.domain.model.Poem
import java.time.LocalDate

internal fun poemForDate(poems: List<Poem>, date: LocalDate): Poem {
    if (poems.isEmpty()) error("Catalog has no poems")
    val size = poems.size.toLong()
    val epochDay = date.toEpochDay()
    val index = ((epochDay % size) + size) % size
    return poems[index.toInt()]
}
