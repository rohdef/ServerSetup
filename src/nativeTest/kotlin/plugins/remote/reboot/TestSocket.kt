package plugins.remote.reboot

import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job

class TestSocket(
    override val remoteAddress: SocketAddress
) : Socket {
    private var status = ConnectionStatus.OPEN

    override fun close() {
        status = ConnectionStatus.CLOSED
    }

    override val localAddress: SocketAddress
        get() = TODO("not implemented")

    override fun attachForReading(channel: ByteChannel): WriterJob {
        return CoroutineScope(GlobalScope.coroutineContext).writer {
            channel.writeStringUtf8("Lottes fede fest")
            println("Lotte Lotte Lotte")

            channel.close()
        }
    }

    override val socketContext: Job
        get() = TODO("not implemented")

    override fun attachForWriting(channel: ByteChannel): ReaderJob {
        return CoroutineScope(GlobalScope.coroutineContext).reader {
            channel.close()
        }
    }

    enum class ConnectionStatus {
        OPEN, CLOSED
    }
}