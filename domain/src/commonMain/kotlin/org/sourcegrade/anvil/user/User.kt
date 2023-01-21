package org.sourcegrade.anvil.user

import java.time.OffsetDateTime

interface User {
    val name: String
    val createdAt: OffsetDateTime
}
