package ni.edu.uca.android_sensors

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.app.Service
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
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
    private var physicsMode = false

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

    private lateinit var handler: Handler
    private lateinit var runnable: Runnable

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        binding.root.setOnClickListener {
            setPhysicsMode()
        }

        handler = Handler(Looper.getMainLooper())
        runnable = object : Runnable {
            override fun run() {
                val location = IntArray(2)
                binding.vwBox.getLocationOnScreen(location)

                val xPos = location[0]
                val yPos = location[1]
                val boxWidth = binding.vwBox.width
                val boxHeight = binding.vwBox.height

                val screenWidth = Resources.getSystem().displayMetrics.widthPixels
                val screenHeight = Resources.getSystem().displayMetrics.heightPixels

                val deltaX = xVelocity / 100
                val deltaY = yVelocity / 100

                if (deltaX > 0 && xPos < screenWidth - boxWidth) {
                    binding.vwBox.translationX += deltaX
                } else if (deltaX < 0 && xPos > 0) {
                    binding.vwBox.translationX += deltaX
                } else {
                    xVelocity = 0f
                }

                if (deltaY > 0 && yPos < screenHeight - boxHeight) {
                    binding.vwBox.translationY += deltaY
                    println("DOWN: ${yPos + deltaY} - ${screenHeight} - ${boxHeight}")
                } else if (deltaY < 0 && yPos > 0) {
                    binding.vwBox.translationY += deltaY
                    println("UP")
                } else {
                    yVelocity = 0f
                }

                println("Running")

                if (physicsMode)
                    handler.postDelayed(this, 10)
            }
        }
    }

    private fun setPhysicsMode() {
        physicsMode = !physicsMode
        if (physicsMode) {
            binding.vwBox.setBackgroundColor(resources.getColor(R.color.purple_500))
            handler.postDelayed(runnable, 10)
        } else {
            binding.vwBox.setBackgroundColor(resources.getColor(R.color.black))
            handler.removeCallbacks(runnable)
        }
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


    private var xVelocity = 0f
    private var yVelocity = 0f
    private var zVelocity = 0f

    private fun applyVelocity(direction: MOVEMENT_DIRECTION, value: Float) {
        when (direction) {
            MOVEMENT_DIRECTION.X -> xVelocity += value
            MOVEMENT_DIRECTION.Y -> yVelocity += value
            MOVEMENT_DIRECTION.Z -> null
        }
    }

    override fun onSensorChanged(event: SensorEvent) {
        val rotX = (event.values[0] * 180 / PI).toFloat()
        val rotY = (event.values[1] * 180 / PI).toFloat()
        val rotZ = (event.values[2] * 180 / PI).toFloat()

        if (physicsMode) {
            applyVelocity(MOVEMENT_DIRECTION.X, rotY)
            applyVelocity(MOVEMENT_DIRECTION.Y, rotX)
        } else {
            if (rotY != 0f) {
                boxIsMoving = true
                animateTo(MOVEMENT_DIRECTION.X, rotY)
            }
            if (rotX != 0f) {
                boxIsMoving = true
                animateTo(MOVEMENT_DIRECTION.Y, rotX)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {

    }
}