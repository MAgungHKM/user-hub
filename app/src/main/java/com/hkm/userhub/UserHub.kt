package com.hkm.userhub

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class UserHub : Application() {
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        val config = RealmConfiguration.Builder()
            .name("user_hub.realm")
            .schemaVersion(1)
            .build()
        Realm.setDefaultConfiguration(config)
    }
}