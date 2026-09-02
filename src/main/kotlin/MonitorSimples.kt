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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() = application {
    
    val windowState = rememberWindowState(
        size = DpSize(width = 500.dp, height = 600.dp) // Altere para os valores desejados
    )    
    Window(
        onCloseRequest = ::exitApplication,
        state = windowState,
        title = "Monitor Simples de Temperatura MQTT"
    ) {    
        MonitorSimplesApp()        
    }
}

@Composable
fun TemperaturaCard(
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
fun MonitorSimplesApp() {

    val brokerUrl = "tcp://localhost:1883" // URL do broker MQTT
    val clientId = "kotlin-desktop-medidorsimples" // ID do cliente MQTT
    val client = remember {
        org.eclipse.paho.client.mqttv3.MqttClient(brokerUrl, clientId)
    }
    var temperatura by remember {
        mutableStateOf("- °C")
    }    

    fun conectarMQTT() {
        client.connect()
        println("Conectado ao broker MQTT!")
    }
    fun subscribeMQTT() {
        
        if (client.isConnected) {
        client.subscribe("local/temperatura") { subscribedTopic, message ->
            println("Mensagem recebida:")
            println("  Topico: $subscribedTopic")
            println("  Temperatura: ${String(message.payload)}")
            temperatura = String(message.payload)  
                               
            
        }
        println("Inscrito no topico local/temperatura")
        } else {
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
                TemperaturaCard(
                        temperature = temperatura.toString(),
                        clientName = "Cliente Simples",
                        fontSize = 52.sp,
                        modifier = Modifier.weight(1f)
                )

               
            }        

            

            Spacer(modifier = Modifier.height(32.dp))

            // Botões
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                Button(onClick = { conectarMQTT() }) { Text("Conectar") }

                Spacer(modifier = Modifier.width(16.dp))
                Button(onClick = { subscribeMQTT() }) { Text("Subscrever") }

                Spacer(modifier = Modifier.width(16.dp))

                Button(onClick = { disconnectMQTT() }) { Text("Desconectar") }
            }
        }
    }
}


