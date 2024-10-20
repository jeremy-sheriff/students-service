package org.app.users.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import org.jasypt.encryption.StringEncryptor
import org.springframework.stereotype.Component

@Converter
@Component
class EncryptionConverter(
    private val encryptor: StringEncryptor  // Inject the encryptor
) : AttributeConverter<String, String> {

    override fun convertToDatabaseColumn(attribute: String?): String? {
        return attribute?.let { encryptor.encrypt(it) }
    }

    override fun convertToEntityAttribute(dbData: String?): String? {
        return dbData?.let { encryptor.decrypt(it) }
    }
}
