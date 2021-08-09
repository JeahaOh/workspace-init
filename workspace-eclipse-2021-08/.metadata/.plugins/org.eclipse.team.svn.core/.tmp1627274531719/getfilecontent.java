/************************************************************************************************
 * 통합예약
 * 		- 관리자 > 수강관리 > 접수 관리
 * 		- 관련 테이블
 * 				TN_EDC_ATNLC_MNGR
 *
 * @author (주)한신정보기술 개발3팀
 * @since 2018.10.07
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일            수정자        수정내용
 *  -------------      --------    ---------------------------
 *  2018.10.07 개발3팀     최초 생성
 *
 * </pre>
 ***************************************************************************************************/
package kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.web;

import java.io.PrintWriter;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springmodules.validation.commons.DefaultBeanValidator;
import kr.co.hanshinit.NeoCMS.cmm.service.CmmUseService;
import kr.co.hanshinit.NeoCMS.cmm.service.PaginationModel;
import kr.co.hanshinit.NeoCMS.cmm.service.ResponseJSON;
import kr.co.hanshinit.NeoCMS.cmm.stereotype.AccesType;
import kr.co.hanshinit.NeoCMS.cmm.stereotype.Interceptor;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.sym.cma.cdc.service.CmmnDetailCodeService;
import kr.co.hanshinit.NeoCMS.tag.pagination.NeoPaginationInfo;
import kr.co.hanshinit.NeoCMS.uat.uia.service.LoginUtil;
import kr.co.hanshinit.NeoCMS.uss.umt.service.UserInfo;
import kr.co.hanshinit.NeoCMS.uss.umt.service.UserInfoService;
import kr.co.hanshinit.NeoCop.cop.cmm.util.StaffLoginUtil;
import kr.co.hanshinit.NeoCop.cop.cmmnPaylog.service.CmmnPaylog;
import kr.co.hanshinit.NeoCop.cop.cmmnPaylog.service.CmmnPaylogService;
import kr.co.hanshinit.NeoCop.cop.cmmnPaylog.service.CmmnPaylogVO;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddField;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldService;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldVO;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOption;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionService;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.CodeCmmVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmCodeService;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmSmsService;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcLctreVO;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcMngr;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcMngrService;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcMngr.service.EdcAtnlcMngrVO;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcUser.service.EdcAtnlcUser;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcUser.service.EdcAtnlcUserBndl;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcUser.service.EdcAtnlcUserService;
import kr.co.hanshinit.NeoEdu.cop.edcAtnlcUser.service.EdcAtnlcUserVO;
import kr.co.hanshinit.NeoEdu.cop.edcInsttMngr.service.EdcInsttMngr;
import kr.co.hanshinit.NeoEdu.cop.edcInsttMngr.service.EdcInsttMngrService;
import kr.co.hanshinit.NeoEdu.cop.edcInsttMngr.service.EdcInsttMngrVO;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctre;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreService;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreVO;
import net.sf.json.JSONObject;

@Controller
@Interceptor("staffAuthorInterceptor")
public class EdcAtnlcMngrController {
	protected Logger logger  = LoggerFactory.getLogger(this.getClass());

	@Resource(name="cmmUseService")
	private CmmUseService cmmUseService;

	@Resource(name="cmmnDetailCodeService")
	private CmmnDetailCodeService cmmnDetailCodeService;

	@Resource(name="beanValidator")
	protected DefaultBeanValidator beanValidator;

	@Resource(name="edcAtnlcMngrService")
	private EdcAtnlcMngrService edcAtnlcMngrService;

	@Resource(name="edcAtnlcUserService")
	private EdcAtnlcUserService edcAtnlcUserService;

	/** 교육 관련 코드 조회 서비스 **/
	@Resource(name="edcCmmCodeService")
	private EdcCmmCodeService edcCmmCodeService;

	/** 강좌관리 서비스 **/
	@Resource(name="edcLctreService")
	private EdcLctreService edcLctreService;

	@Resource(name="userInfoService")
	private UserInfoService userInfoService;

	/** SMS 관련 서비스 **/
	@Resource(name="edcCmmSmsService")
	private EdcCmmSmsService edcCmmSmsService;

	/** 결제 관련 서비스  **/
	@Resource(name="cmmnPaylogService")
	private CmmnPaylogService cmmnPaylogService;

	/** 할인대상 조회(최초) **/
	@Resource(name="resveOptionService")
	private ResveOptionService resveOptionService;

	/** 프로그램 신청양신 추가 서비스 **/
	@Resource(name="resveAddFieldService")
	private ResveAddFieldService resveAddFieldService;

	/** 기관 정보 서비스 **/
	@Resource(name="edcInsttMngrService")
	private EdcInsttMngrService edcInsttMngrService;


	@ModelAttribute("list")
	EdcAtnlcLctreVO getSessionModelAttribute(HttpServletRequest request, @RequestParam(value="rep", required=false) String rep) throws Exception{
		EdcAtnlcLctreVO edcAtnlcLctreVO = new EdcAtnlcLctreVO();

		if("1".equals(rep))
			request.getSession().setAttribute("edcAtnlcLctre", edcAtnlcLctreVO);

		edcAtnlcLctreVO = (EdcAtnlcLctreVO)request.getSession().getAttribute("edcAtnlcLctre");
		Map<String, String> map = new HashMap<String, String>();
		map = PaginationModel.modelExtractMap(edcAtnlcLctreVO, map);
        List<NameValuePair> paramList = PaginationModel.convertParam(map);
        request.getSession().setAttribute("mparam", "&" + URLEncodedUtils.format(paramList, "UTF-8"));

		return edcAtnlcLctreVO;
	}


	@ModelAttribute("atnlcList")
	EdcAtnlcMngrVO getSessionModelAttribute2(HttpServletRequest request, @RequestParam(value="rep", required=false) String rep) throws Exception{
		EdcAtnlcMngrVO edcAtnlcMngrVO = new EdcAtnlcMngrVO();

		if("1".equals(rep))
			request.getSession().setAttribute("edcAtnlcMngr", edcAtnlcMngrVO);

		edcAtnlcMngrVO = (EdcAtnlcMngrVO)request.getSession().getAttribute("edcAtnlcMngr");
		Map<String, String> map = new HashMap<String, String>();
		map = PaginationModel.modelExtractMap(edcAtnlcMngrVO, map);
        List<NameValuePair> paramList = PaginationModel.convertParam(map);
        request.getSession().setAttribute("mparam", "&" + URLEncodedUtils.format(paramList, "UTF-8"));

		return edcAtnlcMngrVO;
	}
	
	
	@ModelAttribute("atnlcPayList")
	CmmnPaylogVO getSessionModelAttribute3(HttpServletRequest request, @RequestParam(value="rep", required=false) String rep) throws Exception{
		CmmnPaylogVO cmmnPaylogVO = new CmmnPaylogVO();

		if("1".equals(rep))
			request.getSession().setAttribute("cmmnPaylog", cmmnPaylogVO);

		cmmnPaylogVO = (CmmnPaylogVO)request.getSession().getAttribute("cmmnPaylog");
		Map<String, String> map = new HashMap<String, String>();
		map = PaginationModel.modelExtractMap(cmmnPaylogVO, map);
        List<NameValuePair> paramList = PaginationModel.convertParam(map);
        request.getSession().setAttribute("mparam", "&" + URLEncodedUtils.format(paramList, "UTF-8"));

		return cmmnPaylogVO;
	}
	

  /**
   * 수강관리 - 강좌 목록 조회(관리자)
   * 
   * @param edcAtnlcMngrVO
   * @param model
   * @return
   * @throws Exception
   */
  @Interceptor(value = {"templateBinding", "progrmAuthorBinding"}, type = AccesType.REJECT, code = "LIST", progrmId = "A01")
  @RequestMapping("/{siteId}/edcAtnlcMngrMngList.do")
  public String edcAtnlcMngrMngList(
    @RequestParam("key") int key,
    @PathVariable(value = "siteId") String siteId,
    @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO,
    @ModelAttribute("list") EdcAtnlcLctreVO edcAtnlcLctreVO,
    HttpServletRequest request,
    ModelMap model
  ) throws Exception {

    String userId = StaffLoginUtil.getLoginId(request.getSession());

    if (!StaffLoginUtil.isAdmin(request.getSession())) {
      if ("".equals(userId)) {
        edcAtnlcLctreVO.setAuthUserId("000000000000000000000000");
      } else {
        edcAtnlcLctreVO.setAuthUserId(userId);
      }
    }

    /** 코드 값 조회 **/
    CodeCmmVO vo = new CodeCmmVO();

    /** 기관 목록 조회 **/
    vo.setSiteId(siteId);
    vo.setSearchDeleteAt("N");
    vo.setSearchInsttGubun("EC");
    vo.setSearchUseAt("Y");
    List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

    if (edcAtnlcLctreVO != null) {
      
      edcAtnlcLctreVO.setSearchStartDate(
          StringUtil.strReplaceALL(edcAtnlcLctreVO.getSearchStartDate(), "-", ""));
      
      edcAtnlcLctreVO
          .setSearchEndDate(StringUtil.strReplaceALL(edcAtnlcLctreVO.getSearchEndDate(), "-", ""));
      
    } else {
      edcAtnlcLctreVO = new EdcAtnlcLctreVO();
    }

    int totCnt = edcAtnlcMngrService.selectEdcLctreAtnlcCnt(edcAtnlcLctreVO);
    NeoPaginationInfo neoPaginationInfo = edcAtnlcLctreVO.getNeoPaginationInfo(totCnt);
    List<EdcAtnlcLctreVO> edcAtnlcLctreList =
        edcAtnlcMngrService.selectEdcLctreAtnlcList(request, edcAtnlcLctreVO, insttList);

    edcAtnlcLctreVO.setSearchStartDate(
        DateUtil.getDateFormat(edcAtnlcLctreVO.getSearchStartDate(), "yyyy-mm-dd"));
    edcAtnlcLctreVO
        .setSearchEndDate(DateUtil.getDateFormat(edcAtnlcLctreVO.getSearchEndDate(), "yyyy-mm-dd"));

    // 예약그룹 목록
    model.addAttribute("insttList", insttList);
    model.addAttribute("totCnt", totCnt);
    model.addAttribute("edcAtnlcLctreList", edcAtnlcLctreList);
    model.addAttribute("paginationInfo", neoPaginationInfo);

    return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrList";
  }
  	
  	
  	
