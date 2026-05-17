package com.kavyakanaja.data

import android.content.Context
import com.kavyakanaja.data.json.PoemsFileDto
import com.kavyakanaja.domain.PoemRepository
import com.kavyakanaja.domain.model.Catalog
import com.kavyakanaja.domain.model.DifficultWord
import com.kavyakanaja.domain.model.Poem
import com.kavyakanaja.domain.model.Poet
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDate

class PoemRepositoryImpl(
    private val appContext: Context,
) : PoemRepository {

    private val mutex = Mutex()
    private var cached: Catalog? = null

    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    override suspend fun getCatalog(): Catalog = mutex.withLock {
        cached ?: loadCatalogLocked().also { cached = it }
    }

    override suspend fun getPoemOfTheDay(date: LocalDate): Poem {
        val catalog = getCatalog()
        return poemForDate(catalog.poems, date)
    }

    override suspend fun getPoemById(id: String): Poem? {
        val catalog = getCatalog()
        return catalog.poems.firstOrNull { it.id == id }
    }

    override suspend fun getPoetById(id: String): Poet? {
        val catalog = getCatalog()
        return catalog.poetsById[id]
    }

    private suspend fun loadCatalogLocked(): Catalog = withContext(Dispatchers.IO) {
        val text = appContext.assets.open(ASSET_FILE).bufferedReader(Charsets.UTF_8).use { it.readText() }
        val dto = json.decodeFromString<PoemsFileDto>(text)
        dto.toDomain()
    }

    private fun PoemsFileDto.toDomain(): Catalog {
        val poetsById = poets.associate { poetDto ->
            poetDto.id to Poet(
                id = poetDto.id,
                name = poetDto.name,
                bio = poetDto.bio,
                bioEnglish = poetDto.bioEnglish,
                imageDrawableName = poetDto.imageDrawableName,
                famousWorks = poetDto.famousWorks,
            )
        }
        val poemsDomain = poems.map { p ->
            Poem(
                id = p.id,
                title = p.title,
                poetId = p.poetId,
                text = p.text,
                meaning = p.meaning,
                meaningEnglish = p.meaningEnglish,
                audioFileName = p.audioFileName,
                difficultWords = p.difficultWords.map { d ->
                    DifficultWord(surface = d.surface, meaning = d.meaning)
                },
            )
        }
        return Catalog(poems = poemsDomain, poetsById = poetsById)
    }

    companion object {
        private const val ASSET_FILE = "poems.json"
    }
}
