package com.lsimonato.d_7th

import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import kotlin.concurrent.thread

class MovieDetail : AppCompatActivity() {

    private lateinit var imgBackdrop: AppCompatImageView
    private lateinit var btnCurtiuA: ImageView
    private lateinit var btnCurtiuB: ImageView

    private lateinit var txtTitulo: TextView
    private lateinit var txtLikes: TextView
    private lateinit var txtViews: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_detail)

        val cIdFilme: String?  = intent.getStringExtra("idFilme")

        imgBackdrop = findViewById(R.id.imgBackdrop)
        btnCurtiuA = findViewById(R.id.btnCurtiuA)
        btnCurtiuB = findViewById(R.id.btnCurtiuB)

        txtTitulo = findViewById(R.id.txtTitulo)
        txtLikes = findViewById(R.id.txtLikes)
        txtViews = findViewById(R.id.txtViews)

//        buscaFilme(cIdFilme)
    }

    private fun buscaFilme(idFilme: String?) {
        thread {
            var filme = try {
                getRequest("$TMDB_MAIN_URL$idFilme$TMDB_API_KEY&$LANG")
            } catch (e: IOException) {
                e.message.toString()
            } catch (e: InterruptedException) {
                e.message.toString()
            }

            var jsonObject = JSONObject(filme)

            var cTitle = jsonObject.getString("title")
            var cVoteCount = jsonObject.getString("vote_count")
            var cPopularity = jsonObject.getString("popularity")
            var cPosterPath = TMDB_IMG_URL + jsonObject.getString("poster_path")

            runOnUiThread {
                var url = URL(cPosterPath)
                var bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                var bmpBackdrop = BitmapDrawable(Resources.getSystem(), bmp)

                imgBackdrop.background = bmpBackdrop
                txtTitulo.text = cTitle
                txtLikes.text = cVoteCount
                txtViews.text = cPopularity
            }
        }
    }

    fun like(view: View) {
        if (btnCurtiuA.background.equals(R.drawable.likea)) {
            btnCurtiuA.setBackgroundResource(R.drawable.likeb)
            btnCurtiuB.setBackgroundResource(R.drawable.likeb)
        } else {
            btnCurtiuA.setBackgroundResource(R.drawable.likea)
            btnCurtiuB.setBackgroundResource(R.drawable.likea)
        }
    }

}