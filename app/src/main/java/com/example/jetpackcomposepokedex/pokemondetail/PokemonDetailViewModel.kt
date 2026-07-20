package com.example.jetpackcomposepokedex.pokemondetail

import androidx.lifecycle.ViewModel
import com.example.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.example.jetpackcomposepokedex.repository.PokemonRepository
import com.example.jetpackcomposepokedex.util.Resource

class PokemonDetailViewModel(
    private val repository: PokemonRepository
): ViewModel() {
    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon>{
        return repository.getPokemonInfo(pokemonName = pokemonName)
    }
}