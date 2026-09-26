package org.jansaarthi.app.ui.components

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.data.model.AppLanguage
import org.jansaarthi.app.ui.localization.getJanSaarthiStrings
import org.jansaarthi.app.ui.theme.GovNavyContainer
import org.jansaarthi.app.ui.theme.GovNavyPrimary
import org.jansaarthi.app.ui.theme.GovSaffron

/**
 * JanSaarthiBottomNav — Shared global bottom navigation bar with full localization support.
 *
 * Tabs: Home (0) · Schemes (1) · Applications (2) · Documents (3) · Profile (4)
 *
 * @param selectedTab  Active tab index: 0=Home, 1=Schemes, 2=Applications, 3=Documents, 4=Profile
 * @param pendingApplications  Badge count on Applications tab (hidden when tab is selected)
 */
@Composable
fun JanSaarthiBottomNav(
    selectedTab: Int = 0,
    currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    pendingApplications: Int = 2,
    onNavigateHome: () -> Unit = {},
    onNavigateSchemes: () -> Unit = {},
    onNavigateApplications: () -> Unit = {},
    onNavigateDocuments: () -> Unit = {},
    onNavigateProfile: () -> Unit = {}
) {
    val strings = getJanSaarthiStrings(currentLanguage)
    val unselectedColor = Color(0xFF94A3B8)

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp,
        modifier = Modifier.height(72.dp)
    ) {
        // 0: Home
        NavigationBarItem(
            selected = selectedTab == 0,
            onClick = onNavigateHome,
            icon = {
                Icon(
                    imageVector = Icons.Filled.Home,
                    contentDescription = strings.navHome,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navHome,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GovNavyPrimary,
                selectedTextColor = GovNavyPrimary,
                indicatorColor = GovNavyContainer,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor
            )
        )

        // 1: Schemes
        NavigationBarItem(
            selected = selectedTab == 1,
            onClick = onNavigateSchemes,
            icon = {
                Icon(
                    imageVector = Icons.Filled.GridView,
                    contentDescription = strings.navSchemes,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navSchemes,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GovNavyPrimary,
                selectedTextColor = GovNavyPrimary,
                indicatorColor = GovNavyContainer,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor
            )
        )

        // 2: Applications
        NavigationBarItem(
            selected = selectedTab == 2,
            onClick = onNavigateApplications,
            icon = {
                BadgedBox(
                    badge = {
                        if (pendingApplications > 0 && selectedTab != 2) {
                            Badge(
                                containerColor = GovSaffron,
                                contentColor = Color.White
                            ) {
                                Text(
                                    text = pendingApplications.toString(),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Assignment,
                        contentDescription = strings.navApplications,
                        modifier = Modifier.size(24.dp)
                    )
                }
            },
            label = {
                Text(
                    text = strings.navApplications,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (selectedTab == 2) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GovNavyPrimary,
                selectedTextColor = GovNavyPrimary,
                indicatorColor = GovNavyContainer,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor
            )
        )

        // 3: Documents
        NavigationBarItem(
            selected = selectedTab == 3,
            onClick = onNavigateDocuments,
            icon = {
                Icon(
                    imageVector = Icons.Filled.FolderOpen,
                    contentDescription = strings.navDocuments,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navDocuments,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (selectedTab == 3) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GovNavyPrimary,
                selectedTextColor = GovNavyPrimary,
                indicatorColor = GovNavyContainer,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor
            )
        )

        // 4: Profile
        NavigationBarItem(
            selected = selectedTab == 4,
            onClick = onNavigateProfile,
            icon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = strings.navProfile,
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = strings.navProfile,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = if (selectedTab == 4) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 10.sp
                    )
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = GovNavyPrimary,
                selectedTextColor = GovNavyPrimary,
                indicatorColor = GovNavyContainer,
                unselectedIconColor = unselectedColor,
                unselectedTextColor = unselectedColor
            )
        )
    }
}

