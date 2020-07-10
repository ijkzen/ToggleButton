package tech.ijkzen.mybutton

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import tech.ijkzen.mybutton.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        mBinding.toggleButton.setOnClickListener {
            mBinding.toggleButton.toggle()
        }
    }
}