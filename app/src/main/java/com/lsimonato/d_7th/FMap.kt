package com.lsimonato.d_7th

import android.content.Context
import android.widget.Toast
import okhttp3.*

const val LANG = "language=pt-BR"
const val TMDB_API_KEY = "?api_key=6061bdd1ff3a878d27953844acdf3779"
const val TMDB_TOKEN_URL = "https://api.themoviedb.org/3/authentication/token/new$TMDB_API_KEY" // Request token
const val TMDB_CATEG_URL = "https://api.themoviedb.org/3/genre/movie/list$TMDB_API_KEY&$LANG" // Categorias
const val TMDB_LIST_URL = "https://api.themoviedb.org/3/discover/movie$TMDB_API_KEY&$LANG&with_genres=" // adicionar id ao final
const val TMDB_MAIN_URL = "https://api.themoviedb.org/3/movie/" // endereço base dos filmes + id do filme + key
const val TMDB_IMG_URL  = "https://image.tmdb.org/t/p/w500/" // endereço base das imagens + img_code.jpg/png

fun getRequest(getUrl: String): String {
    var client = OkHttpClient()
    var request = Request.Builder().url(getUrl).get().build()
    var xResp = client.newCall(request).execute()
    var cResp = xResp.body()?.string()!!
    return cResp
}

fun alert(msg: String, cnt: Context) {
    Toast.makeText(cnt, msg, Toast.LENGTH_SHORT).show()
}