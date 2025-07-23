package io.jadu.remindmex.remindMe.presentation.route

import kotlinx.serialization.Serializable

@Serializable
sealed class NavRoute {
    @Serializable
    data object Login: NavRoute()

    @Serializable
    data object Home: NavRoute()

    @Serializable
    data object AddReminder:NavRoute()

    @Serializable
    data object ReminderDetails:NavRoute()

}