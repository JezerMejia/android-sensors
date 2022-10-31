package ni.edu.uca.android_sensors

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Service
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.Fragment
import ni.edu.uca.android_sensors.databinding.FragmentGyroscopeBinding
import kotlin.math.PI

/**
 * A simple [Fragment] subclass.
 * Use the [GyroscopeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GyroscopeFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentGyroscopeBinding
    private lateinit var sensorManager: SensorManager
    private lateinit var gyroscopeSensor: Sensor
    private var boxIsMoving = false

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

    val xRestoreAnimator = ValueAnimator.ofFloat(0f, 0f).apply {
        interpolator = LinearInterpolator()
        duration = 1000; startDelay = 1000
        addUpdateListener {
            if (boxIsMoving) it.cancel()
            val value = it.animatedValue as Float
            binding.vwBox.translationX = value
        }
    }
    val yRestoreAnimator = ValueAnimator.ofFloat(0f, 0f).apply {
        interpolator = LinearInterpolator()
        duration = 1000; startDelay = 1000
        addUpdateListener {
            if (boxIsMoving) it.cancel()
            val value = it.animatedValue as Float
            binding.vwBox.translationY = value
        }
    }
    val zRestoreAnimator = ValueAnimator.ofFloat(0f, 0f).apply {
        interpolator = LinearInterpolator()
        duration = 1000; startDelay = 1000
        addUpdateListener {
            if (boxIsMoving) it.cancel()
            val value = it.animatedValue as Float
            binding.vwBox.translationZ = value
        }
    }

    enum class MOVEMENT_DIRECTION {
        X,
        Y,
        Z,
    }

    private fun getBoxPosition(source: MOVEMENT_DIRECTION): Float {
        val box = binding.vwBox
        val pos = when (source) {
            MOVEMENT_DIRECTION.X -> box.translationX
            MOVEMENT_DIRECTION.Y -> box.translationY
            MOVEMENT_DIRECTION.Z -> box.translationZ
        }
        return pos
    }

    private fun setBoxPosition(source: MOVEMENT_DIRECTION, value: Float) {
        val box = binding.vwBox
        when (source) {
            MOVEMENT_DIRECTION.X -> box.translationX = value
            MOVEMENT_DIRECTION.Y -> box.translationY = value
            MOVEMENT_DIRECTION.Z -> box.translationZ = value
        }
    }

    private fun animateTo(direction: MOVEMENT_DIRECTION, target: Float) {
        val box = binding.vwBox

        val movementAnimator = ValueAnimator.ofFloat(getBoxPosition(direction), target).apply {
            interpolator = LinearInterpolator()
            duration = 500
            addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    boxIsMoving = false
                    if (direction == MOVEMENT_DIRECTION.X) {
                        xRestoreAnimator.cancel()
                        xRestoreAnimator.setFloatValues(getBoxPosition(direction), 0f)
                        xRestoreAnimator.start()
                    } else if (direction == MOVEMENT_DIRECTION.Y) {
                        yRestoreAnimator.cancel()
                        yRestoreAnimator.setFloatValues(getBoxPosition(direction), 0f)
                        yRestoreAnimator.start()
                    } else if (direction == MOVEMENT_DIRECTION.Z) {
                        zRestoreAnimator.cancel()
                        zRestoreAnimator.setFloatValues(getBoxPosition(direction), 0f)
                        zRestoreAnimator.start()
                    }
                }
            })
        }
        movementAnimator.addUpdateListener {
            val value = it.animatedValue as Float
            setBoxPosition(direction, value)
        }
        movementAnimator.start()
    }

    override fun onSensorChanged(event: SensorEvent) {
        val rotX = (event.values[0] * 180 / PI).toFloat()
        val rotY = (event.values[1] * 180 / PI).toFloat()
        val rotZ = (event.values[2] * 180 / PI).toFloat()

        if (rotY != 0f) {
            boxIsMoving = true
            animateTo(MOVEMENT_DIRECTION.X, rotY)
        }
        if (rotX != 0f) {
            boxIsMoving = true
            animateTo(MOVEMENT_DIRECTION.Y, rotX)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }
}