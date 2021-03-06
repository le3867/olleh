package com.kh.ollehapp.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.kh.ollehapp.domain.common.dao.CodeDAO;
import com.kh.ollehapp.domain.common.paging.FindCriteria;
import com.kh.ollehapp.member.dto.MemberDTO;
import com.kh.ollehapp.member.dto.InquiryComDTO;
import com.kh.ollehapp.member.dto.InquiryDTO;
import com.kh.ollehapp.member.svc.MemberSVC;
import com.kh.ollehapp.web.api.JsonResult;
import com.kh.ollehapp.web.form.Code;
import com.kh.ollehapp.web.form.LoginForm;
import com.kh.ollehapp.web.form.LoginMember;
import com.kh.ollehapp.web.form.LongLatForm;
import com.kh.ollehapp.web.form.ModifyForm;
import com.kh.ollehapp.web.form.bookmarkForm;
import com.kh.ollehapp.web.form.reviewForm;
import com.kh.ollehapp.web.form.member.JoinForm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/member")
public class MemberController {

	private final MemberSVC memberSVC;
	
	@Autowired
	@Qualifier("fc10")
	private FindCriteria fc;


	//????????? ???????????? ?????? ??????
	@GetMapping("/findForm")
	public String findForm() {
		
		return "/member/findForm";
	}
	

	//???????????? ??????
	@GetMapping("/joinForm")
	public String joinForm(Model model) {
		log.info("String joinForm() ?????????");
		model.addAttribute("joinForm", new JoinForm());
		
		return "member/joinForm"; //templates ???????????? ???????????? ????????????
	}
	

	
	
	/**
	 * ????????????
	 * @param memberDTO
	 * @return
	 */
	@PostMapping("/join")
	public String join(
			@Valid @ModelAttribute JoinForm joinForm,
			BindingResult bindingResult) {
		
		log.info("?????????????????? ?????????!");
		log.info("joinForm:{}",joinForm);	
		
		//????????? ????????????
		if(memberSVC.isExistMemberId(joinForm.getMemberId())) {
			bindingResult.reject("error.member.join","????????? ???????????? ???????????????.");
			return "member/joinForm";
		}
		
		//???????????? ??????						
		if(!joinForm.getMemberPw().equals(joinForm.getMemberPwChk())) {
			bindingResult.reject("error.member.join","??????????????? ????????????");
			log.info("??????");
			return "member/joinForm";
		}
				
		if(bindingResult.hasErrors()) {
			log.info("errors={}",bindingResult);
			return "member/joinForm";
		}
		
		//????????? ????????????
		if(memberSVC.isExistTel(joinForm.getTel())) {
			bindingResult.reject("error.member.join","????????? ??????????????????.");
			return "member/joinForm";
		}
		
		MemberDTO mdto = new MemberDTO();
		BeanUtils.copyProperties(joinForm, mdto);
		memberSVC.joinMember(mdto);		
		return "redirect:/";
	}
	/**
	 * ?????????/???????????? ??????
	 * @throws IOException 
	 */
	@PostMapping("/findIdPw")
	
