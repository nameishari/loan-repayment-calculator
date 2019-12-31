package com.lendico.repayment.api;

import com.lendico.repayment.LoanRepaymentCalculatorApplication;
import com.lendico.repayment.dto.request.RepaymentPlanRequest;
import com.lendico.repayment.exception.BadRequestException;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static java.math.BigDecimal.valueOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.MatcherAssert.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = LoanRepaymentCalculatorApplication.class)
class RepaymentPlanIntegrationTest {

    private static final String GENERATE_PLAN_BASE_URL = "/generate-plan";

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    @Test
    void testShouldReturnRepaymentPlanWithFiveMonthDuration() {
        RepaymentPlanRequest request = buildRequest(valueOf(3000.0), 3.0, 5, OffsetDateTime.now().plusMonths(2));
        List<String> paymentDates =
             //@formatter:off
             given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
             .when()
                .post(GENERATE_PLAN_BASE_URL)
                .prettyPeek()
             .then()
                .statusCode(HttpStatus.OK.value())
                .body("borrowerPaymentAmount", contains(604.51f, 604.51f, 604.51f, 604.51f, 604.5f))
                .body("initialOutstandingPrincipal", contains(3000f, 2402.99f, 1804.49f, 1204.49f, 602.99f))
                .body("interest", contains(7.5f, 6.01f, 4.51f, 3.01f, 1.51f))
                .body("principal", contains(597.01f, 598.5f, 600f, 601.5f, 602.99f))
                .body("remainingOutstandingPrincipal", contains(2402.99f, 1804.49f, 1204.49f, 602.99f, 0f))
                .extract().jsonPath().getList("date");
             //@formatter:on
        verifyPaymentDates(request, paymentDates);
    }

    @Test
    void testShouldReturnRepaymentPlanWithYearDuration() {
        RepaymentPlanRequest request = buildRequest(valueOf(5000.0), 5.0, 12, OffsetDateTime.now());
        List<String> paymentDates =
             //@formatter:off
             given()
                .body(request)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
             .when()
                .post(GENERATE_PLAN_BASE_URL)
                .prettyPeek()
             .then()
                .statusCode(HttpStatus.OK.value())
                .body("borrowerPaymentAmount", contains(428.04f, 428.04f, 428.04f, 428.04f, 428.04f, 428.04f, 428.04f, 428.04f, 428.04f, 428.04f, 428.04f, 428.01f))
                .body("initialOutstandingPrincipal", contains(5000f, 4592.79f, 4183.89f, 3773.28f, 3360.96f, 2946.92f, 2531.16f, 2113.67f, 1694.44f, 1273.46f, 850.73f, 426.23f))
                .body("interest", contains(20.83f, 19.14f, 17.43f, 15.72f, 14f, 12.28f, 10.55f, 8.81f, 7.06f, 5.31f, 3.54f, 1.78f))
                .body("principal", contains(407.21f, 408.9f, 410.61f, 412.32f, 414.04f, 415.76f, 417.49f, 419.23f, 420.98f, 422.73f, 424.5f, 426.23f))
                .body("remainingOutstandingPrincipal", contains(4592.79f, 4183.89f, 3773.28f, 3360.96f, 2946.92f, 2531.16f, 2113.67f, 1694.44f, 1273.46f, 850.73f, 426.23f, 0f))
                .extract().jsonPath().getList("date");
             //@formatter:on
        verifyPaymentDates(request, paymentDates);
    }

    private void verifyPaymentDates(RepaymentPlanRequest request, List<String> paymentDates) {
        IntStream.range(0, paymentDates.size())
                .forEach(month -> {
                    OffsetDateTime expectedDate = request.getStartDate().plusMonths(month);
                    OffsetDateTime actualDate = OffsetDateTime.parse(paymentDates.get(month));
                    assertThat(actualDate.toLocalDate().equals(expectedDate.toLocalDate()), is(true));
                });
    }

    @Test
    void testShouldReturnBadRequestIfLoanAmountIsInvalid() {
        List<RepaymentPlanRequest> invalidRequestsWithMissingAttributes = Arrays.asList(
                buildRequest(valueOf(0.0), 2.0, 1, OffsetDateTime.now()),
                buildRequest(valueOf(-1.0), 2.0, 1, OffsetDateTime.now())
        );
        invalidRequestsWithMissingAttributes.forEach(request -> verifyBadRequest(request, "Loan amount should be greater than zero"));
    }

    @Test
    void testShouldReturnBadRequestIfNominalRateIsInvalid() {
        List<RepaymentPlanRequest> invalidRequestsWithMissingAttributes = Arrays.asList(
                buildRequest(valueOf(5000.0), 0.0, 1, OffsetDateTime.now()),
                buildRequest(valueOf(5000.0), -5.0, 1, OffsetDateTime.now())
        );
        invalidRequestsWithMissingAttributes.forEach(request -> verifyBadRequest(request, "Nominal Rate should be greater than zero"));
    }

    @Test
    void testShouldReturnBadRequestIfDurationIsInvalid() {
        List<RepaymentPlanRequest> invalidRequestsWithMissingAttributes = Arrays.asList(
                buildRequest(valueOf(5000.0), 5.0, -1, OffsetDateTime.now()),
                buildRequest(valueOf(5000.0), 5.0, 0, OffsetDateTime.now())
        );
        invalidRequestsWithMissingAttributes.forEach(request -> verifyBadRequest(request, "Duration should be at least one month"));
    }

    @Test
    void testShouldReturnBadRequestIfRequestIsMissingRequiredValues() {
        List<RepaymentPlanRequest> invalidRequestsWithMissingAttributes = Arrays.asList(
                buildRequest(null, 5.0, 0, OffsetDateTime.now()),
                buildRequest(valueOf(5000.0), null, 0, OffsetDateTime.now()),
                buildRequest(valueOf(5000.0), 5.0, null, OffsetDateTime.now()),
                buildRequest(valueOf(5000.0), 5.0, 0, null)
        );
        invalidRequestsWithMissingAttributes.forEach(request -> verifyBadRequest(request, "At least one of these details are missing"));
    }

    private void verifyBadRequest(RepaymentPlanRequest request, String reason) {
        //@formatter:off
        given()
            .body(request)
            .contentType(MediaType.APPLICATION_JSON_VALUE)
        .when()
            .post(GENERATE_PLAN_BASE_URL)
            .prettyPeek()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
            .body("exception", is(BadRequestException.class.getCanonicalName()))
            .body("reason", containsStringIgnoringCase(reason));
        //@formatter:on
    }
    private RepaymentPlanRequest buildRequest(BigDecimal loanAmount, Double nominalRate, Integer duration, OffsetDateTime startDate) {
        return new RepaymentPlanRequest(loanAmount, nominalRate, duration, startDate);
    }
}
