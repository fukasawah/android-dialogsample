package jp.co.neosystem.rd.android.sample.dialogsample

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel

class MainViewModel : ViewModel() {
    val progress : MutableLiveData<Boolean> = MutableLiveData()
}