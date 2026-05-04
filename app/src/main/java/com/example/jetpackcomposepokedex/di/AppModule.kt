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

val appModule = module {

    single<PokeApi> {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PokeApi::class.java)
    }

    single {
        PokemonRepository(get())
    }

    viewModel { PokemonListViewModel(get()) }
    viewModel { PokemonDetailViewModel(get()) }
}
