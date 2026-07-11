package com.balon.blmovie.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.SearchRepository
import com.balon.blmovie.core.extension.asResult
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.response.NetworkPageMeta
import com.balon.blmovie.util.MMKVUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.emptyList
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
    private val mmkv: MMKVUtil,
) : ViewModel() {
    private val suggestKeywordFlow = MutableStateFlow("")
    private val _uiState = MutableStateFlow(
        SearchUiState(
            keywords = "", searchHistory =
                mmkv.getHistory()
        )
    )
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
        observeMovieSuggest()
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
                    "size" to "30"
                )
            ).asResult()
                .collectLatest { result ->
                    if (result.isSuccess) {
                        val data = result.getOrNull()
                        _uiState.update {
                            it.copy(
                                moviePageState = PageState.Success,
                                movieList = data?.data?.list.orEmpty(),
                                movieLoadMoreState = LoadMoreState.Idle,
                                moviePagination = data?.data?.pagination
                            )
                        }
                    } else {
                        val throwable = result.exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                moviePageState = PageState.Error(
                                    throwable = throwable!!
                                ),
                                movieList = emptyList(),
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
                                movieAdultList = data?.data?.list.orEmpty(),
                                movieAdultLoadMoreState = LoadMoreState.Idle,
                                movieAdultPagination = data?.data?.pagination
                            )
                        }
                    } else {
                        val throwable = result.exceptionOrNull()
                        _uiState.update {
                            it.copy(
                                moviePageState = PageState.Error(
                                    throwable = throwable!!
                                ),
                                movieAdultList = emptyList(),
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
                    val throwable = result.exceptionOrNull()!!
                    _uiState.update {
                        it.copy(
                            movieLoadMoreState = LoadMoreState.Error(throwable = throwable),
                        )
                    }
                }


            }

        }
    }

    fun loadMoreAdultMovies() {

        val pagination = _uiState.value.movieAdultPagination ?: return

        /*
         * 防止用户快速滑动时重复请求同一页。
         */
        if (_uiState.value.movieAdultLoadMoreState == LoadMoreState.Loading) return

        /*
         * 当前数量已经达到接口返回的总数量，没有下一页了。
         */
        if (_uiState.value.movieAdultList.size >= pagination.total!!) {
            _uiState.value.movieAdultLoadMoreState = LoadMoreState.NoMore
            return
        }

        val keyword = _uiState.value.keywords.trim()
        if (keyword.isBlank()) return

        val nextPage = pagination.next

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    movieAdultLoadMoreState = LoadMoreState.Loading,
                )
            }

            searchRepository.searchMovieAdult(
                mapOf(
                    "keyword" to keyword.replace("1b",""),
                    "page" to nextPage.toString(),
                    "size" to "30"
                )
            ).asResult().collectLatest { result ->
                if (result.isSuccess) {

                    val data = result.getOrNull()

                    /*
                    * 使用旧列表加新列表。
                    * distinctBy 防止接口分页数据偶尔重复 。
                    */
                    val newList = (
                            _uiState.value.movieAdultList + data?.data?.list!!
                            ).distinctBy { movie ->
                            movie.vodId
                        }

                    _uiState.update {
                        it.copy(
                            movieAdultList = newList,
                            movieAdultPagination = data.data.pagination,
                            movieAdultLoadMoreState = LoadMoreState.Success,
                        )
                    }

                    _uiState.update {
                        it.copy(
                            movieAdultLoadMoreState = LoadMoreState.Idle,
                        )
                    }

                } else {
                    val throwable = result.exceptionOrNull()!!
                    _uiState.update {
                        it.copy(
                            movieAdultLoadMoreState = LoadMoreState.Error(throwable = throwable),
                        )
                    }
                }


            }

        }
    }

    /**
     * 输入框内容改变时调用。
     *
     * 这里不直接请求接口，只更新关键词。
     */
    fun searchMovieSuggest(keyword: String) {
        suggestKeywordFlow.value = keyword
    }

    /**
     * 监听搜索词，防抖后请求联想数据。
     */
    @OptIn(FlowPreview::class)
    fun observeMovieSuggest() {
        viewModelScope.launch {
            suggestKeywordFlow
                // 去掉首尾空格
                .map { keyword ->
                    keyword.trim()
                }

                // 用户停止输入 400ms 后才继续执行
                .debounce(400L.milliseconds)

                // 内容和上一次相同时不重复请求
                .distinctUntilChanged()

                /*
                 * 新关键词到来时，会取消上一次 collectLatest 代码块，
                 * 包括上一次还没完成的网络请求收集。
                 */
                .collectLatest { keyword ->
                    if (keyword.isBlank()) {
                        _uiState.update {
                            it.copy(
                                searchSuggest = emptyList()
                            )
                        }

                        return@collectLatest
                    }

                    searchRepository
                        .searchMovieSuggest(keyword)
                        .asResult()
                        .collectLatest { result ->
                            if (result.isSuccess) {
                                val suggestList =
                                    result.getOrNull()?.data.orEmpty()

                                _uiState.update {
                                    it.copy(
                                        searchSuggest = suggestList
                                    )
                                }
                            } else {
                                /*
                                 * 请求失败时先清空联想结果。
                                 * 也可以改成保留上一次的数据。
                                 */
                                _uiState.update {
                                    it.copy(
                                        searchSuggest = emptyList()
                                    )
                                }
                            }
                        }
                }
        }
    }



    /**
     * 用户真正执行搜索后调用。
     */
    fun addSearchHistory(
        keyword: String
    ) {
        val newHistory =
            mmkv.addHistory(keyword)

        _uiState.update {
            it.copy(
                searchHistory = newHistory
            )
        }
    }

    fun clearSearchHistory() {
        mmkv.clearHistory()

        _uiState.update {
            it.copy(
                searchHistory = emptyList()
            )
        }
    }

}