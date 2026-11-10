package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.charset.Charset

// TODO (1: Fix any bugs)
// TODO (2: Add function saveComic(...) to save comic info when downloaded
// TODO (3: Automatically load previously saved comic when app starts)

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    private val internalFilename = "saved_comic.json"
    private lateinit var file: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)
        file = File(filesDir, internalFilename)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            val comicId = numberEditText.text.toString().trim()
            downloadComic(comicId)
        }

        val savedComic = loadSavedComic()
        if (savedComic != null) {
            showComic(savedComic)
        }

    }

    // Fetches comic from web as JSONObject
    private fun downloadComic (comicId: String) {
        val url = if (comicId.isEmpty()) {
            "https://xkcd.com/info.0.json"
        } else {
            "https://xkcd.com/$comicId/info.0.json"
        }
        requestQueue.add(
            JsonObjectRequest(url,
                { response ->
                    showComic(response)
                    saveComic(response)
                },
                { error ->
                    Toast.makeText(this, "Failed to load comic: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        )
    }

    // Display a comic for a given comic JSON object
    private fun showComic (comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }

    // Implement this function
    private fun saveComic(comicObject: JSONObject) {
        try {
            val outputStream = FileOutputStream(file)
            outputStream.write(comicObject.toString().toByteArray(Charset.defaultCharset()))
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun loadSavedComic(): JSONObject? {
        if (file.exists()) {
            try {
                val inputStream = FileInputStream(file)
                val size = inputStream.available()
                val buffer = ByteArray(size)
                inputStream.read(buffer)
                inputStream.close()

                val jsonString = String(buffer, Charset.defaultCharset())
                return JSONObject(jsonString)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return null
    }
}