package com.balon.blmovie.feature.search

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.balon.blmovie.core.model.MovieAdult
import com.balon.blmovie.core.model.PageState
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.core.model.response.NetworkPageMeta
import com.balon.blmovie.feature.search.component.MoviePage
import com.balon.blmovie.feature.search.component.SearchHistoryContent
import com.balon.blmovie.feature.search.component.SearchInputBox
import com.balon.blmovie.feature.search.component.SearchResultMovieAdultItem
import com.balon.blmovie.feature.search.component.SearchResultTabRow
import com.balon.blmovie.feature.search.component.SearchSuggestContent
import com.hjq.toast.Toaster
import com.quick.app.core.extension.clickableNoRipple
import kotlinx.coroutines.launch

@Composable
fun SearchRoute(
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel(),
    onMovieItemClick: (Movie) -> Unit,
    onMovieAdultItemClick: (MovieAdult) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onBack = onBack,
        onSearch = { keyword ->
            if (keyword.startsWith("1b")) {
                viewModel.searchMovieAdult(
                    keyword.removePrefix("1b")
                )
            } else {
                viewModel.searchMovie(keyword)
            }
        },
        onMovieItemClick = onMovieItemClick,
        onMovieAdultItemClick = onMovieAdultItemClick,
        onMovieLoadMore = viewModel::loadMoreMovies,
        onMovieRetryLoadMore = viewModel::loadMoreMovies
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onBack: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onMovieItemClick: (Movie) -> Unit = {},
    onMovieAdultItemClick: (MovieAdult) -> Unit = {},
    onMovieLoadMore: () -> Unit = {},
    onMovieRetryLoadMore: () -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    /*
     * keyword 是输入框现在显示的内容。
     */
    var keyword by rememberSaveable {
        mutableStateOf("")
    }

    /*
     * searchedKeyword 保存最近一次真正执行搜索的关键词。
     *
     * 从建议页返回时，用它恢复上一次结果页。
     * 用户在建议页继续输入时，不要把它清空。
     */
    var searchedKeyword by rememberSaveable {
        mutableStateOf("")
    }

    /*
     * 页面状态单独保存。
     *
     * 不要再根据 searchedKeyword 和焦点状态在每次重组时重新计算，
     * 否则 Suggest 会立刻被 Result 覆盖。
     */
    var pageState by rememberSaveable {
        mutableStateOf(SearchPageState.History)
    }
    var suggestBackTarget by rememberSaveable {
        mutableStateOf(SearchPageState.History)
    }


    val historyList = remember {
        mutableStateListOf(
            "666",
            "南京工业职业技术大学",
            "南京工业大学",
            "赣南师范大学",
            "动漫"
        )
    }

    val hotKeywords = remember {
        listOf(
            "曜无双优化",
            "哈兰德模仿大赛",
            "苏州职业技术大学",
            "世界杯小报",
            "gpt image 2",
            "flutter插件"
        )
    }

    val suggestList = remember(keyword) {
        if (keyword.isBlank()) {
            emptyList()
        } else {
            listOf(
                "${keyword}信谬",
                "${keyword}什么意思啊",
                "${keyword}ipx",
                "${keyword}件可写的事",
                "${keyword}海航随心飞",
                "${keyword}抢先资源",
                "${keyword}路公交车海昏侯",
                "${keyword}怎么回复",
                "${keyword}天使数字"
            )
        }
    }

    fun doSearch() {
        val realKeyword = keyword.trim()

        if (realKeyword.isBlank()) {
            Toaster.show("输入框为空")
            return
        }

        searchedKeyword = realKeyword
        keyword = realKeyword

        pageState = SearchPageState.Result
        suggestBackTarget = SearchPageState.Result

        focusManager.clearFocus(force = true)

        if (historyList.none { it == realKeyword }) {
            historyList.add(0, realKeyword)
        }
        uiState.keywords = realKeyword
        onSearch(realKeyword)
    }

    fun handleBack() {
        when (pageState) {
            SearchPageState.Suggest -> {
                focusManager.clearFocus(force = true)

                when (suggestBackTarget) {
                    SearchPageState.Result -> {
                        // 从 Result 点击输入框进入 Suggest
                        // 返回时恢复上一次搜索结果
                        keyword = searchedKeyword
                        pageState = SearchPageState.Result
                    }

                    SearchPageState.History,
                    SearchPageState.Suggest -> {
                        // 从 History 进入 Suggest
                        // 返回时回到 History
                        keyword = ""
                        pageState = SearchPageState.History
                    }
                }
            }

            SearchPageState.Result -> {
                /*
                 * Result 返回时不退出搜索页，
                 * 而是回到搜索历史页面。
                 */
                focusManager.clearFocus(force = true)
                keyword = ""
                pageState = SearchPageState.History
                suggestBackTarget = SearchPageState.History
            }

            SearchPageState.History -> {
                /*
                 * History 再返回，才退出搜索页回到主页。
                 */
                focusManager.clearFocus(force = true)
                onBack()
            }
        }
    }

    /*
     * 系统返回手势和顶部返回按钮使用同一套逻辑。
     */
    BackHandler {
        handleBack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                navigationIcon = {
                    Icon(
                        imageVector = Icons.Default.ArrowBackIosNew,
                        contentDescription = "返回",
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier
                            .padding(start = 12.dp)
                            .size(22.dp)
                            .clickableNoRipple {
                                handleBack()
                            }
                    )
                },
                title = {
                    SearchInputBox(
                        keyword = keyword,

                        /*
                         * Result 页面不显示右侧叉号。
                         */
                        showClearButton =
                            pageState != SearchPageState.Result,

                        onKeywordChange = { value ->
                            keyword = value

                            /*
                             * 不要清空 searchedKeyword。
                             * 否则建议页返回时无法恢复上一次 Result。
                             */
                        },

                        onFocusChange = { focused ->
                            if (focused) {
                                when (pageState) {
                                    SearchPageState.Result -> {
                                        suggestBackTarget = SearchPageState.Result
                                        pageState = SearchPageState.Suggest
                                    }

                                    SearchPageState.History -> {
                                        suggestBackTarget = SearchPageState.History
                                        pageState = SearchPageState.Suggest
                                    }

                                    SearchPageState.Suggest -> Unit
                                }
                            }
                        },

                        onClearClick = {
                            keyword = ""
                            pageState = SearchPageState.Suggest
                        },

                        onSearch = {
                            doSearch()
                        }
                    )
                },
                actions = {
                    TextButton(
                        enabled =
                            pageState == SearchPageState.Suggest,
                        onClick = {
                            doSearch()
                        }
                    ) {
                        Text(
                            text = "搜索",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            )
        },
        //排除底部导航栏边距
        contentWindowInsets = ScaffoldDefaults
            .contentWindowInsets
            .exclude(WindowInsets.navigationBars)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
        ) {
            when (pageState) {
                SearchPageState.History -> {
                    SearchHistoryContent(
                        historyList = historyList,
                        hotKeywords = hotKeywords,
                        onKeywordClick = { value ->
                            keyword = value
                            doSearch()
                        },
                        onClearHistoryClick = {
                            historyList.clear()
                        }
                    )
                }

                SearchPageState.Suggest -> {
                    SearchSuggestContent(
                        keyword = keyword,
                        suggestList = suggestList,
                        onSuggestClick = { value ->
                            keyword = value
                            doSearch()
                        }
                    )
                }

                SearchPageState.Result -> {
                    SearchResultContent(
                        uiState = uiState,
                        keyword = searchedKeyword,
                        movieList = uiState.movieList,
                        movieAdultList = uiState.movieAdultList,
                        onMovieItemClick = onMovieItemClick,
                        onMovieAdultItemClick = onMovieAdultItemClick,
                        onMovieLoadMore = onMovieLoadMore,
                        onMovieRetryLoadMore = onMovieRetryLoadMore
                    )
                }
            }
        }
    }
}

