package com.lsimonato.d_7th

import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import org.json.JSONObject
import java.io.IOException
import kotlin.concurrent.thread

class MovieDetail : AppCompatActivity() {

    private lateinit var imgBackdrop: ImageView
    private lateinit var btnCurtiuA: ImageView
    private lateinit var btnCurtiuB: ImageView

    private lateinit var txtTitulo: TextView
    private lateinit var txtLikes: TextView
    private lateinit var txtViews: TextView

    private lateinit var laySimilares: LinearLayout

    var bLike = false

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

        laySimilares = findViewById(R.id.laySimilares)

        buscaFilme(cIdFilme)
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

            if (!jsonObject.isNull("poster_path")) {
                var cPosterPath = TMDB_IMG_URL + jsonObject.getString("poster_path")
                var bmpBackdrop = BitmapDrawable(Resources.getSystem(), getImage(cPosterPath))

                runOnUiThread {
                    imgBackdrop.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                    imgBackdrop.background = bmpBackdrop
                    txtTitulo.text = cTitle
                    txtLikes.text = "$cVoteCount likes"
                    txtViews.text = "$cPopularity views"
                }
            } else {
                runOnUiThread {
                    imgBackdrop.layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 300)
                    imgBackdrop.setBackgroundColor(Color.DKGRAY)
                    txtTitulo.text = cTitle
                    txtLikes.text = "$cVoteCount likes"
                    txtViews.text = "$cPopularity views"
                }
            }
        }

        createSimilar(applicationContext, laySimilares, idFilme)
    }

    fun like(view: View) {
        if (!bLike) {
            btnCurtiuA.setBackgroundResource(R.drawable.likeb)
            btnCurtiuB.setBackgroundResource(R.drawable.likeb)
        } else {
            btnCurtiuA.setBackgroundResource(R.drawable.likea)
            btnCurtiuB.setBackgroundResource(R.drawable.likea)
        }

        bLike = !bLike
    }

    fun createSimilar(context: Context, superior: LinearLayout, id: String?) {
        thread {
            var relacionados = try {
                getRequest("$TMDB_MAIN_URL$id/similar$TMDB_API_KEY&$LANG&page=1")
            } catch (e: IOException) {
                e.message.toString()
            } catch (e: InterruptedException) {
                e.message.toString()
            }

            var jsonObject = JSONObject(relacionados)
            var jsonArray = jsonObject.getJSONArray("results")

            if (jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    var jobject = jsonArray.getJSONObject(i)

                    var xIdFilme = jobject.getString("id")
                    var cNomeFilme = jobject.getString("title")

                    // Cria constraint layout como contenedor dos dados do filme relacionado n
                    var layParams = LayoutParams(LayoutParams.MATCH_PARENT, 200)
                    layParams.setMargins(0,0,0,8)

                    var layout = ConstraintLayout(context)
                    layout.layoutParams = layParams
                    layout.setBackgroundColor(ContextCompat.getColor(context, R.color.CINZA1))
                    layout.setPadding(8,8,8,8)
                    layout.setOnClickListener{
//                        alert("Filme $xIdFilme",context)
                        laySimilares.removeAllViews()
                        buscaFilme(xIdFilme)
                    }

                    runOnUiThread {
                        superior.addView(layout)
                    }

                    // Cria image view com poster do filme n
                    var imgParams = LayoutParams(150, LayoutParams.WRAP_CONTENT)
                    imgParams.leftToLeft = LayoutParams.PARENT_ID
                    imgParams.topToTop = LayoutParams.PARENT_ID
                    imgParams.bottomToBottom = LayoutParams.PARENT_ID
                    imgParams.setMargins(0,0,0,0)

                    var image = ImageView(context)
                    image.setPadding(0,0,0,0)

                    if (!jobject.isNull("poster_path")) {
                        var cPosterPath = TMDB_IMG_URL + jobject.getString("poster_path").replace("/","")
                        var bmp = getImage(cPosterPath)
                        var bmpBack = BitmapDrawable(Resources.getSystem(), bmp)
                        image.background = bmpBack
                    } else {
                        imgParams = LayoutParams(150, LayoutParams.MATCH_PARENT)
                        image.setBackgroundColor(Color.DKGRAY)
                    }

                    image.layoutParams = imgParams

                    runOnUiThread {
                        layout.addView(image)
                    }

                    // Cria text view com titulo do filme
                    var txtParams = LayoutParams(LayoutParams.MATCH_CONSTRAINT, LayoutParams.WRAP_CONTENT)
                    txtParams.leftToLeft = LayoutParams.PARENT_ID
                    txtParams.topToTop = LayoutParams.PARENT_ID
                    txtParams.rightToRight = LayoutParams.PARENT_ID
                    txtParams.setMargins(160,16,24,0)

                    var text = TextView(context)
                    text.setPadding(0,0,0,0)
                    text.layoutParams = txtParams
                    text.setTextColor(Color.WHITE)
                    text.textSize = 16F
                    text.textAlignment = View.TEXT_ALIGNMENT_TEXT_START
                    text.text = cNomeFilme

                    runOnUiThread {
                        layout.addView(text)
                    }
                }
            }
        }
    }

}