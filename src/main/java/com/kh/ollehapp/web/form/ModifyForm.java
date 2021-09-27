package com.kh.ollehapp.web.form;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class ModifyForm {
 private String memberName;
 private String birth;
 private String memberId;
 @NotBlank
 private String memberPw;
 @NotBlank
 private String tel;
 @NotBlank
 private String email;

}
