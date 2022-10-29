package ni.edu.uca.android_sensors

import android.app.Service
import android.content.Context
import android.hardware.*
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import ni.edu.uca.android_sensors.databinding.FragmentLightSensorBinding
import ni.edu.uca.android_sensors.databinding.FragmentMenuBinding

class LightSensorFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentLightSensorBinding
    private lateinit var sensorManager: SensorManager
    private var lightSensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        sensorManager = context?.getSystemService(Service.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        sensorManager.registerListener(this, lightSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onSensorChanged(event: SensorEvent) {
        val lux = event.values[0]
        binding.tvLux.text = "${lux} lx"
        if (lux < 25) {
            binding.tvText.text = "Es de noche"
            binding.tvText.setTextColor(resources.getColor(R.color.white))
            binding.lyLightSensor.setBackgroundColor(resources.getColor(R.color.black))
            binding.tvLux.setTextColor(resources.getColor(R.color.white))
        }
        else {
            binding.tvText.text = "Es de día"
            binding.tvText.setTextColor(resources.getColor(R.color.black))
            binding.lyLightSensor.setBackgroundColor(resources.getColor(R.color.white))
            binding.tvLux.setTextColor(resources.getColor(R.color.black))
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)

    }

    override fun onCreateView(
        layoutInflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLightSensorBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

}