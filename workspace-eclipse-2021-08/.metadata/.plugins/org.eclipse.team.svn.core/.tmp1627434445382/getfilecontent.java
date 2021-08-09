/************************************************************************************************
 * 통합예약
 *						강좌 관련 Service Implements
 * @author  (주)한신정보기술 개발3팀
 * @since    2018.10.20
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일            수정자        수정내용
 *  -------------      --------    ---------------------------
 *  2018.10.20  개발3팀     최초 생성
 *
 * </pre>
 ***************************************************************************************************/
package kr.co.hanshinit.NeoEdu.cop.edcLctre.service.impl;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.co.hanshinit.NeoCMS.cmm.service.AttachmentFileUtil;
import kr.co.hanshinit.NeoCMS.cmm.service.FileMngUtil;
import kr.co.hanshinit.NeoCMS.cmm.service.ResponseJSON;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCop.cop.cmm.service.StaffActionHistoryService;
import kr.co.hanshinit.NeoCop.cop.cmm.service.StaffActionHistoryVO;
import kr.co.hanshinit.NeoCop.cop.cmm.util.StaffLoginUtil;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldBndl;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldService;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldVO;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionBndl;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionService;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.CodeCmmVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmCodeService;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctre;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreActpln;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreService;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreVO;
import kr.co.hanshinit.NeoEdu.cop.resveStatusHistory.service.ResveStatusHistoryService;
import kr.co.hanshinit.NeoEdu.cop.resveStatusHistory.service.ResveStatusHistoryVO;


@Service("edcLctreService")
public class EdcLctreServiceImpl extends EgovAbstractServiceImpl implements EdcLctreService {

	@Resource(name="edcLctreDAO")
	private EdcLctreDAO edcLctreDAO;

    @Resource(name="attachmentFileUtil")
    private AttachmentFileUtil attachmentFileUtil;

    @Resource(name="FileMngUtil")
	private FileMngUtil fileMngUtil;

    /** 코드 정보 조회 서비스  **/
	@Resource(name="edcCmmCodeService")
	private EdcCmmCodeService edcCmmCodeService;

	/** 상태값 변경 관련 서비스 **/
//	@Resource(name = "resveStatusHistoryService")
//	private ResveStatusHistoryService resveStatusHistoryService;

	@Resource(name="staffActionHistoryService")
	private StaffActionHistoryService staffActionHistoryService;


	/** 옵션정보 관련 서비스(교육대상, 할인대상) **/
	@Resource(name="resveOptionService")
	private ResveOptionService resveOptionService;

	/** 프로그램 신청양식 추가 등록 **/
	@Resource(name="resveAddFieldService")
	private ResveAddFieldService resveAddFieldService;

	/**
	 * 교육강좌 분류정보 및 강좌정보의 목록을 조회한다.
	 * @param EdcLctreVO
	 * @return List<EdcLctreVO> 교육강좌 분류정보 및 강좌정보 목록
	 * @throws Exception
	 */
	@Override
	public List<EdcLctreVO> selectEdcLctreList(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();
		cmmVo.setSearchCodeType("'USE_AT', 'CLOSE_SE', 'CLOSE_APPLC_SE' ");
		cmmVo.setSearchUseAt("Y");
		cmmVo.setSearchDeleteAt("N");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		List<EdcLctreVO> list = edcLctreDAO.selectEdcLctreList( edcLctreVO );

		// 데이터가공 구간
		for(EdcLctreVO forVo : list){

		}

		return list;
	}

	/**
	 * 강좌 목록 조회
	 */
	@Override
	public List<EdcLctreVO> selectEdcLctreList(HttpServletRequest request, EdcLctreVO edcLctreVO, List<CodeCmmVO> insttList) throws Exception {
		String userId = StaffLoginUtil.getLoginId(request.getSession());

		if(!StaffLoginUtil.isAdmin(request.getSession())){
			if("".equals(userId)){
				edcLctreVO.setAuthUserId("000000000000000000000000");
			}else{
				edcLctreVO.setAuthUserId(userId);
			}
		}

		// 기관 목록 리스트 HashMap 생성
		HashMap<String, String> insttMap = new HashMap<String, String>();
		for(CodeCmmVO forVo : insttList){
			insttMap.put(forVo.getCodeValue(), forVo.getCodeNm());
		}

		// 코드값 HashMap 생성
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		cmmVo.setSearchCodeType("'TCH_LEC_DIV1', 'USE_AT', 'RCEPT_STTUS', 'LEC_TYPE', 'TCH_LEC_DIV1' ");
		cmmVo.setSearchUseAt("Y");
		cmmVo.setSearchDeleteAt("N");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		List<EdcLctreVO> list = edcLctreDAO.selectEdcLctreList( edcLctreVO );

		// 데이터가공 구간
		for(EdcLctreVO forVo : list){
			//forVo.setUseAt(codeHashMap.get("USE_AT"+forVo.getUseAt()));

			if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
				// 정원 체크 (상태값 확인)
				forVo = checkSttus(forVo);
			}

			forVo.setRceptSttusNm(codeHashMap.get("RCEPT_STTUS"+forVo.getRceptSttus()));
			forVo.setLctreLclas(codeHashMap.get("TCH_LEC_DIV1"+forVo.getLctreLclas()));
			forVo.setEdcMth(codeHashMap.get("LEC_TYPE"+forVo.getEdcMth()));

			if(forVo.getAtnclCt() == 0){
				forVo.setAtnclCtStr("무료");
			}else{
				forVo.setAtnclCtStr(StringUtil.getFormatComma(forVo.getAtnclCt()));
			}

			// 기관 한글명
			forVo.setInsttNm(insttMap.get(forVo.getInsttCode()+""));

			forVo.setPriorRceptBgnde(DateUtil.getDateFormat(forVo.getPriorRceptBgnde(), "yyyy.mm.dd"));
			forVo.setPriorRceptEndde(DateUtil.getDateFormat(forVo.getPriorRceptEndde(), "yyyy.mm.dd"));
			forVo.setRceptBgnde(DateUtil.getDateFormat(forVo.getRceptBgnde(), "yyyy-mm-dd hh:mi"));
			forVo.setRceptEndde(DateUtil.getDateFormat(forVo.getRceptEndde(), "yyyy-mm-dd hh:mi"));
			forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy.mm.dd"));
			forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy.mm.dd"));
		}

