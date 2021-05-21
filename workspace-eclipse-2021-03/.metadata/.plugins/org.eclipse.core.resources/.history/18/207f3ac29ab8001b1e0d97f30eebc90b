package kr.co.hanshinit.NeoCMS.tag.function;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.text.Normalizer;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.support.WebApplicationContextUtils;

import kr.co.hanshinit.NeoCMS.cmm.service.FileMngUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmCodeService;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.MultiKeyHashMap;
/**
 * 문자열 관련 태그 라이브러리 클래스
 * @author (주)한신정보기술 연구개발팀 최관형
 * @since 2014.06.08
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일      수정자    수정내용
 *  ---------- -------- ---------------------------
 *  2014.06.08 최관형    최초 생성
 *  2014.06.19 최관형    htmlSpecialChars 메소드 추가
 *  2017.01.03 최관형    nvl 추가
 *  2017.01.03 최관형    isNumber 메소드 추가
 *  2017.01.12 최관형    autoLink 메소드 추가
 *  2017.03.02 박시내    getDayOfWeek 메소드 추가
 * </pre>
 */
public class TagStringUtil {

	public static String unscript(String str) {
		return StringUtil.unscript(str);
	}

	public static String nl2br(String str) {
		return StringUtil.nl2br(str);
	}

	public static String html2text(String str) {
		return StringUtil.html2text(str);
	}

	public static String replacePhoneNum(String str) {
		return StringUtil.replacePhoneNum(str);
	}

	public static String toDateFormat(String strDate, String strType1, String strType2) {
		return DateUtil.toDateFormat(strDate, strType1, strType2);
	}

	public static String zerofill(Integer str, Integer len, String fill) {
		return StringUtil.zerofill(str, len, fill);
	}

	public static String zerofill(String str, int len, String fill) {
		return StringUtil.zerofill(str, len, fill);
	}

	public static String htmlSpecialChars(String str) {
		return StringUtil.htmlSpecialChars(str);
	}

	public static String cutString(String str, Integer maxSize, String trail) {
		return StringUtil.cutOffUTF8String(str, maxSize, trail);
	}

	public static String getStatusRange(String sdate, String edate) {

		if( StringUtil.isEmpty(sdate) || StringUtil.isEmpty(edate) ) return "";

		try {

			String result = "";

			int intNdate = Integer.parseInt(DateUtil.getNowDateTime("yyyyMMdd"));

			int intSdate = Integer.parseInt(sdate.replaceAll("-", "").replaceAll(" ", ""));
			int intEdate = Integer.parseInt(edate.replaceAll("-", "").replaceAll(" ", ""));

			if( intSdate > intNdate && intNdate < intEdate ) {
				result = "예정";
			} else if( intSdate < intNdate && intNdate > intEdate ) {
				result = "종료";
			} else if( intSdate <= intNdate && intNdate <= intEdate ) {
				result = "진행";
			}

			return result;

		} catch(Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static String getFileExt(String fileName) {
		return FileMngUtil.getFileExt(fileName);
	}

	/**
	 * fileExtToImageFile("ico_", ".gif", fileName, false)
	 * @param prefix
	 * @param postfix
	 * @param fileName
	 * @param folder
	 * @return
	 */
	public static String fileExtToImageFile(String prefix, String postfix, String fileName) {

		// 확장자
		String ext = FileMngUtil.getFileExt(fileName);

		// 이미지 찾기
		String imageFile = ext;

		imageFile = prefix + imageFile + postfix;

		File file = new File(realPath() + "/common/images/board/file/" + imageFile);
		if( !file.exists() ) {
			imageFile = prefix + "noimg" + postfix;
		}

		return imageFile;

	}

	public static String realPath() {

		String path = TagStringUtil.class.getResource("TagStringUtil.class").getPath();
		File file = new File(path);
		path = StringUtils.replace(StringUtils.replace(file.getAbsolutePath(), "\\", "/"), "//", "/");
		String webRoot = path.substring(0, path.lastIndexOf("/WEB-INF"));

		return webRoot;

	}

	public static String nvl(String str) {
		return StringUtil.nvl(str);
	}

	public static boolean isNumber(String str) {
		return StringUtil.isNumber(str);
	}

	public static String autoLink(String str) {
		return StringUtil.autoLink(str);
	}

	/**
     * 첨부파일 용량 변환
     * @param bytes
     * @return String
     */
    public static String fileSizeUnit(Long bytes) {
    	return FileMngUtil.fileSizeUnit(bytes);
    }

	public static Integer toInteger(Integer integer) {
		return integer;
	}

	public static String getNowDateTime(String format) {
		return DateUtil.getNowDateTime(format);
	}

	public static String getDayOfWeek(String strDate) {

		String result = "";
		String dayOfWeek = DateUtil.getDayOfWeek(strDate);

		if( dayOfWeek.equals("1") ) {
			result = "일";
		}else if( dayOfWeek.equals("2") ) {
			result = "월";
		}else if( dayOfWeek.equals("3") ) {
			result = "화";
		}else if( dayOfWeek.equals("4") ) {
			result = "수";
		}else if( dayOfWeek.equals("5") ) {
			result = "목";
		}else if( dayOfWeek.equals("6") ) {
			result = "금";
		}else if( dayOfWeek.equals("7") ) {
			result = "토";
		}
		return result;
	}

	public static String blindString(String str, String len) {
		return StringUtil.blindString(str, Integer.parseInt(len));
	}

	public static String normalize(String str) {
		try {
			return Normalizer.normalize(str, Normalizer.Form.NFKC);
		} catch(Exception e) {
			return str;
		}
	}

    public static String unXPath(String str) {
    	return StringUtil.unXPath(str);
    }

	public static String urlEncode(String str) {
		return StringUtil.UrlEncoding(str);
	}

	public static String urlEncodeByEnc(String str, String enc) {
		return StringUtil.UrlEncoding(str, enc);
	}

	public static String getDateAddMonthYMD(String strDate, Integer month){
		return DateUtil.getDateAddMonthYMD(strDate, month);
	}



    /**
     * Encoding을 한다
     * @param src
     * @param encType
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String encode(String src,String encType) throws UnsupportedEncodingException {
        return StringUtil.urlEncode(src, encType);
    }
    /**
     * Decoding을 한다
     * @param src
     * @param decType
     * @return
     * @throws UnsupportedEncodingException
     */
    public static String decode(String src,String decType) throws UnsupportedEncodingException {
        return StringUtil.urlDecode(src, decType);
    }

	public static MultiKeyHashMap<String, String, String> edcCmmMultiCode(String codes) throws Exception {
		ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
		HttpServletRequest hsr = sra.getRequest();
		WebApplicationContext wac = null;
		wac = WebApplicationContextUtils.getRequiredWebApplicationContext(hsr.getSession().getServletContext());
		EdcCmmCodeService edcCmmCodeService = (EdcCmmCodeService)wac.getBean("edcCmmCodeService");
		return edcCmmCodeService.selectEdcCmmMultiCode(codes);
	}

	public static TreeMap<String, String> edcCmmCode(MultiKeyHashMap<String, String, String> multiMap, String code) {
		System.out.println("code : " + code);
		System.out.println(multiMap.get(code) + "!@#!@#");
		return new TreeMap<String, String>(multiMap.get(code));
	}
}
