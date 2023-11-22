package com.deepshikhayadav.moengage_test

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.Collections


class MainActivity : AppCompatActivity(), NewsListAdapter.OnClickListener {
    companion object {
        const val url =
            "https://candidate-test-data-moengage.s3.amazonaws.com/Android/news-api-feed/staticResponse.json"
        const val TAG = "log"
    }

    lateinit var apiResponse: NewsResponse
    lateinit var mContext: Context
    var rvList: RecyclerView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mContext = this
        rvList = findViewById(R.id.rvNews)
        // get fcm token
        getFCMToken()

        // Request Api call
        makeGetApiRequest()
    }

    private fun getFCMToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = getString(R.string.msg_token_fmt, token)
            Log.d(TAG, msg)

        })
    }

    private fun makeGetApiRequest() {

        CoroutineScope(Dispatchers.IO).launch {
            var httpURLConnection: HttpURLConnection? = null
            try {

                val url = URL(url)

                httpURLConnection =
                    withContext(Dispatchers.IO) {
                        url.openConnection()
                    } as HttpURLConnection

                val code = httpURLConnection.responseCode

                if (code != 200) {
                    throw IOException("The error from the server is $code")
                }

                val bufferedReader = BufferedReader(
                    InputStreamReader(httpURLConnection.inputStream)
                )

                val jsonStringHolder: StringBuilder = StringBuilder()

                while (true) {
                    val readLine =
                        withContext(Dispatchers.IO) {
                            bufferedReader.readLine()
                        } ?: break
                    jsonStringHolder.append(readLine)
                }

                apiResponse =
                    Gson().fromJson(jsonStringHolder.toString(), NewsResponse::class.java)

                withContext(Dispatchers.Main) {
                    // hide progress bar on api load
                    findViewById<ProgressBar>(R.id.progress).visibility = View.GONE

                    dataSet()
                    findViewById<ImageView>(R.id.img_up).setOnClickListener {
                        // sort list in asc order
                        Collections.sort(
                            apiResponse.articles
                        ) { o1, o2 ->
                            o1.publishedAt
                                .compareTo(o2.publishedAt)
                        }
                        dataSet()
                    }
                    findViewById<ImageView>(R.id.img_down).setOnClickListener {
                        // sort list in desc order
                        Collections.sort(
                            apiResponse.articles
                        ) { o1, o2 ->
                            o2.publishedAt
                                .compareTo(o1.publishedAt)
                        }
                        dataSet()
                    }

                }
            } catch (ioexception: IOException) {
                Log.e(this.javaClass.name, ioexception.message.toString())
            } finally {
                httpURLConnection?.disconnect()
            }
        }

    }

    // sets list data
    private fun dataSet() {
        // Call adapter
        val adapter = NewsListAdapter(mContext, apiResponse.articles, this@MainActivity)
        rvList!!.setHasFixedSize(false)
        rvList!!.layoutManager = LinearLayoutManager(mContext)
        rvList!!.adapter = adapter

    }

    // open link in browser
    override fun onClick(position: Int) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(apiResponse.articles[position].url)
            )
        )
    }


}