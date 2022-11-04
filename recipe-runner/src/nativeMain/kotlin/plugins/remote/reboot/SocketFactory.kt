package plugins.remote.reboot

import io.ktor.network.sockets.*

interface SocketFactory {
    suspend fun tcpConnect(hostname: String, port: Int): Socket
}