package com.example.dddsample.app.order.command.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import com.example.dddsample.app.order.command.OrderCommandService;
import com.example.dddsample.app.order.command.dto.OrderCreateCommand;
import com.example.dddsample.app.order.command.dto.OrderShipCommand;
import com.example.dddsample.domain.order.Order;
import com.example.dddsample.domain.order.policy.PaymentPolicyService;
import com.example.dddsample.domain.order.policy.dto.ChargePolicy;
import com.example.dddsample.domain.order.policy.dto.ChargePolicyResult;
import com.example.dddsample.domain.order.repo.OrderRepo;
import com.example.dddsample.domain.order.validator.OrderQuotaValidator;
import com.example.dddsample.domain.order.valueobject.MoneyVO;

import lombok.RequiredArgsConstructor;

@Service
@Validated
@RequiredArgsConstructor
public class OrderCommandServiceImpl implements OrderCommandService {

    private final OrderRepo orderRepo;
    private final PaymentPolicyService paymentPolicyService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(Long userId, OrderCreateCommand command) {
        long activeCount = orderRepo.findByUserId(userId).size();
        OrderQuotaValidator.requireWithinQuota(activeCount);
        Order order = Order.create(
            userId,
            command.skuCode(),
            command.quantity(),
            new MoneyVO(command.amount(), command.currency())
        );
        return orderRepo.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String pay(Long orderId, Long userId) {
        Order order = OrderQuotaValidator.requirePresent(orderRepo.findById(orderId));
        ChargePolicyResult chargeResult = paymentPolicyService.charge(
            new ChargePolicy(userId, order.totalPrice().amount(), order.totalPrice().currency())
        );
        order.markPaid(chargeResult.transactionId());
        orderRepo.save(order);
        return chargeResult.transactionId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void ship(Long orderId, OrderShipCommand command) {
        Order order = OrderQuotaValidator.requirePresent(orderRepo.findById(orderId));
        order.ship(command.trackingNumber());
        orderRepo.save(order);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Long orderId) {
        orderRepo.deleteById(orderId);
    }
}
