package upbrella.be.rent.controller

import org.slf4j.LoggerFactory
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import upbrella.be.rent.dto.request.HistoryFilterRequest
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest
import upbrella.be.rent.dto.request.ReturnUmbrellaByUserRequest
import upbrella.be.rent.dto.response.*
import upbrella.be.rent.service.ConditionReportService
import upbrella.be.rent.service.ImprovementReportService
import upbrella.be.rent.service.LockerService
import upbrella.be.rent.service.RentService
import upbrella.be.slack.service.SlackAlarmService
import upbrella.be.user.dto.response.SessionUser
import upbrella.be.user.service.UserService
import upbrella.be.util.CustomResponse
import javax.servlet.http.HttpSession
import javax.validation.Valid

@RestController
class RentController(
    private val conditionReportService: ConditionReportService,
    private val improvementReportService: ImprovementReportService,
    private val rentService: RentService,
    private val userService: UserService,
    private val slackAlarmService: SlackAlarmService,
    private val lockerService: LockerService
) {
    private val log = LoggerFactory.getLogger(RentController::class.java)

    @GetMapping("/rent/form/{umbrellaId}")
    fun findRentForm(@PathVariable umbrellaId: Long): ResponseEntity<CustomResponse<RentFormResponse>> {
        val rentForm = rentService.findRentForm(umbrellaId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "대여 폼 조회 성공",
                rentForm
            ))
    }

    @GetMapping("/return/form/{storeId}")
    fun findReturnForm(
        @PathVariable storeId: Long,
        httpSession: HttpSession,
        @RequestParam(required = false) salt: String?,
        @RequestParam(required = false) signature: String?
    ): ResponseEntity<CustomResponse<ReturnFormResponse>> {
        val user = httpSession.getAttribute("user") as SessionUser
        val userToReturn = userService.findUserById(user.id)

        val returnForm = rentService.findReturnForm(storeId, userToReturn, salt!!, signature!!)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "반납 폼 조회 성공",
                returnForm
            ))
    }

    @PostMapping("/rent")
    fun rentUmbrellaByUser(@RequestBody @Valid rentUmbrellaByUserRequest: RentUmbrellaByUserRequest, httpSession: HttpSession): ResponseEntity<CustomResponse<LockerPasswordResponse>> {
        val user = httpSession.getAttribute("user") as SessionUser
        val userToRent = userService.findUserById(user.id)

        val lockerPasswordResponse = lockerService.findLockerPassword(rentUmbrellaByUserRequest)

        rentService.addRental(rentUmbrellaByUserRequest, userToRent)

        log.info("UBU 우산 대여 성공")

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "우산 대여 성공",
                lockerPasswordResponse
            ))
    }

    @PatchMapping("/rent")
    fun returnUmbrellaByUser(
        @RequestBody @Valid
        returnUmbrellaByUserRequest: ReturnUmbrellaByUserRequest,
        httpSession: HttpSession
    ): ResponseEntity<CustomResponse<Unit>> {
        val user = httpSession.getAttribute("user") as SessionUser
        val userToReturn = userService.findUserById(user.id)

        rentService.returnUmbrellaByUser(userToReturn, returnUmbrellaByUserRequest)
        val unrefundedRentCount = rentService.countUnrefundedRent()

        slackAlarmService.notifyReturn(unrefundedRentCount)
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "우산 반납 성공"
            ))
    }

    @GetMapping("/admin/rent/histories")
    fun findRentalHistory(
        @ModelAttribute filter: HistoryFilterRequest,
        pageable: Pageable
    ): ResponseEntity<CustomResponse<RentalHistoriesPageResponse>> {
        val histories = rentService.findAllHistories(filter, pageable)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "어드민 대여 내역 조회 성공",
                histories
            ))
    }

    @GetMapping("/admin/rent/histories/status")
    fun findConditionReports(): ResponseEntity<CustomResponse<ConditionReportPageResponse>> {
        val conditionReports = conditionReportService.findAll()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "상태 신고 내역 조회 성공",
                conditionReports
            ))
    }

    @GetMapping("/admin/rent/histories/improvements")
    fun findImprovements(): ResponseEntity<CustomResponse<ImprovementReportPageResponse>> {
        val improvementReports = improvementReportService.findAll()

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "개선 요청 내역 조회 성공",
                improvementReports
            ))
    }

    @PatchMapping("/admin/rent/histories/refund/{historyId}")
    fun refundRent(@PathVariable historyId: Long, httpSession: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        val loginedUser = httpSession.getAttribute("user") as SessionUser

        rentService.checkRefund(historyId, loginedUser.id)
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "환급 확인 성공"
            ))
    }

    @PatchMapping("/admin/rent/histories/payment/{historyId}")
    fun checkPayment(@PathVariable historyId: Long, httpSession: HttpSession): ResponseEntity<CustomResponse<Unit>> {
        val loginedUser = httpSession.getAttribute("user") as SessionUser

        rentService.checkPayment(historyId, loginedUser.id)
        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "입금 확인 성공"
            ))
    }

    @DeleteMapping("/admin/rent/histories/{historyId}/account")
    fun deleteBankAccount(@PathVariable historyId: Long): ResponseEntity<CustomResponse<Unit>> {
        rentService.deleteBankAccount(historyId)

        return ResponseEntity
            .ok()
            .body(CustomResponse(
                "success",
                200,
                "대여 기록의 계좌 삭제 성공"
            ))
    }
}
