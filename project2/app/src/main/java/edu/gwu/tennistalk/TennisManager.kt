package edu.gwu.tennistalk

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONArray
import org.json.JSONObject


class TennisManager {
    val okHttpClient: OkHttpClient

    init {
        val okHttpClientBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
        val loggingInterceptor: HttpLoggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        okHttpClientBuilder.addInterceptor(loggingInterceptor)

        okHttpClient = okHttpClientBuilder.build()
    }


    fun retrievePlayers(apiKey: String, tour: String): List<Player> {
        val players: MutableList<Player> = mutableListOf()

        val request: Request = Request.Builder()
            .url("https://tennis-live-data.p.rapidapi.com/rankings/$tour")
            .header("X-RapidAPI-Host", "tennis-live-data.p.rapidapi.com")
            .header("X-RapidAPI-Key", apiKey)
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            val json = JSONObject(responseBody)
            val results = json.getJSONObject("results")
            val playerArr: JSONArray = results.getJSONArray("rankings")

            for (i in 0 until playerArr.length()) {
                val curr: JSONObject = playerArr.getJSONObject(i)

                val player = Player(
                    name = curr.getString("full_name"),
                    ranking = curr.getInt("ranking"),
                    movement = curr.getString("movement"),
                    points = curr.getInt("ranking_points"),
                    country = curr.getString("country"),
                    id = curr.getInt("id"),
                )

                players.add(player)
            }
        }
        return players
    }
}