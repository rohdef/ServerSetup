package plugins.remote.install

value class ApplicationPath(
    private val directory: Path.Directory,
) : Path.Directory by directory