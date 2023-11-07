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
        INSERT INTO contact (id, name, email, telephone, partner_id)
        VALUES
        (1, 'Contact 1', 'contact1@domain.tld', '12345678', 1),
        (2, 'Contact 2', 'contact2@domain.tld', '22345678', 1),
        (3, 'Contact 3', 'contact3@domain.tld', '32345678', 1),
        (4, 'Contact 4', 'contact4@domain.tld', '42345678', 1),
        (5, 'Contact 5', 'contact5@domain.tld', '52345678', 2)
    """.trimIndent()
