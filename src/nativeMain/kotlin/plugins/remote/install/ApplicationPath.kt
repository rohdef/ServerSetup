package plugins.remote.install

import com.soywiz.korio.file.VfsFile

class ApplicationPath(
    applicationVcf: VfsFile,
) : KorioPathWrapper(applicationVcf)