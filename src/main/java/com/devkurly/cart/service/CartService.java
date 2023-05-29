package com.devkurly.cart.service;

import com.devkurly.cart.domain.Cart;
import com.devkurly.cart.dto.CartProductResponseDto;
import com.devkurly.cart.dto.CartSaveRequestDto;
import com.devkurly.cart.exception.*;
import com.devkurly.global.ErrorCode;
import com.devkurly.mapper.CartMapper;
import com.devkurly.product.domain.ProductDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;

    @Transactional(readOnly = true)
    public List<Cart> viewAllCart(Integer user_id) {
        return cartMapper.findAllByUserId(user_id);
    }

    @Transactional(readOnly = true)
    public List<Cart> viewCheckedCart(CartSaveRequestDto requestDto) {
        List<Cart> cartList = cartMapper.findCheckedByCart(requestDto.toEntity());
        if (cartList.isEmpty()) {
            throw new EmptyCartException("장바구니가 비어 있습니다.", ErrorCode.EMPTY_CART_PRODUCT);
        }
        return cartList;
    }

    @Transactional(readOnly = true)
    public List<CartProductResponseDto> viewCheckedCartProduct(CartSaveRequestDto requestDto) {
        return cartMapper.joinCartProductByCart(requestDto.toEntity());
    }

    @Transactional(readOnly = true)
    public List<CartProductResponseDto> viewCartProduct(Integer user_id) {
        List<CartProductResponseDto> dtoList = cartMapper.joinCartProductByUserId(user_id);
        if (dtoList.isEmpty()) {
            throw new EmptyCartRestException("장바구니가 비어 있습니다.", ErrorCode.EMPTY_CART_PRODUCT);
        }
        return dtoList;
    }

    @Transactional(readOnly = true)
    public Cart checkProductStock(Cart cart) {
        ProductDto productDto = cartMapper.findProductByPdtId(cart.getPdt_id());
        Integer stock = productDto.getStock();
        if (cart.getPdt_qty() > stock) {
            cart.setPdt_qty(stock);
            throw new OutOfStockRestException("제품 재고가 부족합니다.", ErrorCode.OUT_OF_STOCK);
        }
        return cart;
    }

    @Transactional(readOnly = true)
    public Cart checkAddProductStock(Cart cart) {
        Integer addQty = cart.getPdt_qty();
        Integer cartQty = cartMapper.findByCart(cart).getPdt_qty();
        Integer stock = cartMapper.findProductByPdtId(cart.getPdt_id()).getStock();
        if ((cartQty + addQty) > stock) {
            throw new OutOfStockException("제품 재고가 부족합니다.", ErrorCode.OUT_OF_STOCK);
        }
        return cart;
    }


    public void addCart(CartSaveRequestDto requestDto) {
        try {
            Optional.ofNullable(cartMapper.findByCart(requestDto.toEntity()))
                    .ifPresent((cart -> {
                        throw new DuplicateCartException("이미 장바구니에 제품이 있습니다.", ErrorCode.DUPLICATE_CART_PRODUCT);
                    }));
            cartMapper.save(requestDto.toEntity());
            return;
        } catch (DuplicateCartException e) {
            requestDto.setPdt_qty(cartMapper.findByCart(requestDto.toEntity()).getPdt_qty() + requestDto.getPdt_qty());
        } catch (Exception e) {
            throw new RuntimeException();
        }
        cartMapper.update(requestDto.toEntity());
    }

    public int getCookieId(Cookie tempCart) {
        int id = 0;
        if (Optional.ofNullable(tempCart).isPresent()) {
            id = Integer.parseInt(tempCart.getValue());
        }
        return id;
    }

    public int getCookieId(Cookie tempCart, HttpServletResponse response) {
        int id;
        if (tempCart != null) {
            id = Integer.parseInt(tempCart.getValue());
        } else {
            int randomNumber;
            do {
                randomNumber = new Random().nextInt(1000000);
                try {
                    cartMapper.findById(randomNumber);
                } catch (NoSuchElementException ignored) {
                    break;
                }
            } while (true);

            Cookie newTempCart = new Cookie("tempCart", Integer.toString(randomNumber));
            newTempCart.setPath("/");
            response.addCookie(newTempCart);

            id = Integer.parseInt(newTempCart.getValue());
        }

        return id;
    }

    public void modifyCart(Cart cart) {
        cartMapper.update(cart);
    }

    public void removeOneCart(Cart cart) {
        cartMapper.deleteOne(cart);
    }

    public void removeCheckedCart(List<Cart> cartList) {
        for (Cart cart : cartList) {
            cartMapper.deleteOne(cart);
        }
    }

    public void removeCart(Integer user_id) {
        cartMapper.delete(user_id);
    }


}