/**
 * 搜索结果内容。
 */
@Composable
private fun SearchResultContent(
    uiState: SearchUiState,
    keyword: String,
    movieList: List<Movie>,
    movieAdultList: List<MovieAdult>,
    onMovieItemClick: (Movie) -> Unit,
    onMovieAdultItemClick: (MovieAdult) -> Unit,
    onMovieLoadMore: () -> Unit,
    onMovieRetryLoadMore: () -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            3
        }
    )

    val coroutineScope = rememberCoroutineScope()

    val totalCount =
        uiState.moviePagination?.total
            ?: uiState.movieAdultPagination?.total
            ?: 0

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchResultTabRow(
            pagerState = pagerState,
            totalCount = totalCount,
            onTabClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> {
                    val pagination = uiState.moviePagination

                    if (pagination == null) {
                        SearchLoadingPage()
                    } else {
                        if (uiState.moviePageState == PageState.Success) {
                            MoviePage(
                                uiState = uiState,
                                keyword = keyword,
                                pagination = pagination,
                                list = movieList,
                                onMovieItemClick = onMovieItemClick,
                                onLoadMore = onMovieLoadMore,
                                onRetryLoadMore = onMovieRetryLoadMore
                            )
                        }
                    }
                }

                1 -> {
                    SearchEmptyPage(
                        text = "这里显示文章搜索结果"
                    )
                }

                2 -> {
                    val pagination = uiState.movieAdultPagination

                    if (pagination == null) {
                        SearchLoadingPage()
                    } else {
                        MovieAdultPage(
                            keyword = keyword,
                            pagination = pagination,
                            list = movieAdultList,
                            onMovieAdultItemClick =
                                onMovieAdultItemClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchResultHeader(
    keyword: String,
    pagination: NetworkPageMeta
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.outline,
            modifier = Modifier.size(18.dp)
        )

        Text(
            text = "当前搜索：",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
                .copy(alpha = 0.65f),
            modifier = Modifier.padding(start = 8.dp)
        )

        Text(
            text = keyword,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Box(
            modifier = Modifier.weight(1f)
        )

        Text(
            text = "共找到 ",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
                .copy(alpha = 0.75f)
        )

        Text(
            text = pagination.total.toString(),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = " 条数据",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
                .copy(alpha = 0.75f)
        )
    }
}

@Composable
fun MovieAdultPage(
    keyword: String,
    pagination: NetworkPageMeta,
    list: List<MovieAdult>,
    onMovieAdultItemClick: (MovieAdult) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchResultHeader(
            keyword = keyword,
            pagination = pagination
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(
                start = 12.dp,
                end = 12.dp,
                top = 4.dp,
                bottom = 72.dp
            ),
            horizontalArrangement =
                Arrangement.spacedBy(10.dp),
            verticalArrangement =
                Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = list,
                key = { movie ->
                    movie.vodId
                }
            ) { movie ->
                SearchResultMovieAdultItem(
                    item = movie,
                    onMovieAdultItemClick =
                        onMovieAdultItemClick
                )
            }
        }
    }
}

@Composable
private fun SearchLoadingPage() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(28.dp),
            strokeWidth = 2.dp
        )
    }
}

@Composable
private fun SearchEmptyPage(
    text: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.outline
        )
    }
}
