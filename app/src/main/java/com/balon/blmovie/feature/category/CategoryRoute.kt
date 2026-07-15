package com.balon.blmovie.feature.category

import androidx.compose.material3.Text
import androidx.compose.ui.tooling.preview.Preview
import com.balon.blmovie.core.design.theme.BLMovieTheme
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.balon.blmovie.R
import com.balon.blmovie.core.design.theme.BLMovieTheme

/**
 * 分类页面临时展示数据。
 *
 * 后续接入接口后，可以替换成项目里的 Movie。
 */
data class CategoryMovieUi(
    val id: Long,
    val name: String,
    val posterUrl: String,
    val score: String,
    val badge: String = ""
)

private val topTabs = listOf(
    "电影",
    "剧集",
    "综艺",
    "动漫"
)

private val typeOptions = listOf(
    "全部",
    "动作",
    "喜剧",
    "爱情",
    "科幻",
    "悬疑"
)

private val areaOptions = listOf(
    "全部",
    "中国大陆",
    "中国香港",
    "美国",
    "日本",
    "韩国"
)

private val yearOptions = listOf(
    "全部",
    "2024",
    "2023",
    "2022",
    "2021",
    "2020"
)

private val sortOptions = listOf(
    "热门",
    "最新",
    "评分",
    "高分",
    "票房"
)

/**
 * 暂时写死的九条电影数据。
 *
 * posterUrl 目前为空，会显示 placeholder。
 * 后续换成真实海报地址即可。
 */
private val categoryMovies = listOf(
    CategoryMovieUi(
        id = 1,
        name = "谈判专家",
        posterUrl = "",
        score = "8.6",
        badge = "热播"
    ),
    CategoryMovieUi(
        id = 2,
        name = "流浪地球 2",
        posterUrl = "",
        score = "8.3"
    ),
    CategoryMovieUi(
        id = 3,
        name = "第二十条",
        posterUrl = "",
        score = "8.1"
    ),
    CategoryMovieUi(
        id = 4,
        name = "悟空传",
        posterUrl = "",
        score = "7.8",
        badge = "VIP"
    ),
    CategoryMovieUi(
        id = 5,
        name = "熊出没·逆转时空",
        posterUrl = "",
        score = "7.5",
        badge = "独播"
    ),
    CategoryMovieUi(
        id = 6,
        name = "无名之辈",
        posterUrl = "",
        score = "7.4"
    ),
    CategoryMovieUi(
        id = 7,
        name = "飞驰人生 2",
        posterUrl = "",
        score = "8.0",
        badge = "热播"
    ),
    CategoryMovieUi(
        id = 8,
        name = "不能错过的只有你",
        posterUrl = "",
        score = "7.6"
    ),
    CategoryMovieUi(
        id = 9,
        name = "河边的错误",
        posterUrl = "",
        score = "7.2"
    )
)

@Composable
fun CategoryRoute(
    onSearchClick: () -> Unit = {},
    onMovieClick: (CategoryMovieUi) -> Unit = {}
) {
    CategoryScreen(
        onSearchClick = onSearchClick,
        onMovieClick = onMovieClick
    )
}

@Composable
fun CategoryScreen(
    modifier: Modifier = Modifier,
    movies: List<CategoryMovieUi> = categoryMovies,
    onSearchClick: () -> Unit = {},
    onMovieClick: (CategoryMovieUi) -> Unit = {}
) {
    var selectedTopTab by rememberSaveable {
        mutableStateOf(0)
    }

    var selectedType by rememberSaveable {
        mutableStateOf("全部")
    }

    var selectedArea by rememberSaveable {
        mutableStateOf("全部")
    }

    var selectedYear by rememberSaveable {
        mutableStateOf("全部")
    }

    var selectedSort by rememberSaveable {
        mutableStateOf("热门")
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .statusBarsPadding()
    ) {
        CategoryTopBar(
            tabs = topTabs,
            selectedIndex = selectedTopTab,
            onTabClick = {
                selectedTopTab = it
            },
            onSearchClick = onSearchClick
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 16.dp,
                    end = 12.dp,
                    top = 4.dp,
                    bottom = 8.dp
                ),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            CategoryFilterRow(
                title = "类型",
                options = typeOptions,
                selectedOption = selectedType,
                onOptionClick = {
                    selectedType = it
                }
            )

            CategoryFilterRow(
                title = "地区",
                options = areaOptions,
                selectedOption = selectedArea,
                onOptionClick = {
                    selectedArea = it
                }
            )

            CategoryFilterRow(
                title = "年份",
                options = yearOptions,
                selectedOption = selectedYear,
                onOptionClick = {
                    selectedYear = it
                }
            )

            CategoryFilterRow(
                title = "排序",
                options = sortOptions,
                selectedOption = selectedSort,
                onOptionClick = {
                    selectedSort = it
                }
            )
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 24.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(
                items = movies,
                key = { movie ->
                    movie.id
                }
            ) { movie ->
                CategoryMovieItem(
                    movie = movie,
                    onClick = {
                        onMovieClick(movie)
                    }
                )
            }
        }
    }
}

