package ni.edu.uca.android_sensors

import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import ni.edu.uca.android_sensors.databinding.FragmentGyroscopeBinding

/**
 * A simple [Fragment] subclass.
 * Use the [GyroscopeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GyroscopeFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentGyroscopeBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var gyroscopeSensor: Sensor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        sensorManager = context?.getSystemService(Service.SENSOR_SERVICE) as SensorManager
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentGyroscopeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onSensorChanged(event: SensorEvent) {
        val rotX = event.values[0]
        val rotY = event.values[1]
        val rotZ = event.values[2]

        println("RotX: $rotX")
        println("RotY: $rotY")
        println("RotZ: $rotZ")
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }
}