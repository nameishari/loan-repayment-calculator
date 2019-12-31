package com.lendico.repayment.service;

import com.lendico.repayment.dto.request.RepaymentPlanRequest;
import com.lendico.repayment.dto.response.RepaymentPlanDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.lendico.repayment.util.BigDecimalUtils.*;
import static java.math.BigDecimal.valueOf;

@Service
public class RepaymentPlanService {

    private static final int DAYS_IN_MONTH = 30;
    private static final int DAYS_IN_YEAR = 360;
    private static final int MONTHS_IN_YEAR = 12;

    public List<RepaymentPlanDTO> generate(final RepaymentPlanRequest request) {
        List<RepaymentPlanDTO> annuityLoanRepaymentPlan = new ArrayList<>();
        BigDecimal initialOutstandingPrincipal = request.getLoanAmount();
        final BigDecimal annuity = calculateAnnuity(request.getNominalRate(), request.getLoanAmount(), request.getDuration());
        for (int month = 0; month < request.getDuration(); month++) {
            RepaymentPlanDTO repayment = createRepayment(request, initialOutstandingPrincipal, month, annuity);
            initialOutstandingPrincipal = repayment.getRemainingOutstandingPrincipal();
            annuityLoanRepaymentPlan.add(repayment);
        }
        return annuityLoanRepaymentPlan;
    }

    private RepaymentPlanDTO createRepayment(final RepaymentPlanRequest request,
                                             final BigDecimal initialOutstandingPrincipal,
                                             final int month,
                                             final BigDecimal annuity) {
        final BigDecimal interest = calculateInterest(request.getNominalRate(), initialOutstandingPrincipal);
        final BigDecimal principal = calculatePrincipal(interest, annuity, initialOutstandingPrincipal);
        final BigDecimal borrowerPaymentOut = round(principal.add(interest), PRECISION);
        final BigDecimal remainingRemainingOutstandingPrincipal = calculateRemainingOutstandingPrincipal(principal, initialOutstandingPrincipal);
        return RepaymentPlanDTO.builder()
                .date(request.getStartDate().plusMonths(month))
                .interest(interest)
                .principal(principal)
                .borrowerPaymentAmount(borrowerPaymentOut)
                .remainingOutstandingPrincipal(remainingRemainingOutstandingPrincipal)
                .initialOutstandingPrincipal(initialOutstandingPrincipal)
                .build();
    }

    private static BigDecimal calculateInterest(final double nominalRate, final BigDecimal initialOutstandingPrincipal) {
        BigDecimal interest = new BigDecimal((nominalRate * DAYS_IN_MONTH * initialOutstandingPrincipal.doubleValue()) / DAYS_IN_YEAR);
        return divide(interest, valueOf(HUNDRED), PRECISION);
    }

    private static BigDecimal calculateAnnuity(final double nominalRate, final BigDecimal loanAmount, final int duration) {
        double nominalRatePerMonth = (nominalRate / HUNDRED) / MONTHS_IN_YEAR;
        double annuity = (loanAmount.doubleValue() * nominalRatePerMonth) / (1 - Math.pow(1 + nominalRatePerMonth, -duration));
        return round(valueOf(annuity), PRECISION);
    }

    private static BigDecimal calculatePrincipal(final BigDecimal interest, final BigDecimal annuity,
                                                 final BigDecimal initialOutstandingPrincipal) {
        BigDecimal principal = annuity.subtract(interest);
        return round(isGreaterThan(principal, initialOutstandingPrincipal) ? initialOutstandingPrincipal : principal, PRECISION);
    }

    private static BigDecimal calculateRemainingOutstandingPrincipal(final BigDecimal principal,
                                                                     final BigDecimal initialOutstandingPrincipal) {
        BigDecimal remainingOutstandingPrincipal = isLessThan(principal, initialOutstandingPrincipal)
                ? initialOutstandingPrincipal.subtract(principal)
                : ZERO;
        return round(remainingOutstandingPrincipal, PRECISION);
    }
}
