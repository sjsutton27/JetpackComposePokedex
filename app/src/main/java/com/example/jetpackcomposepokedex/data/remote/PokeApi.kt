package com.example.jetpackcomposepokedex.data.remote

import com.example.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.example.jetpackcomposepokedex.data.remote.responses.PokemonList
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {

    // Retrofit interface for making API calls. It defines two suspend functions: getPokemonList and getPokemonInfo.
    // These functions are used to fetch a list of Pokémon and their details based on provided parameters.
    // limit is for Pokémon amount to load in and the offset amount of Pokémon to skip.
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonList

    @GET("pokemon/{name}")
    suspend fun getPokemonInfo(
        @Path("name") name: String
    ):Pokemon
}