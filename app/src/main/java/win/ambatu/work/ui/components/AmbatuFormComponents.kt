package win.ambatu.work.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import win.ambatu.work.ui.theme.ChocoAmbatu

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbatuTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    supportingText: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = ChocoAmbatu,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            singleLine = singleLine,
            minLines = minLines,
            readOnly = readOnly,
            placeholder = placeholder?.let { { Text(it) } },
            trailingIcon = trailingIcon,
            keyboardOptions = keyboardOptions,
            supportingText = supportingText,
            shape = RoundedCornerShape(16.dp),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFE5B834),
                unfocusedContainerColor = Color(0xFFE5B834),
                disabledContainerColor = Color(0xFFE5B834),
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedTextColor = ChocoAmbatu,
                unfocusedTextColor = ChocoAmbatu,
                focusedPlaceholderColor = ChocoAmbatu.copy(alpha = 0.6f),
                unfocusedPlaceholderColor = ChocoAmbatu.copy(alpha = 0.6f)
            )
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbatuDropdownField(
    value: String,
    label: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            color = ChocoAmbatu,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange,
            modifier = Modifier.fillMaxWidth()
        ) {
            TextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = expanded
                    )
                },
                modifier = Modifier
                    .menuAnchor()
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = ChocoAmbatu,
                    unfocusedContainerColor = ChocoAmbatu,
                    disabledContainerColor = ChocoAmbatu,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent,
                    focusedTextColor = Color(0xFFE5B834),
                    unfocusedTextColor = Color(0xFFE5B834),
                    focusedTrailingIconColor = Color(0xFFE5B834),
                    unfocusedTrailingIconColor = Color(0xFFE5B834)
                )
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) },
                content = content
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AmbatuSimpleTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String? = null,
    singleLine: Boolean = false,
    minLines: Int = 1,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier,
        singleLine = singleLine,
        minLines = minLines,
        readOnly = readOnly,
        placeholder = placeholder?.let { { Text(it) } },
        trailingIcon = trailingIcon,
        keyboardOptions = keyboardOptions,
        shape = RoundedCornerShape(16.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = Color(0xFFE5B834),
            unfocusedContainerColor = Color(0xFFE5B834),
            disabledContainerColor = Color(0xFFE5B834),
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedTextColor = ChocoAmbatu,
            unfocusedTextColor = ChocoAmbatu,
            focusedPlaceholderColor = ChocoAmbatu.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = ChocoAmbatu.copy(alpha = 0.6f)
        )
    )
}
