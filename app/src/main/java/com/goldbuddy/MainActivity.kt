package com.goldbuddy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.goldbuddy.api.*
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.NumberFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Configuration
        val goldApiKey = "goldapi-dpqtzqsml6e3zxz-io"
        val exchangeRateApiKey = "0df1d62bb4274c9589357fdd"

        // Setup Retrofit for GoldAPI
        val goldRetrofit = Retrofit.Builder()
            .baseUrl("https://www.goldapi.io/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val goldService = goldRetrofit.create(GoldApiService::class.java)

        // Setup Retrofit for ExchangeRate-API
        val exchangeRetrofit = Retrofit.Builder()
            .baseUrl("https://v6.exchangerate-api.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val exchangeService = exchangeRetrofit.create(ExchangeRateService::class.java)

        val repository = GoldRepository(goldService, exchangeService, goldApiKey, exchangeRateApiKey)

        setContent {
            GoldBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Manual ViewModel creation for demo purposes
                    // In a real app use ViewModelProvider.Factory or Hilt
                    val viewModel = remember { GoldViewModel(repository) }
                    GoldRateScreen(viewModel)
                }
            }
        }
    }
}

@Composable
fun GoldRateScreen(viewModel: GoldViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    val pkrFormat = NumberFormat.getCurrencyInstance(Locale("en", "PK")).apply {
        currency = Currency.getInstance("PKR")
        maximumFractionDigits = 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFFFD700).copy(alpha = 0.15f), Color.White)
                )
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Gold Buddy",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB8860B)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Live Gold Price in Pakistan",
            fontSize = 16.sp,
            color = Color.Gray
        )
        
        Spacer(modifier = Modifier.height(48.dp))

        when (val state = uiState) {
            is GoldRatesState.Loading -> {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color(0xFFD4AF37))
                }
            }
            is GoldRatesState.Success -> {
                Column(modifier = Modifier.weight(1f)) {
                    PriceCard("1 Tola (11.665g)", pkrFormat.format(state.pricePerTola))
                    Spacer(modifier = Modifier.height(16.dp))
                    PriceCard("10 Grams", pkrFormat.format(state.pricePer10Grams))
                    Spacer(modifier = Modifier.height(16.dp))
                    PriceCard("1 Gram", pkrFormat.format(state.pricePerGram))
                }
            }
            is GoldRatesState.Error -> {
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = "Error: ${state.message}", color = Color.Red, modifier = Modifier.padding(16.dp))
                }
            }
        }

        Button(
            onClick = { viewModel.refreshRates() },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD4AF37)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Refresh Price", color = Color.White, fontWeight = FontWeight.Bold)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        if (uiState is GoldRatesState.Success) {
            val timestamp = (uiState as GoldRatesState.Success).lastUpdated * 1000
            val date = java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(timestamp))
            Text(text = "Last updated: $date", fontSize = 12.sp, color = Color.Gray)
        }
    }
}

@Composable
fun PriceCard(label: String, price: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.6f))
    ) {
        Row(
            modifier = Modifier.padding(24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(text = label, fontSize = 14.sp, color = Color.Gray)
                Text(text = price, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold, color = Color.Black)
            }
        }
    }
}

@Composable
fun GoldBuddyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFFD4AF37),
            background = Color.White
        ),
        content = content
    )
}
