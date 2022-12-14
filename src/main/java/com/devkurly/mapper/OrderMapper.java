package com.devkurly.mapper;

import com.devkurly.order.domain.Order;
import com.devkurly.order.domain.OrderProduct;
import com.devkurly.order.dto.OrderProductNameResponseDto;
import com.devkurly.order.dto.OrderResponseDto;
import com.devkurly.product.domain.ProductDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderMapper {

    Integer add(Integer user_id);
    Integer findByUserId(Integer user_id);
    Order findByOrderId(Integer ord_id);
    ProductDto findByPdtId(Integer pdt_id);
    Integer insertOrderProduct(OrderProduct orderProduct);
    List<OrderResponseDto> joinOrderProduct(Integer order_id);
    List<OrderProductNameResponseDto> joinOrderProductName(Integer ord_id);
    Integer updateOrder(Order order);
    Integer updateAmt(Order order);
    Integer delete(@Param("user_id") Integer user_id, @Param("ord_id") Integer ord_id);

}
