package com.balon.blmovie.feature.search.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.quick.app.core.extension.clickableNoRipple

@Composable
fun SearchInputBox(
    keyword: String,
    showClearButton: Boolean = true,
    onKeywordChange: (String) -> Unit,
    onFocusChange: (Boolean) -> Unit,
    onClearClick: () -> Unit,
    onSearch: () -> Unit,
    modifier: Modifier = Modifier
) {
    BasicTextField(
        value = keyword,
        onValueChange = onKeywordChange,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MaterialTheme.colorScheme.onSurface
        ),
        cursorBrush = SolidColor(
            MaterialTheme.colorScheme.primary
        ),
        keyboardOptions = KeyboardOptions(
            imeAction = ImeAction.Search
        ),
        keyboardActions = KeyboardActions(
            onSearch = {
                onSearch()
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(38.dp)
            .background(
                color = MaterialTheme.colorScheme.background,
                shape = CircleShape
            )
            .onFocusChanged { focusState ->
                onFocusChange(focusState.isFocused)
            },
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(12.dp))

                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.size(21.dp)
                )

                Spacer(modifier = Modifier.width(9.dp))

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (keyword.isBlank()) {
                        Text(
                            text = "请输入搜索内容",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    innerTextField()
                }

                if (
                    showClearButton &&
                    keyword.isNotBlank()
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clickableNoRipple {
                                onClearClick()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "清空",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(21.dp)
                        )
                    }
                } else {
                    Spacer(modifier = Modifier.width(12.dp))
                }
            }
        }
    )
}