		return list;
	}

	/**
	 * 강좌 목록 조회
	 */
	@Override
	public List<EdcLctreVO> selectEdcLctreList(HttpServletRequest request, EdcLctreVO edcLctreVO, List<CodeCmmVO> insttList, String siteId) throws Exception {

		// 기관 목록 리스트 HashMap 생성
		HashMap<String, String> insttMap = new HashMap<String, String>();
		for(CodeCmmVO forVo : insttList){
			insttMap.put(forVo.getCodeValue(), forVo.getCodeNm());
		}

		// 코드값 HashMap 생성
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		cmmVo.setSearchCodeType("'TCH_LEC_DIV1', 'USE_AT', 'RCEPT_STTUS', 'LEC_TYPE' ");
		cmmVo.setSearchUseAt("Y");
		cmmVo.setSearchDeleteAt("N");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		List<EdcLctreVO> list = edcLctreDAO.selectEdcLctreList( edcLctreVO );

		// 데이터가공 구간
		for(EdcLctreVO forVo : list){
			forVo.setUseAt(codeHashMap.get("USE_AT"+forVo.getUseAt()));
			forVo.setRceptSttusNm(codeHashMap.get("RCEPT_STTUS"+forVo.getRceptSttus()));
			forVo.setLctreLclas(codeHashMap.get("TCH_LEC_DIV1"+forVo.getLctreLclas()));
			forVo.setEdcMth(codeHashMap.get("LEC_TYPE"+forVo.getEdcMth()));

			if(forVo.getAtnclCt() == 0){
				forVo.setAtnclCtStr("무료");
			}else{
				forVo.setAtnclCtStr(StringUtil.getFormatComma(forVo.getAtnclCt()));
			}

			// 기관 한글명
			forVo.setInsttNm(insttMap.get(forVo.getInsttCode()+""));

			forVo.setPriorRceptBgnde(DateUtil.getDateFormat(forVo.getPriorRceptBgnde(), "yyyy.mm.dd"));
			forVo.setPriorRceptEndde(DateUtil.getDateFormat(forVo.getPriorRceptEndde(), "yyyy.mm.dd"));

			forVo.setRceptBgndeHH(StringUtil.strSubString(forVo.getRceptBgnde(), 8, 10));
			forVo.setRceptBgndeMM(StringUtil.strSubString(forVo.getRceptBgnde(), 10, 12));

			forVo.setRceptEnddeHH(StringUtil.strSubString(forVo.getRceptEndde(), 8, 10));
			forVo.setRceptEnddeMM(StringUtil.strSubString(forVo.getRceptEndde(), 10, 12));

			forVo.setRceptBgnde(DateUtil.getDateFormat(forVo.getRceptBgnde(), "yyyy.mm.dd"));
			forVo.setRceptEndde(DateUtil.getDateFormat(forVo.getRceptEndde(), "yyyy.mm.dd"));
			forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy.mm.dd"));
			forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy.mm.dd"));
		}

		return list;
	}

	/**
	 * 교육강좌 분류정보 및 강좌정보의 레코드 수를 조회 한다.
	 * @param EdcLctreVO
	 * @return int 교육강좌 분류정보 및 강좌정보 레코드 수
	 * @throws Exception
	 */
	@Override
	public int selectEdcLctreTotCnt(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		return (Integer) edcLctreDAO.selectEdcLctreTotCnt( edcLctreVO );
	}

	/**
	 * 교육강좌 분류정보 및 강좌정보의 상세정보를 조회한다.
	 * @param EdcLctreVO
	 * @return EdcLctre 교육강좌 분류정보 및 강좌정보 의 상세정보
	 * @throws Exception
	 */
	@SuppressWarnings("null")
	public EdcLctre selectEdcLctreData(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		EdcLctre rtnVo = (EdcLctre) edcLctreDAO.selectEdcLctreData( edcLctreVO );

		if(rtnVo != null){
			// 데이터 가공
			if(rtnVo.getLctreKey() > 0){
				// 우선접수일
				rtnVo.setPriorRceptBgndeDay(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getPriorRceptBgnde(), 0, 8), "yyyy-mm-dd") );
				rtnVo.setPriorRceptBgndeTime(StringUtil.strSubString(rtnVo.getPriorRceptBgnde(), 8, 10) );
				rtnVo.setPriorRceptBgndeMinute(StringUtil.strSubString(rtnVo.getPriorRceptBgnde(), 10, 12) );
				rtnVo.setPriorRceptEnddeDay(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getPriorRceptEndde(), 0, 8), "yyyy-mm-dd") );
				rtnVo.setPriorRceptEnddeTime(StringUtil.strSubString(rtnVo.getPriorRceptEndde(), 8, 10) );
				rtnVo.setPriorRceptEnddeMinute(StringUtil.strSubString(rtnVo.getPriorRceptEndde(), 10, 12) );

				// 접수일
				rtnVo.setRceptBgndeDD(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getRceptBgnde(), 0, 8), "yyyy-mm-dd") );
				rtnVo.setRceptBgndeHH(StringUtil.strSubString(rtnVo.getRceptBgnde(), 8, 10) );
				rtnVo.setRceptBgndeMM(StringUtil.strSubString(rtnVo.getRceptBgnde(), 10, 12) );
				rtnVo.setRceptEnddeDD(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getRceptEndde(), 0, 8), "yyyy-mm-dd") );
				rtnVo.setRceptEnddeHH(StringUtil.strSubString(rtnVo.getRceptEndde(), 8, 10) );
				rtnVo.setRceptEnddeMM(StringUtil.strSubString(rtnVo.getRceptEndde(), 10, 12) );

				// 추가 접수일
				rtnVo.setAditRceptBgndeTime(StringUtil.strSubString(rtnVo.getAditRceptBgnde(), 8, 10) );
				rtnVo.setAditRceptBgndeMinute(StringUtil.strSubString(rtnVo.getAditRceptBgnde(), 10, 12) );
				rtnVo.setAditRceptBgnde(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getAditRceptBgnde(), 0, 8), "yyyy-mm-dd") );

				rtnVo.setAditRceptEnddeTime(StringUtil.strSubString(rtnVo.getAditRceptEndde(), 8, 10) );
				rtnVo.setAditRceptEnddeMinute(StringUtil.strSubString(rtnVo.getAditRceptEndde(), 10, 12) );
				rtnVo.setAditRceptEndde(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getAditRceptEndde(), 0, 8), "yyyy-mm-dd") );

				// 교육일정
				rtnVo.setEdcBgnde(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getEdcBgnde(), 0, 8), "yyyy-mm-dd") );
				rtnVo.setEdcEndde(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getEdcEndde(), 0, 8), "yyyy-mm-dd") );

				// 교육 시간 설정
				rtnVo.setEdcBeginMinute(StringUtil.strSubString(rtnVo.getEdcBeginTime(), 2, 4));
				rtnVo.setEdcBeginTime(StringUtil.strSubString(rtnVo.getEdcBeginTime(), 0, 2));
				rtnVo.setEdcEndMinute(StringUtil.strSubString(rtnVo.getEdcEndTime(), 2, 4));
				rtnVo.setEdcEndTime(StringUtil.strSubString(rtnVo.getEdcEndTime(), 0, 2));

				// 개설일
				if("".equals(StringUtil.strTrim(rtnVo.getOpenYear()))){
					rtnVo.setOpenDay(DateUtil.getNowDateTime("yyyy-MM-dd"));
				}else{
					rtnVo.setOpenDay(rtnVo.getOpenYear()+"-"+rtnVo.getOpenMonth()+"-"+rtnVo.getOpenDay());
				}

				// 결제 마감일  20181110 1600
				//rtnVo.setSetlePdHH(StringUtil.strSubString(rtnVo.getSetlePd(), 8, 10) );
				//rtnVo.setSetlePdMM(StringUtil.strSubString(rtnVo.getSetlePd(), 10, 12) );
				//rtnVo.setSetlePd(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getSetlePd(), 0, 8), "yyyy-mm-dd") );

				rtnVo.setIndvdlinfoHoldPd(DateUtil.getDateFormat(StringUtil.strSubString(rtnVo.getIndvdlinfoHoldPd(), 0, 8), "yyyy-mm-dd") );
				//rtnVo.setLctreIntrcn(StringUtil.enterReplace(rtnVo.getLctreIntrcn()));

				// &amp; ==> & 으로 변환
				rtnVo.setLctreNm(StringUtil.unescapeHtml3(rtnVo.getLctreNm()));

				String edcTime = rtnVo.getEdcTime();
				String[] edcBgndeTime = {"06","06","06","06","06","06","06"};
				String[] edcBgndeMinute = {"10","10","10","10","10","10","10"};
				String[] edcEndTime = {"06","06","06","06","06","06","06"};
				String[] edcEndMinute = {"10","10","10","10","10","10","10"};
				String[] edcTimeArr = null;


				if(!"".equals(StringUtil.strTrim(edcTime))){
					edcTimeArr = edcTime.split("\\|");

					for(int j=0;j<edcTimeArr.length;j++){
						edcBgndeTime[j] = StringUtil.strSubString(edcTimeArr[j], 0, 2);
						edcBgndeMinute[j] = StringUtil.strSubString(edcTimeArr[j], 2, 4);
						edcEndTime[j] = StringUtil.strSubString(edcTimeArr[j], 4, 6);
						edcEndMinute[j] = StringUtil.strSubString(edcTimeArr[j], 6, 8);
					}
				}

				if(edcTimeArr != null){
					rtnVo.setEdcBeginTimeArr(edcBgndeTime);
					rtnVo.setEdcBeginMinuteArr(edcBgndeMinute);
					rtnVo.setEdcEndTimeArr(edcEndTime);
					rtnVo.setEdcEndMinuteArr(edcEndMinute);
				}
			}
		}
		return rtnVo;
	}

	/**
	 * 교육 강좌 담당자 조회 (없을때 기관 담당자 전화번호 조회)
	 */
	public EdcLctre selectEdcLctreChargerData(HttpServletRequest request, int searchLctreKey) throws Exception {
		EdcLctre rtnVo = (EdcLctre) edcLctreDAO.selectEdcLctreChargerData( searchLctreKey );

		return rtnVo;
	}


	/**
	 * 선택 강좌 목록 조회 (기관명, 대분류명, 강좌명 조회)
	 * @param edcLctreVO
	 * @return
	 * @throws Exception
	 */
	public List<EdcLctreVO> selectEdcLctreChoiseList(EdcLctreVO edcLctreVO) throws Exception{
		
		List<EdcLctreVO> list = edcLctreDAO.selectEdcLctreChoiseList(edcLctreVO);
		
		for(int i=0; i < list.size(); i++){
			if(list != null){
				// 데이터 가공
				if(list.get(i).getLctreKey() > 0){
					// 우선접수일
					list.get(i).setPriorRceptBgndeDay(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getPriorRceptBgnde(), 0, 8), "yyyy-mm-dd") );
					list.get(i).setPriorRceptBgndeTime(StringUtil.strSubString(list.get(i).getPriorRceptBgnde(), 8, 10) );
					list.get(i).setPriorRceptBgndeMinute(StringUtil.strSubString(list.get(i).getPriorRceptBgnde(), 10, 12) );
					list.get(i).setPriorRceptEnddeDay(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getPriorRceptEndde(), 0, 8), "yyyy-mm-dd") );
					list.get(i).setPriorRceptEnddeTime(StringUtil.strSubString(list.get(i).getPriorRceptEndde(), 8, 10) );
					list.get(i).setPriorRceptEnddeMinute(StringUtil.strSubString(list.get(i).getPriorRceptEndde(), 10, 12) );
	
					// 접수일
					list.get(i).setRceptBgndeDD(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getRceptBgnde(), 0, 8), "yyyy-mm-dd") );
					list.get(i).setRceptBgndeHH(StringUtil.strSubString(list.get(i).getRceptBgnde(), 8, 10) );
					list.get(i).setRceptBgndeMM(StringUtil.strSubString(list.get(i).getRceptBgnde(), 10, 12) );
					list.get(i).setRceptEnddeDD(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getRceptEndde(), 0, 8), "yyyy-mm-dd") );
					list.get(i).setRceptEnddeHH(StringUtil.strSubString(list.get(i).getRceptEndde(), 8, 10) );
					list.get(i).setRceptEnddeMM(StringUtil.strSubString(list.get(i).getRceptEndde(), 10, 12) );
	
					// 추가 접수일
					list.get(i).setAditRceptBgndeTime(StringUtil.strSubString(list.get(i).getAditRceptBgnde(), 8, 10) );
					list.get(i).setAditRceptBgndeMinute(StringUtil.strSubString(list.get(i).getAditRceptBgnde(), 10, 12) );
					list.get(i).setAditRceptBgnde(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getAditRceptBgnde(), 0, 8), "yyyy-mm-dd") );
	
					list.get(i).setAditRceptEnddeTime(StringUtil.strSubString(list.get(i).getAditRceptEndde(), 8, 10) );
					list.get(i).setAditRceptEnddeMinute(StringUtil.strSubString(list.get(i).getAditRceptEndde(), 10, 12) );
					list.get(i).setAditRceptEndde(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getAditRceptEndde(), 0, 8), "yyyy-mm-dd") );
	
					// 교육일정
					list.get(i).setEdcBgnde(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getEdcBgnde(), 0, 8), "yyyy-mm-dd") );
					list.get(i).setEdcEndde(DateUtil.getDateFormat(StringUtil.strSubString(list.get(i).getEdcEndde(), 0, 8), "yyyy-mm-dd") );
	
				}
			}
		}
		
		return list;
	}

	/**
	 * 강좌 정보 조회 ( 1. 기관 및 분류)
	 */
	public EdcLctreVO selectEdcLctreStep01(HttpServletRequest request, EdcLctreVO edcLctreVO)  throws Exception{
		EdcLctreVO rtnVo = (EdcLctreVO) edcLctreDAO.selectEdcLctreStep01( edcLctreVO );

		return rtnVo;
	}

	/**
	 * 강좌 정보 조회 ( 2. 강좌정보 및 상태정보)
	 */
	public EdcLctreVO selectEdcLctreStep02(HttpServletRequest request, EdcLctreVO edcLctreVO)  throws Exception{
		EdcLctreVO rtnVo = (EdcLctreVO) edcLctreDAO.selectEdcLctreStep02( edcLctreVO );

		return rtnVo;
	}

	/**
	 * 강좌 정보 조회 ( 3. 일정정보)
	 */
	public EdcLctreVO selectEdcLctreStep03(HttpServletRequest request, EdcLctreVO edcLctreVO)  throws Exception{
		EdcLctreVO rtnVo = (EdcLctreVO) edcLctreDAO.selectEdcLctreStep03( edcLctreVO );

		return rtnVo;
	}


	/**
	 * 교육강좌 분류정보 및 강좌정보 을(를) 등록한다.
	 * @param EdcLctreVO
	 * @return int 교육강좌 분류정보 및 강좌정보의 시퀀스
	 * @throws Exception
	 */
	@Override
	public int insertEdcLctre(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {

		String userId = StaffLoginUtil.getLoginId(request.getSession());
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmmss");
		String clientIp = StringUtil.getClientIpAddr(request);

		edcLctreVO.setFrstRegisterId(userId);
		edcLctreVO.setFrstRegisterPnttm(nowDate);
		edcLctreVO.setFrstRegisterIp(clientIp);

		edcLctreVO.setLastUpdusrId(userId);
		edcLctreVO.setLastUpdusrPnttm(nowDate);
		edcLctreVO.setLastUpdusrIp(clientIp);
		edcLctreVO.setDeleteAt("N");

		// 모집방법
		int cnt = edcLctreVO.getRcritMthArr().length;
		String strRcritMth = "";
		for(int i=0;i<cnt;i++){
			strRcritMth += edcLctreVO.getRcritMthArr()[i] + "|";
		}
		if(strRcritMth.length() > 0){
			strRcritMth = strRcritMth.substring(0, strRcritMth.length()-1);
			edcLctreVO.setRcritMth(strRcritMth);
		}

		return (Integer) edcLctreDAO.insertEdcLctre(edcLctreVO);
	}

	/**
	 * 교육강좌 분류정보 및 강좌정보 을(를) 등록한다.
	 * @param EdcLctreVO
	 * @return int 교육강좌 분류정보 및 강좌정보의 시퀀스
	 * @throws Exception
	 */
	@Override
	public int insertEdcLctreStep01(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		String userId = StaffLoginUtil.getLoginId(request.getSession());
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmmss");
		String clientIp = StringUtil.getClientIpAddr(request);

		edcLctreVO.setFrstRegisterId(userId);
		edcLctreVO.setFrstRegisterPnttm(nowDate);
		edcLctreVO.setFrstRegisterIp(clientIp);

		edcLctreVO.setLastUpdusrId(userId);
		edcLctreVO.setLastUpdusrPnttm(nowDate);
		edcLctreVO.setLastUpdusrIp(clientIp);
		edcLctreVO.setDeleteAt("N");
		edcLctreVO.setUseAt("N");
		edcLctreVO.setCreatStepCode(1);

		int cnt = edcLctreVO.getRcritMthArr().length;
		String strRcritMth = "";
		for(int i=0;i<cnt;i++){
			strRcritMth += edcLctreVO.getRcritMthArr()[i] + "|";
		}
		if(strRcritMth.length() > 0){
			strRcritMth = strRcritMth.substring(0, strRcritMth.length()-1);
			edcLctreVO.setRcritMth(strRcritMth);
		}
		//edcLctreVO.setSetlePd(edcLctreVO.getSetlePd().replace("-", "")+edcLctreVO.getSetlePdHH()+edcLctreVO.getSetlePdMM());

		return (Integer) edcLctreDAO.insertEdcLctreStep01(edcLctreVO);
	}

	/**
	 * 강좌 저장 1단계(업데이트)
	 */
	@Override
	public int updateEdcLctreStep01(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));
		edcLctreVO.setDeleteAt("N");

		/** 강좌 생성 단계 처리  **/
		int stepCnt = edcLctreVO.getCreatStepCode();
		if(stepCnt <= 2){
			edcLctreVO.setCreatStepCode(2);
		}

		// 모집방법
		int cnt = edcLctreVO.getRcritMthArr().length;
		String strRcritMth = "";
		for(int i=0;i<cnt;i++){
			strRcritMth += edcLctreVO.getRcritMthArr()[i] + "|";
		}
		if(strRcritMth.length() > 0){
			strRcritMth = strRcritMth.substring(0, strRcritMth.length()-1);
			edcLctreVO.setRcritMth(strRcritMth);
		}
		//edcLctreVO.setSetlePd(edcLctreVO.getSetlePd().replace("-", "")+edcLctreVO.getSetlePdHH()+"00");

		return (Integer) edcLctreDAO.updateEdcLctreStep01(edcLctreVO);
	}

	/**
	 * 강좌 저장 2단계
	 */
	@Override
	public int updateEdcLctreStep02(HttpServletRequest request
							, EdcLctreVO edcLctreVO
							, ResveAddFieldBndl resveAddFieldBndl
							, ResveOptionBndl resveOptionBndl
							) throws Exception {
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));
		edcLctreVO.setDeleteAt("N");

		/** 강좌 생성 단계 처리  **/
		int stepCnt = edcLctreVO.getCreatStepCode();
		if(stepCnt <= 2){
			edcLctreVO.setCreatStepCode(2);
		}

		// 교육/강좌 대상, 할인 대상및 요율 삭제
		ResveOptionVO resveOptionVO = new ResveOptionVO();
		resveOptionVO.setSearchMngrKey(edcLctreVO.getSearchLctreKey());
		resveOptionVO.setSearchJobSe("L");
		resveOptionService.deleteResveOption(resveOptionVO);

		// 교육/강좌 대상, 할인 대상및 요율 저장
		resveOptionBndl.setMngrKey(StringUtil.strToInt(edcLctreVO.getSearchLctreKey()));
		resveOptionBndl.setInsttCode(edcLctreVO.getInsttCode());
		resveOptionBndl.setJobSe("L");
		resveOptionService.insertResveOptionAll(resveOptionBndl, request);

		// 프로그램 신청양식 추가 등록
		if(resveAddFieldBndl.getBndlItemSeq() != null){
			int addFieldCnt = resveAddFieldBndl.getBndlItemSeq().length;

			ResveAddFieldVO addFieldVO;
			if(addFieldCnt > 0){
				String[] essntlAtArr = resveAddFieldBndl.getEssntlAt().split("\\,");

				addFieldVO = new ResveAddFieldVO();
				addFieldVO.setManagerSeq(StringUtil.strToInt(edcLctreVO.getSearchLctreKey()));
				addFieldVO.setJobSe("EC");
				addFieldVO.setDeleteAt("N");

				for(int i=0;i < addFieldCnt;i++){
					addFieldVO.setFieldNm(resveAddFieldBndl.getBndlFieldNm()[i]);
					addFieldVO.setFieldTy(resveAddFieldBndl.getBndlFieldTy()[i]);
					addFieldVO.setFieldOption(resveAddFieldBndl.getBndlFieldOption()[i]);
					addFieldVO.setEssntlAt(essntlAtArr[i]);
					addFieldVO.setOrdrNo(StringUtil.intToStr(i+1));

					if("".equals(StringUtil.strTrim(resveAddFieldBndl.getBndlItemSeq()[i]))){
						resveAddFieldService.insertResveAddField(request, addFieldVO);
					}else{
						addFieldVO.setSearchItemSeq(resveAddFieldBndl.getBndlItemSeq()[i]);
						resveAddFieldService.updateResveAddField(request, addFieldVO);
					}
				}
			}
		}

		String edcTargetEtc = "";
		if(resveOptionBndl.getBndlJobSe() != null){
			int arrInt = resveOptionBndl.getBndlJobSe().length;
			for(int i=0;i<arrInt;i++){
				if("Y".equals(resveOptionBndl.getBndlCheckYn()[i]) && "T".equals(resveOptionBndl.getBndlTrgetCode()[i])){
					edcTargetEtc = edcTargetEtc + resveOptionBndl.getBndlItemNm()[i] + ",";
				}
			}
		}

		if(resveOptionBndl.getBndlItemNmT() != null){
			for(int i=0;i<resveOptionBndl.getBndlItemNmT().length;i++){
				edcTargetEtc = edcTargetEtc + resveOptionBndl.getBndlItemNmT()[i] + ",";
			}
		}

		if(edcTargetEtc.length() > 0){
			edcTargetEtc = StringUtil.strSubString(edcTargetEtc, 0, edcTargetEtc.length()-1);
		}

		edcLctreVO.setEdcTargetEtc(edcTargetEtc);

		// 결제 방법
		if(edcLctreVO.getMetpayArr() != null){
			int cnt = edcLctreVO.getMetpayArr().length;
			String metpay = "";
			for(int i=0;i<cnt;i++){
				metpay += edcLctreVO.getMetpayArr()[i] + "|";
			}
			if(metpay.length() > 0){
				metpay = metpay.substring(0, metpay.length()-1);
				edcLctreVO.setMetpay(metpay);
			}
		}

		// 개인정보 보유기간
		edcLctreVO.setIndvdlinfoHoldPd(StringUtil.strReplaceALL(edcLctreVO.getIndvdlinfoHoldPd(), "-", ""));

		//강좌소개 문자열치환
		//edcLctreVO.setLctreIntrcn(StringUtil.htmlDext5Char(edcLctreVO.getLctreIntrcn()));

		return (Integer) edcLctreDAO.updateEdcLctreStep02(edcLctreVO);
	}

	/**
	 * 강좌 저장 3단계
	 */
	@Override
	public int updateEdcLctreStep03(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		/** 강좌 생성 단계 처리  **/
		int stepCnt = edcLctreVO.getCreatStepCode();
		if(stepCnt <= 3){
			edcLctreVO.setCreatStepCode(3);
		}

		if(!"".equals(StringUtil.strTrim(edcLctreVO.getOpenDay()))){
			String openDay = StringUtil.strReplaceALL(edcLctreVO.getOpenDay(), "-", "");

			edcLctreVO.setOpenYear(StringUtil.strSubString(openDay, 0, 4));
			edcLctreVO.setOpenQu(DateUtil.getDateQuarter(StringUtil.strSubString(openDay, 4, 6)));
			edcLctreVO.setOpenMonth(StringUtil.strSubString(openDay, 4, 6));
			edcLctreVO.setOpenDay(StringUtil.strSubString(openDay, 6, 8));
		}


		if(!"".equals(StringUtil.strTrim(edcLctreVO.getPriorRceptBgndeDay())))
				edcLctreVO.setPriorRceptBgnde(StringUtil.strReplaceALL(edcLctreVO.getPriorRceptBgndeDay(), "-", "")+edcLctreVO.getPriorRceptBgndeTime()+edcLctreVO.getPriorRceptBgndeMinute());

		if(!"".equals(StringUtil.strTrim(edcLctreVO.getPriorRceptEnddeDay())))
				edcLctreVO.setPriorRceptEndde(StringUtil.strReplaceALL(edcLctreVO.getPriorRceptEnddeDay(), "-", "")+edcLctreVO.getPriorRceptEnddeTime()+edcLctreVO.getPriorRceptEnddeMinute());

		if(!"".equals(StringUtil.strTrim(edcLctreVO.getRceptBgndeDD())))
				edcLctreVO.setRceptBgnde(StringUtil.strReplaceALL(edcLctreVO.getRceptBgndeDD(), "-", "")+edcLctreVO.getRceptBgndeHH()+edcLctreVO.getRceptBgndeMM());

		if(!"".equals(StringUtil.strTrim(edcLctreVO.getRceptEnddeDD())))
				edcLctreVO.setRceptEndde(StringUtil.strReplaceALL(edcLctreVO.getRceptEnddeDD(), "-", "")+edcLctreVO.getRceptEnddeHH()+edcLctreVO.getRceptEnddeMM());

		/** 추가 접수 기간 설정 **/
		if(!"".equals(StringUtil.strTrim(edcLctreVO.getAditRceptBgnde())))
				edcLctreVO.setAditRceptBgnde(StringUtil.strReplaceALL(edcLctreVO.getAditRceptBgnde(), "-", "")+edcLctreVO.getAditRceptBgndeTime()+edcLctreVO.getAditRceptBgndeMinute());

		if(!"".equals(StringUtil.strTrim(edcLctreVO.getAditRceptEndde())))
				edcLctreVO.setAditRceptEndde(StringUtil.strReplaceALL(edcLctreVO.getAditRceptEndde(), "-", "")+edcLctreVO.getAditRceptEnddeTime()+edcLctreVO.getAditRceptEnddeMinute());


		/** 교육 기간 설정  **/
		if(!"".equals(StringUtil.strTrim(edcLctreVO.getEdcBgnde())))
			edcLctreVO.setEdcBgnde(StringUtil.strReplaceALL(edcLctreVO.getEdcBgnde(), "-", "")+"0000");

		if(!"".equals(StringUtil.strTrim(edcLctreVO.getEdcEndde())))
			edcLctreVO.setEdcEndde(StringUtil.strReplaceALL(edcLctreVO.getEdcEndde(), "-", "")+"0000");

		edcLctreVO.setEdcBeginTime(edcLctreVO.getEdcBeginTime()+edcLctreVO.getEdcBeginMinute());				// 교육 시작 시간 ( 0900 )
		edcLctreVO.setEdcEndTime(edcLctreVO.getEdcEndTime()+edcLctreVO.getEdcEndMinute());						// 교육 종료 시간 ( 1800 )
		/** 교육 기간 종료 **/

		if(!"".equals(StringUtil.strTrim(edcLctreVO.getDrwtDe())))
			edcLctreVO.setDrwtDe(StringUtil.strReplaceALL(edcLctreVO.getDrwtDe(), "-", "")+edcLctreVO.getDrwtDeTime()+edcLctreVO.getDrwtDeMinute());

		String edcTime = "";
		for(int i=0;i<edcLctreVO.getEdcBeginTimeArr().length;i++){
			edcTime = edcTime + edcLctreVO.getEdcBeginTimeArr()[i]+edcLctreVO.getEdcBeginMinuteArr()[i]+edcLctreVO.getEdcEndTimeArr()[i]+edcLctreVO.getEdcEndMinuteArr()[i]+"|";
		}

		if(edcTime.length() > 0){
			edcTime = StringUtil.strSubString(edcTime, 0, edcTime.length()-1);
		}

		edcLctreVO.setEdcTime(edcTime);

		return (Integer) edcLctreDAO.updateEdcLctreStep03(edcLctreVO);
	}

	/**
	 * 강좌 저장 4단계
	 */
	@Override
	public int updateEdcLctreStep04(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		/** 강좌 생성 단계 처리  **/
		int stepCnt = edcLctreVO.getCreatStepCode();
		if(stepCnt <= 4){
			edcLctreVO.setCreatStepCode(4);
		}

		return (Integer) edcLctreDAO.updateEdcLctreStep04(edcLctreVO);
	}

	/**
	 * 강좌 저장 5단계
	 */
	@Override
	public int updateEdcLctreStep05(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		/** 강좌 생성 단계 처리  **/
		int stepCnt = edcLctreVO.getCreatStepCode();
		if(stepCnt <= 4){
			edcLctreVO.setCreatStepCode(5);
		}

		return (Integer) edcLctreDAO.updateEdcLctreStep05(edcLctreVO);
	}

	/**
	 * 강좌 폐강 처리
	 * @param request
	 * @param edcLctreVO
	 * @return
	 * @throws Exception
	 */
	@Override
	public int updateEdcLctreCancelYn(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		int rtn = 0;

		edcLctreVO.setCancelYn("Y");
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		rtn = edcLctreDAO.updateEdcLctreCancelYn(edcLctreVO);

		if(rtn > 0){
			// 상태값 변경 이력 저장
//		 	ResveStatusHistoryVO resveStatusHistoryVO = new ResveStatusHistoryVO();
//		 	resveStatusHistoryVO.setManagerSeq(StringUtil.strToInt(edcLctreVO.getSearchLctreKey()));
//			resveStatusHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
//			resveStatusHistoryVO.setSttusCode(edcLctreVO.getCancelYn());
//			resveStatusHistoryVO.setDivCode("L01");
//			resveStatusHistoryVO.setUserId(StaffLoginUtil.getLoginId(request.getSession()));
//			resveStatusHistoryVO.setSttusNm("강좌 폐강처리");
//			resveStatusHistoryVO.setSttusIp(StringUtil.getClientIpAddr(request));
//
//			resveStatusHistoryService.insertResveStatusHistory(request, resveStatusHistoryVO);

			// 상태값 변경 이력 저장
			StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
			staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
			staffActionHistoryVO.setManagerSeq(edcLctreVO.getSearchLctreKey());
			staffActionHistoryVO.setSttusCode("Y");
			staffActionHistoryVO.setDivCode("L01");
			staffActionHistoryVO.setUserId(StaffLoginUtil.getLoginId(request.getSession()));
			staffActionHistoryVO.setSttusNm("강좌 폐강처리");
			staffActionHistoryVO.setProgrmId("EL01");
			staffActionHistoryVO.setUserNm(StaffLoginUtil.getLoginNm(request.getSession()));

			staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);
		}

		return rtn;
	}

	/**
	 * 강좌 상태값 변경이력 저장
	 */
	@Override
	public int updateEdcLctreSttus(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		int rtn = 0;

		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		rtn = edcLctreDAO.updateEdcLctreSttus(edcLctreVO);

		if(rtn > 0){
			// 상태값 변경 이력 저장
//			ResveStatusHistoryVO resveStatusHistoryVO = new ResveStatusHistoryVO();
//			resveStatusHistoryVO.setManagerSeq(StringUtil.strToInt(edcLctreVO.getSearchLctreKey()));
//			resveStatusHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
//			resveStatusHistoryVO.setSttusCode(edcLctreVO.getRceptSttus());
//			resveStatusHistoryVO.setDivCode("L01");
//			resveStatusHistoryVO.setUserId(StaffLoginUtil.getLoginId(request.getSession()));
//			resveStatusHistoryVO.setSttusNm(edcLctreVO.getRceptSttusNm());
//			resveStatusHistoryVO.setSttusIp(StringUtil.getClientIpAddr(request));
//
//			resveStatusHistoryService.insertResveStatusHistory(request, resveStatusHistoryVO);

			// 상태값 변경 이력 저장
			StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
			staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
			staffActionHistoryVO.setManagerSeq(edcLctreVO.getSearchLctreKey());
			staffActionHistoryVO.setSttusCode(edcLctreVO.getRceptSttus());
			staffActionHistoryVO.setDivCode("L01");
			staffActionHistoryVO.setUserId(StaffLoginUtil.getLoginId(request.getSession()));
			staffActionHistoryVO.setSttusNm("강좌 상태값 변경이력 저장");
			staffActionHistoryVO.setProgrmId("EL01");
			staffActionHistoryVO.setUserNm(StaffLoginUtil.getLoginNm(request.getSession()));

			staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);
		}

		return rtn;
	}

	/**
	 * 사용유무 업데이트
	 */
	public int updateEdcLctreUseAt(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception{
		int rtn = 0;

		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		rtn = edcLctreDAO.updateEdcLctreUseAt(edcLctreVO);

		if(rtn > 0){
			// 상태값 변경 이력 저장
			StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
			staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
			staffActionHistoryVO.setManagerSeq(edcLctreVO.getSearchLctreKey());
			staffActionHistoryVO.setSttusCode(edcLctreVO.getUseAt());
			staffActionHistoryVO.setDivCode("L01");
			staffActionHistoryVO.setUserId(StaffLoginUtil.getLoginId(request.getSession()));
			staffActionHistoryVO.setSttusNm("강좌 상태값(사용유무) 변경 처리");
			staffActionHistoryVO.setProgrmId("EL01");
			staffActionHistoryVO.setUserNm(StaffLoginUtil.getLoginNm(request.getSession()));

			staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);
		}

		return rtn;
	}

  	/**
	 * 교육강좌 분류정보 및 강좌정보 을(를) 수정한다.
	 * @param EdcLctreVO
	 * @return int 교육강좌 분류정보 및 강좌정보의 시퀀스
	 * @throws Exception
	 */
	@Override
	public int updateEdcLctre(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		return (Integer) edcLctreDAO.updateEdcLctre(edcLctreVO);
	}

  	/**
	 * 교육강좌 분류정보 및 강좌정보 파일을 업로드 한다. (별도 객체생성 권장)
	 * @param EdcLctreVO
	 * @throws Exception
	 */
	@Override
  	public void uploadEdcLctre(MultipartHttpServletRequest multiPartRequest, HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {

	/*
		for( int i=0; i<3; i++ ) {
			String fileProcTy = request.getParameter("fileProcTy" + i);
			if( !StringUtil.isEmpty(fileProcTy) ) {
				if( !"U".equals(fileProcTy) ) {
					EdcLctreAtchmnfl edcLctreAtchmnfl = new EdcLctreAtchmnfl();
					edcLctreAtchmnfl.setAtchmnflNo(Integer.valueOf(fileProcTy));
					edcLctreAtchmnflService.deleteEdcLctreAtchmnfl(edcLctreAtchmnfl);
				}
			}
		}

		String frstRegisterPnttm = DateUtil.getNowDateTime("yyyyMMddHHmmss");  // 등록일
		String frstRegisterId = StaffLoginUtil.getLoginId(request.getSession());

		String UPLOAD_STORE_PATH = "/DATA/edcLctre/";
		String THUMB_STORE_PATH = UPLOAD_STORE_PATH + "/thumb";
		String edcLctreDir = fileMngUtil.realPath(request, UPLOAD_STORE_PATH);
		String edcLctreThumnDir = fileMngUtil.realPath(request, THUMB_STORE_PATH);
		fileMngUtil.mkdir(edcLctreDir);
		fileMngUtil.mkdir(edcLctreThumnDir);

		String[] arrFileType = fileMngUtil.FILE_TYPE_COMMON_ALL;
		long maxFileSize = 430 * 1024 * 1024;

		List<FileVO> fileVOList = fileMngUtil.parseFileInfMulti(multiPartRequest.getFiles("atchmnfl"), edcLctreDir, maxFileSize, arrFileType, null, "", null);

		for( int i=0; i<fileVOList.size(); i++ ) {
			EdcLctreAtchmnfl edcLctreAtchmnfl = new EdcLctreAtchmnfl();
			edcLctreAtchmnfl.setShnNo(edcLctreVO.getShnNo());
			edcLctreAtchmnfl.setStorePath(UPLOAD_STORE_PATH);
			edcLctreAtchmnfl.setFileNm(fileVOList.get(i).getOrignlFileNm());
			edcLctreAtchmnfl.setStoreFileNm(fileVOList.get(i).getStreFileNm());
			edcLctreAtchmnfl.setReplcText(edcLctreVO.getShnNm());
			edcLctreAtchmnfl.setFrstRegisterPnttm(frstRegisterPnttm);
			edcLctreAtchmnfl.setFrstRegisterId(frstRegisterId);
			edcLctreAtchmnfl.setFileExtsn(fileVOList.get(i).getFileExtsn());
			edcLctreAtchmnfl.setBdtInsrtAt("N");
			edcLctreAtchmnflService.insertEdcLctreAtchmnfl(edcLctreAtchmnfl);

			if( "jpg".equals(edcLctreAtchmnfl.getFileExtsn()) ||
				"jpeg".equals(edcLctreAtchmnfl.getFileExtsn()) ||
				"png".equals(edcLctreAtchmnfl.getFileExtsn()) ||
				"png".equals(edcLctreAtchmnfl.getFileExtsn())) {
				ImageUtil.createThumb(
					fileMngUtil.realPath(request, UPLOAD_STORE_PATH + "/" + edcLctreAtchmnfl.getStoreFileNm()),
					fileMngUtil.realPath(request, THUMB_STORE_PATH + "/t_" + edcLctreAtchmnfl.getStoreFileNm()),
					430, 618, "FIX");
			}
		}
	*/
	}

  	/**
	 * 교육강좌 분류정보 및 강좌정보 의 유효성을 검증한다.
	 * @param EdcLctreVO
	 * @return ResponseJSON 결과코드, 메세지등을 제공
	 * @throws Exception
	 */
  	@Override
	public ResponseJSON validateEdcLctre(HttpServletRequest request,  EdcLctreVO edcLctreVO) throws Exception {
		ResponseJSON result = new ResponseJSON();
		result.setMsg("TEST");
		result.setResult(0);

		return result;
	}

	@Override
	public void deleteEdcLctre(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		edcLctreVO.setDeleteAt("Y");
		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		edcLctreDAO.deleteEdcLctre(edcLctreVO);
	}

  	/**
	 * 교육강좌 분류정보 및 강좌정보 을(를) 일괄등록 및 수정한다.
	 * @param EdcLctreVO
	 * @throws Exception
	 */
	@Override
	public void batchEdcLctre(HttpServletRequest request,  EdcLctreVO edcLctreVO) throws Exception {

	}

  	/**
	 * 교육강좌 분류정보 및 강좌정보 을(를) 전체 조회한다.
	 * @param EdcLctreVO
     * @return EdcLctre 교육강좌 분류정보 및 강좌정보 의 전체결과
	 * @throws Exception
	 */
	@Override
	public List<EdcLctre> selectEdcLctreListAll(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
			List<EdcLctre> list = edcLctreDAO.selectEdcLctreListAll( edcLctreVO );

			// 코드값 HashMap 생성
			HashMap<String, String> codeHashMap = new HashMap<String, String>();
			CodeCmmVO cmmVo = new CodeCmmVO();

			cmmVo.setSearchCodeType("'TCH_LEC_DIV1', 'USE_AT', 'RCEPT_STTUS', 'LEC_TYPE', 'TCH_LEC_DIV1','LEC_CATGY' ");
			cmmVo.setSearchUseAt("Y");
			cmmVo.setSearchDeleteAt("N");
			codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

			/** 기관 목록 조회 **/
			List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, cmmVo);

			// 기관 목록 리스트 HashMap 생성
			HashMap<String, String> insttMap = new HashMap<String, String>();
			for(CodeCmmVO forVo : insttList){
				insttMap.put(forVo.getCodeValue(), forVo.getCodeNm());
			}

			for(EdcLctre forVo : list){
				forVo.setUseAt(codeHashMap.get("USE_AT"+forVo.getUseAt()));
				forVo.setRceptSttus(codeHashMap.get("RCEPT_STTUS"+forVo.getRceptSttus()));
				forVo.setLctreLclas(codeHashMap.get("TCH_LEC_DIV1"+forVo.getLctreLclas()));
				forVo.setEdcMth(codeHashMap.get("LEC_TYPE"+forVo.getEdcMth()));
				forVo.setLctreCtgryNm(codeHashMap.get("LEC_CATGY"+forVo.getLctreCtgry()));

				if(forVo.getAtnclCt() == 0){
					forVo.setAtnclCtStr("무료");
				}else{
					forVo.setAtnclCtStr(StringUtil.getFormatComma(forVo.getAtnclCt()));
				}

				// 기관 한글명
				forVo.setInsttNm(insttMap.get(forVo.getInsttCode()+""));

				forVo.setPriorRceptBgnde(DateUtil.getDateFormat(forVo.getPriorRceptBgnde(), "yyyy.mm.dd"));
				forVo.setPriorRceptEndde(DateUtil.getDateFormat(forVo.getPriorRceptEndde(), "yyyy.mm.dd"));
				forVo.setRceptBgnde(DateUtil.getDateFormat(forVo.getRceptBgnde(), "yyyy-mm-dd hh:mi"));
				forVo.setRceptEndde(DateUtil.getDateFormat(forVo.getRceptEndde(), "yyyy-mm-dd hh:mi"));
				forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy.mm.dd"));
				forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy.mm.dd"));
			}

			return list;
	}


	/**
	 * 강좌 강의 계획서 관리 상세정보를 조회한다.
	 * @param EdcLctreActplnVO
	 * @return EdcLctreActpln 강좌 강의 계획서 관리 의 상세정보
	 * @throws Exception
	*/
	@Override
	public List<EdcLctreActpln> selectEdcLctreActplnData(HttpServletRequest request, EdcLctreActpln edcLctreActpln) throws Exception{
		List<EdcLctreActpln> list = new ArrayList<EdcLctreActpln>();
		list = edcLctreDAO.selectEdcLctreActplnData(edcLctreActpln);

		for(EdcLctreActpln forVo : list){
			forVo.setLctreDe(DateUtil.getDateFormat(forVo.getLctreDe(), "yyyy-mm-dd"));

			if(!"staff".equals(edcLctreActpln.getSiteId())){
				forVo.setLctreCn(StringUtil.newLineReplace(forVo.getLctreCn()));
			}
		}

		return list;
	}

	/**
	 * 강좌 강의 계획서 관리 을(를) 등록한다.
	 * @param EdcLctreActplnVO
	 * @return int 강좌 강의 계획서 관리의 시퀀스
	 * @throws Exception
	 */
	@Override
	public int insertEdcLctreActpln(HttpServletRequest request, EdcLctreActpln edcLctreActpln) throws Exception {
			edcLctreActpln.setUseAt("Y");
			edcLctreActpln.setDeleteAt("N");
			edcLctreActpln.setLctreDe(StringUtil.strReplaceALL(edcLctreActpln.getLctreDe(), "-", ""));

			edcLctreActpln.setFrstRegisterId(StaffLoginUtil.getLoginId(request.getSession()));
			edcLctreActpln.setFrstRegisterPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
			edcLctreActpln.setFrstRegisterIp(StringUtil.getClientIpAddr(request));

			edcLctreActpln.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
			edcLctreActpln.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
			edcLctreActpln.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

			return (Integer) edcLctreDAO.insertEdcLctreActpln(edcLctreActpln);
	}

	/**
	 * 강좌 강의 계획서 관리 을(를) 수정한다.
	 * @param EdcLctreActplnVO
	 * @return int 강좌 강의 계획서 관리의 시퀀스
	 * @throws Exception
	 */
	@Override
	public int updateEdcLctreActpln(HttpServletRequest request, EdcLctreActpln edcLctreActpln) throws Exception {
			edcLctreActpln.setLctreDe(StringUtil.strReplaceALL(edcLctreActpln.getLctreDe(), "-", ""));
			edcLctreActpln.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
			edcLctreActpln.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
			edcLctreActpln.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

			return (Integer) edcLctreDAO.updateEdcLctreActpln(edcLctreActpln);
	}

	/**
	 * 강좌 강의 계획서 삭제 처리
	 */
	@Override
	public void deleteEdcLctreActpln(HttpServletRequest request, EdcLctreActpln edcLctreActpln) throws Exception {
		edcLctreActpln.setDeleteAt("Y");
		edcLctreActpln.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreActpln.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreActpln.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		edcLctreDAO.deleteEdcLctreActpln(edcLctreActpln);
	}

	@Override
	public List<EdcLctre> selectEdcLctreListForMain(int pageUnit) throws Exception {
		return edcLctreDAO.selectEdcLctreListForMain(pageUnit);
	}

	@Override
	public List<EdcLctre> edcLctreListGroupByDstrct(int pageUnit) throws Exception {
		return edcLctreDAO.edcLctreListGroupByDstrct(pageUnit);
	}

	/**
	 * 사용자 화면 > 강좌 상세보기용
	 */
	public EdcLctre webSelectEdcLctreData(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		EdcLctre edcLctre = edcLctreDAO.webSelectEdcLctreData(edcLctreVO);
		edcLctre.setLctreIntrcn(StringUtil.newLineReplace(edcLctre.getLctreIntrcn()));

		return edcLctre;
	}

	/**
	 * 사용자 화면 > 강좌 상세보기용 (수강생인원 추가)
	 */
	public EdcLctreVO webSelectEdcLctreDetail(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		EdcLctreVO edcLctre = edcLctreDAO.webSelectEdcLctreDetail(edcLctreVO);

		if(edcLctre != null){
			System.out.println("0000000000000000000000000");
			System.out.println(edcLctre.getRceptSttusAutoAt());
			if(!"Y".equals(edcLctre.getCancelYn()) && "Y".equals(edcLctre.getRceptSttusAutoAt()) ){
				edcLctreVO = checkSttus(edcLctre);
			}

			edcLctre.setPriorRceptBgndeAll(edcLctre.getPriorRceptBgnde());
			edcLctre.setPriorRceptEnddeAll(edcLctre.getPriorRceptEndde());

			edcLctre.setRefndNm(StringUtil.newLineReplace(edcLctre.getRefndNm()));

			// 각 일정 요일 한글명 설정
			edcLctre.setEdcBgndeDay(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getEdcBgnde(), 0, 8), "yyyyMMdd"));				// 교육 시작 요일
			edcLctre.setEdcEnddeDay(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getEdcEndde(), 0, 8), "yyyyMMdd"));				// 교육 종료 요일
			edcLctre.setRceptBgndeDay(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getRceptBgnde(), 0, 8), "yyyyMMdd"));		// 교육 시작 요일
			edcLctre.setRceptEnddeDay(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getRceptEndde(), 0, 8), "yyyyMMdd"));			// 교육 종료 요일

			// 교육일정
			edcLctre.setEdcBgnde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getEdcBgnde(), 0, 8), "yyyy-mm-dd") );
			edcLctre.setEdcEndde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getEdcEndde(), 0, 8), "yyyy-mm-dd") );

			// 교육 시간 설정
			edcLctre.setEdcBeginMinute(StringUtil.strSubString(edcLctre.getEdcBeginTime(), 2, 4));
			edcLctre.setEdcBeginTime(StringUtil.strSubString(edcLctre.getEdcBeginTime(), 0, 2));
			edcLctre.setEdcEndMinute(StringUtil.strSubString(edcLctre.getEdcEndTime(), 2, 4));
			edcLctre.setEdcEndTime(StringUtil.strSubString(edcLctre.getEdcEndTime(), 0, 2));

			// 신청 시간 설정
			edcLctre.setRceptBgndeHH(StringUtil.strSubString(edcLctre.getRceptBgnde(), 8, 10));
			edcLctre.setRceptBgndeMM(StringUtil.strSubString(edcLctre.getRceptBgnde(), 10, 12));

			edcLctre.setRceptEnddeHH(StringUtil.strSubString(edcLctre.getRceptEndde(), 8, 10));
			edcLctre.setRceptEnddeMM(StringUtil.strSubString(edcLctre.getRceptEndde(), 10, 12));

			// 개설일
			edcLctre.setOpenDay(edcLctre.getOpenYear()+"-"+edcLctre.getOpenMonth()+"-"+edcLctre.getOpenDay());

			// 교육 요일 시간 변환
			edcLctre.setEdcDayTime((DateUtil.replaceDayTime(edcLctre.getEdcDay(), edcLctre.getEdcTime(), ",", ",")));

			// 교육 요일 변환
			edcLctre.setEdcDay(DateUtil.replaceDay(edcLctre.getEdcDay(), ",", ","));

			// 결제 마감일  20181110 1600
			edcLctre.setSetlePdHH(StringUtil.strSubString(edcLctre.getSetlePd(), 8, 10) );
			edcLctre.setSetlePdMM(StringUtil.strSubString(edcLctre.getSetlePd(), 10, 12) );
			edcLctre.setSetlePd(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getSetlePd(), 0, 8), "yyyy-mm-dd") );

			// 추첨일시
			if(!"".equals(StringUtil.strTrim(edcLctre.getDrwtDe()))){
				edcLctre.setDrwtDeTime(StringUtil.strSubString(edcLctre.getDrwtDe(), 8, 10) );
				edcLctre.setDrwtDeMinute(StringUtil.strSubString(edcLctre.getDrwtDe(), 10, 12) );
				edcLctre.setDrwtDe(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getDrwtDe(), 0, 8), "yyyy-mm-dd") );
			}

			// 우선접수 시작일, 시간
			edcLctre.setPriorRceptBgndeD(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getPriorRceptBgnde(), 0, 8), "yyyyMMdd"));	// 우선접수 요일
			edcLctre.setPriorRceptBgndeTime(StringUtil.strSubString(edcLctre.getPriorRceptBgnde(), 8, 10));
			edcLctre.setPriorRceptBgndeMinute(StringUtil.strSubString(edcLctre.getPriorRceptBgnde(), 10, 12));
			edcLctre.setPriorRceptBgnde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getPriorRceptBgnde(), 0, 8), "yyyy-mm-dd"));

			// 우선접수 종료일 시간
			edcLctre.setPriorRceptEnddeD(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getPriorRceptEndde(), 0, 8), "yyyyMMdd"));	// 우선접수 요일
			edcLctre.setPriorRceptEnddeTime(StringUtil.strSubString(edcLctre.getPriorRceptEndde(), 8, 10));
			edcLctre.setPriorRceptEnddeMinute(StringUtil.strSubString(edcLctre.getPriorRceptEndde(), 10, 12));
			edcLctre.setPriorRceptEndde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getPriorRceptEndde(), 0, 8), "yyyy-mm-dd"));


			int sumConfirm = edcLctre.getSttus00() + edcLctre.getSttus01() + edcLctre.getSttus09();
			int sumWait = edcLctre.getSttus02() + edcLctre.getSttus10();

			edcLctre.setSumConfirm(sumConfirm);
			edcLctre.setSumWait(sumWait);
		}

		return edcLctre;
	}


	/**
	 * 사용자 화면 > 강좌 상세보기용 (수강생인원 추가)
	 */
	public EdcLctreVO webSelectEdcLctreDetail(EdcLctreVO edcLctreVO) throws Exception {
		EdcLctreVO edcLctre = edcLctreDAO.webSelectEdcLctreDetail(edcLctreVO);

		if(edcLctre != null){
			edcLctre.setLctreIntrcn(StringUtil.newLineReplace(edcLctre.getLctreIntrcn()));

			// 교육일정
			edcLctre.setEdcBgnde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getEdcBgnde(), 0, 8), "yyyy-mm-dd") );
			edcLctre.setEdcEndde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getEdcEndde(), 0, 8), "yyyy-mm-dd") );

			// 교육 시간 설정
			edcLctre.setEdcBeginMinute(StringUtil.strSubString(edcLctre.getEdcBeginTime(), 2, 4));
			edcLctre.setEdcBeginTime(StringUtil.strSubString(edcLctre.getEdcBeginTime(), 0, 2));
			edcLctre.setEdcEndMinute(StringUtil.strSubString(edcLctre.getEdcEndTime(), 2, 4));
			edcLctre.setEdcEndTime(StringUtil.strSubString(edcLctre.getEdcEndTime(), 0, 2));

			// 개설일
			edcLctre.setOpenDay(edcLctre.getOpenYear()+"-"+edcLctre.getOpenMonth()+"-"+edcLctre.getOpenDay());

			// 결제 마감일  20181110 1600
			edcLctre.setSetlePdHH(StringUtil.strSubString(edcLctre.getSetlePd(), 8, 10) );
			edcLctre.setSetlePdMM(StringUtil.strSubString(edcLctre.getSetlePd(), 10, 12) );
			edcLctre.setSetlePd(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getSetlePd(), 0, 8), "yyyy-mm-dd") );

			// 추첨일시
			if(!"".equals(StringUtil.strTrim(edcLctre.getDrwtDe()))){
				edcLctre.setDrwtDeTime(StringUtil.strSubString(edcLctre.getDrwtDe(), 8, 10) );
				edcLctre.setDrwtDeMinute(StringUtil.strSubString(edcLctre.getDrwtDe(), 10, 12) );
				edcLctre.setDrwtDe(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getDrwtDe(), 0, 8), "yyyy-mm-dd") );
			}

			int sumConfirm = edcLctre.getSttus00() + edcLctre.getSttus01() + edcLctre.getSttus09();
			int sumWait = edcLctre.getSttus02() + edcLctre.getSttus10();

			edcLctre.setSumConfirm(sumConfirm);
			edcLctre.setSumWait(sumWait);
		}

		return edcLctre;
	}


	/**
	 * 사용자 화면 > 강좌 상세보기용 (전체 수강생인원 추가)
	 */
	public EdcLctreVO selectEdcLctreDetail(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		EdcLctreVO edcLctre = edcLctreDAO.selectEdcLctreDetail(edcLctreVO);

		if(edcLctre != null){
			
			if(!"Y".equals(edcLctre.getCancelYn()) && "Y".equals(edcLctre.getRceptSttusAutoAt()) ){
				// 정원 체크 (상태값 확인)
				edcLctre = checkSttus(edcLctre);
			}
			
			// 코드값 HashMap 생성
			HashMap<String, String> codeHashMap = new HashMap<String, String>();
			CodeCmmVO cmmVo = new CodeCmmVO();

			// 상태값, 사용유무, 강좌 상태, 결제 상태, 성별, 접수방법, 할인코드  RCRIT_MTH
			cmmVo.setSearchCodeType("'RCEPT_STTUS','LEC_REG_TYPE','LEC_APP_TYPE'");
			codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

			// 취소 가능일
			String cancelDay = StringUtil.strTrim(edcLctre.getCancelDay());

			if(!"".equals(edcLctre.getCancelDay()) && !"".equals(StringUtil.strTrim(edcLctre.getEdcBgnde()))){
				cancelDay = DateUtil.getDateAddDay(edcLctre.getEdcBgnde(), -(StringUtil.strToInt(edcLctre.getCancelDay())));
			}else{
				cancelDay = StringUtil.strSubString(edcLctre.getEdcBgnde(), 0, 8);
			}

			String nowDay = DateUtil.getNowDateTime("yyyyMMdd");

			edcLctre.setCancelStandDay(cancelDay);
			edcLctre.setNowDay(nowDay);

			edcLctre.setRceptSttusNm(codeHashMap.get("RCEPT_STTUS" + edcLctre.getRceptSttus()));					// 강좌 상태

			String rcirtMthStr = "";
			String[] rcritMth;
			if(!"".equals(StringUtil.strTrim(edcLctre.getRcritMth()))){
				rcritMth = edcLctre.getRcritMth().split("\\|");
				for(int i=0;i<rcritMth.length;i++){
					rcirtMthStr += codeHashMap.get("LEC_REG_TYPE" + rcritMth[i]) + ",";
				}
				if(rcirtMthStr.length() > 0){
					rcirtMthStr = rcirtMthStr.substring(0, rcirtMthStr.length()-1);
				}
			}

			edcLctre.setRceptMthNm(codeHashMap.get("LEC_APP_TYPE" + edcLctre.getRceptMth()));
			edcLctre.setRcritMth(rcirtMthStr);

			edcLctre.setEdcBgndeDay(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getEdcBgnde(), 0, 8), "yyyyMMdd"));		// 교육 시작 요일
			edcLctre.setEdcEnddeDay(DateUtil.getDateDay(StringUtil.strSubString(edcLctre.getEdcEndde(), 0, 8), "yyyyMMdd"));		// 교육 종료 요일

			// 교육일정
			edcLctre.setEdcBgnde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getEdcBgnde(), 0, 8), "yyyy-mm-dd") );
			edcLctre.setEdcEndde(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getEdcEndde(), 0, 8), "yyyy-mm-dd") );

			// 교육 시간 설정
			edcLctre.setEdcBeginMinute(StringUtil.strSubString(edcLctre.getEdcBeginTime(), 2, 4));
			edcLctre.setEdcBeginTime(StringUtil.strSubString(edcLctre.getEdcBeginTime(), 0, 2));
			edcLctre.setEdcEndMinute(StringUtil.strSubString(edcLctre.getEdcEndTime(), 2, 4));
			edcLctre.setEdcEndTime(StringUtil.strSubString(edcLctre.getEdcEndTime(), 0, 2));

			// 개설일
			edcLctre.setOpenDay(edcLctre.getOpenYear()+"-"+edcLctre.getOpenMonth()+"-"+edcLctre.getOpenDay());

			// 결제 마감일  20181110 1600
			edcLctre.setSetlePdHH(StringUtil.strSubString(edcLctre.getSetlePd(), 8, 10) );
			edcLctre.setSetlePdMM(StringUtil.strSubString(edcLctre.getSetlePd(), 10, 12) );
			edcLctre.setSetlePd(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getSetlePd(), 0, 8), "yyyy-mm-dd") );

			// 추첨일시
			if(!"".equals(StringUtil.strTrim(edcLctre.getDrwtDe()))){
				edcLctre.setDrwtDeTime(StringUtil.strSubString(edcLctre.getDrwtDe(), 8, 10) );
				edcLctre.setDrwtDeMinute(StringUtil.strSubString(edcLctre.getDrwtDe(), 10, 12) );
				edcLctre.setDrwtDe(DateUtil.getDateFormat(StringUtil.strSubString(edcLctre.getDrwtDe(), 0, 8), "yyyy-mm-dd") );
			}

			int sumConfirm = edcLctre.getSttus00() + edcLctre.getSttus01();
			int sumWait = edcLctre.getSttus02();
			int totalSum = sumConfirm + sumWait + edcLctre.getSttus03()  + edcLctre.getSttus04();

			edcLctre.setSumConfirm(sumConfirm);
			edcLctre.setSumWait(sumWait);
			edcLctre.setTotalSum(totalSum);

			edcLctre.setEdcDayTime((DateUtil.replaceDayTime(edcLctre.getEdcDay(), edcLctre.getEdcTime(), ",", ",")));
			edcLctre.setEdcDay(DateUtil.replaceDay(edcLctre.getEdcDay(), ",", ","));

		}

		return edcLctre;
	}

	/**
	 * 강좌목록 조회
	 */
	public List<EdcLctreVO> webSelectEdcLctreAtnlcList(HttpServletRequest request, EdcLctreVO edcLctreVO,  List<CodeCmmVO> insttList) throws Exception{

		// 기관 목록 리스트 HashMap 생성
		HashMap<String, String> insttMap = new HashMap<String, String>();
		for(CodeCmmVO forVo : insttList){
			insttMap.put(forVo.getCodeValue(), forVo.getCodeNm());
		}

		// 코드값 HashMap 생성
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		cmmVo.setSearchCodeType("'TCH_LEC_DIV1', 'USE_AT', 'RCEPT_STTUS', 'LEC_TYPE','LEC_CATGY' ");
		cmmVo.setSearchUseAt("Y");
		cmmVo.setSearchDeleteAt("N");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		List<EdcLctreVO> rtnList = edcLctreDAO.webSelectEdcLctreAtnlcList(edcLctreVO);

		// 데이터가공 구간
		for(EdcLctreVO forVo : rtnList){
			if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
				// 정원 체크 (상태값 확인)
				forVo = checkSttus(forVo);
			}

			forVo.setUseAtNm(codeHashMap.get("USE_AT"+forVo.getUseAt()));
			forVo.setRceptSttusNm(codeHashMap.get("RCEPT_STTUS"+forVo.getRceptSttus()));
			forVo.setLctreLclas(codeHashMap.get("TCH_LEC_DIV1"+forVo.getLctreLclas()));

			if(forVo.getAtnclCt() == 0){
				forVo.setAtnclCtStr("무료");
			}else{
				forVo.setAtnclCtStr(StringUtil.getFormatComma(forVo.getAtnclCt()));
			}
			
			forVo.setEdcDayTime((DateUtil.replaceDayTime(forVo.getEdcDay(), forVo.getEdcTime(), ",", ",")));
			
			// 기관 한글명
			forVo.setInsttNm(insttMap.get(forVo.getInsttCode()+""));

			forVo.setPriorRceptBgnde(DateUtil.getDateFormat(forVo.getPriorRceptBgnde(), "yyyy.mm.dd"));
			forVo.setPriorRceptEndde(DateUtil.getDateFormat(forVo.getPriorRceptEndde(), "yyyy.mm.dd"));
			
			forVo.setAditRceptBgnde(DateUtil.getDateFormat(forVo.getAditRceptBgnde(), "yyyy.mm.dd"));
			forVo.setAditRceptEndde(DateUtil.getDateFormat(forVo.getAditRceptEndde(), "yyyy.mm.dd"));

			forVo.setRceptBgndeHH(StringUtil.strSubString(forVo.getRceptBgnde(), 8, 10));
			forVo.setRceptBgndeMM(StringUtil.strSubString(forVo.getRceptBgnde(), 10, 12));

			forVo.setRceptEnddeHH(StringUtil.strSubString(forVo.getRceptEndde(), 8, 10));
			forVo.setRceptEnddeMM(StringUtil.strSubString(forVo.getRceptEndde(), 10, 12));

			forVo.setRceptBgnde(DateUtil.getDateFormat(forVo.getRceptBgnde(), "yyyy.mm.dd"));
			forVo.setRceptEndde(DateUtil.getDateFormat(forVo.getRceptEndde(), "yyyy.mm.dd"));
			forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy.mm.dd"));
			forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy.mm.dd"));

		}

		return rtnList;
	}

	/**
	 * 접수 상태 체크 (기간 확인 및 정원 확인)
	 * @param forVo
	 * @return
	 * @throws Exception
	 */
	public EdcLctreVO checkSttus(EdcLctreVO forVo) throws Exception{
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		String changeSttus = "";
		String edcChangeSttus = "";

		changeSttus = forVo.getRceptSttus();
		edcChangeSttus = forVo.getEdcSttus();
		if(nowDateInt >= StringUtil.strToDouble(forVo.getPriorRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getPriorRceptEndde())){
			changeSttus = "PR1";
			// 정원 체크
			String checkSttus = checkRcritNmpr(forVo, "F");
			if(!"".equals(StringUtil.strTrim(checkSttus))){
				changeSttus = checkSttus;
			}
		}else{
			// 접수기간
			if(nowDateInt < StringUtil.strToDouble(forVo.getRceptBgnde()) ){
					changeSttus = "W01";									// 접수 대기
			}
			if(nowDateInt >= StringUtil.strToDouble(forVo.getRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getRceptEndde())){
					changeSttus = "RP1"; 									// 모집 중

					// 정원 체크
					String checkSttus = checkRcritNmpr(forVo, "R");
					if(!"".equals(StringUtil.strTrim(checkSttus))){
						changeSttus = checkSttus;
					}
			}
			if(nowDateInt > StringUtil.strToDouble(forVo.getRceptEndde())){
					changeSttus = "RC1"; 									// 모집 마감
			}
			
			if(!"RP1".equals(changeSttus)){
				if(!"".equals(StringUtil.strTrim(forVo.getAditRceptBgnde())) && !"".equals(StringUtil.strTrim(forVo.getAditRceptEndde())) ){
					if(nowDateInt > StringUtil.strToDouble(forVo.getRceptBgnde()) &&  nowDateInt < StringUtil.strToDouble(forVo.getAditRceptBgnde()) ){
						changeSttus = "AW1";										// 접수 대기
					}

					if(nowDateInt >= StringUtil.strToDouble(forVo.getAditRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getAditRceptEndde())){
						changeSttus = "AR1"; 										// 모집 중
						System.out.println("########################################$");
						// 정원 체크
						String checkSttus = checkRcritNmpr(forVo, "R");
						if(!"".equals(StringUtil.strTrim(checkSttus))){
							changeSttus = checkSttus;
						}
					}

					if(nowDateInt > StringUtil.strToDouble(forVo.getAditRceptEndde()) && nowDateInt > StringUtil.strToDouble(forVo.getRceptEndde())){
						changeSttus = "AC1"; 										// 모집 마감
					}
				}
			}							
		}

		if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
			// 교육 상태값 변경 처리
			if(nowDateInt < StringUtil.strToDouble(forVo.getEdcBgnde())){
				edcChangeSttus = "EW1";
			}
			if(nowDateInt >= StringUtil.strToDouble(forVo.getEdcBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getEdcEndde())){
				edcChangeSttus = "EP1";
			}
			if(nowDateInt > StringUtil.strToDouble(forVo.getEdcEndde())){
				edcChangeSttus = "EC1";
			}
		}

		forVo.setRceptSttus(changeSttus);
		forVo.setEdcSttus(edcChangeSttus);

		return forVo;
	}

	/**
	 * 접수 상태 체크 (기간 확인 및 정원 확인)
	 * @param forVo
	 * @return
	 * @throws Exception
	 */
	public EdcLctreVO checkSttus(EdcLctreVO forVo, HashMap<Integer, Integer> sumMap) throws Exception{
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		String changeSttus = "";
		String edcChangeSttus = "";

		changeSttus = forVo.getRceptSttus();
		edcChangeSttus = forVo.getEdcSttus();

		int totalSum = forVo.getRcritNmpr() + forVo.getWaitNmpr();
		
		int confirmCnt = 0;

		if(sumMap.containsKey(forVo.getLctreKey())){
			confirmCnt = sumMap.get(forVo.getLctreKey());
		}
		if(totalSum > confirmCnt){
			// 접수기간
			if(nowDateInt >= StringUtil.strToDouble(forVo.getPriorRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getPriorRceptEndde())){
				changeSttus = "PR1"; 										// 모집 중
			}else{

				if(nowDateInt < StringUtil.strToDouble(forVo.getRceptBgnde()) ){
						changeSttus = "W01";									// 접수 대기
				}

				if(nowDateInt >= StringUtil.strToDouble(forVo.getRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getRceptEndde())){
						changeSttus = "RP1"; 									// 모집 중
				}

				if(nowDateInt > StringUtil.strToDouble(forVo.getRceptEndde())){
						changeSttus = "RC1"; 									// 모집 마감
				}
				
				// 추가 접수 기간
				if(!"RP1".equals(changeSttus)){
					if(!"".equals(StringUtil.strTrim(forVo.getAditRceptBgnde())) && !"".equals(StringUtil.strTrim(forVo.getAditRceptEndde())) ){
						if(nowDateInt > StringUtil.strToDouble(forVo.getRceptBgnde()) &&  nowDateInt < StringUtil.strToDouble(forVo.getAditRceptBgnde()) ){
							changeSttus = "AW1";										// 접수 대기
						}

						if(nowDateInt >= StringUtil.strToDouble(forVo.getAditRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getAditRceptEndde())){
							changeSttus = "AR1"; 										// 모집 중

							// 정원 체크
							String checkSttus = checkRcritNmpr(forVo, "R");
							if(!"".equals(StringUtil.strTrim(checkSttus))){
								changeSttus = checkSttus;
							}
						}

						if(nowDateInt > StringUtil.strToDouble(forVo.getAditRceptEndde()) && nowDateInt > StringUtil.strToDouble(forVo.getRceptEndde())){
							changeSttus = "AC1"; 										// 모집 마감
						}
					}
				}		
				
			}
		}else{
			if("PR1".equals(forVo.getRceptSttus())){
				changeSttus = "PC1";
			}else{
				changeSttus = "RC1";
			}
		}
		
		if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
			// 교육 상태값 변경 처리
			if(nowDateInt < StringUtil.strToDouble(forVo.getEdcBgnde())){
				edcChangeSttus = "EW1";
			}
			if(nowDateInt >= StringUtil.strToDouble(forVo.getEdcBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getEdcEndde())){
				edcChangeSttus = "EP1";
			}
			if(nowDateInt > StringUtil.strToDouble(forVo.getEdcEndde())){
				edcChangeSttus = "EC1";
			}
		}

		forVo.setRceptSttus(changeSttus);
		forVo.setEdcSttus(edcChangeSttus);

		return forVo;
	}

	/**
	 * 정원 체크
	 * @param edcLctre
	 * @param checkGubun
	 * @return
	 * @throws Exception
	 */
	public String checkRcritNmpr(EdcLctreVO edcLctre, String checkGubun) throws Exception{
		String rtnStr = "";
		
		int rcritNmpr	= 0;
  		// 신청자 카운트 (취소 제외) 신청완료, 대기
  		int rcritCnt 		= 0;

  		if("F".equals(checkGubun)){
  				// 우선접수인원 정원
  				rcritCnt 		= edcLctre.getSttus01a() + edcLctre.getSttus02a();
  				if("0".equals(edcLctre.getPriorRceptMth())){
  					rcritNmpr	= 	edcLctre.getPriorRceptNmpr() + edcLctre.getPriorRceptWaitNmpr();
  				}else{
  					rcritNmpr	= 	edcLctre.getPriorRceptNmpr() + edcLctre.getPriorRceptWaitNmpr();
  				}
  				
  		}else{
		  		// 정원 정보 (전체 정원)
  				rcritCnt 		= edcLctre.getSttus01b() + edcLctre.getSttus02b();
  				System.out.println(rcritCnt);
  				if("0".equals(edcLctre.getRceptMth())){
  					rcritNmpr	= 	edcLctre.getRcritNmpr() + edcLctre.getWaitNmpr();
  				}else{
  					rcritNmpr	= 	edcLctre.getRcritNmpr() + edcLctre.getWaitNmpr();
  				}
  		}
  		System.out.println(rcritCnt + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
  		System.out.println(rcritNmpr + "@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");

  		// 정원 체크
  		if(rcritNmpr <= rcritCnt){
  			if("F".equals(checkGubun)){
  				rtnStr = "PC1";
  			}else{
  				rtnStr = "RC1";
  			}
  		}else{
  			rtnStr = "";
  		}

		return rtnStr;
	}

	/**
	 * 추첨일자 업데이트
	 */
	public int updateEdcLctreDrwtDe(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception{
		int rtnInt = 0;

		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		rtnInt = edcLctreDAO.updateEdcLctreDrwtDe(edcLctreVO);

		return rtnInt;
	}

/*	*//**
	 *교육 상태값 체크
	 * @param forVo
	 * @return
	 * @throws Exception
	 *//*
	public EdcLctre checkSttus(EdcLctreVO forVo) throws Exception{
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		String stdde = DateUtil.getDateAddDay(-180, "yyyyMMdd");	// 조회 기준일
		stdde = stdde + "0000";

		String changeSttus = "";
		String edcChangeSttus = "";

		changeSttus = forVo.getRceptSttus();
		edcChangeSttus = forVo.getEdcSttus();

		// 우선접수기간
		if(!"".equals(StringUtil.strTrim(forVo.getPriorRceptBgnde())) && !"".equals(StringUtil.strTrim(forVo.getPriorRceptEndde())) ){
			if(nowDateInt < StringUtil.strToDouble(forVo.getPriorRceptBgnde()) ){
				changeSttus = "PW1";										// 접수 대기
			}

			if(nowDateInt >= StringUtil.strToDouble(forVo.getPriorRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getPriorRceptEndde())){
				changeSttus = "PR1"; 										// 모집 중

				// 정원 체크
//				String checkSttus = checkRcritNmpr(forVo, "F");
//				if(!"".equals(StringUtil.strTrim(checkSttus))){
//					changeSttus = checkSttus;
//				}
			}

			if(nowDateInt > StringUtil.strToDouble(forVo.getPriorRceptEndde())){
				changeSttus = "PC1"; 										// 모집 마감
			}

			// 접수기간
			if(nowDateInt > StringUtil.strToDouble(forVo.getPriorRceptEndde()) && nowDateInt < StringUtil.strToDouble(forVo.getRceptBgnde()) ){
				changeSttus = "W01";									// 접수 대기
			}

			if(nowDateInt >= StringUtil.strToDouble(forVo.getRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getRceptEndde())){
				changeSttus = "RP1"; 									// 모집 중

				// 정원 체크
//				String checkSttus = checkRcritNmpr(forVo, "R");
//				if(!"".equals(StringUtil.strTrim(checkSttus))){
//					changeSttus = checkSttus;
//				}
			}

			if(nowDateInt > StringUtil.strToDouble(forVo.getRceptEndde())){
				changeSttus = "RC1"; 									// 모집 마감
			}
		}else{
			// 접수기간
				if(nowDateInt < StringUtil.strToDouble(forVo.getRceptBgnde()) ){
					changeSttus = "W01";									// 접수 대기
				}

				if(nowDateInt >= StringUtil.strToDouble(forVo.getRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getRceptEndde())){
					changeSttus = "RP1"; 									// 모집 중

					// 정원 체크
//					String checkSttus = checkRcritNmpr(forVo, "R");
//					if(!"".equals(StringUtil.strTrim(checkSttus))){
//						changeSttus = checkSttus;
//					}
				}

				if(nowDateInt > StringUtil.strToDouble(forVo.getRceptEndde())){
					changeSttus = "RC1"; 									// 모집 마감
				}
		}

		// 추가 접수 기간
		if(!"RP1".equals(changeSttus)){
			if(!"".equals(StringUtil.strTrim(forVo.getAditRceptBgnde())) && !"".equals(StringUtil.strTrim(forVo.getAditRceptEndde())) ){
				if(nowDateInt > StringUtil.strToDouble(forVo.getRceptBgnde()) &&  nowDateInt < StringUtil.strToDouble(forVo.getAditRceptBgnde()) ){
					changeSttus = "AW1";										// 접수 대기
				}

				if(nowDateInt >= StringUtil.strToDouble(forVo.getAditRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getAditRceptEndde())){
					changeSttus = "AR1"; 										// 모집 중

					// 정원 체크
//					String checkSttus = checkRcritNmpr(forVo, "R");
//					if(!"".equals(StringUtil.strTrim(checkSttus))){
//						changeSttus = checkSttus;
//					}
				}

				if(nowDateInt > StringUtil.strToDouble(forVo.getAditRceptEndde()) && nowDateInt > StringUtil.strToDouble(forVo.getRceptEndde())){
					changeSttus = "AC1"; 										// 모집 마감
				}
			}
		}

		// 교육 상태값 변경 처리
		if(nowDateInt < StringUtil.strToDouble(forVo.getEdcBgnde())){
			edcChangeSttus = "EW1";
		}
		if(nowDateInt >= StringUtil.strToDouble(forVo.getEdcBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getEdcEndde())){
			edcChangeSttus = "EP1";
		}
		if(nowDateInt > StringUtil.strToDouble(forVo.getEdcEndde())){
			edcChangeSttus = "EC1";
		}

		forVo.setRceptSttus(changeSttus);
		forVo.setEdcSttus(edcChangeSttus);

		return forVo;
	}*/

	/**
	 * 연계강좌 목록 조회
	 * @param edcLctreVO
	 * @return
	 * @throws Exception
	 */
	public List<EdcLctre> selectEdcLctreCntcData(EdcLctreVO edcLctreVO) throws Exception{
		List<EdcLctre> list = edcLctreDAO.selectEdcLctreCntcData(edcLctreVO);

		for(EdcLctre forVo : list){
			forVo.setRceptBgnde(DateUtil.getDateFormat(forVo.getRceptBgnde(), "yyyy-mm-dd"));
			forVo.setRceptEndde(DateUtil.getDateFormat(forVo.getRceptEndde(), "yyyy-mm-dd"));
			forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy-mm-dd"));
			forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy-mm-dd"));
		}

		return list;
	}

	/**
	 * 교육강좌 연계 강좌 관리 을(를) 등록한다.
	 * @param EdcLctreCntcVO
	 * @return int 교육강좌 연계 강좌 관리의 시퀀스
	 * @throws Exception
	 */
	public int insertEdcLctreCntc(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception{
		String userId = StaffLoginUtil.getLoginId(request.getSession());
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmmss");
		String clientIp = StringUtil.getClientIpAddr(request);

		edcLctreVO.setFrstRegisterId(userId);
		edcLctreVO.setFrstRegisterPnttm(nowDate);
		edcLctreVO.setFrstRegisterIp(clientIp);

		edcLctreVO.setLastUpdusrId(userId);
		edcLctreVO.setLastUpdusrPnttm(nowDate);
		edcLctreVO.setLastUpdusrIp(clientIp);
		edcLctreVO.setDeleteAt("N");
		edcLctreVO.setUseAt("Y");

		return edcLctreDAO.insertEdcLctreCntc(edcLctreVO);
	}

	/**
	 * 교육강좌 연계 강좌 관리 파일을 업로드 한다. (별도 객체생성 권장)
	 * @param EdcLctreCntcVO
	 * @throws Exception
	 */
  	public void updateEdcLctreCntc(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception{
  		edcLctreVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
		edcLctreVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcLctreVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

  		edcLctreDAO.updateEdcLctreCntc(edcLctreVO);
  	}

  	/**
  	 * 교육강좌 연계  카운트 조회
  	 */
  	public int selectEdcLctreCntcTotCnt(EdcLctreVO edcLctreVO)	throws Exception{
  		return edcLctreDAO.selectEdcLctreCntcTotCnt(edcLctreVO);
  	}

  	/**
  	 * 교육강좌 수강료, 부대비용 조회
  	 */
  	public EdcLctre selectEdcLctrePrice(EdcLctreVO edcLctreVO) throws Exception{
  		return edcLctreDAO.selectEdcLctrePrice(edcLctreVO);
  	}

  	/**
  	 *교육/강좌 목록 리스트 카운트 조회
  	 */
  	@Override
	public int webLctreListTotCnt(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		return (Integer) edcLctreDAO.webLctreListTotCnt( edcLctreVO );
	}

  	public List<EdcLctreVO> webLctreSelectList(HttpServletRequest request,EdcLctreVO edcLctreVO) throws Exception{
  		
  		
	  		/** 기관 목록 조회 (코드 값 조회) **/
			CodeCmmVO vo = new CodeCmmVO();
			vo.setSearchDeleteAt("N");
			vo.setSearchInsttGubun("EC");
			vo.setSearchUseAt("Y");
			List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo, "reserve");
			
			HashMap<String, String> insttMap = new HashMap<String, String>();
			for(CodeCmmVO forVo : insttList){
				insttMap.put(forVo.getCodeValue(), forVo.getCodeNm());
			}
  		
  			// 코드값 HashMap 생성
  			HashMap<String, String> codeHashMap = new HashMap<String, String>();
  			CodeCmmVO cmmVo = new CodeCmmVO();

  			cmmVo.setSearchCodeType("'TCH_LEC_DIV1','USE_AT','RCEPT_STTUS','LEC_TYPE','LEC_CATGY','LEC_APP_TYPE' ");
  			cmmVo.setSearchUseAt("Y");
  			cmmVo.setSearchDeleteAt("N");
  			codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

  			List<EdcLctreVO> rtnList = edcLctreDAO.webLctreSelectList(edcLctreVO);

  			/*
  			HashMap<Integer, Integer> sumMap = new HashMap<Integer, Integer>();
  			
  			String strLctreKey = "";
  			for(EdcLctreVO forVo : rtnList){
  				strLctreKey = strLctreKey + forVo.getLctreKey() + ",";
  			}

  			
  			if(strLctreKey.length() > 0){
  				strLctreKey = strLctreKey.substring(0, strLctreKey.length()-1);

  				edcLctreVO.setSearchLctreKeyIn(strLctreKey);
  	  			List<EdcLctreVO> lctreSum = webLctreSelectListSum(edcLctreVO);
  	  			sumMap = webLctreSelectHashMap(lctreSum);
  			}
  			*/

  			// 데이터가공 구간
  			for(EdcLctreVO forVo : rtnList){
  				if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
  					// 정원 체크 (상태값 확인)
  					//forVo = checkSttus(forVo, sumMap);
  					forVo = checkSttus(forVo);
  				}

  				forVo.setInsttNm(insttMap.get(forVo.getInsttCode()+""));
  				forVo.setUseAtNm(codeHashMap.get("USE_AT"+forVo.getUseAt()));
  				forVo.setRceptSttusNm(codeHashMap.get("RCEPT_STTUS"+forVo.getRceptSttus()));
  				forVo.setLctreLclas(codeHashMap.get("TCH_LEC_DIV1"+forVo.getLctreLclas()));
  				forVo.setEdcMth(codeHashMap.get("LEC_TYPE"+forVo.getEdcMth()));
  				forVo.setLctreCtgry(codeHashMap.get("LEC_CATGY"+forVo.getLctreCtgry()));
  				forVo.setRceptMthNm(codeHashMap.get("LEC_APP_TYPE" + forVo.getRceptMth()));
  				
  				forVo.setEdcDayTime((DateUtil.replaceDayTime(forVo.getEdcDay(), forVo.getEdcTime(), ",", ",")));
  				

  				if(forVo.getAtnclCt() == 0){
  					forVo.setAtnclCtStr("무료");
  				}else{
  					forVo.setAtnclCtStr(StringUtil.getFormatComma(forVo.getAtnclCt()));
  				}

  				forVo.setRceptBgndeHH(StringUtil.strSubString(forVo.getRceptBgnde(), 8, 10));
  				forVo.setRceptBgndeMM(StringUtil.strSubString(forVo.getRceptBgnde(), 10, 12));

  				forVo.setRceptEnddeHH(StringUtil.strSubString(forVo.getRceptEndde(), 8, 10));
  				forVo.setRceptEnddeMM(StringUtil.strSubString(forVo.getRceptEndde(), 10, 12));

  				forVo.setRceptBgnde(DateUtil.getDateFormat(forVo.getRceptBgnde(), "yyyy.mm.dd"));
  				forVo.setRceptEndde(DateUtil.getDateFormat(forVo.getRceptEndde(), "yyyy.mm.dd"));
  				forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy.mm.dd"));
  				forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy.mm.dd"));
  				
  				forVo.setAditRceptBgnde(DateUtil.getDateFormat(forVo.getAditRceptBgnde(), "yyyy.mm.dd"));
  				forVo.setAditRceptEndde(DateUtil.getDateFormat(forVo.getAditRceptEndde(), "yyyy.mm.dd"));

  				forVo.setEdcBeginTime(StringUtil.strSubString(forVo.getEdcBeginTime(), 0, 2)+":"+StringUtil.strSubString(forVo.getEdcBeginTime(), 2, 4));
  				forVo.setEdcEndTime(StringUtil.strSubString(forVo.getEdcEndTime(), 0, 2)+":"+StringUtil.strSubString(forVo.getEdcEndTime(), 2, 4));

  				forVo.setEdcDay(DateUtil.replaceDay(forVo.getEdcDay(), ",", ","));

  				forVo.setPriorRceptBgnde(DateUtil.getDateFormat(forVo.getPriorRceptBgnde(), "yyyy.mm.dd"));
  				forVo.setPriorRceptEndde(DateUtil.getDateFormat(forVo.getPriorRceptEndde(), "yyyy.mm.dd"));
  			}

  			//System.out.println("strLctreKey : " + strLctreKey);

  		return rtnList;
  	}

  	/**
  	 * 선택 강좌에 예약완료, 예약대기 인원수 조회
  	 */
  	public List<EdcLctreVO> webLctreSelectListSum(EdcLctreVO edcLctreVO) throws Exception{
  		List<EdcLctreVO> rtnList = edcLctreDAO.webLctreSelectListSum(edcLctreVO);

  		return rtnList;
  	}

  	/**
  	 * 강좌 key, 접수인원 반환
  	 */
  	public HashMap<Integer, Integer> webLctreSelectHashMap(List<EdcLctreVO> list) throws Exception{
  		HashMap<Integer, Integer> rtnMap = new HashMap<Integer, Integer>();

  		for(EdcLctreVO forVo : list){
  			rtnMap.put(forVo.getLctreKey(), forVo.getTotalSum());
  		}

  		return rtnMap;
  	}

  	public String edcNecessary(EdcLctre vo) throws Exception{
  		String rtnStr = "S";

  		if("".equals(StringUtil.strTrim(vo.getRceptBgnde()))){
  			return "접수시작일";
  		}

  		if("".equals(StringUtil.strTrim(vo.getRceptEndde()))){
  			return "접수종료일";
  		}

  		if("".equals(StringUtil.strTrim(vo.getEdcBgnde()))){
  			return "교육시작일";
  		}

  		if("".equals(StringUtil.strTrim(vo.getEdcEndde()))){
  			return "교육종료일";
  		}

  		if("".equals(StringUtil.strTrim(vo.getRceptMth()))){
  			return "선발방법";
  		}

  		if(vo.getRcritNmpr() == 0){
  			return "모집정원";
  		}

  		return rtnStr;
  	}
  	
	/**
	 * 우선접수 ( 전 강좌 들었는지 체크 ) 
	 * @param EdcLctreVO
	 * @return int 우선접수 ( 전 강좌 들었는지 체크 ) 
	 * @throws Exception
	 */
	@Override
	public int selectEdcLctrePriorTotCnt(HttpServletRequest request, EdcLctreVO edcLctreVO) throws Exception {
		return (Integer) edcLctreDAO.selectEdcLctrePriorTotCnt( edcLctreVO );
	}


}






