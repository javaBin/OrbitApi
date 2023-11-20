module AppStyles exposing (..)

import Html.Styled as Html
import Html.Styled.Attributes as Attr
import Tailwind.Theme as Tw
import Tailwind.Utilities as Tw


tableStyle : Html.Attribute msg
tableStyle =
    Attr.css
        [ Tw.mt_10
        , Tw.border_collapse
        , Tw.w_full
        , Tw.border
        , Tw.border_color Tw.slate_400
        , Tw.bg_color Tw.white
        , Tw.text_sm
        , Tw.shadow_sm
        ]


theadStyle : Html.Attribute msg
theadStyle =
    Attr.css
        [ Tw.bg_color Tw.slate_50
        ]


tdStyle : Html.Attribute msg
tdStyle =
    Attr.css
        [ Tw.border
        , Tw.border_color Tw.slate_300
        , Tw.p_4
        , Tw.text_color Tw.slate_500
        ]


thStyle : Html.Attribute msg
thStyle =
    Attr.css
        [ Tw.border
        , Tw.border_color Tw.slate_300
        , Tw.font_semibold
        , Tw.p_4
        , Tw.text_color Tw.slate_900
        , Tw.text_left
        ]


mainHeadingStyle : Html.Attribute msg
mainHeadingStyle =
    Attr.css
        [ Tw.text_4xl
        , Tw.text_color Tw.slate_500
        ]
