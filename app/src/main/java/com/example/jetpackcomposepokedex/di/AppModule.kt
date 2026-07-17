package com.example.jetpackcomposepokedex.di

import com.example.jetpackcomposepokedex.data.remote.PokeApi
import com.example.jetpackcomposepokedex.pokemondetail.PokemonDetailViewModel
import com.example.jetpackcomposepokedex.pokemonlist.PokemonListViewModel
import com.example.jetpackcomposepokedex.repository.PokemonRepository
import com.example.jetpackcomposepokedex.util.Constants.BASE_URL
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// KOIN module for dependency injection, defines our dependencies
val appModule = module {

    //Retrofit Builder will create the API interface for us
    single<PokeApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }

    // Single instance of the PokemonRepository this will be used in our ViewModels and used with KOIN to inject it into our ViewModels
    single {
        PokemonRepository(api = get())
    }

    viewModel { PokemonListViewModel(repository = get()) }
    viewModel { PokemonDetailViewModel(repository = get()) }
}
