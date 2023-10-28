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



```
SELECT h.id   AS h_id,
       h.name AS h_name,
       h.vet_name,
       h.vet_number,
       h.farrier_name,
       h.farrier_number,
       h.active,
       h.born,
       h.sort_order,
       u.id   AS u_id,
       u.name AS u_name,
       u.number,
       u.owner
FROM horse h
         LEFT OUTER JOIN local_user u ON u.horses ~ h.id::varchar(3)
ORDER BY h.sort_order
```
