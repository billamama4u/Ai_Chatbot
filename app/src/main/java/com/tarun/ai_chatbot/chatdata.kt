package com.tarun.ai_chatbot

import android.graphics.Bitmap
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.ResponseStoppedException
import com.google.ai.client.generativeai.type.content
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object chatdata {

    val apiKey="AIzaSyDXXCCMS13GshTGWgchBru-8WSa_a0CEYE"

    suspend fun userprompt(prompt:String):Chat{
        val generativeModel= GenerativeModel(
            modelName = "gemini-pro",
            apiKey= apiKey
            )

        try {
            val response = withContext(Dispatchers.IO){
                generativeModel.generateContent(prompt)
            }
            return Chat(
                prompt = response.text.toString(),
                bitmap = null,
                isfromuser = false
            )
        }catch (e: Exception){
            return Chat(
                prompt = e.localizedMessage?:"Error",
                bitmap = null,
                isfromuser = false
            )
        }
    }

    suspend fun userprompt(prompt:String,bitmap: Bitmap):Chat{
        val generativeModel= GenerativeModel(
            modelName = "gemini-pro-vision",
            apiKey= apiKey
        )

        val inputcontent = content {
            image(bitmap)
            text(prompt)
        }

        try {
            val response = withContext(Dispatchers.IO){
                generativeModel.generateContent(inputcontent)
            }
            return Chat(
                prompt = response.text.toString(),
                bitmap = null,
                isfromuser = false
            )
        }catch (e: Exception){
            return Chat(
                prompt = e.localizedMessage?:"Error",
                bitmap = null,
                isfromuser = false
            )
        }
    }



}