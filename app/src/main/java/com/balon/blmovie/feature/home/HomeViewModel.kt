package com.balon.blmovie.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.MovieRepository
import com.balon.blmovie.core.extension.asResult
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

/**
 * 主页VM
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val movieRepository: MovieRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadData()
    }

    /**
     * 第一次加载首页
     */
    private fun loadData() {
        viewModelScope.launch {

            // 设置第一次加载状态
            _uiState.update {
                it.copy(
                    pageState = PageState.Loading,
                    pageMeta = null,
                    refreshState = RefreshState.IDLE,
                    loadMoreState = LoadMoreState.IDLE
                )
            }

            loadBanners()

        }
    }

    /**
     * 加载轮播图
     */
    private suspend fun loadBanners() {
        delay(2000L.milliseconds)
        movieRepository.banners()
            .asResult()
            .collectLatest { result ->
                if (result.isSuccess) {
                    // 请求成功
                    _uiState.update {
                        it.copy(
                            pageState = PageState.Success,
                            banners = result.getOrThrow().data!!,
                            refreshState = RefreshState.IDLE,
                            loadMoreState = LoadMoreState.IDLE,
                        )
                    }
                } else {
                    // 请求失败
                    val throwable = result.exceptionOrNull()!!
                    _uiState.update {
                        it.copy(
                            pageState = PageState.Error(
                                throwable = throwable
                            ),
                            refreshState = RefreshState.ERROR,
                            loadMoreState = LoadMoreState.ERROR,
                        )
                    }
                }

            }
    }

    fun reload() {

    }

    fun refresh() {
        val currentState = _uiState.value

        if (currentState.refreshState == RefreshState.REFRESHING) return

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    refreshState = RefreshState.REFRESHING,
                )
            }

            loadBanners()

            _uiState.update {
                it.copy(
                    refreshState = RefreshState.SUCCESS,
                )
            }
            Timber.d("首页刷新成功")
        }
    }

    fun loadMore() {

    }

    fun retry() {

        loadData()


    }


}