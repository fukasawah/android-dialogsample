package jp.co.neosystem.rd.android.sample.dialogsample

import android.databinding.DataBindingUtil
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import jp.co.neosystem.rd.android.sample.dialogsample.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val vm = MainViewModel()
        val action = MainAction(this, vm)

        binding.setLifecycleOwner(this)
        binding.vm = vm
        binding.action = action

        vm.progress.postValue(false)
    }
}
