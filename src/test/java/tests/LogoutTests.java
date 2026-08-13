package tests;

import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.logout.BlankRefreshTokenLogoutResponseModel;
import models.logout.InvalidRefreshTokenResponseModel;
import models.logout.LogoutBodyModel;
import models.logout.SuccessfulLogoutResponseModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static specs.login.LoginSpec.loginRequestSpec;
import static specs.login.LoginSpec.successfulLoginResponseSpec;
import static specs.logout.LogoutSpec.*;

public class LogoutTests extends TestBase {

    @Test
    public void successfulLogoutTest() {
        LoginBodyModel loginData = new LoginBodyModel(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract()
                .as(SuccessfulLoginResponseModel.class);

        LogoutBodyModel logoutData = new LogoutBodyModel(loginResponse.refresh());

        given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(successfulLogoutResponseSpec)
                .extract()
                .as(SuccessfulLogoutResponseModel.class);
    }

    @Test
    public void invalidRefreshTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(TestData.INVALID_REFRESH_TOKEN);

        InvalidRefreshTokenResponseModel logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(invalidRefreshTokenLogoutResponseSpec)
                .extract()
                .as(InvalidRefreshTokenResponseModel.class);

        assertThat(logoutResponse.detail()).isEqualTo(TestData.INVALID_TOKEN_ERROR);
    }

    @Test
    public void emptyRefreshTokenLogoutTest() {
        LogoutBodyModel logoutData = new LogoutBodyModel(TestData.EMPTY_VALUE);

        BlankRefreshTokenLogoutResponseModel logoutResponse = given(logoutRequestSpec)
                .body(logoutData)
                .when()
                .post("/auth/logout/")
                .then()
                .spec(blankRefreshTokenLogoutResponseSpec)
                .extract()
                .as(BlankRefreshTokenLogoutResponseModel.class);

        assertThat(logoutResponse.refresh().get(0)).isEqualTo(TestData.BLANK_FIELD_ERROR);
    }
}