/**
 * 顶部电影、剧集、综艺、动漫切换栏。
 */
@Composable
private fun CategoryTopBar(
    tabs: List<String>,
    selectedIndex: Int,
    onTabClick: (Int) -> Unit,
    onSearchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .padding(
                start = 14.dp,
                end = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        tabs.forEachIndexed { index, title ->
            val selected = selectedIndex == index

            Box(
                modifier = Modifier
                    .width(58.dp)
                    .fillMaxHeight()
                    .clickable {
                        onTabClick(index)
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    style = if (selected) {
                        MaterialTheme.typography.titleLarge
                    } else {
                        MaterialTheme.typography.bodyLarge
                    },
                    color = if (selected) {
                        MaterialTheme.colorScheme.onBackground
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = if (selected) {
                        FontWeight.Bold
                    } else {
                        FontWeight.Medium
                    }
                )

                if (selected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                            .width(22.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onSearchClick
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "搜索",
                tint = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.size(25.dp)
            )
        }
    }
}

/**
 * 单行筛选区域。
 */
@Composable
private fun CategoryFilterRow(
    title: String,
    options: List<String>,
    selectedOption: String,
    onOptionClick: (String) -> Unit,
    onExpandClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier.width(44.dp),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .horizontalScroll(
                    state = rememberScrollState()
                ),
            horizontalArrangement = Arrangement.spacedBy(7.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            options.forEach { option ->
                CategoryFilterChip(
                    text = option,
                    selected = selectedOption == option,
                    onClick = {
                        onOptionClick(option)
                    }
                )
            }
        }

        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "展开$title",
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier
                .padding(start = 3.dp)
                .size(21.dp)
                .clickable {
                    onExpandClick()
                }
        )
    }
}

/**
 * 筛选胶囊。
 */
@Composable
private fun CategoryFilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    val containerColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.background
        },
        animationSpec = tween(durationMillis = 180),
        label = "categoryFilterContainer"
    )

    val contentColor by animateColorAsState(
        targetValue = if (selected) {
            MaterialTheme.colorScheme.onPrimary
        } else {
            MaterialTheme.colorScheme.onBackground
        },
        animationSpec = tween(durationMillis = 180),
        label = "categoryFilterContent"
    )

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(containerColor)
            .clickable(onClick = onClick)
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            color = contentColor,
            fontWeight = if (selected) {
                FontWeight.SemiBold
            } else {
                FontWeight.Normal
            },
            maxLines = 1
        )
    }
}

/**
 * 电影网格卡片。
 */
@Composable
private fun CategoryMovieItem(
    movie: CategoryMovieUi,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(0.72f)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        ) {
            AsyncImage(
                model = movie.posterUrl,
                contentDescription = movie.name,
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.placeholder),
                error = painterResource(R.drawable.placeholder),
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.58f)
                            )
                        )
                    )
            )

            if (movie.badge.isNotBlank()) {
                MovieBadge(
                    text = movie.badge,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }

            Text(
                text = movie.score,
                style = MaterialTheme.typography.titleMedium,
                color = Color(0xFFFFA000),
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(
                        end = 7.dp,
                        bottom = 6.dp
                    )
            )
        }

        Text(
            text = movie.name,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(
                top = 7.dp,
                start = 1.dp,
                end = 1.dp
            )
        )
    }
}

/**
 * 海报左上角标签。
 */
@Composable
private fun MovieBadge(
    text: String,
    modifier: Modifier = Modifier
) {
    val badgeColor = when (text) {
        "VIP" -> Color(0xFFFFB300)
        else -> MaterialTheme.colorScheme.primary
    }

    Box(
        modifier = modifier
            .background(
                color = badgeColor,
                shape = RoundedCornerShape(
                    bottomEnd = 6.dp
                )
            )
            .padding(
                horizontal = 6.dp,
                vertical = 3.dp
            )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CategoryScreenPreview(){
    BLMovieTheme() {
        CategoryScreen()
    }
}