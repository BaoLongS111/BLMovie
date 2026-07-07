package com.balon.blmovie.feature.moviedetail

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.MovieDetailRepository
import com.balon.blmovie.core.extension.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MovieDetailViewModel @Inject constructor (
    private val movieDetailRepository: MovieDetailRepository
): ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState = _uiState.asStateFlow()

    init{
        loadData()
    }

    private fun loadData(){



    }

    fun loadMovieDetail(movieId:Long){
        viewModelScope.launch {
            movieDetailRepository.movieDetail(movieId = movieId)
                .asResult()
                .collectLatest { result ->
                    if(result.isSuccess){
                        val resultData = result.getOrNull()
                        _uiState.update {
                            it.copy(
                                movieDetail = resultData?.data
                            )
                        }
                    }

                }
        }
    }

}