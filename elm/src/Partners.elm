module Partners exposing (..)

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


type alias BasicPartner =
    { id : Int
    , name : String
    , domainName : Maybe String
    }


type alias Model =
    { result : RemoteData Http.Error (List BasicPartner)
    }


type Msg
    = MsgDataReceived (RemoteData Http.Error (List BasicPartner))


retrievePartners : Cmd Msg
retrievePartners =
    Http.get
        { url = "http://localhost:8080/api/partner"
        , expect = Http.expectJson upgradeToRemoteData dataPartnerListDecoder
        }


upgradeToRemoteData : Result Http.Error (List BasicPartner) -> Msg
upgradeToRemoteData result =
    MsgDataReceived (RemoteData.fromResult result)


dataPartnerListDecoder : Decoder (List BasicPartner)
dataPartnerListDecoder =
    JD.list dataBasicPartnerDecoder


dataBasicPartnerDecoder : Decoder BasicPartner
dataBasicPartnerDecoder =
    JD.map3 BasicPartner
        (field "id" int)
        (field "name" string)
        (maybe (field "domainName" string))


initModel : () -> ( Model, Cmd Msg )
initModel _ =
    ( { result = RemoteData.NotAsked }, retrievePartners )


partnerRow : BasicPartner -> Styled.Html Msg
partnerRow partner =
    Styled.tr []
        [ Styled.td
            [ tdStyle ]
            [ Styled.text partner.name ]
        , Styled.td
            [ tdStyle ]
            [ case partner.domainName of
                Just aDomain ->
                    Styled.text aDomain

                Nothing ->
                    Styled.text ""
            ]
        ]


view : Model -> Html.Html Msg
view model =
    case model.result of
        RemoteData.Failure error ->
            Html.text (getErrorMessage error)

        RemoteData.Success partners ->
            Styled.toUnstyled <|
                Styled.div
                    [ Attr.css
                        [ Tw.m_10 ]
                    ]
                    [ Css.Global.global Tw.globalStyles
                    , Styled.h1
                        [ mainHeadingStyle ]
                        [ Styled.text "Partners" ]
                    , Styled.table
                        [ tableStyle ]
                        [ Styled.thead
                            [ theadStyle ]
                            [ Styled.tr []
                                [ Styled.th
                                    [ thStyle ]
                                    [ Styled.text "Name" ]
                                , Styled.th
                                    [ thStyle ]
                                    [ Styled.text "Domain" ]
                                ]
                            ]
                        , Styled.tbody [] (List.map partnerRow partners)
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
