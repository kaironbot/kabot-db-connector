package org.wagham.db.exceptions

class InvalidCredentialsException(guildId: String) : Exception("Invalid credentials for guild db: $guildId")