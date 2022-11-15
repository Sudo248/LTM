package com.sudo248.ltm.websocket

import android.util.Log
import com.sudo248.ltm.api.model.Request
import com.sudo248.ltm.api.model.Response
import com.sudo248.ltm.common.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import org.sudo248.client.WebSocketClient
import org.sudo248.handshake.server.ServerHandshake
import org.sudo248.mqtt.model.MqttMessage
import java.net.URI
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashMap


/**
 * **Created by**
 *
 * @author *Sudo248*
 * @since 00:38 - 23/10/2022
 */
@Singleton
class WebSocketService @Inject constructor() : WebSocketClient(URI("ws://${Constant.WS_HOST}:${Constant.WS_PORT}")) {

    companion object {
        private val TAG = WebSocketService::class.java.simpleName
    }

    private val webSocketScope = CoroutineScope(Dispatchers.Main.immediate + SupervisorJob())

    private val _messageFlow: MutableSharedFlow<MqttMessage> = MutableSharedFlow()
    val messageFlow: SharedFlow<MqttMessage> = _messageFlow

    private val _responseFlow: MutableSharedFlow<Response<*>> = MutableSharedFlow()
    val responseFlow: SharedFlow<Response<*>> = _responseFlow

    private val response: HashMap<Long, ResponseListener>

    private var startRequest: Queue<Any>?

    init {
        startRequest = LinkedList()
        response = LinkedHashMap()
    }

    fun send(request: Any, onResponse: ResponseListener) {
        if (request is Request<*>) {
            response[request.id] = onResponse
            send(request)
        } else {
            send(request)
        }
    }

    override fun send(data: Any?) {
        if (!isOpen) {
            startRequest?.add(data)
        } else {
            super.send(data)
        }
    }

    override fun send(text: String?) {
        if (!isOpen) {
            startRequest?.add(text)
        } else {
            super.send(text)
        }
    }

    override fun send(data: ByteArray) {
        if (!isOpen) {
            startRequest?.add(data)
        } else {
            super.send(data)
        }
    }

    override fun publish(topic: String?, message: Any?) {
        Log.d(TAG, "publish: $topic, message $message")
        if (!isOpen) {
            startRequest?.add(message)
        } else {
            super.publish(topic, message)
        }
    }

    override fun onMqttPublish(message: MqttMessage?) {
        webSocketScope.launch {
            if (message != null) {
                _messageFlow.emit(message)
            } else {
                Log.e(TAG, "onMqttPublish: $message" )
            }
            Log.d(TAG, "onMqttPublish: ${message.toString()}")
        }
    }

//    override fun onMqttSubscribe(message: MqttMessage?) {
//        webSocketScope.launch {
//            if (message != null) {
//                _messageFlow.emit(message)
//            } else {
//                Log.e(TAG, "onMqttSubscribe: $message" )
//            }
//            Log.d(TAG, "onMqttSubscribe: ${message.toString()}")
//        }
//    }

    override fun onOpen(handshake: ServerHandshake?) {
        Log.d("sudoo", "onOpen")
        while (!startRequest!!.isEmpty()) {
            val request = startRequest?.poll()
            if (request is MqttMessage) {
                publish(request.topic, request)
            }
            if (request is ByteArray) {
                super.send(request)
            }
            if (request is String) {
                super.send(request)
            } else {
                super.send(request)
            }
        }
        startRequest = null
    }

    override fun onMessage(message: String?) {

    }

    override fun onMessage(data: Any?) {
        webSocketScope.launch {
            Log.d("sudoo", "onMessage: $data")
            if (data != null && data is Response<*>) {
                /*val res = response.remove(data.requestId)
                res?.onResponse(data)*/
                _responseFlow.emit(data)
            } else {
                Log.e(TAG, "onMessage: $data" )
            }
        }
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        Log.d("sudoo", "onClose: $code, $reason,  $remote")
    }

    override fun onError(ex: Exception?) {
        Log.d("sudoo", "onError: $ex")
    }
}