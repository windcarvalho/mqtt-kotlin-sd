import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.eclipse.paho.client.mqttv3.MqttConnectOptions

fun main() = application {
    Window(onCloseRequest = ::exitApplication, title = "Monitor de Temperatura MQTT") {
        MonitorApp()
    }
}

@Composable
fun TemperatureCard(
        temperature: String,
        clientName: String,
        fontSize: TextUnit,
        modifier: Modifier = Modifier
) {
    Card(modifier = modifier.height(150.dp), elevation = 6.dp) {
        Column(
                modifier = Modifier.fillMaxSize().padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {
            Text(text = temperature, fontSize = fontSize, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(12.dp))

            Text(text = clientName, fontSize = 16.sp)
        }
    }
}

@Composable
fun MonitorApp() {

    val brokerUrl = "tcp://localhost:1883" // URL do broker MQTT
    val clientId = "kotlin-desktop-medidor" // ID do cliente MQTT
    val client = remember {
        org.eclipse.paho.client.mqttv3.MqttClient(brokerUrl, clientId)
    }
    var mediaTemperatura by remember { mutableStateOf("-") }
    var temperatures by remember {
        mutableStateOf(
                listOf(
                        Temperature("-", "-"),
                        Temperature("-", "-"),
                        Temperature("-", "-"),
                        Temperature("-", "-"),
                        Temperature("-", "-"),
                )
        )
    }
    
    fun conectarMQTTFila() {
        //Nesse tipo de conexão a subinscrição persiste
        val options = MqttConnectOptions().apply { isCleanSession = false }
        client.connect(options)
        println("Conectado ao broker MQTT com options!")
    }
    fun conectarMQTT() {
        client.connect()
        println("Conectado ao broker MQTT!")
    }
    fun subscribeMQTT() {
        var mensagem: String
        var partes: List<String>
          if (client.isConnected) {
            client.subscribe("lab04/temperatura") { subscribedTopic, message ->
                println("Mensagem recebida:")
                println("  Topico: $subscribedTopic")
                println("  Temperatura: ${String(message.payload)}")
                temperatures =
                    temperatures.toMutableList().apply {
                        mensagem = String(message.payload)
                        partes = mensagem.split("#")
                        if (partes.size == 2) {
                            this[4] = this[3] // Move a temperatura anterior para a posição 4
                            this[3] = this[2] // Move a temperatura anterior para a posição 3
                            this[2] = this[1] // Move a temperatura anterior para a posição 2
                            this[1] = this[0] // Move a temperatura anterior para a posição 1

                            this[0] =
                                    Temperature(
                                            partes[0].take(6),
                                            partes[1].take(10)
                                    ) // Atualiza a temperatura e o nome do cliente na posição
                            // indexMedida
                        } else {
                            println("Formato de mensagem inválido!")
                        }
                    }
                if (temperatures.all { it.value != "-" }) {
                    val soma = temperatures.sumOf { it.value.toDoubleOrNull() ?: 0.0 }
                mediaTemperatura = String.format("%.2f", soma / temperatures.size)
                } else {
                    mediaTemperatura = "-"
                }
            }
                    println("Inscrito no topico lab04/temperatura")
        }
        else {
            println("Cliente nao conectado. Conecte-se antes de subscrever.")
        }
    }

    fun disconnectMQTT() {
       
        if (client.isConnected) {
            client.disconnect()
            println("Desconectado do broker MQTT!")
        }else{
            println("Cliente estava desconectado do broker MQTT!")
        }
    }

    Box(modifier = Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
        Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
        ) {

            // Fila dos cards
            Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.Bottom
            ) {
                TemperatureCard(
                        temperature = temperatures[0].value + "°C",
                        clientName = temperatures[0].clientName,
                        fontSize = 52.sp,
                        modifier = Modifier.weight(1f)
                )

                TemperatureCard(
                        temperature = temperatures[1].value + "°C",
                        clientName = temperatures[1].clientName,
                        fontSize = 46.sp,
                        modifier = Modifier.weight(1f)
                )

                TemperatureCard(
                        temperature = temperatures[2].value + "°C",
                        clientName = temperatures[2].clientName,
                        fontSize = 40.sp,
                        modifier = Modifier.weight(1f)
                )

                TemperatureCard(
                        temperature = temperatures[3].value + "°C",
                        clientName = temperatures[3].clientName,
                        fontSize = 32.sp,
                        modifier = Modifier.weight(1f)
                )

                TemperatureCard(
                        temperature = temperatures[4].value + "°C",
                        clientName = temperatures[4].clientName,
                        fontSize = 24.sp,
                        modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Média
            Text(
                    text = "A Média das Últimas Cinco Temperatura é: ${mediaTemperatura}°C",
                    fontSize = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Botões
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                
                Button(onClick = {conectarMQTTFila() }) { Text("Conectar") }

                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { subscribeMQTT() }) { Text("Subscrever") }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { disconnectMQTT() }) { Text("Desconectar do MQTT") }
            }
        }
    }
}

data class Temperature(val value: String, val clientName: String)
