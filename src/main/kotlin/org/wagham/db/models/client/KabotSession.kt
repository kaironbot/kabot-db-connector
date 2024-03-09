package org.wagham.db.models.client

import com.mongodb.reactivestreams.client.ClientSession
import org.wagham.db.exceptions.TransactionAbortedException

data class KabotSession(
	val session: ClientSession
) {

	fun tryCommit(stepName: String, result: Boolean) {
		if(!result) {
			throw TransactionAbortedException(stepName)
		}
	}

	suspend fun tryCommit(stepName: String, result: suspend () -> Boolean) {
		if(!result()) {
			throw TransactionAbortedException(stepName)
		}
	}

}