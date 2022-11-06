import io.kotest.matchers.ints.shouldBeGreaterThan
import kotlinx.coroutines.flow.count
import org.wagham.db.KabotMultiDBClient
import org.wagham.db.KabotMultiDBClientTest

fun KabotMultiDBClientTest.testBounties(
    client: KabotMultiDBClient,
    guildId: String
) {
    "getAllBounties should be able to get all the bounties" {
        client.bountiesScope.getAllBounties(guildId).count() shouldBeGreaterThan 0
    }
}