package upbrella.be.store.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.service.LockerService;
import upbrella.be.store.dto.request.CreateLockerRequest;
import upbrella.be.store.dto.request.UpdateLockerRequest;
import upbrella.be.store.dto.response.AllLockerResponse;
import upbrella.be.util.CustomResponse;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class LockerController {

    private final LockerService lockerService;

    @GetMapping("/admin/lockers")
    public ResponseEntity<CustomResponse<AllLockerResponse>> getLockers() {

        AllLockerResponse lockers = lockerService.findAll();

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "보관함 조회 성공",
                        lockers));
    }

    @PostMapping("/admin/lockers")
    public ResponseEntity<CustomResponse<Void>> createLocker(@RequestBody @Valid CreateLockerRequest request) {

        lockerService.createLocker(request);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "보관함 생성 성공",
                        null));
    }

    @PatchMapping("/admin/lockers/{lockerId}")
    public ResponseEntity<CustomResponse<Void>> updateLocker(@PathVariable Long lockerId, @RequestBody @Valid UpdateLockerRequest request) {

        lockerService.updateLocker(lockerId, request);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "보관함 업데이트 성공",
                        null));
    }

    @DeleteMapping("/admin/lockers/{lockerId}")
    public ResponseEntity<CustomResponse<Void>> deleteLocker(@PathVariable Long lockerId) {

        lockerService.deleteLocker(lockerId);

        return ResponseEntity
                .ok()
                .body(new CustomResponse<>(
                        "success",
                        200,
                        "보관함 삭제 성공",
                        null));
    }
}
