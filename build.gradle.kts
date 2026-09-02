plugins {
    kotlin("jvm") version "1.9.24"
    id("org.jetbrains.compose") version "1.6.11"    
}

group = "com.exemplo"
version = "1.0.0"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.foundation)
    implementation(compose.material)
    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.5")
}

compose.desktop {
    application {
        mainClass = "MonitorKt"
    }
}
