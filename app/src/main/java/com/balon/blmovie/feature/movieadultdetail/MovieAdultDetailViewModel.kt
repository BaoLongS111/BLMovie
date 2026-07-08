package com.balon.blmovie.feature.movieadultdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.MovieDetailRepository
import com.balon.blmovie.core.extension.asResult
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import com.balon.blmovie.feature.movieadultdetail.navigation.MovieAdultDetailRoutes
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
    assistedFactory = MovieAdultDetailViewModel.Factory::class
)
class MovieAdultDetailViewModel @AssistedInject constructor (
    private val movieDetailRepository: MovieDetailRepository,
    @Assisted
    private val navKey: MovieAdultDetailRoutes.MovieAdultDetail
): ViewModel() {

    private val _uiState = MutableStateFlow(MovieAdultDetailUiState())
    val uiState = _uiState.asStateFlow()

    private val movieAdultId: Long = navKey.movieAdultId

    init{
        loadData(movieAdultId = movieAdultId)
    }

    private fun loadData(movieAdultId:Long){

        // 设置第一次加载状态
        _uiState.update {
            it.copy(
                pageState = PageState.Loading,
                refreshState = RefreshState.IDLE,
                loadMoreState = LoadMoreState.IDLE
            )
        }


        viewModelScope.launch {
            movieDetailRepository.movieAdultDetail(movieAdultId = movieAdultId)
                .asResult()
                .collectLatest { result ->
                    if(result.isSuccess){
                        val resultData = result.getOrNull()

                        _uiState.update {
                            it.copy(
                                movieAdultDetail = resultData?.data,
                                pageState = PageState.Success
                            )
                        }
                    }else if(result.isFailure){
                        val resultData = result.exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                movieAdultDetail = null,
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
        loadData(movieAdultId)
    }


    @AssistedFactory
    interface Factory {
        fun create(navKey: MovieAdultDetailRoutes.MovieAdultDetail): MovieAdultDetailViewModel
    }

}