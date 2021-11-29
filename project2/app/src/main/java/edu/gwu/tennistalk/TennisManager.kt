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

    fun retrieveMatches(apiKey: String, date: String): List<Match> {
        val allMatches: MutableList<Match> = mutableListOf()

        val request: Request = Request.Builder()
            .url("https://tennis-live-data.p.rapidapi.com/matches-by-date/$date")
            .header("X-RapidAPI-Host", "tennis-live-data.p.rapidapi.com")
            .header("X-RapidAPI-Key", apiKey)
            .build()

        val response: Response = okHttpClient.newCall(request).execute()
        val responseBody: String? = response.body?.string()

        if (response.isSuccessful && !responseBody.isNullOrBlank()) {
            val json = JSONObject(responseBody)
            val results: JSONArray = json.getJSONArray("results")

            for (i in 0 until results.length()) {
                // get tournament info
                val curr: JSONObject = results.getJSONObject(i)
                val currTour = curr.getJSONObject("tournament")
                val tournament = currTour.getString("name")
                val country = currTour.getString("country")
                val city = currTour.getString("city")
                val tour = currTour.getString("code")
                val surface = currTour.getString("surface")
                val startDate = currTour.getString("start_date")
                val endDate = currTour.getString("end_date")

                val matches: JSONArray = curr.getJSONArray("matches")
                for (j in 0 until matches.length()) {
                    // get match info

                    val currMatch = matches.getJSONObject(j)
                    val title = currMatch.getString("title")
                    val status = currMatch.getString("status")
                    val home_player = currMatch.getString("home_player")
                    val home_id = currMatch.getInt("home_id")
                    val away_player = currMatch.getString("away_player")
                    val away_id = currMatch.getInt("away_id")
                    val date = currMatch.getString("date")
                    val round_id = currMatch.getInt("round_id")
                    val round_name = currMatch.getString("round_name")
                    val id = currMatch.getInt("id")

                    //val home = currMatch.getJSONObject("home")
                    //val away = currMatch.getJSONObject("away")
                    val result = currMatch.getJSONObject("result")

                    val winner_id = result.getInt("winner_id")
                    var home_sets = ""
                    var home_set1 = ""
                    var home_set2 = ""
                    var home_set3 = ""
                    var home_set4 = ""
                    var home_set5 = ""
                    var away_sets = ""
                    var away_set1 = ""
                    var away_set2 = ""
                    var away_set3 = ""
                    var away_set4 = ""
                    var away_set5 = ""

                    if (result.has("home_sets"))
                        home_sets = result.getString("home_sets")
                    if (result.has("home_set1"))
                        home_set1 = result.getString("home_set1")
                    if (result.has("home_set2"))
                        home_set2 = result.getString("home_set2")
                    if (result.has("home_set3"))
                        home_set3 = result.getString("home_set3")
                    if (result.has("home_set4"))
                        home_set4 = result.getString("home_set4")
                    if (result.has("home_set5"))
                        home_set5 = result.getString("home_set5")
                    if (result.has("away_sets"))
                        away_sets = result.getString("away_sets")
                    if (result.has("away_set1"))
                        away_set1 = result.getString("away_set1")
                    if (result.has("away_set2"))
                        away_set2 = result.getString("away_set2")
                    if (result.has("away_set3"))
                        away_set3 = result.getString("away_set3")
                    if (result.has("away_set4"))
                        away_set4 = result.getString("away_set4")
                    if (result.has("away_set5"))
                        away_set5 = result.getString("away_set5")


                    // create match object for each match in tournament
                    val match = Match(
                        tournament = tournament,
                        country = country,
                        city = city,
                        tour = tour,
                        surface = surface,
                        startDate = startDate,
                        endDate = endDate,

                        title = title,
                        status = status,
                        home_player = home_player,
                        home_id = home_id,
                        away_player = away_player,
                        away_id = away_id,
                        date = date,
                        round_id = round_id,
                        round_name = round_name,
                        id = id,
                        winner_id = winner_id,
                        home_sets = home_sets,
                        away_sets = away_sets,
                        home_set1 = home_set1,
                        home_set2 = home_set2,
                        home_set3 = home_set3,
                        home_set4 = home_set4,
                        home_set5 = home_set5,
                        away_set1 = away_set1,
                        away_set2 = away_set2,
                        away_set3 = away_set3,
                        away_set4 = away_set4,
                        away_set5 = away_set5
                    )
                    allMatches.add(match)
                }
            }
        }
        return allMatches
    }
}