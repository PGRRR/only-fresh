package com.devkurly.address.controller;

import com.devkurly.address.domain.AddressDto;
import com.devkurly.address.service.AddressService;
import com.devkurly.member.dto.MemberMainResponseDto;
import org.apache.ibatis.javassist.runtime.Desc;
import org.junit.experimental.theories.DataPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.devkurly.member.controller.MemberController.getMemberResponse;

@Controller
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;

    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @GetMapping("/list")
    public String list(HttpSession session, Model m, AddressDto addressDto) throws Exception {
        Integer user_id = getMemberResponse(session);
        addressDto.setUser_id(user_id);

        try {

            List<AddressDto> list = addressService.getListSelect(user_id);
            m.addAttribute("list", list);
            m.addAttribute("addressDto", addressDto);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/address/mypageAddrList";
    }

    @PostMapping("/default")
    public String modifyDefault(AddressDto addressDto, Model m, HttpSession session) throws Exception {
        MemberMainResponseDto responseDto = (MemberMainResponseDto) session.getAttribute("memberResponse");
        Integer user_id = responseDto.getUser_id();

        if(addressDto.getBa_addr()==null)
            addressDto.setBa_addr(false);
        
        addressService.modifybaaadr(addressDto);

        m.addAttribute("addressDto", addressDto);

        return "redirect:/address/list";
    }

    @GetMapping("/read")
    public String addrModify(Integer addr_id, HttpSession session, Model m, AddressDto addressDto) throws Exception { //user_id, addressDto ????????????
        MemberMainResponseDto responseDto = (MemberMainResponseDto) session.getAttribute("memberResponse");
        Integer user_id = responseDto.getUser_id(); // user_id

        addressDto.setUser_id(user_id);
        addressDto.setAddr_id(addr_id);

        try {
            addressDto = addressService.read(addr_id);
            m.addAttribute("addressDto", addressDto); // model??? ????????? view??? ??????

        } catch (Exception e) {
            e.printStackTrace();
        }
        return "/address/mypageAddrModify";
    }

    @PostMapping("/remove")
    public String remove(Integer addr_id, AddressDto addressDto, HttpSession session, Model m, RedirectAttributes rattr) { // addressDto, user_id ????????????
        MemberMainResponseDto responseDto = (MemberMainResponseDto) session.getAttribute("memberResponse");
        Integer user_id = responseDto.getUser_id(); // user_id ?????? ??????

        addressDto.setUser_id(user_id); // ???????????? ????????? user_id??? Dto??? ????????????.
        addressDto.setAddr_id(addr_id);

        try {
            int rowCnt = addressService.remove(addressDto);

            if (rowCnt != 1) // ????????? ????????????
                throw new Exception("address remove error"); // ????????? ??????

                rattr.addFlashAttribute("msg", "DEL_OK"); // ?????? ??????

        } catch (Exception e) {
            e.printStackTrace();
            rattr.addFlashAttribute("msg", "DEL_ERR"); // ?????? ??????
        }

        return "redirect:/address/list";
    }

    @PostMapping("/modify")
        public String modify(Integer addr_id, AddressDto addressDto, HttpSession session, Model m, RedirectAttributes rattr) throws Exception {
            MemberMainResponseDto responseDto = (MemberMainResponseDto) session.getAttribute("memberResponse");
            Integer user_id = responseDto.getUser_id();

            addressDto.setUser_id(user_id); // ???????????? ????????? user_id??? Dto??? ????????????.

        if(addressDto.getChk_addr()==null)
            addressDto.setChk_addr(false);

        try {
            int rowCnt = addressService.modify(addressDto);
            m.addAttribute("addressDto", addressDto);
            
            if (rowCnt != 1)
                throw new Exception("Modify failed");   // ????????? ??????

                rattr.addFlashAttribute("msg", "MOD_OK");

        } catch (Exception e) {
            e.printStackTrace();
            m.addAttribute("addressDto", addressDto);
            rattr.addFlashAttribute("msg", "MOD_ERR");

            return "/address/mypageAddrModify";
        }

        return "redirect:/address/list";
    }

    @GetMapping("/insert")
    public String insert(Model m, AddressDto addressDto, HttpSession session) {
        Integer user_id = getMemberResponse(session);
        addressDto.setUser_id(user_id);

        m.addAttribute("addressDto", addressDto);
        return "/address/mypageAddrAdd";
    }

    @PostMapping("/create")
    public String create(AddressDto addressDto, HttpSession session, Model m, RedirectAttributes rattr) throws Exception {
        Integer user_id = getMemberResponse(session);
        addressDto.setUser_id(user_id);

        if (addressDto.getMain_addr().contains("??????") || addressDto.getMain_addr().contains("??????") || addressDto.getMain_addr().contains("??????") ||
            addressDto.getMain_addr().contains("??????") ){
            addressDto.setDeli_type(true);
        } else {
            addressDto.setDeli_type(false);
        }

        if (addressDto.getChk_addr() == null) {
            addressDto.setChk_addr(false);
        }

        try {
            int rowCnt = addressService.addrInsert(addressDto);

            if (rowCnt != 1){
                throw new Exception("Insert failed");
            }
            rattr.addFlashAttribute("msg", "INS_OK");

            return "redirect:/address/list";
        } catch (Exception e) {
            e.printStackTrace();
            rattr.addFlashAttribute(addressDto);
            m.addAttribute("msg", "INS_ERR");
            return "/address/mypageAddrAdd";
        }

    }

    private String addr_tel_format(String addr_tel) {
        String regEx = "(\\d{2,3})(\\d{3,4})(\\d{4})";
        return addr_tel.replaceAll(regEx, "$1-$2-$3");
    }

        public static String phone(String src) {
            if(src == null) {
                return"";
            } if(src.length()==8) {
                return src.replaceFirst("^([0-9]{4})([0-9]{4})$","$1-$2");
            }else if(src.length()==12) {
                return src.replaceFirst("(^[0-9]{4})([0-9]{4})([0-9]{4})$","$1-$2-$3");
            }
            return src.replaceFirst("(^02|[0-9]{3})([0-9]{3,4})([0-9]{4})$","$1-$2-$3");
        }

}
