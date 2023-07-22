package org.wagham.db.exceptions

class NoActiveCharacterException(val playerId: String) : Exception("No Active Character for Player: $playerId")