package plugins.remote.install

import com.soywiz.korio.file.VfsFile

class WorkDirectoryPath(
    workDirectoryVfs: VfsFile,
) : KorioPathWrapper(workDirectoryVfs)