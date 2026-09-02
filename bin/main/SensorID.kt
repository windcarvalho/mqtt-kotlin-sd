import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    val windowState = rememberWindowState(size = DpSize(width = 400.dp, height = 600.dp))
    Window(
            onCloseRequest = ::exitApplication,
            state = windowState,
            title = "Sensor de Temperatura MQTT - Com ID"
    ) { SensorIDApp() }
}

@Composable
fun SensorIDApp() {
    var count by remember { mutableStateOf(0) }
    val clientId = "kdemo2" // ID do cliente MQTT
    fun enviarTemperaturaComID(temperatura: Double) {
        val brokerUrl = "tcp://localhost:1883" // URL do broker MQTT   
        val local= "lab04"      
        val topic = local + "/temperatura" // Tópico para enviar a temperatura
        val client = org.eclipse.paho.client.mqttv3.MqttClient(brokerUrl, clientId)
        
        client.connect()
        println("Conectado ao broker MQTT!")
        val message =
                org.eclipse.paho.client.mqttv3.MqttMessage(
                        (temperatura.toString() + "#" + clientId).toByteArray()
                )
        println("Enviando temperatura: $temperatura C para o topico: $topic")
        client.publish(topic, message)
        client.disconnect()
        println("Desconectado do broker MQTT!")
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Usamos uma Column para empilhar a imagem e o botão verticalmente
        Column(
                horizontalAlignment =
                        Alignment.CenterHorizontally // Centraliza os itens dentro da coluna
        ) {
            Image(
                    // O caminho é relativo à pasta 'resources'
                    painter = painterResource("termometro2.png"),
                    contentDescription = "Sensor de Temperatura",
                    modifier =
                            Modifier.size(300.dp) // Define o tamanho da imagem
                                    .padding(
                                            bottom = 20.dp
                                    ) // Dá um espaço entre a imagem e o botão
            )

            Text(text = "ID: " + clientId.toString(), modifier = Modifier.padding(bottom = 20.dp))
            Button(
                    onClick = {
                        count++
                        enviarTemperaturaComID(count.toDouble())
                    }
            ) { Text("Temperatura: $count °C") }
        }
    }
}
