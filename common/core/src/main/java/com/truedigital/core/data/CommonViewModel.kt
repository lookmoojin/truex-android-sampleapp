package com.truedigital.core.data

sealed class CommonViewModel {
    var shelfTitle: String = ""
    var shelfId: String = ""
    var shelfIndex = 0
    var vType = ""

    class TopNavigation : CommonViewModel() {
        companion object {
            const val VIEW_TYPE = "trueid_topnav"
        }
    }

    class ShelfMultipleHorizontal(val shelfModel: CommonShelfModel) :
        CommonViewModel() {
        companion object {
            const val VIEW_TYPE = "shelf_multiple_horizontal"
        }
    }

    class ShelfSingleHorizontal(val shelfModel: CommonShelfModel) :
        CommonViewModel() {
        companion object {
            const val VIEW_TYPE = "shelf_single_horizontal"
        }
    }

    class ShelfMultipleVertical(val shelfModel: CommonShelfModel) :
        CommonViewModel() {
        companion object {
            const val VIEW_TYPE = "shelf_multiple_vertical"
        }
    }

    class ShelfMultipleSquare(val shelfModel: CommonShelfModel) :
        CommonViewModel() {
        companion object {
            const val VIEW_TYPE = "shelf_multiple_square"
        }
    }

    class ShelfAds(val shelfModel: CommonShelfModel) :
        CommonViewModel() {
        companion object {
            const val VIEW_TYPE = "shelf_ads"
        }
    }

    class ShelfComponent(val shelfModel: CommonShelfModel) :
        CommonViewModel() {
        companion object {
            const val VIEW_TYPE = "shelf_component"
            const val ITEM_VIEW_TYPE = "item_component"
            const val COMPONENT_INLINE_BANNER = "inline_banner"
            const val TAG_INLINE_BANNER_VISIBLE = "inline_banner_visible"
            const val TAG_INLINE_BANNER_HIDDEN = "inline_banner_hidden"
        }
    }

    sealed class Header : CommonViewModel() {
        class HeaderHorizontal(val headerFooterModel: CommonHeaderFooterModel) : Header() {
            companion object {
                const val VIEW_TYPE = "header_horizontal"
            }
        }

        class HeaderSponsor(val headerFooterModel: CommonHeaderFooterModel) : Header() {
            companion object {
                const val VIEW_TYPE = "header_sponsor"
            }
        }

        class SearchHeaderView(val headerFooterModel: CommonHeaderFooterModel) :
            Header() {
            companion object {
                const val VIEW_TYPE = "search_header_view"
            }
        }
    }

    sealed class Footer : CommonViewModel() {
        class FooterText(val headerFooterModel: CommonHeaderFooterModel) :
            Footer() {
            companion object {
                const val VIEW_TYPE = "footer_text"
            }
        }

        class FooterSponsor(val headerFooterModel: CommonHeaderFooterModel) : Footer() {
            companion object {
                const val VIEW_TYPE = "footer_sponsor"
            }
        }
    }

    sealed class Items : CommonViewModel() {

        class Item16x9Article(val itemModel: CommonItemModel) : Items() {
            companion object {
                const val VIEW_TYPE = "item_16x9_article"
            }
        }

        class Item16x9Content(val itemModel: CommonItemModel) : Items() {
            companion object {
                const val VIEW_TYPE = "item_16x9_content"
            }
        }

        class Item16x9Privilege(val itemModel: CommonItemModel) : Items() {
            companion object {
                const val VIEW_TYPE = "item_16x9_privilege"
            }
        }

        class Item1x1Privilege(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_privilege"
            }
        }

        class Item1x1Shopping(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_shopping"
            }
        }

        class Item4x3Movie(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_4x3_movie"
            }
        }

        class Item16x9Tv(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_16x9_tv"
            }
        }

        class Item1x1Content(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_content"
            }
        }

        class Item1x1Music(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_music"
            }
        }

        class Item1x1Bubble(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_bubble"
            }
        }

        class Item1x1Label(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_label"
            }
        }

        class Item16x4Content(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_16x4_content"
            }
        }

        class Item1x1Highlight(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_highlight"
            }
        }

        class Item1x1Ads(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_1x1_ads"
            }
        }

        class Item16x9Ads(val itemModel: CommonItemModel) :
            Items() {
            companion object {
                const val VIEW_TYPE = "item_16x9_ads"
            }
        }

        class Item16x9Landscape(val itemModel: CommonItemModel) : Items() {
            companion object {
                const val VIEW_TYPE = "item_16x9_landscape"
            }
        }
        class Item4x3TV(val itemModel: CommonItemModel) : Items() {
            companion object {
                const val VIEW_TYPE = "item_4x3_tv"
            }
        }
        class Item16x9Player(val itemModel: CommonItemModel) : Items() {
            companion object {
                const val VIEW_TYPE = "item_16x9_player"
            }
        }
    }
}
