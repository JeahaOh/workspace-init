package kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.impl;

import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.co.hanshinit.NeoCMS.cmm.service.ResponseJSON;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.uat.uia.service.LoginUtil;
import kr.co.hanshinit.NeoCop.cop.cmm.service.StaffActionHistoryService;
import kr.co.hanshinit.NeoCop.cop.cmm.service.StaffActionHistoryVO;
import kr.co.hanshinit.NeoCop.cop.cmm.util.StaffLoginUtil;
import kr.co.hanshinit.NeoCop.cop.cmmnPaylog.service.CmmnPaylogVO;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddField;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldService;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.CodeCmmVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmCodeService;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmSmsService;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmSmsVO;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcLctreVO;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcMngr;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcMngrService;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcMngrVO;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcLctreRstrctCndVO;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcUser.service.EdcAtnlcUserBndl;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcUser.service.EdcAtnlcUserService;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcUser.service.EdcAtnlcUserVO;
import kr.co.hanshinit.NeoEdu.cop.edcCode.service.EdcCode;
import kr.co.hanshinit.NeoEdu.cop.edcCode.service.EdcCodeService;
import kr.co.hanshinit.NeoEdu.cop.edcCode.service.EdcCodeVO;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctre;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreService;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreVO;

@SuppressWarnings("unused")
/**
 * - 선생님들 메모리 관리 신경 안쓰세요???
 * - 선생님들 주석은 안 다시나요???
 * - 선생님들 변수명은 왜 이따구로 지으시나요???
 * - 유효성 검증 함수는 만들어 놓고 왜 사용 안함???????????
 */
@Service("edcAtnlcMngrService")
public class EdcAtnlcMngrServiceImpl extends EgovAbstractServiceImpl implements EdcAtnlcMngrService {

	@Resource(name = "edcAtnlcMngrDAO")
	private EdcAtnlcMngrDAO edcAtnlcMngrDAO;

	/** 코드 정보 조회 서비스 **/
	@Resource(name = "edcCmmCodeService")
	private EdcCmmCodeService edcCmmCodeService;

	@Resource(name = "edcAtnlcMngrService")
	private EdcAtnlcMngrService edcAtnlcMngrService;

	/** 강좌관리 서비스 **/
	@Resource(name = "edcLctreService")
	private EdcLctreService edcLctreService;

	/** 수강자 정보 관련 서비스 **/
	@Resource(name = "edcAtnlcUserService")
	private EdcAtnlcUserService edcAtnlcUserService;

	/** SMS 관련 서비스 **/
	@Resource(name = "edcCmmSmsService")
	private EdcCmmSmsService edcCmmSmsService;

	/** 코드값 조회 서비스 **/
	@Resource(name="edcCodeService")
	private EdcCodeService edcCodeService;

	/** 프로그램 신청양신 추가 서비스 **/
	@Resource(name="resveAddFieldService")
	private ResveAddFieldService resveAddFieldService;

	@Resource(name="staffActionHistoryService")
	private StaffActionHistoryService staffActionHistoryService;
	
	Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 수강관리의 목록을 조회한다.
	 *
	 * @param EdcAtnlcMngrVO
	 * @return List<EdcAtnlcMngrVO> 수강관리 목록
	 * @throws Exception
	 */
	public List<EdcAtnlcMngr> selectEdcAtnlcMngrList(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		List<EdcAtnlcMngr> list = edcAtnlcMngrDAO.selectEdcAtnlcMngrList(edcAtnlcMngrVO);

		if(list.size()>0){
			// 코드값 HashMap 생성
			CodeCmmVO cmmVo = new CodeCmmVO();

			cmmVo.setSearchCodeType("'USER_RCEPT_STTUS','SETLE_STTUS'");
			codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);
		}

