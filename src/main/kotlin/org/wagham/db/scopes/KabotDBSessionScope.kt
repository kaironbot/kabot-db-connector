package org.wagham.db.scopes

import com.mongodb.client.model.InsertOneOptions
import com.mongodb.client.model.UpdateOptions
import kotlinx.coroutines.flow.Flow
import org.bson.types.ObjectId
import org.litote.kmongo.*
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.coroutine.insertOne
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.enums.CharacterStatus
import org.wagham.db.enums.CollectionNames
import org.wagham.db.exceptions.ResourceNotFoundException
import org.wagham.db.models.Character
import org.wagham.db.models.CharacterUpdate
import org.wagham.db.models.Errata
import org.wagham.db.models.Session
import org.wagham.db.models.client.TransactionResult
import org.wagham.db.models.dto.SessionOutcome
import org.wagham.db.models.embed.LabelStub
import org.wagham.db.pipelines.sessions.PlayerMasteredSessions
import org.wagham.db.pipelines.sessions.TimePassedInGame
import org.wagham.db.utils.daysInBetween
import org.wagham.db.utils.isSuccessful
import java.util.Date

class KabotDBSessionScope(
    override val client: KabotMultiDBClient
) : KabotDBScope<Session> {

    override val collectionName = CollectionNames.SESSIONS.stringValue

    override fun getMainCollection(guildId: String): CoroutineCollection<Session> =
        client.getGuildDb(guildId).getCollection(collectionName)

    suspend fun getSessionByUid(guildId: String, sessionUid: Int) =
        getMainCollection(guildId).findOne(Session::uid eq sessionUid)

    /**
     * Retrieves all the [Session]s with a certain title in a guild.
     *
     * @param guildId the guild id.
     * @param title the session title to search.
     * @return a [Flow] of [Session]s.
     */
    fun getSessionsByTitle(guildId: String, title: String): Flow<Session> =
        getMainCollection(guildId).find(Session::title eq title).toFlow()

    fun getAllSessions(guildId: String, startDate: Date? = null, endDate: Date? = null) =
        getMainCollection(guildId).find(
            and(
                listOfNotNull(
                    startDate?.let { Session::date gte it },
                    endDate?.let { Session::date lte it }
                ).takeIf { it.isNotEmpty() } ?: listOf(Session::date ne null)
            )
        ).toFlow()

    fun getAllMasteredSessions(guildId: String, player: String) =
        getMainCollection(guildId)
            .aggregate<PlayerMasteredSessions>(PlayerMasteredSessions.getPipeline(player))
            .toFlow()

    suspend fun getTimePassedInGame(guildId: String, startDate: Date, endDate: Date) =
        getMainCollection(guildId)
            .aggregate<TimePassedInGame>(TimePassedInGame.getPipeline(startDate, endDate))
            .toList()
            .takeIf { it.isNotEmpty() }
            ?.fold(daysInBetween(startDate, endDate) + 1) { acc, it ->
                acc + it.days
            } ?: (daysInBetween(startDate, endDate) + 1)

    /**
     * Registers a new session in a guild. When doing so, it updates all the character updating the status (if the
     * character died in the session, with the new exp and with the last played date.
     * It also updated the character of the master who mastered the session changing its last mastered date and adding
     * an exp prize, if any.
     *
     * @param guildId the id of the guild.
     * @param masterId the id of the character of the master of the session.
     * @param masterReward an exp reward for the master.
     * @param title the title of the session.
     * @param date the date of the session.
     * @param outcomes a [List] of [SessionOutcome], one for each participating character.
     * @param labels a [Set] of [LabelStub] to assign to the session.
     * @return a [TransactionResult]
     */
    suspend fun insertSession(
        guildId: String,
        masterId: String,
        masterReward: Int,
        title: String,
        date: Date,
        outcomes: List<SessionOutcome>,
        labels: Set<LabelStub>
    ): TransactionResult = client.transaction(guildId) { session ->
        val db = client.getGuildDb(guildId)
        val newUid = getMainCollection(guildId).find().descendingSort(Session::uid).first()?.uid?.plus(1) ?: 0
        val playersUpdateStep = outcomes.all {
            val character = db.getCollection<Character>(CollectionNames.CHARACTERS.stringValue).findOne(
                session,
                Character::id eq it.characterId
            ) ?: throw ResourceNotFoundException(it.characterId, "Characters")
            val updatedCharacter = character.copy(
                status = if (it.isDead) CharacterStatus.dead else character.status,
                errata = if(it.isDead) character.errata + Errata(date = date, description = "Dead in session $title", statusChange = CharacterStatus.dead)
                    else character.errata,
                sessionMS = character.sessionMS + it.exp,
                lastPlayed = date
            )
            db.getCollection<Character>(CollectionNames.CHARACTERS.stringValue).updateOne(
                session,
                Character::id eq it.characterId,
                updatedCharacter
            ).isSuccessful()
        }
        val masterCharacter = db.getCollection<Character>(CollectionNames.CHARACTERS.stringValue).findOne(
            session,
            Character::id eq masterId
        ) ?: throw ResourceNotFoundException(masterId, "Characters")
        val masterUpdateStep = db.getCollection<Character>(CollectionNames.CHARACTERS.stringValue).updateOne(
            session,
            Character::id eq masterId,
            masterCharacter.copy(
                masterMS = masterCharacter.masterMS + masterReward,
                lastMastered = date
            )
        ).isSuccessful()

        val sId = ObjectId.get()
        val insertSessionStep = getMainCollection(guildId).updateOne(
            session,
            Session::id eq sId,
            Session(
                id = sId,
                master = masterId,
                date = date,
                title = title,
                duration = 0,
                uid = newUid,
                characters = outcomes.map {
                    CharacterUpdate(it.characterId, it.exp, !it.isDead)
                },
                labels = labels
            ),
            UpdateOptions().upsert(true)
        ).upsertedId != null
        insertSessionStep && playersUpdateStep && masterUpdateStep
    }
}