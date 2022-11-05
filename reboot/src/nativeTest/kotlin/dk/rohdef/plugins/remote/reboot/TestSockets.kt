package dk.rohdef.plugins.remote.reboot

import io.ktor.network.sockets.*

class TestSockets : SocketFactory {
    val generatedSockets = mutableMapOf<InetSocketAddress, List<TestSocket>>()
        .withDefault { emptyList() }

    override suspend fun tcpConnect(hostname: String, port: Int): Socket {
        val address = InetSocketAddress(hostname, port)
        val socket = TestSocket(address)

        generatedSockets[address] = generatedSockets.getValue(address) + socket

        return socket
    }
}