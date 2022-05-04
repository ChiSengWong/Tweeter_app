package com.codepath.apps.restclienttemplate

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.codepath.apps.restclienttemplate.models.Tweet
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import okhttp3.Headers
import java.util.*

class ComposeActivity : AppCompatActivity() {

    lateinit var etCompose: EditText
    lateinit var btnTweet: Button
    lateinit var wordCount: TextView
    lateinit var client: TwitterClient
    var wordcount: Int = 280

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_compose)

        etCompose = findViewById(R.id.etTweetCompose)
        btnTweet = findViewById(R.id.btnTweet)
        wordCount = findViewById(R.id.tvWordCount)
        client = TwitterApplication.getRestClient(this)

        etCompose.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun afterTextChanged(p0: Editable?) {
                var currentText = p0.toString()
                wordcount = 280 - currentText.length
                wordCount.text = "$wordcount/280"
                if (wordcount < 0) {
                    wordCount.setTextColor(Color.RED)
                    btnTweet.isEnabled = false
                }
                else{
                    wordCount.setTextColor(Color.BLACK)
                    btnTweet.isEnabled = true
                }

            }

        })


        btnTweet.setOnClickListener {
            // Grab the content of edittext(etCompose)
            val tweetContent = etCompose.text.toString()
            //make sure the tweet isnt empty
            if (tweetContent.isEmpty()) {
                Toast.makeText (this, "Empty tweets not allowed!", Toast.LENGTH_SHORT).show()
            }else
            //make an api call to Twitter to public tweet
            if (tweetContent.length > 280) {
                Toast.makeText(this, "Tweet is too long! Limit is 140 charcters", Toast.LENGTH_SHORT).show()
            }else{
                //make sure the tweet is under character count
                client.publishTweet(tweetContent, object: JsonHttpResponseHandler(){
                    override fun onFailure(
                        statusCode: Int,
                        headers: Headers?,
                        response: String?,
                        throwable: Throwable?
                    ) {
                        Log.e(TAG, "Failed to public tweet", throwable)
                    }

                    override fun onSuccess(statusCode: Int, headers: Headers, json: JSON) {
                        Log.i(TAG, "Successfully published tweet!",)
                        val tweet = Tweet.fromJson(json.jsonObject)

                        val intent = Intent()
                        intent.putExtra("tweet",tweet)
                        setResult(RESULT_OK, intent)
                        finish()
                    }

                })
            }

        }
    }
    companion object {
        val TAG = "CompoesActivity"
    }
}