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
	
	/*��й�ȣ ��ȣȭ�� ������*/
	private  String RSA_WEB_KEY = "_RSA_WEB_Key_"; // ����Ű session key
	private  String RSA_INSTANCE = "RSA"; // rsa transformation
	private String modifyAuthKey="copyRight_ilovepc"; //modifyPasswordCheck�� ���ؼ��� memberModify.jsp�� �����ϰ� �ϴ� Ű��.
	static Random random = new Random(); //����� ���ϸ� �ߺ��� �ּ�ȭ �ϱ����� �����Լ�
	
	public String getModifyAuthKey() {
		return modifyAuthKey;
	}
	
	protected String getRSA_WEB_KEY() {
		return RSA_WEB_KEY;
	}
	
	protected String getRSA_INSTANCE() {
		return RSA_INSTANCE;
	}
	
	//Ŭ���̾�Ʈ�� ��Ȯ�� IP�� �˾Ƴ��� �޼ҵ�
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
	 * �н����� ��ȣȭ
	 * 1. RSA ����Ű, ����Ű ����
	 * 
	 * */
	public void initRsa(HttpServletRequest request) {
		HttpSession session = request.getSession(); //���� ����
		KeyPairGenerator generator;
		
		try {
			generator = KeyPairGenerator.getInstance(RSA_INSTANCE);
			generator.initialize(1024);
			
			KeyPair keyPair = generator.genKeyPair();
			KeyFactory keyFactory = KeyFactory.getInstance(RSA_INSTANCE);
			PublicKey publicKey = keyPair.getPublic();
			PrivateKey privateKey = keyPair.getPrivate();
			
			session.setAttribute(RSA_WEB_KEY, privateKey); // session�� RSA ����Ű�� ����
			RSAPublicKeySpec publicSpec = (RSAPublicKeySpec) keyFactory.getKeySpec(publicKey, RSAPublicKeySpec.class);
			String publicKeyModulus = publicSpec.getModulus().toString(16);
			String publicKeyExponent = publicSpec.getPublicExponent().toString(16);
			
			request.setAttribute("RSAModulus", publicKeyModulus); // rsa modulus �� request �� �߰�
            request.setAttribute("RSAExponent", publicKeyExponent); // rsa exponent �� request �� �߰�
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	/*
	 * �н����� ��ȣȭ
	 * 1. �ܹ��� �ؽ� ��ȣȭ
	 * @param encryptValue   ��(��ȣȭ ���)
	 * @param mod   ��ȣȭ ���
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
	 * �н����� ��ȣȭ
	 * @param privateKey      ����Ű
	 * @param securedValue    ��ȣ��
	 * @return decryptedValue ��ȣ��
	 * @throws Exception
	 * */
	protected String decryptRsa(PrivateKey privateKey, String securedValue)throws Exception{
		Cipher cipher = Cipher.getInstance(RSA_INSTANCE);
		byte[] encryptedBytes = hexToByteArray(securedValue);
		cipher.init(Cipher.DECRYPT_MODE, privateKey);
		byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
		String decryptedValue = new String(decryptedBytes, "utf-8"); // ���� ���ڵ� ����.
        return decryptedValue;
	}
	
	/**
     * 16�� ���ڿ��� byte �迭�� ��ȯ�Ѵ�.
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
	 * ����Խñۿ� ���� �� �� ����� �̹����� ����� �޼ҵ�
	 * @param width ����� �̹��� width
	 * @param height ����� �̹��� height
	 * @param photoOrderMap ����� �̹��� Map�� ��� Map (photoOrderMap�ȿ� photoThumbMap�� ��)
	 * @param stringParamMap �Խ��� ����, ���� ����,�±׵��� ����Ǵ� ��  Map (���⼭�� ������ ������ �� ����)
	 * @param directory �̹����� ����� ���丮
	 * @param flag ��������, ���ν���� ���ϴ� �÷��׺���
	 * */
    public static void thumbImageCreate(int width, int height, 
    		Map<String,Map<String,String>> photoOrderMap,
    		Map<String,String> stringParamMap, String directory, String flag) {
    	
    	String photoOrderMapKey=""; //photoOrderMap�� Ű������ ���� ���� (����, ���꿡 ���� Ű���� �޶�����. ���� ����鵵 Ű���� �ٸ���)
    	String photoThumbMapKey="";
    	int photo_length = Integer.parseInt(stringParamMap.get("length").toString()); //���̺��� ����
    	
    	if(flag.equals("main")) { //���λ����̶��
    		photoOrderMapKey="imageMain"; //Ű���� �̷���
    		photo_length=0; //���λ����̸� 1���̱� ������ �ݺ����� 1���� ���Ƶ� �ȴ�.
    		photoThumbMapKey="photoView_MainThumbImage"; //�����̹����� �� photoThumbMap�� ���� Key�� 
    	}
    	
    	for(int photoCount=0; photoCount<=photo_length; photoCount++) {
    		
    		if(flag.equals("sub")) { //��������̶��
    			photoOrderMapKey="image_"+photoCount; //Ű���� �̷���
    			photoThumbMapKey="photoView_SubThumbImage"; //�����̹����� �� photoThumbMap�� ���� Key�� 
    		}
	    	String imageMain = photoOrderMap.get(photoOrderMapKey).get("serverUploadName"); //���� ���ϸ�
			String subject = stringParamMap.get("subject").trim(); //����� ���ϸ� ���� ���ؼ� �Խñ� ������ �����´�. 
			subject=subject.replaceAll(" ", ""); //���� ��������
			subject=subject.replaceAll("(^\\p{Z}+|\\p{Z}+$)", ""); //���� ��� ��������
			imageMain=directory+File.separator+imageMain; //���+���λ�����
			int randomNumber=random.nextInt(10000); //����� ���ϸ� ���� ���� ���� ��
			String thumb = subject+"_"+randomNumber+"_thumb.jpg"; //����� ���ϸ� (DB�� ��)
			String thumbImg = directory+File.separator+subject+"_"+randomNumber+"_thumb.jpg"; //���+����� ���ϸ�
			int thumbWidth = width ;//����� ����
	        int thumbHeight = height ;//����� ����
	        
	        Image thumbnail = JimiUtils.getThumbnail(imageMain, thumbWidth, thumbHeight, Jimi.IN_MEMORY,false);// ����� ����
	        try {
				Jimi.putImage(thumbnail, thumbImg); //����� ����
				photoOrderMap.get(photoOrderMapKey).put(photoThumbMapKey, thumb);
				//photoThumbMap.put(photoThumbMapKey,thumb);  //����ڰ� ���ε��� �̸�
			} catch (JimiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	} //end of For
    	
    }
    
    /*
	 * ����Խñۿ� ���� ������ �� ����� �̹����� ����� �޼ҵ�
	 * @param width : ����� �̹��� width
	 * @param height : ����� �̹��� height
	 * @param imageLocation : ���� �̹��� �������
	 * @param serverUploadName : �����̹����� ���� ���ε� �̸�
	 * @return thumbName : ������ ����� �̸� 
	 * */
    public static String thumbImageCreate(int width, int height, String imageLocation,String serverUploadName) {
    	String thumbName = getRandomString()+"_thumb.jpg";
    	String originalFile = imageLocation+File.separator+serverUploadName;//��������(���+���ϸ�)
    	String thumbNameWithPath = imageLocation+File.separator+thumbName; //�����(���+���ϸ�)
    	int thumbWidth = width ;//����� ����
        int thumbHeight = height ;//����� ����
        
        Image thumbnail = JimiUtils.getThumbnail(originalFile, thumbWidth, thumbHeight, Jimi.IN_MEMORY,false);// ����� ����
        try {
			Jimi.putImage(thumbnail, thumbNameWithPath); //����� ����
		} catch (JimiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return thumbName;
    }
    
	//UUID�� �������� �����Ͽ� 32���� ������ ���ڿ��� ��ȯ�մϴ�.
    public static String getRandomString() {
    	return UUID.randomUUID().toString().replaceAll("-", "");
    }
    
	/*
	 * Map�� json���� ��ȯ�Ѵ�.
	 * @param map json���� ��ȯ�� ��
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
