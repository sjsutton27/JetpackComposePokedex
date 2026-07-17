package com.example.jetpackcomposepokedex.pokemonlist

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.jetpackcomposepokedex.repository.PokemonRepository
import androidx.palette.graphics.Palette
import com.example.jetpackcomposepokedex.data.models.PokedexListEntry
import kotlinx.coroutines.launch
import com.example.jetpackcomposepokedex.util.Constants.PAGE_SIZE
import com.example.jetpackcomposepokedex.util.Resource
import kotlinx.coroutines.Dispatchers
import java.util.Locale


class PokemonListViewModel(
    private val repository: PokemonRepository
) : ViewModel(){

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(value = listOf())
    var loadError = mutableStateOf(value = "")
    var isLoading = mutableStateOf(value = false)
    var endReached = mutableStateOf(value = false)

    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    init{
        loadPokemonPaginated()
    }

    fun searchPokemonList(query: String){
        val listToSearch = if(isSearchStarting){
            pokemonList.value
        }else{
            cachedPokemonList
        }
        viewModelScope.launch(context = Dispatchers.IO){
            if(query.isEmpty()){
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter{ result ->
                result.pokemonName.contains(other = query.trim(), ignoreCase = true) || result.number.toString() == query.trim()
            }
            if(isSearchStarting){
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }

    fun loadPokemonPaginated(){
        viewModelScope.launch{
            isLoading.value = true
            when(val result = repository.getPokemonList(
                limit = PAGE_SIZE,
                offset = curPage * PAGE_SIZE
            )){
                is Resource.Success ->{
                    //checks if we went over the PAGE_Size
                    val pokemonResponse = result.data

                    if (pokemonResponse == null) {
                        loadError.value = "Failed to load Pokémon."
                        isLoading.value = false
                        return@launch
                    }
                    endReached.value = curPage * PAGE_SIZE >= pokemonResponse.count
                    val pokedexEntries = pokemonResponse.results.mapIndexed{ _, entry ->
                        val number= if(entry.url.endsWith(suffix = "/")){
                            entry.url.dropLast(n = 1).takeLastWhile { character -> character.isDigit() }
                        }else{
                            entry.url.takeLastWhile { character -> character.isDigit() }
                        }
                        val url = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${number}.png"
                        PokedexListEntry(entry.name.uppercase(Locale.ROOT), url, number.toInt())
                    }
                    curPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error ->{
                    loadError.value = result.message ?: "Failed to load Pokémon."
                    isLoading.value = false

                }

                is Resource.Loading -> {

                }
            }
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinished: (Color) -> Unit){
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate {
            palette -> palette?.dominantSwatch?.rgb?.let{
                colorValue -> onFinished(Color(colorValue))
            }
        }
    }
}