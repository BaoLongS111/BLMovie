package com.balon.blmovie.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.balon.blmovie.core.data.repository.HomeRepository
import com.balon.blmovie.core.exception.CommonException
import com.balon.blmovie.core.extension.asResult
import com.balon.blmovie.core.model.LoadMoreState
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.RefreshState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    private val homeRepository: HomeRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private var startTime: Long = 0

    init {
        loadData()
    }

    /**
     * 第一次加载首页
     */
    private fun loadData() {
        startTime = System.currentTimeMillis()
        viewModelScope.launch {

            // 设置第一次加载状态
            _uiState.update {
                it.copy(
                    pageState = PageState.Loading,
                    pageMeta = null,
                    refreshState = RefreshState.IDLE,
                    loadMoreState = LoadMoreState.Idle
                )
            }

            // 创建异步任务
            val bannerJob = async {
                homeRepository.banners().asResult().first()
            }
            val homeDataJob = async { homeRepository.homeData().asResult().first() }
            val articleListJob = async {
                homeRepository.articleList(
                    mapOf(
                        "page" to "1", "size" to "20"
                    )
                ).asResult().first()
            }

            // 等待所有作业完成
            val bannerResult = bannerJob.await()
            val homeDataResult = homeDataJob.await()
            val articleListResult = articleListJob.await()

            // 最少要有1S的延迟
            val endTime = System.currentTimeMillis()
            val consume = endTime - startTime
            if (consume < EXPECTED_COMSUME_TIME) {
                val delayTimeMillis = EXPECTED_COMSUME_TIME - consume
                Timber.d("loadData delay %d", delayTimeMillis)
                delay(delayTimeMillis)
            }

            if (bannerResult.isFailure || homeDataResult.isFailure || articleListResult.isFailure) {
                //发生异常了，例如：网络错误，解析数据失败等
                val throwable = bannerResult.exceptionOrNull() ?: homeDataResult.exceptionOrNull()
                ?: articleListResult.exceptionOrNull()
                _uiState.update {
                    it.copy(
                        pageState = PageState.Error(
                            throwable = throwable!!
                        )
                    )
                }
                return@launch
            }

            //都请求成功

            //判断业务是否请求成功
            val bannerResultData = bannerResult.getOrNull()!!
            val homeResultData = homeDataResult.getOrNull()!!
            val articleListResultData = articleListResult.getOrNull()!!

            if (!bannerResultData.isSucceed) {
                //业务失败
                _uiState.update {
                    it.copy(
                        pageState = PageState.Error(
                            throwable = CommonException(bannerResultData)
                        )
                    )
                }
                return@launch
            }

            if (!homeResultData.isSucceed) {
                //业务失败
                _uiState.update {
                    it.copy(
                        pageState = PageState.Error(
                            throwable = CommonException(homeResultData)
                        )
                    )
                }
                return@launch
            }

            //所有请求都业务成功
            _uiState.update {
                it.copy(
                    pageState = PageState.Success,
                    banners = bannerResultData.data ?: emptyList(),
                    homeData = homeResultData.data,
                    articleList = articleListResultData.data?.list ?: emptyList(),
                    refreshState = RefreshState.IDLE,
                    loadMoreState = LoadMoreState.Idle,
                )
            }

        }
    }


    fun reload() {

    }

    fun refresh() {
        val currentState = _uiState.value

        // 如果正在刷新，就不要重复刷新
        if (currentState.refreshState == RefreshState.REFRESHING) {
            return
        }

        viewModelScope.launch {
            val startTime = System.currentTimeMillis()

            // 设置刷新中状态
            _uiState.update {
                it.copy(
                    refreshState = RefreshState.REFRESHING,
                )
            }

            // 并行请求 banner 和 category
            val bannerJob = async {
                homeRepository.banners()
                    .asResult()
                    .first()
            }
            val homeDataJob = async { homeRepository.homeData().asResult().first() }
            val articleListJob = async {
                homeRepository.articleList(
                    mapOf(
                        "page" to "1", "size" to "20"
                    )
                ).asResult().first()
            }

            val bannerResult = bannerJob.await()
            val homeDataResult = homeDataJob.await()
            val articleListResult = articleListJob.await()

            // 最少显示一段刷新动画，避免刷新太快一闪而过
            val endTime = System.currentTimeMillis()
            val consume = endTime - startTime

            if (consume < EXPECTED_COMSUME_TIME) {
                val delayTimeMillis = EXPECTED_COMSUME_TIME - consume
                Timber.d("refresh delay %d", delayTimeMillis)
                delay(delayTimeMillis.milliseconds)
            }

            // 只要有一个失败，就认为刷新失败
            if (bannerResult.isFailure || homeDataResult.isFailure || articleListResult.isFailure) {
                val throwable =
                    bannerResult.exceptionOrNull()
                        ?: homeDataResult.exceptionOrNull() ?: articleListResult.exceptionOrNull()

                _uiState.update {
                    it.copy(
                        refreshState = RefreshState.ERROR
                    )
                }
                return@launch
            }
            //都请求成功

            // 判断业务是否成功
            val bannerResultData = bannerResult.getOrNull()!!
            val homeResultData = homeDataResult.getOrNull()!!
            val articleListResultData = articleListResult.getOrNull()!!

            if (!bannerResultData.isSucceed) {
                //业务失败
                // 相当于AsResult里没走这里
                // if (it.isSucceed) {
                //            Result.success(it)
                //        }
                //走了Result.failure(CommonException(it))这里，但是上面好像拦截了这个错误
                _uiState.update {
                    it.copy(
                        pageState = PageState.Error(
                            throwable = CommonException(bannerResultData)
                        )
                    )
                }
                return@launch
            }

            if (!homeResultData.isSucceed) {
                //业务失败
                _uiState.update {
                    it.copy(
                        pageState = PageState.Error(
                            throwable = CommonException(homeResultData)
                        )
                    )
                }
                return@launch
            }
            // 全部成功，更新数据
            _uiState.update {
                it.copy(
                    banners = bannerResultData.data ?: emptyList(),
                    homeData = homeResultData.data,
                    articleList = articleListResultData.data?.list ?: emptyList(),
                    refreshState = RefreshState.SUCCESS,
                )
            }

        }
    }

    fun loadMore() {

    }

    fun retry() {

        loadData()


    }

    fun clearSuccess() {
        _uiState.update {
            if (it.refreshState == RefreshState.SUCCESS) {
                it.copy(
                    refreshState = RefreshState.IDLE,
                )
            } else {
                it
            }
        }
    }

    fun clearError() {
        _uiState.update {
            if (it.refreshState == RefreshState.ERROR) {
                it.copy(
                    refreshState = RefreshState.IDLE,
                )
            } else {
                it
            }
        }
    }

    companion object {
        const val EXPECTED_COMSUME_TIME = 1500
    }
}