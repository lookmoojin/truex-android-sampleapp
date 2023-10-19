package com.truedigital.common.share.resource.exetension

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

val Int.toDp: Dp
    @Composable
    get() = with(LocalDensity.current) { toDp() }
