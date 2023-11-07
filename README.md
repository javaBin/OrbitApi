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

This will start a local postgres instance in docker exposing it on port 5555. User test, password test and db name partner.

To use this db - pass the following environment variable:

`DB_URL=jdbc:postgresql://localhost:5555/partner`

## Examples

Given the following data:

| ID | Domain        | Name      |
|----|---------------|-----------|
| 1  | partner.1.tld | Partner 1 |
| 2  | partner.2.tld | Partner 2 |
| 3  | partner.3.tld | Partner 3 |

and

| ID | Name      | E-mail              | Telephone | Partner |
|----|-----------|---------------------|-----------|---------|
| 1  | Contact 1 | contact1@domain.tld | 12345678  | 1       |
| 2  | Contact 2 | contact2@domain.tld | 22345678  | 1       |
| 3  | Contact 3 | contact3@domain.tld | 32345678  | 1       |
| 4  | Contact 3 | contact4@domain.tld | 42345678  | 1       |
| 5  | Contact 3 | contact5@domain.tld | 52345678  | 2       |

Then:

* http://localhost:8080/partner/

```json
[
  {
    "id": 1,
    "name": "Partner 1",
    "domainName": "partner.1.tld",
    "contacts": []
  },
  {
    "id": 2,
    "name": "Partner 2",
    "domainName": "partner.2.tld",
    "contacts": []
  },
  {
    "id": 3,
    "name": "Partner 3",
    "domainName": "partner.3.tld",
    "contacts": []
  }
]
```


* http://localhost:8080/partner/2

```json
{
  "id": 2,
  "name": "Partner 2",
  "domainName": "partner.2.tld",
  "contacts": [
    {
      "id": 5,
      "name": "Contact 5",
      "email": "contact5@domain.tld",
      "telephone": "52345678",
      "partner": {
        "id": 2,
        "name": "Partner 2",
        "domainName": "partner.2.tld",
        "contacts": [
          {
            "id": 5,
            "name": "Contact 5",
            "email": "contact5@domain.tld",
            "telephone": "52345678",
            "partner": null,
            "lists": []
          }
        ]
      },
      "lists": []
    }
  ]
}
```

* http://localhost:8080/partner/3

404 Not found

```json
{
	"message": "Partner not found"
}
```

* http://localhost:8080/partner/x

400 Bad Request

```json
{
	"message": "ID Parameter missing"
}
```