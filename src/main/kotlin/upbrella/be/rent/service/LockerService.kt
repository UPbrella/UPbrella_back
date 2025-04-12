package upbrella.be.rent.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest
import upbrella.be.rent.dto.response.LockerPasswordResponse
import upbrella.be.rent.entity.Locker
import upbrella.be.rent.exception.LockerCodeAlreadyIssuedException
import upbrella.be.rent.exception.LockerSignatureErrorException
import upbrella.be.rent.exception.NoSignatureException
import upbrella.be.rent.repository.LockerRepository
import upbrella.be.store.dto.request.CreateLockerRequest
import upbrella.be.store.dto.request.UpdateLockerCountRequest
import upbrella.be.store.dto.request.UpdateLockerRequest
import upbrella.be.store.dto.response.AllLockerResponse
import upbrella.be.store.dto.response.SingleLockerResponse
import upbrella.be.store.entity.StoreMeta
import upbrella.be.store.repository.StoreMetaReader
import upbrella.be.util.HotpGenerator
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.time.LocalDateTime

@Service
class LockerService(
    private val lockerRepository: LockerRepository,
    private val storeMetaReader: StoreMetaReader
) {

    fun findAll(): AllLockerResponse {
        val allLockers = lockerRepository.findAll()
        val singleLockers = allLockers.map { SingleLockerResponse.fromLocker(it) }
        return AllLockerResponse(singleLockers)
    }

    @Transactional
    fun createLocker(request: CreateLockerRequest) {
        isMultipleLockers(request.storeId)
        val storeMeta: StoreMeta = storeMetaReader.findById(request.storeId)
        val locker = Locker(storeMeta, 0L, request.secretKey, null, null)
        lockerRepository.save(locker)
    }

    @Transactional
    fun updateLocker(lockerId: Long, request: UpdateLockerRequest) {
        val existing = lockerRepository.findByStoreMetaId(request.storeId)
        if (existing.isPresent && existing.get().id != lockerId) {
            throw IllegalArgumentException("이미 보관함이 존재합니다.")
        }
        val storeMeta: StoreMeta = storeMetaReader.findById(request.storeId)
        lockerRepository.findById(lockerId).ifPresent { locker ->
            locker.updateLocker(storeMeta, request.secretKey)
        }
    }

    fun deleteLocker(lockerId: Long) {
        if (!lockerRepository.existsById(lockerId)) {
            throw IllegalArgumentException("해당 보관함이 존재하지 않습니다.")
        }
        lockerRepository.deleteById(lockerId)
    }

    @Transactional
    fun findLockerPassword(rentUmbrellaByUserRequest: RentUmbrellaByUserRequest): LockerPasswordResponse? =
        lockerRepository.findByStoreMetaId(rentUmbrellaByUserRequest.storeId)
            .map { getLockerPasswordResponse(it) }
            .orElse(null)

    fun validateLockerSignature(storeId: Long, salt: String?, signature: String?) {
        val lockerOptional = lockerRepository.findByStoreMetaId(storeId)
        if (lockerOptional.isEmpty && (salt != null || signature != null)) {
            throw NoSignatureException("구형 보관함은 salt와 signature가 없습니다.")
        }
        if (lockerOptional.isPresent) {
            if (salt == null || signature == null) {
                throw NoSignatureException("허위 반납을 위해 디지털 서명이 필요합니다. 관리자에게 문의주시기 바랍니다")
            }
            val locker = lockerOptional.get()
            val lockerSecretKey = locker.secretKey.uppercase()
            val upperSalt = salt.uppercase()
            validateSignature(signature, upperSalt, lockerSecretKey)
        }
    }

    private fun getLockerPasswordResponse(locker: Locker): LockerPasswordResponse {
        if (locker.lastAccess != null && locker.lastAccess!!.isAfter(LocalDateTime.now().minusMinutes(1))) {
            throw LockerCodeAlreadyIssuedException("UBU 우산 대여 실패: 1분 이내에 이미 대여된 우산")
        }
        val password = HotpGenerator.generate(locker.count.toInt(), locker.secretKey)
        locker.updateCount()
        locker.updateLastAccess(LocalDateTime.now())
        return LockerPasswordResponse(password)
    }

    private fun validateSignature(signature: String, salt: String, lockerSecretKey: String) {
        val lockerSignature = encodeHash(lockerSecretKey, salt)
        if (lockerSignature != signature) {
            throw LockerSignatureErrorException("우산 반납 실패: 잘못된 signature")
        }
    }

    private fun encodeHash(lockerSecretKey: String, salt: String): String {
        val digest = try {
            MessageDigest.getInstance("SHA-256")
        } catch (e: NoSuchAlgorithmException) {
            throw RuntimeException(e)
        }
        val input = "${lockerSecretKey.uppercase()}.${salt.uppercase()}"
        val encodedHash = digest.digest(input.toByteArray(StandardCharsets.UTF_8))
        val hexString = StringBuilder(2 * encodedHash.size)
        for (b in encodedHash) {
            val hex = Integer.toHexString(0xff and b.toInt())
            if (hex.length == 1) {
                hexString.append('0')
            }
            hexString.append(hex)
        }
        return hexString.toString().uppercase()
    }

    private fun isMultipleLockers(storeId: Long) {
        if (lockerRepository.findByStoreMetaId(storeId).isPresent) {
            throw IllegalArgumentException("이미 보관함이 존재합니다.")
        }
    }

    @Transactional
    fun updateCount(storeId: Long, request: UpdateLockerCountRequest): LockerPasswordResponse {
        val lockerOptional = lockerRepository.findByStoreMetaId(storeId)
        if (lockerOptional.isEmpty) {
            throw IllegalArgumentException("해당 보관함이 존재하지 않습니다.")
        }
        val locker = lockerOptional.get()
        locker.updateCount(request.count)
        val password = HotpGenerator.generate(locker.count.toInt(), locker.secretKey)
        locker.updateCount()
        locker.updateLastAccess(LocalDateTime.now())
        return LockerPasswordResponse(password)
    }
}
