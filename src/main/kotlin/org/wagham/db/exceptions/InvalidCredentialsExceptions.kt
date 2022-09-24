package org.wagham.db.exceptions

class InvalidCredentialsExceptions(guildId: String) : Exception("Invalid credentials for guild db: $guildId")