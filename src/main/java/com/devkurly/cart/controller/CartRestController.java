package com.devkurly.cart.controller;

import com.devkurly.cart.domain.Cart;
import com.devkurly.cart.dto.CartProductResponseDto;
import com.devkurly.cart.dto.CartSaveRequestDto;
import com.devkurly.cart.service.CartService;
import com.devkurly.member.dto.MemberMainResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartRestController {

    private final CartService cartService;

    @GetMapping("/view")
    public ResponseEntity<List<CartProductResponseDto>> viewCart(@CookieValue(value = "tempCart", required = false) Cookie tempCart, HttpServletResponse response, HttpSession session) {
        int id = getId(tempCart, response, session);
        return new ResponseEntity<>(cartService.viewCartProduct(id), HttpStatus.OK);
    }

    @PostMapping("/qty")
    public CartProductResponseDto modifyCartQty(@RequestBody CartProductResponseDto responseDto, @CookieValue(value = "tempCart", required = false) Cookie tempCart, HttpServletResponse response, HttpSession session) {
        int id = getId(tempCart, response, session);
        responseDto.setUser_id(id);
        Cart cart = cartService.checkProductStock(responseDto.toEntity());
        responseDto.setPdt_qty(cart.getPdt_qty());
        if (responseDto.getPdt_qty() < 1) {
            responseDto.setPdt_qty(1);
        }
        cartService.modifyCart(responseDto.toEntity());
        return responseDto;
    }

    @PostMapping("/checked")
    public void removeCheckedCart(@CookieValue(value = "tempCart", required = false) Cookie tempCart, @RequestParam(value="checked[]" , required = false) List<String> chArr, HttpServletResponse response, HttpSession session) {
        int id = getId(tempCart, response, session);
        List<Cart> cartList = new ArrayList<>();
        for (String s : chArr) {
            int pdt_id = Integer.parseInt(s);
            Cart cart = Cart.builder()
                    .user_id(id)
                    .pdt_id(pdt_id)
                    .build();
            cartList.add(cart);
        }
        cartService.removeCheckedCart(cartList);
    }

    private int getId(Cookie tempCart, HttpServletResponse response, HttpSession session) {
        int id;
        MemberMainResponseDto memberMainResponseDto = (MemberMainResponseDto) session.getAttribute("memberResponse");
        if (Optional.ofNullable(memberMainResponseDto).isPresent()) {
            id = memberMainResponseDto.getUser_id();
        } else {
            id = cartService.getCookieId(tempCart, response);
        }
        return id;
    }

    @PostMapping
    public void addProductInCart(@PathVariable String memberId, @Valid @RequestBody CartSaveRequestDto requestDto) {
        if (requestDto.getPdt_qty() < 1) {
            requestDto.setPdt_qty(1);
        }
//        int id = getId(tempCart, response, session);
//        requestDto.saveCart(id, pdt_id);
        cartService.checkProductStock(requestDto.toEntity());
        cartService.addCart(requestDto);
    }

}
