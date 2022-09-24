package org.wagham.db.exceptions

class InvalidGuildException(guildId: String) : Exception("Invalid Guild ID: $guildId")