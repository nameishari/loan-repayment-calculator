package com.lendico.repayment.dto.request;

import com.lendico.repayment.validation.ValidationRule;
import com.lendico.repayment.validation.ValidationRules;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.lendico.repayment.util.common.BigDecimalUtils.*;
import static com.lendico.repayment.util.common.ObjectUtils.allNotNull;

@Data
public class RepaymentPlanRequest {

    public static List<ValidationRule<RepaymentPlanRequest>> VALIDATION_RULES = ValidationRules.<RepaymentPlanRequest>newInstance()
            .addRule(req -> !allNotNull(req.getLoanAmount(), req.getNominalRate(), req.getDuration(), req.getStartDate()),
                    "At least one of these details are missing: loan amount, nominal rate, duration, start date")
            .addRule(req -> isNegative(req.getLoanAmount()), "Loan amount cannot be negative")
            .addRule(req -> isZero(req.getLoanAmount()), "Loan amount cannot be zero")
            .addRule(req -> isNegative(req.getNominalRate()), "Nominal Rate cannot be negative")
            .addRule(req -> req.getDuration() == 0, "Duration should be at least one month")
            .addRule(req -> req.getStartDate().toLocalDate().isBefore(LocalDate.now()),  "Start date cannot be in the past")
            .getRules();

    private BigDecimal loanAmount;
    private BigDecimal nominalRate;
    private Integer duration;
    private OffsetDateTime startDate;
}
