package com.balon.blmovie.feature.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Search
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
import com.balon.blmovie.core.model.response.Movie
import com.balon.blmovie.feature.search.component.SearchHistoryContent
import com.balon.blmovie.feature.search.component.SearchInputBox
import com.balon.blmovie.feature.search.component.SearchResultMovieAdultItem
import com.balon.blmovie.feature.search.component.SearchResultMovieItem
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
    onMovieAdultItemClick:(MovieAdult) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    SearchScreen(
        uiState = uiState,
        onBack = onBack,
        onSearch = { it ->
            if (it.startsWith("1b")) {
                viewModel.searchMovieAdult(it.replace("1b", ""))
            } else {
                viewModel.searchMovie(it)
            }

        },
        onMovieItemClick = onMovieItemClick,
        onMovieAdultItemClick = onMovieAdultItemClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    uiState: SearchUiState,
    onBack: () -> Unit = {},
    onSearch: (String) -> Unit = {},
    onMovieItemClick: (Movie) -> Unit = {},
    onMovieAdultItemClick: (MovieAdult) -> Unit = {}
) {
    val focusManager = LocalFocusManager.current

    var keyword by rememberSaveable {
        mutableStateOf("")
    }

    var searchedKeyword by rememberSaveable {
        mutableStateOf("")
    }

    var isInputFocused by rememberSaveable {
        mutableStateOf(false)
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

    uiState.realUiState = when {
        searchedKeyword.isNotBlank() -> SearchPageState.Result
        isInputFocused -> SearchPageState.Suggest
        else -> SearchPageState.History
    }

    fun doSearch() {
        val realKeyword = keyword.trim()
        if (realKeyword.isBlank()) {
            Toaster.show("输入框为空")
        }

        searchedKeyword = realKeyword
        isInputFocused = false
        focusManager.clearFocus()

        if (historyList.none { it == realKeyword }) {
            historyList.add(0, realKeyword)
        }

        onSearch(realKeyword)
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
                                onBack()
                            }
                    )
                },
                title = {
                    SearchInputBox(
                        keyword = keyword,
                        onKeywordChange = {
                            keyword = it
                            searchedKeyword = ""
                        },
                        onFocusChange = { focused ->
                            isInputFocused = focused
                        },
                        onClearClick = {
                            keyword = ""
                            searchedKeyword = ""
                        },
                        onSearch = {
                            doSearch()
                        }
                    )
                },
                actions = {
                    TextButton(
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
            when (uiState.realUiState) {
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
                        keyword = searchedKeyword,
                        movieList = uiState.movieList,
                        movieAdultList = uiState.movieAdultList,
                        onMovieItemClick = onMovieItemClick,
                        onMovieAdultItemClick = onMovieAdultItemClick
                    )
                }
            }
        }
    }
}


/**
 * 搜索结果内容
 */
@Composable
private fun SearchResultContent(
    keyword: String,
    movieList: List<Movie>,
    movieAdultList:List<MovieAdult>,
    onMovieItemClick: (Movie) -> Unit,
    onMovieAdultItemClick: (MovieAdult) -> Unit
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            3
        }
    )

    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SearchResultTabRow(
            pagerState = pagerState,
            totalCount = 5,
            onTabClick = { index ->
                coroutineScope.launch {
                    pagerState.animateScrollToPage(index)
                }
            }
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 8.dp),
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
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.65f),
                modifier = Modifier.padding(start = 8.dp)
            )

            Text(
                text = keyword,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> {
                    MoviePage(movieList, onMovieItemClick = onMovieItemClick)
                }

                1 -> {
                    SearchEmptyPage(
                        text = "这里显示文章搜索结果"
                    )
                }

                2 -> {
                    MovieAdultPage(movieAdultList, onMovieAdultItemClick = onMovieAdultItemClick)
                }
            }
        }
    }
}

@Composable
fun MoviePage(
    list: List<Movie>,
    onMovieItemClick: (Movie) -> Unit
) {
    if (list == null) return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    ) {
        items(list) { item ->
            SearchResultMovieItem(
                item = item,
                modifier = Modifier.padding(bottom = 18.dp),
                onMovieItemClick = onMovieItemClick
            )
        }
    }
}

@Composable
fun MovieAdultPage(
    list: List<MovieAdult>,
    onMovieAdultItemClick: (MovieAdult) -> Unit
) {
    if (list == null) return

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 16.dp,
            vertical = 12.dp
        )
    ) {
        items(list) { item ->
            SearchResultMovieAdultItem(
                item = item,
                modifier = Modifier.padding(bottom = 18.dp),
                onMovieAdultItemClick = onMovieAdultItemClick
            )
        }
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