	public String findIdPw(@ModelAttribute MemberDTO memberDTO,HttpServletRequest request,HttpServletResponse response,Model model) throws IOException {
		
		
		
	MemberDTO memberDTO2 = memberSVC.findIdPw(memberDTO.getMemberName(), memberDTO.getTel());
	

	if(memberDTO2 == null) {
		
		response.setContentType("text/html; charset=UTF-8");
		 
		PrintWriter out = response.getWriter();
		 
		out.println("<script>alert('???????????? ????????? ???????????? ID/PW??? ????????????.'); location.href='/member/findForm';</script>");
		 
		out.flush();



		return "/member/findForm";
	}
	
		model.addAttribute("MemberDTO",memberDTO2);
	 
		
		
				return "/member/findIdPw";
		
	
	}

/**
 * ?????????????????? ????????? ??????	
 */
@GetMapping("/modify")
public String modifyForm1() {
	return "mypage/modify";
}

/**
 * ?????????????????? ?????? ??????
 */

@PostMapping("/modifyMember")
public String modifyMember(
@ModelAttribute LoginForm loginForm,
Model model, HttpServletRequest request,HttpServletResponse response) throws IOException {

	HttpSession session = request.getSession(false);
	LoginMember loginMember
	=(LoginMember)session.getAttribute("loginMember");	
	
	MemberDTO memberDTO3 = memberSVC.login(loginForm.getMemberId(), loginForm.getMemberPw());
	
	//??????
	if(memberDTO3 == null) {

		response.setContentType("text/html; charset=UTF-8");
		 
		PrintWriter out = response.getWriter();
		 
		out.println("<script>alert('???????????? ????????? ???????????? ID/PW??? ????????????.'); location.href='javascript:history.back()';</script>");
		 
		out.flush();

		return "login/loginForm";
	}
	
	
MemberDTO memberDTO = memberSVC.modifyLogin(loginForm.getMemberId(),loginForm.getMemberPw());


MemberDTO memberDTO2 = memberSVC.findByID(loginForm.getMemberId());
ModifyForm modifyForm = new ModifyForm();
BeanUtils.copyProperties(memberDTO2, modifyForm);
model.addAttribute("modifyForm",modifyForm);


log.info("memberDTO:{}",modifyForm);
//??????
if(memberDTO == null ) {
log.info(loginMember.getMemberId());
log.info(loginForm.getMemberId());

	
response.setContentType("text/html; charset=UTF-8");
 
PrintWriter out = response.getWriter();
 
out.println("<script>alert('???????????? ID/PW??? ???????????????.'); location.href='/member/modify';</script>");
 
out.flush();


}else if(!loginMember.getMemberId().equals(loginForm.getMemberId())) {
	response.setContentType("text/html; charset=UTF-8");
	 
	PrintWriter out = response.getWriter();
	 
	out.println("<script>alert('???????????? ID/PW??? ???????????????.'); location.href='/member/modify';</script>");
	 
	out.flush();
}

return "mypage/modifyMember";
}


/**
 * ???????????? ??????
 * @throws IOException 
 */
@PatchMapping("/modify")
public String edit(@Valid @ModelAttribute ModifyForm modifyForm,
									BindingResult bindingResult,HttpServletRequest request,HttpServletResponse response) throws IOException {
	log.info("?????????????????? ?????????");
	HttpSession session = request.getSession(false);
	LoginMember loginMember = (LoginMember)session.getAttribute("loginMember");
	
	//?????????????????????????????????????????????
	//if(loginMember == null) return "redirect:/login";
	
	log.info("modifyForm:{}",modifyForm);
	

	if(modifyForm.getMemberPw()=="") {
		response.setContentType("text/html; charset=UTF-8");
		 
		PrintWriter out = response.getWriter();
		 
		out.println("<script>alert('????????? ???????????????.'); location.href='javascript:history.back()';</script>");
		 
		out.flush();

		return "mypage/modifyMember";
	}else if(modifyForm.getEmail()==""){
		response.setContentType("text/html; charset=UTF-8");
		 
		PrintWriter out = response.getWriter();
		 
		out.println("<script>alert('????????? ???????????????.'); location.href='javascript:history.back()';</script>");
		 
		out.flush();

		return "mypage/modifyMember";
	}else if(modifyForm.getTel()=="") {
		response.setContentType("text/html; charset=UTF-8");
		 
		PrintWriter out = response.getWriter();
		 
		out.println("<script>alert('????????? ???????????????.'); location.href='javascript:history.back()';</script>");
		 
		out.flush();

		return "mypage/modifyMember";
	}
	
	
	MemberDTO mdto = new MemberDTO();
	BeanUtils.copyProperties(modifyForm, mdto);

	
	memberSVC.update(loginMember.getMemberId(),mdto);
	
	log.info("??????????????????:{}",mdto);
	
	response.setContentType("text/html; charset=UTF-8");
	 
	PrintWriter out = response.getWriter();
	 
	out.println("<script>alert('????????? ?????????????????????.'); location.href='javascript:history.back()';</script>");
	 
	out.flush();

	return "mypage/modifyMember";
	
}

/**
 * ???????????? ??????
 * @return
 */
@GetMapping("/delete")
public String deleteForm() {
	return "mypage/delete";
}


/**
 * ???????????? ?????????
 */

@PostMapping("/deleteMember")
public String deleteMember(@ModelAttribute LoginForm loginForm,
		 HttpServletRequest request,HttpServletResponse response) throws IOException {
MemberDTO memberDTO = memberSVC.modifyLogin(loginForm.getMemberId(),loginForm.getMemberPw());

if(memberDTO == null) {

response.setContentType("text/html; charset=UTF-8");
 
PrintWriter out = response.getWriter();
 
out.println("<script>alert('???????????? ID/PW??? ???????????????.'); location.href='/member/delete';</script>");
 
out.flush();

return "mypage/delete";
}

return "mypage/deleteMember";
}

//????????????
@GetMapping("/deleteing")
public String deleteing(HttpServletRequest request) {
	HttpSession session = request.getSession(false);
	//?????????????????????????????????????????????
	if(session == null) return "redirect:/login";
	LoginMember loginMember = (LoginMember)session.getAttribute("loginMember");
	if(loginMember == null) return "redirect:/login";

	

	
	memberSVC.delete(loginMember.getMemberId());
	
	if(session != null) {
		session.invalidate();
	}
	
	return "redirect:/";
}


/**
 * ??????????????? ??????
 * @return
 */
@GetMapping("/reservation")
public String reservation() {
	return "mypage/reservation";
}



/**
 * ??????????????? ??????
 */

@GetMapping("/review")
public String review() {
	return "mypage/review";
}


/**
 * 1:1??????????????? ??????
 */

@GetMapping("/inquiry")
public String inquiry(HttpServletRequest request,
		Model model) {
	HttpSession session = request.getSession(false);
	LoginMember loginMember
	=(LoginMember)session.getAttribute("loginMember");
	
	List<InquiryDTO> list =
	memberSVC.findInquiry(loginMember.getMemberId());
	
	List<InquiryComDTO> list2 = memberSVC.findComment(loginMember.getMemberId());
			

	
	model.addAttribute("iqList",list);
	model.addAttribute("cmList",list2);
	
	return "mypage/inquiry";
}

/**
 * 1:1?????? ???????????????
 */
@GetMapping("/{inquiryCommentNum}/detailComment")
public String detailComment(@PathVariable long inquiryCommentNum,Model model){
	
	log.info("inquiryCommentNum:{}",inquiryCommentNum);
	
	InquiryComDTO inquiryComDTO =  memberSVC.detailComment(inquiryCommentNum);
	
	model.addAttribute("comment",inquiryComDTO);
	
	return "mypage/inquiryComment";
}


/**
 * 1:1?????? ??????
 * @throws IOException 
 */
@PostMapping("/editInquiry")
public String editInquiry(@ModelAttribute InquiryDTO inquiryDTO,HttpServletRequest request,HttpServletResponse response) throws IOException {
	HttpSession session = request.getSession(false);
	LoginMember loginMember
	=(LoginMember)session.getAttribute("loginMember");
	
	log.info("inquiryDTO2:{}",inquiryDTO);
	
	if(inquiryDTO.getInquiryTitle() == null || inquiryDTO.getInquiryContent() == null || inquiryDTO.getInquiryType() == 0) {

		response.setContentType("text/html; charset=UTF-8");
		 
		PrintWriter out = response.getWriter();
		 
		out.println("<script>alert('???????????? ????????????..'); location.href='/member/inquiry';</script>");
		 
		out.flush();

		return "member/inquiry";
		}
	
	memberSVC.editInquiry(loginMember.getMemberId(), inquiryDTO);
	
	response.setContentType("text/html; charset=UTF-8");
	
	PrintWriter out = response.getWriter();
	 
	out.println("<script>alert('?????????????????????.'); location.href='/member/inquiry';</script>");
	 
	out.flush();
	
	return "redirect:/";
}


/**
 * 1:1?????? ?????? ??????
 */
@GetMapping("/{inquiryNum}/modifyInquiry")
@ResponseBody
public JsonResult<List<InquiryDTO>> modifyInquiry(@PathVariable String inquiryNum,Model model) {
	

	
 List<InquiryDTO> list =	memberSVC.inquiryData(inquiryNum);
	
	model.addAttribute("quiryDatalist",list);
	
	return new JsonResult<List<InquiryDTO>>("00","ok",list);
}

/**
 * 1:1?????? ?????? ??????
 * @throws IOException 
 */
@PostMapping("/modifyInquiry")
public String modifyInquiry2(@ModelAttribute InquiryDTO inquiryDTO2 )  {
	log.info("inquiryDTO:{}",inquiryDTO2);
	
	memberSVC.modifyInquiry(inquiryDTO2);
	
	return "redirect:/member/inquiry";
}


/**
 * 1:1?????? ?????? ??????
 */
@GetMapping("/{inquiryNum}/deleteInquiry")
public String deleteInquiry(@PathVariable String inquiryNum) {
	
	memberSVC.deleteInquiry(inquiryNum);
	
	return "redirect:/member/inquiry";
}

/**
 * ??????????????? ??????
 */
@GetMapping({"/bookmark",
						"/bookmark/{reqPage}"})
public String bookmarkList(HttpServletRequest request,@PathVariable(required = false) Integer reqPage,Model model) {
	HttpSession session = request.getSession(false);
	LoginMember loginMember
	=(LoginMember)session.getAttribute("loginMember");
	
	List<bookmarkForm> list = null;
	
//?????????????????? ????????? 1????????????
		if(reqPage == null) reqPage = 1;
		//???????????? ????????? ???????????????
		fc.getRc().setReqPage(reqPage);	
	
		fc.setTotalRec(memberSVC.totoalRecordCount(loginMember.getMemberId()));
		
		list = memberSVC.list(
				fc.getRc().getStartRec(),
				fc.getRc().getEndRec(),loginMember.getMemberId());	
		
	model.addAttribute("blist",list);
	model.addAttribute("fc",fc);
	
	log.info("blist:{}",list);
	log.info("fc:{}",fc);
	
	return "mypage/bookmark";
}
	
/**
 * ??????????????? ??????
 */
@GetMapping("/{bookmarkNum}/deleteBookmark")
public String deleteBookmark(@PathVariable long bookmarkNum) {
	
	memberSVC.deleteBookmark(bookmarkNum);
	
	return "redirect:/member/bookmark";
	
}
}















