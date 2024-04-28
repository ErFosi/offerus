package com.offerus.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ContentAlpha
import com.offerus.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterFieldView() {
    val backgroundColor = MaterialTheme.colorScheme.secondary
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        var username by remember { mutableStateOf("") }
        var fullName by remember { mutableStateOf("") }
        var age by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var phone by remember { mutableStateOf("") }
        var sex by remember { mutableStateOf("") }

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text(stringResource(R.string.username)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Person, contentDescription = stringResource(R.string.username))
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = backgroundColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(stringResource(R.string.full_name)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.AccountCircle, contentDescription = stringResource(R.string.full_name))
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = backgroundColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        OutlinedTextField(
            value = age,
            onValueChange = { age = it },
            label = { Text(stringResource(R.string.age)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.DateRange, contentDescription = stringResource(R.string.age))
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = backgroundColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ),keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Email TextField
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(stringResource(R.string.email)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Email, contentDescription = stringResource(R.string.email))
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = backgroundColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ),keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Email
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Phone TextField
        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(stringResource(R.string.phone)) },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Filled.Phone, contentDescription = stringResource(R.string.phone))
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = backgroundColor,
                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = ContentAlpha.disabled),
                focusedIndicatorColor = MaterialTheme.colorScheme.primary
            ), keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Phone
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )

        // Sex dropdown
        SexDropdown()
    }
}
