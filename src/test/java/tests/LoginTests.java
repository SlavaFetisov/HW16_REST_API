package tests;

import models.login.BlankCredentialsLoginResponseModel;
import models.login.LoginBodyModel;
import models.login.SuccessfulLoginResponseModel;
import models.login.WrongCredentialsLoginResponseModel;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static specs.login.LoginSpec.*;

public class LoginTests extends TestBase {

    @Test
    public void successfulLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(TestData.VALID_USERNAME, TestData.VALID_PASSWORD);

        SuccessfulLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(successfulLoginResponseSpec)
                .extract().as(SuccessfulLoginResponseModel.class);

        assertThat(loginResponse.access()).startsWith(TestData.EXPECTED_JWT_PREFIX);
        assertThat(loginResponse.refresh()).startsWith(TestData.EXPECTED_JWT_PREFIX);
        assertThat(loginResponse.access()).isNotEqualTo(loginResponse.refresh());
    }

    @Test
    public void wrongCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(TestData.VALID_USERNAME, TestData.WRONG_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);

        assertThat(loginResponse.detail()).isEqualTo(TestData.INVALID_CREDENTIALS_ERROR);
    }

    @Test
    public void wrongUsernameLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(TestData.WRONG_USERNAME, TestData.VALID_PASSWORD);

        WrongCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(wrongCredentialsLoginResponseSpec)
                .extract().as(WrongCredentialsLoginResponseModel.class);

        assertThat(loginResponse.detail()).isEqualTo(TestData.INVALID_CREDENTIALS_ERROR);
    }

    @Test
    public void emptyCredentialsLoginTest() {
        LoginBodyModel loginData = new LoginBodyModel(TestData.EMPTY_VALUE, TestData.EMPTY_VALUE);

        BlankCredentialsLoginResponseModel loginResponse = given(loginRequestSpec)
                .body(loginData)
                .when()
                .post("/auth/token/")
                .then()
                .spec(blankCredentialsLoginResponseSpec)
                .extract().as(BlankCredentialsLoginResponseModel.class);

        assertThat(loginResponse.username().get(0)).isEqualTo(TestData.BLANK_FIELD_ERROR);
        assertThat(loginResponse.password().get(0)).isEqualTo(TestData.BLANK_FIELD_ERROR);
    }
}
