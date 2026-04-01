package win.ambatu.work.model

data class LinkModel(
    val text: String,
    val link: String
)

val linkModels = listOf(
    LinkModel(
        text = "Official Website",
        link = "https://work.ambatu.win"
    ),
    LinkModel(
        text = "Github Repo",
        link = "https://github.com/ambawin/ambatuwork-android"
    ),
    LinkModel(
        text = "Share This App",
        link = ""
    )
)