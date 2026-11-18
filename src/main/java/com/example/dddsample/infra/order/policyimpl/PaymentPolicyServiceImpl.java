package com.example.dddsample.infra.order.policyimpl;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.example.dddsample.domain.order.policy.PaymentPolicyService;
import com.example.dddsample.domain.order.policy.dto.ChargePolicy;
import com.example.dddsample.domain.order.policy.dto.ChargePolicyResult;

@Component
public class PaymentPolicyServiceImpl implements PaymentPolicyService {

    @Override
    public ChargePolicyResult charge(ChargePolicy policy) {
        return new ChargePolicyResult(true, "TX-" + UUID.randomUUID());
    }
}
