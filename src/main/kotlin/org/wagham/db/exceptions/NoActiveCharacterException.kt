package org.wagham.db.exceptions

class NoActiveCharacterException(playerId: String) : Exception("No Active Character for Player: $playerId")