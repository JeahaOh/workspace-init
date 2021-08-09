package kr.co.hanshinit.NeoCMS.cop.edu.cos.app.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import kr.co.hanshinit.NeoCMS.cmm.service.CmmUseService;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.ExcelUpdtVO;
import kr.co.hanshinit.NeoCMS.cmm.util.ExcelUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.HttpRequestUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.cop.edc.service.EducationalService;
import kr.co.hanshinit.NeoCMS.cop.edc.service.EducationalVO;
import kr.co.hanshinit.NeoCMS.cop.edu.atd.cos.service.EduAttendCourseService;
import kr.co.hanshinit.NeoCMS.cop.edu.atd.cos.service.EduAttendCourseVO;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.EduCourseApp;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.EduCourseAppService;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.EduCourseAppVO;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.impl.EduCourseAppDAO;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.service.EduCourse;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.service.EduCourseService;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.service.EduCourseVO;
import kr.co.hanshinit.NeoCMS.sym.log.llg.service.IndvdlinfoLog;
import kr.co.hanshinit.NeoCMS.sym.log.llg.service.IndvdlinfoLogService;
import kr.co.hanshinit.NeoCMS.tag.pagination.NeoPaginationInfo;
import kr.co.hanshinit.NeoCMS.uss.umt.service.UserInfo;
import kr.co.hanshinit.NeoCMS.uss.umt.service.UserInfoService;

@Controller
public class CourseAppController {
  
  Logger logger = LoggerFactory.getLogger(this.getClass());

	/** cmmUseService */
	@Resource(name="cmmUseService")
	private CmmUseService cmmUseService;

	/** eduCourseAppService */
	@Resource(name="eduCourseAppService")
	private EduCourseAppService eduCourseAppService;

	/** eduCourseService */
	@Resource(name="eduCourseService")
	private EduCourseService eduCourseService;

	/** EducationalService */
	@Resource(name="edcService")
	private EducationalService edcService;

	/** userInfoService */
	@Resource(name="userInfoService")
	private UserInfoService userInfoService;

	/** eduCourseAppDAO */
	@Resource(name="eduCourseAppDAO")
	private EduCourseAppDAO eduCourseAppDAO;

	/** eduAttendCourseService */
	@Resource(name="eduAttendCourseService")
	EduAttendCourseService eduAttendCourseService;

	@Resource(name="indvdlinfoLogService")
	private IndvdlinfoLogService indvdlinfoLogService;

  /**
   * [관리자] 강좌 접수 현황 (접수자 목록) 페이지.
   *
   * @param eduCourseAppVO
   * @return "NeoCMS/cop/edu/cos/app/courseAppList"
   * @throws Exception
   */
  @RequestMapping(value="/neo/courseAppList.do", method=RequestMethod.GET)
  public String courseAppList(
    ModelMap model
    , EduCourseAppVO eduCourseAppVO
    , HttpServletRequest request
  ) throws Exception {

    String srcSearch = StringUtil.nvl(request.getParameter("srcSearch"));
    String srcStatus = StringUtil.nvl(request.getParameter("srcStatus"));
    String srcResult = StringUtil.nvl(request.getParameter("srcResult"));

    /**
     * 20200915 전체검색 요청.
     * eduCourseAppVO srcStatus = "confirm" 고정선언.
     * eduCourseAppVO srcResult = "prog" 고정선언.
     * 전체검색 안됨 초기화.
     */
    if ("tot".equals(srcSearch)) {
      eduCourseAppVO.setSrcStatus("");
      eduCourseAppVO.setSrcResult("");
    } else {
      eduCourseAppVO.setSrcStatus(srcStatus);
      eduCourseAppVO.setSrcResult(srcResult);
    }

    model.addAttribute("eduCourseAppVO", eduCourseAppVO);

    eduCourseAppVO.setSrcQuarter(null);
    eduCourseAppVO.setSrcYear(null);

    // if(eduCourseAppVO.getPageUnit()==10){
    // eduCourseAppVO.setPageUnit(30);
    // }

    // 선택된 강좌 정보
    EduCourse tmpEduCourse = new EduCourse();
    tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
    EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);
    model.addAttribute("eduCourse", eduCourse);

    // 강좌 접수 방법
    Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
    model.addAttribute("ests02", ests02);

    // 면제자 사유
    Map<String, String> exem01 = cmmUseService.selectCmmnDetailCodeMap("EXEM01");
    model.addAttribute("exem01", exem01);

    // 회원 접수 상태
    Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");
    model.addAttribute("ests03", ests03);

    // 페이징
    int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
    NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
    model.addAttribute("paginationInfo", neoPaginationInfo);

