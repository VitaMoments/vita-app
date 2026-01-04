package eu.vitamoments.app.data.models.validation

import eu.vitamoments.app.data.repository.RepositoryResponse

sealed class ValidationError(open val key: String, open val message: String) {
    data class InvalidData(
        override val key: String,
        override val message: String
    ) : ValidationError(key, message)

    data class Conflict(
        override val key: String,
        override val message: String
    ) : ValidationError(key, message)
}

private fun List<ValidationError>.toRepoValidation(): RepositoryResponse.Error.Validation =
    RepositoryResponse.Error.Validation(
        errors = this.mapNotNull {
            when (it) {
                is ValidationError.InvalidData -> RepositoryResponse.Error.InvalidData(it.key, it.message)
                else -> null
            }
        }
    )
