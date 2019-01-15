package util;

import java.awt.Image;
import java.io.File;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.crypto.Cipher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;

import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.JimiUtils;

public class UtilClass {
	
	/*비밀번호 암호화할 변수들*/
	private  String RSA_WEB_KEY = "_RSA_WEB_Key_"; // 개인키 session key
	private  String RSA_INSTANCE = "RSA"; // rsa transformation
	private String modifyAuthKey="copyRight_ilovepc"; //modifyPasswordCheck를 통해서만 memberModify.jsp에 접근하게 하는 키값.
	static Random random = new Random(); //썸네일 파일명 중복을 최소화 하기위한 랜덤함수
	
	public String getModifyAuthKey() {
		return modifyAuthKey;
	}
	
	protected String getRSA_WEB_KEY() {
		return RSA_WEB_KEY;
	}
	
	protected String getRSA_INSTANCE() {
		return RSA_INSTANCE;
	}
	
	//클라이언트의 정확한 IP를 알아내는 메소드
	public String getClientIP(HttpServletRequest request) {
		String clientIP=request.getHeader("HTTP_X_FORWARDED_FOR");
		
		if(null == clientIP || clientIP.length() == 0
				   || clientIP.toLowerCase().equals("unknown")){
			clientIP = request.getHeader("REMOTE_ADDR");
		}
				 
		if(null == clientIP || clientIP.length() == 0
				   || clientIP.toLowerCase().equals("unknown")){
			clientIP = request.getRemoteAddr();
		}

		return clientIP;
	}
	
	
	/**
	 * 패스워드 암호화
	 * 1. RSA 공개키, 개인키 생성
	 * 
	 * */
	public void initRsa(HttpServletRequest request) {
		HttpSession session = request.getSession(); //세션 생성
		KeyPairGenerator generator;
		
		try {
			generator = KeyPairGenerator.getInstance(RSA_INSTANCE);
			generator.initialize(1024);
			
			KeyPair keyPair = generator.genKeyPair();
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_INSTANCE);
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			session.setAttribute(RSA_WEB_KEY, privateKey); // session에 RSA 개인키를 저장
			RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			String publicKeyModulus = publicSpec.getModulus().toString(16);
			String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
			
			request.setAttribute("RSAModulus", publicKeyModulus); // rsa modulus 를 request 에 추가
            request.setAttribute("RSAExponent", publicKeyExponent); // rsa exponent 를 request 에 추가
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * 패스워드 암호화
	 * 1. 단방향 해쉬 암호화
	 * @param encryptValue   평문(암호화 대상)
	 * @param mod   암호화 방법
	 **/
	public String HashEncryption(String encryptValue,String mod) {
		try {
			MessageDigest md = MessageDigest.getInstance(mod);
			md.update(encryptValue.getBytes());
			byte byteData[] =  md.digest();
			
			StringBuffer sb = new StringBuffer();
			for(int i=0; i<byteData.length; i++) {
				sb.append(Integer.toString((byteData[i]&0xff) + 0x100, 16).substring(1));
			}
			
			String result = sb.toString();
			return result;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
				
	}
	
	/**
	 * 패스워드 복호화
	 * @param privateKey      개인키
	 * @param securedValue    암호문
	 * @return decryptedValue 복호문
	 * @throws Exception
	 * */
	protected String decryptRsa(PrivateKey privateKey, String securedValue)throws Exception{
		Cipher cipher = Cipher.getInstance(RSA_INSTANCE);
		byte[] encryptedBytes = hexToByteArray(securedValue);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes, "utf-8"); // 문자 인코딩 주의.
        return decryptedValue;
	}
	
	/**
     * 16진 문자열을 byte 배열로 변환한다.
     * 
     * @param hex
     * @return
     */
    public static byte[] hexToByteArray(String hex) {
        if (hex == null || hex.length() % 2 != 0) { return new byte[] {}; }
 
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < hex.length(); i += 2) {
            byte value = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
            bytes[(int) Math.floor(i / 2)] = value;
        }
        return bytes;
    }
    
