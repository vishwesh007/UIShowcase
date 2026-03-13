package com.ui.animatedmenu

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.ui.animatedmenu.showcase.*
import com.ui.animatedmenu.ui.theme.AnimatedMenuTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimatedMenuTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    // -2 = splash, -1 = main menu, 0-9 = showcase screens, 10 = showcase launcher
    var currentScreen by remember { mutableIntStateOf(-2) }

    when (currentScreen) {
        -2 -> AnimatedSplashScreen(onFinished = { currentScreen = -1 })
        -1 -> AnimatedBlobMenuScreen(onShowcase = { currentScreen = 10 })
        10 -> ShowcaseLauncher(
            onNavigate = { currentScreen = it },
            onBack = { currentScreen = -1 }
        )
        0 -> GaugeScreen(onBack = { currentScreen = 10 })
        1 -> ECommerceScreen(onBack = { currentScreen = 10 })
        2 -> TimerScreen(onBack = { currentScreen = 10 })
        3 -> DeliveryScreen(onBack = { currentScreen = 10 })
        4 -> WeatherScreen(onBack = { currentScreen = 10 })
        5 -> MusicPlayerScreen(onBack = { currentScreen = 10 })
        6 -> FitnessScreen(onBack = { currentScreen = 10 })
        7 -> ChatScreen(onBack = { currentScreen = 10 })
        8 -> ProfileScreen(onBack = { currentScreen = 10 })
        9 -> SmartHomeScreen(onBack = { currentScreen = 10 })
        11 -> NikeProductScreen(onBack = { currentScreen = 10 })
        12 -> ConfettiScreen(onBack = { currentScreen = 10 })
        13 -> LogoSplashScreen(onBack = { currentScreen = 10 })
        14 -> SlideMenuScreen(onBack = { currentScreen = 10 })
        15 -> SmartNavbarScreen(onBack = { currentScreen = 10 })
        16 -> ShoeSliderScreen(onBack = { currentScreen = 10 })
        17 -> ImageSliderScreen(onBack = { currentScreen = 10 })
        18 -> ParallaxScreen(onBack = { currentScreen = 10 })
        19 -> DessertParallaxScreen(onBack = { currentScreen = 10 })
        20 -> TvAppScreen(onBack = { currentScreen = 10 })
        21 -> PizzaAppScreen(onBack = { currentScreen = 10 })
        22 -> ShoeECommerceScreen(onBack = { currentScreen = 10 })
        23 -> FoodDeliveryScreen(onBack = { currentScreen = 10 })
        24 -> JournalScreen(onBack = { currentScreen = 10 })
        25 -> FurnitureScreen(onBack = { currentScreen = 10 })
        26 -> ShopScreen(onBack = { currentScreen = 10 })
        27 -> MusicAppScreen(onBack = { currentScreen = 10 })
        28 -> PlantShopScreen(onBack = { currentScreen = 10 })
        29 -> FinanceScreen(onBack = { currentScreen = 10 })
        30 -> TravelScreen(onBack = { currentScreen = 10 })
        31 -> MeditationScreen(onBack = { currentScreen = 10 })
        32 -> CryptoScreen(onBack = { currentScreen = 10 })
        33 -> SidebarDashboardScreen(onBack = { currentScreen = 10 })
        34 -> AnimatedLoaderScreen(onBack = { currentScreen = 10 })
        35 -> AnimatedSearchBarScreen(onBack = { currentScreen = 10 })
        36 -> FigmaConfigScreen(onBack = { currentScreen = 10 })
    }
}
