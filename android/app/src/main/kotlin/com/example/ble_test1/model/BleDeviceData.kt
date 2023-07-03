package com.example.ble_test1.model

import org.json.JSONException
import org.json.JSONObject


class BleDeviceData(var name: String, var macAddr: String) {
    fun toMap(): String {
        val jo = JSONObject()
        try {
            jo.put(fName, name)
            jo.put(fMacAddr, macAddr)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return jo.toString()
    }

    companion object {
        const val fName = "name"
        const val fMacAddr = "macAddr"
        fun fromMap(obj: Any): BleDeviceData? {
            try {
                val jo = JSONObject(obj.toString())
                return BleDeviceData(jo.getString(fName), jo.getString(fMacAddr),)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return null
        }
    }
}