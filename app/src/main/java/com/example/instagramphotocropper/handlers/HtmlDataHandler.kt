package com.example.instagramphotocropper.handlers

import com.example.instagramphotocropper.objects.InstagramData

object HtmlDataHandler {
    fun getJson(html : String) : String{
        val first = html.split("window._sharedData = ")
        if (first.isNotEmpty()){
            return first.get(1).split(";</script>").first()
        }
        return ""
    }

    fun getUrls(obj : InstagramData) : List<String>{
        val response = arrayListOf<String>()
        obj.entry_data.postPage?.let { postPage ->
            postPage.first().graphql.shortcode_media.edge_sidecar_to_children?.let {
                it.edges.map { edge ->
                    response.add(edge.node.display_url)
                }
            } ?: kotlin.run {
                response.add(obj.entry_data.postPage.first().graphql.shortcode_media.display_url)
            }
        } ?: kotlin.run {
            //TODO: handle this
        }

        return response
    }
}