    List<EduCourseApp> eduCourseAppList =
        eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);
    model.addAttribute("eduCourseAppList", eduCourseAppList);

    IndvdlinfoLog indvdlinfoLog = new IndvdlinfoLog();
    indvdlinfoLog.setAtnddu("열람");
    indvdlinfoLog.setTrtmntPurps("강좌 접수현황 목록");
    indvdlinfoLog.setSearchWrd(eduCourseAppVO.getCourse() + "");
    indvdlinfoLogService.insertIndvdlinfoLog(request, indvdlinfoLog);


    return "NeoCMS/cop/edu/cos/app/courseAppList";
  }





  /**
   * [관리자] 강좌 접수 등록 페이지.
   *
   * @param eduCourseAppVO
   * @return "NeoCMS/cop/edu/cos/app/courseAppRegist"
   * @throws Exception
   */
  @RequestMapping(value = "/neo/courseAppRegist.do", method = RequestMethod.GET)
  public String courseAppRegist(ModelMap model, EduCourseAppVO eduCourseAppVO,
      HttpServletRequest request,
      @RequestParam(value = "mode", required = false, defaultValue = "0") String mode)
      throws Exception {

    model.addAttribute("eduCourseAppVO", eduCourseAppVO);

    // 강좌 선택
    EduCourse temp = new EduCourse();
    temp.setCourse(eduCourseAppVO.getCourse());
    EduCourseVO eduCourse = eduCourseService.selectEduCourse(temp, request);
    if (eduCourse != null) {
      if (eduCourse.getCount() <= eduCourse.getConfirm() && mode.equals("0")) {
        return "NeoCMS/cop/edu/cos/app/courseAppRegistChk";
      }
    }
    // 강좌 접수 방법
    Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
    model.addAttribute("ests02", ests02);

    // 면제자 사유
    Map<String, String> exem01 = cmmUseService.selectCmmnDetailCodeMap("EXEM01");
    model.addAttribute("exem01", exem01);

    // 회원 접수 상태
    Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");
    model.addAttribute("ests03", ests03);

    // 강좌 목록 선택
    List<EduCourseVO> eduCourseMap = eduCourseService.selectEduCourseMap(new EduCourseVO());
    model.addAttribute("eduCourseMap", eduCourseMap);


    Date today = new Date();
    model.addAttribute("today", today);

    return "NeoCMS/cop/edu/cos/app/courseAppRegist";
  }

  /**
   * [관리자] 강좌 접수 등록 처리.
   *
   * @param eduCourseAppVO
   * @return "/neo/courseAppList.do?course="+course
   * @throws Exception
   */
  @RequestMapping(value = "/neo/courseAppRegist.do", method = RequestMethod.POST)
  public String courseAppRegistDo(ModelMap model, EduCourseApp eduCourseApp,
      HttpServletRequest request) throws Exception {

    eduCourseAppService.insertEduCourseApp(eduCourseApp, request);

    return cmmUseService.redirectMsg(model, "등록되었습니다.",
        "/neo/courseAppList.do?course=" + eduCourseApp.getCourse());
  }







	/**
	 * 강좌 접수 수정화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppUpdt"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppUpdt.do", method=RequestMethod.GET)
	public String courseAppUpdt(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		EduCourseApp eduCourseApp = eduCourseAppService.selectEduCourseApp(eduCourseAppVO);
		model.addAttribute("eduCourseApp", eduCourseApp);

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
		model.addAttribute("ests02", ests02);

		// 면제자 사유
		Map<String, String> exem01 = cmmUseService.selectCmmnDetailCodeMap("EXEM01");
		model.addAttribute("exem01", exem01);

		// 회원 접수 상태
		Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");
		model.addAttribute("ests03", ests03);

		// 강좌 목록 선택
//		List<EduCourseVO> eduCourseMap = eduCourseService.selectEduCourseMap(new EduCourseVO());
//		model.addAttribute("eduCourseMap", eduCourseMap);

		// 강좌 선택
		EduCourse temp = new EduCourse();
		temp.setCourse(eduCourseAppVO.getCourse());
		EduCourseVO eduCourse = eduCourseService.selectEduCourse(temp, request);
		model.addAttribute("eduCourse", eduCourse);

		// 결제 수단
		Map<String, String> paty01 = cmmUseService.selectCmmnDetailCodeMap("PATY01");
		model.addAttribute("paty01", paty01);

		Date today = new Date();
        model.addAttribute("today", today);

        model.addAttribute("histList", eduCourseAppService.selectEduCourseHistList(eduCourseAppVO));

		IndvdlinfoLog indvdlinfoLog = new IndvdlinfoLog();
		indvdlinfoLog.setAtnddu("열람");
		indvdlinfoLog.setTrtmntPurps("강좌 접수현황 조회");
		indvdlinfoLog.setSearchWrd(eduCourseAppVO.getApp()+"");
		indvdlinfoLogService.insertIndvdlinfoLog(request, indvdlinfoLog);

		return "NeoCMS/cop/edu/cos/app/courseAppUpdt";
	}

	/**
	 * 강좌 접수를 수정한다.
	 * @param eduCourseAppVO
	 * @return "/neo/courseAppList.do?course="+course
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppUpdt.do", method=RequestMethod.POST)
	public String courseAppUpdtDo(ModelMap model
			, EduCourseApp eduCourseApp
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.updateEduCourseApp(eduCourseApp, request);

		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/neo/courseAppList.do?course="+eduCourseApp.getCourse());
	}

	/**
	 * 강좌 접수 상태를 변경하는 화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppChange"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppChange.do", method=RequestMethod.GET)
	public String courseAppChange(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		return "NeoCMS/cop/edu/cos/app/courseAppChange";
	}

	/**
	 * 강좌 접수 상태를 변경한다.
	 * @param eduCourseAppVO
	 * @return "/neo/courseAppList.do?course="+course
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppChange.do", method=RequestMethod.POST)
	public String courseAppChangeDo(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.updateEduCourseAppStatus(eduCourseAppVO, request);

		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/neo/courseAppList.do?course="+eduCourseAppVO.getCourse()+"&srcYear="+eduCourseAppVO.getSrcYear()+"&srcQuarter="+eduCourseAppVO.getSrcQuarter());
	}

	/**
	 * 강좌 접수를 삭제한다.
	 * @param eduCourseAppVO
	 * @return "/neo/courseAppList.do?course="+course
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppDelete.do", method=RequestMethod.GET)
	public String courseAppDelete(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.deleteEduCourseApp(eduCourseAppVO);

		return cmmUseService.redirectMsg(model, "삭제되었습니다.", "/neo/courseAppList.do?course="+eduCourseAppVO.getCourse()+"&srcYear="+eduCourseAppVO.getSrcYear()+"&srcQuarter="+eduCourseAppVO.getSrcQuarter());

	}

	/**
	 * 강좌 수료 화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @param appList
	 * @param appResult
	 * @return "NeoCMS/cop/edu/cos/app/courseAppResult"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppResult.do", method=RequestMethod.GET)
	public String courseAppResult(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			, @RequestParam("appList") String appList
			, @RequestParam("appResult") String appResult
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);
		model.addAttribute("appList", appList);
		model.addAttribute("appResult", appResult);

		Date today = new Date();
		model.addAttribute("today", today);

		// 선택된 강좌 정보
		EduCourse tmpEduCourse = new EduCourse();
		tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
		EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);
		model.addAttribute("eduCourse", eduCourse);

		// 기관 정보
		EducationalVO edcVO = new EducationalVO();
		edcVO.setEducational(eduCourse.getEducational());
		EducationalVO educational = edcService.selectEdc(edcVO, request);
		model.addAttribute("educational", educational);

		return "NeoCMS/cop/edu/cos/app/courseAppResult";
	}

	/**
	 * SMS 발송 팝업창
	 * @param model
	 * @param eduCourseAppVO
	 * @param request
	 * @param appList
	 * @return "NeoCMS/cop/edu/cos/app/courseAppSmsPop"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppSmsPop.do", method=RequestMethod.POST)
	public String courseAppSmsPop(ModelMap model
	        , EduCourseAppVO eduCourseAppVO
	        , HttpServletRequest request
            , @RequestParam("appList") String appList
	        ) throws Exception {

        String[] arr = appList.split(",");
	    List<String> lst = new ArrayList<String>(Arrays.asList(arr));
	    HashMap<String, Object> hm = new HashMap<String, Object>();
	    hm.put("clist", lst);

	    List<EduCourseApp> ecaList = eduCourseAppService.selectEduCourseAppSMSList(hm);
	    model.addAttribute("ecaList", ecaList);

	    // 강좌 선택
	 	EduCourse temp = new EduCourse();
	 	temp.setCourse(eduCourseAppVO.getCourse());
	 	EduCourseVO eduCourse = eduCourseService.selectEduCourse(temp, request);
	 	model.addAttribute("eduCourse", eduCourse);

        model.addAttribute("eduCourseAppVO", eduCourseAppVO);
        model.addAttribute("appList", appList);

	    return "NeoCMS/cop/edu/cos/app/courseAppSmsPop";
	}


	/**
	 * SMS 발송 팝업창
	 * @param model
	 * @param eduCourseAppVO
	 * @param request
	 * @param appList
	 * @return "NeoCMS/cop/edu/cos/app/courseAppSmsPop"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppSmsPopAll.do", method=RequestMethod.POST)
	public String courseAppSmsPopAll(ModelMap model
	        , EduCourseAppVO eduCourseAppVO
	        , HttpServletRequest request

	        ) throws Exception {

		EduCourseAppVO searchVo = new EduCourseAppVO();
		searchVo.setCourse(eduCourseAppVO.getCourse());
		searchVo.setSrcYear(eduCourseAppVO.getSrcYear());
		searchVo.setSrcQuarter(eduCourseAppVO.getSrcQuarter());
		searchVo.setFirstIndex(0);
		searchVo.setLastIndex(1000);
		List<EduCourseApp> ecaList = eduCourseAppService.selectEduCourseAppList(searchVo);

	    model.addAttribute("ecaList", ecaList);

	    String appList = "";

	    for(int i=0; i<ecaList.size();i++){
	    	if(!"".equals(StringUtil.nvl(ecaList.get(i).getMobile()))){
	    		appList  = appList +ecaList.get(i).getApp();
	    		if(i != ecaList.size()-1){
	    			appList = appList +",";
	    		}

	    	}
	    }

	    // 강좌 선택
	 	EduCourse temp = new EduCourse();
	 	temp.setCourse(eduCourseAppVO.getCourse());
	 	EduCourseVO eduCourse = eduCourseService.selectEduCourse(temp, request);
	 	model.addAttribute("eduCourse", eduCourse);

        model.addAttribute("eduCourseAppVO", eduCourseAppVO);
        model.addAttribute("appList", appList);

	    return "NeoCMS/cop/edu/cos/app/courseAppSmsPop";
	}


	@RequestMapping(value="/neo/courseAppSmsSend.do", method=RequestMethod.POST)
	public String courseAppSmsSend(@RequestParam("sms_content") String sms_content
	        , @RequestParam("trnsnum") String trnsnum
	        , @RequestParam("appList") String appList
	        , @RequestParam("course") String course
	        , ModelMap model) throws Exception {

	    String rst = "success";

	    try {

	        String[] arr = appList.split(",");
	        List<String> lst = new ArrayList<String>(Arrays.asList(arr));
	        HashMap<String, Object> hm = new HashMap<String, Object>();
	        hm.put("clist", lst);

	        List<EduCourseApp> ecaList = eduCourseAppService.selectEduCourseAppSMSList(hm);

	        eduCourseAppService.insertEduCourseAppSMS(trnsnum, sms_content, ecaList, course);

        } catch(Exception e) {
            rst = "fail";

        } finally {
            model.addAttribute("result", rst);
        }

	    return "NeoCMS/cop/edu/cos/app/courseAppSmsResult";
	}

	/**
	 * 강좌 수료 결과를 등록한다.
	 * @param eduCourseAppVO
	 * @param appList
	 * @param appResult
	 * @return "/neo/courseAppList.do?course="+course
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppResult.do", method=RequestMethod.POST)
	public String courseAppResultDo(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			, @RequestParam("appList") String appList
			, @RequestParam("appResult") String appResult
			) throws Exception {

		EduCourseAppVO temp = new EduCourseAppVO();
		temp.setResult(appResult);
		temp.setEsdate(eduCourseAppVO.getEsdate());
		temp.setEedate(eduCourseAppVO.getEedate());
		String edate = DateUtil.getNowDateTime("yyyyMMddHHmmss");
		temp.setEdate(edate);

		String[] apps = appList.split(",");
		for (String app : apps) {
			temp.setApp(Integer.parseInt(app));
			eduCourseAppService.updateEduCourseAppResult(temp, request);
		}


		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/neo/courseAppList.do?course="+eduCourseAppVO.getCourse()+"&srcYear="+eduCourseAppVO.getSrcYear()+"&srcQuarter="+eduCourseAppVO.getSrcQuarter());
	}

  /**
   * 강좌 수료 목록으로 이동한다.
   * 
   * @param eduCourseAppVO
   * @return "NeoCMS/cop/edu/cos/app/courseAppCompList"
   * @throws Exception
   */
  @RequestMapping(value = "/neo/courseAppCompList.do", method = RequestMethod.GET)
  public String courseAppCompList(
    ModelMap model,
    EduCourseAppVO eduCourseAppVO,
    HttpServletRequest request
  ) throws Exception {
    
    logger.debug("\n\t>> /neo/courseAppCompList.do <<");
    HttpRequestUtil.requestLog(request);

    eduCourseAppVO.setSrcQuarter(null);
    eduCourseAppVO.setSrcYear(null);

    model.addAttribute("eduCourseAppVO", eduCourseAppVO);

    // 선택된 강좌 정보
    EduCourse tmpEduCourse = new EduCourse();
    tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
    EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);
    model.addAttribute("eduCourse", eduCourse);

    // 강좌 접수 방법
    Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
    model.addAttribute("ests02", ests02);

    // 페이징
    int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
    NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
    model.addAttribute("paginationInfo", neoPaginationInfo);

    List<EduCourseApp> eduCourseAppList =
        eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);
    model.addAttribute("eduCourseAppList", eduCourseAppList);

    IndvdlinfoLog indvdlinfoLog = new IndvdlinfoLog();
    indvdlinfoLog.setAtnddu("열람");
    indvdlinfoLog.setTrtmntPurps("강좌 수료현황 목록");
    indvdlinfoLog.setSearchWrd(eduCourseAppVO.getCourse() + "");
    indvdlinfoLogService.insertIndvdlinfoLog(request, indvdlinfoLog);


    return "NeoCMS/cop/edu/cos/app/courseAppCompList";
  }

	/**
	 * 강좌 수료 결과 수정화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppCompUpdt"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppCompUpdt.do", method=RequestMethod.GET)
	public String courseAppCompUpdt(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		EduCourseApp eduCourseApp = eduCourseAppService.selectEduCourseApp(eduCourseAppVO);
		model.addAttribute("eduCourseApp", eduCourseApp);

		// 선택된 강좌 정보
		EduCourse tmpEduCourse = new EduCourse();
		tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
		EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);
		model.addAttribute("eduCourse", eduCourse);

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
		model.addAttribute("ests02", ests02);

		return "NeoCMS/cop/edu/cos/app/courseAppCompUpdt";
	}

	/**
	 * 접수 내역 화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppXpayView"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppXpayView.do", method=RequestMethod.GET)
	public String courseAppXpayView(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		EduCourseApp eduCourseApp = eduCourseAppService.selectEduCourseApp(eduCourseAppVO);
		model.addAttribute("eduCourseApp", eduCourseApp);

		// 선택된 강좌 정보
		EduCourse tmpEduCourse = new EduCourse();
		tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
		EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);
		model.addAttribute("eduCourse", eduCourse);

		// 접수 회원 정보
		UserInfo userInfo = userInfoService.selectUserInfo(eduCourseApp.getUser());
		model.addAttribute("userInfo", userInfo);

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
		model.addAttribute("ests02", ests02);

		// 면제자 사유
		Map<String, String> exem01 = cmmUseService.selectCmmnDetailCodeMap("EXEM01");
		model.addAttribute("exem01", exem01);

		// 회원 접수 상태
		Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");
		model.addAttribute("ests03", ests03);

		//강사소개분류
        Map<String, String> fei01 = cmmUseService.selectCmmnDetailCodeMap("FEI01");
        model.addAttribute("fei01",fei01);

        // 결제 수단
		Map<String, String> paty01 = cmmUseService.selectCmmnDetailCodeMap("PATY01");
 		model.addAttribute("paty01", paty01);

 		// 환불 상태
 		Map<String, String> cast01 = cmmUseService.selectCmmnDetailCodeMap("CAST01");
 		model.addAttribute("cast01", cast01);


		if(!"".equals(StringUtil.nvl(eduCourseApp.getCanceldate()))){
			EduAttendCourseVO eduAttendCourseVO = new EduAttendCourseVO();
			eduAttendCourseVO.setCourse(eduCourseAppVO.getCourse());
			eduAttendCourseVO.setSearchDate(eduCourseApp.getCanceldate());
			model.addAttribute("dayCnt", eduAttendCourseService.selectEduAttendCourseTotCnt(eduAttendCourseVO));
		}

		IndvdlinfoLog indvdlinfoLog = new IndvdlinfoLog();
		indvdlinfoLog.setAtnddu("열람");
		indvdlinfoLog.setTrtmntPurps("강좌 접수현황 결제정보");
		indvdlinfoLog.setSearchWrd(eduCourseAppVO.getApp()+"");
		indvdlinfoLogService.insertIndvdlinfoLog(request, indvdlinfoLog);


		return "NeoCMS/cop/edu/cos/app/courseAppXpayView";
	}

	/**
	 * 접수 내역 수정화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppXpayUpdt"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppXpayUpdt.do", method=RequestMethod.GET)
	public String courseAppXpayUpdt(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		EduCourseApp eduCourseApp = eduCourseAppService.selectEduCourseApp(eduCourseAppVO);
		model.addAttribute("eduCourseApp", eduCourseApp);

		// 결제 수단
		Map<String, String> paty01 = cmmUseService.selectCmmnDetailCodeMap("PATY01");
 		model.addAttribute("paty01", paty01);

 		// 환불 상태
 		Map<String, String> cast01 = cmmUseService.selectCmmnDetailCodeMap("CAST01");
 		model.addAttribute("cast01", cast01);

		return "NeoCMS/cop/edu/cos/app/courseAppXpayUpdt";
	}

	/**
	 * 접수 내역을 수정한다.
	 * @param eduCourseAppVO
	 * @return "/neo/courseAppXpayView.do?app="+app+"&course="+course
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppXpayUpdt.do", method=RequestMethod.POST)
	public String courseAppXpayUpdtDo(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.updateEduCourseAppCancel(eduCourseAppVO, request);

		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/neo/courseAppXpayView.do?app="+eduCourseAppVO.getApp()+"&course="+eduCourseAppVO.getCourse());
	}

  /**
   * [관리자] 접수 인원 통계 페이지.
   */
  @RequestMapping(value = "/neo/courseAppStatistics.do", method = RequestMethod.GET)
  public String courseAppStatistics(ModelMap model, EduCourseAppVO eduCourseAppVO,
      HttpServletRequest request) throws Exception {
    int maxYear = eduCourseService.selectMaxYear();
    model.addAttribute("maxYear", maxYear);
    int minYear = eduCourseService.selectMinYear();
    model.addAttribute("minYear", minYear);

    ArrayList<Map<String, Integer>> statisticsList = new ArrayList<Map<String, Integer>>();
    String[] srcStatus = {"tot", "confirmVisit", "confirmOnline", "wait", "cancelVisit",
        "cancelOnline", "exempted", "pay", "payCancel"};
    for (int i = 1; i < 5; i++) {
      Map<String, Integer> map = new HashMap<String, Integer>();
      eduCourseAppVO.setSrcQuarter("" + i);
      for (String tmp : srcStatus) {
        eduCourseAppVO.setSrcStatus(tmp);
        int val = eduCourseAppService.selectEduCourseAppStatistics(eduCourseAppVO);
        map.put(tmp, val);
      }

      int tot = map.get("tot");
      int appTot = 0;
      int percent = 0;
      if (tot != 0) {
        appTot = map.get("confirmVisit") + map.get("confirmOnline") + map.get("wait")
            + map.get("cancelVisit") + map.get("cancelOnline");
        percent = (appTot * 100 / tot);
      }
      map.put("percent", percent);

      statisticsList.add(map);
    }
    model.addAttribute("statisticsList", statisticsList);

    return "NeoCMS/cop/edu/cos/app/courseAppStatistics";
  }

	/**
	 * 온라인 결제내역 화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppPayList"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppPayList.do", method=RequestMethod.GET)
	public String courseAppPayList(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {
		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		int maxYear = eduCourseService.selectMaxYear();
		model.addAttribute("maxYear", maxYear);
		int minYear = eduCourseService.selectMinYear();
		model.addAttribute("minYear", minYear);

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
		model.addAttribute("ests02", ests02);

		// 회원 접수 상태
		Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");
		model.addAttribute("ests03", ests03);

		eduCourseAppVO.setSrcResult("");

		//페이징
		int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
		NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
		model.addAttribute("paginationInfo", neoPaginationInfo);

		List<EduCourseApp> eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);
		model.addAttribute("eduCourseAppList", eduCourseAppList);


		//srcStatus검색 조건을 담은 eduCourseAppVO를 새로 생성하는게 좋으나, 일단 임시변수(beforeSrcStatus)로 저장해 놓는 방법을 이용함.
		String beforeSrcStatus = eduCourseAppVO.getSrcStatus();
		String[] srcStatus = {"pay", "payCancel", "payStandBy"};
		Map<String, Integer> statistics = new HashMap<String, Integer>();
		for(String tmp:srcStatus){
			eduCourseAppVO.setSrcStatus(tmp);
			int val = eduCourseAppService.selectEduCourseAppStatistics(eduCourseAppVO);
			statistics.put(tmp, val);
		}
		model.addAttribute("statistics", statistics);
		eduCourseAppVO.setSrcStatus(beforeSrcStatus);

		return "NeoCMS/cop/edu/cos/app/courseAppPayList";
	}

	/**
	 * 수강생 검색 화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppSearch"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppSearch.do", method=RequestMethod.GET)
	public String courseAppSearch(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

	//	eduCourseAppVO.setSrcQuarter(null);
	//	eduCourseAppVO.setSrcYear(null);

		int maxYear = eduCourseService.selectMaxYear();
		model.addAttribute("maxYear", maxYear);
		int minYear = eduCourseService.selectMinYear();
		model.addAttribute("minYear", minYear);

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
		model.addAttribute("ests02", ests02);

		// 면제자 사유
		Map<String, String> exem01 = cmmUseService.selectCmmnDetailCodeMap("EXEM01");
		model.addAttribute("exem01", exem01);

		// 회원 접수 상태
		Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");
		model.addAttribute("ests03", ests03);

		eduCourseAppVO.setSrcResult("");

		int totCnt = 0;
		List<EduCourseApp> eduCourseAppList = null;
		NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
	//	if(!StringUtil.isEmpty(eduCourseAppVO.getSearchKrwd())){
			//페이징
			totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
			neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
			eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);
	//	}
		model.addAttribute("paginationInfo", neoPaginationInfo);
		model.addAttribute("eduCourseAppList", eduCourseAppList);

		return "NeoCMS/cop/edu/cos/app/courseAppSearch";
	}

	/**
	 * 수강생 강좌 변경 화면으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppSearchChange"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppSearchChange.do", method=RequestMethod.GET)
	public String courseAppSearchChange(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {
		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		int maxYear = eduCourseService.selectMaxYear();
		model.addAttribute("maxYear", maxYear);
		int minYear = eduCourseService.selectMinYear();
		model.addAttribute("minYear", minYear);

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
		model.addAttribute("ests02", ests02);

		// 회원 접수 상태
		Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");
		model.addAttribute("ests03", ests03);

		eduCourseAppVO.setSrcResult("");

		int totCnt = 0;
		List<EduCourseApp> eduCourseAppList = null;
		NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
	//	if(!StringUtil.isEmpty(eduCourseAppVO.getSearchKrwd())){
			//페이징
			totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
			neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
			eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);
	//	}
		model.addAttribute("paginationInfo", neoPaginationInfo);
		model.addAttribute("eduCourseAppList", eduCourseAppList);

		//해당 년도, 해당 분기 강의 목록(select박스).
		EduCourseVO eduCourseVO = new EduCourseVO();
		eduCourseVO.setSrcQuarter(eduCourseAppVO.getSrcQuarter());
		eduCourseVO.setSrcYear(eduCourseAppVO.getSrcYear());
		eduCourseVO.setFirstIndex(0);
		eduCourseVO.setLastIndex(eduCourseService.selectEduCourseTotCnt(eduCourseVO));
		List<EduCourseVO> eduCourseList = eduCourseService.selectEduCourseList(eduCourseVO);
		model.addAttribute("eduCourseList", eduCourseList);

		return "NeoCMS/cop/edu/cos/app/courseAppSearchChange";
	}

	/**
	 * 수강생 강좌를 변경한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppSearchChange"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppSearchChange.do", method=RequestMethod.POST)
	public String courseAppSearchChangeDo(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {
		int course = eduCourseAppVO.getCourse();

		EduCourse eduCourse = new EduCourse();
		eduCourse.setCourse(course);
		eduCourse = eduCourseService.selectEduCourse(eduCourse, request);

		int educational = eduCourse.getEducational();
		EducationalVO edcVO = new EducationalVO();
		edcVO.setEducational(educational);
		edcVO = edcService.selectEdc(edcVO, request);

		eduCourseAppVO.setPname(eduCourse.getTitle());
		eduCourseAppVO.setEname(edcVO.getTitle());

		eduCourseAppService.updateEduCourseAppCourse(eduCourseAppVO, request);

		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/neo/courseAppSearchChange.do?"
				+ "pageUnit="+eduCourseAppVO.getPageUnit()
				+ "&srcStatus="+eduCourseAppVO.getSrcStatus()
				+ "&srcYear="+eduCourseAppVO.getSrcYear()
				+ "&srcQuarter="+eduCourseAppVO.getSrcQuarter()
				+ "&searchCnd="+eduCourseAppVO.getSearchCnd()
				+ "&searchKrwd="+eduCourseAppVO.getSearchKrwd()
				+ "&pageIndex="+eduCourseAppVO.getPageIndex());
	}

	/**
	 * 수강생 수료증을 프린트한다.
	 * @param eduCourseAppVO
	 * @param appList
	 * @return "NeoCMS/cop/edu/cos/app/courseAppCompPrint"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/courseAppCompPrint.do", method=RequestMethod.POST)
	public String courseAppCompPrint(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, @RequestParam("appList") String appList
			, HttpServletRequest request
			) throws Exception {

		List<Map<String,String>> eduCompList = new ArrayList<Map<String, String>>();

		String[] apps = appList.split(",");
		for(String app:apps){
			EduCourseApp eduCourseApp = new EduCourseApp();
			eduCourseApp.setApp(Integer.parseInt(app));
			eduCourseApp = eduCourseAppService.selectEduCourseApp(eduCourseApp);
			if(eduCourseApp.getResult().equals("1")){
				Map<String,String> eduComp = new HashMap<String, String>();
				int rank = eduCourseAppDAO.selectEduCompApp(Integer.parseInt(app));
				eduComp.put("app", app);
				eduComp.put("edate", eduCourseApp.getEdate());
				if(rank==0){
					eduComp.put("rank", ""+(eduCourseAppDAO.selectEduCompRank(eduCourseApp.getEdate())+1));
					eduCourseAppDAO.insertEduComp(eduComp);
				}else{
					eduComp.put("rank", ""+rank);
				}
				eduComp.put("esdate", eduCourseApp.getEsdate());
				eduComp.put("eedate", eduCourseApp.getEedate());
				eduComp.put("name", eduCourseApp.getName());

				EduCourse eduCourse = new EduCourse();
				eduCourse.setCourse(eduCourseApp.getCourse());
				eduCourse = eduCourseService.selectEduCourse(eduCourse, request);
				eduComp.put("courseName", eduCourse.getTitle());
				eduComp.put("company", eduCourseApp.getCompany());
				eduComp.put("userOrg", eduCourseApp.getUserOrg());
				eduComp.put("ename", eduCourseApp.getEname());

				eduCompList.add(eduComp);
			}
		}

		model.addAttribute("eduCompList", eduCompList);

		EduCourseVO eduCourseVO = new EduCourseVO();
		eduCourseVO.setSrcYear(eduCourseAppVO.getSrcYear());
		eduCourseVO.setSrcQuarter(eduCourseAppVO.getSrcQuarter());
		model.addAttribute("dateMap",eduCourseService.selectEduCourseMaxCsDate(eduCourseVO));
		Date today = new Date();
		model.addAttribute("today", today);



		return "NeoCMS/cop/edu/cos/app/courseAppCompPrint";
	}

  /**
   * [관리자] 강좌관리 > 강좌목록 > 접수현황 > 엑셀다운
   */
  @RequestMapping(value="/neo/courseAppListExcelDown.do", method=RequestMethod.GET)
  public void courseAppListExcelDown(
      EduCourseAppVO eduCourseAppVO,
      HttpServletRequest request,
      HttpServletResponse response) throws Exception {
    
    logger.debug("/neo/courseAppListExcelDown.do");

    eduCourseAppVO.setSrcQuarter(null);
    eduCourseAppVO.setSrcYear(null);

    // 선택된 강좌 정보
    EduCourse tmpEduCourse = new EduCourse();
    tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
    EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);

    // 강좌 접수 방법
    Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");

    // 회원 접수 상태
    Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");

    Map<String, String> area = new HashMap<String, String>();
    area.put("0", "관내");
    area.put("1", "관외");

    // 페이징
    int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
    eduCourseAppVO.setFirstIndex(0);
    eduCourseAppVO.setLastIndex(totCnt);

    List<EduCourseApp> eduCourseAppList =
        eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);

    ArrayList<ExcelUpdtVO> target = new ArrayList<ExcelUpdtVO>();

    // 강좌명
    target.add(new ExcelUpdtVO(0, 0, 0, false, eduCourse.getTitle()));


    int courseCnt = 2;
    for (EduCourseApp eduCourseApp : eduCourseAppList) {
      logger.debug("\n\t>> eduCourseApp : {} <<", ToStringBuilder.reflectionToString( eduCourseApp, ToStringStyle.MULTI_LINE_STYLE) );
      // 접수번호
      target.add(new ExcelUpdtVO(0, courseCnt, 0, false, "" + eduCourseApp.getApp()));
      // 접수구분
      target.add(new ExcelUpdtVO(0, courseCnt, 1, false, ests02.get(eduCourseApp.getType())));
      // 이름
      target.add(new ExcelUpdtVO(0, courseCnt, 2, false, eduCourseApp.getName()));
      // 전화번호
      target.add(new ExcelUpdtVO(0, courseCnt, 3, false, eduCourseApp.getPhone()));
      // 핸드폰번호
      target.add(new ExcelUpdtVO(0, courseCnt, 4, false, eduCourseApp.getMobile()));
      // 이메일
      target.add(new ExcelUpdtVO(0, courseCnt, 5, false, eduCourseApp.getEmail()));
      // 소속
      target.add(new ExcelUpdtVO(0, courseCnt, 6, false, eduCourseApp.getCompany()));
      // 직위
      target.add(new ExcelUpdtVO(0, courseCnt, 7, false, eduCourseApp.getPosition()));
      // 거주지
      target.add(new ExcelUpdtVO(0, courseCnt, 8, false, area.get(eduCourseApp.getArea())));
      // 접수상태
      target.add(new ExcelUpdtVO(0, courseCnt, 9, false, ests03.get(eduCourseApp.getStatus())));
      // 신청일
      target.add(new ExcelUpdtVO(0, courseCnt, 10, true,
          DateUtil.toDateFormat(eduCourseApp.getRdate(), "yyyyMMddHHmmss", "yyyy.MM.dd HH:mm")));
      courseCnt++;
    }


    IndvdlinfoLog indvdlinfoLog = new IndvdlinfoLog();
    indvdlinfoLog.setAtnddu("열람");
    indvdlinfoLog.setTrtmntPurps("강좌 접수현황 엑셀다운로드");
    indvdlinfoLog.setSearchWrd(eduCourseAppVO.getSearchKrwd());
    indvdlinfoLogService.insertIndvdlinfoLog(request, indvdlinfoLog);


    ExcelUtil.excelUpdtDown("appList.xls", null, target, request, response);
  }

	@RequestMapping(value="/neo/courseAppSearchChangeExcelDown.do", method=RequestMethod.GET)
	public void courseAppSearchChangeExcelDown(
			EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			, HttpServletResponse response
			) throws Exception {

		//강사소개분류
        Map<String, String> fei01 = cmmUseService.selectCmmnDetailCodeMap("FEI01");

        Map<String, String> sex = new HashMap<String, String>();
		sex.put("0", "남");
		sex.put("1", "여");

		eduCourseAppVO.setSrcResult("");

		int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
		eduCourseAppVO.setFirstIndex(0);
		eduCourseAppVO.setLastIndex(totCnt);
		List<EduCourseApp> eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);

		//해당 년도, 해당 분기 강의 목록(select박스).
		EduCourseVO eduCourseVO = new EduCourseVO();
		eduCourseVO.setSrcQuarter(eduCourseAppVO.getSrcQuarter());
		eduCourseVO.setSrcYear(eduCourseAppVO.getSrcYear());
		eduCourseVO.setFirstIndex(0);
		eduCourseVO.setLastIndex(eduCourseService.selectEduCourseTotCnt(eduCourseVO));
		List<EduCourseVO> eduCourseList = eduCourseService.selectEduCourseList(eduCourseVO);
		Map<Integer, EduCourseVO> eduCourseMap = new HashMap<Integer, EduCourseVO>();
		for (EduCourseVO tempEduCourseVO : eduCourseList) {
			eduCourseMap.put(tempEduCourseVO.getCourse(), tempEduCourseVO);
		}

		ArrayList<ExcelUpdtVO> target = new ArrayList<ExcelUpdtVO>();

		target.add(new ExcelUpdtVO(0, 0, 0, false, eduCourseAppVO.getSrcYear()+"년 "+eduCourseAppVO.getSrcQuarter()+"기 접수자 현황"));	//yyyy년 n기 접수자 현황

		int courseCnt = 2;
		for (EduCourseApp eduCourseApp : eduCourseAppList) {
			target.add(new ExcelUpdtVO(0, courseCnt, 0, false, fei01.get(eduCourseMap.get(eduCourseApp.getCourse()).getCategory())));	//분야
			target.add(new ExcelUpdtVO(0, courseCnt, 1, false, eduCourseMap.get(eduCourseApp.getCourse()).getTitle()));	//강좌명
			target.add(new ExcelUpdtVO(0, courseCnt, 2, false, eduCourseApp.getName()));	//이름
			target.add(new ExcelUpdtVO(0, courseCnt, 3, false, DateUtil.toDateFormat(eduCourseApp.getBirth(), "yyyyMMddHHmmss", "yyyy-MM-dd")));	//생년월일
			target.add(new ExcelUpdtVO(0, courseCnt, 4, false, sex.get(eduCourseApp.getSex())));	//성별
			target.add(new ExcelUpdtVO(0, courseCnt, 5, false, eduCourseApp.getMobile()));	//핸드폰번호
			target.add(new ExcelUpdtVO(0, courseCnt, 6, false, eduCourseApp.getEmail()));	//이메일
			target.add(new ExcelUpdtVO(0, courseCnt, 7, true, DateUtil.toDateFormat(eduCourseApp.getRdate(), "yyyyMMddHHmmss", "yyyy-MM-dd")));	//신청일
			courseCnt++;
		}

		ExcelUtil.excelUpdtDown("appChange.xls", null, target, request, response);

	}

	@RequestMapping(value="/neo/courseAppPayListExcelDown.do", method=RequestMethod.GET)
	public void courseAppPayListExcelDown(
			EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			, HttpServletResponse response
			) throws Exception {

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");

		// 회원 접수 상태
		Map<String, String> ests03 = cmmUseService.selectCmmnDetailCodeMap("ESTS03");

		eduCourseAppVO.setSrcResult("");

		//페이징
		int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
		eduCourseAppVO.setFirstIndex(0);
		eduCourseAppVO.setLastIndex(totCnt);
		List<EduCourseApp> eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);

		ArrayList<ExcelUpdtVO> target = new ArrayList<ExcelUpdtVO>();

		target.add(new ExcelUpdtVO(0, 0, 0, false, eduCourseAppVO.getSrcYear()+"년 "+eduCourseAppVO.getSrcQuarter()+"기 온라인 결제현황"));	//yyyy년 n기 접수자 현황

		int courseCnt = 2;
		for (EduCourseApp eduCourseApp : eduCourseAppList) {
			target.add(new ExcelUpdtVO(0, courseCnt, 0, false, eduCourseApp.getPname()));	//강좌명
			target.add(new ExcelUpdtVO(0, courseCnt, 1, false, ""+eduCourseApp.getAmount()));	//수강료
			target.add(new ExcelUpdtVO(0, courseCnt, 2, false, eduCourseApp.getOid()));	//접수번호
			target.add(new ExcelUpdtVO(0, courseCnt, 3, false, eduCourseApp.getTid()));	//거래번호
			target.add(new ExcelUpdtVO(0, courseCnt, 4, false, ests02.get(eduCourseApp.getType())));	//접수구분
			target.add(new ExcelUpdtVO(0, courseCnt, 5, false, eduCourseApp.getName()));	//이름
			target.add(new ExcelUpdtVO(0, courseCnt, 6, false, DateUtil.toDateFormat(eduCourseApp.getBirth(), "yyyyMMddHHmmss", "yyyy-MM-dd")));	//생년월일
			target.add(new ExcelUpdtVO(0, courseCnt, 7, false, eduCourseApp.getMobile()));	//핸드폰번호
			target.add(new ExcelUpdtVO(0, courseCnt, 8, false, eduCourseApp.getEmail()));	//이메일
			target.add(new ExcelUpdtVO(0, courseCnt, 9, false, ests03.get(eduCourseApp.getStatus())));	//접수상태
			String payStatus = "";
			if((!StringUtil.isEmpty(eduCourseApp.getPaystatus()) && !(eduCourseApp.getCancelstatus().equals("1")))){
				payStatus = "결제완료";
			}else if((!StringUtil.isEmpty(eduCourseApp.getPaystatus()) && (eduCourseApp.getCancelstatus().equals("1")))){
				payStatus = "환불";
			}
			target.add(new ExcelUpdtVO(0, courseCnt, 10, false, payStatus));	//결제상태
			target.add(new ExcelUpdtVO(0, courseCnt, 11, true, DateUtil.toDateFormat(eduCourseApp.getRdate(), "yyyyMMddHHmmss", "yyyy-MM-dd")));	//신청일
			courseCnt++;
		}

		ExcelUtil.excelUpdtDown("payList.xls", null, target, request, response);

	}

  @RequestMapping(value = "/neo/courseAppCompListExcelDown.do", method = RequestMethod.GET)
  public void courseAppCompListExcelDown(
    EduCourseAppVO eduCourseAppVO,
    HttpServletRequest request,
    HttpServletResponse response
  ) throws Exception {
    logger.debug("\n\t>> /neo/courseAppCompListExcelDown.do <<");
    HttpRequestUtil.requestLog(request);


    eduCourseAppVO.setSrcQuarter(null);
    eduCourseAppVO.setSrcYear(null);
    if( null == eduCourseAppVO.getSrcResult() || "".equals(eduCourseAppVO.getSrcResult()) ) {
      eduCourseAppVO.setSrcResult("comp");
    }

    // 선택된 강좌 정보
    EduCourse tmpEduCourse = new EduCourse();
    tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
    EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);

    Map<String, String> sex = new HashMap<String, String>();
    sex.put("0", "남");
    sex.put("1", "여");

    // 페이징
    int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
    eduCourseAppVO.setFirstIndex(0);
    eduCourseAppVO.setLastIndex(totCnt);
    List<EduCourseApp> eduCourseAppList =
        eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);

    ArrayList<ExcelUpdtVO> target = new ArrayList<ExcelUpdtVO>();
    
    String title = eduCourse.getTitle();
    switch( eduCourseAppVO.getSrcResult() ) {
      case "comp" : title += " 수료 ";
        break;
      case "prog" : title += " 미수료 ";
        break;
    }

    // 강좌명 수료 현황
    target.add(new ExcelUpdtVO(0, 0, 0, false, title + "현황"));

    // 연번
    int cnt = 1;
    int courseCnt = 2;
    for (EduCourseApp eduCourseApp : eduCourseAppList) {
      // 연번
      target.add(new ExcelUpdtVO(0, courseCnt, 0, false, "" + cnt++));
      // 교육기간
      target.add(new ExcelUpdtVO(0, courseCnt, 1, false,
          DateUtil.toDateFormat(eduCourseApp.getEsdate(), "yyyyMMddHHmmss", "yyyy-MM-dd") + "  ~"
              + DateUtil.toDateFormat(eduCourseApp.getEedate(), "yyyyMMddHHmmss", "yyyy-MM-dd")));
      // 성별
      target.add(new ExcelUpdtVO(0, courseCnt, 2, false, sex.get(eduCourseApp.getSex())));
      // 이름
      target.add(new ExcelUpdtVO(0, courseCnt, 3, false, eduCourseApp.getName()));
      // 생년월일
      target.add(new ExcelUpdtVO(0, courseCnt, 4, false,
          DateUtil.toDateFormat(eduCourseApp.getBirth(), "yyyyMMddHHmmss", "yyyy-MM-dd")));
      // 핸드폰번호
      target.add(new ExcelUpdtVO(0, courseCnt, 5, false, eduCourseApp.getMobile()));
      // 소속
      target.add(new ExcelUpdtVO(0, courseCnt, 6, false, eduCourseApp.getCompany()));
      // 직위
      target.add(new ExcelUpdtVO(0, courseCnt, 7, false, eduCourseApp.getPosition()));
      // 비고
      target.add(new ExcelUpdtVO(0, courseCnt, 8, true, eduCourseApp.getBigo()));
      
      courseCnt++;
    }

    ExcelUtil.excelUpdtDown("appResult.xls", null, target, request, response);

  }

	@RequestMapping(value="/neo/appPayListExcelDown.do", method=RequestMethod.GET)
	public void appPayListExcelDown(
			EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			, HttpServletResponse response
			) throws Exception {


		// 결제 수단
		Map<String, String> paty01 = cmmUseService.selectCmmnDetailCodeMap("PATY01");

		Map<String, String> exem01 = cmmUseService.selectCmmnDetailCodeMap("EXEM01");


		eduCourseAppVO.setSrcResult("");

		//페이징
		int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
		eduCourseAppVO.setFirstIndex(0);
		eduCourseAppVO.setLastIndex(totCnt);
		List<EduCourseApp> eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);

		ArrayList<ExcelUpdtVO> target = new ArrayList<ExcelUpdtVO>();

		target.add(new ExcelUpdtVO(0, 0, 0, false, eduCourseAppVO.getSrcYear()+"년 "+eduCourseAppVO.getSrcQuarter()+"기 온라인 결제현황"));	//yyyy년 n기 온라인 결제현황

		int courseCnt = 4;
		int cnt = 1;	//연번
		int sum=0;
		for (EduCourseApp eduCourseApp : eduCourseAppList) {
			target.add(new ExcelUpdtVO(0, courseCnt, 0, false, ""+cnt++));	//연변
			if("".equals(eduCourseApp.getPaydate())){
				target.add(new ExcelUpdtVO(0, courseCnt, 1, true, ""));	//수납일자
			}else{
				target.add(new ExcelUpdtVO(0, courseCnt, 1, true, DateUtil.toDateFormat(eduCourseApp.getPaydate(), "yyyyMMddHHmmss", "yyyy-MM-dd")));	//수납일자
			}
			if("".equals(eduCourseApp.getPaydate())){
				target.add(new ExcelUpdtVO(0, courseCnt, 2, true, ""));	//처리일자
			}else{
				target.add(new ExcelUpdtVO(0, courseCnt, 2, true, DateUtil.toDateFormat(DateUtil.getSdateAddDay(eduCourseApp.getPaydate(), 1), "yyyyMMddHHmmss", "yyyy-MM-dd")));	//처리일자
			}
			target.add(new ExcelUpdtVO(0, courseCnt, 3, false, eduCourseApp.getPname()));	//강좌명
			target.add(new ExcelUpdtVO(0, courseCnt, 4, false, eduCourseApp.getName()));	//성명
			target.add(new ExcelUpdtVO(0, courseCnt, 5, false, String.format("%,d", eduCourseApp.getAmount())));	//수강료
			target.add(new ExcelUpdtVO(0, courseCnt, 6, true, paty01.get(eduCourseApp.getPaytype())));	//수납방법
			target.add(new ExcelUpdtVO(0, courseCnt, 7, false, eduCourseApp.getMobile()));	//연락처
			target.add(new ExcelUpdtVO(0, courseCnt, 8, true, exem01.get(eduCourseApp.getExempted())));	//면제자
			target.add(new ExcelUpdtVO(0, courseCnt, 9, true, eduCourseApp.getBigo()));	//비고
			sum = eduCourseApp.getAmount() + sum;
			courseCnt++;
		}
		target.add(new ExcelUpdtVO(0, 3, 5, false, String.format("%,d", sum)));	//연변
		ExcelUtil.excelUpdtDown("appPayList.xls", null, target, request, response);

	}

}
