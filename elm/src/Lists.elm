module Lists exposing (..)

import AppStyles exposing (mainHeadingStyle, tableStyle, tdStyle, thStyle, theadStyle)
import Browser
import Css.Global
import ErrorTexts exposing (getErrorMessage)
import Html
import Html.Styled as Styled
import Html.Styled.Attributes as Attr
import Http
import Json.Decode as JD exposing (Decoder, field, int, maybe, string)
import RemoteData exposing (RemoteData)
import Tailwind.Theme as Tw
import Tailwind.Utilities as Tw


type alias BasicList =
    { id : Int
    , name : String
    }


type alias Model =
    { result : RemoteData Http.Error (List BasicList)
    }


type Msg
    = MsgDataReceived (RemoteData Http.Error (List BasicList))


retrieveLists : Cmd Msg
retrieveLists =
    Http.get
        { url = "http://localhost:8080/api/list"
        , expect = Http.expectJson upgradeToRemoteData dataListListDecoder
        }


upgradeToRemoteData : Result Http.Error (List BasicList) -> Msg
upgradeToRemoteData result =
    MsgDataReceived (RemoteData.fromResult result)


dataListListDecoder : Decoder (List BasicList)
dataListListDecoder =
    JD.list dataBasicListDecoder


dataBasicListDecoder : Decoder BasicList
dataBasicListDecoder =
    JD.map2 BasicList
        (field "id" int)
        (field "name" string)


initModel : () -> ( Model, Cmd Msg )
initModel _ =
    ( { result = RemoteData.NotAsked }, retrieveLists )


listRow : BasicList -> Styled.Html Msg
listRow infoList =
    Styled.tr []
        [ Styled.td
            [ tdStyle ]
            [ Styled.text infoList.name ]
        ]


view : Model -> Html.Html Msg
view model =
    case model.result of
        RemoteData.Failure error ->
            Html.text (getErrorMessage error)

        RemoteData.Success lists ->
            Styled.toUnstyled <|
                Styled.div
                    [ Attr.css
                        [ Tw.m_10 ]
                    ]
                    [ Css.Global.global Tw.globalStyles
                    , Styled.h1
                        [ mainHeadingStyle ]
                        [ Styled.text "Lists" ]
                    , Styled.table
                        [ tableStyle ]
                        [ Styled.thead
                            [ theadStyle ]
                            [ Styled.tr []
                                [ Styled.th
                                    [ thStyle ]
                                    [ Styled.text "Name" ]
                                ]
                            ]
                        , Styled.tbody [] (List.map listRow lists)
                        ]
                    ]

        RemoteData.Loading ->
            Html.text "Loading ..."

        RemoteData.NotAsked ->
            Html.text ""


update : Msg -> Model -> ( Model, Cmd Msg )
update msg model =
    case msg of
        MsgDataReceived result ->
            ( { model | result = result }, Cmd.none )


subscriptions : Model -> Sub msg
subscriptions _ =
    Sub.none


main : Program () Model Msg
main =
    Browser.element
        { init = initModel
        , view = view
        , update = update
        , subscriptions = subscriptions
        }
