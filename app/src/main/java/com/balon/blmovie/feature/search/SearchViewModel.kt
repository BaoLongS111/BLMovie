package com.balon.blmovie.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.SearchRepository
import com.balon.blmovie.core.extension.asResult
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.response.NetworkPageMeta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState(keywords=""))
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
                actorPageState = PageState.Loading,
                moviePagination = NetworkPageMeta(),
                movieAdultPagination = NetworkPageMeta()
            )
        }
    }

    fun searchMovie(keywords: String) {

        viewModelScope.launch {
            searchRepository.searchMovie(
                mapOf(
                    "keyword" to keywords,
                    "page" to "1",
                    "size" to "20"
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
                                movieList = data?.data?.list!!,
                                movieLoadMoreState = LoadMoreState.Idle,
                                articleLoadMoreState = LoadMoreState.Idle,
                                actorLoadMoreState = LoadMoreState.Idle,
                                moviePagination = data.data.pagination
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
                                movieList = emptyList(),
                                moviePagination = null
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
                                movieAdultPageState = PageState.Success,
                                articlePageState = PageState.Loading,
                                actorPageState = PageState.Loading,
                                movieAdultList = data?.data?.list!!,
                                movieLoadMoreState = LoadMoreState.Idle,
                                articleLoadMoreState = LoadMoreState.Idle,
                                actorLoadMoreState = LoadMoreState.Idle,
                                movieAdultPagination = data.data.pagination
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
                                movieAdultList = emptyList(),
                                movieAdultPagination = null
                            )
                        }
                    }
                }


        }

    }


    fun loadMoreMovies() {

        val pagination = _uiState.value.moviePagination ?: return

        /*
         * 防止用户快速滑动时重复请求同一页。
         */
        if (_uiState.value.movieLoadMoreState == LoadMoreState.Loading) return

        /*
         * 当前数量已经达到接口返回的总数量，没有下一页了。
         */
        if (_uiState.value.movieList.size >= pagination.total!!) {
            _uiState.value.movieLoadMoreState = LoadMoreState.NoMore
            return
        }

        val keyword = _uiState.value.keywords.trim()
        if (keyword.isBlank()) return

        val nextPage = pagination.next

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    movieLoadMoreState = LoadMoreState.Loading,
                )
            }

            searchRepository.searchMovie(
                mapOf(
                    "keyword" to keyword,
                    "page" to nextPage.toString(),
                    "size" to "20"
                )
            ).asResult().collectLatest { result ->
                if (result.isSuccess) {

                    val data = result.getOrNull()

                    /*
                    * 使用旧列表加新列表。
                    * distinctBy 防止接口分页数据偶尔重复 。
                    */
                    val newList = (
                            _uiState.value.movieList + data?.data?.list!!
                            ).distinctBy { movie ->
                            movie.vodId
                        }

                    _uiState.update {
                        it.copy(
                            movieList = newList,
                            moviePagination = data.data.pagination,
                            movieLoadMoreState = LoadMoreState.Success,
                        )
                    }

                    _uiState.update {
                        it.copy(
                            movieLoadMoreState = LoadMoreState.Idle,
                        )
                    }

                } else {
                    val throwable  = result.exceptionOrNull()!!
                    _uiState.update {
                        it.copy(
                            movieLoadMoreState = LoadMoreState.Error(throwable = throwable ),
                        )
                    }
                }


            }

        }
    }
}