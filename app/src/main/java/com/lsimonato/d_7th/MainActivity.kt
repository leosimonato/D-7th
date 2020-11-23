package com.lsimonato.d_7th

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.skydoves.powerspinner.IconSpinnerAdapter
import com.skydoves.powerspinner.IconSpinnerItem
import com.skydoves.powerspinner.PowerSpinnerView
import okhttp3.Call
import okhttp3.Response
import org.json.JSONObject
import java.io.IOException
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity() {

    var token = ""
    var generos = ""
    var filmes = ""

    private lateinit var spnCategoria: PowerSpinnerView
    private lateinit var spnFilmes: PowerSpinnerView

    var xIdGenero = arrayListOf<String>()
    var xNomeGenero = arrayListOf<IconSpinnerItem>()
    var xIdFilme = arrayListOf<String>()
    var xNomeFilme = arrayListOf<IconSpinnerItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        spnCategoria = findViewById(R.id.spnCategoria)
        spnFilmes = findViewById(R.id.spnFilmes)

        solicitaToken()
        buscaGeneros()

        spnCategoria.setOnClickListener {
            spnCategoria.showOrDismiss()
        }

        spnCategoria.setOnSpinnerDismissListener {
            alert("Categoria ${spnCategoria.text} selecionada!", applicationContext)
            spnCategoria.showOrDismiss()

            var cIdGenero = xIdGenero[spnCategoria.selectedIndex]
            buscaFilmes(cIdGenero)
        }

        spnFilmes.setOnClickListener {
            spnFilmes.showOrDismiss()
        }

        spnFilmes.setOnSpinnerDismissListener {
            alert("Filme ${spnFilmes.text} selecionado!", applicationContext)
            spnFilmes.showOrDismiss()
            var cIdFilme = xIdFilme[spnFilmes.selectedIndex]

            val intent = Intent(this, MovieDetail::class.java)
            intent.putExtra("idFilme", cIdFilme)
            startActivity(intent)
        }
    }

    fun solicitaToken() {
        thread {
            token = try {
                getRequest(TMDB_TOKEN_URL)
            } catch (e: IOException) {
                e.message.toString()
            } catch (e: InterruptedException) {
                e.message.toString()
            }
        }
    }

    fun buscaGeneros() {
        thread {
            generos = try {
                getRequest(TMDB_CATEG_URL)
            } catch (e: IOException) {
                e.message.toString()
            } catch (e: InterruptedException) {
                e.message.toString()
            }

            var jsonObject = JSONObject(generos)
            var jsonArray = jsonObject.getJSONArray("genres")

            if (jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    var jobject = jsonArray.getJSONObject(i)

                    xIdGenero.add(jobject.getString("id"))
                    xNomeGenero.add(IconSpinnerItem(getDrawable(R.drawable.spn_icon),jobject.getString("name")))
                }
            }

            runOnUiThread {
                spnCategoria.apply {
                    setSpinnerAdapter(IconSpinnerAdapter(this))
                    setItems(xNomeGenero)
                    getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)
                    selectItemByIndex(0) // select an item initially.
                    lifecycleOwner = this@MainActivity
                }
            }
        }
    }

    fun buscaFilmes(id: String) {
        thread {
            filmes = try {
                getRequest(TMDB_LIST_URL + id)
            } catch (e: IOException) {
                e.message.toString()
            } catch (e: InterruptedException) {
                e.message.toString()
            }

            var jsonObject = JSONObject(filmes)
            var jsonArray = jsonObject.getJSONArray("results")

            xIdFilme.clear()
            xNomeFilme.clear()

            if (jsonArray.length() > 0) {
                for (i in 0 until jsonArray.length()) {
                    var jobject = jsonArray.getJSONObject(i)

                    xIdFilme.add(jobject.getString("id"))
                    xNomeFilme.add(IconSpinnerItem(getDrawable(R.drawable.spn_icon),jobject.getString("title")))
                }
            }

            runOnUiThread {
                spnFilmes.apply {
                    setSpinnerAdapter(IconSpinnerAdapter(this))
                    setItems(xNomeFilme)
                    getSpinnerRecyclerView().layoutManager = GridLayoutManager(context, 1)
                    selectItemByIndex(0) // select an item initially.
                    lifecycleOwner = this@MainActivity
                }
            }
        }
    }

}