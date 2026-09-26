package org.jansaarthi.app.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jansaarthi.app.ui.theme.*

/**
 * Accessible Input Field with large touch targets, bold contrast, and prominent labels.
 */
@Composable
fun JanSaarthiTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    prefixText: String? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    fontSizeMultiplier: Float = 1.0f
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // High visibility label above the box
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (isError) GovError else GovTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = (15 * fontSizeMultiplier).sp
            )
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = GovTextMuted,
                        fontSize = (15 * fontSizeMultiplier).sp
                    )
                )
            },
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 12.dp, end = 4.dp)
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isError) GovError else GovNavyPrimary,
                        modifier = Modifier.size(22.dp)
                    )
                    if (prefixText != null) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prefixText,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GovTextPrimary,
                                fontSize = (15 * fontSizeMultiplier).sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .padding(horizontal = 6.dp)
                                .height(20.dp)
                                .width(1.dp)
                                .background(GovBorder)
                        )
                    }
                }
            },
            trailingIcon = trailingContent,
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF1F5F9),
                errorContainerColor = Color(0xFFFFF5F5),
                focusedBorderColor = GovNavyPrimary,
                unfocusedBorderColor = GovBorder,
                errorBorderColor = GovError,
                cursorColor = GovNavyPrimary,
                focusedTextColor = GovTextPrimary,
                unfocusedTextColor = GovTextPrimary
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = (16 * fontSizeMultiplier).sp,
                fontWeight = FontWeight.Medium,
                color = GovTextPrimary
            )
        )

        if (isError && !errorMessage.isNullOrEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = GovError,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GovError,
                        fontSize = (13 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

/**
 * Accessible Password Field with toggleable visibility
 */
@Composable
fun JanSaarthiPasswordField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    leadingIcon: ImageVector,
    passwordVisible: Boolean,
    onPasswordVisibilityToggle: () -> Unit,
    modifier: Modifier = Modifier,
    isError: Boolean = false,
    errorMessage: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    fontSizeMultiplier: Float = 1.0f
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                color = if (isError) GovError else GovTextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = (15 * fontSizeMultiplier).sp
            )
        )

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = 56.dp),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = GovTextMuted,
                        fontSize = (15 * fontSizeMultiplier).sp
                    )
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = if (isError) GovError else GovNavyPrimary,
                    modifier = Modifier
                        .padding(start = 12.dp)
                        .size(22.dp)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onPasswordVisibilityToggle,
                    modifier = Modifier.size(48.dp) // Large touch target
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (passwordVisible) "Hide password" else "Show password",
                        tint = GovTextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                disabledContainerColor = Color(0xFFF1F5F9),
                errorContainerColor = Color(0xFFFFF5F5),
                focusedBorderColor = GovNavyPrimary,
                unfocusedBorderColor = GovBorder,
                errorBorderColor = GovError,
                cursorColor = GovNavyPrimary,
                focusedTextColor = GovTextPrimary,
                unfocusedTextColor = GovTextPrimary
            ),
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                fontSize = (16 * fontSizeMultiplier).sp,
                fontWeight = FontWeight.Medium,
                color = GovTextPrimary
            )
        )

        if (isError && !errorMessage.isNullOrEmpty()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Error",
                    tint = GovError,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = GovError,
                        fontSize = (13 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }
        }
    }
}

/**
 * High-Contrast Primary Citizen Action Button (min 56dp touch height)
 */
@Composable
fun JanSaarthiPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    fontSizeMultiplier: Float = 1.0f
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = GovNavyPrimary,
            contentColor = Color.White,
            disabledContainerColor = Color(0xFFCBD5E1),
            disabledContentColor = Color(0xFF64748B)
        ),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.5.dp
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                if (leadingIcon != null) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = (17 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Secondary / Guest Button with high visibility border (min 56dp touch height)
 */
@Composable
fun JanSaarthiSecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
    supportingText: String? = null,
    fontSizeMultiplier: Float = 1.0f
) {
    OutlinedButton(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(if (supportingText != null) 64.dp else 56.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, GovNavyPrimary),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.White,
            contentColor = GovNavyPrimary
        )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            if (leadingIcon != null) {
                Icon(
                    imageVector = leadingIcon,
                    contentDescription = null,
                    tint = GovNavyPrimary,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontSize = (16 * fontSizeMultiplier).sp,
                        fontWeight = FontWeight.Bold,
                        color = GovNavyPrimary
                    )
                )
                if (supportingText != null) {
                    Text(
                        text = supportingText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontSize = (11 * fontSizeMultiplier).sp,
                            color = Color(0xFF64748B)
                        )
                    )
                }
            }
        }
    }
}
