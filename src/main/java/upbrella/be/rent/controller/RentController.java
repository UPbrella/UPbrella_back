package upbrella.be.rent.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.user.dto.response.UserInfoResponse;
import upbrella.be.util.CustomResponse;

import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/rent")
public class RentController {

    @PostMapping
    public ResponseEntity<CustomResponse> findUserInfo(RentUmbrellaByUserRequest rentUmbrellaByUserRequest, HttpSession httpSession) {

        return ResponseEntity
                .ok()
                .body(new CustomResponse(
                        "success",
                        200,
                        "우산 대여 성공"
                ));
    }
}
