package eu.vitamoments.app.data.models.validation

import eu.vitamoments.app.data.mapper.extension_functions.isEmail
import eu.vitamoments.app.data.models.domain.user.User

fun User.validate(): List<ValidationError> = buildList {
    // Whitespace aan begin/eind expliciet afkeuren
    if (email != email.trim()) {
        add(ValidationError.InvalidData("email", "Email has whitespace at start or end"))
    }

    if (email.isBlank()) {
        add(ValidationError.InvalidData("email", "Email is blank"))
    } else if (!email.isEmail()) {
        add(ValidationError.InvalidData("email", "Email not valid: $email"))
    }

    if (createdAt > updatedAt) {
        add(ValidationError.InvalidData("timestamps", "createdAt shouldn't be after updatedAt"))
    }

    if (deletedAt != null && deletedAt < updatedAt) {
        add(ValidationError.InvalidData("timestamps", "deletedAt shouldn't be before updatedAt"))
    }
}
