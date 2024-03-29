package com.devkurly.cart.controller;

import com.devkurly.cart.domain.Cart;
import com.devkurly.cart.dto.CartSaveRequestDto;
import com.devkurly.cart.service.CartService;
import com.devkurly.member.dto.MemberMainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/carts")
public class CartController {

    private final CartService cartService;

    @GetMapping("")
    public String viewCart() {
        return "/cart/cart";
    }

    @PostMapping("/{pdt_id}")
    public String addCart(@PathVariable Integer pdt_id, @CookieValue(value = "tempCart", required = false) Cookie tempCart, CartSaveRequestDto requestDto, HttpServletResponse response, HttpSession session) {
        if (requestDto.getPdt_qty() < 1) {
            requestDto.setPdt_qty(1);
        }
        int id = getId(tempCart, response, session);
        requestDto.saveCart(id, pdt_id);
        cartService.checkProductStock(requestDto.toEntity());
        cartService.addCart(requestDto);
        return "redirect:/carts";
    }

    @GetMapping("/delete/{ptd_id}")
    public String removeOneCart(@PathVariable Integer ptd_id, @CookieValue(value = "tempCart", required = false) Cookie tempCart, HttpServletResponse response, HttpSession session) {
        int id = getId(tempCart, response, session);
        Cart cart = Cart.builder()
                .user_id(id)
                .pdt_id(ptd_id)
                .build();
        cartService.removeOneCart(cart);
        return "redirect:/carts";
    }

    private int getId(Cookie tempCart, HttpServletResponse response, HttpSession session) {
        int id;
        MemberMainResponseDto memberResponse = (MemberMainResponseDto) session.getAttribute("memberResponse");
        if (Optional.ofNullable(memberResponse).isPresent()) {
            id = memberResponse.getUser_id();
        } else {
            id = cartService.getCookieId(tempCart, response);
        }
        return id;
    }
}
