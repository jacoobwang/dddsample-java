package com.example.dddsample;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.dddsample.adapter.auditlog.controller.AuditLogAdminController;
import com.example.dddsample.adapter.order.controller.OrderController;
import com.example.dddsample.adapter.order.dto.OrderDisplayResponse;
import com.example.dddsample.app.auditlog.command.dto.AuditLogSaveCommand;
import com.example.dddsample.app.order.command.dto.OrderCreateCommand;
import com.example.dddsample.app.order.command.dto.OrderShipCommand;
import com.example.dddsample.shared.BaseResponse;

@SpringBootTest
class OrderAndAuditLogFlowTest {

    @Autowired
    private OrderController orderController;

    @Autowired
    private AuditLogAdminController auditLogAdminController;

    @Test
    void orderLifecycleShouldWorkEndToEnd() {
        Long orderId = orderController.create(
            new OrderCreateCommand("SKU-WATCH-01", 2, new BigDecimal("399.00"), "USD")
        ).data();
        assertThat(orderId).isNotNull();

        String txId = orderController.pay(orderId).data();
        assertThat(txId).startsWith("TX-");

        orderController.ship(orderId, new OrderShipCommand("SF-12345678"));

        BaseResponse<OrderDisplayResponse> display = orderController.getDisplayDetail(orderId);
        assertThat(display.data().status()).isEqualTo("SHIPPED");
        assertThat(display.data().statusLabel()).isEqualTo("已发货");
        assertThat(display.data().trackingNumber()).isEqualTo("SF-12345678");
    }

    @Test
    void auditLogSimpleCrudShouldWorkEndToEnd() {
        Long logId = auditLogAdminController.create(
            new AuditLogSaveCommand("ORDER", "CREATE_ORDER", "created order 1001")
        ).data();
        assertThat(logId).isNotNull();

        auditLogAdminController.updateById(
            logId,
            new AuditLogSaveCommand("ORDER", "SHIP_ORDER", "shipped order 1001")
        );

        var detail = auditLogAdminController.getDetail(logId).data();
        assertThat(detail.action()).isEqualTo("SHIP_ORDER");

        var page = auditLogAdminController.queryPage(1, 10, null).data();
        assertThat(page.total()).isGreaterThanOrEqualTo(1);

        auditLogAdminController.batchDelete(List.of(logId));
        assertThat(auditLogAdminController.getDetail(logId).data()).isNull();
    }
}
