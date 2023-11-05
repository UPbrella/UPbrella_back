package upbrella.be.rent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import upbrella.be.rent.dto.request.RentUmbrellaByUserRequest;
import upbrella.be.rent.dto.response.LockerPasswordResponse;
import upbrella.be.rent.entity.Locker;
import upbrella.be.rent.exception.LockerCodeAlreadyIssuedException;
import upbrella.be.rent.repository.LockerRepository;
import upbrella.be.util.HotpGenerator;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LockerService {

    private final LockerRepository lockerRepository;

    @Transactional
    public LockerPasswordResponse checkSignature(RentUmbrellaByUserRequest rentUmbrellaByUserRequest) throws NoSuchAlgorithmException {

        Optional<Locker> lockerOptional = lockerRepository.findByStoreMetaId(rentUmbrellaByUserRequest.getStoreId());

        if (lockerOptional.isPresent()) {
            Locker locker = lockerOptional.get();
            String lockerSecretKey = locker.getSecretKey().toUpperCase();

            String salt = rentUmbrellaByUserRequest.getSalt().toUpperCase();
            String signature = rentUmbrellaByUserRequest.getSignature();

            try {
                // SHA-256 해시 생성
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                byte[] encodedhash = digest.digest((lockerSecretKey + salt).getBytes(StandardCharsets.UTF_8));

                // 바이트 배열을 16진수 문자열로 변환
                StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
                for (byte b : encodedhash) {
                    String hex = Integer.toHexString(0xff & b);
                    if (hex.length() == 1) {
                        hexString.append('0');
                    }
                    hexString.append(hex);
                }

                String lockerSignature = hexString.toString();

                if (!lockerSignature.equals(signature)) {
                    throw new IllegalArgumentException("UBU 우산 대여 실패: 잘못된 시크릿 키");
                }
                if (locker.getLastAccess() != null && locker.getLastAccess().isAfter(LocalDateTime.now().minusMinutes(1))) {
                    throw new LockerCodeAlreadyIssuedException("UBU 우산 대여 실패: 1분 이내에 이미 대여된 우산");
                }

                String password = HotpGenerator.generate((int) locker.getCount(), locker.getSecretKey());
                locker.updateCount();
                locker.updateLastAccess(LocalDateTime.now());

                return LockerPasswordResponse.builder()
                        .password(password)
                        .build();

            } catch (NoSuchAlgorithmException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    public static void main(String[] args) {

        String lockerSecretKey = "0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF0123456789ABCDEF";

        String salt = "abc".toUpperCase();

        try {
            // SHA-256 해시 생성
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest((lockerSecretKey + salt).getBytes(StandardCharsets.UTF_8));

            // 바이트 배열을 16진수 문자열로 변환
            StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
            for (byte b : encodedhash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            String lockerSignature = hexString.toString();


            System.out.println(lockerSignature);


        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}



