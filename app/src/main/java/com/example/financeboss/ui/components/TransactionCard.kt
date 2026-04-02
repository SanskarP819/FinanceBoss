package com.example.financeboss.ui.components



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.financeboss.data.local.entity.TransactionEntity
import com.example.financeboss.data.local.entity.TransactionType
import com.example.financeboss.ui.theme.CategoryColors
import com.example.financeboss.ui.theme.ExpenseRed
import com.example.financeboss.ui.theme.IncomeGreen
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun TransactionCard(
    transaction: TransactionEntity,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = CategoryColors[transaction.category.name] ?: Color.Gray
    val amountColor = if (transaction.type == TransactionType.INCOME) IncomeGreen else ExpenseRed
    val amountPrefix = if (transaction.type == TransactionType.INCOME) "+" else "-"
    val dateStr = SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(transaction.date))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(categoryColor.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Text(transaction.category.emoji, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = transaction.category.displayName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(" • ", style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(dateStr, style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Text(
                text = "$amountPrefix₹${"%,.0f".format(transaction.amount)}",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = amountColor
            )
        }
    }
}