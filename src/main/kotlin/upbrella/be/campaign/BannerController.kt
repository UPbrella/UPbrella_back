package upbrella.be.campaign

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.server.ResponseStatusException
import upbrella.be.util.CustomResponse
import java.net.URI
import java.nio.file.Files
import java.nio.file.Path
import java.time.Clock
import java.time.LocalDateTime
import java.util.UUID
import javax.validation.Valid
import javax.imageio.ImageIO

@RestController
class BannerController(
    private val repository: BannerRepository,
    private val clock: Clock,
    @Value("\${upbrella.uploads-directory:./uploads}") private val uploadsDirectory: String
) {
    @GetMapping("/banners")
    fun active(): CustomResponse<List<Banner>> {
        val now = LocalDateTime.now(clock)
        return response(repository.findAllByDeletedIsFalseOrderByDisplayOrderAscIdAsc().filter {
            it.enabled && (it.startsAt == null || !now.isBefore(it.startsAt)) && (it.endsAt == null || now.isBefore(it.endsAt))
        })
    }

    @GetMapping("/admin/banners")
    fun all() = response(repository.findAllByDeletedIsFalseOrderByDisplayOrderAscIdAsc())

    @PostMapping("/admin/banners")
    fun create(@Valid @RequestBody request: BannerRequest): CustomResponse<Banner> {
        validate(request)
        return response(repository.save(Banner(request.title, request.imageUrl, request.targetUrl,
            request.displayOrder, request.enabled, request.startsAt, request.endsAt)))
    }

    @PatchMapping("/admin/banners/{id}")
    fun update(@PathVariable id: Long, @Valid @RequestBody request: BannerRequest): CustomResponse<Banner> {
        validate(request)
        val banner = repository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        if (banner.deleted) throw ResponseStatusException(HttpStatus.NOT_FOUND)
        banner.title = request.title; banner.imageUrl = request.imageUrl; banner.targetUrl = request.targetUrl
        banner.enabled = request.enabled; banner.displayOrder = request.displayOrder
        banner.startsAt = request.startsAt; banner.endsAt = request.endsAt
        return response(repository.save(banner))
    }

    @DeleteMapping("/admin/banners/{id}")
    fun delete(@PathVariable id: Long): CustomResponse<Map<String, Boolean>> {
        val banner = repository.findById(id).orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND) }
        banner.deleted = true
        repository.save(banner)
        return response(mapOf("deleted" to true))
    }

    @PostMapping("/admin/banners/image", consumes = ["multipart/form-data"])
    fun upload(@RequestParam("image") file: MultipartFile): CustomResponse<Map<String, String>> {
        if (file.isEmpty || file.size > 5 * 1024 * 1024) invalid("5MB 이하의 이미지 파일을 선택해 주세요.")
        val bytes = file.bytes
        val input = ImageIO.createImageInputStream(bytes.inputStream())
        val extension = input.use { stream ->
            val readers = ImageIO.getImageReaders(stream)
            if (!readers.hasNext()) invalid("PNG 또는 JPEG 이미지를 선택해 주세요.")
            val reader = readers.next()
            try {
                reader.input = stream
                if (reader.getWidth(0).toLong() * reader.getHeight(0) > 20_000_000) invalid("이미지 해상도가 너무 큽니다.")
                when (reader.formatName.lowercase()) { "png" -> "png"; "jpeg", "jpg" -> "jpg"; else -> invalid("PNG 또는 JPEG 이미지를 선택해 주세요.") }
            } finally { reader.dispose() }
        }
        val directory = Path.of(uploadsDirectory).toAbsolutePath().normalize()
        Files.createDirectories(directory)
        val name = "${UUID.randomUUID()}.$extension"
        Files.write(directory.resolve(name), bytes)
        return response(mapOf("imageUrl" to "/uploads/$name"))
    }

    private fun validate(request: BannerRequest) {
        if (!safeUrl(request.targetUrl, true)) invalid("이동 링크는 앱 내 경로 또는 HTTPS 주소여야 합니다.")
        if (!safeUrl(request.imageUrl, false)) invalid("이미지 주소는 HTTPS 주소 또는 업로드 이미지여야 합니다.")
        if (request.startsAt != null && request.endsAt != null && !request.endsAt.isAfter(request.startsAt)) invalid("종료 시각은 시작 시각보다 늦어야 합니다.")
    }

    private fun safeUrl(value: String, internal: Boolean): Boolean {
        if (value.any { it.isISOControl() || it == '\\' }) return false
        if (value.startsWith("/") && !value.startsWith("//")) return internal || value.matches(Regex("/uploads/[a-f0-9-]+\\.(png|jpg)"))
        return runCatching { URI(value).let { it.scheme == "https" && !it.host.isNullOrBlank() && it.userInfo == null } }.getOrDefault(false)
    }
    private fun invalid(message: String): Nothing = throw ResponseStatusException(HttpStatus.BAD_REQUEST, message)
    private fun <T> response(data: T) = CustomResponse("success", 200, "배너 요청 완료", data)
}
