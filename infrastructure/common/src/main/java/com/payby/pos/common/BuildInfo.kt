package com.payby.pos.common

data class BuildInfo(
    val flavor: String,
    val versionCode: Int,
    val versionName: String,
    val serverType: Int = 0,   // 0-live 1-debug 2-uat
)