package com.example.jetpackcomposepokedex.data.models

/**
 * PokedexListEntry is a data class that represents a single entry in the Pokémon list.
 * @param pokemonName is the name of the Pokémon.
 * @param imageUrl is the URL of the image of the Pokémon.
 * @param number is the number of the Pokémon., won't be displayed in the Pokémon list screen will be in the detail screen.*/
data class PokedexListEntry(
    val pokemonName: String,
    val imageUrl: String,
    val number: Int
)
