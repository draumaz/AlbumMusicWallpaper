package com.lucasvinicius.musicwallpaper.data.repository

import com.lucasvinicius.musicwallpaper.data.model.LookupResult
import com.lucasvinicius.musicwallpaper.data.model.TrackInfo
import com.lucasvinicius.musicwallpaper.data.remote.ItunesApi
import java.util.Collections

class ArtworkRepositoryImpl(
    private val itunesApi: ItunesApi
) : ArtworkRepository {

    private val cache = Collections.synchronizedMap(mutableMapOf<String, LookupResult>())

    private fun cleanText(text: String): String {
        return text.replace("🅴", "")
            .replace(Regex("(?i)\\s*\\(Explicit\\)"), "")
            .replace(Regex("(?i)\\s*\\[Explicit]"), "")
            .replace(Regex("(?i)\\s*-\\s*EP$"), "")
            .replace(Regex("(?i)\\s*-\\s*Single$"), "")
            .replace(Regex("(?i)\\s*\\(feat\\..*?\\)"), "")
            .trim()
    }

    override suspend fun resolveArtwork(trackInfo: TrackInfo): LookupResult {
        val cleanArtist = cleanText(trackInfo.artist)
        val cleanTitle = cleanText(trackInfo.title)
        val cacheKey = "$cleanArtist-$cleanTitle"

        cache[cacheKey]?.let { return it }

        if (cleanArtist.isBlank()) {
            return LookupResult.Error("Metadados insuficientes: artista obrigatório.")
        }

        return try {
            val term = "$cleanArtist $cleanTitle"
            val itunesResponse = itunesApi.searchTrack(term = term)

            if (itunesResponse.isSuccessful) {
                val result = itunesResponse.body()?.results?.firstOrNull()
                val thumbnailUrl = result?.artworkUrl100

                if (thumbnailUrl != null) {
                    val highResUrl = thumbnailUrl.replace("100x100bb", "1000x1000bb")
                    val lookupResult = LookupResult.StaticHighRes(highResUrl)
                    cache[cacheKey] = lookupResult
                    lookupResult
                } else {
                    cache[cacheKey] = LookupResult.NotFound
                    LookupResult.NotFound
                }
            } else {
                val lookupResult = LookupResult.NotFound
                cache[cacheKey] = lookupResult
                lookupResult
            }
        } catch (e: Exception) {
            LookupResult.Error(e.message ?: "Erro no iTunes")
        }
    }
}
