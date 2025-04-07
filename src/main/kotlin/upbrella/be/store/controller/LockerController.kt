package upbrella.be.store.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import upbrella.be.rent.dto.response.LockerPasswordResponse
import upbrella.be.rent.service.LockerService
import upbrella.be.store.dto.request.CreateLockerRequest
import upbrella.be.store.dto.request.UpdateLockerCountRequest
import upbrella.be.store.dto.request.UpdateLockerRequest
import upbrella.be.store.dto.response.AllLockerResponse
import upbrella.be.util.CustomResponse
import javax.validation.Valid

@RestController
class LockerController(private val lockerService: LockerService) {

    @GetMapping("/admin/lockers")
    fun getLockers(): ResponseEntity<CustomResponse<AllLockerResponse>> {
        val lockers = lockerService.findAll()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "보관함 조회 성공",
                lockers
            ))
    }

    @PostMapping("/admin/lockers")
    fun createLocker(@RequestBody @Valid request: CreateLockerRequest): ResponseEntity<CustomResponse<Void>> {
        lockerService.createLocker(request)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "보관함 생성 성공",
                null
            ))
    }

    @PatchMapping("/admin/lockers/{lockerId}")
    fun updateLocker(
        @PathVariable lockerId: Long,
        @RequestBody @Valid request: UpdateLockerRequest
    ): ResponseEntity<CustomResponse<Void>> {
        lockerService.updateLocker(lockerId, request)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "보관함 업데이트 성공",
                null
            ))
    }

    @DeleteMapping("/admin/lockers/{lockerId}")
    fun deleteLocker(@PathVariable lockerId: Long): ResponseEntity<CustomResponse<Void>> {
        lockerService.deleteLocker(lockerId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "보관함 삭제 성공",
                null
            ))
    }

    @PatchMapping("/lockers/{storeMetaId}")
    fun updateCounter(
        @PathVariable storeMetaId: Long,
        @RequestBody count: UpdateLockerCountRequest
    ): ResponseEntity<CustomResponse<LockerPasswordResponse>> {
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "보관함 카운터 업데이트 성공",
                lockerService.updateCount(storeMetaId, count)
            ))
    }
}
