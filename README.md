# javaBin Partner API

## Models

* Partner
    * Name of Partner

* Contacts
    * Name of Contact
    * E-mail of Contact
    * Tlf/Mob for Contact
    * Partner
    * Lists

* Lists
    * Name
    * Contacts

### Joins

The database layer responds with a row per joined result - so a partner with 3 contacts will give 3 partners - each
with one contact. To handle this - the interface Identifiable and the following utility function is used:

```kotlin
fun <T : Identifiable> List<T>.mergeFold(copy: (item1: T, item2: T) -> T)
```

This allows for simple merging to one entry with all values - e.g.

```kotlin
listOfPartners.mergeFold { p1, p2 ->
    p1.copy(contacts = p1.contacts + p2.contacts)
}
```

## Local DB

`docker compose up -d`

This will start a local postgres instance in docker exposing it on port 5555. User test, password test and db name
partner.

To use this db - pass the following environment variable:

`DB_URL=jdbc:postgresql://localhost:5555/partner`

## API

See swagger on http://localhost:8080/swagger-ui

Provide a current JWT via the authorize function.

## Auth

The app expects two environment variables - GITHUB_ID and GITHUB_SECRET

This connects `/login` to login with GitHub.

On return the application will exchange this for a local JWT.

When running locally - the static page served on / will display the current JWT if found.
This makes testing locally with swagger simpler.
