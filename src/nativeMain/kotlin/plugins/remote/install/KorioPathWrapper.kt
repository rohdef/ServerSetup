package plugins.remote.install

import com.soywiz.korio.file.VfsFile

abstract class KorioPathWrapper(
    vfs: VfsFile,
) : Path() {
    override val absolutePath = vfs.absolutePath
}