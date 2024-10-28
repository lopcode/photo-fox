package com.photofox.web.page

data class PageResponse<ViewModel : Any>(
    val templatePath: String,
    val viewModel: ViewModel,
)
