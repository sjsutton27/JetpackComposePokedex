package com.example.jetpackcomposepokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.jetpackcomposepokedex.pokemondetail.PokemonDetailScreen
import com.example.jetpackcomposepokedex.pokemonlist.PokemonListScreen
import com.example.jetpackcomposepokedex.ui.theme.JetpackComposePokedexTheme

// Remove @AndroidEntryPoint since Koin doesn't require it
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            JetpackComposePokedexTheme {
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "pokemon_list_screen"
                ) {
                    composable("pokemon_list_screen") {
                        PokemonListScreen(navController = navController)
                    }

                    composable(
                        route = "pokemon_detail_screen/{dominantColor}/{pokemonName}",
                        arguments = listOf(
                            navArgument("dominantColor") {
                                type = NavType.IntType
                            },
                            navArgument("pokemonName") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val dominantColor = remember {
                            val colorInt = it.arguments?.getInt("dominantColor")
                            colorInt?.let { Color(it) } ?: Color.White
                        }
                        val pokemonName = remember {
                            it.arguments?.getString("pokemonName")
                        }

                        PokemonDetailScreen(
                            dominantColor = dominantColor,
                            pokemonName = pokemonName?.lowercase()?: "",
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
