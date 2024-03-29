package com.devkurly.global;

import com.devkurly.cart.exception.*;
import com.devkurly.member.exception.DuplicateMemberException;
import com.devkurly.member.exception.SignInException;
import com.devkurly.member.exception.SignUpException;
import com.devkurly.order.exception.AddressException;
import com.devkurly.order.exception.OrderException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public void exceptionCatcher(HttpServletResponse response) throws IOException {
        response.sendRedirect("/");
    }

    @ExceptionHandler(EmptyCartException.class)
    public void cartCatcher(HttpServletResponse response) throws IOException {
        System.out.println("GlobalExceptionHandler: 장바구니가 비어 있습니다. (redirect)");
        // redirect 작동
        response.sendRedirect("/carts?error=1");
    }

    @ExceptionHandler(EmptyCartRestException.class)
    public ResponseEntity<String> cartRestCatcher() {
        System.out.println("GlobalExceptionHandler: 장바구니가 비어 있습니다. (rest)");
        return ResponseEntity.badRequest().body(ErrorCode.EMPTY_CART_PRODUCT.getMessage());
    }

    @ExceptionHandler(MaxCartException.class)
    public void cartMaxCatcher(HttpServletResponse response) throws IOException {
        System.out.println("GlobalExceptionHandler: 장바구니 최대치에 도달 했습니다. (redirect)");
        // redirect 작동 안함
        response.sendRedirect("/carts?error=4");
    }

    @ExceptionHandler(OutOfStockRestException.class)
    public ResponseEntity<String> productRestCatcher(Exception e) {
        System.out.println("GlobalExceptionHandler: 제품 재고가 부족합니다.");
        return ResponseEntity.badRequest().body(ErrorCode.OUT_OF_STOCK.getMessage());
    }

    @ExceptionHandler(OutOfStockException.class)
    public void productCatcher(HttpServletResponse response) throws IOException {
        System.out.println("GlobalExceptionHandler: 장바구니 최대치에 도달 했습니다. (redirect)");
        // redirect 작동 안함
        response.sendRedirect("/carts?error=5");
    }


    @ExceptionHandler(SignInException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> signInCatcher(HttpServletResponse response, HttpServletRequest request, Exception e) throws IOException {
        System.out.println("GlobalExceptionHandler: 로그인에 실패했습니다.");
        String parameter = request.getParameter("coupn_nm");
        if (Optional.ofNullable(parameter).isPresent()) {
            String coupn_nm = URLEncoder.encode(parameter, StandardCharsets.UTF_8);
            response.sendRedirect("/members?error=1&toURL=" + request.getRequestURL() + "?coupn_nm=" + coupn_nm);
        } else {
            response.sendRedirect("/members?error=1&toURL=" + request.getRequestURL());
        }
        return ResponseEntity.badRequest().body(ErrorCode.SIGN_IN_FAIL.getMessage());
    }

    @ExceptionHandler(DuplicateMemberException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> duplicateCatcher(HttpServletResponse response, Exception e) throws IOException {
        System.out.println("GlobalExceptionHandler: 이미 등록된 이메일입니다.");
        response.sendRedirect("/members/signup?error=1");
        return ResponseEntity.badRequest().body(ErrorCode.SIGN_UP_FAIL.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<String> validCatcher(HttpServletResponse response, Exception e) throws IOException {
        System.out.println("GlobalExceptionHandler: 잘못된 값이 입력 되었습니다.");
        response.sendRedirect("/members/signup?error=2");
        return ResponseEntity.badRequest().body(ErrorCode.SIGN_UP_FAIL.getMessage());
    }

    @ExceptionHandler(SignUpException.class)
    protected ResponseEntity<String> signUpCatcher(HttpServletResponse response, Exception e) throws IOException {
        System.out.println("GlobalExceptionHandler: 잘못된 값이 입력 되었습니다.");
        response.sendRedirect("/members/signup?error=3");
        return ResponseEntity.badRequest().body(ErrorCode.SIGN_UP_FAIL.getMessage());
    }

    @ExceptionHandler(OrderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    protected ResponseEntity<String> orderCatcher(HttpServletResponse response, Exception e) throws IOException {
        System.out.println("GlobalExceptionHandler: 주문 중 오류가 발생 했습니다.");
        response.sendRedirect("/carts?error=1");
        return ResponseEntity.badRequest().body(ErrorCode.SIGN_UP_FAIL.getMessage());
    }

    @ExceptionHandler(AddressException.class)
    protected ResponseEntity<String> addressCatcher() {
        System.out.println("GlobalExceptionHandler: 배송지가 없습니다.");
        return ResponseEntity.badRequest().body(ErrorCode.ORDER_ERROR.getMessage());
    }
}
