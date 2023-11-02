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

## Local DB

`docker compose up -d`

This will start a local postgres instance in docker exposing it on port 5555. User test, password test and db name partner.

To use this db - pass the following environment variable:

`DB_URL=jdbc:postgresql://localhost:5555/partner`

## Examples

Given the following data:

| ID | Domain | Name |
|----|--------|------|
| 1  | foo.no | Foo  |
| 2  | bar.no | Bar  |

Then:

* http://localhost:8080/partner/

```json
[
	{
		"id": 1,
		"name": "Foo",
		"domainName": "foo.no",
		"contacts": [
		]
	},
	{
		"id": 2,
		"name": "Bar",
		"domainName": "bar.no",
		"contacts": [
		]
	}
]
```


* http://localhost:8080/partner/1

```json
{
    "id": 1,
    "name": "Foo",
    "domainName": "foo.no",
    "contacts": [
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