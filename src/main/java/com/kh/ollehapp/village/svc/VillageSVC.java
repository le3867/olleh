package com.kh.ollehapp.village.svc;

import java.util.List;

import com.kh.ollehapp.review.dto.CommentDTO;
import com.kh.ollehapp.review.dto.ReviewDTO;
import com.kh.ollehapp.village.dto.BookmarkDTO;
import com.kh.ollehapp.village.dto.VillageDTO;
import com.kh.ollehapp.web.form.bookmarkForm;
import com.kh.ollehapp.web.form.villageForm;

public interface VillageSVC {

//마을정보
VillageDTO villageData(long id);

//별점
int villageScore(long id);

//리뷰조회
List<ReviewDTO> searchReview(long id);

//관심리스트 등록
void bookmark(String memberId,long id);

//댓글등록
String comment(long reviewNum,String commentContent,String memberId);

//댓글목록
List<CommentDTO> commentList(long reviewNum);

//댓글삭제
void deleteComment(long commentNum);

//검색
List<VillageDTO> searchVill(String searchContent,String searchContent2);

/**
 * 관심리스트 전체 요청페이지 목록
 * @param startRec
 * @param endRec
 * @return
 */	
List<ReviewDTO> list(int startRec, int endRec,long id);

/**
 * 게시판 전체 레코드 수 
 * @return
 */
long totoalRecordCount(long id);


/**
 * 랭킹페이지
 */
List<villageForm> ranking();  
}

