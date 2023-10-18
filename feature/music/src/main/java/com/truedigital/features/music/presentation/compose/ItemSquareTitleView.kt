package com.truedigital.features.music.presentation.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.truedigital.common.share.resource.ui.theme.TrueIdTheme
import com.truedigital.features.tuned.R

@Composable
fun ItemSquareTitleView(
    imageUrl: String,
    title: String,
    onItemClicked: (() -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .wrapContentHeight()
            .width(154.dp)
            .padding(end = 12.dp)
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current).data(imageUrl)
                .placeholder(R.drawable.placeholder_new_trueid_white_square)
                .error(R.drawable.placeholder_new_trueid_white_square).build(),
            contentDescription = "",
            modifier = Modifier
                .size(154.dp)
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    enabled = true,
                    onClick = {
                        onItemClicked?.invoke()
                    }
                ),
            contentScale = ContentScale.Crop
        )
        Text(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight()
                .padding(top = 10.dp),
            text = title,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            style = MaterialTheme.typography.subtitle2,
            color = colorResource(id = R.color.black)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0x000000)
@Composable
fun MatchFixturesPreview() {
    TrueIdTheme {
        ItemSquareTitleView("", "title title title title title title title")
    }
}
