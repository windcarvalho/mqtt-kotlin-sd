import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
   Window(
        onCloseRequest = ::exitApplication,
        title = "Sensor de Temperatura MQTT") 
        {
        App()        
    }
}
fun enviarTemperatura(temperatura: Double) {
    val brokerUrl = "tcp://localhost:1883" // URL do broker MQTT
    val clientId = "kdemo2" // ID do cliente MQTT
    val topic = "lab04/temperatura" // Tópico para enviar a temperatura

    val client = org.eclipse.paho.client.mqttv3.MqttClient(brokerUrl, clientId)
    client.connect()

    val message = org.eclipse.paho.client.mqttv3.MqttMessage((temperatura.toString()+"#"+clientId).toByteArray())
    client.publish(topic, message)

    client.disconnect()
}

@Composable
fun App() {
    var count by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(onClick = { count++ 
        enviarTemperatura(count.toDouble())
        }) {
            Text("Cliquei $count vezes")
            
        }
    }
}
