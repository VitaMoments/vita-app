package nl.fbdevelopment.healthyplatform.data.repository

sealed class RepositoryResponse<out T> {
    data class Success<T>(val body: T) : RepositoryResponse<T>()

    sealed class Error(open val message: String) : RepositoryResponse<Nothing>() {
        /**
         * Fouten als response op user input
         * Bijvoorbeeld: UserEmail bestaat al
         */
        data class Conflict(val key: String, override val message: String) : Error(message)

        /**
         * Fouten die door de user-input ontstaan.
         * Bijvoorbeeld: invalid fields, wrong postcode.
         */
        data class InvalidData(val key: String, override val message: String) : Error(message)

        /**
         * Fouten waarbij de gebruiker iets fout doet,
         * zoals verkeerde login credentials.
         */
        data class Unauthorized(override val message: String) : Error(message)

        /**
         * Fouten door resources die ontbreken.
         * Bijv. productId niet gevonden.
         */
        data class NotFound(override val message: String) : Error(message)

        /**
         * Interne failures die niet door de gebruiker komen.
         */
        data class Internal(override val message: String = "Something went wrong on our server. Please try again later") : Error(message)
    }
}