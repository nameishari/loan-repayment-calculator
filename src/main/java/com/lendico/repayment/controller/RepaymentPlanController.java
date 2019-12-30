package com.lendico.repayment.controller;

import com.lendico.repayment.dto.request.RepaymentPlanRequest;
import com.lendico.repayment.dto.response.RepaymentPlanDTO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

import static com.lendico.repayment.validation.ValidationUtils.validateRequestUntilFirstError;

@RestController
public class RepaymentPlanController {

    @PostMapping(value = "generate-plan")
    public List<RepaymentPlanDTO> generatePlan(@RequestBody RepaymentPlanRequest request) {
        validateRequestUntilFirstError(request, RepaymentPlanRequest.VALIDATION_RULES);
        return Collections.emptyList();
    }
}
