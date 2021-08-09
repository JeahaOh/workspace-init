/************************************************************************************************
 * 금천구 통합예약
 *					관리자 > 	강좌 관련 Controller
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

package kr.co.hanshinit.NeoEdu.cop.edcLctre.web;

import java.io.File;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import kr.co.hanshinit.NeoCop.cop.cmm.util.StaffLoginUtil;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddField;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldBndl;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldService;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldVO;
import kr.co.hanshinit.NeoCop.cop.resveAtchmnfl.service.ResveAtchmnfl;
import kr.co.hanshinit.NeoCop.cop.resveAtchmnfl.service.ResveAtchmnflService;
import kr.co.hanshinit.NeoCop.cop.resveAtchmnfl.service.ResveAtchmnflVO;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionBndl;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionService;
import kr.co.hanshinit.NeoCop.cop.resveOption.service.ResveOptionVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.CodeCmmVO;
import kr.co.hanshinit.NeoEdu.cop.cmm.service.EdcCmmCodeService;
import kr.co.hanshinit.NeoEdu.cop.edcInsttMngr.service.EdcInsttMngr;
import kr.co.hanshinit.NeoEdu.cop.edcInsttMngr.service.EdcInsttMngrService;
import kr.co.hanshinit.NeoEdu.cop.edcInsttMngr.service.EdcInsttMngrVO;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctre;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreActpln;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreCopyDeleteService;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreService;
import kr.co.hanshinit.NeoEdu.cop.edcLctre.service.EdcLctreVO;
import kr.co.hanshinit.NeoEdu.cop.edcLctreDiv.service.EdcLctreDiv;
import kr.co.hanshinit.NeoEdu.cop.edcLctreDiv.service.EdcLctreDivService;
import kr.co.hanshinit.NeoEdu.cop.edcLctreDiv.service.EdcLctreDivVO;
import kr.co.hanshinit.NeoEdu.cop.edcLctreInstrctr.service.EdcLctreInstrctr;
import kr.co.hanshinit.NeoEdu.cop.edcLctreInstrctr.service.EdcLctreInstrctrService;
import kr.co.hanshinit.NeoEdu.cop.edcLctreInstrctr.service.EdcLctreInstrctrVO;
import kr.co.hanshinit.NeoEdu.cop.edcRefndRegltn.service.EdcRefndRegltnService;
import net.sf.json.JSONObject;

@Controller
@Interceptor("staffAuthorInterceptor")
public class EdcLctreController {
	protected Logger egovLogger = LoggerFactory.getLogger(this.getClass());

	@Resource(name = "cmmUseService")
	private CmmUseService cmmUseService;

	@Resource(name = "cmmnDetailCodeService")
	private CmmnDetailCodeService cmmnDetailCodeService;

	@Resource(name = "beanValidator")
	protected DefaultBeanValidator beanValidator;

	@Resource(name = "edcLctreService")
	private EdcLctreService edcLctreService;

	/** 교육 관련 코드 조회 서비스 **/
	@Resource(name = "edcCmmCodeService")
	private EdcCmmCodeService edcCmmCodeService;

	/** 교육 강좌 [강사 관련 서비스] **/
	@Resource(name="edcLctreInstrctrService")
	private EdcLctreInstrctrService edcLctreInstrctrService;

	/** 강좌복사, 삭제 서비스 **/
	@Resource(name="edcLctreCopyDeleteService")
	private EdcLctreCopyDeleteService edcLctreCopyDeleteService;

	/** 강좌분류 관리 서비스 **/
	@Resource(name="edcLctreDivService")
	private EdcLctreDivService edcLctreDivService;

	@Resource(name="resveOptionService")
	private ResveOptionService resveOptionService;

	/** 첨부파일 업로드 **/
	@Resource(name="resveAtchmnflService")
	private ResveAtchmnflService resveAtchmnflService;

	/** 환불규정 서비스 **/
	@Resource(name="edcRefndRegltnService")
	private EdcRefndRegltnService edcRefndRegltnService;

	/** 프로그램 신청양식 추가 등록 **/
	@Resource(name="resveAddFieldService")
	private ResveAddFieldService resveAddFieldService;

	/** 기관 정보 서비스 **/
	@Resource(name="edcInsttMngrService")
	private EdcInsttMngrService edcInsttMngrService;

	@ModelAttribute("list")
	EdcLctreVO getSessionModelAttribute(HttpServletRequest request, @RequestParam(value="rep", required=false) String rep) throws Exception{
		EdcLctreVO edcLctreVO = new EdcLctreVO();
		if("1".equals(rep)) {
			request.getSession().setAttribute("edcLctreMng", edcLctreVO);
		}

		if(edcLctreVO != null){
			edcLctreVO = (EdcLctreVO)request.getSession().getAttribute("edcLctreMng");
			Map<String, String> map = new HashMap<String, String>();
			map = PaginationModel.modelExtractMap(edcLctreVO, map);
	        List<NameValuePair> paramList = PaginationModel.convertParam(map);
	        request.getSession().setAttribute("mparam", "&" + URLEncodedUtils.format(paramList, "UTF-8"));
		}

		return edcLctreVO;
	}

	/**
	 * 교육강좌 분류정보 및 강좌정보 - 목록(관리자)
	 *
	 * @param edcLctreVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type = AccesType.REJECT, code = "LIST", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreMngList.do")
	public String edcLctreMngList(
				 @PathVariable("siteId") String siteId
				, @ModelAttribute("list") EdcLctreVO edcLctreVO
				, HttpServletRequest request
				, ModelMap model) throws Exception {

		CodeCmmVO vo = new CodeCmmVO();

		if(edcLctreVO != null){
			String userId = StaffLoginUtil.getLoginId(request.getSession());

			if(!StaffLoginUtil.isAdmin(request.getSession())){
				if("".equals(userId)){
					edcLctreVO.setAuthUserId("000000000000000000000000");
					vo.setAuthUserId("000000000000000000000000");
				}else{
					edcLctreVO.setAuthUserId(userId);
					vo.setAuthUserId(userId);
				}
			}

			edcLctreVO.setSearchStartDate(StringUtil.strReplaceALL(edcLctreVO.getSearchStartDate(), "-", ""));
			edcLctreVO.setSearchEndDate(StringUtil.strReplaceALL(edcLctreVO.getSearchEndDate(), "-", ""));
		}else{
			edcLctreVO = new EdcLctreVO();
		}

		/** 코드 값 조회 **/
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

		/** 강좌 목록 조회 **/
		int totCnt = edcLctreService.selectEdcLctreTotCnt(request, edcLctreVO);
		NeoPaginationInfo neoPaginationInfo = edcLctreVO.getNeoPaginationInfo(totCnt);

		List<EdcLctreVO> edcLctreList = edcLctreService.webSelectEdcLctreAtnlcList(request, edcLctreVO, insttList);

		edcLctreVO.setSearchStartDate(DateUtil.getDateFormat(edcLctreVO.getSearchStartDate(), "yyyy-mm-dd"));
		edcLctreVO.setSearchEndDate(DateUtil.getDateFormat(edcLctreVO.getSearchEndDate(), "yyyy-mm-dd"));

		model.addAttribute("totCnt", totCnt);
		model.addAttribute("edcLctreList", edcLctreList);
		model.addAttribute("paginationInfo", neoPaginationInfo);

		return "NeoEdu/cop/edcLctre/edcLctreList";
	}

	/**
	 * 교육강좌 분류정보 및 강좌정보 - 조회(관리자)
	 *
	 * @param edcLctreVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding",	"progrmAuthorBinding" }, type = AccesType.REJECT, code = "VIEW", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreMngView.do")
	public String edcLctreView(HttpServletRequest request, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO,
			ModelMap model) throws Exception {

		EdcLctre edcLctreData = edcLctreService.selectEdcLctreData(request, edcLctreVO);
		model.addAttribute("edcLctre", edcLctreData);

		return "NeoEdu/cop/edcLctre/edcLctreView";
	}

	/**
	 * 교육강좌 분류정보 및 강좌정보 - 등록(관리자)
	 *
	 * @param edcLctreVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding",	"progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/addEdcLctreMngView.do")
	public String addEdcLctreMngView(@ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO, ModelMap model,
			HttpServletRequest request) throws Exception {

		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 학과분류, 강좌분류(대분류), 6진분류(1차), 강좌접수 형태
		// 학점은행제평가_학점인정여부, 직업능력개발훈련비 지원 강좌 여부, 평생학습계좌제평가 인정여부, 시민제안채택강좌 여부, 교육대상
		// 접수방법, 접수상태, 접수상태 변경 옵션, 연령제한사용유무 , 모집방법,
		// 학점은행제평가(학점) 여부, 교육방법, 사용유무, 강좌수준, 교육주기, 교육대상
		vo.setSearchCodeType("'LES_DIV', 'TCH_LEC_DIV1', 'LEC_DIV_LVL1', 'LEC_REG_CLASS'"
				+ ", 'LEC_CB_YN', 'LEC_HRD_YN', 'LEC_ALL_YN', 'LEC_ALL_CIVIL', 'LEC_TARGET'"
				+ ", 'LEC_APP_TYPE', 'RCEPT_STTUS', 'RCEPT_STTUS_AUTO_AT', 'LEC_ADD_AGE_YN', ''    "
				+ ", 'PNT_BANK_YN', 'LEC_TYPE', 'USE_AT', 'LEC_ADD_LVL', 'EDC_CYCLE', 'LEC_TARGET' ");
		vo.setSearchUseAt("Y");
		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);

		/** 기관 목록 조회 **/
		vo.setSearchDeleteAt("N");
		vo.setSearchInsttGubun("EC");
		vo.setSearchUseAt("Y");
		List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

		/** 오늘일자 **/
		String nowDate = DateUtil.getNowDateTime("yyyy-MM-dd");

		model.addAttribute("nowDate", nowDate); // 오늘 날짜
		model.addAttribute("listCodeMap", listCodeMap); // 코드값 목록 Map
		model.addAttribute("insttList", insttList); // 기관 목록

		return "NeoEdu/cop/edcLctre/edcLctreRegist";
	}

	/**
	 * 강좌 등록 1단계
	 *
	 * @param edcLctreVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreStep01.do")
	public String edcLctreStep01(
									@RequestParam("key") int key
									, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
									, ModelMap model
									, HttpServletRequest request) throws Exception {

		/** 코드 값 조회 후 HashMap 으로 반환  **/
		CodeCmmVO vo = new CodeCmmVO();
		// 학과분류, 강좌분류(대분류), 6진분류(1차), 강좌접수 형태
		// 학점은행제평가_학점인정여부, 직업능력개발훈련비 지원 강좌 여부, 평생학습계좌제평가 인정여부, 시민제안채택강좌 여부, 교육대상
		// 접수방법, 접수상태, 접수상태 변경 옵션, 연령제한사용유무 , 모집방법,
		// 학점은행제평가(학점) 여부, 교육방법, 사용유무, 강좌수준, 교육주기, 교육대상
		vo.setSearchCodeType("'LES_DIV', 'TCH_LEC_DIV1', 'LEC_DIV_LVL1', 'LEC_REG_CLASS'"
						+ ", 'LEC_CB_YN', 'LEC_HRD_YN', 'LEC_ALL_YN', 'LEC_ALL_CIVIL', 'LEC_TARGET'"
						+ ", 'LEC_APP_TYPE', 'RCEPT_STTUS', 'RCEPT_STTUS_AUTO_AT', 'LEC_ADD_AGE_YN', ''    "
						+ ", 'PNT_BANK_YN', 'LEC_TYPE', 'USE_AT', 'LEC_ADD_LVL', 'EDC_CYCLE', 'LEC_TARGET' "
						+ ", 'LEC_CATGY','EDC_STTUS','LEC_REG_TYPE' ");
		vo.setSearchUseAt("Y");
		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);

		/** 예약그룹  조회 **/
		vo.setSiteId("staff");
		vo.setLoginUserId(StaffLoginUtil.getLoginId(request.getSession()));
		vo.setSearchDeleteAt("N");
		vo.setSearchInsttGubun("EC");
		vo.setSearchUseAt("Y");
		List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

		EdcLctre edcLctre = new EdcLctre();

		/** 조회, 수정 시 **/
		if(!"".equals(StringUtil.strTrim(edcLctreVO.getSearchLctreKey()))){
			/** 강좌 내용 조회 **/
			edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);

			for(CodeCmmVO forVo : insttList){
				if(forVo.getCodeValue().equals(edcLctre.getInsttCode()+"")){
					edcLctre.setInsttNm(forVo.getCodeNm());
					edcLctreVO.setSearchInsttCode(edcLctre.getInsttCode()+"");
					break;
				}
			}

			CodeCmmVO codeCmmVO = new CodeCmmVO();

			/** 분류 코드명 조회  **/
			EdcLctreDivVO edcLctreDivVO = new EdcLctreDivVO();
			edcLctreDivVO.setSearchLctreDivCode(edcLctre.getLctreDivCode()+"");
			EdcLctreDiv edcLctreDiv =  edcLctreDivService.selectEdcLctreDivData(request, edcLctreDivVO);

			if(edcLctreDiv != null)
				edcLctre.setLctreDivNm(edcLctreDiv.getLctreDivNm());
		}

		if("".equals(StringUtil.strTrim(edcLctre.getGpsLa()))){
			edcLctre.setGpsLa("37.4570784");
			edcLctre.setGpsLo("126.8957012");
		}

		model.addAttribute("edcLctre", edcLctre);
		model.addAttribute("listCodeMap", listCodeMap); 		// 코드값 목록 Map
		model.addAttribute("insttList", insttList); 						// 기관 목록

		return "NeoEdu/cop/edcLctre/edcLctreStep01";
	}

	/**
	 * 강좌 등록 2단계
	 *
	 * @param edcLctreVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding",	"progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreStep02.do")
	public String edcLctreStep02(@RequestParam("key") int key, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO, ModelMap model,
			HttpServletRequest request) throws Exception {

		EdcLctre edcLctre = new EdcLctre();

		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 교육대상
		// 접수방법, 접수상태, 접수상태 변경 옵션, 연령제한사용유무 , 모집방법,
		// 학점은행제평가(학점) 여부, 교육방법, 사용유무, 강좌수준, 교육주기, 교육대상
		vo.setSearchCodeType("'LEC_APP_TYPE', 'RCEPT_STTUS', 'RCEPT_STTUS_AUTO_AT', 'LEC_ADD_AGE_YN' "
				+ ", 'LEC_TYPE', 'USE_AT', 'LEC_ADD_LVL', 'EDC_CYCLE', 'LEC_TARGET', 'LEC_REG_CLASS', 'EDC_STTUS','LEC_REG_TYPE'");
		vo.setSearchUseAt("Y");
		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);

		model.addAttribute("listCodeMap", listCodeMap); // 코드값 목록 Map

		/** 강좌정보 조회 **/
		if(!"".equals(StringUtil.strTrim(edcLctreVO.getSearchLctreKey()))){
			edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);
		}
		model.addAttribute("edcLctre", edcLctre);

		/** 교육대상 / 할인대상 조회 **/
		ResveOptionVO resveOptionVO = new ResveOptionVO();
		resveOptionVO.setSearchMngrKey(edcLctreVO.getSearchLctreKey());
		resveOptionVO.setSearchJobSe("L");
		HashMap<String, Object> optionMap =  resveOptionService.selectOptionAndCode(resveOptionVO, "'DSCNT_CODE', 'LEC_TARGET'");

		model.addAttribute("optionMap", optionMap);

		/** 프로그램 신청양식 추가 등록 조회 **/
		ResveAddFieldVO resveAddFieldVO = new ResveAddFieldVO();
		resveAddFieldVO.setSearchManagerSeq(edcLctreVO.getSearchLctreKey());
		resveAddFieldVO.setSearchDeleteAt("N");
		resveAddFieldVO.setSearchJobSe("EC");

		List<ResveAddField> addFieldList =  resveAddFieldService.selectResveAddFieldListAll(resveAddFieldVO);
		model.addAttribute("addFieldList", addFieldList);

		/** 기관 정보 조회 **/
		EdcInsttMngrVO edcInsttMngrVO = new EdcInsttMngrVO();
		edcInsttMngrVO.setInsttCode(edcLctre.getInsttCode());
		EdcInsttMngr edcInsttMngr =  edcInsttMngrService.selectEdcInsttMngrData(request, edcInsttMngrVO);
		model.addAttribute("edcInsttMngr", edcInsttMngr);

		return "NeoEdu/cop/edcLctre/edcLctreStep02";
	}

	/**
	 * 강좌 등록 3단계
	 *
	 * @param edcLctreVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding",	"progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreStep03.do")
	public String edcLctreStep03(@RequestParam("key") int key, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO, ModelMap model,
			HttpServletRequest request) throws Exception {

		EdcLctre edcLctre = new EdcLctre();

		/** 오늘일자 **/
		String nowDate = DateUtil.getNowDateTime("yyyy-MM-dd");

		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 학년코드
		vo.setSearchCodeType("'GRADE_CODE', 'LEC_APP_TYPE'");
		vo.setSearchUseAt("Y");
		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);
		model.addAttribute("listCodeMap", listCodeMap); // 코드값 목록 Map

		/** 강좌정보 조회 **/
		if(!"".equals(StringUtil.strTrim(edcLctreVO.getSearchLctreKey()))){
			edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);
		}
		model.addAttribute("edcLctre", edcLctre);
		model.addAttribute("nowDate", nowDate); // 오늘 날짜

		/** 연계강좌 조회  **/
		edcLctreVO.setSearchUseAt("Y");
		edcLctreVO.setSearchDeleteAt("N");
		List<EdcLctre> cntcList =  edcLctreService.selectEdcLctreCntcData(edcLctreVO);
		model.addAttribute("cntcList", cntcList);

		return "NeoEdu/cop/edcLctre/edcLctreStep03";
	}

	/**
	 * 강좌등록 4단계 (강사 정보 저장)
	 *
	 * @param edcLctreVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding","progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreStep04.do")
	public String edcLctreStep04(
					@RequestParam("key") int key
					, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
					, ModelMap model
					, HttpServletRequest request) throws Exception {

		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 강사 구분
		vo.setSearchCodeType("'TCH_TYPE' ");
		vo.setSearchUseAt("Y");
		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);
		model.addAttribute("listCodeMap", listCodeMap);

		EdcLctre edcLctre = new EdcLctre();

		/** 강좌정보 조회 **/
		if(!"".equals(StringUtil.strTrim(edcLctreVO.getSearchLctreKey()))){
			edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);
		}
		model.addAttribute("edcLctre", edcLctre);

		// 강사정보 조회
		EdcLctreInstrctrVO edcLctreInstrctrVO = new EdcLctreInstrctrVO();
		edcLctreInstrctrVO.setSearchLctreKey(edcLctreVO.getSearchLctreKey());
		edcLctreInstrctrVO.setSearchDeleteAt("N");
		edcLctreInstrctrVO.setSearchUseAt("Y");
		//HashMap<String, Object> rtnMap = edcLctreInstrctrService.selectEdcLctreInstrctrDataMap(request, edcLctreInstrctrVO);
		//model.addAttribute("rtnMap", rtnMap);

		//강사정보 조회 변경(2019.07.19)
		List<EdcLctreInstrctr> listEdcLctreInstrctr = edcLctreInstrctrService.selectEdcLctreInstrctrDataList(request, edcLctreInstrctrVO);
		model.addAttribute("listEdcLctreInstrctr", listEdcLctreInstrctr);

		return "NeoEdu/cop/edcLctre/edcLctreStep04";
	}

	/**
	 * 강좌등록 5단계
	 *
	 * @param edcLctreVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding",	"progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreStep05.do")
	public String edcLctreStep05(
										@RequestParam("key") int key
										, @PathVariable(value = "siteId") String siteId
										, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
										, @ModelAttribute("edcLctreActpln") EdcLctreActpln edcLctreActpln
										, ModelMap model
										, HttpServletRequest request)
										throws Exception {

		/** 오늘일자 **/
		String nowDate = DateUtil.getNowDateTime("yyyy-MM-dd");

		/** 강의 계획서 조회 **/
		edcLctreActpln.setLctreKey(StringUtil.strToInt(edcLctreVO.getSearchLctreKey()));
		edcLctreActpln.setDeleteAt("N");
		edcLctreActpln.setSiteId(siteId);
		List<EdcLctreActpln> list = edcLctreService.selectEdcLctreActplnData(request, edcLctreActpln);

		ResveAtchmnflVO resveAtchmnflVO = new ResveAtchmnflVO();
		resveAtchmnflVO.setSearchManageNo(edcLctreVO.getSearchLctreKey());
		resveAtchmnflVO.setSearchJobSe("EC");
		resveAtchmnflVO.setSearchUseAt("Y");
		resveAtchmnflVO.setSearchDeleteAt("N");

		List<ResveAtchmnfl> resveAtchmnflList =  resveAtchmnflService.selectResveAtchmnflListAll(resveAtchmnflVO);
		
		EdcLctre edcLctre = new EdcLctre();
		
		/** 강좌정보 조회 **/
		if(!"".equals(StringUtil.strTrim(edcLctreVO.getSearchLctreKey()))){
			edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);
		}
		model.addAttribute("edcLctre", edcLctre);

		model.addAttribute("list", list); 																// 강의계획서 리스트
		model.addAttribute("CNT", resveAtchmnflList.size()+1); 				// 첨부파일 개수
		model.addAttribute("resveAtchmnflList", resveAtchmnflList); 			// 첨부파일 목록
		model.addAttribute("nowDate", nowDate); 										// 오늘 날짜

		return "NeoEdu/cop/edcLctre/edcLctreStep05";
	}

	/**
	 * 교육강좌 분류정보 및 강좌정보 - 등록처리(관리자)
	 *
	 * @param multiPartRequest
	 * @param edcLctreVO
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@ResponseBody
	@RequestMapping("/staff/addEdcLctreMng.do")
	public ResponseJSON addEdcLctre(@RequestParam("key") int key, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO,
			final MultipartHttpServletRequest multiPartRequest, HttpServletRequest request, BindingResult bindingResult,
			ModelMap model) throws Exception {

		ResponseJSON rs = new ResponseJSON();
		model.addAttribute("edcLctreVO", edcLctreVO);

		beanValidator.validate(edcLctreVO, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("edcLctreVO", edcLctreVO);
			// return "NeoCMS/cop/edcLctre/edcLctreUpdt";
		}

		try {

			int lctreKey = edcLctreService.insertEdcLctre(request, edcLctreVO);

		} catch (Exception e) {
			model.addAttribute("edcLctre", edcLctreVO);
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
	 * 교육강좌 분류정보 및 강좌정보 - 등록처리(관리자)
	 *
	 * @param multiPartRequest
	 * @param edcLctreVO
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/insertEdcLctreStep01.do")
	public void insertEdcLctreStep01(
										@RequestParam("key") int key
										, @PathVariable(value = "siteId") String siteId
										, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
										, HttpServletRequest request
										, HttpServletResponse response
										, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		model.addAttribute("edcLctreVO", edcLctreVO);

		try {
			int lctreKey = 0;


			if("".equals(StringUtil.strTrim(edcLctreVO.getSearchLctreKey()))){
				edcLctreVO.setUseAt("N");
				lctreKey = edcLctreService.insertEdcLctreStep01(request, edcLctreVO);
			}else{
				EdcLctre edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);

				String resultStr = "";
				if("Y".equals(edcLctreVO.getUseAt())){
					resultStr = edcLctreService.edcNecessary(edcLctre);

					if(!"S".equals(resultStr)){
						edcLctreVO.setUseAt("N");
						obj.put("msgCheck", resultStr + "(필수값) 입력되지 않아 사용여부를 사용으로 변경할수 없습니다.\n\r다른 정보는 저장 처리 되었습니다.");
						obj.put("msgCheckStr", resultStr);
					}
				}

				edcLctreService.updateEdcLctreStep01(request, edcLctreVO);
				lctreKey =  StringUtil.strToInt(edcLctreVO.getSearchLctreKey());
			}

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("msg", "저장 성공");
			obj.put("lctreKey", lctreKey);

		} catch (Exception e) {
			obj.put("result", "F");
			obj.put("msg", e);
			egovLogger.error("EdcLctreController.insertEdcLctreStep01 : ", e);

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	@Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@ResponseBody
	@RequestMapping("/{siteId}/updateEdcLctreStep02.do")
	public void updateEdcLctreStep02(
			 						@RequestParam("key") int key
								    , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
								    , @ModelAttribute("resveAddFieldBndl") ResveAddFieldBndl resveAddFieldBndl
								    , @ModelAttribute("resveOptionBndl") ResveOptionBndl resveOptionBndl
									, BindingResult bindingResult
									, HttpServletRequest request
									, HttpServletResponse response
									, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		model.addAttribute("edcLctreVO", edcLctreVO);

		try {
			int resultCnt = edcLctreService.updateEdcLctreStep02(request, edcLctreVO, resveAddFieldBndl, resveOptionBndl);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("msg", "저장 성공");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

		} catch (Exception e) {
			obj.put("result", "F");
			obj.put("msg", e);
			egovLogger.error("EdcLctreController.updateEdcLctreStep02 : ", e);

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	/**
	 * 일정정보 저장
	 * @param edcLctreVO
	 * @param bindingResult
	 * @param multiPartRequest
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/updateEdcLctreStep03.do")
	public void updateEdcLctreStep03(
									@RequestParam("key") int key
								    , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
									, HttpServletRequest request
									, HttpServletResponse response
									, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		model.addAttribute("edcLctreVO", edcLctreVO);

		try {
			edcLctreVO.setSearchLctreKey(edcLctreVO.getLctreKey()+"");
			int resultCnt = edcLctreService.updateEdcLctreStep03(request, edcLctreVO);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("msg", "저장 성공");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

		} catch (Exception e) {
			obj.put("result", "F");
			obj.put("msg", e);
			egovLogger.error("EdcLctreController.updateEdcLctreStep03 : ", e);

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}


	/**
	 * 일정정보 저장
	 * @param edcLctreVO
	 * @param bindingResult
	 * @param multiPartRequest
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@ResponseBody
	@RequestMapping("/{siteId}/updateEdcLctreStep04.do")
	public void updateEdcLctreStep04(
									@RequestParam("key") int key
									, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
								    , @ModelAttribute("edcLctreInstrctrVO") EdcLctreInstrctrVO edcLctreInstrctrVO
									, BindingResult bindingResult
									, final MultipartHttpServletRequest multiPartRequest
									, HttpServletRequest request
									, HttpServletResponse response
									, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		model.addAttribute("edcLctreVO", edcLctreVO);

		beanValidator.validate(edcLctreVO, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("edcLctreVO", edcLctreVO);
			// return "NeoCMS/cop/edcLctre/edcLctreUpdt";
		}

		try {
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			edcLctreInstrctrVO.setSearchLctreKey(edcLctreVO.getSearchLctreKey());

			// 강사 정보 저장
			int resultCnt = edcLctreInstrctrService.insertEdcLctreInstrctrArr(request, edcLctreInstrctrVO);

			System.out.println("resultCnt : " + resultCnt);


			if(resultCnt == 0){
				obj.put("result", "F");
				obj.put("msg", "저장하지 못했습니다.");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}else{
				obj.put("result", "S");
				obj.put("msg", "저장 성공");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}

		} catch (Exception e) {
			obj.put("result", "F");
			obj.put("msg", e);
			egovLogger.error("EdcLctreController.updateEdcLctreStep03 : ", e);

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	/**
	 * 강좌 폐강처리
	 * @param key
	 * @param edcLctreVO
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = { "templateBinding", "progrmAuthorBinding" }, type = AccesType.REJECT, code = "LIST", progrmId = "EL01")
	@RequestMapping("/{siteId}/updateEdcLctreCancelYn.do")
	public String updateEdcLctreCancelYn(
									  @RequestParam("key") int key
									, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
									, HttpServletRequest request
									, ModelMap model) throws Exception {

		String strLctreKey = "";
		for(int i=0;i<edcLctreVO.getLctreKeyArr().length;i++){
			strLctreKey += edcLctreVO.getLctreKeyArr()[i] +  ",";
		}

		if(strLctreKey.length() > 0){
			strLctreKey = strLctreKey.substring(0, strLctreKey.length()-1);

			edcLctreVO.setLctreKeyStr(strLctreKey);
		}

		int rtn = edcLctreService.updateEdcLctreCancelYn(request, edcLctreVO);

		return cmmUseService.redirectMsg(model, "폐강처리 완료 되었습니다.",
				"./edcLctreMngList.do?key=" + key + "&pageUnit=" + edcLctreVO.getPageUnit() + "&searchCnd="
						+ edcLctreVO.getSearchCnd() + "&searchKrwd=" + edcLctreVO.getSearchKrwd() + "&pageIndex="
						+ edcLctreVO.getPageIndex());

	}


	/**
	 * 교육강좌 분류정보 및 강좌정보 - 수정처리(관리자)
	 *
	 * @param edcLctreVO
	 * @param request
	 * @param model
	 * @return "NeoCMS/cop/edcLctre/edcLctreMngUpdt"
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "UPDATE", progrmId = "EL01")
	@ResponseBody
	@RequestMapping("/{siteId}/updateEdcLctreMng.do")
	public ResponseJSON EdcLctreUpdProc(@RequestParam("key") int key, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO,
			BindingResult bindingResult, final MultipartHttpServletRequest multiPartRequest, HttpServletRequest request,
			ModelMap model) throws Exception {

		ResponseJSON rs = new ResponseJSON();

		beanValidator.validate(edcLctreVO, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("edcLctreVO", edcLctreVO);
			// return "NeoCMS/cop/edcLctre/edcLctreUpdt";
		}

		try {

			edcLctreService.updateEdcLctre(request, edcLctreVO);

		} catch (Exception e) {
			model.addAttribute("edcLctre", edcLctreVO);
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
	 * 교육강좌 분류정보 및 강좌정보 - 삭제처리(관리자)
	 *
	 * @param edcLctreVO
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type = AccesType.REJECT, code = "DELETE", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreMngDel.do")
	public String edcLctreDel(@RequestParam("key") int key, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO,
			HttpServletRequest request, ModelMap model) throws Exception {

		String rtnMsg = "";

		try {
			int rtnCnt = edcLctreCopyDeleteService.edcLctreDeleteAll(request, edcLctreVO);

			if(rtnCnt == 0){
				rtnMsg = "해당 강좌는 수강생이 존재하여 삭제 할 수 없습니다.";
			}else{
				rtnMsg = "삭제 처리 되었습니다.";
			}

		} catch (Exception e) {

			model.addAttribute("edcLctre", edcLctreVO);
			model.addAttribute("isValidate", true);
			model.addAttribute("message", e.getMessage());
			e.printStackTrace();

			return "NeoEdu/cop/edcLctre/edcLctreList";
		}

		return cmmUseService.redirectMsg(model, rtnMsg,
				"./edcLctreMngList.do?key=" + key + "&pageUnit=" + edcLctreVO.getPageUnit() + "&searchCnd="
						+ edcLctreVO.getSearchCnd() + "&searchKrwd=" + edcLctreVO.getSearchKrwd() + "&pageIndex="
						+ edcLctreVO.getPageIndex());
	}


	/**
	 * 교육 강좌 강사정보 - 목록(관리자)
	 * @param edcLctreInstrctrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="LIST", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreInstrctrMngList.do")
	public String edcLctreInstrctrMngList(
			@ModelAttribute("edcLctreInstrctrVO") EdcLctreInstrctrVO edcLctreInstrctrVO,
			ModelMap model) throws Exception {

		int totCnt = edcLctreInstrctrService.selectEdcLctreInstrctrTotCnt(edcLctreInstrctrVO);
		NeoPaginationInfo neoPaginationInfo = edcLctreInstrctrVO.getNeoPaginationInfo(totCnt);
		List<EdcLctreInstrctr> edcLctreInstrctrList = edcLctreInstrctrService.selectEdcLctreInstrctrList(edcLctreInstrctrVO);

		model.addAttribute("totCnt", totCnt);
		model.addAttribute("edcLctreInstrctrList", edcLctreInstrctrList);
        model.addAttribute("paginationInfo", neoPaginationInfo);

		return "NeoEdu/cop/edcLctreInstrctr/edcLctreInstrctrList";
	}

  	/**
	 * 교육 강좌 강사정보 - 조회(관리자)
	 * @param edcLctreInstrctrVO
	 * @param model
	 * @return
	 * @throws Exception
	 */
  	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="VIEW", progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreInstrctrMngView.do")
	public String edcLctreInstrctrView(HttpServletRequest request,
			@ModelAttribute("edcLctreInstrctrVO") EdcLctreInstrctrVO edcLctreInstrctrVO, ModelMap model) throws Exception {

		EdcLctreInstrctr edcLctreInstrctrData = edcLctreInstrctrService.selectEdcLctreInstrctrData(request, edcLctreInstrctrVO);
		model.addAttribute("edcLctreInstrctr", edcLctreInstrctrData);

		return "NeoEdu/cop/edcLctreInstrctr/edcLctreInstrctrView";
	}

  	/**
	 * 교육 강좌 강사정보 - 등록처리(관리자)
	 * @param multiPartRequest
	 * @param edcLctreInstrctrVO
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="ADD", progrmId = "EL01")
  	@ResponseBody
	@RequestMapping("/{siteId}/addEdcLctreInstrctrMng.do")
	public ResponseJSON addEdcLctreInstrctr(
			@ModelAttribute("edcLctreInstrctrVO") EdcLctreInstrctrVO edcLctreInstrctrVO,
			final MultipartHttpServletRequest multiPartRequest,HttpServletRequest request,
			BindingResult bindingResult,
			ModelMap model) throws Exception {

		ResponseJSON rs = new ResponseJSON();
		model.addAttribute("edcLctreInstrctrVO", edcLctreInstrctrVO);

		beanValidator.validate(edcLctreInstrctrVO, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("edcLctreInstrctrVO", edcLctreInstrctrVO);
//			return "NeoCMS/cop/edcLctreInstrctr/edcLctreInstrctrUpdt";
		}

		try {

			edcLctreInstrctrService.insertEdcLctreInstrctr(request, edcLctreInstrctrVO);

		} catch(Exception e) {
			model.addAttribute("edcLctreInstrctr", edcLctreInstrctrVO);
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
	 * 교육 강좌 강사정보 - 수정처리(관리자)
	 * @param edcLctreInstrctrVO
	 * @param request
	 * @param model
	 * @return "NeoCMS/cop/edcLctreInstrctr/edcLctreInstrctrMngUpdt"
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE", progrmId = "EL01")
	@ResponseBody
	@RequestMapping("/{siteId}/updateEdcLctreInstrctrMng.do")
	public ResponseJSON EdcLctreInstrctrUpdProc(
			@ModelAttribute("edcLctreInstrctrVO") EdcLctreInstrctrVO edcLctreInstrctrVO, BindingResult bindingResult,
			final MultipartHttpServletRequest multiPartRequest,HttpServletRequest request,  ModelMap model) throws Exception {

		ResponseJSON rs = new ResponseJSON();

		beanValidator.validate(edcLctreInstrctrVO, bindingResult);
		if (bindingResult.hasErrors()) {
			model.addAttribute("edcLctreInstrctrVO", edcLctreInstrctrVO);
//			return "NeoCMS/cop/edcLctreInstrctr/edcLctreInstrctrUpdt";
		}

		try {

			edcLctreInstrctrService.updateEdcLctreInstrctr(request, edcLctreInstrctrVO);

		} catch(Exception e) {
			model.addAttribute("edcLctreInstrctr", edcLctreInstrctrVO);
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

	/************************************************************************************************************
	 *	강의 계획서 관련 Controller  시작
	 *       					강좌관리 5단계
	 ************************************************************************************************************/


	/**
	 * 강좌 강의 계획서 관리 - 등록처리(관리자)
	 * @param multiPartRequest
	 * @param edcLctreActplnVO
	 * @param request
	 * @param bindingResult
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="ADD", progrmId = "EL01")
  	@RequestMapping("/{siteId}/insertEdcLctreActplnMng.do")
	public void insertEdcLctreActplnMng(
											  @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
											, @ModelAttribute("edcLctreActpln") EdcLctreActpln edcLctreActpln
											, HttpServletRequest request
											, HttpServletResponse response
											) throws Exception {
		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			edcLctreActpln.setLctreKey(StringUtil.strToInt(edcLctreVO.getSearchLctreKey()));
			int cnt = edcLctreService.insertEdcLctreActpln(request, edcLctreActpln);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			if(cnt == 0){
				obj.put("result", "F");
				obj.put("msg", "저장하지 못했습니다.");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}else{
				obj.put("result", "S");
				obj.put("msg", "저장 성공");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}
		} catch(Exception e) {
			e.printStackTrace();
		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	/**
	 * 강좌 강의 계획서 관리 - 수정처리(관리자)
	 * @param edcLctreActplnVO
	 * @param request
	 * @param model
	 * @return "NeoCMS/cop/edcLctreActpln/edcLctreActplnMngUpdt"
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/updateEdcLctreActplnMng.do")
	public void EdcLctreActplnUpdProc(
					@RequestParam("key") int key
			  		, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
			  		, @ModelAttribute("edcLctreActpln") EdcLctreActpln edcLctreActpln
			  		,HttpServletRequest request
			  		, HttpServletResponse response
			  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			int cnt = edcLctreService.updateEdcLctreActpln(request, edcLctreActpln);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			if(cnt == 0){
				obj.put("result", "F");
				obj.put("msg", "저장하지 못했습니다.");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}else{
				obj.put("result", "S");
				obj.put("msg", "저장 성공");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}

		} catch(Exception e) {
			model.addAttribute("edcLctreActpln", edcLctreActpln);
			model.addAttribute("isValidate", true);
			model.addAttribute("message", e.getMessage());
			e.printStackTrace();
		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}

	}


	/**
	 * 강좌 강의 계획서 관리 - 삭제처리(관리자)
	 * @param edcLctreActplnVO
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreActplnMngDel.do")
	public void edcLctreActplnDel(
								@RequestParam("key") int key
							    , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
						  		, @ModelAttribute("edcLctreActpln") EdcLctreActpln edcLctreActpln
						  		,HttpServletRequest request
						  		, HttpServletResponse response
						  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;
		model.addAttribute("edcLctreActpln", edcLctreActpln);

		try {
			edcLctreService.deleteEdcLctreActpln(request, edcLctreActpln);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("msg", "저장 성공");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
		} catch(Exception e) {
			model.addAttribute("edcLctreActpln", edcLctreActpln);
			model.addAttribute("isValidate", true);
			model.addAttribute("message", e.getMessage());

			obj.put("result", "F");
			obj.put("msg", "저장하지 못했습니다.");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

			e.printStackTrace();

		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}


	/**
	 * 강사 삭제  - 삭제처리(관리자)
	 * @param edcLctreActplnVO
	 * @param request
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreInstrctrDel.do")
	public void edcLctreInstrctrDel(
								@RequestParam("key") int key
								, @PathVariable(value = "siteId") String siteId
							    , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
						  		, @ModelAttribute("edcLctreInstrctrVO") EdcLctreInstrctrVO edcLctreInstrctrVO
						  		,HttpServletRequest request
						  		, HttpServletResponse response
						  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {

			edcLctreInstrctrService.deleteEdcLctreInstrctr(request, edcLctreInstrctrVO);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("msg", "삭제 처리 완료 되었습니다.");
		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("msg", "저장하지 못했습니다.");

			e.printStackTrace();
		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}


	/**
	 * 선택 강좌 복사 팝업
	 * @param edcLctreVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = {"progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/popupEdcLctreChoiseCopy.do")
	public String popupEdcLctreChoiseCopy(
					@PathVariable("siteId") String siteId
				 ,  @RequestParam("key") int key
				 ,	@ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
				 , ModelMap model
				 , HttpServletRequest request) throws Exception {
		
		
		/** 코드 값 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		// 학년코드
		vo.setSearchCodeType("'GRADE_CODE', 'LEC_APP_TYPE'");
		vo.setSearchUseAt("Y");
		vo.setSearchDeleteAt("N");

		HashMap<String, List<CodeCmmVO>> listCodeMap = new HashMap<>();
		listCodeMap = edcCmmCodeService.selectEdcCmmMap(vo);
		model.addAttribute("listCodeMap", listCodeMap); // 코드값 목록 Map
		

		/** 오늘일자 **/
		String nowDate = DateUtil.getNowDateTime("yyyy-MM-dd");

		/** 선택 강좌 Key **/
		String strLctreKey = "";
		for(int i=0;i<edcLctreVO.getLctreKeyArr().length;i++){
			strLctreKey += edcLctreVO.getLctreKeyArr()[i] +  ",";
		}

		if(strLctreKey.length() > 0){
			strLctreKey = strLctreKey.substring(0, strLctreKey.length()-1);

			edcLctreVO.setLctreKeyStr(strLctreKey);
		}

		/** 선택 강좌 정보 조회 **/
		List<EdcLctreVO> list = edcLctreService.selectEdcLctreChoiseList(edcLctreVO);

		EdcLctreVO edcLctre = new EdcLctreVO();
		if(list != null) {
			edcLctre = list.get(0);
			model.addAttribute("edcLctre", edcLctre);
		}
		
		model.addAttribute("nowDate", nowDate);
		model.addAttribute("list", list);
		model.addAttribute("key", key);
		model.addAttribute("siteId", siteId);

		return "NeoEdu/cop/edcLctre/popupEdcLctreChoiseCopy";
	}


	/**
	 * 사업분류(기수)별  강좌 복사 팝업
	 * @param edcLctreVO
	 * @param model
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@Interceptor(value = {"progrmAuthorBinding" }, type = AccesType.REJECT, code = "ADD", progrmId = "EL01")
	@RequestMapping("/{siteId}/popupEdcLctreCopy.do")
	public String popupEdcLctreCopy(
			 	   @RequestParam(value="key") String key
				 , @PathVariable("siteId") String siteId
				 , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
				 , ModelMap model
				 , HttpServletRequest request) throws Exception {

		/** 오늘일자 **/
		String nowDate = DateUtil.getNowDateTime("yyyy-MM-dd");

		/** 기관 목록 조회 **/
		CodeCmmVO vo = new CodeCmmVO();
		vo.setSearchDeleteAt("N");
		vo.setSearchInsttGubun("EC");
		vo.setSearchUseAt("Y");
		List<CodeCmmVO> insttList = edcCmmCodeService.selectEdcCmmInsttCode(request, vo);

		model.addAttribute("nowDate", nowDate);
		model.addAttribute("insttList", insttList);

		return "NeoEdu/cop/edcLctre/popupEdcLctreCopy";
	}

	/**
	 * 강좌 사용유무 업데이트
	 * @param edcLctreVO
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/updateEdcLctreUseAt.do")
	public void updateEdcLctreUseAt(
								@RequestParam("key") int key
								, @PathVariable(value = "siteId") String siteId
							    , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
						  		, HttpServletRequest request
						  		, HttpServletResponse response
						  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			// 필수값이 입력되어 있는지 체크
			EdcLctre edcLctre = edcLctreService.selectEdcLctreData(request, edcLctreVO);
			String resultStr = edcLctreService.edcNecessary(edcLctre);

			if("S".equals(resultStr)){
				edcLctreService.updateEdcLctreUseAt(request, edcLctreVO);

				obj.put("result", "S");
				obj.put("msg", "저장 성공");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}else{
				if("Y".equals(edcLctre.getUseAt())){
					edcLctreService.updateEdcLctreUseAt(request, edcLctreVO);

					obj.put("result", "S");
					obj.put("msg", "저장 성공");
					obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
				}else{
					obj.put("result", "F");
					obj.put("msg", resultStr);
				}
			}

		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("msg", "저장하지 못했습니다.");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

			e.printStackTrace();

		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	/**
	 * 파일 업로드
	 * @param edcLctreVO
	 * @param edcLctreActpln
	 * @param multiPartRequest
	 * @param request
	 * @param response
	 * @param bindingResult
	 * @param model
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="ADD", progrmId = "EL01")
  	@ResponseBody
	@RequestMapping("/{siteId}/uploadEdcLctreFile.do")
	public void uploadEdcLctreFile(
											@RequestParam("key") int key
											, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
											, @ModelAttribute("resveAtchmnflVO") ResveAtchmnflVO resveAtchmnflVO
											, final MultipartHttpServletRequest multiPartRequest
											,HttpServletRequest request
											, HttpServletResponse response
											, BindingResult bindingResult
											, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			// 첨부파일 등록
			resveAtchmnflVO.setManageNo(edcLctreVO.getSearchLctreKey());
			resveAtchmnflVO.setJobSe("EC");
			resveAtchmnflVO.setFrstRegisterId(StaffLoginUtil.getLoginId(request.getSession()));
			resveAtchmnflVO.setUploadStorePath(File.separator+"DATA"+File.separator+"DATA"+File.separator+"EC"+File.separator+DateUtil.getNowDateTime("yyyyMM")+File.separator);
			resveAtchmnflVO.setMaxFileSize(30 * 1024 * 1024);

			resveAtchmnflService.uploadResveAtchmnfl(multiPartRequest, request, resveAtchmnflVO);

			obj.put("result", "S");
			obj.put("msg", "저장 성공");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("msg", "첨부파일을 저장하지 못했습니다.");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

		} finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}

	}


	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="DELETE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/edcLctreFileDel.do")
	public void edcLctreFileDel(
								@RequestParam("key") int key
							    , @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
//						  		, @ModelAttribute("edcLctreAtchmnflVO") EdcLctreAtchmnflVO edcLctreAtchmnflVO
						  		,HttpServletRequest request
						  		, HttpServletResponse response
						  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {

//			edcLctreAtchmnflService.deleteEdcLctreAtchmnfl(edcLctreAtchmnflVO);


			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("msg", "삭제 성공");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("msg", "삭제하지 못했습니다.");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

			egovLogger.error("EdcLctreController.edcLctreFileDel", e);

		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	/**
	 * 중복수강, 재수강 제한 강좌 삭제 처리
	 * @param edcLctreVO
	 * @param request
	 * @param response
	 * @param model
	 * @throws Exception
	 */
	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="DELETE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/updateEdcLctreCntc.do")
	public void updateEdcLctreCntc(
							      @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
						  		,HttpServletRequest request
						  		, HttpServletResponse response
						  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			edcLctreVO.setDeleteAt("Y");
			edcLctreVO.setUseAt("N");
			edcLctreService.updateEdcLctreCntc(request, edcLctreVO);

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			obj.put("result", "S");
			obj.put("msg", "삭제 되었습니다.");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("msg", "삭제 처리되지 않았습니다.");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

			e.printStackTrace();

		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="DELETE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/insertEdcLctreCntc.do")
	public void insertEdcLctreCntc(
							      @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
						  		,HttpServletRequest request
						  		, HttpServletResponse response
						  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			// 기존 데이터 확인
			int cnt = edcLctreService.selectEdcLctreCntcTotCnt(edcLctreVO);

			if(cnt > 0){
				obj.put("result", "F");
				obj.put("msg", "이미 등록되어 있는 강좌입니다.");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}else{
				edcLctreVO.setDeleteAt("N");
				edcLctreVO.setUseAt("Y");
				edcLctreService.insertEdcLctreCntc(request, edcLctreVO);

				obj.put("result", "S");
				obj.put("msg", "등록 되었습니다.");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}

		} catch(Exception e) {
			obj.put("result", "F");
			obj.put("msg", "등록 처리되지 않았습니다.");
			obj.put("lctreKey", edcLctreVO.getSearchLctreKey());

			e.printStackTrace();

		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}
	}

	@Interceptor(value = "progrmAuthorBinding", type=AccesType.REJECT, code="UPDATE",  progrmId = "EL01")
	@RequestMapping("/{siteId}/updateEdcLctreActplnMngAll.do")
	public void updateEdcLctreActplnMngAll(
					@RequestParam("key") int key
			  		, @ModelAttribute("edcLctreVO") EdcLctreVO edcLctreVO
			  		, @ModelAttribute("edcLctreActpln") EdcLctreActpln edcLctreActpln
			  		,HttpServletRequest request
			  		, HttpServletResponse response
			  		, ModelMap model) throws Exception {

		JSONObject obj = new JSONObject();
		PrintWriter out = null;

		try {
			int lctreKeyInt = StringUtil.strToInt(edcLctreVO.getSearchLctreKey());
			int cnt = 1;

			if(edcLctreActpln.getLctreDeArr() != null){
				int loopCnt = edcLctreActpln.getLctreDeArr().length;

				EdcLctreActpln loopVo = new EdcLctreActpln();
				for(int i=0;i<loopCnt;i++){
					loopVo = new EdcLctreActpln();
					loopVo.setLctreDe(edcLctreActpln.getLctreDeArr()[i]);
					loopVo.setLctreThema(edcLctreActpln.getLctreThemaArr()[i]);
					loopVo.setLctreCn(edcLctreActpln.getLctreCnArr()[i]);
					loopVo.setLctreKey(lctreKeyInt);
					loopVo.setSn(edcLctreActpln.getSnArr()[i]);

					edcLctreService.updateEdcLctreActpln(request, loopVo);
				}
			}else{
				cnt = 0;
			}

			response.setCharacterEncoding("utf-8");
			out = response.getWriter();

			if(cnt == 0){
				obj.put("result", "F");
				obj.put("msg", "저장할 데이터가 존재하지 않습니다.");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}else{
				obj.put("result", "S");
				obj.put("msg", "저장 성공");
				obj.put("lctreKey", edcLctreVO.getSearchLctreKey());
			}

		} catch(Exception e) {
			model.addAttribute("edcLctreActpln", edcLctreActpln);
			model.addAttribute("isValidate", true);
			model.addAttribute("message", e.getMessage());
			e.printStackTrace();
		}finally {
			out.write(obj.toString());
			out.flush();
			out.close();
		}

	}

}
