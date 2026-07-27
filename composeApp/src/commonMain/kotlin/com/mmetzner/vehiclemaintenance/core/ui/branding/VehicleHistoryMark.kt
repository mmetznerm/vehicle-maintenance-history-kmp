package com.mmetzner.vehiclemaintenance.core.ui.branding

import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import vehiclemaintenance.composeapp.generated.resources.Res
import vehiclemaintenance.composeapp.generated.resources.vehicle_history_mark

@Composable
fun VehicleHistoryMark(
    modifier: Modifier = Modifier,
    elevation: Dp = 0.dp
) {
    Image(
        painter = painterResource(Res.drawable.vehicle_history_mark),
        contentDescription = null,
        modifier = modifier.shadow(
            elevation = elevation,
            shape = RoundedCornerShape(31),
            clip = false
        )
    )
}
