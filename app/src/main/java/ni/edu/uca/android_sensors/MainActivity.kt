package ni.edu.uca.android_sensors

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ni.edu.uca.android_sensors.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}