    /*
	 * 포토게시글에 글을 쓸 때 썸네일 이미지를 만드는 메소드
	 * @param width 썸네일 이미지 width
	 * @param height 썸네일 이미지 height
	 * @param photoOrderMap 썸네일 이미지 Map이 담길 Map (photoOrderMap안에 photoThumbMap이 들어감)
	 * @param stringParamMap 게시판 제목, 사진 내용,태그들이 저장되는 맵  Map (여기서는 제목을 꺼내서 쓸 예정)
	 * @param directory 이미지가 저장될 디렉토리
	 * @param flag 서브썸네일, 메인썸네일 정하는 플래그변수
	 * */
    public static void thumbImageCreate(int width, int height, 
    		Map<String,Map<String,String>> photoOrderMap,
    		Map<String,String> stringParamMap, String directory, String flag) {
    	
    	String photoOrderMapKey=""; //photoOrderMap의 키값으로 쓰일 변수 (메인, 서브에 따라 키값이 달라진다. 또한 서브들도 키값이 다르다)
    	String photoThumbMapKey="";
    	int photo_length = Integer.parseInt(stringParamMap.get("length").toString()); //테이블의 길이
    	
    	if(flag.equals("main")) { //메인사진이라면
    		photoOrderMapKey="imageMain"; //키값을 이렇게
    		photo_length=0; //메인사진이면 1장이기 때문에 반복문을 1번만 돌아도 된다.
    		photoThumbMapKey="photoView_MainThumbImage"; //메인이미지일 때 photoThumbMap에 넣을 Key값 
    	}
    	
    	for(int photoCount=0; photoCount<=photo_length; photoCount++) {
    		
    		if(flag.equals("sub")) { //서브사진이라면
    			photoOrderMapKey="image_"+photoCount; //키값을 이렇게
    			photoThumbMapKey="photoView_SubThumbImage"; //서브이미지일 때 photoThumbMap에 넣을 Key값 
    		}
	    	String imageMain = photoOrderMap.get(photoOrderMapKey).get("serverUploadName"); //사진 파일명
			String subject = stringParamMap.get("subject").trim(); //썸네일 파일명에 쓰기 위해서 게시글 제목을 가져온다. 
			subject=subject.replaceAll(" ", ""); //제목 공백제거
			subject=subject.replaceAll("(^\\p{Z}+|\\p{Z}+$)", ""); //제목 모든 공백제거
			imageMain=directory+File.separator+imageMain; //경로+메인사진명
			int randomNumber=random.nextInt(10000); //썸네일 파일명에 쓰기 위한 랜덤 값
			String thumb = subject+"_"+randomNumber+"_thumb.jpg"; //썸네일 파일명 (DB에 들어갈)
			String thumbImg = directory+File.separator+subject+"_"+randomNumber+"_thumb.jpg"; //경로+썸네일 파일명
			int thumbWidth = width ;//썸네일 가로
	        int thumbHeight = height ;//썸네일 세로
	        
	        Image thumbnail = JimiUtils.getThumbnail(imageMain, thumbWidth, thumbHeight, Jimi.IN_MEMORY,false);// 썸네일 설정
	        try {
				Jimi.putImage(thumbnail, thumbImg); //썸네일 생성
				photoOrderMap.get(photoOrderMapKey).put(photoThumbMapKey, thumb);
				//photoThumbMap.put(photoThumbMapKey,thumb);  //사용자가 업로드한 이름
			} catch (JimiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} //end of For
    	
    }
    
    /*
	 * 포토게시글에 글을 수정할 때 썸네일 이미지를 만드는 메소드
	 * @param width : 썸네일 이미지 width
	 * @param height : 썸네일 이미지 height
	 * @param imageLocation : 원본 이미지 저장장소
	 * @param serverUploadName : 원본이미지의 서버 업로드 이름
	 * @return thumbName : 생성된 썸네일 이름 
	 * */
    public static String thumbImageCreate(int width, int height, String imageLocation,String serverUploadName) {
    	String thumbName = getRandomString()+"_thumb.jpg";
    	String originalFile = imageLocation+File.separator+serverUploadName;//원본사진(경로+파일명)
    	String thumbNameWithPath = imageLocation+File.separator+thumbName; //썸네일(경로+파일명)
    	int thumbWidth = width ;//썸네일 가로
        int thumbHeight = height ;//썸네일 세로
        
        Image thumbnail = JimiUtils.getThumbnail(originalFile, thumbWidth, thumbHeight, Jimi.IN_MEMORY,false);// 썸네일 설정
        try {
			Jimi.putImage(thumbnail, thumbNameWithPath); //썸네일 생성
		} catch (JimiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thumbName;
    }
    
	//UUID를 랜덤으로 생성하여 32자의 랜덤한 문자열을 반환합니다.
    public static String getRandomString() {
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
	/*
	 * Map을 json으로 변환한다.
	 * @param map json으로 변환될 맵
	 * */
	public JSONObject getJsonStringFromMap(Map<Integer,Object>map) {
		JSONObject jsonObject = new JSONObject();
		
		for(Map.Entry<Integer, Object> entry : map.entrySet()) {
			int key = entry.getKey();
			Object value = entry.getValue();
			jsonObject.put(key, value);
		}
		
		
		return jsonObject;
	}
	
}
