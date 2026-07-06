package com.balon.blmovie.feature.moviedetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.balon.blmovie.core.design.component.MyVideoPlayer
import com.balon.blmovie.core.design.theme.BLMovieTheme
import com.balon.blmovie.core.extension.parsePlayEpisodes

@Composable
fun MovieDetailRoute(
    movieId: Long,
    viewModel: MovieDetailViewModel = hiltViewModel(),
    onBack: () -> Unit
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    MovieDetailScreen(
        uiState = uiState
    )

    LaunchedEffect(key1 = movieId) {
        viewModel.loadMovieDetail(movieId)
    }
}

@Composable
fun MovieDetailScreen(
    uiState: MovieDetailUiState
) {
    if(uiState.movieDetail==null){
        return
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        val movie = uiState.movieDetail

        val episodes = remember(movie.playUrl) {
            movie?.playUrl?.parsePlayEpisodes()
        }

        val currentEpisode = episodes?.firstOrNull()
        MyVideoPlayer(
            url = currentEpisode!!.url,
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        )
        LazyColumn() {
            item {
                Text(text = uiState.movieDetail.toString())
            }
        }


    }
}

@Preview
@Composable
fun MovieDetailScreenPreview() {
    BLMovieTheme() {
        MovieDetailScreen(MovieDetailUiState())
    }
}
