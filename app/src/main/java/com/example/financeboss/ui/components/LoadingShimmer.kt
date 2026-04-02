package com.example.financeboss.ui.components




import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp

@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f),
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
    )
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(tween(1000, easing = FastOutSlowInEasing)),
        label = "shimmer"
    )
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

@Composable
fun ShimmerTransactionCard() {
    val brush = shimmerBrush()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(22.dp))
                .background(brush)
        )
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Box(
                Modifier
                    .fillMaxWidth(0.6f)
                    .height(14.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
            Box(
                Modifier
                    .fillMaxWidth(0.4f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
        Box(
            Modifier
                .width(60.dp)
                .height(14.dp)
                .clip(RoundedCornerShape(4.dp))
                .background(brush)
        )
    }
}

@Composable
fun ShimmerHomeScreen() {
    val brush = shimmerBrush()
    Column(Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxWidth().height(140.dp).background(brush))
        Spacer(Modifier.height(12.dp))
        Row(
            Modifier.padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                Modifier.weight(1f).height(80.dp)
                    .clip(RoundedCornerShape(16.dp)).background(brush)
            )
            Box(
                Modifier.weight(1f).height(80.dp)
                    .clip(RoundedCornerShape(16.dp)).background(brush)
            )
        }
        Spacer(Modifier.height(16.dp))
        Box(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(200.dp)
                .clip(RoundedCornerShape(16.dp)).background(brush)
        )
        Spacer(Modifier.height(16.dp))
        repeat(4) { ShimmerTransactionCard() }
    }
}