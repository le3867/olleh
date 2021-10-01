package com.kh.ollehapp.web.form;

import lombok.Data;

@Data
public class villageForm {
	private int ROWNUM;
	private long id; 									//마을고유번호
	private String exprnVillageNm;		//마을이름
	private String exprnCn;           //체험프로그램명
	private String rdnmadr;           //도로명주소
	private String villageLatitude;   //위도
	private String villageLongitude;  //경도
	private String score;
}
