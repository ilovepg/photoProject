package util;

import java.security.PrivateKey;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/*�����͸� �����ؾ� �� �� ����ϴ� Ŭ����*/
public class ValidateClass {
	UtilClass util ; // �ʿ��� �������� ����ִ� Util Ŭ���� ����
	
	public ValidateClass() {
		util = new UtilClass();
	}
	
	/*ȸ�������� �� ID, PW ��Ģ ����*/
	/*
	 * 1. ID���� ��ȿ�� �˻�
	 * 2. PW, PWCK ��ȣȭ
	 * 3-1. PW�� PWCK ������ �˻�
	 * 3-2. PW���� ��ȿ�� �˻�
	 * 4. PW��Ģ ��ȿ�� �˻�
	 * */
	public String registerValidate(String userID, String userPW, String userPWCK, HttpServletRequest request) throws Exception {
		// 1. ID���� ��ȿ�� �˻�
		if(userID.length()<2) {
			return "���̵����";
		}

		//2. PW, PWCK ��ȣȭ
		/*
		 * 1. ���ǿ��� ����Ű�� �����´�.
		 * 2. ��ȣȭ ����
		 * 3. ����Ű ����
		 * */
		HttpSession session = request.getSession();
		PrivateKey privateKey = (PrivateKey)session.getAttribute(util.getRSA_WEB_KEY());
		userPW = util.decryptRsa(privateKey, userPW);
		userPWCK = util.decryptRsa(privateKey, userPWCK);
		
		// 3-1. PW�� PWCK������ �˻�
		if (!userPW.equals(userPWCK)) {
			return "��й�ȣüũ";
		}

		// 3-2.��й�ȣ ���� �˻�
		if(userPW.length()<10) {
			return "��й�ȣ����";
		}
		
		// 4. ��й�ȣ ��ȿ�� �˻�� : ����, Ư�����ڰ� ���ԵǾ�� �Ѵ�.
		String regExp_userPW = "([0-9].*[!,@,#,^,&,*,(,)])|([!,@,#,^,&,*,(,)].*[0-9])";
		// ����ǥ���� ������
		Pattern pattern_userPW = Pattern.compile(regExp_userPW);
		Matcher matcher_userPW = pattern_userPW.matcher(userPW);
		if(!matcher_userPW.find()) {
			return "��й�ȣ��Ģ";
		} 
		return userPW;
	}
	
	/*�α��� �� �� PW ��ȣȭ*/
	/*��ȣȭ �޼ҵ带 proteced�� �ؼ� ���⸦ ���ļ� �ؾ��ҵ�..*/
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
	 * ���ǿ� ���� �ִ��� ����
	 * @Param request : ��û ��ü
	 * @Param request : ���� Attribute name��
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
	 * ���ǿ� �ִ� �α��� ���̵�� �Ķ���Ͱ� ���Ͽ� ������ ���� 
	 * @Param request : ��û ��ü
	 * @Param compareID : ���� ���̵�
	 * */
	public Boolean loginUserCompare(HttpServletRequest request, String compareID ) {
		HttpSession session = request.getSession();
		Object tempObj=session.getAttribute("userID");
		if(tempObj!=null) {
			String userID=tempObj.toString();
			if(userID.equals(compareID)) { //�α��ε� ���̵�� ���� ���̵� ���ٸ�
				return true;
			}else {
				return false;
			}
		}
		return false;
		
	}
	
}
