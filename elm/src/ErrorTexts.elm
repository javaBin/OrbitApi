module ErrorTexts exposing (..)

import Http


getErrorMessage : Http.Error -> String
getErrorMessage errorDetail =
    case errorDetail of
        Http.NetworkError ->
            "Connection error"

        Http.BadStatus errorStatus ->
            "Invalid server response " ++ String.fromInt errorStatus

        Http.Timeout ->
            "Request timeout"

        Http.BadUrl reasonError ->
            "Invalid request url " ++ reasonError

        Http.BadBody invalidData ->
            "Invalid data " ++ invalidData
