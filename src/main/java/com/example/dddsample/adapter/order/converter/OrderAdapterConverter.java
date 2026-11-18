package com.example.dddsample.adapter.order.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import com.example.dddsample.adapter.order.dto.OrderDisplayResponse;
import com.example.dddsample.app.order.query.dto.OrderDetailQueryResult;

@Mapper(
    componentModel = "spring",
    unmappedSourcePolicy = ReportingPolicy.WARN,
    unmappedTargetPolicy = ReportingPolicy.ERROR,
    typeConversionPolicy = ReportingPolicy.ERROR
)
public interface OrderAdapterConverter {

    @Mapping(target = "statusLabel", source = "status", qualifiedByName = "resolveStatusLabel")
    OrderDisplayResponse toDisplayResponse(OrderDetailQueryResult result);

    @Named("resolveStatusLabel")
    default String resolveStatusLabel(String status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case "CREATED" -> "待支付";
            case "PAID" -> "已支付";
            case "SHIPPED" -> "已发货";
            case "CANCELLED" -> "已取消";
            default -> status;
        };
    }
}
