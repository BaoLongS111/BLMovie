package com.balon.blmovie.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.SearchRepository
import com.balon.blmovie.core.extension.asResult
import com.balon.blmovie.core.model.PageState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // 初始化数据
        _uiState.update {
            it.copy(
                moviePageState = PageState.Loading,
                articlePageState = PageState.Loading,
                actorPageState = PageState.Loading
            )
        }
    }

    fun searchMovie(keywords: String) {

        viewModelScope.launch {
            searchRepository.searchMovie(
                mapOf(
                    "keyword" to keywords,
                    "page" to "1",
                    "size" to "50"
                )
            ).asResult()
                .collectLatest { result ->
                    if (result.isSuccess) {
                        val data = result.getOrNull()
                        _uiState.update {
                            it.copy(
                                moviePageState = PageState.Success,
                                articlePageState = PageState.Loading,
                                actorPageState = PageState.Loading,
                                movieList = data?.data?.list!!
                            )
                        }
                    } else {
                        val throwable = result.exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                moviePageState = PageState.Error(
                                    throwable = throwable!!
                                ),
                                articlePageState = PageState.Loading,
                                actorPageState = PageState.Loading,
                                movieList = emptyList()
                            )
                        }

                    }
                }


        }

    }

    fun searchMovieAdult(keywords: String) {

        viewModelScope.launch {
            searchRepository.searchMovieAdult(
                mapOf(
                    "keyword" to keywords,
                    "page" to "1",
                    "size" to "50"
                )
            ).asResult()
                .collectLatest { result ->
                    if (result.isSuccess) {
                        val data = result.getOrNull()
                        _uiState.update {
                            it.copy(
                                moviePageState = PageState.Success,
                                articlePageState = PageState.Loading,
                                actorPageState = PageState.Loading,
                                movieAdultList  = data?.data?.list!!
                            )
                        }
                    } else {
                        val throwable = result.exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                moviePageState = PageState.Error(
                                    throwable = throwable!!
                                ),
                                articlePageState = PageState.Loading,
                                actorPageState = PageState.Loading,
                                movieAdultList  = emptyList()
                            )
                        }
                    }
                }


        }

    }
}