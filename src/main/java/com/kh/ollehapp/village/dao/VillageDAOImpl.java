package com.kh.ollehapp.village.dao;

import java.util.List;

import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.kh.ollehapp.review.dto.CommentDTO;
import com.kh.ollehapp.review.dto.ReviewDTO;
import com.kh.ollehapp.village.dto.BookmarkDTO;
import com.kh.ollehapp.village.dto.VillageDTO;
import com.kh.ollehapp.web.form.bookmarkForm;
import com.kh.ollehapp.web.form.villageForm;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VillageDAOImpl implements VillageDAO {

	private final JdbcTemplate jt;
	
	/**
	 * 마을정보
	 */
	@Override
	public VillageDTO villageData(long id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select id, ");
		sql.append(" exprnVillageNm, ");
		sql.append(" homepageUrl, ");
		sql.append(" rprsntvName, ");
		sql.append(" phoneNumber, ");
		sql.append(" ctprvnNm, ");
		sql.append(" signguNm, ");
		sql.append(" exprnSe, ");
		sql.append(" exprnCn, ");
		sql.append(" rdnmadr, ");
		sql.append(" villageLatitude, ");
		sql.append(" villageLongitude ");
		sql.append(" from village  ");
		sql.append(" where id=? ");
		
		VillageDTO villageDTO = jt.queryForObject(sql.toString(), new BeanPropertyRowMapper<>(VillageDTO.class),id);
		return villageDTO;
	}
	
	/**
	 * 별점
	 */
	@Override
	public int villageScore(long id) {
	 String sql= "	 select AVG(score) from review where id = ? ";
	 
	  if(jt.queryForObject(sql, Integer.class,id) == null) {
	  	return 0;
	  }
	 
	  int result = jt.queryForObject(sql, Integer.class,id);
	  return result;
	}
	
	
	/**
	 * 리뷰조회
	 */
	@Override
	public List<ReviewDTO> searchReview(long id) {
		StringBuffer sql = new StringBuffer();
		sql.append(" select reviewNum, ");
		sql.append(" reviewtitle, ");
		sql.append(" memberId, ");
		sql.append(" rcdate ");
		sql.append(" from review, ");
		sql.append(" village ");
		sql.append(" where review.id=village.id ");
		sql.append(" and review.id=? ");
		
		List<ReviewDTO> list = jt.query(sql.toString(), new BeanPropertyRowMapper<>(ReviewDTO.class),id);
		
		return list;
	}
	
	/**
	 * 관심리스트 등록
	 */
@Override
	public void bookmark(String memberId,long id) {
		StringBuffer sql = new StringBuffer();
	  sql.append("	insert into bookmark "); 
	  sql.append("	values(bookmark_bookmarkNum_seq.nextval,?,?) ");
	  
	  jt.update(sql.toString(),memberId,id);
		
	}

/**
 * 댓글등록
 */
@Override
public String comment(long reviewNum, String commentContent, String memberId) {
	StringBuffer sql = new StringBuffer();
	sql.append(" insert into  ");
	sql.append(" reviewComment( ");
	sql.append(" commentNum, ");
	sql.append(" reviewNum, ");
	sql.append(" memberId, ");
	sql.append(" commentContent)  ");
	sql.append(" values( ");
	sql.append(" reviewComment_reviewCommentNum_seq.nextval, ");
	sql.append(" ?, ");
	sql.append(" ?, ");
	sql.append(" ?) ");
	
	String result = String.valueOf(jt.update(sql.toString(),reviewNum,memberId,commentContent));
	return result;
}
/**
 * 댓글목록
 */
@Override
public List<CommentDTO> commentList(long reviewNum) {
	StringBuffer sql = new StringBuffer();
	sql.append(" select  ");
	sql.append(" commentNum, ");
	sql.append(" reviewNum, ");
	sql.append(" memberId, ");
	sql.append(" commentcontent  ");
	sql.append(" from reviewComment  ");
	sql.append(" where reviewnum=? ");
	
	List<CommentDTO> list = jt.query(sql.toString(),new BeanPropertyRowMapper<>(CommentDTO.class),reviewNum);
	return list;
}

/**
 * 댓글삭제
 */
@Override
public void deleteComment(long commentNum) {
	
	String sql = " delete reviewcomment where commentNum = ? ";
	
	jt.update(sql,commentNum);
	
}

/**
 * 검색
 */
@Override
public List<VillageDTO> searchVill(String searchContent,String searchContent2) {
	StringBuffer sql = new StringBuffer();
	sql.append(" select id,exprnVillageNm,HOMEPAGEURL,phoneNumber,ctprvnnm,signgunm,exprnse,exprncn,rdnmadr,villageLatitude,villagelongitude from village  ");
	sql.append(" where exprnVillageNm like '%'||?||'%' ");
	sql.append(" or exprnCn like '%'||?||'%' ");
	
	List<VillageDTO> list = jt.query(sql.toString(), new BeanPropertyRowMapper<>(VillageDTO.class),searchContent,searchContent2);
	return list;
}


/**
 * 리뷰목록
 */
@Override
public List<ReviewDTO> list(int startRec, int endRec, long id) {
	StringBuffer sql = new StringBuffer();
	sql.append(" select t1.* ");
	sql.append(" from (select row_number() ");
	sql.append(" over(order by reviewNum desc) as num, ");
	sql.append(" reviewNum,reviewtitle, memberId, rcdate,");
	sql.append("  rdnmadr,review.id from review,village ");
	sql.append("  where village.id = review.id and village.id=? ) ");
	sql.append(" t1 where num between ? and ? and id=? ");
	
	List<ReviewDTO> list = jt.query(
			sql.toString(), 
			new BeanPropertyRowMapper<>(ReviewDTO.class),
			id,startRec,endRec,id
			);	
return list;
}

/**
 * 리뷰 전체 레코드수
 */
@Override
public long totoalRecordCount(long id) {
	
	StringBuffer sql = new StringBuffer();
	sql.append("select count(*) ");
	sql.append("	from review ");
	sql.append("	where Id = ? ");
	
	long totalCount = jt.queryForObject(sql.toString(), Long.class , id);
	return totalCount;
}

/**
 * 랭킹
 */
@Override
public List<villageForm> ranking() {
	StringBuffer sql = new StringBuffer();
	sql.append(" select Rownum,x.* from(  ");
	sql.append(" select exprnvillageNM,exprncn,rdnmadr,village.id,villageLatitude,villageLongitude,avg(score)\"score\" ");
	sql.append(" from review,village where review.id = village.id ");
	sql.append(" group by exprnvillageNM,exprncn,rdnmadr,village.id,villageLatitude,villageLongitude order by avg(score) desc)x ");
		
List<villageForm> list = jt.query(sql.toString(), new BeanPropertyRowMapper<>(villageForm.class));
	return list;
}
}














