package com.offerus.navigation

import androidx.compose.material.icons.Icons
import com.offerus.R

data class BottomBarActions(
    var route: String,
    var selectedIcon: Int
)

val SECTIONS = listOf(
    BottomBarActions(
        route = BottomBarRoute.SEARCH,
        selectedIcon = R.drawable.search,

        ),
    BottomBarActions(
        route = BottomBarRoute.HOME,
        selectedIcon = R.drawable.ic_launcher_foreground,

        ),
    BottomBarActions(
        route = BottomBarRoute.MYOFFERS,
        selectedIcon = R.drawable.ic_launcher_foreground,

        )
)

object BottomBarRoute{
    const val HOME = "home"
    const val SEARCH = "search"
    const val MYOFFERS = "my_offers"
}