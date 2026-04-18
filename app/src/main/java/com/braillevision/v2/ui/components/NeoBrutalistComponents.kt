package com.braillevision.v2.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.ZeroCornerSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.braillevision.v2.ui.theme.Black
import com.braillevision.v2.ui.theme.White
import com.braillevision.v2.ui.theme.Yellow
import com.braillevision.v2.ui.theme.Orange
import com.braillevision.v2.ui.theme.Blue
import com.braillevision.v2.ui.theme.Green
import com.braillevision.v2.ui.theme.Red
import com.braillevision.v2.ui.theme.Cyan

object NeoBrutalist {
    
    val BorderWidth = 3.dp
    val ShadowOffset = 4.dp
    val CornerRadius = 0.dp
    val ContentPadding = 16.dp
    
    val Shapes = object {
        val Rectangle = RectangleShape
        val RoundedSmall = RoundedCornerShape(4.dp)
        val RoundedMedium = RoundedCornerShape(8.dp)
    }
    
    val Colors = object {
        val Primary = Black
        val Secondary = Yellow
        val Accent = Orange
        val Info = Blue
        val Success = Green
        val Warning = Orange
        val Error = Red
    }
}

@Composable
fun NeoCard(
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    borderColor: Color = Black,
    shadowColor: Color = Black,
    shadowOffset: Dp = NeoBrutalist.ShadowOffset,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(shadowColor)
        )
        Card(
            modifier = Modifier
                .offset((-shadowOffset.value).dp, (-shadowOffset.value).dp)
                .border(
                    BorderStroke(NeoBrutalist.BorderWidth, borderColor),
                    RectangleShape
                ),
            shape = RectangleShape,
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor
            ),
            content = content
        )
    }
}

@Composable
fun NeoButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = Yellow,
    textColor: Color = Black,
    shadowColor: Color = Black,
    shadowOffset: Dp = NeoBrutalist.ShadowOffset,
    content: @Composable RowScope.() -> Unit
) {
    Box(
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(shadowColor)
        )
        Button(
            onClick = onClick,
            modifier = Modifier
                .offset((-shadowOffset.value).dp, (-shadowOffset.value).dp)
                .border(
                    BorderStroke(NeoBrutalist.BorderWidth, Black),
                    RectangleShape
                ),
            enabled = enabled,
            shape = RectangleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = backgroundColor,
                contentColor = textColor
            ),
            content = content
        )
    }
}

@Composable
fun NeoOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    backgroundColor: Color = White,
    textColor: Color = Black,
    content: @Composable RowScope.() -> Unit
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier.border(
            BorderStroke(NeoBrutalist.BorderWidth, Black),
            RectangleShape
        ),
        enabled = enabled,
        shape = RectangleShape,
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = backgroundColor,
            contentColor = textColor
        ),
        content = content
    )
}

@Composable
fun NeoTextButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Yellow,
    textColor: Color = Black,
    shadowColor: Color = Black,
    shadowOffset: Dp = NeoBrutalist.ShadowOffset
) {
    NeoButton(
        onClick = onClick,
        modifier = modifier,
        backgroundColor = backgroundColor,
        textColor = textColor,
        shadowColor = shadowColor,
        shadowOffset = shadowOffset
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

@Composable
fun NeoIconTextButton(
    onClick: () -> Unit,
    text: String,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = Yellow,
    textColor: Color = Black,
    shadowColor: Color = Black,
    shadowOffset: Dp = NeoBrutalist.ShadowOffset
) {
    NeoButton(
        onClick = onClick,
        modifier = modifier,
        backgroundColor = backgroundColor,
        textColor = textColor,
        shadowColor = shadowColor,
        shadowOffset = shadowOffset
    ) {
        icon()
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}

@Composable
fun NeoBox(
    modifier: Modifier = Modifier,
    backgroundColor: Color = White,
    borderColor: Color = Black,
    shadowColor: Color = Black,
    shadowOffset: Dp = NeoBrutalist.ShadowOffset,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(shadowColor)
        )
        Box(
            modifier = Modifier
                .offset((-shadowOffset.value).dp, (-shadowOffset.value).dp)
                .background(backgroundColor)
                .border(
                    BorderStroke(NeoBrutalist.BorderWidth, borderColor),
                    RectangleShape
                )
        ) {
            content()
        }
    }
}
