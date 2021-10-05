package com.kh.ollehapp.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.kh.ollehapp.domain.common.paging.FindCriteria;
import com.kh.ollehapp.review.dto.CommentDTO;
import com.kh.ollehapp.review.dto.ReviewDTO;
import com.kh.ollehapp.review.dto.ReviewViewDTO;
import com.kh.ollehapp.review.svc.ReviewSVC;
import com.kh.ollehapp.village.dto.BookmarkDTO;
import com.kh.ollehapp.village.dto.VillageDTO;
import com.kh.ollehapp.village.svc.VillageSVC;
import com.kh.ollehapp.web.api.JsonResult;
import com.kh.ollehapp.web.form.LoginMember;
import com.kh.ollehapp.web.form.bookmarkForm;
import com.kh.ollehapp.web.form.villageForm;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@AllArgsConstructor
@RequestMapping("/village")
public class villageController {

	private final VillageSVC villageSVC;
	private final ReviewSVC reviewSVC;
	@Autowired
	@Qualifier("fc10")
	private FindCriteria fc;

	
	/**
	 * 마을상세목록
	 * @param id
	 * @param model
	 * @return
	 */
	@GetMapping({"/{id}","/{id}/{reqPage}"})
	public String village(@PathVariable long id,@PathVariable(required = false) Integer reqPage,Model model) {
		
		List<ReviewDTO> list = null;
		
		VillageDTO villageDTO = villageSVC.villageData(id);
		int score = villageSVC.villageScore(id);
		
		
	//요청페이지가 없으면 1페이지로
			if(reqPage == null) reqPage = 1;
			//사용자가 요청한 페이지번호
			fc.getRc().setReqPage(reqPage);	
		
			fc.setTotalRec(villageSVC.totoalRecordCount(id));
			
			list = villageSVC.list(
					fc.getRc().getStartRec(),
					fc.getRc().getEndRec(),id);	
		
		
	
		
		model.addAttribute("village",villageDTO);
		model.addAttribute("reviewList",list);
		model.addAttribute("villageScore",score);
		model.addAttribute("fc",fc);
		
		log.info("list:{}",list);
		return "village/villageDetail";
	}
	
	/**
	 * 관심리스트 등록
	 * @throws IOException 
	 */
	@GetMapping("/{id}/bookmark")
	public String attention(@PathVariable long id,HttpServletRequest request,HttpServletResponse response) throws IOException {
		
		HttpSession session = request.getSession(false);
		LoginMember loginMember 
		= (LoginMember)session.getAttribute("loginMember");
		
		if(loginMember.getMemberId()==null) {
			response.setContentType("text/html; charset=UTF-8");
			 
			PrintWriter out = response.getWriter();
			 
			out.println("<script>alert('로그인이 필요합니다.'); location.href='/login/loginMember';</script>");
			 
			out.flush();

			return "member/inquiry";
		}
		
		
		
		log.info("id:{}",id);
		villageSVC.bookmark(loginMember.getMemberId(),id);
		
return "redirect:/village/{id}";
}
	
/**
 * 리뷰상세
 */
@GetMapping("/{reviewNum}/reviewDetail")
public String reviewDetail(		@PathVariable long reviewNum,
		HttpServletRequest request,
		Model model) {
	HttpSession session = request.getSession(false);
	LoginMember loginMember 
		= (LoginMember)session.getAttribute("loginMember");
	
	List<CommentDTO> list = villageSVC.commentList(reviewNum);
	
	ReviewViewDTO reviewViewDTO = reviewSVC.findReviewView2(reviewNum);
	
	model.addAttribute("reviewDTO",reviewViewDTO);

	
	model.addAttribute("reList",list);
	model.addAttribute("memberId",loginMember.getMemberId());

	return "village/ReviewDetail";
}
	

	/**
	 * 검색페이지 양식
	 */
	@PostMapping("/searchVill")
	public String searchVill(@RequestParam("searchContent") String searchContent,Model model) {
		log.info("searchContent:{}",searchContent);
		
		List<VillageDTO> list = villageSVC.searchVill(searchContent,searchContent);
		

		
		model.addAttribute("sList",list);
		model.addAttribute("searchContent",searchContent);
		
		return "village/searchVill";
	}
	
	/**
	 * 랭킹페이지
	 */
	@GetMapping("/ranking")
	public String ranking(Model model) {
		
		List<villageForm> list = villageSVC.ranking();
		
		log.info("list:{}",list);

		model.addAttribute("sList",list);
		
		return "village/Ranking";
	}
	
	
	
	
	
}
