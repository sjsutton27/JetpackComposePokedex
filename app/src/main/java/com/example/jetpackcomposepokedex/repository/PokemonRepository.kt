package com.example.jetpackcomposepokedex.repository

import com.example.jetpackcomposepokedex.data.remote.PokeApi
import com.example.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.example.jetpackcomposepokedex.data.remote.responses.PokemonList
import com.example.jetpackcomposepokedex.util.Resource


class PokemonRepository(
    private val api: PokeApi
){
    //Dependency injection for the repository
    // These are the functions that will be used to fetch data from the API. Which will be used in our ViewModels
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList> {
        val response = try{
            api.getPokemonList(limit, offset)
        }catch(e : Exception){
            return Resource.Error(message = "Unknown Error Occurred")
        }
        return Resource.Success(response)

    }

    suspend fun getPokemonInfo(pokemonName: String): Resource<Pokemon> {
        val response = try{
            api.getPokemonInfo(pokemonName)
        }catch(e : Exception){
            return Resource.Error(message = "Unknown Error Occurred")
        }
        return Resource.Success(data = response)

    }
}