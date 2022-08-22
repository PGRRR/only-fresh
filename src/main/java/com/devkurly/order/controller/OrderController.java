package com.devkurly.order.controller;

import com.devkurly.cart.domain.Cart;
import com.devkurly.cart.dto.CartProductResponseDto;
import com.devkurly.cart.dto.CartSaveRequestDto;
import com.devkurly.cart.service.CartService;
import com.devkurly.order.domain.Order;
import com.devkurly.order.domain.OrderProduct;
import com.devkurly.order.dto.OrderResponseDto;
import com.devkurly.order.dto.OrderUpdateRequestDto;
import com.devkurly.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static com.devkurly.member.controller.MemberController.getMemberResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    @GetMapping("")
    public String requestOrder(@RequestParam(value = "checked") List<String> chArr, Model model, HttpSession session) {
        Integer user_id = getMemberResponse(session);

        // 유저 기반 주문 생성
        orderService.addOrder(user_id);
        Integer order_id = orderService.checkRecentOrderId(user_id);

        int pdt_id;
        List<CartProductResponseDto> checkedCartProduct = new ArrayList<>();
        for (String str : chArr) {
            pdt_id = Integer.parseInt(str);
            List<Cart> carts = cartService.viewCheckedCart(new CartSaveRequestDto(user_id, pdt_id));
            for (Cart cart : carts) {
                orderService.insertOrderProduct(new OrderProduct(order_id, cart.getPdt_id(), cart.getPdt_qty()));
                List<CartProductResponseDto> viewCartProduct = cartService.viewCheckedCartProduct(new CartSaveRequestDto(cart));
                checkedCartProduct.addAll(viewCartProduct);
//                cartService.removeOneCart(cart);
            }
        }

        // 생성한 주문 기반으로 주문서 출력
        List<OrderResponseDto> orderResponseDto = orderService.viewOrderProduct(order_id);
        int sum = 0;
        for (CartProductResponseDto responseDto : checkedCartProduct) {
            sum += responseDto.getSel_price() * responseDto.getPdt_qty();
        }
        Order order = orderService.checkOrder(order_id);
        order.setAll_amt(sum);
        orderService.modifyOrder(new OrderUpdateRequestDto(order));
        DecimalFormat df = new DecimalFormat("###,###");
        model.addAttribute("sum", df.format(sum));
        model.addAttribute("order_id", order_id);
        model.addAttribute("cart", checkedCartProduct);
        model.addAttribute("order", orderResponseDto);
        return "order/order";
    }
//
//
//    @GetMapping("/1/{ord_id}")
//    public String viewOrder(@PathVariable Integer ord_id) {
//
//        return "/order/order";
//    }
//
//    @PostMapping("/3")
//    public String modifyOrder(OrderUpdateRequestDto requestDto) {
//        orderService.modifyOrder(requestDto.toEntity());
//        return "redirect:/orders";
//    }
//
//    @PostMapping("/4/{ord_id}")
//    public String deleteOrder(@PathVariable Integer ord_id, HttpSession session) {
//        orderService.removeOrder((Integer) session.getAttribute("memberResponse"), ord_id);
//        return "redirect:/orders";
//    }
}
