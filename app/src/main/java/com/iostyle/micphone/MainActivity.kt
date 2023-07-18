package com.iostyle.micphone

import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.hjq.permissions.XXPermissions

class MainActivity : AppCompatActivity(), SoundMeterListener {

    private var audioView: AudioVisualizerView? = null
    private var threshold = 500

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        audioView = findViewById(R.id.audioView)

        findViewById<Button>(R.id.start)?.setOnClickListener {
            XXPermissions.getDenied(this, listOf("android.permission.RECORD_AUDIO")).let {
                if (it.isEmpty()) {
                    startRecord()
                } else {
                    XXPermissions.startPermissionActivity(this, it)
                }
            }
        }

        findViewById<Button>(R.id.stop)?.setOnClickListener {
            soundMeter?.stop()
        }

        val thresholdTv = findViewById<TextView>(R.id.thresholdTv).also {
            it.text = threshold.toString()
        }

        findViewById<Button>(R.id.sub)?.setOnClickListener {
            threshold -= 100
            thresholdTv.text = threshold.toString()
        }

        findViewById<Button>(R.id.add)?.setOnClickListener {
            threshold += 100
            thresholdTv.text = threshold.toString()
        }
    }

    private var soundMeter: SoundMeter? = null

    private fun startRecord() {
        Toast.makeText(this, "启动！", Toast.LENGTH_SHORT).show()

        soundMeter = SoundMeter(this)
        soundMeter?.start()
    }

    override fun onAmplitudeChanged(amplitude: Double) {
        Log.e("audio", "$amplitude")

        if (amplitude > threshold) {
            // 手机振动一下
            vibrate(this@MainActivity, 2000)
        }

        audioView?.addDataPoint(amplitude.toFloat() / threshold)
    }

    private fun vibrate(context: Context, milliseconds: Long) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        // 检查设备的 API 版本是否支持新的震动效果
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 创建一个震动效果，指定震动的时间
            val vibrationEffect =
                VibrationEffect.createOneShot(milliseconds, 255)
            vibrator.vibrate(vibrationEffect)
        } else {
            // 旧的 API 版本只支持固定的震动时间
            vibrator.vibrate(milliseconds)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundMeter?.stop()
        soundMeter = null
    }

}