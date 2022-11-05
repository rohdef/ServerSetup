package dk.rohdef.plugins.remote.reboot

import io.ktor.network.selector.*
import io.ktor.network.sockets.*

class KtorSockets : SocketFactory {
    override suspend fun tcpConnect(hostname: String, port: Int): Socket {
        val selectorManager = SelectorManager()
        return aSocket(selectorManager)
            .tcp()
            .connect(hostname, port)
    }
}