package com.tarun.ai_chatbot

import android.graphics.Bitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {

    private val _chatState = MutableStateFlow(ChatState())
    val chilstate = _chatState.asStateFlow()

    fun onEvent(event: ChatUiEvent) {
        when (event) {
            is ChatUiEvent.Sendprompt -> {
                if (_chatState.value.prompt.isNotEmpty()) {
                     addPrompt(event.prompt, event.bitmap)

                    if (event.bitmap != null) {
                        getResponseWithImage(event.prompt, event.bitmap)
                    } else {
                        getResponse(event.prompt)
                    }

                    _chatState.update { it.copy(bitmap = null) }
                }
            }

            is ChatUiEvent.Updateprompt -> {
                _chatState.update {
                    it.copy(prompt = event.newPrompt)
                }
            }
        }

    }

    fun addPrompt(prompt: String, bitmap: Bitmap?) {
        _chatState.update {
            it.copy(
                chatlist = it.chatlist.toMutableList().apply {
                    add(0, Chat(prompt, bitmap, true))
                }, prompt = "", bitmap = null
            )
        }
    }

    private fun getResponse(prompt: String) {
        viewModelScope.launch {
            val chat = chatdata.userprompt(prompt)
            _chatState.update {
                it.copy(chatlist = it.chatlist.toMutableList().apply {
                    add(0, chat)
                })
            }

        }
    }

    private fun getResponseWithImage(prompt: String, bitmap: Bitmap) {
        viewModelScope.launch {
            val chat = chatdata.userprompt(prompt, bitmap)
            _chatState.update {
                it.copy(chatlist = it.chatlist.toMutableList().apply {
                    add(0, chat)
                }, prompt = "", bitmap = null)
            }

        }
    }

}