  @Interceptor(value = {"progrmAuthorBinding"}, type = AccesType.REJECT, code = "EXCEL", progrmId = "A01")
  @RequestMapping("/{siteId}/edcAtnlcMngrMngListExcel.do")
  public String edcAtnlcMngrMngListExcel(
    @RequestParam("key") int key,
    @PathVariable(value = "siteId") String siteId,
    @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO,
    @ModelAttribute("list") EdcAtnlcLctreVO edcAtnlcLctreVO,
    HttpServletRequest request,
    HttpServletResponse response,
    ModelMap model
  ) throws Exception {

    String userId = StaffLoginUtil.getLoginId(request.getSession());

    if (!StaffLoginUtil.isAdmin(request.getSession())) {
      if ("".equals(userId)) {
        edcAtnlcLctreVO.setAuthUserId("000000000000000000000000");
      } else {
        edcAtnlcLctreVO.setAuthUserId(userId);
      }
    }

    /** 코드 값 조회 **/
    CodeCmmVO vo = new CodeCmmVO();

    /** 기관 목록 조회 **/
    vo.setSiteId(siteId);
    vo.setSearchDeleteAt("N");
    vo.setSearchInsttGubun("EC");
    vo.setSearchUseAt("Y");
    List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

    if (edcAtnlcLctreVO != null) {
      
      edcAtnlcLctreVO.setSearchStartDate(
          StringUtil.strReplaceALL(edcAtnlcLctreVO.getSearchStartDate(), "-", ""));
      
      edcAtnlcLctreVO
          .setSearchEndDate(StringUtil.strReplaceALL(edcAtnlcLctreVO.getSearchEndDate(), "-", ""));
      
    } else {
      edcAtnlcLctreVO = new EdcAtnlcLctreVO();
    }

    edcAtnlcLctreVO.setPageUnit(999999999);

    int totCnt = edcAtnlcMngrService.selectEdcLctreAtnlcCnt(edcAtnlcLctreVO);
    NeoPaginationInfo neoPaginationInfo = edcAtnlcLctreVO.getNeoPaginationInfo(totCnt);
    List<EdcAtnlcLctreVO> edcAtnlcLctreList =
        edcAtnlcMngrService.selectEdcLctreAtnlcList(request, edcAtnlcLctreVO, insttList);

    edcAtnlcLctreVO.setSearchStartDate(
        DateUtil.getDateFormat(edcAtnlcLctreVO.getSearchStartDate(), "yyyy-mm-dd"));
    edcAtnlcLctreVO
        .setSearchEndDate(DateUtil.getDateFormat(edcAtnlcLctreVO.getSearchEndDate(), "yyyy-mm-dd"));

    // 예약그룹 목록
    model.addAttribute("insttList", insttList);
    model.addAttribute("totCnt", totCnt);
    model.addAttribute("edcAtnlcLctreList", edcAtnlcLctreList);
    model.addAttribute("paginationInfo", neoPaginationInfo);

    String filename = URLEncoder.encode("강좌접수목록.xls", "UTF-8");

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
    response.setHeader("Pragma", "public");
    response.setHeader("Content-charset", "UTF-8");
    response.setHeader("Content-Disposition", "attachment;filename=" + filename);

    return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrListExcel";
  }
	

  	/**
  	 * 수강관리 - 출석현황(관리자)
  	 * @param edcAtnlcMngrVO
  	 * @param edcAtnlcLctreVO
  	 * @param request
  	 * @param model
  	 * @return
  	 * @throws Exception
  	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/edcAtnlcAttendMngList.do")
	public String edcAtnlcAttendMngList(
					@RequestParam("key") int key
					, @PathVariable(value = "siteId") String siteId
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("list") EdcAtnlcLctreVO edcAtnlcLctreVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		String userId = StaffLoginUtil.getLoginId(request.getSession());
		if(!StaffLoginUtil.isAdmin(request.getSession())){
				if("".equals(StringUtil.strTrim(userId))){
					edcAtnlcLctreVO.setAuthUserId("0000000000000000000000000000000");
				}else{
					edcAtnlcLctreVO.setAuthUserId(userId);
				}
		}

		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 학과분류, 강좌분류(대분류), 6진분류(1차), 강좌접수 형태, 접수방법, 접수상태, 사용유무, 교육 방법
		vo.setSearchCodeType(
				"'LES_DIV', 'TCH_LEC_DIV1', 'LEC_DIV_LVL1', 'LEC_REG_CLASS', 'LEC_APP_TYPE', 'RCEPT_STTUS', 'USE_AT', 'LEC_TYPE' ");

		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);

		/** 기관 목록 조회 **/
		vo.setSiteId(siteId);
		vo.setSearchDeleteAt("N");
		vo.setSearchInsttGubun("EC");
		vo.setSearchUseAt("Y");
		List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

		edcAtnlcLctreVO.setSearchStartDate(StringUtil.strReplaceALL(edcAtnlcLctreVO.getSearchStartDate(), "-", ""));
		edcAtnlcLctreVO.setSearchEndDate(StringUtil.strReplaceALL(edcAtnlcLctreVO.getSearchEndDate(), "-", ""));
		
		int totCnt = edcAtnlcMngrService.selectEdcLctreAtnlcCnt(edcAtnlcLctreVO);
		NeoPaginationInfo neoPaginationInfo = edcAtnlcLctreVO.getNeoPaginationInfo(totCnt);
		List<EdcAtnlcLctreVO> edcAtnlcLctreList = edcAtnlcMngrService.selectEdcLctreAtnlcList(request, edcAtnlcLctreVO, insttList);

		model.addAttribute("listCodeMap", listCodeMap); 					// 코드값 목록 Map
		model.addAttribute("insttList", insttList); 									// 기관 목록

