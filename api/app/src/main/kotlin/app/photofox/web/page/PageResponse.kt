package app.photofox.web.page

// remember to set cache control to "no cache" for templated views

data class PageResponse<ViewModel : Any>(
    val templatePath: String,
    val viewModel: ViewModel,
)
