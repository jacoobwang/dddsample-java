package com.example.dddsample.infra.order.converter;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.example.dddsample.app.order.query.dto.OrderDetailQueryResult;
import com.example.dddsample.domain.order.Order;
import com.example.dddsample.domain.order.valueobject.MoneyVO;
import com.example.dddsample.infra.order.dataobject.OrderDO;
import com.example.dddsample.shared.order.OrderStatusEnum;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.WARN,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR
)
public interface OrderConverter {

    OrderDetailQueryResult toDetailQueryResult(OrderDO entity);

    default Order toDomain(OrderDO entity) {
        if (entity == null) {
            return null;
        }
        return Order.restore(
            entity.getId(),
            entity.getUserId(),
            entity.getSkuCode(),
            entity.getQuantity(),
            new MoneyVO(entity.getAmount(), entity.getCurrency()),
            OrderStatusEnum.valueOf(entity.getStatus()),
            entity.getPaymentTransactionId(),
            entity.getTrackingNumber(),
            entity.getCreatedAt()
        );
    }

    default OrderDO toDataObject(Order order) {
        if (order == null) {
            return null;
        }
        OrderDO entity = new OrderDO();
        entity.setId(order.id());
        entity.setUserId(order.userId());
        entity.setSkuCode(order.skuCode());
        entity.setQuantity(order.quantity());
        entity.setAmount(order.totalPrice().amount());
        entity.setCurrency(order.totalPrice().currency());
        entity.setStatus(order.status().name());
        entity.setPaymentTransactionId(order.paymentTransactionId());
        entity.setTrackingNumber(order.trackingNumber());
        entity.setCreatedAt(order.createdAt());
        return entity;
    }
}
