package com.devkurly.member.controller;

import com.devkurly.address.domain.AddressDto;
import com.devkurly.cart.domain.Cart;
import com.devkurly.cart.dto.CartSaveRequestDto;
import com.devkurly.cart.service.CartService;
import com.devkurly.member.dto.*;
import com.devkurly.member.exception.SignInException;
import com.devkurly.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final CartService cartService;

    @GetMapping("")
    public String viewSignIn(HttpSession session) {
        Object sessionAttribute = session.getAttribute("memberResponse");
        if (sessionAttribute == null) {
            return "/member/signIn";
        }
        return "redirect:/";
    }

    @PostMapping("")
    public String postSignIn(@CookieValue(value = "tempCart", required = false) Cookie tempCart, CartSaveRequestDto cartSaveRequestDto, MemberSignInRequestDto requestDto, boolean rememberId, String toURL, HttpServletRequest request, HttpServletResponse response, HttpSession session) {
        MemberMainResponseDto memberResponse = memberService.signIn(requestDto);

        Cookie idCookie = new Cookie("email", requestDto.getUser_email());
        if (!rememberId) {
            idCookie.setMaxAge(0);
        }
        response.addCookie(idCookie);

        HttpSession requestSession = request.getSession();
        requestSession.setAttribute("memberResponse", memberResponse);

        cookieToLoginCart(tempCart, cartSaveRequestDto, response, session);
        if ((toURL == null || toURL.equals(""))) {
            toURL = "/";
        } else if (toURL.contains("coupn_nm")) {
            String[] split = toURL.split("=");
            String s = split[1];
            String encode = URLEncoder.encode(s, StandardCharsets.UTF_8);
            return "redirect:" + split[0] + "=" + encode;
        }
        return "redirect:" + toURL;
    }

    @GetMapping("/signup")
    public String viewSignUp() {
        return "/member/signUp";
    }

    @PostMapping("/signup")
    public String postSignUp(@ModelAttribute @Valid MemberSaveRequestDto requestDto, AddressDto addressDto, BindingResult result) {
        if (result.hasErrors()) {
            return "redirect:/members/signup?error=3";
        }
        memberService.join(requestDto);
        MemberMainResponseDto memberResponse = memberService.findUserId(new MemberSignInRequestDto(requestDto.getUser_email(), requestDto.getPwd()));
        addressDto.setUser_id(memberResponse.getUser_id());
        addressDto.setAddr_name(requestDto.getUser_nm());
        addressDto.setAddr_tel(requestDto.getTelno());
        memberService.saveDefaultAddress(addressDto);
        return "redirect:/members";
    }

    @GetMapping("/signout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    @GetMapping("info/verify")
    public String viewVerifyMemberModify() {
        return "/member/verify";
    }

    @PostMapping("/info/verify")
    public String verifyMemberModify(String pwd, HttpSession session) {
        Integer user_id = getMemberResponse(session);
        memberService.updatePassword(user_id, pwd);
        return "redirect:/members/info";
    }

    @GetMapping("/info")
    public String viewModifyMember(Model model, HttpSession session) {
        Integer user_id = getMemberResponse(session);
        MemberUpdateResponseDto updateResponse = memberService.findUpdateMember(user_id);
        model.addAttribute("updateResponse", updateResponse);
        return "/member/update";
    }

    @PostMapping("/info")
    public String modifyMember(MemberUpdateRequestDto updateRequest, Model model, HttpSession session) {
        String prvPwd = updateRequest.getPwd();
        MemberMainResponseDto memberResponse = memberService.signIn(new MemberSignInRequestDto(updateRequest.getUser_email(), prvPwd));
        MemberUpdateResponseDto updateResponse = memberService.modifyMember(updateRequest);
        session.setAttribute("memberResponse", memberResponse);
        model.addAttribute("updateResponse", updateResponse);
        return "redirect:/members/info";
    }

    private void cookieToLoginCart(Cookie tempCart, CartSaveRequestDto cartSaveRequestDto, HttpServletResponse response, HttpSession session) {
        List<Cart> carts = cartService.viewAllCart(cartService.getCookieId(tempCart));
        for (Cart cart : carts) {
            cart.setUser_id(getMemberResponse(session));
            cartSaveRequestDto.saveCart(cart.getUser_id(), cart.getPdt_id(), cart.getPdt_qty());
            cartService.addCart(cartSaveRequestDto);
            cartService.removeCart(cartService.getCookieId(tempCart));
        }
        if (Optional.ofNullable(tempCart).isPresent()) {
            tempCart.setPath("/");
            tempCart.setMaxAge(0);
            response.addCookie(tempCart);
        }
    }

    @GetMapping("/kakao")
    public String sns(@RequestParam(value = "code", required = false) String code, MemberKakaoResponseDto responseDto, HttpSession session, Model model) {
        String access_Token = memberService.getAccessToken(code);
        HashMap<String, Object> userInfo = memberService.getUserInfo(access_Token);
        String email = (String) userInfo.get("email");
        try {
            MemberMainResponseDto memberResponse = memberService.kakaoSignIn(email);
            session.setAttribute("memberResponse", memberResponse);
            return "redirect:/";
        } catch (SignInException e) {
            String name = (String) userInfo.get("nickname");
            String gender = (String) userInfo.get("gender");
            responseDto.setUser_email(email);
            responseDto.setUser_nm(name);
            responseDto.setGender(gender);
            model.addAttribute("kakao", responseDto);
            return "/member/signUp";
        }
    }

    public static Integer getMemberResponse(HttpSession session) {
        return ((MemberMainResponseDto) session.getAttribute("memberResponse")).getUser_id();
    }
}
