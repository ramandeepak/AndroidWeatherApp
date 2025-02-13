package com.example.weatherapp.ui.home


import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weatherapp.ui.theme.WeatherAppTheme
import com.example.weatherapp.ui.theme.WeatherBlue
import com.example.weatherapp.ui.theme.WeatherInfoCardBlue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.SubcomposeAsyncImage
import android.content.Context
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import com.example.weatherapp.R
import com.example.weatherapp.data.model.WeatherResponse
import com.example.weatherapp.ui.component.WeatherProgressIndicator
import com.example.weatherapp.ui.component.WeatherTextView
import com.example.weatherapp.viewmodel.WeatherViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class WeatherHomeActivity : AppCompatActivity() {

    companion object {
         const val ICON_DESCRIPTION_SEARCH = "Search Icon"
         const val ICON_DESCRIPTION_WEATHER = "Weather Icon"
         const val ICON_DESCRIPTION_PLACEHOLDER = "Placeholder Icon"
         const val TOAST_DELAY: Long = 500
         const val TOAST_MESSAGE = "Please enter a city name or zip code."
         const val SEARCHBAR_PLACEHOLDER_MESSAGE = "Enter new location"
         const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    private val viewModel: WeatherViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        viewModel.checkLocationPermissions(this, this)
        setContent {
            WeatherAppTheme {
                Scaffold {  innerPadding ->
                    RenderWeatherComponents(viewModel, innerPadding)
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, get the location
                viewModel.getLocation(this, this)
            } else {
                // Permissions denied, handle accordingly
            }
        }
    }
}

@Composable
fun RenderWeatherComponents(viewModel: WeatherViewModel, paddingValues: PaddingValues) {
    val weatherForecast = viewModel.weatherForecast.observeAsState()
    val isLoading = viewModel.isLoading.observeAsState()

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .background(WeatherBlue)
            .padding(paddingValues)
    ) {
        NewRoundedSearchBar(viewModel)

        if (isLoading.value == true) {
            WeatherProgressIndicator()
        } else {
            Column(
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 64.dp)
            ) {
                weatherForecast.value?.let {
                    WeatherInfoCard(viewModel, weatherForecast)
                    ScrollableRowExample(viewModel)
                }
            }
        }
    }
}

@Composable
fun NewRoundedSearchBar(viewModel: WeatherViewModel) {

    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    var isFocused by remember { mutableStateOf(false) }
    var searchText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        value = searchText,
        singleLine = true,
        onValueChange = { searchText = it },
        keyboardActions = KeyboardActions(onDone = {
            focusManager.clearFocus()
            coroutineScope.launch {
                showToastAfterDelay(searchText, context, viewModel)
            }
        }),
        placeholder = { Text(WeatherHomeActivity.SEARCHBAR_PLACEHOLDER_MESSAGE) },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(top = 32.dp)
            .onFocusChanged { focusState ->
                isFocused = focusState.isFocused
            },
        shape = RoundedCornerShape(20.dp),
        trailingIcon = {
            IconButton(onClick = {
                focusManager.clearFocus()
                coroutineScope.launch {
                    showToastAfterDelay(searchText, context, viewModel)
                }
            }) {
                Icon(
                    imageVector = Icons.Filled.Search,
                    contentDescription = WeatherHomeActivity.ICON_DESCRIPTION_SEARCH,
                    tint = if(isFocused) Color.White else Color.Gray
                )
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color.White,
            unfocusedBorderColor = Color.Gray
        )
    )
}

@Composable
fun WeatherInfoCard(viewModel: WeatherViewModel, weatherForecast: State<WeatherResponse?>) {
    Card(
        elevation = CardDefaults.cardElevation(6.dp),
        colors = CardDefaults.cardColors(
            containerColor = WeatherInfoCardBlue
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp, vertical = 64.dp)
            .height(400.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            WeatherTextView("Weather in ${viewModel.getCityName()}", 20.sp)
            WeatherTextView(viewModel.getCityCountry(), 16.sp)
            WeatherTextView(viewModel.getCurrentTime(), 16.sp)
            WeatherTextView(viewModel.getCurrentTemperature(), 64.sp)
            WeatherTextView(viewModel.getCurrentMinMaxTemperature(), 10.sp)
            LoadImageWithPlaceholder(viewModel.getCurrentWeatherIconURL())
            WeatherTextView(viewModel.getCurrentWeatherCondition(), 16.sp)
        }
    }
}

@Composable
fun ScrollableRowExample(viewModel: WeatherViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp)
    ) {
            for (i in 1..<viewModel.getHourlyForecast().count()) {
                Column (
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {

                    WeatherTextView(viewModel.getHourOfTheDay(i), 18.sp)

                    Card(
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .size(180.dp),
                        colors = CardDefaults.cardColors(containerColor = WeatherInfoCardBlue)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(3.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(vertical = 6.dp, horizontal = 2.dp)
                        ) {
                            WeatherTextView(viewModel.getTemperatureForTheHour(i), 18.sp)
                            LoadImageWithPlaceholder(viewModel.getWeatherIconForTheHour(i))
                            WeatherTextView(viewModel.getWeatherConditionForTheHour(i), 12.sp)
                            WeatherTextView("${viewModel.getChanceOfPPTForTheHour(i)}% PPT", 10.sp)
                        }
                    }
                }
            }

    }
}

@Composable
fun LoadImageWithPlaceholder(url: String) {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = WeatherHomeActivity.ICON_DESCRIPTION_WEATHER,
            modifier = Modifier.size(64.dp),
            contentScale = ContentScale.Crop,
            loading = {
                Image(
                    painter = painterResource(id = R.drawable.placeholder_cloud),
                    contentDescription = WeatherHomeActivity.ICON_DESCRIPTION_PLACEHOLDER,
                    modifier = Modifier.size(64.dp),
                    contentScale = ContentScale.Crop
                )
            }
        )
}

suspend fun showToastAfterDelay(searchText: String, context: Context, viewModel: WeatherViewModel) {
    delay(WeatherHomeActivity.TOAST_DELAY)
    if (searchText.isEmpty()) {
        Toast.makeText(context, WeatherHomeActivity.TOAST_MESSAGE, Toast.LENGTH_SHORT)
            .show()
    } else {
        viewModel.fetchWeatherData(searchText)
    }
}

@Preview(showBackground = true)
@Composable
fun WeatherAppPreview() {
    WeatherAppTheme {

    }
}