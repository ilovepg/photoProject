package util;

import java.security.PrivateKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*데이터를 검증해야 할 때 사용하는 클래스*/
public class ValidateClass {
	UtilClass util ; // 필요한 도구들이 들어있는 Util 클래스 생성
	
	public ValidateClass() {
		util = new UtilClass();
	}
	
	/*회원가입할 때 ID, PW 규칙 검증*/
	/*
	 * 1. ID길이 유효성 검사
	 * 2. PW, PWCK 복호화
	 * 3-1. PW와 PWCK 같은지 검사
	 * 3-2. PW길이 유효성 검사
	 * 4. PW규칙 유효성 검사
	 * */
	public String registerValidate(String userID, String userPW, String userPWCK, HttpServletRequest request) throws Exception {
		// 1. ID길이 유효성 검사
		if(userID.length()<2) {
			return "아이디길이";
		}

		//2. PW, PWCK 복호화
		/*
		 * 1. 세션에서 개인키를 가져온다.
		 * 2. 복호화 진행
		 * 3. 개인키 삭제
		 * */
		HttpSession session = request.getSession();
		PrivateKey privateKey = (PrivateKey)session.getAttribute(util.getRSA_WEB_KEY());
		userPW = util.decryptRsa(privateKey, userPW);
		userPWCK = util.decryptRsa(privateKey, userPWCK);
		
		// 3-1. PW와 PWCK같은지 검사
		if (!userPW.equals(userPWCK)) {
			return "비밀번호체크";
		}

		// 3-2.비밀번호 길이 검사
		if(userPW.length()<10) {
			return "비밀번호길이";
		}
		
		// 4. 비밀번호 유효성 검사식 : 숫자, 특수문자가 포함되어야 한다.
		String regExp_userPW = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
		// 정규표현식 컴파일
		Pattern pattern_userPW = Pattern.compile(regExp_userPW);
		Matcher matcher_userPW = pattern_userPW.matcher(userPW);
		if(!matcher_userPW.find()) {
			return "비밀번호규칙";
		} 
		return userPW;
	}
	
	/*로그인 할 때 PW 복호화*/
	/*복호화 메소드를 proteced로 해서 여기를 거쳐서 해야할듯..*/
	public String decryptRsa(String userPW,HttpServletRequest request) {
		try {
			HttpSession session=request.getSession();
			PrivateKey privateKey = (PrivateKey)session.getAttribute(util.getRSA_WEB_KEY());
			return util.decryptRsa(privateKey, userPW);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/*
	 * 세션에 값이 있는지 검증
	 * @Param request : 요청 객체
	 * @Param request : 세션 Attribute name값
	 * */
	public Object isNullSessionValue(HttpServletRequest request, String param) {
		HttpSession session=request.getSession();
		Object tempObj=session.getAttribute(param);
		if(tempObj==null) {
			return null;
		}else {
			return tempObj;
		}
	}
	
	/*
	 * 세션에 있는 로그인 아이디와 파라미터값 비교하여 같은지 검증 
	 * @Param request : 요청 객체
	 * @Param compareID : 비교할 아이디
	 * */
	public Boolean loginUserCompare(HttpServletRequest request, String compareID ) {
		HttpSession session = request.getSession();
		Object tempObj=session.getAttribute("userID");
		if(tempObj!=null) {
			String userID=tempObj.toString();
			if(userID.equals(compareID)) { //로그인된 아이디와 비교할 아이디가 같다면
				return true;
			}else {
				return false;
			}
		}
		return false;
		
	}
	
}