		model.addAttribute("totCnt", totCnt);
		model.addAttribute("edcAtnlcLctreList", edcAtnlcLctreList);
        model.addAttribute("paginationInfo", neoPaginationInfo);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcAttendList";
	}


  /**
   * 수강관리 - 수강생조회
   * 
   * @param edcAtnlcMngrVO
   * @param edcAtnlcLctreVO
   * @param request
   * @param model
   * @return
   * @throws Exception
   */
  @Interceptor(value = {"templateBinding", "progrmAuthorBinding"}, type = AccesType.REJECT, code = "LIST", progrmId = "A01")
  @RequestMapping("/{siteId}/edcAtnlcMngList.do")
  public String edcAtnlcMngList(
    @RequestParam("key") int key,
    @ModelAttribute("atnlcList") EdcAtnlcMngrVO edcAtnlcMngrVO,
    @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO,
    @PathVariable("siteId") String siteId, HttpServletRequest request, ModelMap model
  ) throws Exception {

    String userId = StaffLoginUtil.getLoginId(request.getSession());
    if (!StaffLoginUtil.isAdmin(request.getSession())) {
      if ("".equals(StringUtil.strTrim(userId))) {
        edcAtnlcLctreVO.setAuthUserId("0000000000000000000000000000000");
        edcAtnlcMngrVO.setAuthUserId("0000000000000000000000000000000");
      } else {
        edcAtnlcLctreVO.setAuthUserId(userId);
        edcAtnlcMngrVO.setAuthUserId(userId);
      }
    }

    int totCnt = 0;
    NeoPaginationInfo neoPaginationInfo;
    List<EdcAtnlcMngr> edcAtnlcMngrList;

    //String searchAll = StringUtil.strTrim(edcAtnlcMngrVO.getSearchAll());

    
    logger.debug("\n\t>> insttList BGN");
    CodeCmmVO cmmVo = new CodeCmmVO();

    /** 기관 목록 조회 **/
    cmmVo.setSiteId(siteId);
    cmmVo.setSearchDeleteAt("N");
    cmmVo.setSearchUseAt("Y");
    cmmVo.setSearchInsttGubun("EC");
    List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, cmmVo);
    logger.debug("\n\t>> insttList END");

    edcAtnlcMngrVO
        .setSearchStartDate(StringUtil.strReplaceALL(edcAtnlcMngrVO.getSearchStartDate(), "-", ""));
    edcAtnlcMngrVO
        .setSearchEndDate(StringUtil.strReplaceALL(edcAtnlcMngrVO.getSearchEndDate(), "-", ""));

    totCnt = edcAtnlcMngrService.selectEdcAtnlcMngrTotCnt(edcAtnlcMngrVO);
    logger.debug("\n\t>> totCnt : {} <<\n", totCnt);
    
    neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    edcAtnlcMngrList = edcAtnlcMngrService.selectEdcAtnlcMngrList(edcAtnlcMngrVO);
    logger.debug("\n\t>> edcAtnlcMngrList : {} <<\n", edcAtnlcMngrList.size());

    model.addAttribute("paginationInfo", neoPaginationInfo);


    // if(!"".equals(searchAll)){
    // if(searchAll.length() > 1){
    // totCnt = edcAtnlcMngrService.selectEdcAtnlcMngrTotCnt(edcAtnlcMngrVO);
    // neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    // edcAtnlcMngrList = edcAtnlcMngrService.selectEdcAtnlcMngrList(edcAtnlcMngrVO);
    //
    // model.addAttribute("paginationInfo", neoPaginationInfo);
    // }else{
    // edcAtnlcMngrList = new ArrayList<EdcAtnlcMngr>();
    // neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    // }
    // }else{
    // edcAtnlcMngrList = new ArrayList<EdcAtnlcMngr>();
    // neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    // }


    model.addAttribute("insttList", insttList);
    model.addAttribute("totCnt", totCnt);
    model.addAttribute("edcAtnlcMngrList", edcAtnlcMngrList);
    model.addAttribute("paginationInfo", neoPaginationInfo);

    return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcList";
  }
  	
  	
  /**
   * 엑셀다운로드
   * 
   * @param key
   * @param edcAtnlcMngrVO
   * @param edcAtnlcLctreVO
   * @param request
   * @param model
   * @return
   * @throws Exception
   */
  @Interceptor(value = {"progrmAuthorBinding"}, type = AccesType.REJECT, code = "EXCEL", progrmId = "A01")
  @RequestMapping("/{siteId}/edcAtnlcMngListExcel.do")
  public String edcAtnlcMngListExcel(
    @RequestParam("key") int key,
    @ModelAttribute("atnlcList") EdcAtnlcMngrVO edcAtnlcMngrVO,
    @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO,
    @PathVariable("siteId") String siteId, HttpServletRequest request,
    HttpServletResponse response,
    ModelMap model
  ) throws Exception {

    String userId = StaffLoginUtil.getLoginId(request.getSession());
    if (!StaffLoginUtil.isAdmin(request.getSession())) {
      if ("".equals(StringUtil.strTrim(userId))) {
        edcAtnlcLctreVO.setAuthUserId("0000000000000000000000000000000");
        edcAtnlcMngrVO.setAuthUserId("0000000000000000000000000000000");
      } else {
        edcAtnlcLctreVO.setAuthUserId(userId);
        edcAtnlcMngrVO.setAuthUserId(userId);
      }
    }

    int totCnt = 0;
    NeoPaginationInfo neoPaginationInfo;
    List<EdcAtnlcMngr> edcAtnlcMngrList;

    //String searchAll = StringUtil.strTrim(edcAtnlcMngrVO.getSearchAll());

    CodeCmmVO cmmVo = new CodeCmmVO();

    /** 기관 목록 조회 **/
    cmmVo.setSiteId(siteId);
    cmmVo.setSearchDeleteAt("N");
    cmmVo.setSearchUseAt("Y");
    cmmVo.setSearchInsttGubun("EC");
    List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, cmmVo);

    edcAtnlcMngrVO
        .setSearchStartDate(StringUtil.strReplaceALL(edcAtnlcMngrVO.getSearchStartDate(), "-", ""));
    edcAtnlcMngrVO
        .setSearchEndDate(StringUtil.strReplaceALL(edcAtnlcMngrVO.getSearchEndDate(), "-", ""));
    edcAtnlcMngrVO.setPageUnit(999999999);
    totCnt = edcAtnlcMngrService.selectEdcAtnlcMngrTotCnt(edcAtnlcMngrVO);
    neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    edcAtnlcMngrList = edcAtnlcMngrService.selectEdcAtnlcMngrList(edcAtnlcMngrVO);

    model.addAttribute("paginationInfo", neoPaginationInfo);


    // if(!"".equals(searchAll)){
    // if(searchAll.length() > 1){
    // totCnt = edcAtnlcMngrService.selectEdcAtnlcMngrTotCnt(edcAtnlcMngrVO);
    // neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    // edcAtnlcMngrList = edcAtnlcMngrService.selectEdcAtnlcMngrList(edcAtnlcMngrVO);
    //
    // model.addAttribute("paginationInfo", neoPaginationInfo);
    // }else{
    // edcAtnlcMngrList = new ArrayList<EdcAtnlcMngr>();
    // neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    // }
    // }else{
    // edcAtnlcMngrList = new ArrayList<EdcAtnlcMngr>();
    // neoPaginationInfo = edcAtnlcMngrVO.getNeoPaginationInfo(totCnt);
    // }

    model.addAttribute("insttList", insttList);
    model.addAttribute("totCnt", totCnt);
    model.addAttribute("edcAtnlcMngrList", edcAtnlcMngrList);
    model.addAttribute("paginationInfo", neoPaginationInfo);

    String filename = URLEncoder.encode("수강생목록.xls", "UTF-8");

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
    response.setHeader("Pragma", "public");
    response.setHeader("Content-charset", "UTF-8");
    response.setHeader("Content-Disposition", "attachment;filename=" + filename);

    return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcListExcel";
  }

  	


  /**
   * 수강관리 - 예약 사용자 목록 조회
   * 
   * @param edcAtnlcMngrVO
   * @param model
   * @return
   * @throws Exception
   */
  @Interceptor(value = {"templateBinding", "progrmAuthorBinding"}, type = AccesType.REJECT, code = "LIST", progrmId = "A01")
  @RequestMapping("/{siteId}/edcAtnlcMngrMngUserList.do")
  public String edcAtnlcMngrMngUserList(
    @RequestParam("key") int key,
    @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO,
    @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO,
    HttpServletRequest request,
    ModelMap model
  ) throws Exception {
    
    try {
      // 강좌 정보 조회
      EdcLctreVO edcLctreVO = new EdcLctreVO();
      edcLctreVO.setSearchLctreKey(edcAtnlcLctreVO.getSearchLctreKey());
      
      EdcLctre edcLctre = edcLctreService.selectEdcLctreDetail(request, edcLctreVO);
      model.addAttribute("edcLctre", edcLctre);
      logger.debug("\n\t>> edcLctre : {} <<\n\n", ToStringBuilder.reflectionToString(edcLctre, ToStringStyle.MULTI_LINE_STYLE));

      // 수강 신청 목록조회
      edcAtnlcMngrVO.setSearchSort("DESC");
      // edcAtnlcMngrVO.setSearchUserNm(StringUtil.strTrim(edcAtnlcMngrVO.getSearchUserNm()));
      List<EdcAtnlcMngrVO> edcAtnlcMngrList =
          edcAtnlcMngrService.selectEdcAtnlcUserList(edcAtnlcMngrVO);
      logger.debug("\n\t>> edcAtnlcMngrList : {} <<\n\n", ToStringBuilder.reflectionToString(edcAtnlcMngrList, ToStringStyle.MULTI_LINE_STYLE));
      logger.debug("\n\t>> edcAtnlcMngrList : {} <<\n\n", edcAtnlcMngrList);
      model.addAttribute("edcAtnlcMngrList", edcAtnlcMngrList);
    } catch (Exception e) {
      StringUtil.printException( e );
    }

    return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrUserList";
  }

  /**
   * 엑셀다운로드
   * 
   * @param key
   * @param edcAtnlcMngrVO
   * @param edcAtnlcLctreVO
   * @param request
   * @param model
   * @return
   * @throws Exception
   */
  @Interceptor(value = {"progrmAuthorBinding"}, type = AccesType.REJECT, code = "EXCEL", progrmId = "A01")
  @RequestMapping("/{siteId}/edcAtnlcMngrMngUserListExcel.do")
  public String edcAtnlcMngrMngUserListExcel(
    @RequestParam("key") int key,
    @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO,
    @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO,
    HttpServletRequest request,
    HttpServletResponse response,
    ModelMap model
  ) throws Exception {

    // 강좌 정보 조회
    EdcLctreVO edcLctreVO = new EdcLctreVO();
    edcLctreVO.setSearchLctreKey(edcAtnlcLctreVO.getSearchLctreKey());
    
    EdcLctre edcLctre = edcLctreService.selectEdcLctreDetail(request, edcLctreVO);
    model.addAttribute("edcLctre", edcLctre);
    logger.debug("\n\t>> edcLctre : {} <<\n\n", ToStringBuilder.reflectionToString(edcLctre, ToStringStyle.MULTI_LINE_STYLE));

    // 수강 신청 목록조회
    edcAtnlcMngrVO.setSearchSort("ASC");
    List<EdcAtnlcMngrVO> edcAtnlcMngrList =
        edcAtnlcMngrService.selectEdcAtnlcUserList(edcAtnlcMngrVO);
    model.addAttribute("edcAtnlcMngrList", edcAtnlcMngrList);
    logger.debug("\n\t>> edcAtnlcMngrList : {} <<\n\n", ToStringBuilder.reflectionToString(edcAtnlcMngrList, ToStringStyle.MULTI_LINE_STYLE));
    logger.debug("\n\t>> edcAtnlcMngrList : {} <<\n\n", edcAtnlcMngrList);

    String filename =
        URLEncoder.encode(edcLctre.getLctreNm().replaceAll(" ", "_") + "_수강생목록.xls", "UTF-8");

    response.setContentType("application/vnd.ms-excel");
    response.setHeader("Expires", "0");
    response.setHeader("Cache-Control", "must-revalidate, post-check=0,pre-check=0");
    response.setHeader("Pragma", "public");
    response.setHeader("Content-charset", "UTF-8");
    response.setHeader("Content-Disposition", "attachment;filename=" + filename);

    return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrUserListExcel";
  }

  	/**
  	 * 추첨제 강좌 일때 홈페이지 적용
  	 * @param edcAtnlcMngrVO
  	 * @param edcAtnlcLctreVO
  	 * @param request
  	 * @param model
  	 * @return
  	 * @throws Exception
  	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/updateAtnlcMngConfmHmpgReflctAt.do")
	public String updateAtnlcMngConfmHmpgReflctAt(
					@RequestParam("key") int key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		model.addAttribute("EdcAtnlcMngrVO", edcAtnlcMngrVO);
  		model.addAttribute("EdcAtnlcLctreVO", edcAtnlcLctreVO);

  		// 수강 신청 목록조회
  		edcAtnlcMngrVO.setConfmHmpgReflctAt("Y");
  		edcAtnlcMngrVO.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));
  		edcAtnlcMngrVO.setLoginUserNm(StaffLoginUtil.getLoginNm(request.getSession()));

  		int rtn  = edcAtnlcMngrService.updateAtnlcMngConfmHmpgReflctAt(request, edcAtnlcMngrVO);
  		String msg = "";

  		if(rtn > 0){
  			// 문자 메시지 발송


  			msg = "반영되었습니다.";
  		}else{
  			msg = "반영되지 않았습니다.";
  		}

  		return cmmUseService.redirectMsg(model, msg,
				"./edcAtnlcMngrMngUserList.do?key="+key
							+"&pageUnit=" + edcAtnlcMngrVO.getPageUnit()
							+ "&searchCnd=" + edcAtnlcMngrVO.getSearchCnd()
							+ "&searchLctreKey=" + edcAtnlcMngrVO.getLctreKey());
	}


	/**
	 * 수강관리 - 조회(관리자)
	 * @param edcAtnlcMngrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="VIEW", progrmId="A01")
	@RequestMapping("/{siteId}/edcAtnlcMngrMngView.do")
	public String edcAtnlcMngrView(
				@RequestParam("key") int key
				, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
				, HttpServletRequest request
				, ModelMap model) throws Exception {

		EdcAtnlcMngr edcAtnlcMngrData = edcAtnlcMngrService.selectEdcAtnlcMngrData(request, edcAtnlcMngrVO);
		model.addAttribute("edcAtnlcMngr", edcAtnlcMngrData);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrView";
	}

	/**
	 * 수강관리 - 등록(관리자)
	 * @param edcAtnlcMngrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="ADD", progrmId="A01")
	@RequestMapping("/{siteId}/addEdcAtnlcMngrMngView.do")
	public String addedcAtnlcMngrView(
								@RequestParam("key") int key
								, @PathVariable("siteId") String siteId
								, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
								, HttpServletRequest request
								, ModelMap model
								) throws Exception {

  		String lctreKey = StringUtil.intToStr(edcAtnlcMngrVO.getLctreKey());

  		// 강좌 정보 조회
  		EdcLctreVO edcLctreVO = new EdcLctreVO();
  		edcLctreVO.setSearchLctreKey(lctreKey);
  		EdcLctreVO edcLctre = edcLctreService.selectEdcLctreDetail(request, edcLctreVO);
  		model.addAttribute("edcLctre", edcLctre);

  		// 모집 기간 체크
  		if(!edcAtnlcMngrService.selectCheckEdcAtnlcDate(edcLctre, siteId)){
  			return cmmUseService.redirectMsg(model, "현재 수강 신청 기간이 아닙니다.",
  					"./edcAtnlcMngrMngUserList.do?key="+key
  								+"&pageUnit=" + edcAtnlcMngrVO.getPageUnit()
  								+ "&searchCnd=" + edcAtnlcMngrVO.getSearchCnd()
  								+ "&searchKrwd=" + edcAtnlcMngrVO.getSearchKrwd()
  								+ "&searchLctreKey=" + lctreKey
  								+ "&pageIndex=" + edcAtnlcMngrVO.getPageIndex());
  		}

  		// 정원 정보
  		int rcritNmpr	= 	edcLctre.getRcritNmpr() + edcLctre.getWaitNmpr();
  		// 신청자 카운트 (취소 제외)
  		int rcritCnt 		= edcLctre.getSttus00() + edcLctre.getSttus01() + edcLctre.getSttus02() + edcLctre.getSttus09() + edcLctre.getSttus10();

  		// 정원 체크
  		if(rcritNmpr <= rcritCnt){
  			//return cmmUseService.redirectMsg(model, "모집정원이 마감되었습니다.",
  			//		"./edcAtnlcMngrMngList.do?key="+key
  			//					+"&pageUnit=" + edcAtnlcMngrVO.getPageUnit()
  			//					+ "&searchCnd=" + edcAtnlcMngrVO.getSearchCnd()
  			//					+ "&searchKrwd=" + edcAtnlcMngrVO.getSearchKrwd()
  			//					+ "&pageIndex=" + edcAtnlcMngrVO.getPageIndex());
  		}

  		// 할인대상 및 할인률
  		ResveOptionVO resveOptionVO = new ResveOptionVO();
  		resveOptionVO.setSearchMngrKey(lctreKey);
  		resveOptionVO.setSearchJobSe("L");
  		resveOptionVO.setSearchTrgetCode("D");
  		List<ResveOption> optionList =   resveOptionService.selectResveOptionListAll(resveOptionVO);

  		model.addAttribute("optionList", optionList);

  		// 프로그램 신청양식 추가 조회
  		ResveAddFieldVO resveAddFieldVO = new ResveAddFieldVO();
  		resveAddFieldVO.setSearchManagerSeq(lctreKey);
  		resveAddFieldVO.setSearchDeleteAt("N");
  		resveAddFieldVO.setSearchJobSe("EC");
  		List<ResveAddField> resveAddFieldList =   resveAddFieldService.selectResveAddFieldListAll(resveAddFieldVO);

  		HashMap<String, String> addFieldMap = resveAddFieldService.makeAddFieldHtml(resveAddFieldList);
  		model.addAttribute("resveAddFieldList", resveAddFieldList);
  		model.addAttribute("addFieldMap", addFieldMap);

  		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 학과분류, 강좌분류(대분류), 6진분류(1차), 강좌접수 형태, 접수방법, 접수상태, 사용유무, 교육 방법
		vo.setSearchCodeType("'APPLCNT_RELATE'");
		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);

		model.addAttribute("listCodeMap", listCodeMap);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrRegist";
	}

  	/**
  	 * 수동등록(전화, 방문예약)시 체크 로직
  	 * @param key
  	 * @param siteId
  	 * @param edcLctreVO
  	 * @param edcAtnlcMngrVO
  	 * @param edcAtnlcUserVO
  	 * @param edcAtnlcUserBndl
  	 * @param cmmnPaylogVO
  	 * @param bindingResult
  	 * @param request
  	 * @param response
  	 * @param model
  	 * @throws Exception
  	 */
  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="ADD", progrmId="A01")
  	@RequestMapping("/{siteId}/addEdcAtnlcMngrCheck.do")
  	public void addEdcAtnlcMngrCheck(@RequestParam("key") int key
					, @PathVariable("siteId") String siteId
					, @ModelAttribute("edcLctreVO") 			EdcLctreVO edcLctreVO
					, @ModelAttribute("edcAtnlcMngrVO") 	EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcUserVO") 	EdcAtnlcUserVO edcAtnlcUserVO
					, @ModelAttribute("edcAtnlcUserBndl") 	EdcAtnlcUserBndl edcAtnlcUserBndl
					, @ModelAttribute("cmmnPaylogVO") 		CmmnPaylogVO cmmnPaylogVO
					, BindingResult bindingResult
					, HttpServletRequest request
					, HttpServletResponse response
					, ModelMap model) throws Exception {

  		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		// 수강신청 가능여부 체크
    	String userId = edcAtnlcMngrVO.getUserId();
    	String diCode = edcAtnlcMngrVO.getDiCode();

		try {
			response.setCharacterEncoding("utf-8");
	    	out = response.getWriter();

	    	if(!"".equals(StringUtil.strTrim(userId))){
	    		edcAtnlcMngrVO.setMberSe("0");
	    	}else{
	    		edcAtnlcMngrVO.setMberSe("1");
	       		userId = edcAtnlcUserBndl.getBndlTel02()[0]+edcAtnlcUserBndl.getBndlUserBrthdy()[0];
	    		userId = StringUtil.strReplaceALL(userId, "-", "");
	    		userId = StringUtil.strReplaceALL(userId, "\\.", "");
	    		edcAtnlcMngrVO.setDiCode(userId);
	    	}

	    	edcAtnlcMngrVO.setSearchLctreKey(StringUtil.intToStr(edcLctreVO.getLctreKey()));
	    	EdcLctreVO rtnEdcLctreVo = edcAtnlcMngrService.checkLctre(request, edcLctreVO, edcAtnlcMngrVO, edcAtnlcUserBndl, userId, siteId);

	    	obj.put("resultMsg", rtnEdcLctreVo.getReturnMsg());
		    obj.put("result", rtnEdcLctreVo.getReturnCode());

		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("resultMsg", "등록중 오류가 발생하였습니다.");

			e.printStackTrace();
		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}

  	}


  	
  	
  	/**
     * 수강관리 - 등록처리(관리자)
     * 
     * @param multiPartRequest
     * @param edcAtnlcMngrVO
     * @param request
     * @param bindingResult
     * @param model
     * @return
     * @throws Exception
     */
    @Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "ADD", progrmId = "A01")
    @RequestMapping("/{siteId}/addEdcAtnlcMngrMng.do")
    public void addEdcAtnlcMngr(
      @RequestParam("key") int key
      , @PathVariable("siteId") String siteId
      , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
      , @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
      , @ModelAttribute("edcAtnlcUserVO") EdcAtnlcUserVO edcAtnlcUserVO
      , @ModelAttribute("edcAtnlcUserBndl") EdcAtnlcUserBndl edcAtnlcUserBndl
      , @ModelAttribute("cmmnPaylogVO") CmmnPaylogVO cmmnPaylogVO
      , BindingResult bindingResult
      , HttpServletRequest request
      , HttpServletResponse response
      , ModelMap model
    ) throws Exception {

      JSONObject obj = new JSONObject();
      PrintWriter out = null;

      try {
        response.setCharacterEncoding("utf-8");
        out = response.getWriter();

        // 수강신청 가능여부 체크
        String userId = edcAtnlcMngrVO.getUserId();
        String diCode = edcAtnlcMngrVO.getDiCode();

        if (!"".equals(StringUtil.strTrim(userId))) {
          edcAtnlcMngrVO.setMberSe("0");
        } else {
          edcAtnlcMngrVO.setMberSe("1");

          userId = edcAtnlcUserBndl.getBndlTel02()[0] + edcAtnlcUserBndl.getBndlUserBrthdy()[0]
              + edcAtnlcUserBndl.getBndlUserNm()[0];
          userId = StringUtil.strReplaceALL(userId, "-", "");
          userId = StringUtil.strReplaceALL(userId, "\\.", "");
          edcAtnlcMngrVO.setDiCode(userId);
        }

        edcAtnlcMngrVO.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));
        edcAtnlcMngrVO.setLoginUserNm(StaffLoginUtil.getLoginNm(request.getSession()));
        edcLctreVO.setFrstRegisterId(StaffLoginUtil.getLoginId(request.getSession()));

        edcLctreVO.setSiteId("staff");
        EdcLctreVO rtnEdcVO = edcAtnlcMngrService.resveProcess(request, edcAtnlcMngrVO,
            edcAtnlcUserVO, edcAtnlcUserBndl, edcLctreVO, userId, siteId);

        if (cmmnPaylogVO.getRcpmnySe() != null) {
          if (!"".equals(StringUtil.strTrim(cmmnPaylogVO.getTotprice()))) {
            String nowDate = DateUtil.getNowDateTime();
            cmmnPaylogVO.setAppCd(rtnEdcVO.getAtnclKey());
            cmmnPaylogVO.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));
            cmmnPaylogVO.setLoginUserNm(StaffLoginUtil.getLoginNm(request.getSession()));
            cmmnPaylogVO.setAppidate(StringUtil.strSubString(nowDate, 0, 8));
            cmmnPaylogVO.setAppitime(StringUtil.strSubString(nowDate, 8, 14));
            cmmnPaylogVO.setJobSe("EC");
            cmmnPaylogVO.setSiteId("staff");
            cmmnPaylogVO.setAutoSe("Y");

            cmmnPaylogService.insertCmmnPaylog(request, cmmnPaylogVO);
          }
        }

        obj.put("atnclKey", rtnEdcVO.getAtnclKey());
        obj.put("resultMsg", rtnEdcVO.getReturnMsg());
        obj.put("result", rtnEdcVO.getReturnCode());

      } catch (Exception e) {
        obj.put("result", "F");
        obj.put("resultMsg", "등록중 오류가 발생하였습니다.");

        e.printStackTrace();
      } finally {
        out.write(obj.toString());
        out.flush();
        out.close();
      }
    }
    
    
    
    
    

	/**
	 * 수강관리 - 수정(관리자)
	 * @param edcAtnlcMngrVO
	 * @param model
	 * @return "NeoCMS/cop/edcAtnlcMngr/edcAtnlcMngrView"
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="UPDATE", progrmId="A01")
	@RequestMapping("/{siteId}/updateEdcAtnlcMngrMngView.do")
	public String updateEdcAtnlcMngrView(
					@RequestParam("key") int key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

		EdcAtnlcMngr edcAtnlcMngrData = edcAtnlcMngrService.selectEdcAtnlcMngrData(request, edcAtnlcMngrVO);
		model.addAttribute("edcAtnlcMngr", edcAtnlcMngrData);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrUpdt";
	}


	/**
	 * 수강관리 - 수정처리(관리자)
	 * @param edcAtnlcMngrVO
	 * @param request
	 * @param model
	 * @return "NeoCMS/cop/edcAtnlcMngr/edcAtnlcMngrMngUpdt"
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE", progrmId="A01")
	@ResponseBody
	@RequestMapping("/{siteId}/updateEdcAtnlcMngrMng.do")
	public void updateEdcAtnlcMngrMng(
			  @RequestParam(value="key") String key
			, @PathVariable(value="siteId") String siteId
			, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
			, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
			, @ModelAttribute("edcAtnlcUserVO") EdcAtnlcUserVO edcAtnlcUserVO
			, @ModelAttribute("edcAtnlcUserBndl") EdcAtnlcUserBndl edcAtnlcUserBndl
			, final MultipartHttpServletRequest multiPartRequest
			, HttpServletRequest request
			, HttpServletResponse response
			, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			String userId = edcAtnlcMngrVO.getUserId();
			edcLctreVO.setSearchNotAtnclKey(edcAtnlcMngrVO.getAtnclKey()+"");		// 체크시에 본인강좌 제외 후 비교
			EdcLctreVO rtnEdcLctreVO = edcAtnlcMngrService.updateCheckBooking(request, edcLctreVO, edcAtnlcMngrVO, edcAtnlcUserVO, edcAtnlcUserBndl, userId);

			//if(rtnEdcLctreVO.getCheckBooking()){
				edcAtnlcMngrVO.setSiteId(siteId);
				edcAtnlcMngrVO.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));
				edcAtnlcMngrVO.setLoginUserNm(StaffLoginUtil.getLoginNm(request.getSession()));
				edcAtnlcMngrVO.setFrstRegisterId(StaffLoginUtil.getLoginId(request.getSession()));

				edcAtnlcMngrService.updateEdcUser(request, edcAtnlcMngrVO, edcAtnlcUserBndl, rtnEdcLctreVO);

				obj.put("result", "S");
				obj.put("msg", "저장 되었습니다.");
			//}else{
			//	obj.put("result", "S");
			//	obj.put("msg", rtnEdcLctreVO.getReturnMsg());
			//}

		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("msg", "저장 실패");
			e.printStackTrace();
		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}

	}

	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE", progrmId="A01")
	@ResponseBody
	@RequestMapping("/{siteId}/updateEdcAtnlcMngrOptn.do")
	public ResponseJSON updateEdcAtnlcMngrOptn(
				@RequestParam("key") int key
				, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
				, BindingResult bindingResult
				, HttpServletRequest request,  ModelMap model) throws Exception {

		ResponseJSON rs = new ResponseJSON();

		beanValidator.validate(edcAtnlcMngrVO, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("edcAtnlcMngrVO", edcAtnlcMngrVO);
//			return "NeoCMS/cop/edcAtnlcMngr/edcAtnlcMngrUpdt";
		}

		try {

			edcAtnlcMngrService.updateEdcAtnlcMngrOptn(request, edcAtnlcMngrVO);

		} catch(Exception e) {
			model.addAttribute("edcAtnlcMngr", edcAtnlcMngrVO);
			model.addAttribute("isValidate", true);
			model.addAttribute("message", e.getMessage());
			e.printStackTrace();
			rs.setMsg(e.getMessage());
			rs.setResult(0);
			return rs;
		}

		rs.setResult(1);
		return rs;

	}

	/**
	 * 수강관리 - 삭제처리(관리자)
	 * @param edcAtnlcMngrVO
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="DELETE", progrmId="A01")
	@RequestMapping("/{siteId}/edcAtnlcMngrMngDel.do")
	public String edcAtnlcMngrDel(@RequestParam(value="key") String key,@ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO,
			HttpServletRequest request, ModelMap model) throws Exception {

		try {
			edcAtnlcMngrService.deleteEdcAtnlcMngr(edcAtnlcMngrVO);

		} catch(Exception e) {

			model.addAttribute("edcAtnlcMngr", edcAtnlcMngrVO);
			model.addAttribute("isValidate", true);
			model.addAttribute("message", e.getMessage());
			e.printStackTrace();

			return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrList";
		}

		return cmmUseService.redirectMsg(model, "삭제를 완료하였습니다.", "./edcAtnlcMngrList.do?pageUnit=" + edcAtnlcMngrVO.getPageUnit() + "&searchCnd=" + edcAtnlcMngrVO.getSearchCnd() + "&searchKrwd=" + edcAtnlcMngrVO.getSearchKrwd() + "&pageIndex=" + edcAtnlcMngrVO.getPageIndex());
	}


	/**
	 * 사용자 조회 후 결과값 리턴
	 * @param edcAtnlcMngrVO
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param model
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="LIST", progrmId = "A01")
	@RequestMapping("/{siteId}/selectUserEdcLctreActplnMng.do")
	public void selectUserEdcLctreActplnMng(
											@RequestParam("key") int key
											, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
											,HttpServletRequest request
											, HttpServletResponse response
											, BindingResult bindingResult
											, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;
		UserInfo userInfo = new UserInfo();

		try {
			userInfo = userInfoService.selectUserInfo(edcAtnlcMngrVO.getUserId());

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			if(userInfo == null){
				obj.put("result", "F");
				obj.put("msg", "저장하지 못했습니다.");

			}else{
				//userInfo.setBirth(DateUtil.getDateFormat(userInfo.getBirth(), "yyyy-mm-dd"));

				obj.put("result", "S");
				obj.put("userInfo", userInfo);
				obj.put("msg", "저장 성공");
			}

		} catch(Exception e) {
			logger.error("EdcAtnlcMngrController.selectUserEdcLctreActplnMng", e);

		} finally {
			model.addAttribute("userInfo", userInfo);

			out.write(obj.toString());
			out.flush();
			out.close();
		}

	}

	/**
	 * 수강관리 > 접수 관리 > 수강생 목록 조회 > 수강 상세 정보 조회 (단건)
	 * @param edcAtnlcMngrVO
	 * @param edcAtnlcLctreVO
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/edcAtnlcMngrMngUserView.do")
	public String edcAtnlcMngrMngUserView(
					@RequestParam("key") int key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcUserVO") EdcAtnlcUserVO edcAtnlcUserVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		// 강좌 정보 조회
  		EdcLctreVO edcLctreVO = new EdcLctreVO();
  		edcLctreVO.setSearchLctreKey(edcAtnlcMngrVO.getSearchLctreKey());
  		EdcLctre edcLctre = edcLctreService.selectEdcLctreDetail(request, edcLctreVO);
  		model.addAttribute("edcLctre", edcLctre);

  		// 신청 정보 및 상태값 조회
  		EdcAtnlcMngr edcAtnlcMngr =  edcAtnlcMngrService.selectEdcAtnlcMngrData(request, edcAtnlcMngrVO);

  		model.addAttribute("edcAtnlcMngr", edcAtnlcMngr);

		// 수강생 정보 조회
		edcAtnlcUserVO.setLastIndex(20);
		edcAtnlcUserVO.setFirstIndex(0);
		edcAtnlcUserVO.setAtnclKey(edcAtnlcMngr.getAtnclKey());

		List<EdcAtnlcUser> userList = edcAtnlcUserService.selectEdcAtnlcUserList(edcAtnlcUserVO);
		model.addAttribute("userList", userList);

		// 추가입력 필드 조회
		ResveAddFieldVO resveAddFieldVO = new ResveAddFieldVO();
		resveAddFieldVO.setSearchManagerSeq(edcAtnlcMngrVO.getSearchLctreKey());
		resveAddFieldVO.setSearchJobSe("EC");
		resveAddFieldVO.setSearchMngrSeq(edcAtnlcMngrVO.getSearchAtnclKey());
		resveAddFieldVO.setSearchDeleteAt("N");
		List<ResveAddField> resveAddFieldList =   resveAddFieldService.selectResveInputAddFieldData(resveAddFieldVO);

		HashMap<String, String> addFieldMap = resveAddFieldService.makeAddFieldHtml(resveAddFieldList);
  		model.addAttribute("resveAddFieldList", resveAddFieldList);
  		model.addAttribute("addFieldMap", addFieldMap);

		// 할인대상 및 할인률
		ResveOptionVO resveOptionVO = new ResveOptionVO();
		resveOptionVO.setSearchMngrKey(edcAtnlcMngrVO.getSearchLctreKey());
		resveOptionVO.setSearchJobSe("L");
		resveOptionVO.setSearchTrgetCode("D");
		List<ResveOption> optionList =   resveOptionService.selectResveOptionListAll(resveOptionVO);
		model.addAttribute("optionList", optionList);

		List<CmmnPaylog> cmmnPaylogList =  new ArrayList<CmmnPaylog>();
		if("Y".equals(edcLctre.getFreeYn())){
			// 결제 내역 조회 - 유료 일때
			CmmnPaylogVO cmmnPaylogVO = new CmmnPaylogVO();
			cmmnPaylogVO.setSearchAppCd(edcAtnlcMngr.getAtnclKey()+"");
			cmmnPaylogList =  cmmnPaylogService.selectCmmnPaylogListAll(cmmnPaylogVO);
		}
		model.addAttribute("cmmnPaylogList", cmmnPaylogList);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrMngUserView";
	}

	/**
	 * 수강관리 - 사업(기수)단위 추첨 화면 이동
	 * @param edcAtnlcMngrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="UPDATE", progrmId="A01")
	@RequestMapping("/{siteId}/edcSemstrDrwt.do")
	public String edcSemstrDrwt(
					@RequestParam("key") int key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 학과분류, 강좌분류(대분류), 6진분류(1차), 강좌접수 형태, 접수방법, 접수상태, 사용유무, 교육 방법
		vo.setSearchCodeType(
				"'LES_DIV', 'TCH_LEC_DIV1', 'LEC_DIV_LVL1', 'LEC_REG_CLASS', 'LEC_APP_TYPE', 'RCEPT_STTUS', 'USE_AT', 'LEC_TYPE' ");

		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);

		model.addAttribute("listCodeMap", listCodeMap); 					// 코드값 목록 Map


		return "NeoEdu/cop/edcAtnlcMngr/edcSemstrDrwt";
	}


  	/**
	 * 수강관리 - 사업(기수)단위 추첨 화면 이동
	 * @param edcAtnlcMngrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="UPDATE", progrmId="A01")
	@RequestMapping("/{siteId}/edcSemstrChoiseDrwt.do")
	public String edcSemstrChoiseDrwt(
					@RequestParam("key") int key
					, @PathVariable("siteId") String siteId
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		String strLctreKey = "";

  		if(edcAtnlcMngrVO.getLctreKeyArr() != null){
  			for(int i=0;i<edcAtnlcMngrVO.getLctreKeyArr().length;i++){
  				strLctreKey += edcAtnlcMngrVO.getLctreKeyArr()[i] +  ",";
  			}

  			if(strLctreKey.length() > 0){
  				strLctreKey = strLctreKey.substring(0, strLctreKey.length()-1);
  				edcAtnlcMngrVO.setLctreKeyStr(strLctreKey);

  				edcAtnlcMngrVO.setSearchLctreKeyIn(strLctreKey);
  			}
  		}else{
  			strLctreKey = edcAtnlcMngrVO.getSearchLctreKeyIn();
  		}

		List<CodeCmmVO> insttList = new ArrayList<CodeCmmVO>();

		edcAtnlcLctreVO.setSearchLctreKeyIn(strLctreKey);
		edcAtnlcLctreVO.setSearchPriorRceptMth("1");
		edcAtnlcLctreVO.setFirstIndex(0);
		edcAtnlcLctreVO.setLastIndex(300);

		List<EdcAtnlcLctreVO> edcAtnlcLctreList = edcAtnlcMngrService.selectEdcLctreAtnlcList(request, edcAtnlcLctreVO, insttList);

		if(edcAtnlcLctreList.size() <= 0){
			// 추첨 대상 강좌가 없을때
			return cmmUseService.redirectMsg(model, "추첨 대상 강좌가 없습니다.", "./edcAtnlcMngrMngList.do?key="+key);
		}

		model.addAttribute("edcAtnlcLctreList", edcAtnlcLctreList); 		// 추첨대상 목록 리스트
		return "NeoEdu/cop/edcAtnlcMngr/edcSemstrChoiseDrwt";
	}

  	/**
  	 * 수강 신청 상태값 변경 처리
  	 * @param edcAtnlcMngrVO
  	 * @param request
  	 * @param response
  	 * @param bindingResult
  	 * @param model
  	 * @throws Exception
  	 */
  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE", progrmId = "A01")
	@RequestMapping("/{siteId}/updateEdcAtnlcMngrSttus.do")
	public void updateEdcAtnlcMngrSttus(
											@RequestParam("key") int key
											, @PathVariable("siteId") String siteId
											, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
											, HttpServletRequest request
											, HttpServletResponse response
											, BindingResult bindingResult
											, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			edcAtnlcMngrVO.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));
			edcAtnlcMngrVO.setLoginUserNm(StaffLoginUtil.getLoginNm(request.getSession()));
			edcAtnlcMngrVO.setSiteId(siteId);
			edcAtnlcMngrVO.setSearchAtnclKeyIn(edcAtnlcMngrVO.getAtnclKeyStr());
			edcAtnlcMngrVO.setSearchLctreKey(edcAtnlcMngrVO.getLctreKey() + "");

			edcAtnlcMngrService.updateEdcAtnlcSttusGubun(request, edcAtnlcMngrVO);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "F");
			obj.put("msg", "저장하지 못했습니다.");

		} catch(Exception e) {
			logger.error("EdcAtnlcMngrController.updateEdcAtnlcMngrSttus", e);

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

  	/**
  	 * 수강 신청 상태값 변경 처리
  	 * @param edcAtnlcMngrVO
  	 * @param request
  	 * @param response
  	 * @param bindingResult
  	 * @param model
  	 * @throws Exception
  	 */
  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE", progrmId = "A01")
	@RequestMapping("/{siteId}/checkSttusConfirm.do")
	public void checkSttusConfirm(
											@RequestParam("key") int key
											, @PathVariable("siteId") String siteId
											, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
											, HttpServletRequest request
											, HttpServletResponse response
											, BindingResult bindingResult
											, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			edcAtnlcMngrVO.setSearchLctreKey(edcAtnlcMngrVO.getLctreKey()+"");
			edcAtnlcMngrVO.setSearchStatus("01");
			int rtnInt = edcAtnlcMngrService.selectEdcRcritNmprSttus(edcAtnlcMngrVO);

			if(rtnInt == 1){
				obj.put("result", "F");
				obj.put("msg", "현재 승인인원이 모집정원을 초과합니다. 승인처리 하시겠습니까?");
			}else{
				obj.put("result", "S");
				obj.put("msg", "승인처리");
			}

		} catch(Exception e) {
			logger.error("EdcAtnlcMngrController.updateEdcAtnlcMngrSttus", e);

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

  	/**
  	 * 마이페이지에서 신청정보 확인
  	 * @param edcAtnlcMngrVO
  	 * @param edcAtnlcUserVO
  	 * @param request
  	 * @param model
  	 * @return
  	 * @throws Exception
  	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/edcAtnlcMngrMngUserViewMypage.do")
	public String edcAtnlcMngrMngUserViewMypage(
					@RequestParam("key") int key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcUserVO") EdcAtnlcUserVO edcAtnlcUserVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		// 강좌 정보 조회
  		EdcLctreVO edcLctreVO = new EdcLctreVO();
  		edcLctreVO.setSearchLctreKey(edcAtnlcMngrVO.getSearchLctreKey());
  		EdcLctre edcLctre = edcLctreService.selectEdcLctreDetail(request, edcLctreVO);
  		model.addAttribute("edcLctre", edcLctre);

  		// 신청 정보 및 상태값 조회
  		EdcAtnlcMngr edcAtnlcMngr =  edcAtnlcMngrService.selectEdcAtnlcMngrData(request, edcAtnlcMngrVO);
		model.addAttribute("edcAtnlcMngr", edcAtnlcMngr);

		// 수강생 정보 조회
		edcAtnlcUserVO.setLastIndex(20);
		edcAtnlcUserVO.setFirstIndex(0);
		edcAtnlcUserVO.setAtnclKey(edcAtnlcMngr.getAtnclKey());

		List<EdcAtnlcUser> userList = edcAtnlcUserService.selectEdcAtnlcUserList(edcAtnlcUserVO);
		model.addAttribute("userList", userList);

		// 추가입력 필드 조회
		ResveAddFieldVO resveAddFieldVO = new ResveAddFieldVO();
		resveAddFieldVO.setSearchManagerSeq(edcAtnlcMngrVO.getSearchLctreKey());
		resveAddFieldVO.setSearchJobSe("EC");
		resveAddFieldVO.setSearchMngrSeq(edcAtnlcMngrVO.getSearchAtnclKey());
		resveAddFieldVO.setSearchDeleteAt("N");
		List<ResveAddField> resveAddFieldList =   resveAddFieldService.selectResveInputAddFieldData(resveAddFieldVO);

		String addFieldStr = resveAddFieldService.makeAddFieldHtmlStr(resveAddFieldList);
  		model.addAttribute("resveAddFieldList", resveAddFieldList);
  		model.addAttribute("addFieldStr", addFieldStr);

		// 할인대상 및 할인률
		ResveOptionVO resveOptionVO = new ResveOptionVO();
		resveOptionVO.setSearchMngrKey(edcAtnlcMngrVO.getSearchLctreKey());
		resveOptionVO.setSearchJobSe("L");
		resveOptionVO.setSearchTrgetCode("D");
		List<ResveOption> optionList =   resveOptionService.selectResveOptionListAll(resveOptionVO);
		model.addAttribute("optionList", optionList);

		List<CmmnPaylog> cmmnPaylogList =  new ArrayList<CmmnPaylog>();
		if("Y".equals(edcLctre.getFreeYn())){
			// 결제 내역 조회 - 유료 일때
			CmmnPaylogVO cmmnPaylogVO = new CmmnPaylogVO();
			cmmnPaylogVO.setAppCd(edcAtnlcMngr.getAtnclKey());
			cmmnPaylogVO.setSearchAppCd(edcAtnlcMngr.getAtnclKey()+"");

			cmmnPaylogList =  cmmnPaylogService.selectCmmnPaylogListAll(cmmnPaylogVO);
		}

		model.addAttribute("cmmnPaylogList", cmmnPaylogList);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrMngUserViewMypage";
	}


  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="LIST", progrmId = "A01")
	@RequestMapping("/{siteId}/selectEdcLctreConfirmList.do")
	public void selectEdcLctreConfirmList(
											@RequestParam("key") int key
											, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
											, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
											, HttpServletRequest request
											, HttpServletResponse response
											, BindingResult bindingResult
											, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			/** 기관 목록 조회 **/
			CodeCmmVO vo = new CodeCmmVO();
			vo.setSearchDeleteAt("N");
			vo.setSearchInsttGubun("EC");
			vo.setSearchUseAt("Y");
			List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

			edcAtnlcLctreVO.setSearchRceptMth("1");
			edcAtnlcLctreVO.setFirstIndex(0);
			edcAtnlcLctreVO.setLastIndex(300);

			List<EdcAtnlcLctreVO> edcAtnlcLctreList = edcAtnlcMngrService.selectEdcLctreAtnlcList(request, edcAtnlcLctreVO, insttList);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("list", StringUtil.jsonParserList(edcAtnlcLctreList));

		} catch(Exception e) {
			logger.error("EdcAtnlcMngrController.selectUserEdcLctreActplnMng", e);

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}

	}

  	/**
	 * 수강관리 - 예약 사용자 목록 조회
	 * @param edcAtnlcMngrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = { "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/popupEdcAtnlcMngrMngUserList.do")
	public String popupEdcAtnlcMngrMngUserList(
			 		  @PathVariable("siteId") String siteId
			 		, @RequestParam(value="key") String key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		// 강좌 정보 조회
  		EdcLctreVO edcLctreVO = new EdcLctreVO();
		edcLctreVO.setSearchLctreKey(edcAtnlcLctreVO.getSearchLctreKey());
		EdcLctre edcLctre = edcLctreService.selectEdcLctreDetail(request, edcLctreVO);
  		model.addAttribute("edcLctre", edcLctre);

  		// 수강 신청 목록조회
  		List<EdcAtnlcMngrVO> edcAtnlcMngrList = edcAtnlcMngrService.edcAtnlcMngrMngUserList(request, edcAtnlcMngrVO);
		model.addAttribute("edcAtnlcMngrList", edcAtnlcMngrList);
		model.addAttribute("key", key);
		model.addAttribute("siteId", siteId);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcMngrUserListPopup";
	}

  	/**
  	 * 추첨제 강좌 일때 홈페이지 적용
  	 * @param edcAtnlcMngrVO
  	 * @param edcAtnlcLctreVO
  	 * @param request
  	 * @param model
  	 * @return
  	 * @throws Exception
  	 */
  	@Interceptor(value = { "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/popupUpdateAtnlcMngConfmHmpgReflctAt.do")
	public String popupUpdateAtnlcMngConfmHmpgReflctAt(
			 		  @PathVariable("siteId") String siteId
			 		, @RequestParam(value="key") String key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		model.addAttribute("EdcAtnlcMngrVO", edcAtnlcMngrVO);
  		model.addAttribute("EdcAtnlcLctreVO", edcAtnlcLctreVO);

  		// 수강 신청 목록조회
  		edcAtnlcMngrVO.setConfmHmpgReflctAt("Y");
  		int rtn  = edcAtnlcMngrService.updateAtnlcMngConfmHmpgReflctAt(request, edcAtnlcMngrVO);
  		String msg = "";

  		if(rtn > 0){
  			msg = "반영되었습니다.";
  		}else{
  			msg = "반영되지 않았습니다.";
  		}

  		return cmmUseService.redirectMsg(model, msg,
				"./popupEdcAtnlcMngrMngUserList.do?pageUnit=" + edcAtnlcMngrVO.getPageUnit()
							+ "&searchCnd=" + edcAtnlcMngrVO.getSearchCnd()
							+ "&searchLctreKey=" + edcAtnlcMngrVO.getLctreKey());
	}


  	/**
  	 * 인원 추가 가능 여부 확인
  	 * @param key
  	 * @param edcAtnlcMngrVO
  	 * @param bindingResult
  	 * @param request
  	 * @param response
  	 * @param model
  	 * @throws Exception
  	 */
  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="ADD", progrmId="A01")
  	@RequestMapping("/{siteId}/edcAtnlcAddUserCheck.do")
	public void edcAtnlcAddUserCheck(
			 @RequestParam("key") int key
			, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
			, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
			, @ModelAttribute("edcAtnlcUserVO") EdcAtnlcUserVO edcAtnlcUserVO
			, @ModelAttribute("edcAtnlcUserBndl") EdcAtnlcUserBndl edcAtnlcUserBndl
			, BindingResult bindingResult
			, HttpServletRequest request
			, HttpServletResponse response
			, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf-8");
	    	out = response.getWriter();

	    	System.out.println("edcAtnlcMngrVO.getUserId() : " + edcAtnlcMngrVO.getUserId());

	    	edcAtnlcMngrVO.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));
	    	edcAtnlcMngrVO.setLoginUserNm(StaffLoginUtil.getLoginNm(request.getSession()));

	    	// 예약 추가 가능 여부 판단
	    	EdcLctreVO rtnEdcVO = edcAtnlcMngrService.edcAtnlcAddUserCheck(request, edcAtnlcMngrVO);

	    	if(rtnEdcVO.getCheckBooking()){
	    		obj.put("resultMsg", rtnEdcVO.getReturnMsg());
			    obj.put("result", "S");
	    	}else{
	    		obj.put("resultMsg", rtnEdcVO.getReturnMsg());
			    obj.put("result", rtnEdcVO.getReturnCode());
	    	}

		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("resultMsg", "사용자 추가 확인 중 오류가 발생하였습니다.");

			e.printStackTrace();
		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

  	/**
  	 * 수강증 출력
  	 * @param siteId
  	 * @param edcAtnlcMngrVO
  	 * @param edcAtnlcUserVO
  	 * @param request
  	 * @param model
  	 * @return
  	 * @throws Exception
  	 */
  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="ADD", progrmId="A01")
  	@RequestMapping("/{siteId}/popupEdcLctreCtfhvMng.do")
	public String popupEdcLctreCtfhv(
			  @RequestParam(value="key") String key
			, @PathVariable("siteId") String siteId
			, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
			, @ModelAttribute("edcAtnlcUserVO") EdcAtnlcUserVO edcAtnlcUserVO
			, HttpServletRequest request
			, ModelMap model) throws Exception {

  		// 사용자 아이디 조회
  		String userId = LoginUtil.getLoginId(request.getSession());
  		edcAtnlcMngrVO.setSearchUserId(userId);

   		// 신청 정보 및 상태값 조회
  		EdcAtnlcMngr edcAtnlcMngr =  edcAtnlcMngrService.selectEdcAtnlcMngrData(request, edcAtnlcMngrVO);
  		model.addAttribute("edcAtnlcMngr", edcAtnlcMngr);

  		// 강좌 정보 조회
  		EdcLctreVO rtnEdcLctreVO = new EdcLctreVO();
  		rtnEdcLctreVO.setSearchLctreKey(edcAtnlcMngr.getLctreKey()+"");
  		EdcLctre edcLctre = edcLctreService.selectEdcLctreDetail(request, rtnEdcLctreVO);
  		model.addAttribute("edcLctre", edcLctre);

		// 수강생 정보 조회
		edcAtnlcUserVO.setLastIndex(100);
		edcAtnlcUserVO.setFirstIndex(0);
		edcAtnlcUserVO.setAtnclKey(StringUtil.strToInt(edcAtnlcMngrVO.getSearchAtnclKey()));

		List<EdcAtnlcUser> userList = edcAtnlcUserService.selectEdcAtnlcUserList(edcAtnlcUserVO);

		model.addAttribute("userList", userList);
		model.addAttribute("edcAtnlcMngrVO", edcAtnlcMngr);

		// 기관명 조회
		EdcInsttMngrVO edcInsttMngrVO = new EdcInsttMngrVO();
		edcInsttMngrVO.setSearchInsttCode(edcLctre.getInsttCode()+"");
		EdcInsttMngr edcInsttMngr = edcInsttMngrService.selectEdcInsttNm(edcInsttMngrVO);

		model.addAttribute("edcInsttMngr", edcInsttMngr);

		String nowDate = DateUtil.getNowDateTime();
		String nowPrintTime = nowDate.substring(0, 4)+"년 "+ nowDate.substring(4, 6)+"월 " + nowDate.substring(6, 8)+"일";
		model.addAttribute("nowPrintTime", nowPrintTime);

  		return "NeoCop/cop/reserveMyPage/popupEdcLctreCtfhv";
	}

  	/**
  	 * 강좌 전체 문자 메시지 발송
  	 * @param request
  	 * @param edcAtnlcMngrVO
  	 * @param edcLctreVO
  	 * @return
  	 * @throws Exception
  	 */
  	public int edcHomePageReflection(HttpServletRequest request, EdcAtnlcMngrVO edcAtnlcMngrVO, EdcLctreVO edcLctreVO) throws Exception{
  		int rtnInt = 0;

  		// 수강생 정보 조회


  		return rtnInt;
  	}

  	/**
	 * 수강관리 - 예약 사용자 목록 조회
	 * @param edcAtnlcMngrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/edcDrwtUserList.do")
	public String edcDrwtUserList(
					  @RequestParam("key") int key
					, @ModelAttribute("edcAtnlcMngrVO") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		// 강좌 정보 조회
  		EdcLctreVO edcLctreVO = new EdcLctreVO();
		edcLctreVO.setSearchLctreKey(edcAtnlcLctreVO.getSearchLctreKey());
		EdcLctre edcLctre = edcLctreService.selectEdcLctreDetail(request, edcLctreVO);
  		model.addAttribute("edcLctre", edcLctre);

  		// 수강 신청 목록조회
  		edcAtnlcMngrVO.setSearchSort("DESC");
  		List<EdcAtnlcMngrVO> edcAtnlcMngrList = edcAtnlcMngrService.selectEdcAtnlcUserList(edcAtnlcMngrVO);
		model.addAttribute("edcAtnlcMngrList", edcAtnlcMngrList);

		return "NeoEdu/cop/edcAtnlcMngr/edcDrwtUserList";
	}
  	
  	
  	
  	
  	
  	
  	
  	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type=AccesType.REJECT, code="LIST", progrmId="A01")
	@RequestMapping("/{siteId}/edcAtnlcPayMngList.do")
	public String edcAtnlcPayMngList(
					@RequestParam("key") int key
					, @ModelAttribute("atnlcList") EdcAtnlcMngrVO edcAtnlcMngrVO
					, @ModelAttribute("edcAtnlcLctreVO") EdcAtnlcLctreVO edcAtnlcLctreVO
					, @ModelAttribute("atnlcPayList") CmmnPaylogVO cmmnPaylogVO
					, @PathVariable("siteId") String siteId
					, HttpServletRequest request
					, ModelMap model) throws Exception {

  		String userId = StaffLoginUtil.getLoginId(request.getSession());
		if(!StaffLoginUtil.isAdmin(request.getSession())){
				if("".equals(StringUtil.strTrim(userId))){
					cmmnPaylogVO.setAuthUserId("0000000000000000000000000000000");
					cmmnPaylogVO.setAuthUserId("0000000000000000000000000000000");
				}else{
					cmmnPaylogVO.setAuthUserId(userId);
					cmmnPaylogVO.setAuthUserId(userId);
				}
		}
		CodeCmmVO vo = new CodeCmmVO();
		
		// 학과분류, 강좌분류(대분류), 6진분류(1차), 강좌접수 형태, 접수방법, 접수상태, 사용유무, 교육 방법
		vo.setSearchCodeType("'LES_DIV', 'TCH_LEC_DIV1', 'LEC_DIV_LVL1', 'LEC_REG_CLASS', 'LEC_APP_TYPE', 'RCEPT_STTUS', 'USE_AT', 'LEC_TYPE' ");

		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);

		/** 기관 목록 조회 **/
		vo.setSiteId(siteId);
		vo.setSearchDeleteAt("N");
		vo.setSearchInsttGubun("EC");
		vo.setSearchUseAt("Y");

		List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

		/** 오늘일자 **/
		String nowDate = DateUtil.getNowDateTime("yyyy-MM-dd");

		model.addAttribute("nowDate", nowDate); 					// 오늘 날짜
		model.addAttribute("listCodeMap", listCodeMap); 	// 코드값 목록 Map
		model.addAttribute("insttList", insttList); 						// 기관 목록
		

		int totCnt = 0;
		NeoPaginationInfo neoPaginationInfo;
		List<CmmnPaylogVO> edcAtnlcMngrList;
		
		totCnt = edcAtnlcMngrService.selectEdcAtnlcPayMngrTotCnt(cmmnPaylogVO);
		neoPaginationInfo = cmmnPaylogVO.getNeoPaginationInfo(totCnt);
		edcAtnlcMngrList = edcAtnlcMngrService.selectEdcAtnlcPayMngrList(cmmnPaylogVO);

		model.addAttribute("paginationInfo", neoPaginationInfo);
		

		model.addAttribute("totCnt", totCnt);
		model.addAttribute("edcAtnlcMngrList", edcAtnlcMngrList);
		model.addAttribute("paginationInfo", neoPaginationInfo);

		return "NeoEdu/cop/edcAtnlcMngr/edcAtnlcPayList";
	}  	
  	
  	
  	
  	
  	
  	
  	
  	
  	
  	
  	

}