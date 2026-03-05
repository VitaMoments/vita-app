package eu.vitamoments.app.data.tables

import eu.vitamoments.app.data.models.enums.UserRole
import eu.vitamoments.app.data.models.enums.PrivacyStatus
import eu.vitamoments.app.data.tables.base.BaseUUIDTable
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.datetime

object UsersTable : BaseUUIDTable("users") {
//    Auth
    val email = varchar("email", 150).uniqueIndex()
    val username = varchar("username", 100).uniqueIndex()
    val password = varchar("password", 255)
    val role = enumerationByName<UserRole>("role", 10).default(UserRole.USER)
    val emailVerifiedAt = datetime("email_verified_at").nullable()

//    Profile
    val firstname = varchar("first_name", 80).nullable()
    val lastname = varchar("last_name", 80).nullable()
    val alias = varchar("alias", 100).nullable()
    val bio = varchar("bio", 255).nullable()
    val phone = varchar("phone", 32).nullable()
    val birthDate = date("birth_date").nullable()
    val imageUrl = varchar("image_url", 255).nullable()
    val coverImageUrl = varchar("cover_image_url", 255).nullable()

//    localization
    val locale = varchar("locale", 50).nullable()
    val timeZone = varchar("time_zone", 64).nullable()

//    privacy
    val detailsPrivacy = enumerationByName<PrivacyStatus>("details_privacy", 16).default(PrivacyStatus.PRIVATE)
}