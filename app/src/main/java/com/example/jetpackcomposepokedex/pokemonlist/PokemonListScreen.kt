package com.example.jetpackcomposepokedex.pokemonlist

import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.jetpackcomposepokedex.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.ImageRequest
import com.example.jetpackcomposepokedex.data.models.PokedexListEntry
import com.example.jetpackcomposepokedex.ui.theme.RobotoCondensed
import org.koin.androidx.compose.koinViewModel

/**
 * PokemonListScreen is the main screen of the app.
 * koinViewModel is used to inject the PokemonListViewModel into the composable.
 * @param navController is used to navigate between screens.*/
@Composable
fun PokemonListScreen(
    navController: NavController,
    viewModel: PokemonListViewModel = koinViewModel()
){
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.background
            )
            .fillMaxSize()
            .statusBarsPadding()
    ){
        Column {
            Spacer(
                modifier = Modifier.height(height = 20.dp)
            )
            // Image is a composable that is used to display the pokemon header image
            Image(
                painter = painterResource(id = R.drawable.ic_international_pok_mon_logo),
                contentDescription = "Pokemon",
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterHorizontally)
            )

            SearchBar(
                hint = "Search...",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(all = 16.dp)
            ){ search ->
                viewModel.searchPokemonList(query = search)
            }

            Spacer(modifier = Modifier.height(height = 16.dp))

            PokemonList(navController = navController)

        }
    }
}

/**
 * SearchBar is a custom composable that is used to search for a Pokémon.
 * hint is the hint that is displayed when the search bar is empty.
 * onSearch is a lambda that is called when the user enters a search query.*/
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
){
    // remember is used to remember the state of the text field.
    var text by remember{
        mutableStateOf(value = "")
    }

    var isHintDisplayed by remember {
        mutableStateOf(value = hint != "")
    }

    Box(modifier = modifier){
        BasicTextField(
            value = text,
            onValueChange = { value ->
                text = value
                onSearch(value)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(elevation = 5.dp, shape = CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged { hint ->
                    isHintDisplayed = !hint.isFocused && text.isNotEmpty()
                }

        )
        if(isHintDisplayed){
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}

@Composable
fun PokemonList(
    navController: NavController,
    viewModel: PokemonListViewModel = koinViewModel()
){
    val pokemonList by remember {
        viewModel.pokemonList
    }
    val endReached by remember { viewModel.endReached }
    val loadError by remember { viewModel.loadError}
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    LazyColumn (contentPadding = PaddingValues(16.dp)){
        val itemCount = if(pokemonList.size % 2 == 0){
            pokemonList.size / 2
        }else{
            pokemonList.size / 2 + 1
        }

        items(itemCount){
            if(it >- itemCount - 1 && !endReached && !isLoading && !isSearching){
                    viewModel.loadPokemonPaginated()
            }
            PokedexRow(rowIndex = it, entries = pokemonList, navController = navController)
        }
    }

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxWidth()
    ){
        if(isLoading){
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if(loadError.isNotEmpty()){
            RetrySection(error = loadError) {
                viewModel.loadPokemonPaginated()
            }
        }
    }
}

/**
 * PokedexEntry is a composable that is used to display a single entry in the Pokémon list.
 * @param entry is the entry that is displayed.
 * @param navController is used to navigate between screens.
 * @param modifier is used to modify the composable.
 * @param viewModel is used to inject the PokemonListViewModel into the composable.*/
@Composable
fun PokedexEntry(
    entry: PokedexListEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: PokemonListViewModel = koinViewModel()
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember { mutableStateOf(value = defaultDominantColor) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(size = 10.dp))
            .clip(shape = RoundedCornerShape(size = 10.dp))
            .aspectRatio(ratio = 1f)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(dominantColor, defaultDominantColor)
                )
            )
            .clickable {
                navController.navigate(
                    route = "pokemon_detail_screen/${dominantColor.toArgb()}/${entry.pokemonName}"
                )
            }
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(context = LocalContext.current)
                    .data(data = entry.imageUrl)
                    .crossfade(enable = true)
                    .build(),
                contentDescription = entry.pokemonName,
                loading = {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .scale(scale = 0.5f)
                            .align(alignment = Alignment.Center)
                    )
                },
                success = { success ->
                    // Calculate the dominant color from the loaded image
                    viewModel.calcDominantColor(success.result.drawable) { color ->
                        dominantColor = color
                    }
                    SubcomposeAsyncImageContent()
                },
                modifier = Modifier
                    .size(size = 120.dp)
                    .align(alignment = Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(height = 8.dp))

            Text(
                entry.pokemonName.replaceFirstChar { character -> character.uppercase() },
                fontFamily = RobotoCondensed,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

// PokedexRow is each row of two Pokémon in the list
// rowIndex is the index of the row
// entries is the list of Pokémon
// navController is used to navigate between screens.
@Composable
fun PokedexRow(
    rowIndex: Int,
    entries: List<PokedexListEntry>,
    navController: NavController
){
    Column {
        Row{
            PokedexEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(weight = 1f)
            )
            Spacer(modifier = Modifier.width(width = 16.dp))
            if(entries.size >= rowIndex * 2 + 2){
                PokedexEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(weight = 1f)
                )
            }
            else{
                Spacer(modifier = Modifier.weight(weight = 1f))
            }
        }

        Spacer(modifier = Modifier.height(height = 16.dp))
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
){
    Column{
        Text(text = error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(height = 8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(alignment = Alignment.CenterHorizontally)
        ){
            Text(text = "Retry")
        }
    }
}