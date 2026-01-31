package com.example.employeecontact.domain.exception

class InvalidDataFormatException(
    message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause) {

    companion object {
        fun invalidCsv(reason: String): InvalidDataFormatException =
            InvalidDataFormatException("Invalid CSV format: $reason")

        fun invalidJson(reason: String): InvalidDataFormatException =
            InvalidDataFormatException("Invalid JSON format: $reason")

        fun invalidDate(dateString: String): InvalidDataFormatException =
            InvalidDataFormatException("Invalid date format: $dateString. Expected formats: yyyy.MM.dd or yyyy-MM-dd")

        fun invalidField(field: String, reason: String): InvalidDataFormatException =
            InvalidDataFormatException("Invalid $field: $reason")
    }
}
