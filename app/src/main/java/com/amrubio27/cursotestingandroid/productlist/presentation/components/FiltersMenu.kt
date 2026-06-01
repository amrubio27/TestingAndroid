package com.amrubio27.cursotestingandroid.productlist.presentation.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.amrubio27.cursotestingandroid.R
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.FILTER_VIEW
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.productListCategory
import com.amrubio27.cursotestingandroid.core.presentation.testing.UiTestTag.productListSortOption
import com.amrubio27.cursotestingandroid.productlist.domain.model.SortOption
import com.amrubio27.cursotestingandroid.productlist.presentation.ProductListUiState

@Composable
fun FiltersMenu(
    modifier: Modifier = Modifier,
    state: ProductListUiState.Success,
    onCategorySelected: (String?) -> Unit,
    onSortSelected: (SortOption) -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag(FILTER_VIEW)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                stringResource(R.string.product_list_sort_title_categories),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    modifier = Modifier.testTag(productListCategory(null)),
                    selected = state.selectedCategory == null,
                    onClick = { onCategorySelected(null) },
                    label = {
                        Text(
                            stringResource(R.string.product_list_category_all),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )
                state.categories.forEach { category ->
                    FilterChip(
                        modifier = Modifier.testTag(productListCategory(category)),
                        selected = category.equals(state.selectedCategory, ignoreCase = true),
                        onClick = { onCategorySelected(category) },
                        label = { Text(category, style = MaterialTheme.typography.labelSmall) }
                    )
                }
            }

            HorizontalDivider()

            Text(
                stringResource(R.string.product_list_sort_title),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )

            Row(
                Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = state.sortOption == SortOption.PRICE_ASC,
                    onClick = { onSortSelected(SortOption.PRICE_ASC) },
                    label = {
                        Text(
                            stringResource(R.string.product_list_sort_price_asc),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(productListSortOption(SortOption.PRICE_ASC))
                )
                FilterChip(
                    selected = state.sortOption == SortOption.PRICE_DESC,
                    onClick = { onSortSelected(SortOption.PRICE_DESC) },
                    label = {
                        Text(
                            stringResource(R.string.product_list_sort_price_desc),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(productListSortOption(SortOption.PRICE_DESC))
                )
                FilterChip(
                    selected = state.sortOption == SortOption.DISCOUNT,
                    onClick = { onSortSelected(SortOption.DISCOUNT) },
                    label = {
                        Text(
                            stringResource(R.string.product_list_sort_discount),
                            style = MaterialTheme.typography.labelSmall
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag(productListSortOption(SortOption.DISCOUNT))
                )
            }
        }
    }
}