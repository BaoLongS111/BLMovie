package com.balon.blmovie.feature.moviedetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.MovieDetailRepository
import com.balon.blmovie.core.extension.asResult
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import com.balon.blmovie.feature.moviedetail.navigation.MovieDetailRoutes
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(
    assistedFactory = MovieDetailViewModel.Factory::class
)
class MovieDetailViewModel @AssistedInject constructor (
    private val movieDetailRepository: MovieDetailRepository,
    @Assisted
    private val navKey: MovieDetailRoutes.MovieDetail
): ViewModel() {

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val movieId: Long = navKey.movieId

    init{
        loadData(movieId = movieId)
    }

    private fun loadData(movieId:Long){

        // 设置第一次加载状态
        _uiState.update {
            it.copy(
                pageState = PageState.Loading,
                refreshState = RefreshState.IDLE,
                loadMoreState = LoadMoreState.IDLE
            )
        }


        viewModelScope.launch {
            movieDetailRepository.movieDetail(movieId = movieId)
                .asResult()
                .collectLatest { result ->
                    if(result.isSuccess){
                        val resultData = result.getOrNull()

                        _uiState.update {
                            it.copy(
                                movieDetail = resultData?.data,
                                pageState = PageState.Success
                            )
                        }
                    }else if(result.isFailure){
                        val resultData = result.exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                movieDetail = null,
                                pageState = PageState.Error(
                                    throwable = resultData!!
                                )
                            )
                        }
                    }

                }
        }
    }

    fun onRetry(){
        loadData(movieId)
    }


    @AssistedFactory
    interface Factory {
        fun create(navKey: MovieDetailRoutes.MovieDetail): MovieDetailViewModel
    }

}