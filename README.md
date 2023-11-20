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

## API

```
/partner
  - GET - list partners
  - POST- create partner
/partner/{PID}
  - GET - partner with contacts
/partner/{PID}/contact
  - POST - create contact
/list
  - GET - list mailing lists
  - POST - create mailing list
/list/{LID}
  - GET - get mailing list with contacts
/list/{LID}/contact/{CID}
  - POST - subscribe contact to a list (create the assocication - sets subscribe true)
/list/{LID}/contact/{CID}/subscribe
  - PATCH - set subscribe flag true on a subscription
/list/{LID}/contact/{CID}/unsubscribe
  - PATCH - set subscribe false true on a subscription   
```

## Examples

Given the following data:

### Partner

| ID | Domain        | Name      |
|----|---------------|-----------|
| 1  | partner.1.tld | Partner 1 |
| 2  | partner.2.tld | Partner 2 |
| 3  | partner.3.tld | Partner 3 |

### Contact

| ID | Name      | E-mail              | Telephone | Source   | Partner |
|----|-----------|---------------------|-----------|----------|---------|
| 1  | Contact 1 | contact1@domain.tld | 12345678  | Source 1 | 1       |
| 2  | Contact 2 | contact2@domain.tld | 22345678  | Source 2 | 1       |
| 3  | Contact 3 | contact3@domain.tld | 32345678  | Source 3 | 1       |
| 4  | Contact 3 | contact4@domain.tld | 42345678  | null     | 1       |
| 5  | Contact 3 | contact5@domain.tld | 52345678  | null     | 2       |

### List

| ID | Name   |
|----|--------|
| 1  | List 1 |
| 2  | List 2 |

### Contact List

| Contact Id | List Id | Subscribed |
|------------|---------|------------|
| 1          | 1       | true       | 
| 1          | 2       | true       |
| 1          | 3       | false      |
| 3          | 1       | true       |

Then:

### http://localhost:8080/partner

Fetches list of partners

```json
[
  {
    "id": 1,
    "name": "Partner 1",
    "domainName": [
      "partner.1.tld"
    ]
  },
  {
    "id": 2,
    "name": "Partner 2",
    "domainName": [
      "partner.2.tld"
    ]
  },
  {
    "id": 3,
    "name": "Partner 3",
    "domainName": [
      "partner.3.tld"
    ]
  }
]
```

### http://localhost:8080/partner/X

Fetches partner with basic contact info and for each contact basic list info for subscribed lists

e.g X=1

```json
{
  "id": 1,
  "name": "Partner 1",
  "domainName": [
    "partner.1.tld"
  ],
  "contacts": [
    {
      "id": 4,
      "name": "Contact 4",
      "email": "contact4@domain.tld",
      "telephone": "42345678",
      "source": null,
      "lists": []
    },
    {
      "id": 2,
      "name": "Contact 2",
      "email": "contact2@domain.tld",
      "telephone": "22345678",
      "source": "Source 2",
      "lists": []
    },
    {
      "id": 3,
      "name": "Contact 3",
      "email": "contact3@domain.tld",
      "telephone": "32345678",
      "source": "Source 3",
      "lists": [
        {
          "id": 1,
          "name": "List 1"
        }
      ]
    },
    {
      "id": 1,
      "name": "Contact 1",
      "email": "contact1@domain.tld",
      "telephone": "12345678",
      "source": "Source 1",
      "lists": [
        {
          "id": 1,
          "name": "List 1"
        },
        {
          "id": 2,
          "name": "List 2"
        }
      ]
    }
  ]
}
```

#### If partner not found:

404 Not found

```json
{
	"message": "Partner not found"
}
```

### POST http://localhost:8080/partner

Creates a partner

Body:

```json
{
  "name": "Test Partner",
  "domainName": ["test1.domain.tld", "test2.domain.tld"]
}
```

Response:

```json
{
  "id": 4,
  "name": "Test Partner",
  "domainName": [
    "test1.domain.tld",
    "test2.domain.tld"
  ]
}
```

### POST http://localhost:8080/partner/X/contact/

Creates a contact for a partner - returns the updated partner

e.g. X=4

Body:

```json
{
  "name": "Test Contact",
  "email": "test@test.domain.tld",
  "source": "Test Source"
}
```

Response:

```json
{
  "id": 4,
  "name": "Test Partner",
  "domainName": [
    "test1.domain.tld",
    "test2.domain.tld"
  ]
  "contacts": [
    {
      "id": 6,
      "name": "Test Contact",
      "email": "test@test.domain.tld",
      "telephone": null,
      "source": "Test Source",
      "lists": []
    }
  ]
}
```

### `http://localhost:8080/list`

Fetches a list of lists

```json
[
  {
    "id": 1,
    "name": "List 1"
  },
  {
    "id": 2,
    "name": "List 2"
  },
  {
    "id": 3,
    "name": "List 3"
  }
]
```

### POST http://localhost:8080/list

Creates a list

Body:

```json
{
  "name": "Test List"
}
```

Response:

```json
{
  "id": 4,
  "name": "Test List"
}
```

### http://localhost:8080/list/X

Fetches a list. 

e.g X=1

```json
{
  "id": 1,
  "name": "List 1",
  "contacts": [
    {
      "id": 3,
      "name": "Contact 3",
      "email": "contact3@domain.tld",
      "telephone": "32345678",
      "source": "Source 3"
    },
    {
      "id": 1,
      "name": "Contact 1",
      "email": "contact1@domain.tld",
      "telephone": "12345678",
      "source": "Source 1"
    }
  ],
  "unsubscribed": []
}
```

e.g. X=3

```json
{
  "id": 3,
  "name": "List 3",
  "contacts": [],
  "unsubscribed": [
    {
      "id": 1,
      "name": "Contact 1",
      "email": "contact1@domain.tld",
      "telephone": "12345678",
      "source": "Source 1"
    }
  ]
}
```

#### If list not found:

404 Not found

```json
{
  "message": "List not found"
}
```

### POST http://localhost:8080/list/X/contact/Y

Subscribes a contact to a list

e.g. X=2 and Y=2 (list 2 has contact 1 already)

```json
{
    "id": 2,
    "name": "List 2",
    "contacts": [
        {
            "id": 2,
            "name": "Contact 2",
            "email": "contact2@domain.tld",
            "telephone": "22345678",
            "source": "Source 2"
        },
        {
            "id": 1,
            "name": "Contact 1",
            "email": "contact1@domain.tld",
            "telephone": "12345678",
            "source": "Source 1"
        }
    ],
    "unsubscribed": []
}
```

### PATCH http://localhost:8080/list/X/contact/Y/subscribe

e.g. X=3 and Y=1

```json
{
    "id": 3,
    "name": "List 3",
    "contacts": [
        {
            "id": 1,
            "name": "Contact 1",
            "email": "contact1@domain.tld",
            "telephone": "12345678",
            "source": "Source 1"
        }
    ],
    "unsubscribed": []
}
```

### PATCH http://localhost:8080/list/X/contact/Y/unsubscribe

e.g. X=1 and Y=3

```json
{
    "id": 1,
    "name": "List 1",
    "contacts": [
        {
            "id": 1,
            "name": "Contact 1",
            "email": "contact1@domain.tld",
            "telephone": "12345678",
            "source": "Source 1"
        }
    ],
    "unsubscribed": [
        {
            "id": 3,
            "name": "Contact 3",
            "email": "contact3@domain.tld",
            "telephone": "32345678",
            "source": "Source 3"
        }
    ]
}
```