package vo.photoView;

public class PhotoBoard_sub {
	int photo_subNo; //서브 이미지 고유번호
	int photo_ownNo; //해당 게시글 내 서브 이미지의 번호(순서)
	String photo_sub; //서브 이미지 파일명
	String photo_content; //서브 이미지 설명
	
	public int getPhoto_subNo() {
		return photo_subNo;
	}
	public void setPhoto_subNo(int photo_subNo) {
		this.photo_subNo = photo_subNo;
	}
	public int getPhoto_ownNo() {
		return photo_ownNo;
	}
	public void setPhoto_ownNo(int photo_ownNo) {
		this.photo_ownNo = photo_ownNo;
	}
	public String getPhoto_sub() {
		return photo_sub;
	}
	public void setPhoto_sub(String photo_sub) {
		this.photo_sub = photo_sub;
	}
	public String getPhoto_content() {
		return photo_content;
	}
	public void setPhoto_content(String photo_content) {
		this.photo_content = photo_content;
	}
	
}
