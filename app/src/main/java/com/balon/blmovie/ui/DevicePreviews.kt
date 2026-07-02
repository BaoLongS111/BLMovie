package com.balon.blmovie.ui/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */



import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * Multipreview annotation that represents various device sizes. Add this annotation to a composable
 * to render various devices.
 */
@Preview(
    name = "phone_light",
    showBackground = true,
    device = "spec:width=720dp,height=1280dp,dpi=480",
)
@Preview(
    name = "landscape_light",
    showBackground = true,
    device = "spec:width=1280dp,height=720dp,dpi=480",
)
@Preview(
    name = "foldable_light",
    showBackground = true,
    device = "spec:width=673dp,height=841dp,dpi=480",
)
@Preview(
    name = "tablet_light",
    showBackground = true,
    device = "spec:width=1280dp,height=800dp,dpi=480",
)

@Preview(
    name = "phone_dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=720dp,height=1280dp,dpi=480",
)
@Preview(
    name = "landscape_dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1280dp,height=720dp,dpi=480",
)
@Preview(
    name = "foldable_dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=673dp,height=841dp,dpi=480",
)
@Preview(
    name = "tablet_dark",
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
    device = "spec:width=1280dp,height=800dp,dpi=480",
)
annotation class DevicePreviews
