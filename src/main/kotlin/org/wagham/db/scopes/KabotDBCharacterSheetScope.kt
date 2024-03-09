package org.wagham.db.scopes

import com.mongodb.client.model.UpdateOptions
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CollectionNames
import org.wagham.db.models.CharacterSheet
import org.wagham.db.models.embed.CharacterToken
import org.wagham.db.utils.Base64String
import org.wagham.db.utils.CharacterId
import org.wagham.db.utils.isSuccessful

class KabotDBCharacterSheetScope(
	override val client: KabotMultiDBClient
) : KabotDBScope<CharacterSheet> {

	override val collectionName = CollectionNames.CHARACTER_SHEETS.stringValue

	override fun getMainCollection(guildId: String): CoroutineCollection<CharacterSheet> =
		client.getGuildDb(guildId).getCollection(collectionName)

	/**
	 * Retrieves the [CharacterSheet] for the character whose id is passed as parameter, if there is one.
	 *
	 * @param guildId the id of the guild where the character is.
	 * @param characterId the id of the character.
	 * @return a [CharacterSheet] or null if there is no registered sheet for that character.
	 */
	suspend fun getCharacterSheet(guildId: String, characterId: CharacterId): CharacterSheet? =
		getMainCollection(guildId).findOne(CharacterSheet::id eq characterId)

	/**
	 * Updates a [CharacterSheet] or creates it if it does not exist.
	 * The [CharacterSheet.id] will be forced to [characterId].
	 *
	 * @param guildId the guild where to update the character sheet.
	 * @param characterId the character owner of the [CharacterSheet].
	 * @param sheet a [CharacterSheet] to create or update.
	 * @return true if the operation succeeded and false otherwise.
	 */
	suspend fun createOrUpdateCharacterSheet(guildId: String, characterId: CharacterId, sheet: CharacterSheet): Boolean =
		getMainCollection(guildId).updateOne(
			CharacterSheet::id eq characterId,
			sheet.copy(id = characterId),
			UpdateOptions().upsert(true)
		).isSuccessful()

	/**
	 * Updates the [CharacterSheet.token] for a character or creates a new [CharacterSheet] for them.
	 *
	 * @param guildId the guild where to update the [CharacterSheet].
	 * @param characterId the character owner of the [CharacterSheet].
	 * @param token a base64-encoded image.
	 * @param mimeType the mime type of the image.
	 * @return true if the operation succeeded and false otherwise.
	 */
	suspend fun setToken(guildId: String, characterId: CharacterId, token: Base64String, mimeType: String): Boolean {
		val characterToken = CharacterToken(image = token, mimeType = mimeType)
		val currentSheet = getCharacterSheet(guildId, characterId)?.copy(token = characterToken)
			?: CharacterSheet(id = characterId, token = characterToken)
		return createOrUpdateCharacterSheet(guildId, characterId, currentSheet)
	}
}