package com.example.dddsample.domain.order.policy;

import com.example.dddsample.domain.order.policy.dto.ChargePolicy;
import com.example.dddsample.domain.order.policy.dto.ChargePolicyResult;

public interface PaymentPolicyService {

    ChargePolicyResult charge(ChargePolicy policy);
}
