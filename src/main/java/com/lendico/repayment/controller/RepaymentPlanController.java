package com.lendico.repayment.controller;

import com.lendico.repayment.dto.request.RepaymentPlanRequest;
import com.lendico.repayment.dto.response.RepaymentPlanDTO;
import com.lendico.repayment.service.RepaymentPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.lendico.repayment.validation.ValidationUtils.validateRequestUntilFirstError;

@RestController
@RequiredArgsConstructor
public class RepaymentPlanController {

    private final RepaymentPlanService repaymentPlanService;

    @PostMapping(value = "generate-plan")
    public List<RepaymentPlanDTO> generatePlan(@RequestBody RepaymentPlanRequest request) {
        validateRequestUntilFirstError(request, RepaymentPlanRequest.VALIDATION_RULES);
        return repaymentPlanService.generate(request);
    }
}
