package com.example.room_kotlin.viewmodel

import android.os.Build
import android.util.Patterns
import androidx.annotation.RequiresApi
import androidx.databinding.Bindable
import com.example.room_kotlin.db.Subscriber
import com.example.room_kotlin.db.SubscriberRepository
import kotlinx.coroutines.Job
import androidx.databinding.Observable
import androidx.lifecycle.*
import com.example.room_kotlin.ui.Event
import kotlinx.coroutines.launch


class SubscriberViewModel(private val repository: SubscriberRepository) : ViewModel(), Observable {

    val subscribers = repository.subscribers
    private var isUpdateOrDelete = false
    private lateinit var subscriberToUpdateOrDelete: Subscriber

    @Bindable
    val inputName = MutableLiveData<String>()

    @Bindable
    val inputEmail = MutableLiveData<String>()

    @Bindable
    val saveOrUpdateButtonText = MutableLiveData<String>()

    @Bindable
    val clearAllButtonText = MutableLiveData<String>()

    private val statusMessage = MutableLiveData<Event<String>>()
    val message: LiveData<Event<String>>
        get() = statusMessage

    init {
        saveOrUpdateButtonText.value = "Save"
        clearAllButtonText.value = "Clear All"
    }

    fun saveOrUpdate() {

        if (inputName.value == null) {
            statusMessage.value = Event("pls enter name")
        } else if (inputEmail.value == null) {
            statusMessage.value = Event("pls enter email")
        } else if (Patterns.EMAIL_ADDRESS.matcher(inputEmail.value!!).matches()) {
            statusMessage.value = Event("pls enter correct email address")
        } else {
            if (isUpdateOrDelete) {
                subscriberToUpdateOrDelete.name = inputName.value!!
                subscriberToUpdateOrDelete.email = inputEmail.value!!
                update(subscriberToUpdateOrDelete)
            } else {
                val name = inputName.value!!
                val email = inputEmail.value!!
                insert(Subscriber(0, name, email))
                inputName.value = null
                inputEmail.value = null
            }
        }


    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun clearAllOrDelete() {
        if (isUpdateOrDelete) {
            delete(subscriberToUpdateOrDelete)
        } else {
            clearAll()
        }

    }


    private fun insert(subscriber: Subscriber): Job = viewModelScope.launch {
        val newRowId: Long = repository.insert(subscriber)
        if (newRowId > -1) {
            statusMessage.value = Event("Subscriber inserted successfully $newRowId")
        } else {
            statusMessage.value = Event("Error")
        }
    }

    private fun update(subscriber: Subscriber): Job = viewModelScope.launch {
        val noOfRows: Int = repository.update(subscriber)
        if (noOfRows > -1) {
            repository.delete(subscriber)
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "save"
            clearAllButtonText.value = "clear all"
            statusMessage.value = Event("Subscriber updated successfully")
        } else {
            statusMessage.value = Event("error")
        }
    }

    private fun delete(subscriber: Subscriber): Job = viewModelScope.launch {
        val noOfRowsDeleted = repository.delete(subscriber)
        if (noOfRowsDeleted > -1) {
            inputName.value = null
            inputEmail.value = null
            isUpdateOrDelete = false
            saveOrUpdateButtonText.value = "save"
            clearAllButtonText.value = "clear all"
            statusMessage.value = Event("Subscriber deleted successfully $noOfRowsDeleted")
        } else {
            statusMessage.value = Event("Error")
        }

    }

    private fun clearAll(): Job = viewModelScope.launch {
        val noOfRowsDeleted = repository.deleteAll()
        if (noOfRowsDeleted > -1) {
            statusMessage.value = Event("all subscribers deleted successfully $noOfRowsDeleted")
        } else {
            statusMessage.value = Event("error")
        }
    }

    fun initUpdateAndDelete(subscriber: Subscriber) {
        inputName.value = subscriber.name
        inputEmail.value = subscriber.email
        isUpdateOrDelete = true
        subscriberToUpdateOrDelete = subscriber
        saveOrUpdateButtonText.value = "update"
        clearAllButtonText.value = "Delete"
    }

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }
}
