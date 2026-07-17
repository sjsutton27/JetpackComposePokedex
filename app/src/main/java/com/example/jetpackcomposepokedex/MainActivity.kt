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

// Remove @AndroidEntryPoint since KOIN doesn't require it
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            JetpackComposePokedexTheme {
                // Setup navigation
                // rememberNavController is used to remember the state of the NavController
                val navController = rememberNavController()
                //Before we used to need Nav-graph for fragments in XML, In jetpack compose you only need a NavHost
                // In NavHost we define the start destination or use a nav graph builder to define the graph. In XML we would use <navigation> and an ID
                NavHost(
                    navController = navController,
                    startDestination = "pokemon_list_screen"
                ) {
                    //Define our composable screens here
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
                    ) { navBackStackEntry ->
                        val dominantColor = remember {
                            val colorInt = navBackStackEntry.arguments?.getInt("dominantColor")
                            //Use let to check if colorInt is not null and return the color value inside the let lambda
                            colorInt?.let { colorValue -> Color(colorValue) } ?: Color.White
                        }
                        val pokemonName = remember {
                            navBackStackEntry.arguments?.getString("pokemonName")
                        }

                        PokemonDetailScreen(
                            dominantColor = dominantColor,
                            pokemonName = pokemonName?.lowercase().orEmpty(),
                            navController = navController
                        )
                    }
                }
            }
        }
    }
}
