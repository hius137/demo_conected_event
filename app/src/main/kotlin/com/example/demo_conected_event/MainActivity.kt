package com.example.demo_conected_event

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.plugin.common.EventChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import kotlin.random.Random

class MainActivity : FlutterActivity() {

    private lateinit var eventConnectChannel: EventChannel
    private lateinit var connectivity: Connectivity
    private var eventSink: EventChannel.EventSink? = null

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_CONNECT") {
                val result = intent.getStringExtra("result")
                eventSink?.success(result)
            }
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val filter = IntentFilter("ACTION_CONNECT")
        registerReceiver(receiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val METHOD_RAMDOM_CHANNEL = "native-channel"
        private const val EVENT_CONNECT_CHANNEL = "connect-channel"
        private const val METHOD_CONNECT_CHANNEL = "connect-channel"
    }

    override fun configureFlutterEngine(flutterEngine: FlutterEngine) {
        eventConnectChannel = EventChannel(
            flutterEngine.dartExecutor.binaryMessenger, EVENT_CONNECT_CHANNEL
        )

        eventConnectChannel.setStreamHandler(
            object : EventChannel.StreamHandler {
                override fun onListen(arguments: Any?, events: EventChannel.EventSink) {
                    eventSink = events
                }

                override fun onCancel(arguments: Any?) {
                    eventSink = null
                }
            },
        )

        val methodRandom = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, METHOD_RAMDOM_CHANNEL
        )
        methodRandom.setMethodCallHandler { call, result ->
            handleAction(call, result)
        }

        val methodConnect = MethodChannel(
            flutterEngine.dartExecutor.binaryMessenger, METHOD_CONNECT_CHANNEL
        )
        methodConnect.setMethodCallHandler { call, result ->
            handleConnectStatus(call, result)
        }
    }

    private fun handleAction(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "action" -> {
                val num = call.argument<Int>("num")
                val res = getRandomNum(num!!)
                result.success(res)
            }

            else -> result.notImplemented()
        }
    }

    private fun handleConnectStatus(call: MethodCall, result: MethodChannel.Result) {
        if ("check-connect" == call.method) {
            result.success(connectivity.networkType)
        } else {
            result.notImplemented()
        }
    }

    private fun getRandomNum(num: Int): Int {
        return Random.nextInt(num)
    }
}