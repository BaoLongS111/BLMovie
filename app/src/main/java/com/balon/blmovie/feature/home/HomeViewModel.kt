package com.balon.blmovie.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.MovieRepository
import com.balon.blmovie.core.extension.asResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

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

            // 设置第一次加载为True
            _uiState.value = _uiState.value.copy(
                isFirstLoading = true,
                pageMeta = null,
                refreshState = RefreshState.IDLE,
                loadMoreState = LoadMoreState.IDLE,
                errorMessage = null,
            )

            loadBanners()


        }
    }

    /**
     * 加载轮播图
     */
    private suspend fun loadBanners() {
        movieRepository.banners()
            .asResult()
            .collectLatest { result ->
                if (result.isSuccess) {
                    // 请求成功
                    _uiState.update {
                        it.copy(
                            banners = result.getOrThrow().data!!
                        )
                    }
                } else {
                    // 请求失败
                    val throwable = result.exceptionOrNull()!!
                    Timber.e(throwable, "加载轮播图失败")
                }

            }
    }
}