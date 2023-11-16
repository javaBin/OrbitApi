package no.java.partner

import org.intellij.lang.annotations.Language

@Language("PostgreSQL")
val createPartners =
    """
        INSERT INTO partner (id, name, domainName)
        VALUES
        (1, 'Partner 1', 'partner.1.tld'),
        (2, 'Partner 2', 'partner.2.tld'),
        (3, 'Partner 3', 'partner.3.tld')
    """.trimIndent()

@Language("PostgreSQL")
val createContacts =
    """
        INSERT INTO contact (id, name, email, telephone, source, partner_id)
        VALUES
        (1, 'Contact 1', 'contact1@domain.tld', '12345678', 'Source 1', 1),
        (2, 'Contact 2', 'contact2@domain.tld', '22345678', 'Source 2', 1),
        (3, 'Contact 3', 'contact3@domain.tld', '32345678', 'Source 3', 1),
        (4, 'Contact 4', 'contact4@domain.tld', '42345678', null, 1),
        (5, 'Contact 5', 'contact5@domain.tld', '52345678', null, 2)
    """.trimIndent()

@Language("PostgreSQL")
val createLists =
    """
        INSERT INTO list (id, name)
        VALUES
        (1, 'List 1'),
        (2, 'List 2'),
        (3, 'List 3')
    """.trimIndent()

@Language("PostgreSQL")
val createListContacts =
    """
        INSERT INTO contact_list (contact_id, list_id, subscribed)
        VALUES
        (1, 1, true),
        (1, 2, true),
        (1, 3, false),
        (3, 1, true)
    """.trimIndent()

@Language("PostgreSQL")
val resetSeqs =
    """
        SELECT setval('partner_id_seq', (SELECT MAX(id) FROM partner));
        SELECT setval('contact_id_seq', (SELECT MAX(id) FROM contact));
        SELECT setval('list_id_seq', (SELECT MAX(id) FROM list));
    """.trimIndent()
