package com.example.testapplt.ui.viewmodels

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testapplt.R
import com.example.testapplt.data.Repository
import com.example.testapplt.data.models.ListItem
import com.example.testapplt.utils.SingleLiveEvent
import com.example.testapplt.utils.isInternetAvailable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repository: Repository,
    private val context: Application
) : ViewModel() {
    private val itemsMutable = MutableLiveData(emptyList<ListItem>())
    val itemsList: LiveData<List<ListItem>>
        get() = itemsMutable

    private var currentSort = "server"

    private val showProgressBarMutable = MutableLiveData(false)
    val progressBarVisible: LiveData<Boolean>
        get() = showProgressBarMutable

    private val serverConnectErrorEvent = SingleLiveEvent<String>()
    val serverConnectError: LiveData<String>
        get() = serverConnectErrorEvent

    fun init() {
        var count = 0
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if(count<=0){
                    count = 20
                    updateItems()
                }
            }
        }, 0, 1000)
    }

    fun sortSwitch(sort: String) {
        when (sort) {
            "server" -> currentSort ="server"
            "date" -> currentSort = "date"
        }
        init()
    }


    private fun updateItems() {
        if(isInternetAvailable(context)){
            viewModelScope.launch(Dispatchers.IO) {
                try {
                    showProgressBarMutable.postValue(true)
                    val list = repository.getList()
                    when (currentSort) {
                        "server" -> {
                            itemsMutable.postValue(list.sortedBy {it.sort})
                        }
                        "date" -> {
                            itemsMutable.postValue(list.sortedBy {it.date})
                        }
                        else -> {
                            serverConnectErrorEvent.postValue("Incorrect sort type")
                        }
                    }
                    showProgressBarMutable.postValue(false)
                } catch (e: Exception) {
                    showProgressBarMutable.postValue(false)
                    serverConnectErrorEvent.postValue(e.message)
                }
            }
        }else{
            serverConnectErrorEvent.postValue(context.getString(R.string.connection_error))
        }

    }
}