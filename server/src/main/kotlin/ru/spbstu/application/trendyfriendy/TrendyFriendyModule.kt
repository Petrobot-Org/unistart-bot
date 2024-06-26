package ru.spbstu.application.trendyfriendy

import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val trendyFriendyModule = module {
    single { HotReloader("trends.yaml") }
    singleOf(::TrendyFriendyService)
}
