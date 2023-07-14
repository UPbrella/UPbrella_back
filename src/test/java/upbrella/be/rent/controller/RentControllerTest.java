package upbrella.be.rent.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import upbrella.be.docs.RestDocsSupport;

import static org.junit.jupiter.api.Assertions.*;

public class RentControllerTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new RentController();
    }

    @DisplayName("우산 대여 요청 테스트")
    @Test
    void rendUmbrella() {
    }
}