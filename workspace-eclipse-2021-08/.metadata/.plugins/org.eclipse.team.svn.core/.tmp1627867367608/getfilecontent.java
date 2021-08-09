package kr.co.hanshinit.NeoCMS.cop.edu.cos.app.web;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import egovframework.com.cmm.LoginVO;
import kr.co.hanshinit.NeoCMS.cmm.service.CmmUseService;
import kr.co.hanshinit.NeoCMS.cmm.stereotype.Interceptor;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.SessionUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.EduCourseApp;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.EduCourseAppService;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.EduCourseAppVO;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.app.service.impl.EduCourseAppDAO;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.service.EduCourse;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.service.EduCourseService;
import kr.co.hanshinit.NeoCMS.cop.edu.cos.service.EduCourseVO;
import kr.co.hanshinit.NeoCMS.cop.qsm.rep.service.QestnarRespond;
import kr.co.hanshinit.NeoCMS.cop.qsm.rep.service.QestnarRspnsService;
import kr.co.hanshinit.NeoCMS.tag.pagination.NeoPaginationInfo;
import kr.co.hanshinit.NeoCMS.uat.uia.service.LoginUtil;
import kr.co.hanshinit.NeoCMS.uss.umt.service.UserInfo;
import kr.co.hanshinit.NeoCMS.uss.umt.service.UserInfoService;

/**
 * 안쓰는 경로는 시발 왜 냄겨둔거냐?
 * 이게 쓰는거냐 아님 진짜 안쓰는건데 안지운거냐 시방
 * 그렇게 일하고 월급 받으면 안 부끄럽냐?
 */

@Controller
public class CourseAppWebController {

  /** cmmUseService */
  @Resource(name = "cmmUseService")
  private CmmUseService cmmUseService;

  /** eduCourseAppService */
  @Resource(name = "eduCourseAppService")
  EduCourseAppService eduCourseAppService;

  /** eduCourseService */
  @Resource(name = "eduCourseService")
  private EduCourseService eduCourseService;

  /** userInfoService */
  @Resource(name = "userInfoService")
  private UserInfoService userInfoService;

  /** eduCourseAppDAO */
  @Resource(name = "eduCourseAppDAO")
  private EduCourseAppDAO eduCourseAppDAO;

  @Resource(name = "qestnarRspnsService")
  private QestnarRspnsService qestnarRspnsService;

  @Interceptor("templateBinding")
  @RequestMapping( value = "/{siteId}/courseAppList.do", method = RequestMethod.GET)
  public String courseAppList(
    ModelMap model
    , @PathVariable("siteId") String siteId
    , EduCourseAppVO eduCourseAppVO
    , HttpServletRequest request
  ) throws Exception {

    model.addAttribute("eduCourseAppVO", eduCourseAppVO);

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

    return "NeoCMS/cop/edu/cos/app/courseAppList";
  }



