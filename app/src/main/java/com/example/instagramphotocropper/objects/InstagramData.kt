package com.example.instagramphotocropper.objects

import com.google.gson.annotations.SerializedName

data class InstagramData(
    val browser_push_pub_key: String,
    val bundle_variant: String,
    val cache_schema_version: Int,
    val config: Config,
    val connection_quality_rating: String,
    val consent_dialog_config: ConsentDialogConfig,
    val country_code: String,
    val deployment_stage: String,
    val device_id: String,
    val encryption: Encryption,
    val entry_data: EntryData
) {
    data class Config(
        val csrf_token: String,
        val viewer: Any?,
        val viewerId: Any?
    )

    data class ConsentDialogConfig(
        val is_user_linked_to_fb: Boolean,
        val should_show_consent_dialog: Boolean,
        val should_use_winning_variant_qe: Any?
    )

    data class Encryption(
        val key_id: String,
        val public_key: String,
        val version: String
    )

    data class EntryData(
        @SerializedName("PostPage")
        val postPage: List<PostPage>?
    ) {
        data class PostPage(
            val graphql: Graphql
        ) {
            data class Graphql(
                val shortcode_media: ShortcodeMedia
            ) {
                data class ShortcodeMedia(
                    val __typename: String,
                    val can_see_insights_as_brand: Boolean,
                    val caption_is_edited: Boolean,
                    val coauthor_producers: List<Any>,
                    val commenting_disabled_for_viewer: Boolean,
                    val comments_disabled: Boolean,
                    val dimensions: Dimensions,
                    val display_resources: List<DisplayResource>,
                    val display_url: String,
                    val edge_media_preview_comment: EdgeMediaPreviewComment,
                    val edge_media_preview_like: EdgeMediaPreviewLike,
                    val edge_media_to_caption: EdgeMediaToCaption,
                    val edge_media_to_hoisted_comment: EdgeMediaToHoistedComment,
                    val edge_media_to_parent_comment: EdgeMediaToParentComment,
                    val edge_media_to_sponsor_user: EdgeMediaToSponsorUser,
                    val edge_media_to_tagged_user: EdgeMediaToTaggedUser,
                    val edge_related_profiles: EdgeRelatedProfiles,
                    val edge_sidecar_to_children: EdgeSidecarToChildren?,
                    val edge_web_media_to_related_media: EdgeWebMediaToRelatedMedia,
                    val fact_check_information: Any?,
                    val fact_check_overall_rating: Any?,
                    val gating_info: Any?,
                    val has_ranked_comments: Boolean,
                    val id: String,
                    val is_ad: Boolean,
                    val is_affiliate: Boolean,
                    val is_paid_partnership: Boolean,
                    val is_video: Boolean,
                    val like_and_view_counts_disabled: Boolean,
                    val location: Any?,
                    val media_overlay_info: Any?,
                    val media_preview: Any?,
                    val owner: Owner,
                    val sensitivity_friction_info: Any?,
                    val sharing_friction_info: SharingFrictionInfo,
                    val shortcode: String,
                    val taken_at_timestamp: Int,
                    val tracking_token: String,
                    val upcoming_event: Any?,
                    val viewer_can_reshare: Boolean,
                    val viewer_has_liked: Boolean,
                    val viewer_has_saved: Boolean,
                    val viewer_has_saved_to_collection: Boolean,
                    val viewer_in_photo_of_you: Boolean
                ) {
                    data class Dimensions(
                        val height: Int,
                        val width: Int
                    )

                    data class DisplayResource(
                        val config_height: Int,
                        val config_width: Int,
                        val src: String
                    )

                    data class EdgeMediaPreviewComment(
                        val count: Int,
                        val edges: List<Edge>
                    ) {
                        data class Edge(
                            val node: Node
                        ) {
                            data class Node(
                                val created_at: Int,
                                val did_report_as_spam: Boolean,
                                val edge_liked_by: EdgeLikedBy,
                                val id: String,
                                val is_restricted_pending: Boolean,
                                val owner: Owner,
                                val text: String,
                                val viewer_has_liked: Boolean
                            ) {
                                data class EdgeLikedBy(
                                    val count: Int
                                )

                                data class Owner(
                                    val id: String,
                                    val is_verified: Boolean,
                                    val profile_pic_url: String,
                                    val username: String
                                )
                            }
                        }
                    }

                    data class EdgeMediaPreviewLike(
                        val count: Int,
                        val edges: List<Any>
                    )

                    data class EdgeMediaToCaption(
                        val edges: List<Edge>
                    ) {
                        data class Edge(
                            val node: Node
                        ) {
                            data class Node(
                                val text: String
                            )
                        }
                    }

                    data class EdgeMediaToHoistedComment(
                        val edges: List<Any>
                    )

                    data class EdgeMediaToParentComment(
                        val count: Int,
                        val edges: List<Edge>,
                        val page_info: PageInfo
                    ) {
                        data class Edge(
                            val node: Node
                        ) {
                            data class Node(
                                val created_at: Int,
                                val did_report_as_spam: Boolean,
                                val edge_liked_by: EdgeLikedBy,
                                val edge_threaded_comments: EdgeThreadedComments,
                                val id: String,
                                val is_restricted_pending: Boolean,
                                val owner: Owner,
                                val text: String,
                                val viewer_has_liked: Boolean
                            ) {
                                data class EdgeLikedBy(
                                    val count: Int
                                )

                                data class EdgeThreadedComments(
                                    val count: Int,
                                    val edges: List<Any>,
                                    val page_info: PageInfo
                                ) {
                                    data class PageInfo(
                                        val end_cursor: Any?,
                                        val has_next_page: Boolean
                                    )
                                }

                                data class Owner(
                                    val id: String,
                                    val is_verified: Boolean,
                                    val profile_pic_url: String,
                                    val username: String
                                )
                            }
                        }

                        data class PageInfo(
                            val end_cursor: Any?,
                            val has_next_page: Boolean
                        )
                    }

                    data class EdgeMediaToSponsorUser(
                        val edges: List<Any>
                    )

                    data class EdgeMediaToTaggedUser(
                        val edges: List<Edge>
                    ) {
                        data class Edge(
                            val node: Node
                        ) {
                            data class Node(
                                val user: User,
                                val x: Double,
                                val y: Double
                            ) {
                                data class User(
                                    val followed_by_viewer: Boolean,
                                    val full_name: String,
                                    val id: String,
                                    val is_verified: Boolean,
                                    val profile_pic_url: String,
                                    val username: String
                                )
                            }
                        }
                    }

                    data class EdgeRelatedProfiles(
                        val edges: List<Any>
                    )

                    data class EdgeSidecarToChildren(
                        val edges: List<Edge>
                    ) {
                        data class Edge(
                            val node: Node
                        ) {
                            data class Node(
                                val __typename: String,
                                val accessibility_caption: String,
                                val dimensions: Dimensions,
                                val display_resources: List<DisplayResource>,
                                val display_url: String,
                                val edge_media_to_tagged_user: EdgeMediaToTaggedUser,
                                val fact_check_information: Any?,
                                val fact_check_overall_rating: Any?,
                                val gating_info: Any?,
                                val id: String,
                                val is_video: Boolean,
                                val media_overlay_info: Any?,
                                val media_preview: String,
                                val sensitivity_friction_info: Any?,
                                val sharing_friction_info: SharingFrictionInfo,
                                val shortcode: String,
                                val tracking_token: String,
                                val upcoming_event: Any?
                            ) {
                                data class Dimensions(
                                    val height: Int,
                                    val width: Int
                                )

                                data class DisplayResource(
                                    val config_height: Int,
                                    val config_width: Int,
                                    val src: String
                                )

                                data class EdgeMediaToTaggedUser(
                                    val edges: List<Edge>
                                ) {
                                    data class Edge(
                                        val node: Node
                                    ) {
                                        data class Node(
                                            val user: User,
                                            val x: Double,
                                            val y: Double
                                        ) {
                                            data class User(
                                                val followed_by_viewer: Boolean,
                                                val full_name: String,
                                                val id: String,
                                                val is_verified: Boolean,
                                                val profile_pic_url: String,
                                                val username: String
                                            )
                                        }
                                    }
                                }

                                data class SharingFrictionInfo(
                                    val bloks_app_url: Any?,
                                    val should_have_sharing_friction: Boolean
                                )
                            }
                        }
                    }

                    data class EdgeWebMediaToRelatedMedia(
                        val edges: List<Any>
                    )

                    data class Owner(
                        val blocked_by_viewer: Boolean,
                        val edge_followed_by: EdgeFollowedBy,
                        val edge_owner_to_timeline_media: EdgeOwnerToTimelineMedia,
                        val followed_by_viewer: Boolean,
                        val full_name: String,
                        val has_blocked_viewer: Boolean,
                        val id: String,
                        val is_private: Boolean,
                        val is_unpublished: Boolean,
                        val is_verified: Boolean,
                        val pass_tiering_recommendation: Boolean,
                        val profile_pic_url: String,
                        val requested_by_viewer: Boolean,
                        val restricted_by_viewer: Any?,
                        val username: String
                    ) {
                        data class EdgeFollowedBy(
                            val count: Int
                        )

                        data class EdgeOwnerToTimelineMedia(
                            val count: Int
                        )
                    }

                    data class SharingFrictionInfo(
                        val bloks_app_url: Any?,
                        val should_have_sharing_friction: Boolean
                    )
                }
            }
        }
    }
}