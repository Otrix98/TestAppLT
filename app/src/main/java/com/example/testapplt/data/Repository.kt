package com.example.testapplt.data

import com.example.testapplt.data.models.ListItem
import com.example.testapplt.networking.DevExamApi
import javax.inject.Inject

class Repository@Inject constructor(
    private val api: DevExamApi
) {

    suspend fun getPhoneMask(): String {
        return api.getPhoneMask().phoneMask
    }

    suspend fun authCheck(phone: String, password: String): Boolean {
        return api.checkAuth(phone, password).success
    }

    suspend fun getList(): List<ListItem> {
        return api.getList()
    }
}