  /**
   * [사용자] 온라인 교육 센터 교육 신청 페이지.
   */
  @Interceptor("templateBinding")
  @RequestMapping(value = "/{siteId}/courseAppRegist.do", method = RequestMethod.GET)
  public String courseAppRegist(
      ModelMap model
      , @PathVariable("siteId") String siteId
      , @RequestParam("key") int key
      , EduCourseAppVO eduCourseAppVO
      , EduCourseVO eduCourseVO
      , HttpServletRequest request
    ) throws Exception {

    model.addAttribute("eduCourseAppVO", eduCourseAppVO);
    model.addAttribute("eduCourseVO", eduCourseVO);

    String user = LoginUtil.getLoginId(request.getSession());

    eduCourseAppVO.setUser(user);

    if ( null == user || "".equals(user)) {
      return cmmUseService.redirectMsg(model, "로그인 페이지로 이동합니다.", "/" + siteId + "/login.do?key=29");
    }

    // 회원 정보
    UserInfo userInfo = userInfoService.selectUserInfo(user);
    model.addAttribute("userInfo", userInfo);


    int appChk = eduCourseAppService.selectEduCourseAppChk(eduCourseAppVO);
    if (appChk != 0) {
      return cmmUseService.redirectMsg(model, "이미 신청하신 강좌입니다.", "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse());
    }

    /**
     * 20200901
     * 동일 그룹 강좌 제어를 오류라 하여 제거함.
     *
     * 정책기획과 이승호
     * 온라인법정교육센터 회계교육이 (1)~(5) 지정기부금관리 및 개정재무회계규칙으로 묶여 있어,
     * (1)번 동영상 수강신청 후 (2)번 동영상 수강신청 시 같은 그룹으로 묶여 수강신청이 안되고 있습니다.
     * 교육생들 문의전화가 많아서 빠르게 처리해주시면 감사드리겠습니다.
     */
//    String courseGroup = "edu" + eduCourseVO.getCourse();
//    List<EduCourseVO> eduCourseChkList = eduCourseService.selectEduCourseChk(user);
//    for (EduCourseVO temp : eduCourseChkList) {
//      String courseGroups = temp.getCourseGroup();
//      if (courseGroups != null) {
//        String[] strings = courseGroups.split(",");
//        for (String chkString : strings) {
//          if (chkString.equals(courseGroup)) {
//            return cmmUseService.redirectMsg(
//              model
//              , "같은 그룹의 강좌를 신청하셨습니다.\\n : " + temp.getTitle()
//              , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
//            );
//          }
//        }
//      }
//    }

    // 강좌 선택
    EduCourse temp = new EduCourse();
    temp.setCourse(eduCourseAppVO.getCourse());
    EduCourseVO eduCourse = eduCourseService.selectEduCourse(temp, request);

    if (!"0".equals(eduCourse.getApptype())) {
      return cmmUseService.redirectMsg(
        model, "온라인 강좌가 아닙니다."
        , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
      );
    }



//    if ("Y".equals(eduCourse.getPricetype()) && !"FEISJ14".equals(eduCourse.getCategory())) {
//      int cnt = (int) Math.round((double) eduCourse.getCount() / 2);
//      if (eduCourse.getConfirmByOnline() >= cnt) {
//        return cmmUseService.redirectMsg(
//          model, "정원이 초과되어 접수할수 없습니다."
//          , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
//        );
//      }
//    } else {
//      if ( eduCourse.getConfirm() >= eduCourse.getCount()
//          && !"FEISJ14".equals(eduCourse.getCategory())
//      ) {
//        return cmmUseService.redirectMsg(
//          model, "정원이 초과되어 접수할수 없습니다."
//          , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
//        );
//      }
//    }

    if (eduCourse.getConfirm() >= eduCourse.getCount() && !"FEISJ14".equals(eduCourse.getCategory())) {
      return cmmUseService.redirectMsg(
        model, "정원이 초과되어 접수할수 없습니다."
        , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
      );
    }

    if(!"2".equals(eduCourse.getStatus())){
      return cmmUseService.redirectMsg(
        model, "신청접수 강좌가 아닙니다."
        , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
      );
    }

    model.addAttribute("eduCourse", eduCourse);

    // 면제자 사유
    Map<String, String> exem01 = cmmUseService.selectCmmnDetailCodeMap("EXEM01");
    model.addAttribute("exem01", exem01);

    Date today = new Date();
    model.addAttribute("today", today);

    return "NeoCMS/cop/edu/cos/app/webCourseAppRegist";
  }



  /**
   * [사용자] 온라인 법정 교육 센터 교육 신청 처리 로직.
   */
  @Interceptor("templateBinding")
  @RequestMapping(value = "/{siteId}/courseAppRegist.do", method = RequestMethod.POST)
  public String courseAppRegistDo(
      ModelMap model
      , @PathVariable("siteId") String siteId
      , @RequestParam("key") int key
      , EduCourseApp eduCourseApp
      , EduCourseAppVO eduCourseAppVO
      , EduCourseVO eduCourseVO
      , HttpServletRequest request
  ) throws Exception {

    String user = LoginUtil.getLoginId(request.getSession());

    eduCourseAppVO.setUser(user);

    if (null == user || "".equals(user)) {
      return cmmUseService.redirectMsg(model, "로그인 페이지로 이동합니다.", "/" + siteId + "/login.do?key=29");
    }

    // 회원 정보
    //UserInfo userInfo = userInfoService.selectUserInfo(user);


    int appChk = eduCourseAppService.selectEduCourseAppChk(eduCourseAppVO);
    if (appChk != 0) {
      return cmmUseService.redirectMsg(
        model, "이미 신청하신 강좌입니다."
        , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
      );
    }

    /**
     * 20200901
     * 동일 그룹 강좌 제어를 오류라 하여 제거함.
     *
     * 정책기획과 이승호
     * 온라인법정교육센터 회계교육이 (1)~(5) 지정기부금관리 및 개정재무회계규칙으로 묶여 있어,
     * (1)번 동영상 수강신청 후 (2)번 동영상 수강신청 시 같은 그룹으로 묶여 수강신청이 안되고 있습니다.
     * 교육생들 문의전화가 많아서 빠르게 처리해주시면 감사드리겠습니다.
     */
//    String courseGroup = "edu" + eduCourseVO.getCourse();
//    List<EduCourseVO> eduCourseChkList = eduCourseService.selectEduCourseChk(user);
//    for (EduCourseVO temp : eduCourseChkList) {
//      String courseGroups = temp.getCourseGroup();
//      if (courseGroups != null) {
//        String[] strings = courseGroups.split(",");
//        for (String chkString : strings) {
//          if (chkString.equals(courseGroup)) {
//            return cmmUseService.redirectMsg(
//              model, "같은 그룹의 강좌를 신청하셨습니다.\\n : " + temp.getTitle()
//              , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
//            );
//          }
//        }
//      }
//    }

    // 강좌 선택
    EduCourse temp = new EduCourse();
    temp.setCourse(eduCourseAppVO.getCourse());
    EduCourseVO eduCourse = eduCourseService.selectEduCourse(temp, request);

    if (!"0".equals(eduCourse.getApptype())) {
      return cmmUseService.redirectMsg(
        model, "온라인 강좌가 아닙니다."
        , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
      );
    }


//    if ("Y".equals(eduCourse.getPricetype())) {
//      int cnt = (int) Math.round((double) eduCourse.getCount() / 2);
//      if (eduCourse.getConfirmByOnline() >= cnt && !"FEISJ14".equals(eduCourse.getCategory())) {
//        return cmmUseService.redirectMsg(
//          model, "정원이 초과되어 접수할수 없습니다."
//          , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
//        );
//      }
//    } else {
//      if (
//          eduCourse.getConfirm() >= eduCourse.getCount()
//          && !"FEISJ14".equals(eduCourse.getCategory())
//      ) {
//        return cmmUseService.redirectMsg(
//          model, "정원이 초과되어 접수할수 없습니다."
//          , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
//        );
//      }
//    }

    if(eduCourse.getConfirm() >= eduCourse.getCount() && !"FEISJ14".equals(eduCourse.getCategory()) ) {
      return cmmUseService.redirectMsg(
        model, "정원이 초과되어 접수할수 없습니다."
        , "/"+siteId+"/courseView.do?key="+key+"&course="+eduCourseAppVO.getCourse()
      );
    }


    if (!"2".equals(eduCourse.getStatus())) {
      return cmmUseService.redirectMsg(
        model, "신청접수 강좌가 아닙니다."
        , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
      );
    }


    //  강의 등록

    int appKey = eduCourseAppService.insertEduCourseApp(eduCourseApp, request);
//
//    String price = StringUtil.nvl(eduCourseVO.getPrice2());
//    if ("".equals(price)) {
//      price = "0";
//    }
//
//    if ("Y".equals(StringUtil.nvl(eduCourseVO.getPricetype())) && !"0".equals(price)) {
//      return cmmUseService.redirectMsg(
//        model, "유료강의입니다. 결제페이지로 이동합니다. 결제완료하셔야 신청완료됩니다."
//        , "/" + siteId + "/setleReqest.do?key=" + key + "&app=" + appKey
//      );
//    }


    return cmmUseService.redirectMsg(
      model, "등록되었습니다."
      , "/" + siteId + "/courseView.do?key=" + key + "&course=" + eduCourseAppVO.getCourse()
    );

  }










	@RequestMapping(value="/{siteId}/courseAppUpdt.do", method=RequestMethod.GET)
	public String courseAppUpdt(ModelMap model
			, @PathVariable("siteId") String siteId
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

		return "NeoCMS/cop/edu/cos/app/courseAppUpdt";
	}



	@RequestMapping(value="/{siteId}/courseAppUpdt.do", method=RequestMethod.POST)
	public String courseAppUpdtDo(ModelMap model
			, @PathVariable("siteId") String siteId
			, EduCourseApp eduCourseApp
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.updateEduCourseApp(eduCourseApp, request);

		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/{siteId}/courseAppList.do?course="+eduCourseApp.getCourse());
	}



	@RequestMapping(value="/{siteId}/courseAppChange.do", method=RequestMethod.GET)
	public String courseAppChange(ModelMap model
			, @PathVariable("siteId") String siteId
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.updateEduCourseAppStatus(eduCourseAppVO, request);

		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/{siteId}/courseAppList.do?course="+eduCourseAppVO.getCourse());
	}


  /**
   * [사용자] 온라인법정교육센터 나의 수강 목록 페이지
   */
  @Interceptor("templateBinding")
  @RequestMapping(value = "/{siteId}/myEduCourseAppList.do", method = RequestMethod.GET)
  public String myEduCourseAppList(
    ModelMap model
    , @PathVariable("siteId") String siteId
    , @RequestParam("key") int key
    , EduCourseAppVO eduCourseAppVO
    , HttpServletRequest request
    , HttpSession session
  ) throws Exception {

    LoginVO loginVO = (LoginVO) SessionUtil.getSessionValue(session, "loginVO");

    if (loginVO == null) {
      return cmmUseService.backMsg(model, "로그인 후 이용해주세요.");
    }

    String user = LoginUtil.getLoginId(request.getSession());

    eduCourseAppVO.setUser(user);

    model.addAttribute("eduCourseAppVO", eduCourseAppVO);

    // 페이징
    int totCnt = eduCourseAppService.selectMyEduCourseAppTotCnt(eduCourseAppVO);
    NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
    model.addAttribute("paginationInfo", neoPaginationInfo);

    List<EduCourseApp> myEduCourseAppList =
        eduCourseAppService.selectMyEduCourseAppList(eduCourseAppVO);
    model.addAttribute("myEduCourseAppList", myEduCourseAppList);

    // 환불 상태
    Map<String, String> cast01 = cmmUseService.selectCmmnDetailCodeMap("CAST01");
    model.addAttribute("cast01", cast01);

    return "NeoCMS/cop/edu/cos/app/webMyEduCourseAppList";
  }




  /**
   * [사용자] 강좌 접수 상태를 변경 페이지.
   *
   * @param eduCourseAppVO
   * @return "NeoCMS/cop/edu/cos/app/myCourseAppChange"
   * @throws Exception
   */
  @Interceptor("templateBinding")
  @RequestMapping(value = "/{siteId}/myCourseAppChange.do", method = RequestMethod.GET)
  public String myCourseAppChange(
      ModelMap model
      , EduCourseAppVO eduCourseAppVO
      , @PathVariable("siteId") String siteId
      , @RequestParam("key") int key
      , HttpServletRequest request
  ) throws Exception {
    model.addAttribute("siteId", siteId);
    model.addAttribute("key", key);
    model.addAttribute("eduCourseAppVO", eduCourseAppVO);

    return "NeoCMS/cop/edu/cos/app/webMyCourseAppChange";
  }



	/**
	 * [사용자] 강좌 접수 상태를 변경 로직.
	 * @param eduCourseAppVO
	 * @return "/neo/courseAppList.do?course="+course
	 * @throws Exception
	 */
  @RequestMapping(value = "/{siteId}/myCourseAppChange.do", method = RequestMethod.POST)
  public String myCourseAppChangeDo(
      ModelMap model
      , EduCourseAppVO eduCourseAppVO
      , @PathVariable("siteId") String siteId
      , @RequestParam("key") int key
      , HttpServletRequest request
  ) throws Exception {

    if (!"".equals(StringUtil.nvl(eduCourseAppVO.getTid()))) {
      eduCourseAppVO.setCancelamount(0);
      eduCourseAppVO.setCanceldate(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
      // eduCourseAppVO.setCanceldateRes(eduCourseAppVO.getRemoveText());
      eduCourseAppVO.setCancelstatus("2");
      eduCourseAppService.updateEduCourseAppCancel(eduCourseAppVO, request);
      eduCourseAppService.updateEduCourseAppStatus(eduCourseAppVO, request);
    } else {
      eduCourseAppService.updateEduCourseAppStatus(eduCourseAppVO, request);
    }

    return cmmUseService.redirectMsg(
      model, "수정되었습니다."
      , "/" + siteId + "/myEduCourseAppList.do?key=" + key
    );
  }





	@Interceptor("templateBinding")
	@RequestMapping(value="/{siteId}/myCourseAppList.do", method=RequestMethod.GET)
	public String myCourseAppList(ModelMap model
			, @PathVariable("siteId") String siteId
			, @RequestParam("key") int key
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

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



		//페이징
		int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
		NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
		model.addAttribute("paginationInfo", neoPaginationInfo);

		List<EduCourseApp> eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);
		model.addAttribute("eduCourseAppList", eduCourseAppList);

		return "NeoCMS/cop/edu/cos/app/webMyCourseAppList";
	}

	/**
	 * 강좌 수료 목록으로 이동한다.
	 * @param eduCourseAppVO
	 * @return "NeoCMS/cop/edu/cos/app/courseAppCompList"
	 * @throws Exception
	 */
	@Interceptor("templateBinding")
	@RequestMapping(value="/{siteId}/myCourseAppCompList.do", method=RequestMethod.GET)
	public String myCourseAppCompList(ModelMap model
			, EduCourseAppVO eduCourseAppVO
			, @PathVariable("siteId") String siteId
			, @RequestParam("key") int key
			, HttpServletRequest request
			) throws Exception {

		model.addAttribute("eduCourseAppVO", eduCourseAppVO);

		// 선택된 강좌 정보
		EduCourse tmpEduCourse = new EduCourse();
		tmpEduCourse.setCourse(eduCourseAppVO.getCourse());
		EduCourseVO eduCourse = eduCourseService.selectEduCourse(tmpEduCourse, request);
		model.addAttribute("eduCourse", eduCourse);

		// 강좌 접수 방법
		Map<String, String> ests02 = cmmUseService.selectCmmnDetailCodeMap("ESTS02");
		model.addAttribute("ests02", ests02);

		//페이징
		int totCnt = eduCourseAppService.selectEduCourseAppTotCnt(eduCourseAppVO);
		NeoPaginationInfo neoPaginationInfo = eduCourseAppVO.getNeoPaginationInfo(totCnt);
		model.addAttribute("paginationInfo", neoPaginationInfo);

		List<EduCourseApp> eduCourseAppList = eduCourseAppService.selectEduCourseAppList(eduCourseAppVO);
		model.addAttribute("eduCourseAppList", eduCourseAppList);

		return "NeoCMS/cop/edu/cos/app/webMyCourseAppCompList";
	}

	@Interceptor("templateBinding")
	@RequestMapping(value="/{siteId}/onlineCourseAppStaUpdt.do")
	public String onlineCourseAppStaUpdt(ModelMap model
			, @PathVariable("siteId") String siteId
			, @RequestParam("key") int key
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			, @RequestParam("appList") String appList
			, @RequestParam("appResult") String appResult
			) throws Exception {

		eduCourseAppService.onlineCourseAppStaUpdt(eduCourseAppVO);

		EduCourseAppVO temp = new EduCourseAppVO();
		temp.setResult(appResult);
		temp.setEsdate(eduCourseAppVO.getEsdate());
		temp.setEedate(eduCourseAppVO.getEedate());
		String edate = DateUtil.getNowDateTime("yyyyMMddHHmmss");  // 등록일
		temp.setEdate(edate);
		temp.setApp(Integer.parseInt(appList));
		eduCourseAppService.updateEduCourseAppResult(temp, request);

		return cmmUseService.redirectMsg(model, "수강 완료되었습니다.", "/"+siteId+"/myEduCourseAppList.do?key="+key);
	}

	@RequestMapping(value="/{siteId}/courseAppCompPrint.do", method=RequestMethod.GET)
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


				if(eduCourse.getQestnarNo() != 0) {
					QestnarRespond qestnarRespond = new QestnarRespond();
					LoginVO loginVO = LoginUtil.getLoginVO(request.getSession());

					qestnarRespond.setQestnarNo(eduCourse.getQestnarNo());
					qestnarRespond.setUserNm(LoginUtil.getLoginNm(request.getSession()));
					qestnarRespond.setUserDplctCode(loginVO.getId());

					int cnt = qestnarRspnsService.selectQestnarRespondCnt(qestnarRespond);

					if(cnt == 0) {
						return cmmUseService.redirectMsg(model, "설문조사후 수료증을 발급 받으실수 있습니다.", "/www/addQestnarRspnsView.do?key=54&qestnarNo="+eduCourse.getQestnarNo()+"&pageUnit=10&pageIndex=1&searchCnd=all");
					}
				}

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


		if(eduCompList.size() == 1) {
			return "NeoCMS/cop/edu/cos/app/webCourseAppCompPrint";
		}else {
			return "NeoCMS/cop/edu/cos/app/webCourseAppCompPrint2";
		}
	}

	/*
	@RequestMapping(value="/{siteId}/courseAppDelete.do", method=RequestMethod.GET)
	public String courseAppDelete(ModelMap model
			, @PathVariable("siteId") String siteId
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.deleteEduCourseApp(eduCourseAppVO);

		return cmmUseService.redirectMsg(model, "삭제되었습니다.", "/{siteId}/courseAppList.do?course="+eduCourseAppVO.getCourse());
	}

	@RequestMapping(value="/{siteId}/courseAppResult.do", method=RequestMethod.GET)
	public String courseAppResult(ModelMap model
			, @PathVariable("siteId") String siteId
			, EduCourseAppVO eduCourseAppVO
			, HttpServletRequest request
			) throws Exception {

		eduCourseAppService.updateEduCourseAppStatus(eduCourseAppVO);

		return cmmUseService.redirectMsg(model, "수정되었습니다.", "/{siteId}/courseAppList.do?course="+eduCourseAppVO.getCourse());
	}
	 */
}
