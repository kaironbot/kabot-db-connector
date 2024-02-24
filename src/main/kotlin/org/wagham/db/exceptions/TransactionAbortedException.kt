package org.wagham.db.exceptions

class TransactionAbortedException(steps: Map<String, Boolean>) :
	Exception("Transaction failed, steps ${steps.filterValues { it }.keys.joinToString(", ")} failed")