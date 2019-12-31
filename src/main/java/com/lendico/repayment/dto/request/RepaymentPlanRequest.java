package com.lendico.repayment.dto.request;

import com.lendico.repayment.validation.ValidationRule;
import com.lendico.repayment.validation.ValidationRules;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static com.lendico.repayment.util.ObjectUtils.allNotNull;
import static com.lendico.repayment.util.BigDecimalUtils.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepaymentPlanRequest {

    public static List<ValidationRule<RepaymentPlanRequest>> VALIDATION_RULES = ValidationRules.<RepaymentPlanRequest>newInstance()
            .addRule(req -> !allNotNull(req.getLoanAmount(), req.getNominalRate(), req.getDuration(), req.getStartDate()),
                    "At least one of these details are missing: loan amount, nominal rate, duration, start date")
            .addRule(req -> isNegative(req.getLoanAmount()) || isZero(req.getLoanAmount()), "Loan amount should be greater than zero") // or we can configure a minimum loan amount
            .addRule(req -> req.getNominalRate() <= 0, "Nominal Rate should be greater than zero")
            .addRule(req -> req.getDuration() <= 0, "Duration should be at least one month")
            .getRules();

    private BigDecimal loanAmount;
    private Double nominalRate;
    private Integer duration;
    private OffsetDateTime startDate;
}
