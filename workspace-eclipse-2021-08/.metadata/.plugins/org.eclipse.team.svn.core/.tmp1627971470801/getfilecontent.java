package kr.co.hanshinit.NeoCMS.cmm.service;

import java.io.Serializable;

import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;

import org.apache.commons.lang.builder.ToStringBuilder;
/**
 * 공통으로 사용하는 모델 클래스
 * @author (주)한신정보기술 연구개발팀 최관형
 * @since 2014.04.23
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일           수정자     수정내용
 *  ------------- -------- ---------------------------
 *  2014.04.23 최관형     최초 생성
 *
 * </pre>
 */
public class CmmModel implements Serializable {

	private static final long serialVersionUID = -7377893036433911489L;

	/** 최초등록시점 */
	private String frstRegisterPnttm;

	/** 최초등록자ID */
	private String frstRegisterId;

	/** 최초등록자IP */
	private String frstRegisterIp;

	/** 최종수정시점 */
	private String lastUpdusrPnttm;

	/** 최종수정자ID */
	private String lastUpdusrId;

	/** 최종수정자IP */
	private String lastUpdusrIp;

	public String getFrstRegisterPnttm() {
		return frstRegisterPnttm;
	}

	public String getFrstRegisterPnttmYMD() {
		return DateUtil.toDateFormat(frstRegisterPnttm, "yyyyMMddHHmmss", "yyyy.MM.dd");
	}

	public String getFrstRegisterPnttmYMD2() {
		return DateUtil.toDateFormat(frstRegisterPnttm, "yyyyMMddHHmmss", "yyyy-MM-dd");
	}

	public String getFrstRegisterPnttmYMDHMS() {
		return DateUtil.toDateFormat(frstRegisterPnttm, "yyyyMMddHHmmss", "yyyy년 MM월 dd일 HH시 mm분 ss초");
	}

	public String getFrstRegisterPnttmYMDHMS2() {
		return DateUtil.toDateFormat(frstRegisterPnttm, "yyyyMMddHHmmss", "yyyy년 MM월 dd일");
	}

	public String getFrstRegisterPnttmMD() {
		return DateUtil.toDateFormat(frstRegisterPnttm, "yyyyMMddHHmmss", "MM-dd");
	}

	public String getFrstRegisterPnttmHMS() {
    return DateUtil.toDateFormat(frstRegisterPnttm, "yyyyMMddHHmmss", "HH:mm:ss");
  }

	public void setFrstRegisterPnttm(String frstRegisterPnttm) {
		this.frstRegisterPnttm = frstRegisterPnttm;
	}

	public String getFrstRegisterId() {
		return frstRegisterId;
	}

	public void setFrstRegisterId(String frstRegisterId) {
		this.frstRegisterId = frstRegisterId;
	}

	public String getLastUpdusrPnttm() {
		return lastUpdusrPnttm;
	}

	public void setLastUpdusrPnttm(String lastUpdusrPnttm) {
		this.lastUpdusrPnttm = lastUpdusrPnttm;
	}

	public String getLastUpdusrId() {
		return lastUpdusrId;
	}

	public void setLastUpdusrId(String lastUpdusrId) {
		this.lastUpdusrId = lastUpdusrId;
	}

	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	public String getLastUpdusrPnttmYMD() {
		return DateUtil.toDateFormat(lastUpdusrPnttm, "yyyyMMddHHmmss", "yyyy.MM.dd");
	}

	public String getLastUpdusrPnttmYMD2()
	{
		return DateUtil.toDateFormat(lastUpdusrPnttm, "yyyyMMddHHmmss", "yyyy.MM.dd HH:mm");
	}

	public String getFrstRegisterIp() {
		return frstRegisterIp;
	}

	public void setFrstRegisterIp(String frstRegisterIp) {
		this.frstRegisterIp = frstRegisterIp;
	}

	public String getLastUpdusrIp() {
		return lastUpdusrIp;
	}

	public void setLastUpdusrIp(String lastUpdusrIp) {
		this.lastUpdusrIp = lastUpdusrIp;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
