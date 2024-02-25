package org.wagham.db.scopes

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.models.CharacterSheet
import org.wagham.db.models.MongoCredentials
import org.wagham.db.uuid
import java.util.Base64

class KabotDBCharacterSheetScopeTest : StringSpec() {
	private val client = KabotMultiDBClient(
		MongoCredentials(
			"ADMIN",
			System.getenv("DB_TEST_USER").shouldNotBeNull(),
			System.getenv("DB_TEST_PWD").shouldNotBeNull(),
			System.getenv("TEST_DB").shouldNotBeNull(),
			System.getenv("DB_TEST_IP").shouldNotBeNull(),
			System.getenv("DB_TEST_PORT").shouldNotBeNull().toInt(),
		)
	)
	private val guildId = System.getenv("TEST_DB_ID").shouldNotBeNull()

	init {
		testCharacterSheets()
	}

	private fun StringSpec.testCharacterSheets() {

		"Cannot get a Character Sheet that does not exist" {
			client.characterSheetsScope.getCharacterSheet(guildId, uuid()) shouldBe null
		}

		"Can create a Character Sheet and update the token" {
			val fakeBase64Image = Base64.getEncoder().encodeToString(uuid().toByteArray())
			val characterId = uuid()
			val characterSheet = CharacterSheet(id = characterId)
			client.characterSheetsScope.createOrUpdateCharacterSheet(guildId, characterId, characterSheet) shouldBe true
			client.characterSheetsScope.getCharacterSheet(guildId, characterId) shouldBe characterSheet

			client.characterSheetsScope.setToken(guildId, characterId, fakeBase64Image) shouldBe true
			client.characterSheetsScope.getCharacterSheet(guildId, characterId)?.token shouldBe fakeBase64Image
		}

	}
}