		// 데이터가공 구간
		String edcChangeSttus = "";
		for(EdcAtnlcMngr forVo : list){
			edcChangeSttus = "";

			forVo.setSttusNm(codeHashMap.get("USER_RCEPT_STTUS"+forVo.getUserRceptSttus()));
			forVo.setSetleSttusNm(codeHashMap.get("SETLE_STTUS"+forVo.getSetleSttus()));
			if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
				if(StringUtil.strToDouble(forVo.getEdcBgnde()) > 0){
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
	
					forVo.setEdcSttus(edcChangeSttus);
				}
			}

		}
		return list;
	}

	/**
	 * 수강관리의 목록을 조회한다.
	 *
	 * @param EdcAtnlcMngrVO
	 * @return List<EdcAtnlcMngrVO> 수강관리 목록
	 * @throws Exception
	 */
	public List<EdcAtnlcMngrVO> selectEdcAtnlcMngrUserList(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {

		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.selectEdcAtnlcMngrUserList(edcAtnlcMngrVO);

		// 데이터가공 구간
		for (int i = 0; i < list.size(); i++) {
			// @@{listLoop}
		}
		return list;
	}

	/**
	 * 수강관리의 레코드 수를 조회 한다.
	 */
	public int selectEdcAtnlcMngrTotCnt(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		return (Integer) edcAtnlcMngrDAO.selectEdcAtnlcMngrTotCnt(edcAtnlcMngrVO);
	}

	/**
	 * 수강생 인원 수 조회
	 */
	public int selectCheckEdcAtnlcUserCnt(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		return (Integer) edcAtnlcMngrDAO.selectCheckEdcAtnlcUserCnt(edcAtnlcMngrVO);
	}

	/**
	 * 수강생 인원 수 조회(단체용)
	 */
	public int selectCheckEdcAtnlcUserEdcNmprCnt(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		return (Integer) edcAtnlcMngrDAO.selectCheckEdcAtnlcUserEdcNmprCnt(edcAtnlcMngrVO);
	}


	/**
	 * 접수 기간 확인
	 * @param edcAtnlcMngrVO
	 * @param edcLctreVO
	 * @return
	 * @throws Exception
	 */
	public boolean selectCheckEdcAtnlcDate(EdcLctreVO edcLctreVO) throws Exception{
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getPriorRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcLctreVO.getPriorRceptEndde())){
			return true;
		}

		if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcLctreVO.getRceptEndde())){
			return true;
		}

		if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getAditRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcLctreVO.getAditRceptEndde())){
			return true;
		}

		return false;
	}

	/**
	 * 접수 기간 확인 (관리자 용)
	 * @param edcAtnlcMngrVO
	 * @param edcLctreVO
	 * @return
	 * @throws Exception
	 */
	public boolean selectCheckEdcAtnlcDate(EdcLctreVO edcLctreVO, String siteId) throws Exception{
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		try{
			if(StringUtil.strToDouble(edcLctreVO.getPriorRceptBgnde()) > 0){
				if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getPriorRceptBgnde())){
					return true;
				}
			}

			if(StringUtil.strToDouble(edcLctreVO.getRceptBgnde()) > 0) {
				if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getRceptBgnde())){
					return true;
				}
			}
			if(StringUtil.strToDouble(edcLctreVO.getAditRceptBgnde()) > 0) {
				if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getAditRceptBgnde())){
					return true;
				}
			}			
			
		}catch(Exception e){
			egovLogger.error("", e);
		}


		return false;
	}

	/**
	 * 모집기간 체크
	 */
	public String selectCheckEdcAtnlcDateStr(EdcLctreVO edcLctreVO) throws Exception{
		String rtnStr = "C1";

		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);
		
		System.out.println(edcLctreVO.getPriorRceptBgnde());
		System.out.println(edcLctreVO.getPriorRceptEndde());
		
		System.out.println(edcLctreVO.getAditRceptBgnde());
		System.out.println(edcLctreVO.getAditRceptEndde());
		
		System.out.println(edcLctreVO.getRceptBgnde());
		System.out.println(edcLctreVO.getRceptEndde());

		if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getPriorRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcLctreVO.getPriorRceptEndde())){
			return "R1";
		}

		if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcLctreVO.getRceptEndde())){
			return "R1";
		}

		if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getAditRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcLctreVO.getAditRceptEndde())){
			return "A1";
		}

		return rtnStr;
	}

	/**
	 * 모집기간 체크
	 */
	public String selectCheckEdcAtnlcDateStr(EdcLctreVO edcLctreVO, String siteId) throws Exception{
		String rtnStr = "C1";

		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		if(StringUtil.strToDouble(edcLctreVO.getPriorRceptBgnde()) > 0){
			if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getPriorRceptBgnde())){
					return "R1";
			}
		}

		if(StringUtil.strToDouble(edcLctreVO.getRceptBgnde()) > 0){
			if(nowDateInt >= StringUtil.strToDouble(edcLctreVO.getRceptBgnde())){
				return "R1";
			}
		}

		return rtnStr;
	}


	/**
	 * 수강관리의 상세정보를 조회한다.
	 */
	public EdcAtnlcMngr selectEdcAtnlcMngrData(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO)
			throws Exception {

		// 상태값, 사용유무, 강좌 상태, 결제 상태, 성별, 접수방법, 할인코드
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		cmmVo.setSearchCodeType("'SETLE_STTUS','	APP_METHOD'");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		EdcAtnlcMngr data = (EdcAtnlcMngr) edcAtnlcMngrDAO.selectEdcAtnlcMngrData(edcAtnlcMngrVO);

		if(data != null){
			if(!"".equals(StringUtil.strTrim(data.getUserBrthdy()))){
				data.setUserBrthdyYear(StringUtil.strSubString(data.getUserBrthdy(), 0, 4));
				data.setUserBrthdyMonth(StringUtil.strSubString(data.getUserBrthdy(), 4, 6));
				data.setUserBrthdyDay(StringUtil.strSubString(data.getUserBrthdy(), 6, 8));

				data.setUserBrthdy(DateUtil.getDateFormat(data.getUserBrthdy(), "yyyy-mm-dd"));
			}

			data.setSetleSttusNm(codeHashMap.get("SETLE_STTUS" + data.getSetleSttus()));
			data.setRceptMthNm(codeHashMap.get("APP_METHOD" + data.getRceptMth()));

			if("Y".equals(data.getEtcCtYn())){
				// 부대비용 합산 결제시
				data.setTotalPrice(data.getSetleAmount() + StringUtil.strToInt(data.getEtcCt()));
			}else if("S".equals(data.getEtcCtYn())){
				// 부대비용 별도수납 시
				data.setTotalPrice(data.getSetleAmount());
			}else if("N".equals(data.getEtcCtYn())){
				data.setTotalPrice(data.getSetleAmount());
			}
			
			data.setUserTel(StringUtil.getTelNoMask(data.getUserTel()));
			data.setUserMobile(StringUtil.getTelNoMask(data.getUserMobile()));

			if(!"".equals(StringUtil.strTrim(data.getUserEmail()))){
				String[] emailArr = data.getUserEmail().split("\\@");

				if(emailArr.length > 1){
					data.setUserEmailBefore(emailArr[0]);
					data.setUserEmailAfter(emailArr[1]);
				}
			}

			//data.setMemo(StringUtil.newLineReplace(data.getMemo()));
		}

		return data;
	}

  /**
   * 예약 처리
   * 와 시발 이렇게 많은 양의 코드에 주석을 안단 대단한 새끼가 있네
   * @param request
   * @param edcAtnlcMngrVO
   * @return
   * @throws Exception
   */
  public int insUpEdcAtnlcMngr(
    HttpServletRequest request,
    EdcAtnlcMngrVO edcAtnlcMngrVO,
    EdcAtnlcUserVO edcAtnlcUserVO,
    EdcAtnlcUserBndl edcAtnlcUserBndl,
    EdcLctreVO rtnEdcVO
  ) throws Exception {
    int rtnInt = 0;

    try {
      String loginUserId = edcAtnlcMngrVO.getLoginUserId();
      String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();
      String sttusCode = "";

      
      int atnclUserCnt = 0;
      if (edcAtnlcUserBndl.getBndlUserNm() != null) {
        atnclUserCnt = edcAtnlcUserBndl.getBndlUserNm().length;
      }
      int edcAtnlcUserKey = 0;

      EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();

      String userId = edcAtnlcMngrVO.getUserId();
      String diCode = edcAtnlcMngrVO.getDiCode();

      if ("".equals(StringUtil.strTrim(edcAtnlcMngrVO.getAtnclNo()))) {
        // 수강 번호가 없다면, 분기처리를 이렇게 어렵게 해놓으면 어떠라는 건지...
        
        // ★★★
        if ("PR1".equals(rtnEdcVO.getRceptSttus())) {
          // 우선접수
          edcAtnlcMngrVO.setReceiptMethod("A");
        } else if ("RP1".equals(rtnEdcVO.getRceptSttus())) {
          // 일반접수
          edcAtnlcMngrVO.setReceiptMethod("B");
        } else if ("AR1".equals(rtnEdcVO.getRceptSttus())) {
          // 추가접수
          edcAtnlcMngrVO.setReceiptMethod("C");
        }

        /** 예약자 정보 저장 **/
        // 강좌별로 값 변경 처리 해야함.
        edcAtnlcMngrVO.setUserRceptSttus(rtnEdcVO.getSttus());
        edcAtnlcMngrVO.setUserId(userId);
        edcAtnlcMngrVO.setDiCode(diCode);

        if ("Y".equals(rtnEdcVO.getGrpYn())) {
          edcAtnlcMngrVO.setRceptNmpr(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[0]));
        } else {
          edcAtnlcMngrVO.setRceptNmpr(1);
        }

        edcAtnlcUserVO.setSttus(rtnEdcVO.getSttus());

        // 결제 상태값 설정 BGN
        if ("N".equals(rtnEdcVO.getFreeYn())) {
          // 무료
          edcAtnlcMngrVO.setSetleSttus("04");
          edcAtnlcMngrVO.setSetleAmount(0);
          edcAtnlcMngrVO.setEtcCt(StringUtil.intToStr(rtnEdcVO.getEtcCt()));
        } else {
          // 유료
          if (edcAtnlcMngrVO.getDscntPt() == 100) {
            edcAtnlcMngrVO.setSetleSttus("01");
            sttusCode = "01";
          } else {
            edcAtnlcMngrVO.setSetleSttus("00");
            sttusCode = "02";
          }

          edcAtnlcMngrVO.setSetleAmount(rtnEdcVO.getAtnclCt());
          edcAtnlcMngrVO.setEtcCt(StringUtil.intToStr(rtnEdcVO.getEtcCt()));
        }
        // 결제 상태값 설정 END
        
        edcAtnlcMngrVO.setSortNo(0);

        //  할인 여부 확인 BGN
        if ("".equals(edcAtnlcUserVO.getDscntCode())) {
          edcAtnlcMngrVO.setDscntYn("N");
        } else {
          edcAtnlcMngrVO.setDscntYn("Y");
        }
        //  할인 여부 확인 END

        // 예약자가 여러명을 등록했는지 확인 BGN
        int loopCnt = 0;
        if (edcAtnlcUserBndl.getBndlUserNm() != null) {
          loopCnt = edcAtnlcUserBndl.getBndlUserNm().length;
        }
        // 예약자가 여러명을 등록했는지 확인 END

        int totalPrice = 0;
        int totalEtcPrice = 0;
        // 모집 정원
        int rcritNmpr = 0;
        String rceptMth = "";
        if ("PR1".equals(rtnEdcVO.getRceptSttus())) {
          rcritNmpr = rtnEdcVO.getPriorRceptNmpr();
          edcAtnlcUserVO.setReceiptMethod("A");
          rceptMth = rtnEdcVO.getPriorRceptMth();
        } else {
          rcritNmpr = rtnEdcVO.getRcritNmpr();
          edcAtnlcUserVO.setReceiptMethod("BC");
          rceptMth = rtnEdcVO.getRceptMth();
        }

        // 신청자 카운트 (취소, 대기 제외)
        int rcritCnt = rtnEdcVO.getSttus00() + rtnEdcVO.getSttus01();
        // int waitCnt = rtnEdcVO.getSttus02();

        int waitCnt = edcAtnlcUserService.selectEdcAtnlcUserWaitCnt(edcAtnlcUserVO);

        // 예약 인원 확인
        int currentNmpr = loopCnt;
        if (currentNmpr == 0) {
          currentNmpr = 1;
        }

        if ("Y".equals(rtnEdcVO.getGrpYn())) {
          currentNmpr = 0;
          for (int j = 0; j < edcAtnlcUserBndl.getBndlUserNm().length; j++) {
            currentNmpr += StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[j]);
          }
        }

        // 승인 인원수에 현재 예약인원을 더한다.
        rcritCnt = rcritCnt + currentNmpr;

        if ("0".equals(rceptMth)) {
          // 선착순 일때
          edcAtnlcMngrVO.setConfmHmpgReflctAt("Y");

          // 예약 상태 확인
          if (rcritNmpr >= rcritCnt) {
            edcAtnlcMngrVO.setUserRceptSttus("01");
            edcAtnlcMngrVO.setConfmDe(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
            sttusCode = "01";
          } else {
            edcAtnlcMngrVO.setUserRceptSttus("02");
            sttusCode = "02";
          }

        } else {
          // 그외일때 (추첨, 승인, 관리자 선정)
          edcAtnlcMngrVO.setConfmHmpgReflctAt("N");
          edcAtnlcMngrVO.setUserRceptSttus("02");
          sttusCode = "02";
        }

        EdcAtnlcUserVO forVO;

        if (loopCnt > 0) {
          // 여러명 등록했을 경우 수강료 계산 Loop BGN
          for (int i = 0; i < loopCnt; i++) {
            forVO = new EdcAtnlcUserVO();
            String[] dscntArr = edcAtnlcUserBndl.getBndlDscntYn()[i].split("\\|");

            // 수강료 계산
            if (StringUtil.strToInt(dscntArr[1]) > 0) {
              // 수강료 계산
              forVO.setIndvdlzSetleAmount(
                rtnEdcVO.getAtnclCt() * (100 - StringUtil.strToInt(dscntArr[1])) / 100
              );
            } else {
              forVO.setIndvdlzSetleAmount(edcAtnlcMngrVO.getAtnclCt());
            }
            forVO.setEtcAmount(rtnEdcVO.getEtcCt());

            if ("Y".equals(rtnEdcVO.getEtcCtYn())) {
              totalPrice = totalPrice + forVO.getIndvdlzSetleAmount() + forVO.getEtcAmount();
            } else if ("S".equals(rtnEdcVO.getEtcCtYn())) {
              totalPrice = totalPrice + forVO.getIndvdlzSetleAmount();
            } else if ("N".equals(rtnEdcVO.getEtcCtYn())) {
              totalPrice = totalPrice + forVO.getIndvdlzSetleAmount();
            }
            totalEtcPrice = totalEtcPrice + forVO.getEtcAmount();
          }
          // 여러명 등록했을 경우 수강료 계산 Loop END
        } else {
          // 수강생 한명일때 수강료 계산 BGN
          if (rtnEdcVO.getAtnclCt() > 0) {
            if (edcAtnlcUserVO.getDscntNm() != null) {
              String[] dscntArr = edcAtnlcUserVO.getDscntNm().split("\\|");
              totalPrice = rtnEdcVO.getAtnclCt() * (100 - StringUtil.strToInt(dscntArr[1])) / 100;
              totalEtcPrice = rtnEdcVO.getEtcCt();
            } else {
              totalPrice = rtnEdcVO.getAtnclCt();
              totalEtcPrice = rtnEdcVO.getEtcCt();
            }
          }
          // 수강생 한명일때 수강료 계산 END
        }
        
        
        

        System.out.println("totalPrice : " + totalPrice);

        edcAtnlcMngrVO.setSetleAmount(totalPrice);
        edcAtnlcMngrVO.setEtcCt(StringUtil.intToStr(totalEtcPrice));
        edcAtnlcMngrVO
            .setUserBrthdy(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserBrthdy(), "-", ""));
        edcAtnlcMngrVO.setEtcCt(StringUtil.intToStr(rtnEdcVO.getEtcCt()));

        if ("Y".equals(edcAtnlcMngrVO.getSmsYn())) {
          edcAtnlcMngrVO.setSmsYn("Y");
        } else {
          edcAtnlcMngrVO.setSmsYn("N");
        }

        String emailSum =
            edcAtnlcMngrVO.getUserEmailBefore() + "@" + edcAtnlcMngrVO.getUserEmailAfter();
        edcAtnlcMngrVO.setUserEmail(emailSum);

        rtnInt = edcAtnlcMngrService.insertEdcAtnlcMngr(request, edcAtnlcMngrVO);

        if (loopCnt > 0) {
          for (int i = 0; i < loopCnt; i++) {
            forVO = new EdcAtnlcUserVO();
            forVO.setLoginUserId(loginUserId);

            /** 수강자 정보 저장 **/
            forVO.setLctreKey(rtnEdcVO.getLctreKey());
            forVO.setAtnclKey(rtnInt);
            forVO.setUserNm(edcAtnlcUserBndl.getBndlUserNm()[i]);
            forVO.setUserBrthdy(
                StringUtil.strReplaceALL(edcAtnlcUserBndl.getBndlUserBrthdy()[i], "-", ""));
            forVO.setUserSexdstn(edcAtnlcUserBndl.getBndlUserSexdstn()[i]);
            forVO.setComplAt("0");
            forVO.setEdcTime(0);
            forVO.setAbandnYn("N");
            forVO.setGrpYn("N");

            // 대기 예약시 대기 순번 채번
            if (rcritNmpr >= rcritCnt) {
              forVO.setWaitSn(0);
            } else {
              forVO.setWaitSn(waitCnt);
            }

            forVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());

            if ("Y".equals(rtnEdcVO.getGrpYn())) {
              forVO.setEdcNmpr(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[i]));
            } else {
              forVO.setEdcNmpr(1);
            }

            forVO.setTel02(edcAtnlcUserBndl.getBndlTel02()[i]);
            forVO.setDrwtIdx(0);
            forVO.setDrwtSttus("N");

            String[] dscntArr = edcAtnlcUserBndl.getBndlDscntYn()[i].split("\\|");
            if ("N".equals(edcAtnlcUserBndl.getBndlDscntYn()[i])) {
              forVO.setDscntYn("N");
            } else {
              forVO.setDscntYn("Y");
            }
            forVO.setDscntCode(dscntArr[0]);
            forVO.setDscntPt(StringUtil.strToInt(dscntArr[1]));
            forVO.setDscntNm(dscntArr[2]);

            String email = edcAtnlcUserBndl.getBndlAtnlcEmailBefore()[i] + "@"
                + edcAtnlcUserBndl.getBndlAtnlcEmailAfter()[i];

            forVO.setAtnlcEmail(email);
            forVO.setApplcntRelate(edcAtnlcUserBndl.getBndlApplcntRelate()[i]);
            forVO.setMberSe(edcAtnlcUserVO.getMberSe());

            if (edcAtnlcUserBndl.getBndlSchoolGradeYear() != null) {
              if (!"".equals(StringUtil.strTrim(edcAtnlcUserBndl.getBndlSchoolGradeYear()[i]))) {
                String schoolGrade = edcAtnlcUserBndl.getBndlSchoolGradeYear()[i] + "|"
                    + edcAtnlcUserBndl.getBndlSchoolGradeBan()[i] + "|"
                    + edcAtnlcUserBndl.getBndlSchoolGradeNo()[i];
                forVO.setSchoolGrade(schoolGrade); // 학년 반
              }
            }

            // 수강료 계산
            if (StringUtil.strToInt(dscntArr[1]) > 0) {
              // 수강료 계산
              forVO.setIndvdlzSetleAmount(
                rtnEdcVO.getAtnclCt() * (100 - StringUtil.strToInt(dscntArr[1])) / 100
              );
            } else {
              forVO.setIndvdlzSetleAmount(edcAtnlcMngrVO.getAtnclCt());
            }
            forVO.setEtcAmount(rtnEdcVO.getEtcCt());

            forVO.setZip(edcAtnlcUserBndl.getBndlZip()[i]);
            forVO.setAdres(edcAtnlcUserBndl.getBndlAdres()[i]);
            forVO.setAdresDetail(edcAtnlcUserBndl.getBndlAdresDetail()[i]);
            forVO.setDongCode(edcAtnlcUserBndl.getBndlDongCode()[i]);

            forVO.setLoginUserId(loginUserId);
            forVO.setLoginUserNm(loginUserNm);
            edcAtnlcUserKey = edcAtnlcUserService.insertEdcAtnlcUser(request, forVO);
          }
        } else {
          String[] dscntArr;

          forVO = new EdcAtnlcUserVO();

          if (rtnEdcVO.getAtnclCt() > 0) {
            if (edcAtnlcUserVO.getDscntNm() != null) {
              dscntArr = edcAtnlcUserVO.getDscntNm().split("\\|");

              forVO.setDscntCode(dscntArr[0]);
              forVO.setDscntPt(StringUtil.strToInt(dscntArr[1]));
              forVO.setDscntNm(dscntArr[2]);
              forVO.setIndvdlzSetleAmount(
                  rtnEdcVO.getAtnclCt() * (100 - StringUtil.strToInt(dscntArr[1])) / 100); // 수강료 계산

              if ("N".equals(dscntArr[0])) {
                forVO.setDscntYn("N");
              } else {
                forVO.setDscntYn("Y");
              }
            } else {
              forVO.setDscntCode("");
              forVO.setDscntPt(0);
              forVO.setDscntNm("대상아님");
              forVO.setDscntYn("N");
              forVO.setIndvdlzSetleAmount(rtnEdcVO.getAtnclCt());
            }
          }

          forVO.setLctreKey(rtnEdcVO.getLctreKey());
          forVO.setAtnclKey(rtnInt);
          forVO.setUserNm(edcAtnlcMngrVO.getUserNm());
          forVO.setUserBrthdy(edcAtnlcMngrVO.getUserBrthdy());
          forVO.setUserSexdstn(edcAtnlcMngrVO.getUserSexdstn());
          forVO.setComplAt("N");
          forVO.setEdcTime(0);
          forVO.setAbandnYn("N");
          forVO.setGrpYn("N");

          forVO.setApplcntRelate(edcAtnlcUserVO.getApplcntRelate());
          forVO.setAtnlcEmail(edcAtnlcMngrVO.getUserEmail());
          forVO.setMberSe(edcAtnlcUserVO.getMberSe());

          if (edcAtnlcUserVO.getSchoolGradeYear() != null) {
            forVO.setSchoolGrade(edcAtnlcUserVO.getSchoolGradeYear() + "|"
                + edcAtnlcUserVO.getSchoolGradeBan() + "|" + edcAtnlcUserVO.getSchoolGradeNo());
          }

          // 대기 예약시 대기 순번 채번
          if (rcritNmpr >= rcritCnt) {
            forVO.setWaitSn(0);
          } else {
            forVO.setWaitSn(waitCnt + 1);
          }

          forVO.setEdcNmpr(1);

          forVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());
          forVO.setTel01(edcAtnlcMngrVO.getUserTel());
          forVO.setTel02(edcAtnlcMngrVO.getUserMobile());
          forVO.setDrwtIdx(0);
          forVO.setDrwtSttus("N");


          forVO.setEtcAmount(rtnEdcVO.getEtcCt());

          forVO.setLoginUserId(loginUserId);
          forVO.setLoginUserNm(loginUserNm);
          edcAtnlcUserService.insertEdcAtnlcUser(request, forVO);
        }

        // 추가 입력 정보 저장
        ResveAddFieldVO addFieldVO = new ResveAddFieldVO();
        addFieldVO.setSearchManagerSeq(rtnEdcVO.getLctreKey() + "");
        addFieldVO.setSearchDeleteAt("N");
        addFieldVO.setSearchJobSe("EC");
        List<ResveAddField> resveAddFieldList =
            resveAddFieldService.selectResveAddFieldListAll(addFieldVO);

        for (ResveAddField forVo : resveAddFieldList) {
          addFieldVO = new ResveAddFieldVO();

          addFieldVO.setManagerSeq(rtnInt);
          addFieldVO.setItemSeq(
              StringUtil.strToInt(request.getParameter("itemSeq" + forVo.getItemSeq())));
          addFieldVO.setSubSeq(edcAtnlcUserKey);
          addFieldVO.setInputCn(request.getParameter("inputCn" + forVo.getItemSeq()));
          addFieldVO.setJobSe("EC");
          addFieldVO.setMngrSeq(rtnEdcVO.getLctreKey());
          addFieldVO.setFrstRegisterId(loginUserId);
          addFieldVO.setLastUpdusrId(loginUserId);

          // Insert
          resveAddFieldService.insertResveInputAddField(request, addFieldVO);
        }

        // SMS 발송 및 SMS History 저장
        edcCmmSmsVO = new EdcCmmSmsVO();
        edcCmmSmsVO.setSearchAtnclKey(rtnInt);

        if ("0".equals(rceptMth)) {
          // 선착순일때
          if ("01".equals(sttusCode)) {
            // 당첨
            edcCmmSmsVO.setSearchStepCode("L01");
          } else if ("00".equals(sttusCode)) {
            // 대기
            edcCmmSmsVO.setSearchStepCode("L11");
          }
        } else {
          // 그외 경우 (추첨, 승인, 관리자 선정)
          edcCmmSmsVO.setSearchStepCode("L12");
        }

        edcCmmSmsVO.setJobSe("EC");
        edcCmmSmsVO.setSttusDiv("INSERT");
        edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);
      }

      
      
      // 상태값 변경 이력 저장 BGN
      StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
      staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
      staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(rtnInt));
      staffActionHistoryVO.setSttusCode(edcAtnlcMngrVO.getUserRceptSttus());
      staffActionHistoryVO.setDivCode("A01");
      staffActionHistoryVO.setUserId(loginUserId);
      staffActionHistoryVO.setSttusNm("강좌 최초 신청");
      staffActionHistoryVO.setProgrmId("EA01");
      staffActionHistoryVO.setUserNm(loginUserNm);

      staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);
      // 상태값 변경 이력 저장 END


    } catch (Exception e) {
      egovLogger.error("EdcAtnlcMngrServiceImpl.insUpEdcAtnlcMngr : ", e);
    }

    return rtnInt;
  }

	/**
	 * 예약 처리 (관리자 화면)
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	public  int insUpEdcAtnlcMngrStaff(HttpServletRequest request
														, EdcAtnlcMngrVO edcAtnlcMngrVO
														, EdcAtnlcUserVO edcAtnlcUserVO
														, EdcAtnlcUserBndl edcAtnlcUserBndl
														, EdcLctreVO rtnEdcVO
					) throws Exception {

		int rtnInt = 0;

		try{
				String loginUserId = edcAtnlcMngrVO.getLoginUserId();
				String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();
				String sttusCode = "";

				int atnclUserCnt = 0;
				if(edcAtnlcUserBndl.getBndlUserNm() != null){
					atnclUserCnt = edcAtnlcUserBndl.getBndlUserNm().length;
				}
				int edcAtnlcUserKey = 0;

				EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();

				String userId = edcAtnlcMngrVO.getUserId();
				String diCode = edcAtnlcMngrVO.getDiCode();

				if ("".equals(StringUtil.strTrim(edcAtnlcMngrVO.getAtnclNo()))){
					/** 예약자 정보 저장 **/
					// 강좌별로 값 변경 처리 해야함.
					edcAtnlcMngrVO.setUserRceptSttus(rtnEdcVO.getSttus());
					edcAtnlcMngrVO.setUserId(userId);
					edcAtnlcMngrVO.setDiCode(diCode);

					if("Y".equals(rtnEdcVO.getGrpYn())){
						edcAtnlcMngrVO.setRceptNmpr(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[0]));
					}else{
						edcAtnlcMngrVO.setRceptNmpr(1);
					}

					edcAtnlcUserVO.setSttus(rtnEdcVO.getSttus());

					// 결제 상태값 설정
					if("N".equals(rtnEdcVO.getFreeYn())){
						// 무료
						edcAtnlcMngrVO.setSetleAmount(0);
						edcAtnlcMngrVO.setEtcCt(edcAtnlcUserBndl.getBndlEtcAmount()[0]);
					}else{
						// 유료
						edcAtnlcMngrVO.setSetleAmount(StringUtil.strToInt(edcAtnlcUserBndl.getBndlIndvdlzSetleAmount()[0]));
						edcAtnlcMngrVO.setEtcCt(edcAtnlcUserBndl.getBndlEtcAmount()[0]);
					}

					sttusCode = edcAtnlcMngrVO.getSetleSttus();

					edcAtnlcMngrVO.setSortNo(0);

					if("".equals(edcAtnlcUserVO.getDscntCode())){
						edcAtnlcMngrVO.setDscntYn("N");
					}else{
						edcAtnlcMngrVO.setDscntYn("Y");
					}

					int loopCnt = 0;
					if(edcAtnlcUserBndl.getBndlUserNm() != null){
						loopCnt = edcAtnlcUserBndl.getBndlUserNm().length;
					}

					int totalPrice = 0;
					int totalEtcPrice = 0;
					// 모집 정원
			  		int rcritNmpr	= 	rtnEdcVO.getRcritNmpr();
			  		// 신청자 카운트 (취소, 대기  제외)
			  		int rcritCnt 		= rtnEdcVO.getSttus00() + rtnEdcVO.getSttus01();
			  		int waitCnt    	= rtnEdcVO.getSttus02();
			  		
					if("PR1".equals(rtnEdcVO.getRceptSttus())){  //우선접수
						edcAtnlcMngrVO.setReceiptMethod("A");
					}else if ("RP1".equals(rtnEdcVO.getRceptSttus())){ //일반접수
						edcAtnlcMngrVO.setReceiptMethod("B");
					}else if ("AR1".equals(rtnEdcVO.getRceptSttus())){ //추가접수
						edcAtnlcMngrVO.setReceiptMethod("C");
					}else {
						edcAtnlcMngrVO.setReceiptMethod("B");
					}

			  		// 예약 인원 확인
			  		int currentNmpr = loopCnt;
					if(currentNmpr == 0){
						currentNmpr = 1;
					}

					if("Y".equals(rtnEdcVO.getGrpYn())){
						for(int j=0;j<edcAtnlcUserBndl.getBndlUserNm().length;j++){
							currentNmpr += StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[j]);
						}
					}

					// 승인 인원수에 현재 예약인원을 더한다.
					rcritCnt = rcritCnt + currentNmpr;

			  		if("0".equals(rtnEdcVO.getRceptMth())){
						// 선착순 일때
						edcAtnlcMngrVO.setConfmHmpgReflctAt("Y");
					}else{
						// 그외일때 (추첨, 승인, 관리자 선정)
						edcAtnlcMngrVO.setConfmHmpgReflctAt("N");
						edcAtnlcMngrVO.setSttus("02");
					}

			  		EdcAtnlcUserVO forVO;

			  		if("Y".equals(rtnEdcVO.getEtcCtYn())){
						totalPrice = totalPrice + StringUtil.strToInt(edcAtnlcUserBndl.getBndlIndvdlzSetleAmount()[0]) + StringUtil.strToInt(edcAtnlcUserBndl.getBndlEtcAmount()[0]);
					}else if("S".equals(rtnEdcVO.getEtcCtYn())){
						totalPrice = totalPrice + StringUtil.strToInt(edcAtnlcUserBndl.getBndlIndvdlzSetleAmount()[0]);
					}
					totalEtcPrice = StringUtil.strToInt(edcAtnlcUserBndl.getBndlEtcAmount()[0]);

					edcAtnlcMngrVO.setSetleAmount(totalPrice);
					edcAtnlcMngrVO.setEtcCt(StringUtil.intToStr(totalEtcPrice));
					edcAtnlcMngrVO.setUserBrthdy(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserBrthdy(), "-", ""));

					if("Y".equals(edcAtnlcMngrVO.getSmsYn())){
						edcAtnlcMngrVO.setSmsYn("Y");
					}else{
						edcAtnlcMngrVO.setSmsYn("N");
					}

					String emailSum = edcAtnlcMngrVO.getUserEmailBefore()+"@"+edcAtnlcMngrVO.getUserEmailAfter();
					edcAtnlcMngrVO.setUserEmail(emailSum);
					edcAtnlcMngrVO.setUserRceptSttus(edcAtnlcUserVO.getSttus());

					if("02".equals(rtnEdcVO.getSttus())){
						// 대기 순번 채번
						int waitInt = edcAtnlcUserService.selectEdcAtnlcUserWaitCnt(edcAtnlcUserVO);
						edcAtnlcUserVO.setWaitSn(waitInt);
					}

					rtnInt = edcAtnlcMngrService.insertEdcAtnlcMngr(request, edcAtnlcMngrVO);

			  		if(loopCnt > 0){
						for(int i=0;i<loopCnt;i++){
								forVO = new EdcAtnlcUserVO();
								forVO.setLoginUserId(loginUserId);

								/** 수강자 정보 저장 **/
								forVO.setLctreKey(rtnEdcVO.getLctreKey());
								forVO.setAtnclKey(rtnInt);
								forVO.setUserNm(edcAtnlcUserBndl.getBndlUserNm()[i]);
								forVO.setUserBrthdy(StringUtil.strReplaceALL(edcAtnlcUserBndl.getBndlUserBrthdy()[i], "-", ""));
								forVO.setUserSexdstn(edcAtnlcUserBndl.getBndlUserSexdstn()[i]);
								forVO.setComplAt("0");
								forVO.setEdcTime(0);
								forVO.setAbandnYn("N");
								forVO.setGrpYn("N");
								forVO.setWaitSn(0);

								// 대기 예약시 대기 순번 채번
								if(rcritNmpr >= rcritCnt){
									forVO.setWaitSn(0);
								}else{
									forVO.setWaitSn(edcAtnlcUserVO.getWaitSn());
								}

								forVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());

								if("Y".equals(rtnEdcVO.getGrpYn())){
									forVO.setEdcNmpr(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[i]));
								}else{
									forVO.setEdcNmpr(1);
								}

								forVO.setTel02(edcAtnlcUserBndl.getBndlTel02()[i]);
								forVO.setDrwtIdx(0);
								forVO.setDrwtSttus("N");

								String[] dscntArr = edcAtnlcUserBndl.getBndlDscntYn()[i].split("\\|");
								if("N".equals(edcAtnlcUserBndl.getBndlDscntYn()[i])){
									forVO.setDscntYn("N");
								}else{
									forVO.setDscntYn("Y");
								}
								forVO.setDscntCode(dscntArr[0]);
								forVO.setDscntPt(StringUtil.strToInt(dscntArr[1]));
								forVO.setDscntNm(dscntArr[2]);

								String email = edcAtnlcUserBndl.getBndlAtnlcEmailBefore()[i]+"@"+edcAtnlcUserBndl.getBndlAtnlcEmailAfter()[i];

								forVO.setAtnlcEmail(email);
								forVO.setApplcntRelate(edcAtnlcUserBndl.getBndlApplcntRelate()[i]);
								forVO.setMberSe(edcAtnlcUserVO.getMberSe());

								if(edcAtnlcUserBndl.getBndlSchoolGradeYear() != null){
									if(!"".equals(StringUtil.strTrim(edcAtnlcUserBndl.getBndlSchoolGradeYear()[i]))){
										String schoolGrade = edcAtnlcUserBndl.getBndlSchoolGradeYear()[i]+"|"+edcAtnlcUserBndl.getBndlSchoolGradeBan()[i]+"|"+ edcAtnlcUserBndl.getBndlSchoolGradeNo()[i];
										forVO.setSchoolGrade(schoolGrade);		// 학년 반
									}
								}

								forVO.setZip(edcAtnlcUserBndl.getBndlZip()[i]);
								forVO.setAdres(edcAtnlcUserBndl.getBndlAdres()[i]);
								forVO.setAdresDetail(edcAtnlcUserBndl.getBndlAdresDetail()[i]);
								forVO.setDongCode(edcAtnlcUserBndl.getBndlDongCode()[i]);

								forVO.setLoginUserId(loginUserId);
								forVO.setLoginUserNm(loginUserNm);
								forVO.setIndvdlzSetleAmount(StringUtil.strToInt(edcAtnlcUserBndl.getBndlIndvdlzSetleAmount()[0]));
								forVO.setEtcAmount(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEtcAmount()[0]));

								edcAtnlcUserKey = edcAtnlcUserService.insertEdcAtnlcUser(request, forVO);
						}
			  		}

					// 추가 입력 정보 저장
					ResveAddFieldVO addFieldVO = new ResveAddFieldVO();
		    		addFieldVO.setSearchManagerSeq(rtnEdcVO.getLctreKey()+"");
		    		addFieldVO.setSearchDeleteAt("N");
		    		addFieldVO.setSearchJobSe("EC");
			  		List<ResveAddField> resveAddFieldList =   resveAddFieldService.selectResveAddFieldListAll(addFieldVO);

			  		for(ResveAddField forVo : resveAddFieldList){
			  			addFieldVO = new ResveAddFieldVO();

			  			addFieldVO.setManagerSeq(rtnInt);
			  			addFieldVO.setItemSeq(StringUtil.strToInt(request.getParameter("itemSeq"+forVo.getItemSeq())));
			  			addFieldVO.setSubSeq(edcAtnlcUserKey);
			  			addFieldVO.setInputCn(request.getParameter("inputCn"+forVo.getItemSeq()));
			  			addFieldVO.setJobSe("EC");
			  			addFieldVO.setMngrSeq(rtnEdcVO.getLctreKey());
			  			addFieldVO.setFrstRegisterId(loginUserId);
			  			addFieldVO.setLastUpdusrId(loginUserId);

			  			// Insert
			  			resveAddFieldService.insertResveInputAddField(request, addFieldVO);
			  		}

					// SMS 발송 및 SMS History 저장
					edcCmmSmsVO = new EdcCmmSmsVO();
					edcCmmSmsVO.setSearchAtnclKey(rtnInt);

					if("0".equals(rtnEdcVO.getRceptMth())){
						// 선착순일때
						if("01".equals(rtnEdcVO.getSttus())){
								// 당첨
								edcCmmSmsVO.setSearchStepCode("L01");
						}else if("00".equals(rtnEdcVO.getSttus())){
								// 대기
								edcCmmSmsVO.setSearchStepCode("L11");
						}
					}else{
						// 그외 경우 (추첨, 승인, 관리자 선정)
						edcCmmSmsVO.setSearchStepCode("L12");
					}

					edcCmmSmsVO.setJobSe("EC");
					edcCmmSmsVO.setSttusDiv("INSERT");
					edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);
				}

				// 상태값 변경 이력 저장
				StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
				staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
				staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(rtnInt));
				staffActionHistoryVO.setSttusCode(edcAtnlcMngrVO.getUserRceptSttus());
				staffActionHistoryVO.setDivCode("A01");
				staffActionHistoryVO.setUserId(loginUserId);
				staffActionHistoryVO.setSttusNm("강좌 최초 신청");
				staffActionHistoryVO.setProgrmId("EA01");
				staffActionHistoryVO.setUserNm(loginUserNm);

				staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);

		}catch (Exception e) {
			egovLogger.error("EdcAtnlcMngrServiceImpl.insUpEdcAtnlcMngr : ", e);
		}

		return rtnInt;
	}

	
	
  /**
   * 수강 신청 가능 여부 판단
   * @param request
   * @param edcLctreVO
   * @param edcAtnlcMngrVO
   * @return
   * @throws Exception
   */
  public EdcLctreVO checkBooking(
    HttpServletRequest request,
    EdcLctreVO edcLctreVO,
    String diCode
  ) throws Exception {
    EdcLctreVO rtnEdcLctreVO = new EdcLctreVO();

    int atnlcInt = 1;
    String userId = edcLctreVO.getLoginUserId();
    String userNm = edcLctreVO.getLoginUserNm();

    // 강좌 접수 조건 확인
    edcLctreVO.setSearchLctreKey(StringUtil.intToStr(edcLctreVO.getLctreKey()));
    rtnEdcLctreVO = edcLctreService.webSelectEdcLctreDetail(request, edcLctreVO);

    rtnEdcLctreVO.setCheckBooking(false);

    if ("Y".equals(rtnEdcLctreVO.getCancelYn())) {
      rtnEdcLctreVO.setReturnMsg("폐강된 강좌 입니다.");
      rtnEdcLctreVO.setReturnCode("C01");

      return rtnEdcLctreVO;
    }

    // 모집 기간 체크
    EdcLctreVO checkEdcLctreVO = new EdcLctreVO();
    checkEdcLctreVO.setPriorRceptBgnde(rtnEdcLctreVO.getPriorRceptBgnde().replace("-", "")
        + rtnEdcLctreVO.getPriorRceptBgndeTime() + rtnEdcLctreVO.getPriorRceptBgndeMinute());
    checkEdcLctreVO.setPriorRceptEndde(rtnEdcLctreVO.getPriorRceptEndde().replace("-", "")
        + rtnEdcLctreVO.getPriorRceptEnddeTime() + rtnEdcLctreVO.getPriorRceptEnddeMinute());
    checkEdcLctreVO.setRceptBgnde(rtnEdcLctreVO.getRceptBgnde());
    checkEdcLctreVO.setRceptEndde(rtnEdcLctreVO.getRceptEndde());
    checkEdcLctreVO.setAditRceptBgnde(rtnEdcLctreVO.getAditRceptBgnde());
    checkEdcLctreVO.setAditRceptEndde(rtnEdcLctreVO.getAditRceptEndde());
    String checkDate = edcAtnlcMngrService.selectCheckEdcAtnlcDateStr(checkEdcLctreVO);

    // 접수기간 구분 P1, R1, A1
    rtnEdcLctreVO.setSearchDateGubun(checkDate);

    if ("C1".equals(checkDate)) {
      // 수강 신청 기간이 아님.
      rtnEdcLctreVO.setReturnMsg("수강 신청 기간이 아닙니다.");
      rtnEdcLctreVO.setReturnCode("C12");

      return rtnEdcLctreVO;
    }

    // 중복 신청 여부 확인
    int atnlcCnt = 0;
    EdcAtnlcMngrVO edcAtnlcMngrVO = new EdcAtnlcMngrVO();
    edcAtnlcMngrVO.setSearchLctreKey(edcLctreVO.getLctreKey() + "");
    edcAtnlcMngrVO.setSearchUserId(userId);
    edcAtnlcMngrVO.setSearchStatus("'00', '01', '02', '09', '10'  ");

    atnlcCnt = edcAtnlcMngrService.selectCheckEdcAtnlcUserCnt(edcAtnlcMngrVO);

    if ("Y".equals(rtnEdcLctreVO.getDplctRceptYn())) {
      // 중복 신청 가능 (전체 중복 가능 인원 수 비교 )
      if (rtnEdcLctreVO.getEdcNmprLmtt() <= atnlcCnt) {
        rtnEdcLctreVO.setReturnMsg("최대 수강인원수가 초가되었습니다.(동일강좌 최대 예약 초과)");
        rtnEdcLctreVO.setReturnCode("C09");

        return rtnEdcLctreVO;
      }
    } else {
      // 중복 신청 불가
      if (!"".equals(StringUtil.strTrim(userId))) {
        if (atnlcCnt > 0) {
          rtnEdcLctreVO.setReturnMsg("수강 신청이 완료 되어있는 강좌 입니다.");
          rtnEdcLctreVO.setReturnCode("C10");

          return rtnEdcLctreVO;
        }
      } else {

      }
    }

    if ("Y".equals(rtnEdcLctreVO.getGrpYn())) {
      atnlcCnt = edcAtnlcMngrService.selectCheckEdcAtnlcUserEdcNmprCnt(edcAtnlcMngrVO);

      if (rtnEdcLctreVO.getGrpRceptNmpr() < atnlcCnt) {
        rtnEdcLctreVO.setReturnMsg("최대 수강신청 인원수가 초과 되었습니다.");
        rtnEdcLctreVO.setReturnCode("C10");

        return rtnEdcLctreVO;
      }
    }

    // 중복 재수강 제한 강좌
    // 연계강좌 수강 금지 시작
    // 중복수강제한 조건, 재수강제한 조건
    // 재수강 금지 (목록 중 이미 [2개]이상의 강좌를 수료)
    // 중복수강제한 조건 (본강좌와 동시에 수강을 할 수 없는 강좌를 등록합니다.)
    // 연계구분(type : D : 중복수강, S: 재수강, R : 연계)
    EdcAtnlcMngrVO checkAtnlVo = new EdcAtnlcMngrVO();
    checkAtnlVo.setSearchUserId(edcAtnlcMngrVO.getUserId());

    checkAtnlVo.setSearchLctreKey(rtnEdcLctreVO.getLctreKey() + "");
    checkAtnlVo.setSearchUserId(userId);
    List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.edcAtnlcAddUserCheck(checkAtnlVo);

    for (EdcAtnlcMngrVO forVo : list) {
      if ("D".equals(forVo.getJobSe())) {
        if (forVo.getCnt() > 0) {
          rtnEdcLctreVO.setReturnMsg("중복수강 제한 강좌에 신청하였습니다.");
          rtnEdcLctreVO.setReturnCode("C20");

          return rtnEdcLctreVO;
        }
      }

      if ("S".equals(forVo.getJobSe())) {
        if ("01".equals(forVo.getUserRceptSttus())) {
          if (forVo.getCnt() > 1) {
            rtnEdcLctreVO.setReturnMsg("재수강 제한 강좌 입니다.");
            rtnEdcLctreVO.setReturnCode("C21");

            return rtnEdcLctreVO;
          }
        }
      }
    }
    /** 연계강좌 수강 금지 끝 **/

    rtnEdcLctreVO.setCheckBooking(true);

    return rtnEdcLctreVO;
  }


	
	
	
	
	
  /**
   * 강좌 신청 가능 여부 확인
   * 
   * @param request
   * @param edcLctreVO
   * @return
   * @throws Exception
   */
  public EdcLctreVO checkBooking(
    HttpServletRequest request,
    EdcLctreVO edcLctreVO,
    EdcAtnlcMngrVO edcAtnlcMngrVO,
    EdcAtnlcUserVO edcAtnlcUserVO,
    EdcAtnlcUserBndl edcAtnlcUserBndl,
    String userId
  ) throws Exception {
    
    StringUtil.mktrace();

    EdcLctreVO rtnEdcLctreVO = new EdcLctreVO();

    try {
      int atnlcInt = 1;
      // 수강생 인원 수 체크
      if (edcAtnlcUserBndl.getBndlUserNm() != null) {
        atnlcInt = edcAtnlcUserBndl.getBndlUserNm().length;
      }

      // 강좌 접수 조건 확인  
      edcLctreVO.setSearchLctreKey(StringUtil.intToStr(edcLctreVO.getLctreKey()));
      rtnEdcLctreVO = edcLctreService.webSelectEdcLctreDetail(request, edcLctreVO);
      rtnEdcLctreVO.setCheckBooking(false);

      //  우선접수 확인 BGN
      if ("PR1".equals(rtnEdcLctreVO.getRceptSttus())) {
        rtnEdcLctreVO.setUserId(LoginUtil.getLoginId(request.getSession()));
        int cnt = edcLctreService.selectEdcLctrePriorTotCnt(request, rtnEdcLctreVO);

        if (cnt == 0) {
          rtnEdcLctreVO.setReturnMsg("우선접수 대상이 아닙니다.");
          rtnEdcLctreVO.setReturnCode("C01");
          return rtnEdcLctreVO;
        }

        // 금천구민 인지 확인, 우선접수 처리
        // String addr = edcAtnlcUserBndl.getBndlAdres()[0];
        //
        // if(!addr.contains("금천구")){
        // rtnEdcLctreVO.setReturnMsg("우선접수기간에는 금천구민 만 접수 가능합니다.");
        // rtnEdcLctreVO.setReturnCode("C01");
        //
        // return rtnEdcLctreVO;
        // }
      }
      //  우선접수 확인 END

      //  폐강 여부 BGN
      if ("Y".equals(rtnEdcLctreVO.getCancelYn())) {
        rtnEdcLctreVO.setReturnMsg("폐강된 강좌 입니다.");
        rtnEdcLctreVO.setReturnCode("C01");

        return rtnEdcLctreVO;
      }
      //  폐강 여부 END
      

      // 나이제한 조건, 학년제한 조건, 성별 제한 조건
      HashMap<String, String> ageMap = new HashMap<String, String>();

      logger.debug("\n\t>> 나이제한 조건 확인 : {}", edcAtnlcUserBndl.getBndlUserNm() != null);
      if (edcAtnlcUserBndl.getBndlUserNm() != null) {
        ageMap = checkAge(rtnEdcLctreVO, edcAtnlcUserBndl);
      } else {
        ageMap = checkAgeOne(rtnEdcLctreVO, edcAtnlcMngrVO);
      }

      if (!"S".equals(ageMap.get("resultCode"))) {
        rtnEdcLctreVO.setReturnMsg(ageMap.get("resultMsg"));
        rtnEdcLctreVO.setReturnCode("C99");

        return rtnEdcLctreVO;
      }

      // 모집 기간 체크
      EdcLctreVO checkEdcLctreVO = new EdcLctreVO();
      checkEdcLctreVO.setPriorRceptBgnde(rtnEdcLctreVO.getPriorRceptBgnde().replace("-", "")
          + rtnEdcLctreVO.getPriorRceptBgndeTime() + rtnEdcLctreVO.getPriorRceptBgndeMinute());
      checkEdcLctreVO.setPriorRceptEndde(rtnEdcLctreVO.getPriorRceptEndde().replace("-", "")
          + rtnEdcLctreVO.getPriorRceptEnddeTime() + rtnEdcLctreVO.getPriorRceptEnddeMinute());
      checkEdcLctreVO.setRceptBgnde(rtnEdcLctreVO.getRceptBgnde());
      checkEdcLctreVO.setRceptEndde(rtnEdcLctreVO.getRceptEndde());
      checkEdcLctreVO.setAditRceptBgnde(rtnEdcLctreVO.getAditRceptBgnde());
      checkEdcLctreVO.setAditRceptEndde(rtnEdcLctreVO.getAditRceptEndde());
      String checkDate = edcAtnlcMngrService.selectCheckEdcAtnlcDateStr(checkEdcLctreVO);

      // 접수기간 구분 P1, R1, A1
      rtnEdcLctreVO.setSearchDateGubun(checkDate);

      if ("C1".equals(checkDate)) {
        // 수강 신청 기간이 아님.
        rtnEdcLctreVO.setReturnMsg("수강 신청 기간이 아닙니다.");
        rtnEdcLctreVO.setReturnCode("C12");

        return rtnEdcLctreVO;
      }

      // 모집 상태 체크
      // 기간이 수강신청 기간이라도 모집상태가 모집중이 아닐 경우 return
      if ("W01".equals(StringUtil.strTrim(rtnEdcLctreVO.getRceptSttus()))
          || "RC1".equals(StringUtil.strTrim(rtnEdcLctreVO.getRceptSttus()))
          || "AC1".equals(StringUtil.strTrim(rtnEdcLctreVO.getRceptSttus()))
          || "PC1".equals(StringUtil.strTrim(rtnEdcLctreVO.getRceptSttus()))
      ) {
        rtnEdcLctreVO.setReturnMsg("강좌모집 상태가 모집 중이 아닙니다.");
        rtnEdcLctreVO.setReturnCode("C13");

        return rtnEdcLctreVO;
      }

      // 신청정원수 확인
      int rcritNmpr = 0;
      // 신청자 카운트 (취소 제외)
      int rcritCnt = 0;
      String rceptMth = "";

      if ("PR1".equals(rtnEdcLctreVO.getRceptSttus())) {
        rceptMth = rtnEdcLctreVO.getPriorRceptMth();
        rcritCnt = rtnEdcLctreVO.getSttus01a() + rtnEdcLctreVO.getSttus02a();
        // 정시 접수 및 추가 접수 기간
        if ("0".equals(rceptMth)) {
          rcritNmpr = rtnEdcLctreVO.getPriorRceptNmpr() + rtnEdcLctreVO.getPriorRceptWaitNmpr();
        } else {
          rcritNmpr = rtnEdcLctreVO.getPriorRceptNmpr() + rtnEdcLctreVO.getPriorRceptWaitNmpr();
        }
      } else {
        rceptMth = rtnEdcLctreVO.getRceptMth();
        rcritCnt = rtnEdcLctreVO.getSttus01b() + rtnEdcLctreVO.getSttus02b();
        // 정시 접수 및 추가 접수 기간
        if ("0".equals(rceptMth)) {
          rcritNmpr = rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
        } else {
          rcritNmpr = rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
        }
      }


      // 현재 예약 인원 추가
      int atnclUserCnt = 1;
      if (edcAtnlcUserBndl.getBndlUserNm() != null) {
        atnclUserCnt = edcAtnlcUserBndl.getBndlUserNm().length;
        if (atnclUserCnt == 0) {
          atnclUserCnt = 1;
        }
      }

      if ("Y".equals(rtnEdcLctreVO.getGrpYn())) {
        atnclUserCnt = StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[0]);
      }

      rcritCnt = rcritCnt + atnclUserCnt;


      // 모집 정원 체크
      if (rcritNmpr < rcritCnt) {
        // 모집 정원 마감
        rtnEdcLctreVO.setReturnMsg("모집 정원이 초과 되었습니다.");
        rtnEdcLctreVO.setReturnCode("C02");

        return rtnEdcLctreVO;
      } else {
        // 모집 정원 미 마감

        // 선착순일때
        if ("0".equals(rceptMth)) {
          rtnEdcLctreVO.setSttus("01");
        } else {
          rtnEdcLctreVO.setReturnMsg("대기 예약이  완료 되었습니다.");
          rtnEdcLctreVO.setReturnCode("C06");
          rtnEdcLctreVO.setSttus("02");
        }

      }

      // 중복 신청 여부 확인
      edcAtnlcMngrVO.setSearchUserId(userId);
      edcAtnlcMngrVO.setSearchLctreKey(edcLctreVO.getSearchLctreKey());
      int atnlcCnt = selectEdcCnt(edcAtnlcMngrVO);

      if ("Y".equals(rtnEdcLctreVO.getDplctRceptYn())) {
        // 중복 신청 가능 (전체 중복 가능 인원 수 비교 )
        if (rtnEdcLctreVO.getEdcNmprLmtt() < atnlcInt) {
          rtnEdcLctreVO.setReturnMsg("최대 수강인원수가 초가되었습니다.(동일강좌 최대 예약 초과)");
          rtnEdcLctreVO.setReturnCode("C09");

          return rtnEdcLctreVO;
        }

        if ("Y".equals(rtnEdcLctreVO.getGrpYn())) {
          if (rtnEdcLctreVO.getGrpRceptNmpr() < atnlcInt) {
            rtnEdcLctreVO.setReturnMsg("최대 수강인원수가 초가되었습니다.(동일강좌 최대 예약 초과)");
            rtnEdcLctreVO.setReturnCode("C09");

            return rtnEdcLctreVO;
          }
        }
      } else {
        // 중복 신청 불가 및 단체 인원수 체크 로직
        edcAtnlcMngrVO = new EdcAtnlcMngrVO();
        edcAtnlcMngrVO.setSearchLctreKey(edcLctreVO.getLctreKey() + "");

        if (!"".equals(StringUtil.strTrim(userId))) {
          edcAtnlcMngrVO.setSearchUserId(userId);

          edcAtnlcMngrVO.setSearchStatus("'00', '01', '02', '09', '10'");
          atnlcCnt = edcAtnlcMngrService.selectCheckEdcAtnlcUserCnt(edcAtnlcMngrVO);

          if (atnlcCnt > 0) {
            rtnEdcLctreVO.setReturnMsg("수강 신청이 완료 되어있는 강좌 입니다.");
            rtnEdcLctreVO.setReturnCode("C10");

            return rtnEdcLctreVO;
          }
        } else {

        }

      }

      // 연계강좌 수강 금지 시작
      // 중복수강제한 조건, 재수강제한 조건
      // 재수강 금지 (목록 중 이미 [2개]이상의 강좌를 수료)
      // 중복수강제한 조건 (본강좌와 동시에 수강을 할 수 없는 강좌를 등록합니다.)
      // 연계구분(type : D : 중복수강, S: 재수강, R : 연계)
      EdcAtnlcMngrVO checkAtnlVo = new EdcAtnlcMngrVO();
      if ("".equals(StringUtil.strTrim(edcAtnlcMngrVO.getUserId()))) {
        // 아이디 없는 경우 == 개인정보(이름, 전화번호) 중복 여부 체크
        checkAtnlVo.setSearchUserNm(edcAtnlcMngrVO.getUserNm());
        checkAtnlVo
            .setSearchUserBrthdy(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserBrthdy(), "-", ""));
        checkAtnlVo.setSearchTel01(edcAtnlcMngrVO.getUserTel());
      } else {
        // 아이디 있는 경우
        checkAtnlVo.setSearchUserId(edcAtnlcMngrVO.getUserId());
      }

      checkAtnlVo.setSearchLctreKey(rtnEdcLctreVO.getLctreKey() + "");
      checkAtnlVo.setSearchUserId(userId);
      List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.edcAtnlcAddUserCheck(checkAtnlVo);

      for (EdcAtnlcMngrVO forVo : list) {
        if ("D".equals(forVo.getJobSe())) {
          if (forVo.getCnt() > 0) {
            rtnEdcLctreVO.setReturnMsg("중복수강 제한 강좌에 신청하였습니다.");
            rtnEdcLctreVO.setReturnCode("C20");

            return rtnEdcLctreVO;
          }
        }

        if ("S".equals(forVo.getJobSe())) {
          if ("01".equals(forVo.getUserRceptSttus())) {
            if (forVo.getCnt() > 1) {
              rtnEdcLctreVO.setReturnMsg("재수강 제한 강좌 입니다.");
              rtnEdcLctreVO.setReturnCode("C21");

              return rtnEdcLctreVO;
            }
          }
        }
      }
      /** 연계강좌 수강 금지 끝 **/

      rtnEdcLctreVO.setReturnCode("S");
      rtnEdcLctreVO.setCheckBooking(true);

    } catch (Exception e) {
      egovLogger.error("EdcAtnlcMngrServiceImpl.checkBooking : ", e);
    }

    return rtnEdcLctreVO;
  }
	
	
	
	
	
	
	
	

  /**
   * [관리자용] 강좌 신청 가능 여부 확인 
   */
  public EdcLctreVO checkBooking(
    HttpServletRequest request,
    EdcLctreVO edcLctreVO,
    EdcAtnlcMngrVO edcAtnlcMngrVO,
    EdcAtnlcUserVO edcAtnlcUserVO,
    EdcAtnlcUserBndl edcAtnlcUserBndl,
    String userId,
    String siteId
  ) throws Exception {

    EdcLctreVO rtnEdcLctreVO = new EdcLctreVO();

    try {
      int atnlcInt = 1;
      // 수강생 인원 수 체크
      if (edcAtnlcUserBndl.getBndlUserNm() != null) {
        atnlcInt = edcAtnlcUserBndl.getBndlUserNm().length;
      }

      // 강좌 접수 조건 확인
      edcLctreVO.setSearchLctreKey(StringUtil.intToStr(edcLctreVO.getLctreKey()));
      rtnEdcLctreVO = edcLctreService.webSelectEdcLctreDetail(request, edcLctreVO);
      rtnEdcLctreVO.setCheckBooking(false);

      if ("Y".equals(rtnEdcLctreVO.getCancelYn())) {
        rtnEdcLctreVO.setReturnMsg("폐강된 강좌 입니다.");
        rtnEdcLctreVO.setReturnCode("C01");

        return rtnEdcLctreVO;
      }

      // 나이제한 조건, 학년제한 조건, 성별 제한 조건
      HashMap<String, String> ageMap = new HashMap<String, String>();

      // if(edcAtnlcUserBndl.getBndlUserNm() != null){
      // ageMap = checkAge(rtnEdcLctreVO, edcAtnlcUserBndl);
      // }else{
      // ageMap = checkAgeOne(rtnEdcLctreVO, edcAtnlcMngrVO);
      // }

      // if(!"S".equals(ageMap.get("resultCode"))){
      // rtnEdcLctreVO.setReturnMsg(ageMap.get("resultMsg"));
      // rtnEdcLctreVO.setReturnCode("C99");

      // return rtnEdcLctreVO;
      // }

      // 모집 기간 체크
      String checkDate = edcAtnlcMngrService.selectCheckEdcAtnlcDateStr(rtnEdcLctreVO, siteId);

      // 접수기간 구분 P1, R1, A1
      rtnEdcLctreVO.setSearchDateGubun(checkDate);

      if ("C1".equals(checkDate)) {
        // 수강 신청 기간이 아님.
        // 요청으로 인해 주석처리
        //  rtnEdcLctreVO.setReturnMsg("수강 신청 기간이 아닙니다."); rtnEdcLctreVO.setReturnCode("C12");
        //  return rtnEdcLctreVO;
      }

      // 신청정원수 확인
      int rcritNmpr = 0;
      // 신청자 카운트 (취소 제외)
      int rcritCnt = rtnEdcLctreVO.getSttus00() + rtnEdcLctreVO.getSttus01()
          + rtnEdcLctreVO.getSttus02() + rtnEdcLctreVO.getSttus09() + rtnEdcLctreVO.getSttus10();
      // 현재 예약 인원 추가
      int atnclUserCnt = 1;
      if (edcAtnlcUserBndl.getBndlUserNm() != null) {
        atnclUserCnt = edcAtnlcUserBndl.getBndlUserNm().length;
        if (atnclUserCnt == 0) {
          atnclUserCnt = 1;
        }
      }

      if ("Y".equals(rtnEdcLctreVO.getGrpYn())) {
        atnclUserCnt = StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[0]);
      }

      rcritCnt = rcritCnt + atnclUserCnt;

      // 정시 접수 및 추가 접수 기간
      rcritNmpr = rtnEdcLctreVO.getRcritNmpr(); // + rtnEdcLctreVO.getWaitNmpr();
      String rceptMth = "";


      if ("PR1".equals(rtnEdcLctreVO.getRceptSttus())) {
        rceptMth = rtnEdcLctreVO.getPriorRceptMth();
        rcritCnt = rtnEdcLctreVO.getSttus01a() + rtnEdcLctreVO.getSttus02a();
        // 정시 접수 및 추가 접수 기간
        if ("0".equals(rceptMth)) {
          rcritNmpr = rtnEdcLctreVO.getPriorRceptNmpr() + rtnEdcLctreVO.getPriorRceptWaitNmpr();
        } else {
          rcritNmpr = rtnEdcLctreVO.getPriorRceptNmpr() + rtnEdcLctreVO.getPriorRceptWaitNmpr();
        }
      } else {
        rceptMth = rtnEdcLctreVO.getRceptMth();
        rcritCnt = rtnEdcLctreVO.getSttus01b() + rtnEdcLctreVO.getSttus02b();
        // 정시 접수 및 추가 접수 기간
        if ("0".equals(rceptMth)) {
          rcritNmpr = rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
        } else {
          rcritNmpr = rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
        }
      }


      if ("0".equals(rtnEdcLctreVO.getRceptMth())) {
        if (rcritNmpr < rcritCnt) {
          rtnEdcLctreVO.setReturnMsg("대기 예약이  완료 되었습니다.");
          rtnEdcLctreVO.setSttus("02");
        } else {
          rtnEdcLctreVO.setSttus("01");
        }
      } else {
        rtnEdcLctreVO.setReturnMsg("대기 예약이  완료 되었습니다.");
        rtnEdcLctreVO.setSttus("02");
      }

      // 중복 신청 여부 확인
      int atnlcCnt = 0;
      if ("Y".equals(rtnEdcLctreVO.getDplctRceptYn())) {
        // 중복 신청 가능 (전체 중복 가능 인원 수 비교 )
        edcAtnlcMngrVO.setSearchUserId(userId);
        edcAtnlcMngrVO.setSearchLctreKey(edcLctreVO.getSearchLctreKey());
        atnlcCnt = selectEdcCnt(edcAtnlcMngrVO);
        int edcNmprLmttInt = rtnEdcLctreVO.getEdcNmprLmtt();
        if (atnlcCnt >= edcNmprLmttInt) {
          rtnEdcLctreVO.setReturnMsg("최대 수강인원수가 초과 되었습니다.(동일강좌 최대 예약 초과)");
          rtnEdcLctreVO.setReturnCode("C09");

          return rtnEdcLctreVO;
        }
      } else {
        // 중복 신청 불가
        edcAtnlcMngrVO = new EdcAtnlcMngrVO();
        edcAtnlcMngrVO.setSearchLctreKey(edcLctreVO.getLctreKey() + "");

        if (!"".equals(StringUtil.strTrim(userId))) {
          edcAtnlcMngrVO.setSearchUserId(userId);
          edcAtnlcMngrVO.setSearchStatus("'00', '01', '02', '09', '10'  ");
          atnlcCnt = edcAtnlcMngrService.selectCheckEdcAtnlcUserCnt(edcAtnlcMngrVO);

          if (atnlcCnt > 0) {
            rtnEdcLctreVO.setReturnMsg("수강 신청이 완료 되어있는 강좌 입니다.");
            rtnEdcLctreVO.setReturnCode("C10");

            return rtnEdcLctreVO;
          }
        } else {

        }
      }

      rtnEdcLctreVO.setReturnCode("S");
      rtnEdcLctreVO.setCheckBooking(true);

    } catch (Exception e) {
      egovLogger.error("EdcAtnlcMngrServiceImpl.checkBooking : ", e);
    }

    return rtnEdcLctreVO;
  }
	
	
	
	
	
	

	/**
	 * 연계강좌 체크 로직
	 * @return
	 */
	@Override
	public EdcLctreVO checkLctre(
						 HttpServletRequest request
						, EdcLctreVO edcLctreVO
						, EdcAtnlcMngrVO edcAtnlcMngrVO
						, EdcAtnlcUserBndl edcAtnlcUserBndl
						, String userId
						, String siteId) throws Exception{

		EdcLctreVO rtnEdcLctreVO = new EdcLctreVO();
		/**
  		 * 	연계강좌 수강 금지 시작
  		 *		중복수강제한 조건,  재수강제한 조건
  		 *		재수강 금지 (목록 중 이미 [2개]이상의 강좌를 수료)
  		 *		중복수강제한 조건 (본강좌와 동시에 수강을 할 수 없는 강좌를 등록합니다.)
  		 *		연계구분(type : D : 중복수강, S: 재수강)
  		**/
  		EdcAtnlcMngrVO checkAtnlVo = new EdcAtnlcMngrVO();
  		if("".equals(StringUtil.strTrim(edcAtnlcMngrVO.getUserId()))){
  			// 아이디 없는 경우 == 개인정보(이름, 전화번호) 중복 여부 체크
  			checkAtnlVo.setSearchUserId(userId);

  		}else{
  			// 아이디 있는 경우
  			checkAtnlVo.setSearchUserId(edcAtnlcMngrVO.getUserId());
  		}

  		checkAtnlVo.setSearchLctreKey(edcAtnlcMngrVO.getSearchLctreKey());
  		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.edcAtnlcAddUserCheck(checkAtnlVo);

  		for(EdcAtnlcMngrVO forVo : list){
  				if("D".equals(forVo.getJobSe())){
  					if(forVo.getCnt() > 0){
  						rtnEdcLctreVO.setReturnMsg("중복수강 제한 강좌에 접수되어 있는 사용자입니다.\n\r접수를 진행하시겠습니까?");
  						rtnEdcLctreVO.setReturnCode("C20");

  						return rtnEdcLctreVO;
  					}
  				}

  				if("S".equals(forVo.getJobSe())){
  					if("01".equals(forVo.getUserRceptSttus())){
	  					if(forVo.getCnt() > 1){
	  						rtnEdcLctreVO.setReturnMsg("재수강 제한 강좌에 접수되어 있는 사용자입니다.\n\r접수를 진행하시겠습니까?");
	  						rtnEdcLctreVO.setReturnCode("C21");

	  						return rtnEdcLctreVO;
	  					}
  					}
  				}
  		}
  		/** 연계강좌 수강 금지 끝 **/
  		rtnEdcLctreVO.setReturnCode("S");

  		return rtnEdcLctreVO;
	}


  /**
   * 성별, 나이, 학년 제한 확인
   * 
   * @param rtnEdcLctreVO
   * @param edcAtnlcUserBndl
   * @return
   * @throws Exception
   */
  public HashMap<String, String> checkAge(
    EdcLctreVO rtnEdcLctreVO
    , EdcAtnlcUserBndl edcAtnlcUserBndl
  ) throws Exception {
    
    HashMap<String, String> rtnMap = new HashMap<String, String>();

    boolean lmitSchoolCheck = false;
    boolean ageRceptAtCheck = false;

    int gradeBegin = 0;
    int gradeEnd = 0;
    
    //  학년제한 시작과 끝이 있다면, BGN
    if (!"".equals(rtnEdcLctreVO.getLmttGradeBegin()) && !"".equals(rtnEdcLctreVO.getLmttGradeEnd()) ) {
      gradeBegin = StringUtil.strToInt(rtnEdcLctreVO.getLmttGradeBegin());
      gradeEnd = StringUtil.strToInt(rtnEdcLctreVO.getLmttGradeEnd());
      //  학년 제한이 있다.
      lmitSchoolCheck = true;
      logger.debug("학년제한 시작과 끝이 있다 -> gradeBegin : {}, gradeEnd : {}, lmitSchoolCheck : {}"
        , gradeBegin, gradeEnd, lmitSchoolCheck
      );
    }
    //  학년제한 시작과 끝이 있다면, END
    
    // 나이제한 여부 (만나이기준)
    double ageRceptLow = StringUtil.strToDouble(StringUtil.strReplaceALL(rtnEdcLctreVO.getAgeRceptLow(), "-", ""));
    double ageRceptHigh = StringUtil.strToDouble(StringUtil.strReplaceALL(rtnEdcLctreVO.getAgeRceptHigh(), "-", ""));

    if ("Y".equals(rtnEdcLctreVO.getAgeRceptAt())) {
      ageRceptAtCheck = true;
    }

    if (edcAtnlcUserBndl.getBndlUserNm() != null) {
      int loopCnt = edcAtnlcUserBndl.getBndlUserNm().length;

      for (int i = 0; i < loopCnt; i++) {
        
        // 여자만 접수일때 체크
        if ("F".equals(rtnEdcLctreVO.getLmttSexdstn())) {
          // 여자만 가능
          if (!"F".equals(edcAtnlcUserBndl.getBndlUserSexdstn()[i])) {
            rtnMap.put("resultCode", "SEX_F");
            rtnMap.put("resultMsg", "해당 강좌는 여성만 접수 가능합니다.");
            return rtnMap;
          }
        }

        // 남자만 접수일때 체크
        if ("M".equals(rtnEdcLctreVO.getLmttSexdstn())) {
          if (!"M".equals(edcAtnlcUserBndl.getBndlUserSexdstn()[i])) {
            rtnMap.put("resultCode", "SEX_F");
            rtnMap.put("resultMsg", "해당 강좌는 남성만 접수 가능합니다.");

            return rtnMap;
          }
        }

        // 학년제한이 있을 경우 BGN
        if (lmitSchoolCheck) {
          int age = StringUtil.getSchoolYear(edcAtnlcUserBndl.getBndlUserBrthdy()[i], "2");
          logger.debug(">> lmitSchoolCheck : {}, age : {} <<", lmitSchoolCheck, age);
          // 학년제한 시작과 끝이 있다 -> gradeBegin : 1, gradeEnd : 12, lmitSchoolCheck : true
          // >> lmitSchoolCheck : true, age : 29 <<

          //  20210721 제정신?
          if (gradeBegin > age || gradeEnd < age) {
            rtnMap.put("resultCode", "SCHOOL_F");
            rtnMap.put("resultMsg", "모집하고 있는 학년과 맞지 않습니다.(생년월일기준)");

            return rtnMap;
          }
        }
       // 학년제한이 있을 경우 END

        if (ageRceptAtCheck) {
          double age =
              StringUtil.strToDouble(edcAtnlcUserBndl.getBndlUserBrthdy()[i].replaceAll("-", ""));

          if (ageRceptLow > 0 && ageRceptHigh == 0) {
            if (age <= ageRceptLow) {
              rtnMap.put("resultCode", "AGE_F");
              rtnMap.put("resultMsg", "교육/강좌 모집 연령에 맞지 않습니다.");

              return rtnMap;
            }
          }

          if (ageRceptLow == 0 && ageRceptHigh > 0) {
            if (age <= ageRceptHigh) {
              rtnMap.put("resultCode", "AGE_F");
              rtnMap.put("resultMsg", "교육/강좌 모집 연령에 맞지 않습니다.");

              return rtnMap;
            }
          }

          if (ageRceptLow > 0 && ageRceptHigh > 0) {
            if (age >= ageRceptLow && age <= ageRceptHigh) {

            } else {
              rtnMap.put("resultCode", "AGE_F");
              rtnMap.put("resultMsg", "교육/강좌 모집 연령에 맞지 않습니다.");

              return rtnMap;
            }
          }
        }
      }
      
      
    }

    rtnMap.put("resultCode", "S");
    return rtnMap;
  }

  /**
   * 성별, 나이, 학년 제한 확인
   * 
   * @param rtnEdcLctreVO
   * @param edcAtnlcUserBndl
   * @return
   * @throws Exception
   */
  public HashMap<String, String> checkAgeOne(
    EdcLctreVO rtnEdcLctreVO
    , EdcAtnlcMngrVO edcAtnlcMngrVo
  ) throws Exception {
    
    
    
    HashMap<String, String> rtnMap = new HashMap<String, String>();

    //  학년 제한 여부
    boolean lmitSchoolCheck = false;
    //  나이 제한 여부
    boolean ageRceptAtCheck = false;

    int gradeBegin = 0;
    int gradeEnd = 0;

    //  학년제한 시작과 끝이 있다면, BGN
    if (!"".equals(rtnEdcLctreVO.getLmttGradeBegin()) && !"".equals(rtnEdcLctreVO.getLmttGradeEnd()) ) {
      gradeBegin = StringUtil.strToInt(rtnEdcLctreVO.getLmttGradeBegin());
      gradeEnd = StringUtil.strToInt(rtnEdcLctreVO.getLmttGradeEnd());
      //  학년 제한이 있다.
      lmitSchoolCheck = true;
      
      logger.debug("학년제한 시작과 끝이 있다 -> gradeBegin : {}, gradeEnd : {}, lmitSchoolCheck : {}"
        , gradeBegin, gradeEnd, lmitSchoolCheck
      );
    }
    //  학년제한 시작과 끝이 있다면, END

    // 나이제한 여부 (만나이기준)
    int ageRceptLow = StringUtil.strToInt(rtnEdcLctreVO.getAgeRceptLow());
    int ageRceptHigh = StringUtil.strToInt(rtnEdcLctreVO.getAgeRceptHigh());

    if (ageRceptLow > 0 || ageRceptHigh > 0) {
      ageRceptAtCheck = true;
    }

    // 여자만 접수일때 체크
    // if("F".equals(rtnEdcLctreVO.getLmttSexdstn())){
    // // 여자만 가능
    // if(!"F".equals(edcAtnlcMngrVo.getUserSexdstn())){
    // rtnMap.put("resultCode", "SEX_F");
    // rtnMap.put("resultMsg", "해당 강좌는 여성만 접수 가능합니다.");
    //
    // return rtnMap;
    // }
    // }

    // 남자만 접수일때 체크
    // if("M".equals(rtnEdcLctreVO.getLmttSexdstn())){
    // if(!"M".equals(edcAtnlcMngrVo.getUserSexdstn())){
    // rtnMap.put("resultCode", "SEX_F");
    // rtnMap.put("resultMsg", "해당 강좌는 남성만 접수 가능합니다.");
    //
    // return rtnMap;
    // }
    // }


    // 학년제한이 있을 경우 BGN
    if ( lmitSchoolCheck ) {
      int age = StringUtil.getSchoolYear(edcAtnlcMngrVo.getUserBrthdy(), "2");

      //  20210721 제정신?
      if (gradeBegin > age || gradeEnd < age) {
        rtnMap.put("resultCode", "SCHOOL_F");
        rtnMap.put("resultMsg", "모집하고 있는 학년과 맞지 않습니다.(생년월일기준)");

        return rtnMap;
      }
    }
    // 학년제한이 있을 경우 END

    if (ageRceptAtCheck) {
      int age = StringUtil.getAge(edcAtnlcMngrVo.getUserBrthdy(), "1");

      if (ageRceptLow > 0 && ageRceptHigh == 0) {
        if (age <= ageRceptLow) {
          rtnMap.put("resultCode", "AGE_F");
          rtnMap.put("resultMsg", "교육/강좌 모집 연령에 맞지 않습니다.");

          return rtnMap;
        }
      }

      if (ageRceptLow == 0 && ageRceptHigh > 0) {
        if (age >= ageRceptHigh) {
          rtnMap.put("resultCode", "AGE_F");
          rtnMap.put("resultMsg", "교육/강좌 모집 연령에 맞지 않습니다.");

          return rtnMap;
        }
      }

      if (ageRceptLow > 0 && ageRceptHigh > 0) {
        if (age >= ageRceptLow && age <= ageRceptHigh) {

        } else {
          rtnMap.put("resultCode", "AGE_F");
          rtnMap.put("resultMsg", "교육/강좌 모집 연령에 맞지 않습니다.");

          return rtnMap;
        }
      }
    }

    rtnMap.put("resultCode", "S");

    return rtnMap;
  }


	/**
	 * 강좌 신청 마지막 단계 서비스
	 */
	public EdcAtnlcMngrVO selectReservationFinish(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		return edcAtnlcMngrDAO.selectReservationFinish(edcAtnlcMngrVO);
	}

	
	
	
	
	
	
	
	
	
	
	/**
	 * 수강관리 을(를) 등록한다.
	 *
	 * @param EdcAtnlcMngrVO
	 * @return int 수강관리의 시퀀스
	 * @throws Exception
	 */
	public int insertEdcAtnlcMngr(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		String nowDay = DateUtil.getNowDateTime("yyyyMMddHHmmss");
		String nowIp = StringUtil.getClientIpAddr(request);
		String userId = edcAtnlcMngrVO.getLoginUserId();

		/** 예약번호 설정 **/
		if ("01".equals(edcAtnlcMngrVO.getRceptMth())) {
			// 온라인
			edcAtnlcMngrVO.setAtnclNo("O");
		} else if ("02".equals(edcAtnlcMngrVO.getRceptMth())) {
			// 방문접수
			edcAtnlcMngrVO.setAtnclNo("V");
		} else if ("03".equals(edcAtnlcMngrVO.getRceptMth())) {
			// 전화접수
			edcAtnlcMngrVO.setAtnclNo("T");
		}else if ("4".equals(edcAtnlcMngrVO.getRceptMth())) {
			// 온라인/방문접수 혼용
			edcAtnlcMngrVO.setAtnclNo("A");
		}else{
			edcAtnlcMngrVO.setAtnclNo("O");
		}
		
		if( "02".equals(edcAtnlcMngrVO.getUserRceptSttus())) {
			edcAtnlcMngrVO.setWaitAt("Y");
		}

		/** 수강 신청일자 정보 설정 **/
		edcAtnlcMngrVO.setUserTel(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserTel(), "-", ""));
		edcAtnlcMngrVO.setUserMobile(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserMobile(), "-", ""));

		/** 접수년 **/
		edcAtnlcMngrVO.setRceptYear(nowDay.substring(0, 4));
		/** 접수월 **/
		edcAtnlcMngrVO.setRceptMt(nowDay.substring(4, 6));
		/** 접수일 **/
		edcAtnlcMngrVO.setRceptDay(nowDay.substring(6, 8));
		/** 접수날짜 (14자리) **/
		edcAtnlcMngrVO.setRceptDe(nowDay);
		/** 접수시간(6자리) **/
		edcAtnlcMngrVO.setRceptTime(nowDay.substring(8, 14));

		edcAtnlcMngrVO.setFrstRegisterId(userId);
		edcAtnlcMngrVO.setFrstRegisterPnttm(nowDay);
		edcAtnlcMngrVO.setFrstRegisterIp(nowIp);

		edcAtnlcMngrVO.setLastUpdusrPnttm(nowDay);
		edcAtnlcMngrVO.setLastUpdusrIp(nowIp);
		edcAtnlcMngrVO.setLastUpdusrId(userId);

		return (Integer) edcAtnlcMngrDAO.insertEdcAtnlcMngr(edcAtnlcMngrVO);
	}
	
	
	
	
	
	
	
	
	
	

	/**
	 * 수강생 정보 업데이트
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @param edcAtnlcUserVO
	 * @return
	 * @throws Exception
	 */
	public int updateAtnlcMngrSave(HttpServletRequest request
							, EdcAtnlcMngrVO edcAtnlcMngrVO
							, EdcAtnlcUserVO edcAtnlcUserVO
			) throws Exception {
		int rtnInt=0;

		edcAtnlcUserService.updateEdcAtnlcUserInfo(request, edcAtnlcUserVO);

		updateEdcAtnlcMngr(request, edcAtnlcMngrVO);

		return rtnInt;
	}

	/**
	 * 수강생 정보 업데이트
	 */
	@SuppressWarnings("unlikely-arg-type")
  public int updateEdcUser(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO
									, EdcAtnlcUserBndl edcAtnlcUserBndl , EdcLctreVO rtnEdcLctreVO
			) throws Exception{
		int rtnInt = 0;

		try{
			String loginUserId = edcAtnlcMngrVO.getLoginUserId();
			String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();

			String bndlDscntYn = "N";
			int totalPrice = 0;
			int loopCnt = edcAtnlcUserBndl.getBndlUserNm().length;
			int cancelCnt = 0;

			EdcAtnlcUserVO forVO;
			for(int i=0;i<loopCnt;i++){
				forVO = new EdcAtnlcUserVO();
				/** 수강자 정보 저장 **/
				forVO.setAtnclKey(edcAtnlcMngrVO.getAtnclKey());
				forVO.setSn(StringUtil.strToInt(edcAtnlcUserBndl.getBndlSn()[i]));
				forVO.setUserNm(edcAtnlcUserBndl.getBndlUserNm()[i]);
				forVO.setUserBrthdy(StringUtil.strReplaceALL(edcAtnlcUserBndl.getBndlUserBrthdy()[i], "-", ""));
				forVO.setUserSexdstn(edcAtnlcUserBndl.getBndlUserSexdstn()[i]);
				forVO.setTel02(edcAtnlcUserBndl.getBndlTel02()[i]);

				if("Y".equals(rtnEdcLctreVO.getGrpYn())){
					forVO.setEdcNmpr(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[i]));
					forVO.setGrpYn("Y");
				}else{
					forVO.setEdcNmpr(1);
					forVO.setGrpYn("N");
				}

				String[] dscntArr = edcAtnlcUserBndl.getBndlDscntYn()[i].split("\\|");
				if("N".equals(edcAtnlcUserBndl.getBndlDscntYn()[i])){
					forVO.setDscntYn("N");
				}else{
					forVO.setDscntYn("Y");
				}
				forVO.setDscntCode(dscntArr[0]);
				forVO.setDscntPt(StringUtil.strToInt(dscntArr[1]));
				forVO.setDscntNm(dscntArr[2]);

				forVO.setComplAt(edcAtnlcUserBndl.getBndlComplAt()[i]);								// 수료여부(0:수료아님,1:수료)
				forVO.setAtnlcEmail(edcAtnlcUserBndl.getBndlAtnlcEmail()[i]);							// 이메일 주소
				forVO.setApplcntRelate(edcAtnlcUserBndl.getBndlApplcntRelate()[i]);				// 수료 여부

				//if(StringUtil.strToInt(dscntArr[1]) > 0){
				//	forVO.setIndvdlzSetleAmount(rtnEdcLctreVO.getAtnclCt() * (100-StringUtil.strToInt(dscntArr[1])) / 100);		// 수강료 계산
				//}else{
					//forVO.setIndvdlzSetleAmount(edcAtnlcMngrVO.getAtnclCt());
					forVO.setIndvdlzSetleAmount(StringUtil.strToInt(edcAtnlcUserBndl.getBndlIndvdlzSetleAmount()[i]));
				//}
				forVO.setEtcAmount(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEtcAmount()[i]));									// 부대비용 입력
				forVO.setLastUpdusrId(loginUserId);																								// 수정자 아이디

				if(edcAtnlcMngrVO.getUserRceptSttus().equals(edcAtnlcUserBndl.getBndlSttus()[i])){
					// 상태값 같은 경우
					forVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());
				}else{
					// 다른 경우
					forVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());
					/*
					if("01".equals(edcAtnlcMngrVO.getUserRceptSttus())){
						if("02".equals(edcAtnlcUserBndl.getBndlSttus()[i])){
							forVO.setSttus("01");
						}
					}else if("02".equals(edcAtnlcMngrVO.getUserRceptSttus())){
						if("01".equals(edcAtnlcUserBndl.getBndlSttus()[i]) || "00".equals(edcAtnlcUserBndl.getBndlSttus()[i])){
							forVO.setSttus("02");
						}
					}else if("03".equals(edcAtnlcMngrVO.getUserRceptSttus())){
						forVO.setSttus("03");
					}else if("04".equals(edcAtnlcMngrVO.getUserRceptSttus())){
						forVO.setSttus("04");
					}
					*/
				}

				// 모든 신청자 취소인지 확인을 위한 카운트
				if("03".equals(edcAtnlcUserBndl.getBndlSttus()[i]) || "04".equals(edcAtnlcUserBndl.getBndlSttus()[i])){
					cancelCnt++;
				}

				if(edcAtnlcUserBndl.getBndlSchoolGradeYear() != null){
					if(!"".equals(StringUtil.strTrim(edcAtnlcUserBndl.getBndlSchoolGradeYear()[i]))){
						String schoolGrade = edcAtnlcUserBndl.getBndlSchoolGradeYear()[i]+"|"+edcAtnlcUserBndl.getBndlSchoolGradeBan()[i]+"|"+ edcAtnlcUserBndl.getBndlSchoolGradeNo()[i];
						forVO.setSchoolGrade(schoolGrade);
					}
				}

				if(!"".equals(StringUtil.strTrim(edcAtnlcUserBndl.getBndlAtnlcEmailAfter()[i])) && !"".equals(StringUtil.strTrim(edcAtnlcUserBndl.getBndlAtnlcEmailBefore()[i]))){
					String email = StringUtil.strTrim(edcAtnlcUserBndl.getBndlAtnlcEmailBefore()[i])+"@"+StringUtil.strTrim(edcAtnlcUserBndl.getBndlAtnlcEmailAfter()[i]);
					forVO.setAtnlcEmail(email);
				}

				forVO.setZip(edcAtnlcUserBndl.getBndlZip()[i]);
				forVO.setAdres(edcAtnlcUserBndl.getBndlAdres()[i]);
				forVO.setAdresDetail(edcAtnlcUserBndl.getBndlAdresDetail()[i]);
				forVO.setDongCode(edcAtnlcUserBndl.getBndlDongCode()[i]);

				if("".equals(StringUtil.strTrim(edcAtnlcUserBndl.getBndlSn()[i]))){
					forVO.setLctreKey(edcAtnlcMngrVO.getLctreKey());
					edcAtnlcUserService.insertEdcAtnlcUser(request, forVO);
				}else{
					edcAtnlcUserService.updateEdcAtnlcUser(request, forVO);
				}

				if("Y".equals(edcAtnlcUserBndl.getBndlDscntYn())){
					if("N".equals(bndlDscntYn))
						bndlDscntYn = "Y";
				}

				// 결제 총액
				if("Y".equals(rtnEdcLctreVO.getEtcCtYn())){
					totalPrice = totalPrice + forVO.getIndvdlzSetleAmount() + forVO.getEtcAmount();
				}else if("S".equals(rtnEdcLctreVO.getEtcCtYn())){
					totalPrice = totalPrice + forVO.getIndvdlzSetleAmount();
				}else{
					totalPrice = totalPrice + forVO.getIndvdlzSetleAmount();
				}
			}

			// 추가 입력 정보 저장 시작
			ResveAddFieldVO addFieldVO = new ResveAddFieldVO();

			// 삭제 처리 후 재등록
			addFieldVO.setSearchManagerSeq(StringUtil.intToStr(edcAtnlcMngrVO.getAtnclKey()));
			addFieldVO.setSearchMngrSeq(StringUtil.intToStr(edcAtnlcMngrVO.getLctreKey()));
			addFieldVO.setSearchJobSe("EC");
	  		resveAddFieldService.deleteResveInputAddField(addFieldVO);

	  		addFieldVO = new ResveAddFieldVO();
			addFieldVO.setSearchManagerSeq(edcAtnlcMngrVO.getLctreKey()+"");
			addFieldVO.setSearchDeleteAt("N");
			addFieldVO.setSearchJobSe("EC");
	  		List<ResveAddField> resveAddFieldList =   resveAddFieldService.selectResveAddFieldListAll(addFieldVO);

	  		for(ResveAddField forVo : resveAddFieldList){
	  			addFieldVO = new ResveAddFieldVO();

	  			addFieldVO.setManagerSeq(edcAtnlcMngrVO.getAtnclKey());
	  			addFieldVO.setItemSeq(StringUtil.strToInt(request.getParameter("itemSeq"+forVo.getItemSeq())));
	  			addFieldVO.setSubSeq(1);

	  			String inputCn = "";
	  			if("checkbox".equals(forVo.getFieldTy())){
	  				String[] checkStr = request.getParameterValues("inputCn"+forVo.getItemSeq());
	  				if(checkStr != null){
		  				for(String val : checkStr){
		  					inputCn += val+"|";
		  				}
	  				}
	  			}else{
	  				inputCn = request.getParameter("inputCn"+forVo.getItemSeq());
	  			}

	  			addFieldVO.setInputCn(inputCn);
	  			addFieldVO.setJobSe("EC");
	  			addFieldVO.setMngrSeq(edcAtnlcMngrVO.getLctreKey());
	  			addFieldVO.setFrstRegisterId(loginUserId);
	  			addFieldVO.setLastUpdusrId(loginUserId);

	  			// Insert
	  			resveAddFieldService.insertResveInputAddField(request, addFieldVO);
	  		}
	  		// 추가 입력정보 저장 끝

	  		edcAtnlcMngrVO.setRceptNmpr(loopCnt);
			edcAtnlcMngrVO.setDscntYn(bndlDscntYn);
			edcAtnlcMngrVO.setSetleAmount(totalPrice);
			edcAtnlcMngrVO.setLastUpdusrId(loginUserId);

			if(!"".equals(StringUtil.strTrim(edcAtnlcMngrVO.getUserEmailAfter())) && !"".equals(StringUtil.strTrim(edcAtnlcMngrVO.getUserEmailBefore()))){
				String email = StringUtil.strTrim(edcAtnlcMngrVO.getUserEmailBefore())+"@"+StringUtil.strTrim(edcAtnlcMngrVO.getUserEmailAfter());
				edcAtnlcMngrVO.setUserEmail(email);
			}

			if(!"O".equals(StringUtil.strSubString(edcAtnlcMngrVO.getAtnclNo(), 0, 1)) && !"0".equals(edcAtnlcMngrVO.getMberSe()) ){
				String userId = edcAtnlcUserBndl.getBndlTel02()[0]+edcAtnlcUserBndl.getBndlUserBrthdy()[0]+edcAtnlcUserBndl.getBndlUserNm()[0];

				if(!"".equals(StringUtil.strTrim(userId))){
					userId = StringUtil.strReplaceALL(userId, "-", "");
					userId = StringUtil.strReplaceALL(userId, "\\.", "");
				}

				edcAtnlcMngrVO.setUserId(userId);
			}

			// 신청자 정보 업데이트
			rtnInt = updateEdcAtnlcMngr(request, edcAtnlcMngrVO);

			// 취소일때 대기 예약 있는지 확인 후 승인 처리 시작
			if("0".equals(rtnEdcLctreVO.getRceptMth())){
				// 선발 방식이 선착순일 경우만 적용
				boolean checkCancel = false;
				if(loopCnt == cancelCnt){
					checkCancel = true;
				}else if("03".equals(edcAtnlcMngrVO.getUserRceptSttus())){
					checkCancel = true;
				}else if("04".equals(edcAtnlcMngrVO.getUserRceptSttus())){
					checkCancel = true;
				}

				if(checkCancel){
					EdcAtnlcMngrVO waitAtnlcVo = new EdcAtnlcMngrVO();
					waitAtnlcVo.setSearchAtnclKey(edcAtnlcMngrVO.getAtnclKey()+"");
					waitAtnlcVo.setSearchUserRceptSttus("02");
					waitAtnlcVo.setSearchLctreKey(rtnEdcLctreVO.getLctreKey()+"");

					edcAtnlcWaitUpdate(request, waitAtnlcVo, rtnEdcLctreVO);
				}
			}
			// 취소일때 대기 예약 있는지 확인 후 승인 처리 끝

	  		// Action History 저장
	  		StaffActionHistoryVO vo = new StaffActionHistoryVO();
	  		vo.setUserId(loginUserId);
	  		vo.setUserNm(loginUserNm);
	  		vo.setProgrmId("EA01");
	  		vo.setManagerSeq(edcAtnlcMngrVO.getAtnclKey()+"");
	  		vo.setSttusCode("UPDATE");
	  		vo.setDivCode("A01");
	  		vo.setSttusNm("교육강좌 > 강좌 접수관리 > 접수강좌 업데이트 [관리번호 : " +edcAtnlcMngrVO.getAtnclNo() +"]");

	  		staffActionHistoryService.insertStaffActionHistory(request, vo);

		}catch (Exception e) {
			e.printStackTrace();
		}

		return rtnInt;
	}

	/**
	 * 대기 예약 승인으로 변경 처리
	 */
	public int edcAtnlcWaitUpdate(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO, EdcLctreVO edcLctreVO) throws Exception{
		int rtnInt = 0;

		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.edcAtnlcWaitList(edcAtnlcMngrVO);
		int rcritNmpr = edcLctreVO.getRcritNmpr();
		int rceptSttusCnt = edcLctreVO.getSttus01();
		int cntSttus01 = rceptSttusCnt;

		for(EdcAtnlcMngrVO forVo : list){
			cntSttus01 = cntSttus01 + forVo.getCnt();

			// 정원이 승인자보다
			if(cntSttus01 <= rcritNmpr){
				String dateNow = DateUtil.getNowDateTime("yyyyMMddHHmmss");
				forVo.setUserRceptSttus("01");
				forVo.setAtnclKey(forVo.getAtnclKey());
				forVo.setConfmDe(dateNow);

				forVo.setLastUpdusrId("system");
				forVo.setLastUpdusrIp(StringUtil.getClientIpAddr(request));
				forVo.setLastUpdusrPnttm(dateNow);

				// 예약정보 업데이트
				edcAtnlcMngrDAO.updateEdcAtnlcMngrSttus(forVo);

				// 사용자 정보 업데이트
				EdcAtnlcUserVO edcAtnlcUserVO = new EdcAtnlcUserVO();
				edcAtnlcUserVO.setSearchSttus("02");
				edcAtnlcUserVO.setSttus("01");
				edcAtnlcUserVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
				edcAtnlcUserVO.setAtnclKey(forVo.getAtnclKey());
				edcAtnlcUserService.updateEdcAtnlcUserStatus(request, edcAtnlcUserVO);
				
				
				EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
				edcCmmSmsVO.setSearchAtnclKey(forVo.getAtnclKey());
				edcCmmSmsVO.setSearchStepCode("L13");
				edcCmmSmsVO.setJobSe("EC");
				edcCmmSmsVO.setSttusDiv("INSERT");
				edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);

			}
		}

		return rtnInt;
	}

	/**
	 * 대기 예약자 승인으로 변경 처리 서비스
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	public int edcAtnlcWaitUpdate(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		int rtnInt = 0;

		EdcLctreVO edcLctreVO = new EdcLctreVO();
		edcLctreVO.setSearchLctreKey(edcAtnlcMngrVO.getSearchLctreKey());
		EdcLctreVO edcLctreData = edcLctreService.webSelectEdcLctreDetail(request, edcLctreVO);
		int rcritNmpr = 0;
		int rceptSttusCnt = 0;			// 승인인원
		
		String rceptMth = "";
		if("A".equals(edcLctreData.getReceiptMethod())){
			rcritNmpr = edcLctreData.getPriorRceptNmpr();
			rceptSttusCnt = edcLctreData.getSttus01a();
			rceptMth = edcLctreData.getPriorRceptMth(); 
			edcAtnlcMngrVO.setReceiptMethod("A");
		}else{
			rcritNmpr = edcLctreData.getRcritNmpr();
			rceptSttusCnt = edcLctreData.getSttus01b();
			rceptMth = edcLctreData.getRceptMth();
			edcAtnlcMngrVO.setReceiptMethod("BC");
		}
		
		/*
		edcAtnlcMngrVO.setSearchStatus("01");
		EdcAtnlcMngrVO rtnEdcAtnlcMngrVO = edcAtnlcMngrDAO.selectEdcRcritNmprSttus(edcAtnlcMngrVO);
		*/
			
		
		int cntSttus01 = rceptSttusCnt;

		if(!"0".equals(rceptMth)){
			return rtnInt;
		}

		edcAtnlcMngrVO.setSearchAtnclKey("");
		edcAtnlcMngrVO.setSearchUserRceptSttus("02");
		
		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.edcAtnlcWaitList(edcAtnlcMngrVO);

		for(EdcAtnlcMngrVO forVo : list){
			cntSttus01 = cntSttus01 + forVo.getCnt();

			// 정원이 승인자보다
			if(cntSttus01 <= rcritNmpr){
				String dateNow = DateUtil.getNowDateTime("yyyyMMddHHmmss");
				forVo.setUserRceptSttus("01");
				forVo.setAtnclKey(forVo.getAtnclKey());
				forVo.setConfmDe(dateNow);

				forVo.setLastUpdusrId("system");
				forVo.setLastUpdusrIp(StringUtil.getClientIpAddr(request));
				forVo.setLastUpdusrPnttm(dateNow);

				// 예약정보 업데이트
				edcAtnlcMngrDAO.updateEdcAtnlcMngrSttus(forVo);

				// 사용자 정보 업데이트
				EdcAtnlcUserVO edcAtnlcUserVO = new EdcAtnlcUserVO();
				edcAtnlcUserVO.setSearchSttus("02");
				edcAtnlcUserVO.setSttus("01");
				edcAtnlcUserVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
				edcAtnlcUserVO.setAtnclKey(forVo.getAtnclKey());
				edcAtnlcUserService.updateEdcAtnlcUserStatus(request, edcAtnlcUserVO);
				
				
				EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
				edcCmmSmsVO.setSearchAtnclKey(forVo.getAtnclKey());
				edcCmmSmsVO.setSearchStepCode("L13");
				edcCmmSmsVO.setJobSe("EC");
				edcCmmSmsVO.setSttusDiv("INSERT");
				edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);

				// 상태값 변경 이력 저장
				StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
				staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
				staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(edcAtnlcMngrVO.getAtnclKey()));
				staffActionHistoryVO.setSttusCode("01");
				staffActionHistoryVO.setSiteId(edcAtnlcMngrVO.getSiteId());
				staffActionHistoryVO.setDivCode("A01");
				staffActionHistoryVO.setSttusNm("강좌 상태값 자동 승인 처리");
				staffActionHistoryVO.setProgrmId("EA01");
				staffActionHistoryVO.setUserId(edcAtnlcMngrVO.getLoginUserId());
				staffActionHistoryVO.setUserNm(edcAtnlcMngrVO.getLoginUserNm());

				staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);
			}
		}

		return rtnInt;
	}


	/**
	 * 수강관리 을(를) 수정한다.
	 *
	 * @param EdcAtnlcMngrVO
	 * @return int 수강관리의 시퀀스
	 * @throws Exception
	 */
	public int updateEdcAtnlcMngr(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		edcAtnlcMngrVO.setUserBrthdy(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserBrthdy(), "-", ""));
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		if("".equals(StringUtil.strTrim(edcAtnlcMngrVO.getEtcCt()))){
			edcAtnlcMngrVO.setEtcCt("0");
		}

		if(!"01".equals(edcAtnlcMngrVO.getRceptMth())){
			if("02".equals(edcAtnlcMngrVO.getRceptMt())){
				edcAtnlcMngrVO.setSearchRceptMthChange("02");
			}else if("03".equals(edcAtnlcMngrVO.getRceptMt())){
				edcAtnlcMngrVO.setSearchRceptMthChange("03");
			}
		}

		if("01".equals(edcAtnlcMngrVO.getUserRceptSttus())){
			edcAtnlcMngrVO.setSearchLctreKey(edcAtnlcMngrVO.getLctreKey()+"");
			edcAtnlcMngrVO.setSearchAtnclKey(edcAtnlcMngrVO.getAtnclKey()+"");			
			EdcAtnlcMngr edcAtnlcMngr =  edcAtnlcMngrService.selectEdcAtnlcMngrData(request, edcAtnlcMngrVO);
			System.out.println(edcAtnlcMngr.getUserRceptSttus() + "@@@@@@@@@@@@@@@@@@@@@@@@#");
			if(edcAtnlcMngr.getUserRceptSttus().equals("02")){
				EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
				edcCmmSmsVO.setSearchAtnclKey(edcAtnlcMngr.getAtnclKey());
				edcCmmSmsVO.setSearchStepCode("L13");
				edcCmmSmsVO.setJobSe("EC");
				edcCmmSmsVO.setSttusDiv("INSERT");
				edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);
			}
			edcAtnlcMngrVO.setConfmDe(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		}else{
			edcAtnlcMngrVO.setConfmDe(" ");
		}

		return (Integer) edcAtnlcMngrDAO.updateEdcAtnlcMngr(edcAtnlcMngrVO);
	}

	/**
	 * 수강 신청 취소
	 */
	@Override
	public int updateEdcAtnlcMngrCancel(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO, EdcAtnlcUserVO edcAtnlcUserVO) throws Exception{
		int rtnInt = 0;
 		String loginUserId = edcAtnlcMngrVO.getLoginUserId();
 		String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();

 		try{
 			// 수강 취소
 	  		edcAtnlcMngrVO.setUserRceptSttus("03");
 	  		edcAtnlcMngrService.updateEdcAtnlcMngrSttus(request, edcAtnlcMngrVO);

 	  		edcAtnlcUserVO.setSttus("03");
 	  		edcAtnlcUserService.updateEdcAtnlcUserStatus(request, edcAtnlcUserVO);

			// 문자 전송
			// SMS 발송 및 SMS History 저장
			EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
			edcCmmSmsVO.setSearchAtnclKey(edcAtnlcMngrVO.getAtnclKey());
			edcCmmSmsVO.setSearchStepCode("L02");
			edcCmmSmsVO.setJobSe("EC");

			edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);

			// 상태값 변경 이력 저장
			StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
			staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
			staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(edcAtnlcMngrVO.getAtnclKey()));
			staffActionHistoryVO.setSttusCode(edcAtnlcMngrVO.getUserRceptSttus());
			staffActionHistoryVO.setSiteId(edcAtnlcMngrVO.getSiteId());
			staffActionHistoryVO.setDivCode("A01");
			staffActionHistoryVO.setSttusNm("강좌 취소");
			staffActionHistoryVO.setProgrmId("EA01");
			staffActionHistoryVO.setUserId(loginUserId);
			staffActionHistoryVO.setUserNm(loginUserNm);

			staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);

			rtnInt = 1;
 		}catch(Exception e){
 			rtnInt = -1;
 		}

		return rtnInt;
	}


	/**
	 * 수강 신청 취소(환불요청)
	 */
	@Override
	public int updateEdcAtnlcMngrCancelRefund(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO, EdcAtnlcUserVO edcAtnlcUserVO) throws Exception{
		int rtnInt = 0;
 		String loginUserId = edcAtnlcMngrVO.getLoginUserId();
 		String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();

 		try{
 			// 수강 취소
 	  		edcAtnlcMngrVO.setUserRceptSttus("03");
 	  		edcAtnlcMngrVO.setSetleSttus("02");
 	  		edcAtnlcMngrService.updateEdcAtnlcMngrSttus(request, edcAtnlcMngrVO);

 	  		edcAtnlcUserVO.setSttus("03");
 	  		edcAtnlcUserVO.setLoginUserId(loginUserId);
 	  		edcAtnlcUserVO.setLoginUserNm(loginUserNm);
 	  		edcAtnlcUserVO.setAtnclKeyStr(StringUtil.intToStr(edcAtnlcMngrVO.getAtnclKey()));
 	  		edcAtnlcUserService.updateEdcAtnlcUserStatusIn(request, edcAtnlcUserVO);

			// 문자 전송
			// SMS 발송 및 SMS History 저장
			EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
			edcCmmSmsVO.setSearchAtnclKey(edcAtnlcMngrVO.getAtnclKey());
			edcCmmSmsVO.setSearchStepCode("L07");
			edcCmmSmsVO.setJobSe("EC");

			edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);

			// 상태값 변경 이력 저장
			StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
			staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
			staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(edcAtnlcMngrVO.getAtnclKey()));
			staffActionHistoryVO.setSttusCode(edcAtnlcMngrVO.getUserRceptSttus());
			staffActionHistoryVO.setSiteId(edcAtnlcMngrVO.getSiteId());
			staffActionHistoryVO.setDivCode("A01");
			staffActionHistoryVO.setSttusNm("교육/강좌 환불요청");
			staffActionHistoryVO.setProgrmId("EA01");
			staffActionHistoryVO.setUserId(loginUserId);
			staffActionHistoryVO.setUserNm(loginUserNm);

			staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);

			rtnInt = 1;
 		}catch(Exception e){
 			rtnInt = -1;
 		}

		return rtnInt;
	}


	/**
	 * 수강관리 상태값 업데이트
	 *
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	public int updateEdcAtnlcMngrSttus(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		edcAtnlcMngrVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		return (Integer) edcAtnlcMngrDAO.updateEdcAtnlcMngrSttus(edcAtnlcMngrVO);
	}

	/**
	 * 상태값 변경 처리 IN 조건
	 */
	public int updateEdcAtnlcMngrSttusIn(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		edcAtnlcMngrVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		return (Integer) edcAtnlcMngrDAO.updateEdcAtnlcMngrSttusIn(edcAtnlcMngrVO);
	}

	/**
	 * 기수 단위 상태값 업데이트 (승인 ==> 기타 상태로)
	 */
	public int updateEdcAtnlcMngrSemstrSttusIn(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		edcAtnlcMngrVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		return (Integer) edcAtnlcMngrDAO.updateEdcAtnlcMngrSemstrSttusIn(edcAtnlcMngrVO);
	}

	/**
	 * 상태값 변경 시 승인인 경우와 아닌경우 처리 로직 분기 처리
	 * @param request
	 * @return
	 */
	public int updateEdcAtnlcSttusGubun(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		int rtnInt = 0;

		edcAtnlcMngrVO.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));

		edcAtnlcMngrVO.setSearchLctreKey(edcAtnlcMngrVO.getLctreKey()+"");
		edcAtnlcMngrVO.setSearchAtnclKey(edcAtnlcMngrVO.getAtnclKeyStr());
		EdcAtnlcMngr edcAtnlcMngr =  edcAtnlcMngrService.selectEdcAtnlcMngrData(request, edcAtnlcMngrVO);
		
		if("01".equals(edcAtnlcMngrVO.getUserRceptSttus())){
			// 승인 처리 시
			edcAtnlcMngrVO.setConfmDe(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
			rtnInt = updateEdcAtnlcSttus01(request, edcAtnlcMngrVO);
			
			if(edcAtnlcMngr.getUserRceptSttus().equals("02")){
				EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
				edcCmmSmsVO.setSearchAtnclKey(edcAtnlcMngr.getAtnclKey());
				edcCmmSmsVO.setSearchStepCode("L13");
				edcCmmSmsVO.setJobSe("EC");
				edcCmmSmsVO.setSttusDiv("INSERT");
				edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);
			}
			
		}else if("02".equals(edcAtnlcMngrVO.getUserRceptSttus())){

			edcAtnlcMngrVO.setConfmDe("");
			rtnInt = updateEdcAtnlcAllSttus(request, edcAtnlcMngrVO);

			// 대기예약으로 변경시 마지막 순번으로 업데이트 시킴
			if(edcAtnlcMngrVO.getAtnclKeyStr() != null){
				int lctreKey = edcAtnlcMngrVO.getLctreKey();
				String[] atnclArr = edcAtnlcMngrVO.getAtnclKeyStr().split("\\,");
				int loopCnt = atnclArr.length;

				for(int i=0;i<loopCnt;i++){
					// 대기예약으로 업데이트 시킴
					EdcAtnlcUserVO edcAtnlcUserVO = new EdcAtnlcUserVO();
					edcAtnlcUserVO.setLctreKey(lctreKey);

					int waitInt = edcAtnlcUserService.selectEdcAtnlcUserWaitCnt(edcAtnlcUserVO);
					edcAtnlcUserVO.setWaitSn(waitInt);
					edcAtnlcUserVO.setAtnclKey(StringUtil.strToInt(atnclArr[i]));
					edcAtnlcUserVO.setLoginUserId(edcAtnlcMngrVO.getLoginUserId());
					edcAtnlcUserService.updateEdcAtnlcUserWaitSn(request, edcAtnlcUserVO);
				}
			}

		}else if("03".equals(edcAtnlcMngrVO.getUserRceptSttus()) || "04".equals(edcAtnlcMngrVO.getUserRceptSttus())){
			edcAtnlcMngrVO.setConfmDe("");
			rtnInt = updateEdcAtnlcAllSttus(request, edcAtnlcMngrVO);

			// 선착순일 경우 대기자 승인 처리
			rtnInt = edcAtnlcWaitUpdate(request, edcAtnlcMngrVO);
		}else{
			// 그외 경우 처리
			edcAtnlcMngrVO.setConfmDe("");
			rtnInt = updateEdcAtnlcAllSttus(request, edcAtnlcMngrVO);
		}

		return rtnInt;
	}

	/**
	 * 상태값 승인 처리 로직
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	@Override
	public int updateEdcAtnlcAllSttus01(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		int intRtn = 0;
		String loginUserId = edcAtnlcMngrVO.getLoginUserId();
		String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();

		try{
			// 강좌 정보 조회 (정원 조회)
			EdcLctreVO edcLctreVO = new EdcLctreVO();
			edcLctreVO.setSearchLctreKey(edcAtnlcMngrVO.getLctreKey()+"");
			EdcLctre edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);

			// 변경 대상 수강자 상태값 조회
			edcAtnlcMngrVO.getSearchLctreKeyIn();
			List<EdcAtnlcMngrVO> edcAtnlcMngrList = edcAtnlcMngrService.edcAtnlcMngrMngUserList(request, edcAtnlcMngrVO);

			// 현재 모집인원 조회
			edcAtnlcMngrVO.setSearchLctreKey(edcAtnlcMngrVO.getLctreKey()+"");
			EdcAtnlcMngrVO edcAtnlcSum = edcAtnlcMngrService.lctreAtnlcCount(request, edcAtnlcMngrVO);

			int rcripNmpr = edcLctre.getRcritNmpr();					// 모집 정원
			int sttusNmpr = edcAtnlcSum.getSttus01();				// 모집인원중 승인 처리

			// 정원 비교
			for(EdcAtnlcMngrVO forVo : edcAtnlcMngrList){
				if(!"01".equals(forVo.getSttus())){
					if(rcripNmpr > sttusNmpr){
						// 수강 신청건 상태 변경
						edcAtnlcMngrVO.setAtnclKeyStr(forVo.getAtnclKey()+"");
						intRtn = edcAtnlcMngrService.updateEdcAtnlcMngrSttusIn(request, edcAtnlcMngrVO);

						// 실수강자 정보 업데이트
						EdcAtnlcUserVO edcAtnlcUserVO = new EdcAtnlcUserVO();
						edcAtnlcUserVO.setAtnclKeyStr(edcAtnlcMngrVO.getAtnclKeyStr());
						edcAtnlcUserVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());
						edcAtnlcUserService.updateEdcAtnlcUserStatusIn(request, edcAtnlcUserVO);

						// 히스토리 저장   // USER_RCEPT_STTUS
						EdcCodeVO edcCodeVO = new EdcCodeVO();
						edcCodeVO.setType("USER_RCEPT_STTUS");
						edcCodeVO.setCode(edcAtnlcMngrVO.getUserRceptSttus());
						EdcCode edcCodeData = edcCodeService.selectEdcCodeData(request, edcCodeVO);

						StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
						staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
						staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(forVo.getAtnclKey()));
						staffActionHistoryVO.setSttusCode(edcAtnlcMngrVO.getUserRceptSttus());
						staffActionHistoryVO.setDivCode("A01");
						staffActionHistoryVO.setUserId(loginUserId);
						staffActionHistoryVO.setSttusNm("상태값 변경 처리("+edcCodeData.getCodeNm()+")");
						staffActionHistoryVO.setProgrmId("EA01");
						staffActionHistoryVO.setUserNm(loginUserNm);

						staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);

						if("Y".equals(edcAtnlcMngrVO.getSmsYn())){
							// 문자 메시지 전송
							// SMS 발송 및 SMS History 저장
//							EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
//							edcCmmSmsVO.setSearchAtnclKey(rtnInt);
//
//							if("0".equals(rtnEdcVO.getRceptMth())){
//								// 선착순일때
//								if("01".equals(rtnEdcVO.getSttus())){
//										// 당첨
//										edcCmmSmsVO.setSearchStepCode("L01");
//								}else if("00".equals(rtnEdcVO.getSttus())){
//										// 대기
//										edcCmmSmsVO.setSearchStepCode("L11");
//								}
//							}else{
//								// 그외 경우 (추첨, 승인, 관리자 선정)
//								edcCmmSmsVO.setSearchStepCode("L12");
//							}
//
//							edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);
						}


					}else{
						intRtn  = -9999;
					}

					sttusNmpr++;

				}else{
					intRtn = -9999;
				}
			}

		}catch(Exception e){
			egovLogger.error("EdcAtnlcMngrServiceImpl.updateEdcAtnlcAllSttus : ", e);
		}

		return intRtn;
	}

	@Override
	public int updateEdcAtnlcSttus01(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		int intRtn = 0;
		String loginUserId = edcAtnlcMngrVO.getLoginUserId();
		String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();

		try{
			// 변경 대상 수강자 상태값 조회
			edcAtnlcMngrVO.getSearchLctreKeyIn();
			List<EdcAtnlcMngrVO> edcAtnlcMngrList = edcAtnlcMngrService.edcAtnlcMngrMngUserList(request, edcAtnlcMngrVO);

			// 정원 비교
			for(EdcAtnlcMngrVO forVo : edcAtnlcMngrList){
				// 수강 신청건 상태 변경
				edcAtnlcMngrVO.setAtnclKeyStr(forVo.getAtnclKey()+"");
				intRtn = edcAtnlcMngrService.updateEdcAtnlcMngrSttusIn(request, edcAtnlcMngrVO);

				// 실수강자 정보 업데이트
				EdcAtnlcUserVO edcAtnlcUserVO = new EdcAtnlcUserVO();
				edcAtnlcUserVO.setAtnclKeyStr(edcAtnlcMngrVO.getAtnclKeyStr());
				edcAtnlcUserVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());
				edcAtnlcUserService.updateEdcAtnlcUserStatusIn(request, edcAtnlcUserVO);

				StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
				staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
				staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(forVo.getAtnclKey()));
				staffActionHistoryVO.setSttusCode(edcAtnlcMngrVO.getUserRceptSttus());
				staffActionHistoryVO.setDivCode("A01");
				staffActionHistoryVO.setUserId(loginUserId);
				staffActionHistoryVO.setSttusNm("상태값 변경 처리(승인처리)");
				staffActionHistoryVO.setProgrmId("EA01");
				staffActionHistoryVO.setUserNm(loginUserNm);

				staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);
			}

		}catch(Exception e){
			egovLogger.error("EdcAtnlcMngrServiceImpl.updateEdcAtnlcSttus01 : ", e);
		}

		return intRtn;
	}


	/**
	 * 수강 관리 상태값 업데이트, 실수강자 정보 업데이트 (그외 경우)
	 *
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	@Override
	public int updateEdcAtnlcAllSttus(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		int intRtn = 0;
		String loginUserId 		=  edcAtnlcMngrVO.getLoginUserId();
		String loginUserNm 	=  edcAtnlcMngrVO.getLoginUserNm();

		try{
				// 수강 신청건 상태 변경
				intRtn = edcAtnlcMngrService.updateEdcAtnlcMngrSttusIn(request, edcAtnlcMngrVO);

				// 실수강자 정보 업데이트
				EdcAtnlcUserVO edcAtnlcUserVO = new EdcAtnlcUserVO();
				edcAtnlcUserVO.setAtnclKeyStr(edcAtnlcMngrVO.getAtnclKeyStr());
				edcAtnlcUserVO.setSttus(edcAtnlcMngrVO.getUserRceptSttus());

				edcAtnlcUserService.updateEdcAtnlcUserStatusIn(request, edcAtnlcUserVO);

				String[] arrAtnclKey = edcAtnlcMngrVO.getAtnclKeyStr().split("\\,");

				// 상태값 변경 이력 저장
				// 히스토리 저장   // USER_RCEPT_STTUS
				EdcCodeVO edcCodeVO = new EdcCodeVO();
				edcCodeVO.setType("USER_RCEPT_STTUS");
				edcCodeVO.setCode(edcAtnlcMngrVO.getUserRceptSttus());
				EdcCode edcCodeData = edcCodeService.selectEdcCodeData(request, edcCodeVO);

				StaffActionHistoryVO staffActionHistoryVO;

				for (int i = 0; i < arrAtnclKey.length; i++) {
					staffActionHistoryVO = new StaffActionHistoryVO();
					staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
					staffActionHistoryVO.setManagerSeq(arrAtnclKey[i]);
					staffActionHistoryVO.setSttusCode(edcAtnlcMngrVO.getUserRceptSttus());
					staffActionHistoryVO.setDivCode("A01");
					staffActionHistoryVO.setUserId(loginUserId);
					staffActionHistoryVO.setSttusNm("상태값 변경 처리("+edcCodeData.getCodeNm()+")");
					staffActionHistoryVO.setProgrmId("EA01");
					staffActionHistoryVO.setUserNm(loginUserNm);

					staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);
				}

		}catch(Exception e){
			egovLogger.error("EdcAtnlcMngrServiceImpl.updateEdcAtnlcAllSttus : ", e);
		}

		return intRtn;
	}

	/**
	 * 수강관리 파일을 업로드 한다. (별도 객체생성 권장)
	 *
	 * @param EdcAtnlcMngrVO
	 * @throws Exception
	 */
	public void uploadEdcAtnlcMngr(MultipartHttpServletRequest multiPartRequest, HttpServletRequest request,
			EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {


	}

	/**
	 * 수강관리 의 유효성을 검증한다.
	 *
	 * @param EdcAtnlcMngrVO
	 * @return ResponseJSON 결과코드, 메세지등을 제공
	 * @throws Exception
	 */
	public ResponseJSON validateEdcAtnlcMngr(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		ResponseJSON result = new ResponseJSON();
		result.setMsg("TEST");
		result.setResult(0);

		return result;
	}

	public void deleteEdcAtnlcMngr(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		edcAtnlcMngrDAO.deleteEdcAtnlcMngr(edcAtnlcMngrVO);
	}

	/**
	 * 수강관리 을(를) 전체 조회한다.
	 *
	 * @param EdcAtnlcMngrVO
	 * @return EdcAtnlcMngr 수강관리 의 전체결과
	 * @throws Exception
	 */
	public List<EdcAtnlcMngr> selectEdcAtnlcMngrListAll(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		List<EdcAtnlcMngr> list = edcAtnlcMngrDAO.selectEdcAtnlcMngrListAll(edcAtnlcMngrVO);
		return list;
	}

	/**
	 * 수강관리 > 접수 관리 목록 Count
	 *
	 * @param edcAtnlcLctreVO
	 * @return
	 * @throws Exception
	 */
	public int selectEdcLctreAtnlcCnt(EdcAtnlcLctreVO edcAtnlcLctreVO) throws Exception {
		return (Integer) edcAtnlcMngrDAO.selectEdcLctreAtnlcCnt(edcAtnlcLctreVO);
	}

	/**
	 * 수강관리 > 접수 관리 목록 조회(사용자 홈페이지 조회)
	 *
	 * @param edcAtnlcLctreVO
	 * @return
	 * @throws Exception
	 */
	public List<EdcAtnlcLctreVO> webSelectEdcLctreAtnlcList(HttpServletRequest request, EdcAtnlcLctreVO edcAtnlcLctreVO) throws Exception {
		List<EdcAtnlcLctreVO> list = edcAtnlcMngrDAO.selectEdcLctreAtnlcList(edcAtnlcLctreVO);

		return list;
	}

	public List<EdcAtnlcLctreVO> selectEdcLctreAtnlcList(HttpServletRequest request, EdcAtnlcLctreVO edcAtnlcLctreVO, List<CodeCmmVO> insttList) throws Exception {
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		// 기관 목록 리스트 HashMap 생성
		HashMap<String, String> insttMap = new HashMap<String, String>();
		for (CodeCmmVO forVo : insttList) {
			insttMap.put(forVo.getCodeValue(), forVo.getCodeNm());
		}

		// 코드값 HashMap 생성
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		cmmVo.setSearchCodeType("'TCH_LEC_DIV1', 'USE_AT', 'RCEPT_STTUS', 'LEC_TYPE', 'TCH_LEC_DIV1', 'LEC_APP_TYPE' ");
		cmmVo.setSearchUseAt("Y");
		cmmVo.setSearchDeleteAt("N");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		List<EdcAtnlcLctreVO> list = edcAtnlcMngrDAO.selectEdcLctreAtnlcList(edcAtnlcLctreVO);

		String edcChangeSttus= "";
		// 데이터가공 구간
		for (EdcAtnlcLctreVO forVo : list) {
			// 정원 체크 (상태값 확인)
			if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
				forVo = checkSttus(forVo);
			}

			edcChangeSttus= "";
			if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
				if(StringUtil.strToDouble(forVo.getEdcBgnde()) > 0){
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
	
					forVo.setEdcSttus(edcChangeSttus);
				}
			}
			
			forVo.setEdcDayTime((DateUtil.replaceDayTime(forVo.getEdcDay(), forVo.getEdcTime(), ",", ",")));

			forVo.setUseAt(codeHashMap.get("USE_AT" + forVo.getUseAt()));
			forVo.setRceptSttusNm(codeHashMap.get("RCEPT_STTUS" + forVo.getRceptSttus()));
			forVo.setRceptMth(codeHashMap.get("LEC_APP_TYPE" + forVo.getRceptMth()));
			forVo.setLctreLclas(codeHashMap.get("TCH_LEC_DIV1" + forVo.getLctreLclas()));

			// 기관 한글명
			forVo.setInsttNm(insttMap.get(forVo.getInsttCode() + ""));

			forVo.setPriorRceptBgnde(DateUtil.getDateFormat(forVo.getPriorRceptBgnde(), "yyyy.mm.dd"));
			forVo.setPriorRceptEndde(DateUtil.getDateFormat(forVo.getPriorRceptEndde(), "yyyy.mm.dd"));
			forVo.setRceptBgnde(DateUtil.getDateFormat(forVo.getRceptBgnde(), "yyyy.mm.dd"));
			forVo.setRceptEndde(DateUtil.getDateFormat(forVo.getRceptEndde(), "yyyy.mm.dd"));
			forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy.mm.dd"));
			forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy.mm.dd"));

			forVo.setAtnclCt(StringUtil.getFormatData(forVo.getAtnclCt()));

			if("".equals(StringUtil.strTrim(forVo.getDrwtDe()))){
				forVo.setDrwtDe("추첨전");
			}else{
				forVo.setDrwtDe(DateUtil.getDateFormat(forVo.getDrwtDe(), "yyyy-mm-dd hh:mi"));
			}

			if("N".equals(forVo.getFreeYn())){
				forVo.setAtnclCtStr("무료");
			}else{
				forVo.setAtnclCtStr(forVo.getAtnclCt());
			}

			int sttusAll = StringUtil.strToInt(forVo.getSttus00()) + StringUtil.strToInt(forVo.getSttus01())
					+ StringUtil.strToInt(forVo.getSttus02()) + StringUtil.strToInt(forVo.getSttus03())
					+ StringUtil.strToInt(forVo.getSttus04()) + StringUtil.strToInt(forVo.getSttus09())
					+ StringUtil.strToInt(forVo.getSttus10());

			forVo.setSttus01((StringUtil.strToInt(forVo.getSttus01()) + StringUtil.strToInt(forVo.getSttus09())) + "");
			forVo.setSttus02((StringUtil.strToInt(forVo.getSttus02()) + StringUtil.strToInt(forVo.getSttus10())) + "");

			forVo.setSttus03((StringUtil.strToInt(forVo.getSttus03()) + StringUtil.strToInt(forVo.getSttus04())) + "");
			forVo.setSttusAll(StringUtil.intToStr(sttusAll));


		}

		return list;
	}

	/**
	 * 수강관리 > 접수관리 > 수강생 목록 조회
	 */
	public List<EdcAtnlcMngrVO> edcAtnlcMngrMngUserList(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO)
			throws Exception {
		// 코드값 HashMap 생성
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		// 상태값, 사용유무, 강좌 상태, 결제 상태, 성별, 접수방법, 할인코드
		cmmVo.setSearchCodeType(
				"'USER_RCEPT_STTUS', 'USE_AT', 'RCEPT_STTUS', 'SETLE_STTUS', 'TCH_GENDER', 'APP_METHOD', 'DSCNT_CODE' ");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.selectEdcAtnlcMngrUserList(edcAtnlcMngrVO);

		// 데이터가공 구간
		for (EdcAtnlcMngrVO forVo : list) {
			forVo.setUserRceptSttusNm(codeHashMap.get("USER_RCEPT_STTUS" + forVo.getUserRceptSttus()));
			forVo.setSttusNm(codeHashMap.get("USER_RCEPT_STTUS" + forVo.getSttus()));
			forVo.setSetleSttus(codeHashMap.get("SETLE_STTUS" + forVo.getSetleSttus()));
			forVo.setUserSexdstn(codeHashMap.get("TCH_GENDER" + forVo.getUserSexdstn()));
			forVo.setRceptMth(codeHashMap.get("APP_METHOD" + forVo.getRceptMth()));
			forVo.setDscntCode(codeHashMap.get("DSCNT_CODE" + forVo.getDscntCode()));
			forVo.setUseAt(codeHashMap.get("USE_AT" + forVo.getUseAt()));

			forVo.setFrstRegisterPnttm(DateUtil.getDateFormat(forVo.getFrstRegisterPnttm(), "yyyy.mm.dd hh:mi:ss"));
			forVo.setUserBrthdy(DateUtil.getDateFormat(forVo.getUserBrthdy(), "yyyy-mm-dd"));
		}

		return list;
	}

	/**
	 * 강좌 정보 및 수강생 목록 조회
	 *
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	public HashMap<String, Object> edcAtnlcMngrUserHashMap(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO)
			throws Exception {
		HashMap<String, Object> rtnMap = new HashMap<String, Object>();

		// 코드값 HashMap 생성
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		// 상태값, 사용유무, 강좌 상태, 결제 상태, 성별, 접수방법, 할인코드
		cmmVo.setSearchCodeType(
				"'USER_RCEPT_STTUS', 'USE_AT', 'RCEPT_STTUS', 'SETLE_STTUS', 'TCH_GENDER', 'APP_METHOD', 'DSCNT_CODE' ");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		// 강좌 정보 조회
		EdcLctreVO edcLctreVO = new EdcLctreVO();
		edcLctreVO.setSearchLctreKey(edcAtnlcMngrVO.getSearchLctreKey());
		EdcLctre edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);

		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.selectEdcAtnlcMngrUserList(edcAtnlcMngrVO);

		// 데이터가공 구간
		for (EdcAtnlcMngrVO forVo : list) {
			forVo.setUserRceptSttus(codeHashMap.get("USER_RCEPT_STTUS" + forVo.getUserRceptSttus()));
			forVo.setSttus(codeHashMap.get("USER_RCEPT_STTUS" + forVo.getSttus()));
			forVo.setSetleSttus(codeHashMap.get("SETLE_STTUS" + forVo.getSetleSttus()));
			forVo.setUserSexdstn(codeHashMap.get("TCH_GENDER" + forVo.getUserSexdstn()));
			forVo.setRceptMth(codeHashMap.get("APP_METHOD" + forVo.getRceptMth()));
			forVo.setDscntCode(codeHashMap.get("DSCNT_CODE" + forVo.getDscntCode()));
			forVo.setFrstRegisterPnttm(DateUtil.getDateFormat(forVo.getFrstRegisterPnttm(), "yyyy.mm.dd hh:mi:ss"));

			forVo.setUseAt(codeHashMap.get("USE_AT" + forVo.getUseAt()));

			// 접수유형별 카운트 , 남성, 여성 카운트, 우선접수인원 카운트
		}

		rtnMap.put("list", list);

		return rtnMap;
	}

	/**
	 * 수강인원 및 접수 제한 방법 조회
	 *
	 * @param edcLctreRstrctCndVO
	 * @return
	 * @throws Exception
	 */
	public EdcLctreRstrctCndVO selectEdcLctreRstrctCnd(EdcLctreRstrctCndVO edcLctreRstrctCndVO) throws Exception {
		return edcAtnlcMngrDAO.selectEdcLctreRstrctCnd(edcLctreRstrctCndVO);
	}

	/**
	 * 마이페이지 수강신청 카운트 조회
	 */
	public Integer myReqstLctreListCnt(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		return edcAtnlcMngrDAO.myReqstLctreListCnt(edcAtnlcMngrVO);
	}

	/**
	 * 마이페이지 수강신청 목록 조회
	 */
	public List<EdcAtnlcMngrVO> myReqstLctreList(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		List<EdcAtnlcMngrVO> rtnList = edcAtnlcMngrDAO.myReqstLctreList(edcAtnlcMngrVO);

		for(EdcAtnlcMngrVO forVo : rtnList){
			forVo.setSetleAmountStr(StringUtil.getFormatComma(forVo.getSetleAmount()));
			forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy.mm.dd"));
			forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy.mm.dd"));

			forVo.setEdcBeginTime(StringUtil.strSubString(forVo.getEdcBeginTime(), 0, 2)+":"+StringUtil.strSubString(forVo.getEdcBeginTime(), 2, 4));
			forVo.setEdcEndTime(StringUtil.strSubString(forVo.getEdcEndTime(), 0, 2)+":"+StringUtil.strSubString(forVo.getEdcEndTime(), 2, 4));

			forVo.setRceptDe(DateUtil.getDateFormat(forVo.getRceptDe(), "yyyy.mm.dd"));
			forVo.setRceptTime(StringUtil.strSubString(forVo.getRceptTime(), 0, 2)+":"+StringUtil.strSubString(forVo.getRceptTime(), 2, 4));
			
			forVo.setEdcDayTime((DateUtil.replaceDayTime(forVo.getEdcDay(), forVo.getEdcTime(), ",", ",")));
		}

		return rtnList;
	}

	/**
	 * 강좌 상세 조회
	 */
	public EdcAtnlcMngrVO myPageLctreAtnlcView(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		EdcAtnlcMngrVO rtnVO = edcAtnlcMngrDAO.myPageLctreAtnlcView(edcAtnlcMngrVO);

		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		String stdde = DateUtil.getDateAddDay(-180, "yyyyMMdd");	// 조회 기준일
		stdde = stdde + "0000";

		// 교육 진행 상태 확인
		if(StringUtil.strToDouble(rtnVO.getEdcBgnde()) > 0){
			// 교육 상태값 변경 처리
			if(nowDateInt < StringUtil.strToDouble(rtnVO.getEdcBgnde())){
				rtnVO.setEdcSttus("EW1");
			}
			if(nowDateInt >= StringUtil.strToDouble(rtnVO.getEdcBgnde()) && nowDateInt <= StringUtil.strToDouble(rtnVO.getEdcEndde())){
				rtnVO.setEdcSttus("EP1");
			}
			if(nowDateInt > StringUtil.strToDouble(rtnVO.getEdcEndde())){
				rtnVO.setEdcSttus("EC1");
			}
		}

		// 교육 요일 변환
		rtnVO.setEdcDay(DateUtil.replaceDay(rtnVO.getEdcDay(), ",", ","));

		// 각 일정 요일 한글명 설정
		rtnVO.setEdcBgndeDay(DateUtil.getDateDay(StringUtil.strSubString(rtnVO.getEdcBgnde(), 0, 8), "yyyyMMdd"));		// 교육 시작 요일
		rtnVO.setEdcEnddeDay(DateUtil.getDateDay(StringUtil.strSubString(rtnVO.getEdcEndde(), 0, 8), "yyyyMMdd"));		// 교육 종료 요일

		rtnVO.setSetleAmountStr(StringUtil.getFormatComma(rtnVO.getSetleAmount()));
		rtnVO.setEdcBgnde(DateUtil.getDateFormat(rtnVO.getEdcBgnde(), "yyyy-mm-dd"));
		rtnVO.setEdcEndde(DateUtil.getDateFormat(rtnVO.getEdcEndde(), "yyyy-mm-dd"));

		rtnVO.setEdcBeginTime(StringUtil.strSubString(rtnVO.getEdcBeginTime(), 0, 2)+":"+StringUtil.strSubString(rtnVO.getEdcBeginTime(), 2, 4));
		rtnVO.setEdcEndTime(StringUtil.strSubString(rtnVO.getEdcEndTime(), 0, 2)+":"+StringUtil.strSubString(rtnVO.getEdcEndTime(), 2, 4));
		rtnVO.setRceptDe(DateUtil.getDateFormat(rtnVO.getRceptDe(), "yyyy.mm.dd hh:mi"));

		if(!"".equals(StringUtil.strTrim(rtnVO.getUserBrthdy()))){
			rtnVO.setUserBrthdyYear(StringUtil.strSubString(rtnVO.getUserBrthdy(), 0, 4));
			rtnVO.setUserBrthdyMonth(StringUtil.strSubString(rtnVO.getUserBrthdy(), 4, 6));
			rtnVO.setUserBrthdyDay(StringUtil.strSubString(rtnVO.getUserBrthdy(), 6, 8));

			rtnVO.setUserBrthdy(DateUtil.getDateFormat(rtnVO.getUserBrthdy(), "yyyy-mm-dd"));
		}

		if(!"".equals(StringUtil.strTrim(rtnVO.getSetlePd()))){
			rtnVO.setSetlePdHH(StringUtil.strSubString(rtnVO.getSetlePd(), 8, 10) );
			rtnVO.setSetlePdMM(StringUtil.strSubString(rtnVO.getSetlePd(), 10, 12) );
			rtnVO.setSetlePd(DateUtil.getDateFormat(StringUtil.strSubString(rtnVO.getSetlePd(), 0, 8), "yyyy-mm-dd") );
		}

		rtnVO.setUserTel(StringUtil.getTelNoMask(rtnVO.getUserTel()));
		rtnVO.setUserMobile(StringUtil.getTelNoMask(rtnVO.getUserMobile()));

		if(!"".equals(StringUtil.strTrim(rtnVO.getUserEmail()))){
			String[] emailArr = rtnVO.getUserEmail().split("\\@");

			if(emailArr.length > 1){
				rtnVO.setUserEmailBefore(emailArr[0]);
				rtnVO.setUserEmailAfter(emailArr[1]);
			}
		}

		return rtnVO;
	}

	/**
	 * 수강신청정보 업데이트
	 */
	public EdcAtnlcMngrVO upEdcAtnlcMngr(HttpServletRequest request	, EdcAtnlcMngrVO edcAtnlcMngrVO	, EdcAtnlcUserVO edcAtnlcUserVO	, EdcAtnlcUserBndl edcAtnlcUserBndl ) throws Exception{
		EdcAtnlcMngrVO rtnVO = new EdcAtnlcMngrVO();
		String loginUserId = edcAtnlcMngrVO.getLoginUserId();
		String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();

		String userId = edcAtnlcMngrVO.getLoginUserId();
		String diCode = edcAtnlcMngrVO.getDiCode();
		int rtnInt = edcAtnlcMngrVO.getAtnclKey();

		edcAtnlcMngrVO.setSearchUserId(edcAtnlcMngrVO.getUserId());

		edcAtnlcMngrVO.setSearchAtnclKey(edcAtnlcMngrVO.getAtnclKey()+"");
		EdcAtnlcMngrVO searchRtnVo = new EdcAtnlcMngrVO();
		searchRtnVo = edcAtnlcMngrService.myPageLctreAtnlcView(edcAtnlcMngrVO);

		// 수강 신청 목록이 있는지 확인
		if(searchRtnVo != null){
			if(!userId.equals(searchRtnVo.getUserId())){
				rtnVO.setReturnCode("U01");
				rtnVO.setReturnMsg("로그인 사용자와 수강신청자 아이디가 일치하지 않습니다.");

				return rtnVO;
			}
		}else{
			rtnVO.setReturnCode("U02");
			rtnVO.setReturnMsg("정보가 일치하는 강좌가 존재하지 않습니다.");

			return rtnVO;
		}

		int totalPrice = 0;
		int totalEtcCt = 0;
		int setleAmount = searchRtnVo.getAtnclCt();
		String saleYn = "N";

		if (!"".equals(StringUtil.strTrim(searchRtnVo.getAtnclNo()))) {
			EdcAtnlcUserVO forVO = new EdcAtnlcUserVO();
			forVO.setIndvdlzSetleAmount(setleAmount);

			int loopCnt = edcAtnlcUserBndl.getBndlUserNm().length;

			for(int i=0;i<loopCnt;i++){
					//String[] dscntArr = edcAtnlcUserBndl.getBndlDscntYn()[i].split("\\|");

					forVO = new EdcAtnlcUserVO();
					/** 수강자 정보 저장 **/
					forVO.setAtnclKey(rtnInt);
					forVO.setSn(StringUtil.strToInt(edcAtnlcUserBndl.getBndlSn()[i]));
					forVO.setUserNm(edcAtnlcUserBndl.getBndlUserNm()[i]);
					forVO.setUserBrthdy(StringUtil.strReplaceALL(edcAtnlcUserBndl.getBndlUserBrthdy()[i], "-", ""));
					forVO.setUserSexdstn(edcAtnlcUserBndl.getBndlUserSexdstn()[i]);
					forVO.setTel02(StringUtil.strReplaceALL(edcAtnlcUserBndl.getBndlTel02()[i], "-", ""));
					forVO.setApplcntRelate(edcAtnlcUserBndl.getBndlApplcntRelate()[i]);
					forVO.setAtnlcEmail(edcAtnlcUserBndl.getBndlAtnlcEmailBefore()[i]+"@"+edcAtnlcUserBndl.getBndlAtnlcEmailAfter()[i]);

					//if("N".equals(edcAtnlcUserBndl.getBndlDscntYn()[i])){
					//	forVO.setDscntYn("N");
					//}else{
					//	forVO.setDscntYn("Y");
					//}

					//forVO.setDscntCode(dscntArr[0]);
					//forVO.setDscntPt(StringUtil.strToInt(dscntArr[1]));
					//forVO.setDscntNm(dscntArr[2]);

					//if(StringUtil.strToInt(dscntArr[1]) > 0){
					//	forVO.setIndvdlzSetleAmount(setleAmount * (100-StringUtil.strToInt(dscntArr[1])) / 100);		// 수강료 계산
					//	saleYn = "Y";
					//}else{
					//	forVO.setIndvdlzSetleAmount(edcAtnlcMngrVO.getAtnclCt());
					//}
					//forVO.setEtcAmount(StringUtil.strToInt(edcAtnlcUserBndl.getBndlEtcAmount()[i]));						// 부대비용 입력

					forVO.setZip(edcAtnlcUserBndl.getBndlZip()[i]);
					forVO.setAdres(edcAtnlcUserBndl.getBndlAdres()[i]);
					forVO.setAdresDetail(edcAtnlcUserBndl.getBndlAdresDetail()[i]);
					forVO.setDongCode(edcAtnlcUserBndl.getBndlDongCode()[i]);

					edcAtnlcUserService.updateEdcAtnlcUserInfo(request, forVO);

					// 결제 총액
					totalPrice = totalPrice + forVO.getIndvdlzSetleAmount() + forVO.getEtcAmount();
					totalEtcCt = totalEtcCt + forVO.getEtcAmount();
			}


			/** 예약자 정보 저장 **/
			edcAtnlcMngrVO.setUserTel(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserTel(),"-",""));
			edcAtnlcMngrVO.setUserMobile(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserMobile(),"-",""));
			edcAtnlcMngrVO.setUserBrthdy(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserBrthdy(),"-",""));
			edcAtnlcMngrVO.setDscntYn(saleYn);

			// 정보 업데이트
			edcAtnlcMngrVO.setSetleAmount(totalPrice);
			edcAtnlcMngrVO.setEtcCt(StringUtil.intToStr(totalEtcCt));
			edcAtnlcMngrVO.setRceptNmpr(loopCnt);
			edcAtnlcMngrVO.setUserEmail(edcAtnlcMngrVO.getUserEmailBefore()+"@"+edcAtnlcMngrVO.getUserEmailAfter());

			edcAtnlcMngrDAO.updateEdcAtnlcMngrInfo(request, edcAtnlcMngrVO);

			// 상태값 변경 이력 저장
			StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
			staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
			staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(rtnInt));
			staffActionHistoryVO.setSttusCode(searchRtnVo.getUserRceptSttus());
			staffActionHistoryVO.setDivCode("A01");
			staffActionHistoryVO.setSttusNm("수강자 정보 수정(사용자 수정)");
			staffActionHistoryVO.setProgrmId("EA01");
			staffActionHistoryVO.setUserId(loginUserId);
			staffActionHistoryVO.setUserNm(loginUserNm);

			staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);


		} else{
			rtnVO.setReturnCode("U02");
			rtnVO.setReturnMsg("수강신청 번호가 일치 하지 않습니다.");

		}

		return rtnVO;
	}

	/**
	 * 수강신청자 정보 업데이트
	 */
	public int updateEdcAtnlcMngrInfo( HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		return edcAtnlcMngrService.updateEdcAtnlcMngrInfo(request, edcAtnlcMngrVO);
	}

	@Override
	public void updateEdcAtnlcMngrOptn(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		edcAtnlcMngrVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));
		edcAtnlcMngrDAO.updateEdcAtnlcMngrOptn(request, edcAtnlcMngrVO);
	}

	/**
	 * 결제 완료 처리
	 */
	@Override
	public void updateEdcAtnlcMngrSetleSttus(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception {
		edcAtnlcMngrVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));
		edcAtnlcMngrDAO.updateEdcAtnlcMngrSetleSttus(request, edcAtnlcMngrVO);
	}

	/**
	 * 결제완료, 문자전송, 상태값 변경이력 저장
	 */
	@Override
	public String updateEdcAtnlcMngrPay(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		String rtnStr = "S";
		String loginUserId = edcAtnlcMngrVO.getLoginUserId();
		String loginUserNm = edcAtnlcMngrVO.getLoginUserNm();

		try{
				// 결제 완료 처리
				updateEdcAtnlcMngrSetleSttus(request, edcAtnlcMngrVO);

				// 문자 메시지 전송
				EdcCmmSmsVO edcCmmSmsVO = new EdcCmmSmsVO();
				edcCmmSmsVO.setSearchAtnclKey(edcAtnlcMngrVO.getAtnclKey());
				edcCmmSmsVO.setSearchStepCode("L05");
				edcCmmSmsVO.setJobSe("EC");
				edcCmmSmsVO.setSiteId(edcAtnlcMngrVO.getSiteId());
				edcCmmSmsService.sendEdcSms(request, edcCmmSmsVO);

				// 상태값 변경 이력 저장
				StaffActionHistoryVO staffActionHistoryVO = new StaffActionHistoryVO();
				staffActionHistoryVO.setSttusChangeDe(DateUtil.getCurrentTimeStamp());
				staffActionHistoryVO.setManagerSeq(StringUtil.intToStr(edcAtnlcMngrVO.getAtnclKey()));
				staffActionHistoryVO.setSttusCode("01");
				staffActionHistoryVO.setSiteId(edcAtnlcMngrVO.getSiteId());
				staffActionHistoryVO.setDivCode("A01");
				staffActionHistoryVO.setSttusNm("결제 완료");
				staffActionHistoryVO.setProgrmId("EA01");
				staffActionHistoryVO.setUserId(loginUserId);
				staffActionHistoryVO.setUserNm(loginUserNm);

				staffActionHistoryService.insertStaffActionHistory(request, staffActionHistoryVO);

		}catch (Exception e) {
			rtnStr = "F";
			egovLogger.error("[결제처리 오류] updateEdcAtnlcMngrPay==>", e.toString());
		}

		return rtnStr;
	}

	/**
	 * 추첨 결과 홈페이지 반영
	 */
	public int updateAtnlcMngConfmHmpgReflctAt(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		edcAtnlcMngrVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		return edcAtnlcMngrDAO.updateAtnlcMngConfmHmpgReflctAt(edcAtnlcMngrVO);
	}

	/**
	 * 추첨 결과 홈페이지 반영 및 문자 메시지 전송
	 */
	public int homePageReflct(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{

		return 0;
	}

	/**
	 * 강좌별 접수 카운트
	 * @param request
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	public EdcAtnlcMngrVO lctreAtnlcCount(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		EdcAtnlcMngrVO edcAtnlcMngr= edcAtnlcMngrDAO.lctreAtnlcCount(edcAtnlcMngrVO);

		return edcAtnlcMngr;
	}

	/**
	 * 기수별 수강 신청 건수
	 */
	public List<EdcAtnlcMngrVO> semstrCodeCount(EdcAtnlcMngrVO edcAtnlcMngrVO)  throws Exception{
		List<EdcAtnlcMngrVO> edcAtnlcMngrList = edcAtnlcMngrDAO.semstrCodeCount(edcAtnlcMngrVO);

		return edcAtnlcMngrList;
	}

	/**
	 * 접수 상태 체크 (기간 확인 및 정원 확인)
	 * @param forVo
	 * @return
	 * @throws Exception
	 */
	public EdcAtnlcLctreVO checkSttus(EdcAtnlcLctreVO forVo) throws Exception{
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
			if(nowDateInt >= StringUtil.strToDouble(forVo.getPriorRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(forVo.getPriorRceptEndde())){
				changeSttus = "PR1"; 										// 모집 중

				// 정원 체크
				String checkSttus = checkRcritNmpr(forVo, "PR1");
				if(!"".equals(StringUtil.strTrim(checkSttus))){
					changeSttus = checkSttus;
				}
			}
		}

		if(!"PR1".equals(changeSttus)){
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
		}

		// 추가 접수 기간 ★★★
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
		if(!"Y".equals(forVo.getCancelYn()) && "Y".equals(forVo.getRceptSttusAutoAt()) ){
			if(StringUtil.strToDouble(forVo.getEdcBgnde()) > 0){
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
	public String checkRcritNmpr(EdcAtnlcLctreVO edcAtnlcLctreVO, String checkGubun) throws Exception{
		String rtnStr = "";

		int rcritNmpr	= 0;
  		// 신청자 카운트 (취소 제외)
  		int rcritCnt 		= StringUtil.strToInt(edcAtnlcLctreVO.getSttus00()) + StringUtil.strToInt(edcAtnlcLctreVO.getSttus01())
  							+ StringUtil.strToInt(edcAtnlcLctreVO.getSttus02()) + StringUtil.strToInt(edcAtnlcLctreVO.getSttus09()) + StringUtil.strToInt(edcAtnlcLctreVO.getSttus10());

  		if("F".equals(checkGubun)){
  				// 우선접수인원 정원
  				rcritNmpr	= 	edcAtnlcLctreVO.getPriorRceptNmpr();
  		}else{
		  		// 정원 정보 (전체 정원)
		  		rcritNmpr	= 	edcAtnlcLctreVO.getRcritNmpr() + edcAtnlcLctreVO.getWaitNmpr();
  		}

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
	 * 강좌 정원 체크 후 상태값 업데이트
	 * @param lcterKey
	 * @return
	 * @throws Exception
	 */
	public String checkNmpr(HttpServletRequest request, int lcterKey) throws Exception{
		String rtnStr = "F";

		try{
				String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
				double nowDateInt = StringUtil.strToDouble(nowDate);
				String changeSttus = "";

				// 강좌 정원 조회 및 예약 인원 확인
				EdcAtnlcMngrVO edcAtnlcMngrVO = edcAtnlcMngrDAO.checkNmpr(lcterKey);

				if(edcAtnlcMngrVO == null){
					rtnStr = "S";
					return rtnStr;
				}


				int totNumber =  edcAtnlcMngrVO.getRcritNmpr() + edcAtnlcMngrVO.getWaitNmpr();
				changeSttus = edcAtnlcMngrVO.getRceptSttus();

				if(totNumber  > edcAtnlcMngrVO.getCnt() ){
						// 접수 상태로 변경 처리
						// 접수 일자 확인
						if(!"".equals(StringUtil.strTrim(edcAtnlcMngrVO.getPriorRceptBgnde())) && !"".equals(StringUtil.strTrim(edcAtnlcMngrVO.getPriorRceptEndde())) ){
							if(nowDateInt >= StringUtil.strToDouble(edcAtnlcMngrVO.getPriorRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcAtnlcMngrVO.getPriorRceptEndde())){
								changeSttus = "PR1"; 					// 모집 중
							}
						}

						// 접수기간
						if(nowDateInt >= StringUtil.strToDouble(edcAtnlcMngrVO.getRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcAtnlcMngrVO.getRceptEndde())){
									changeSttus = "RP1"; 				// 모집 중
						}

						// 추가 접수 기간
						if(!"RP1".equals(changeSttus)){
							if(!"".equals(StringUtil.strTrim(edcAtnlcMngrVO.getAditRceptBgnde())) && !"".equals(StringUtil.strTrim(edcAtnlcMngrVO.getAditRceptEndde())) ){
								if(nowDateInt >= StringUtil.strToDouble(edcAtnlcMngrVO.getAditRceptBgnde()) && nowDateInt <= StringUtil.strToDouble(edcAtnlcMngrVO.getAditRceptEndde())){
									changeSttus = "AR1"; 				// 모집 중
								}
							}
						}
				}

				if(!changeSttus.equals(edcAtnlcMngrVO.getRceptSttus())){
					// 상태값이 다른 경우 강좌 상태값 업데이트 처리
					EdcLctreVO edcLctreVO = new EdcLctreVO();
					edcLctreVO.setRceptSttus(changeSttus);
					edcLctreVO.setSearchLctreKey(lcterKey+"");

					edcLctreService.updateEdcLctreSttus(request, edcLctreVO);
				}

				rtnStr = "S";
		}catch(Exception e){
			rtnStr = "F";
		}

		return rtnStr;
	}

	/**
	 * 강좌 수강제한조건 체크 및 등록 수정
	 */
	public synchronized EdcLctreVO resveProcess(
					HttpServletRequest request
					, EdcAtnlcMngrVO edcAtnlcMngrVO
					, EdcAtnlcUserVO edcAtnlcUserVO
					, EdcAtnlcUserBndl edcAtnlcUserBndl
					, EdcLctreVO edcLctreVO
					, String userId
					, String siteId
					) throws Exception{

		EdcLctreVO rtnVO = new EdcLctreVO();
		String loginUserId = edcAtnlcMngrVO.getLoginUserId();

		if ("".equals(StringUtil.strTrim(edcAtnlcMngrVO.getAtnclNo()))) {
			rtnVO = checkBooking(request, edcLctreVO, edcAtnlcMngrVO, edcAtnlcUserVO, edcAtnlcUserBndl, userId, siteId);
    	}else{
    		rtnVO.setReturnMsg("등록 할수 없는 상태 입니다.");
    		rtnVO.setReturnCode("C20");

    		return rtnVO;
    	}

		if("".equals(StringUtil.strTrim(userId))){
			userId = StringUtil.strReplaceALL(edcAtnlcUserBndl.getBndlTel02()[0], "-", "");
			userId = StringUtil.strReplaceALL(edcAtnlcUserBndl.getBndlTel02()[0], "\\.", "");
			userId = userId + edcAtnlcUserBndl.getBndlUserBrthdy()[0];
			edcAtnlcMngrVO.setDiCode(userId);
		}

		int  atnclKey = 0;
    	if(rtnVO.getCheckBooking()){
    		edcAtnlcMngrVO.setUserRceptSttus(rtnVO.getSttus());
    		edcAtnlcMngrVO.setUserId(userId);
    		edcAtnlcMngrVO.setDiCode(edcAtnlcMngrVO.getDiCode());

       		edcAtnlcUserVO.setFrstRegisterId(loginUserId);
    		edcAtnlcUserVO.setSttus(rtnVO.getSttus());
    		atnclKey = insUpEdcAtnlcMngrStaff(request, edcAtnlcMngrVO, edcAtnlcUserVO, edcAtnlcUserBndl, rtnVO);

    		rtnVO.setAtnclKey(atnclKey);

	   		// 정원 마감여부 확인
      		int rcritNmpr	= 	rtnVO.getRcritNmpr() + rtnVO.getWaitNmpr();		// 정원
      		// 신청자 카운트 (취소, 대기  제외)
      		int rcritCnt 		= rtnVO.getSttus00() + rtnVO.getSttus01() + rtnVO.getSttus09();
      		int waitCnt    	= rtnVO.getSttus02() + rtnVO.getSttus10();
      		int resveCnt = edcAtnlcUserBndl.getBndlUserNm().length;

      		if((rcritCnt+waitCnt+resveCnt) >= rcritNmpr){
      			// 정원 + 대기 + 현재 신청인원을 더하여 정원과 비교
      			// 예약 인원이 크거나 같을때   모집 마감으로 상태값 변경 처리
      			edcLctreVO.setRceptSttus("RC1");
      			edcLctreVO.setRceptSttusNm("접수 마감");

      			edcLctreService.updateEdcLctreSttus(request, edcLctreVO);
      		}
    	}

		return rtnVO;
	}

	/**
	 * 수강생 목록조회(건별)
	 */
	public List<EdcAtnlcMngrVO> selectEdcAtnlcUserList(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		// 코드값 HashMap 생성
		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		CodeCmmVO cmmVo = new CodeCmmVO();

		// 상태값, 사용유무, 강좌 상태, 결제 상태, 성별, 접수방법, 할인코드APPLCNT_RELATE
		cmmVo.setSearchCodeType("'USER_RCEPT_STTUS', 'SETLE_STTUS', 'TCH_GENDER', 'APP_METHOD','LEC_REG_TYPE','APPLCNT_RELATE','DONG_CODE'");
		codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);

		List<EdcAtnlcMngrVO> rtnList = edcAtnlcMngrDAO.selectEdcAtnlcUserList2(edcAtnlcMngrVO);

		// 데이터가공 구간
		for (EdcAtnlcMngrVO forVo : rtnList) {
				forVo.setUserRceptSttusNm(codeHashMap.get("USER_RCEPT_STTUS" + forVo.getUserRceptSttus()));
				forVo.setSetleSttus(codeHashMap.get("SETLE_STTUS" + forVo.getSetleSttus()));
				forVo.setUserSexdstn(codeHashMap.get("TCH_GENDER" + forVo.getUserSexdstn()));
				forVo.setRceptMth(codeHashMap.get("APP_METHOD" + forVo.getRceptMth()));
				forVo.setApplcntRelate(codeHashMap.get("APPLCNT_RELATE" + forVo.getApplcntRelate()));
				forVo.setDongCodeNm(codeHashMap.get("DONG_CODE" + forVo.getDongCode()));

				forVo.setFrstRegisterPnttm(DateUtil.getDateFormat(forVo.getFrstRegisterPnttm(), "yyyy.mm.dd hh:mi:ss"));
				forVo.setResveUserBrthdy(DateUtil.getDateFormat(forVo.getResveUserBrthdy(), "yyyy-mm-dd"));
				forVo.setAtnlcUserBrthdy(DateUtil.getDateFormat(forVo.getAtnlcUserBrthdy(), "yyyy-mm-dd"));
				forVo.setUserTel(StringUtil.getTelNoMask(forVo.getUserTel()));
				forVo.setUserMobile(StringUtil.getTelNoMask(forVo.getUserMobile()));

				forVo.setAtnlcUserTel(StringUtil.getTelNoMask(forVo.getAtnlcUserTel()));
				
				ResveAddFieldVO resveAddFieldVO = new ResveAddFieldVO();
				resveAddFieldVO.setSearchManagerSeq(forVo.getLctreKey()+"");
				resveAddFieldVO.setSearchJobSe("EC");
				resveAddFieldVO.setSearchMngrSeq(forVo.getAtnclKey()+"");
				resveAddFieldVO.setSearchDeleteAt("N");
				// 존나 비효율적인데 이거 대체 누구냐 진짜....
				List<ResveAddField> resveAddFieldList = resveAddFieldService.selectResveInputAddFieldData(resveAddFieldVO);
				forVo.setResveAddFieldList(resveAddFieldList);
		}

		return rtnList;
	}

	/**
	 * 인원 추가 가능 여부 확인
	 */
	public EdcLctreVO edcAtnlcAddUserCheck(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		// 강좌 접수 조건 확인
		EdcLctreVO rtnEdcLctreVO = new EdcLctreVO();
		rtnEdcLctreVO.setSearchLctreKey(StringUtil.intToStr(edcAtnlcMngrVO.getLctreKey()));
		rtnEdcLctreVO = edcLctreService.webSelectEdcLctreDetail(request, rtnEdcLctreVO);
		rtnEdcLctreVO.setCheckBooking(false);

		if(edcAtnlcMngrVO.getCnt() >  rtnEdcLctreVO.getEdcNmprLmtt()){
			rtnEdcLctreVO.setReturnMsg("교육/강좌 인원수 제한이 넘었습니다.\n\r수강자를 추가할 수 없습니다.");
			rtnEdcLctreVO.setReturnCode("C22");

			return rtnEdcLctreVO;
		}

		// 정원 (정원 + 대기 인원)
  		int rcritNmpr	= 	rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
  		// 신청자 카운트 (취소 제외)
  		int rcritCnt 		= rtnEdcLctreVO.getSttus00() + rtnEdcLctreVO.getSttus01() + rtnEdcLctreVO.getSttus02() + rtnEdcLctreVO.getSttus09() + rtnEdcLctreVO.getSttus10();
  		// 현재 예약 인원 추가
  		rcritCnt++;
  		
  		String rceptMth = "";
  		
  		if("PR1".equals(rtnEdcLctreVO.getRceptSttus())){
  			rceptMth = rtnEdcLctreVO.getPriorRceptMth();
  			rcritCnt = rtnEdcLctreVO.getSttus01a() + rtnEdcLctreVO.getSttus02a();
	  		//정시 접수 및 추가 접수 기간
  			if("0".equals(rceptMth)){
  				rcritNmpr = 	rtnEdcLctreVO.getPriorRceptNmpr() + rtnEdcLctreVO.getPriorRceptWaitNmpr();
  			}else{
  				rcritNmpr = 	rtnEdcLctreVO.getPriorRceptNmpr() + rtnEdcLctreVO.getPriorRceptWaitNmpr();
  			}
  		}else{
  			rceptMth = rtnEdcLctreVO.getRceptMth();
	  		rcritCnt = rtnEdcLctreVO.getSttus01b() + rtnEdcLctreVO.getSttus02b();
	  		//정시 접수 및 추가 접수 기간
	  		if("0".equals(rceptMth)){
	  			rcritNmpr = 	rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
	  		}else{
	  			rcritNmpr = 	rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
	  		}
  		}  	

  		// 모집 정원 체크
  		if(rcritNmpr < rcritCnt){
  			// 모집 정원 마감
			rtnEdcLctreVO.setReturnMsg("모집 정원이 초과 되었습니다.\n\r수강자를 추가할 수 없습니다.");
			rtnEdcLctreVO.setReturnCode("C02");

			return rtnEdcLctreVO;
  		}

  		/** 연계강좌 수강 금지 시작 **/
  		// 재수강 금지 (목록 중 이미 [2개]이상의 강좌를 수료)
  		// 중복수강제한 조건 (본강좌와 동시에 수강을 할 수 없는 강좌를 등록합니다.)
  		// 연계구분(type : D : 중복수강, S: 재수강, R : 연계)
  		if("".equals(StringUtil.strTrim(edcAtnlcMngrVO.getUserId()))){
  			// 아이디 없는 경우 == 개인정보(이름, 전화번호) 중복 여부 체크
  			edcAtnlcMngrVO.setSearchUserNm(edcAtnlcMngrVO.getUserNm());
  			edcAtnlcMngrVO.setSearchUserBrthdy(StringUtil.strReplaceALL(edcAtnlcMngrVO.getUserBrthdy(), "-", ""));
  			edcAtnlcMngrVO.setSearchTel01(edcAtnlcMngrVO.getUserTel());
  		}else{
  			// 아이디 있는 경우
  			edcAtnlcMngrVO.setSearchUserId(edcAtnlcMngrVO.getUserId());
  			edcAtnlcMngrVO.setSearchLctreKey(rtnEdcLctreVO.getSearchLctreKey());
  		}

  		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.edcAtnlcAddUserCheck(edcAtnlcMngrVO);

  		for(EdcAtnlcMngrVO forVo : list){
  				if("D".equals(forVo.getJobSe())){
  					if(forVo.getCnt() > 0){
  						rtnEdcLctreVO.setReturnMsg("중복수강 제한 강좌에 신청하였습니다.");
  						rtnEdcLctreVO.setReturnCode("C20");

  						return rtnEdcLctreVO;
  					}
  				}

  				if("S".equals(forVo.getJobSe())){
  					if("01".equals(forVo.getUserRceptSttus())){
	  					if(forVo.getCnt() > 1){
	  						rtnEdcLctreVO.setReturnMsg("재수강 제한 강좌 입니다.");
	  						rtnEdcLctreVO.setReturnCode("C21");

	  						return rtnEdcLctreVO;
	  					}
  					}
  				}
  		}
  		/** 연계강좌 수강 금지 끝 **/



  		rtnEdcLctreVO.setCheckBooking(true);

		return rtnEdcLctreVO;
	}

	/**
	 * 수강신청 업데이트 시 상태 확인
	 */
	public EdcLctreVO updateCheckBooking(
							HttpServletRequest request
							, EdcLctreVO edcLctreVO
							, EdcAtnlcMngrVO edcAtnlcMngrVO
							, EdcAtnlcUserVO edcAtnlcUserVO
							, EdcAtnlcUserBndl edcAtnlcUserBndl
							, String userId
						) throws Exception{

				EdcLctreVO rtnEdcLctreVO = new EdcLctreVO();

				try{
						int atnlcInt = 1;
						// 수강생 인원 수 체크
						if(edcAtnlcUserBndl.getBndlUserNm() != null){
							atnlcInt = edcAtnlcUserBndl.getBndlUserNm().length;
						}

						// 강좌 접수 조건 확인
						edcLctreVO.setSearchLctreKey(StringUtil.intToStr(edcLctreVO.getLctreKey()));
						rtnEdcLctreVO = edcLctreService.webSelectEdcLctreDetail(request, edcLctreVO);
						rtnEdcLctreVO.setCheckBooking(false);

						if("Y".equals(rtnEdcLctreVO.getCancelYn())){
							rtnEdcLctreVO.setReturnMsg("폐강된 강좌 입니다.");
							rtnEdcLctreVO.setReturnCode("C01");

							return rtnEdcLctreVO;
						}

						// 나이제한 조건, 학년제한 조건, 성별 제한 조건
						HashMap<String, String> ageMap = new HashMap<String, String>();
						ageMap = checkAge(rtnEdcLctreVO, edcAtnlcUserBndl);

						if(!"S".equals(ageMap.get("resultCode"))){
							rtnEdcLctreVO.setReturnMsg(ageMap.get("resultMsg"));
							rtnEdcLctreVO.setReturnCode("C99");

							return rtnEdcLctreVO;
						}

						// 신청정원수 확인
						int rcritNmpr	= 	0;
						// 신청자 카운트 (취소 제외)
						int rcritCnt 		= rtnEdcLctreVO.getSttus00() + rtnEdcLctreVO.getSttus01() + rtnEdcLctreVO.getSttus02() + rtnEdcLctreVO.getSttus09() + rtnEdcLctreVO.getSttus10();
						// 현재 예약 인원 추가

						int atnclUserCnt = edcAtnlcUserBndl.getBndlUserNm().length;
						if(atnclUserCnt == 0){
							atnclUserCnt = 1;
						}

						System.out.println("rtnEdcLctreVO.getGrpYn() : " + rtnEdcLctreVO.getGrpYn());

						// 단체 신청시
						if("Y".equals(rtnEdcLctreVO.getGrpYn())){
							atnclUserCnt = StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[0]);
						}

						rcritCnt = rcritCnt+atnclUserCnt;
						rcritNmpr	= 	rtnEdcLctreVO.getRcritNmpr() + rtnEdcLctreVO.getWaitNmpr();
						
						int nmpr = 0;
						
						if("01".equals(edcAtnlcMngrVO.getUserRceptSttus())){
							int sttus01 = 0;
							
							if("PR1".equals(rtnEdcLctreVO.getRceptSttus())){
								sttus01 = rtnEdcLctreVO.getSttus01a();
								nmpr = rtnEdcLctreVO.getPriorRceptNmpr();
							}else{
								sttus01 = rtnEdcLctreVO.getSttus01b();
								nmpr = rtnEdcLctreVO.getRcritNmpr();
							}

							for(int i=0;i<atnlcInt;i++){
								if("Y".equals(edcLctreVO.getGrpYn())){
									sttus01 = sttus01 + StringUtil.strToInt(edcAtnlcUserBndl.getBndlEdcNmpr()[i]);
								}
							}

							if(sttus01 > nmpr){
								rtnEdcLctreVO.setReturnMsg("최대 승인 모집정원 보다 더 많은 인원이 승인 처리되어 업데이트 하지 못했습니다.");
								rtnEdcLctreVO.setReturnCode("C09");

								return rtnEdcLctreVO;
							}
						}

						// 모집 정원 체크
						/*if(rcritNmpr < rcritCnt){
							// 모집 정원 마감
							rtnEdcLctreVO.setReturnMsg("모집 정원이 초과 되었습니다.");
							rtnEdcLctreVO.setReturnCode("C02");

							return rtnEdcLctreVO;
						}else{
							// 모집 정원 미 마감
							if("Y".equals(rtnEdcLctreVO.getDplctRceptYn())){
								// 중복 신청 가능 (전체 중복 가능 인원 수 비교 )
								if(rtnEdcLctreVO.getEdcNmprLmtt() < atnlcInt){
									rtnEdcLctreVO.setReturnMsg("최대 수강인원수가 초가되었습니다.(동일강좌 최대 예약 초과)");
									rtnEdcLctreVO.setReturnCode("C09");

									return rtnEdcLctreVO;
								}
							}
						}*/

						rtnEdcLctreVO.setReturnCode("S");
						rtnEdcLctreVO.setCheckBooking(true);

				}catch (Exception e) {
					egovLogger.error("EdcAtnlcMngrServiceImpl.checkBooking : ", e);
				}

				return rtnEdcLctreVO;
	}

	/**
	 * 교육/강좌 대기자 대기 순번 조회
	 */
	public List<EdcAtnlcMngrVO> edcAtnlcWaitList(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		List<EdcAtnlcMngrVO> list = edcAtnlcMngrDAO.edcAtnlcWaitList(edcAtnlcMngrVO);

		return list;
	}

	public int edcHomePageReflection(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO, EdcLctreVO edcLctreVO) throws Exception{
		return 0;
	}

	/**
	 * 결제 방법 저장
	 */
	public int updateEdcPayResult(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		int rtnInt = 0;
		edcAtnlcMngrVO.setLastUpdusrId(edcAtnlcMngrVO.getLoginUserId());
		edcAtnlcMngrVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
		edcAtnlcMngrVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

		if("CARD".equals(edcAtnlcMngrVO.getMetpay())){
			edcAtnlcMngrVO.setMetpay("100000000000");
		}else if("BANK".equals(edcAtnlcMngrVO.getMetpay())){
			edcAtnlcMngrVO.setMetpay("010000000000");
		}else if("VCNT".equals(edcAtnlcMngrVO.getMetpay())){
			edcAtnlcMngrVO.setMetpay("001000000000");
		}else if("NO".equals(edcAtnlcMngrVO.getMetpay())){
			edcAtnlcMngrVO.setMetpay("NO");
		}

		rtnInt = edcAtnlcMngrDAO.updateEdcPayResult(edcAtnlcMngrVO);

		return rtnInt;
	}

	/**
	 * 수강/신청 등록 사용자 group by 로 제외 (count 반환)
	 * @param edcAtnlcMngrVO
	 * @return
	 */
	public int selectEdcAtnlcReserveListCnt(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		return edcAtnlcMngrDAO.selectEdcAtnlcReserveListCnt(edcAtnlcMngrVO);
	}

	/**
	 * 수강/신청 등록 사용자 group by 로 제외 (리스트 반환)
	 * @param edcAtnlcMngrVO
	 * @return
	 */
	public List<EdcAtnlcMngr> selectEdcAtnlcReserveList(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		List<EdcAtnlcMngr> rtnList = edcAtnlcMngrDAO.selectEdcAtnlcReserveList(edcAtnlcMngrVO);

		for(EdcAtnlcMngr forVo : rtnList){
			forVo.setUserMobile(StringUtil.getTelNoMask(forVo.getUserMobile()));
			forVo.setUserBrthdy(DateUtil.getDateFormat(forVo.getUserBrthdy(), "yyyy-mm-dd"));
			forVo.setEdcBgnde(DateUtil.getDateFormat(forVo.getEdcBgnde(), "yyyy-mm-dd"));
			forVo.setEdcEndde(DateUtil.getDateFormat(forVo.getEdcEndde(), "yyyy-mm-dd"));
		}

		return rtnList;
	}

	/**
	 * 수강생 카운트 조회
	 */
	public int selectEdcCnt(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		return edcAtnlcMngrDAO.selectEdcCnt(edcAtnlcMngrVO);
	}

	/**
	 * 승인인원 초과 유무 확인
	 * @param edcAtnlcMngrVO
	 * @return
	 * @throws Exception
	 */
	public int selectEdcRcritNmprSttus(EdcAtnlcMngrVO edcAtnlcMngrVO) throws Exception{
		int rtnInt = 0;
		edcAtnlcMngrVO = edcAtnlcMngrDAO.selectEdcRcritNmprSttus(edcAtnlcMngrVO);

		if(edcAtnlcMngrVO.getCnt() >= edcAtnlcMngrVO.getRcritNmpr()){
				rtnInt = 1;
		}
		return rtnInt;
	}
	
	
	
	/**
	 * 수강관리의 목록을 조회한다.
	 *
	 * @param EdcAtnlcMngrVO
	 * @return List<EdcAtnlcMngrVO> 수강관리 목록
	 * @throws Exception
	 */
	public List<CmmnPaylogVO> selectEdcAtnlcPayMngrList(CmmnPaylogVO cmmnPaylogVO) throws Exception {
		String nowDate = DateUtil.getNowDateTime("yyyyMMddHHmm");
		double nowDateInt = StringUtil.strToDouble(nowDate);

		HashMap<String, String> codeHashMap = new HashMap<String, String>();
		List<CmmnPaylogVO> list = edcAtnlcMngrDAO.selectEdcAtnlcPayMngrList(cmmnPaylogVO);

		if(list.size()>0){
			// 코드값 HashMap 생성
			CodeCmmVO cmmVo = new CodeCmmVO();

			cmmVo.setSearchCodeType("'USER_RCEPT_STTUS','SETLE_STTUS'");
			codeHashMap = edcCmmCodeService.selectEdcCmmCodeMap(cmmVo);
		}

		// 데이터가공 구간
		String edcChangeSttus = "";
		for(CmmnPaylogVO forVo : list){
			edcChangeSttus = "";

			forVo.setSttusNm(codeHashMap.get("USER_RCEPT_STTUS"+forVo.getUserRceptSttus()));
			forVo.setSetleSttusNm(codeHashMap.get("SETLE_STTUS"+forVo.getSetleSttus()));

		}
		return list;
	}

	

	/**
	 * 수강관리의 레코드 수를 조회 한다.
	 */
	public int selectEdcAtnlcPayMngrTotCnt(CmmnPaylogVO cmmnPaylogVO) throws Exception {
		return (Integer) edcAtnlcMngrDAO.selectEdcAtnlcPayMngrTotCnt(cmmnPaylogVO);
	}
}








