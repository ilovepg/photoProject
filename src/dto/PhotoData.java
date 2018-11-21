package dto;

import java.util.ArrayList;
import java.util.List;

import vo.photoView.PhotoBoard_sub;
import vo.photoView.PhotoBoard_tag;
import vo.photoView.PhotoBoard_thumb;

/*포토 뷰 페이지 데이터*/
public class PhotoData {
	
	//포토게시판_메인 데이터
	private int photo_boardNo; //게시글 고유번호
	private String photo_subject; //게시글 제목
	private String photo_main; //게시글 메인사진
	private String photo_a_line_review; //게시글 한줄평
	private String photo_writer; // 게시글 작성자
	private String photo_upload_date ; //게시글 작성시간
	
	/*데이터가 가변적인 여러개라서 List로 가지고 있음.*/
	//포토게시판_서브 데이터
	List<PhotoBoard_sub> photoBoard_sub = new ArrayList<PhotoBoard_sub>();
	//포토게시판_태그
	List<PhotoBoard_tag> photoBoard_tag = new ArrayList<PhotoBoard_tag>();
	//포토게시판_썸네일
	List<PhotoBoard_thumb> photoBoard_thumb = new ArrayList<PhotoBoard_thumb>();
	
	public int getPhoto_boardNo() {
		return photo_boardNo;
	}
	public void setPhoto_boardNo(int photo_boardNo) {
		this.photo_boardNo = photo_boardNo;
	}
	public String getPhoto_subject() {
		return photo_subject;
	}
	public void setPhoto_subject(String photo_subject) {
		this.photo_subject = photo_subject;
	}
	public String getPhoto_main() {
		return photo_main;
	}
	public void setPhoto_main(String photo_main) {
		this.photo_main = photo_main;
	}
	public String getPhoto_a_line_review() {
		return photo_a_line_review;
	}
	public void setPhoto_a_line_review(String photo_a_line_review) {
		this.photo_a_line_review = photo_a_line_review;
	}
	public String getPhoto_writer() {
		return photo_writer;
	}
	public void setPhoto_writer(String photo_writer) {
		this.photo_writer = photo_writer;
	}
	public String getPhoto_upload_date() {
		return photo_upload_date;
	}
	public void setPhoto_upload_date(String photo_upload_date) {
		this.photo_upload_date = photo_upload_date;
	}
	public List<PhotoBoard_sub> getPhotoBoard_sub() {
		return photoBoard_sub;
	}
	
	public List<PhotoBoard_tag> getPhotoBoard_tag() {
		return photoBoard_tag;
	}
	
	public List<PhotoBoard_thumb> getPhotoBoard_thumb() {
		return photoBoard_thumb;
	}
	
	public void addPhotoBoard_sub(PhotoBoard_sub item) {
		photoBoard_sub.add(item);
	}

	public void addPhotoBoard_tag(PhotoBoard_tag item) {
		photoBoard_tag.add(item);
	}
	
	public void addPhotoBoard_thumb(PhotoBoard_thumb item) {
		photoBoard_thumb.add(item);
	}
	@Override
	public String toString() {
		return "PhotoData [photo_boardNo=" + photo_boardNo + ", photo_subject=" + photo_subject + ", photo_main="
				+ photo_main + ", photo_a_line_review=" + photo_a_line_review + ", photo_writer=" + photo_writer
				+ ", photo_upload_date=" + photo_upload_date + ", photoBoard_sub=" + photoBoard_sub
				+ ", photoBoard_tag=" + photoBoard_tag + ", photoBoard_thumb=" + photoBoard_thumb
				+ ", getPhoto_boardNo()=" + getPhoto_boardNo() + ", getPhoto_subject()=" + getPhoto_subject()
				+ ", getPhoto_main()=" + getPhoto_main() + ", getPhoto_a_line_review()=" + getPhoto_a_line_review()
				+ ", getPhoto_writer()=" + getPhoto_writer() + ", getPhoto_upload_date()=" + getPhoto_upload_date()
				+ ", getPhotoBoard_sub()=" + getPhotoBoard_sub() + ", getPhotoBoard_tag()=" + getPhotoBoard_tag()
				+ ", getPhotoBoard_thumb()=" + getPhotoBoard_thumb() + "]";
	}
	
	
	
}
