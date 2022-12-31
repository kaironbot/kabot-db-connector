package org.wagham.db.exceptions

class ResourceNotFoundException(resourceId: String, collectionName: String)
    : Exception("Resource $resourceId not found on $collectionName collection")