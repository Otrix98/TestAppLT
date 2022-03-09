package com.example.testapplt.ui.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.example.testapplt.R
import com.example.testapplt.data.Repository
import com.example.testapplt.utils.SingleLiveEvent
import com.example.testapplt.utils.isInternetAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LogInViewModel @Inject constructor(
    private val repository: Repository,
    private val context: Application
) : ViewModel() {
    private val getPhoneMaskMutable = MutableLiveData(context.getString(R.string.phone_mask))
    val phoneMask: LiveData<String>
        get() = getPhoneMaskMutable

    private val authCheckMutable = MutableLiveData("initial")
    val authCheck: LiveData<String>
        get() = authCheckMutable

    private val showProgressBarMutable = MutableLiveData(false)
    val progressBarVisible: LiveData<Boolean>
        get() = showProgressBarMutable

    private val serverConnectErrorEvent = SingleLiveEvent<String>()
    val serverConnectError: LiveData<String>
        get() = serverConnectErrorEvent

    fun getPhoneMask() {
        if (isInternetAvailable(context)) {
        viewModelScope.launch {
            try {
                val mask = repository.getPhoneMask()
                getPhoneMaskMutable.postValue(mask)
            } catch (t: Throwable) {
                serverConnectErrorEvent.postValue(t.message)
            }
        }
        }else {
            serverConnectErrorEvent.postValue(context.getString(R.string.connection_error))
        }
    }

    fun authCheck(phone: String, password: String) {
        showProgressBar()
        if (isInternetAvailable(context)) {
            viewModelScope.launch {
                try {
                    val status = repository.authCheck(phone, password)
                    authCheckMutable.postValue(status.toString())
                    hideProgressBar()
                } catch (t: Throwable) {
                    hideProgressBar()
                    serverConnectErrorEvent.postValue(t.message)
                }
            }
        } else {
            hideProgressBar()
            serverConnectErrorEvent.postValue(context.getString(R.string.connection_error))
        }
    }

    private fun showProgressBar() {
        showProgressBarMutable.postValue(true)
    }

    private fun hideProgressBar() {
        showProgressBarMutable.postValue(false)
    }
}