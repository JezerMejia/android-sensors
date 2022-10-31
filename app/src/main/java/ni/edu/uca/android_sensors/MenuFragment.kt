package ni.edu.uca.android_sensors

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import ni.edu.uca.android_sensors.databinding.FragmentMenuBinding

class MenuFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        layoutInflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)

        //Button navigation
        binding.btnLightSensor.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_lightSensorFragment)
        }
        binding.btnXSensor.setOnClickListener {
            // Cambiar param del navigate
            findNavController().navigate(R.id.action_menuFragment_to_lightSensorFragment)
        }
        binding.btnSensorAccel.setOnClickListener {
            findNavController().navigate(R.id.action_menuFragment_to_sensorAccel)
        }

        return binding.root
    }
}