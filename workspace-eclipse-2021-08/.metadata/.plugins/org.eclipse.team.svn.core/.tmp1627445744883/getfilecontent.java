package kr.co.hanshinit.NeoCMS.cop.pzm.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import kr.co.hanshinit.NeoCMS.cmm.service.CmmUseService;
import kr.co.hanshinit.NeoCMS.cmm.stereotype.AccesType;
import kr.co.hanshinit.NeoCMS.cmm.stereotype.Interceptor;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.cop.pzm.service.PopupZone;
import kr.co.hanshinit.NeoCMS.cop.pzm.service.PopupZoneService;
import kr.co.hanshinit.NeoCMS.sym.cma.cdc.service.CmmnDetailCode;
import kr.co.hanshinit.NeoCMS.sym.cma.cdc.service.CmmnDetailCodeService;
import kr.co.hanshinit.NeoCMS.sym.cma.coc.service.CmmnCodeService;
import kr.co.hanshinit.NeoCMS.sym.sit.sii.service.SiteInfo;
import kr.co.hanshinit.NeoCMS.sym.sit.sii.service.SiteInfoService;
import kr.co.hanshinit.NeoCMS.sym.sit.smm.service.SiteMngrVO;
import kr.co.hanshinit.NeoCMS.uat.uia.service.LoginUtil;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springmodules.validation.commons.DefaultBeanValidator;
/**
 * 팝업존을 관리하기 위한  비즈니스 구현 클래스
 * @author (주)한신정보기술 연구개발팀 최관형
 * @since 2014.06.10
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일           수정자     수정내용
 *  ------------- -------- ---------------------------
 *  2014.06.10 최관형     최초 생성
 *
 * </pre>
 */
@Interceptor(value = "progrmAuthorBinding", type=AccesType.PASS)
@Controller
public class PopupZoneController {

	/** popupZoneService */
	@Resource(name="popupZoneService")
	private PopupZoneService popupZoneService;

	/** cmmnCodeService */
	@Resource(name="cmmnCodeService")
	private CmmnCodeService cmmnCodeService;

	/** cmmnDetailCodeService */
	@Resource(name="cmmnDetailCodeService")
	private CmmnDetailCodeService cmmnDetailCodeService;

	/** siteInfoService */
	@Resource(name="siteInfoService")
	private SiteInfoService siteInfoService;

	/** Validator */
	@Resource(name="beanValidator")
	protected DefaultBeanValidator beanValidator;

	@Resource(name="cmmUseService")
	private CmmUseService cmmUseService;

	/**
	 * 팝업존 목록을 조회한다.
	 * @param popupZone
	 * @param model
	 * @return "NeoCMS/cop/pzm/popupZoneList"
	 * @throws Exception
	 */
	@RequestMapping(value="/neo/selectPopupZoneList.do")
	public String selectPopupZoneList(@ModelAttribute("popupZone") PopupZone popupZone,
			HttpServletRequest request, ModelMap model) throws Exception {

		//권한 인터셉터에서 사이트권한 정보를 가져온다.
		@SuppressWarnings("unchecked")
		List<SiteMngrVO> siteMngrSiteInfoList  = (List<SiteMngrVO>) request.getAttribute("siteMngrSiteInfoList");
		System.out.println("siteMngrSiteInfoList 뽑음!!!");
		System.out.println(siteMngrSiteInfoList);

		if(siteMngrSiteInfoList!=null && siteMngrSiteInfoList.size()!=0){
			System.out.println("111111");


			//사이트 권한이 있는 경우.
			// 기본 사이트 선택
			if( StringUtil.isEmpty(popupZone.getSiteId()) ) {
				popupZone.setSiteId(siteMngrSiteInfoList.get(0).getSiteId());
			}

			// 사이트 목록
			model.addAttribute("siteInfoList", siteMngrSiteInfoList);

			// 사이트정보 목록 MAP으로 변환
			Map<String, String> siteInfoMap = new HashMap<String, String>();
			for(SiteMngrVO siteMngrVO : siteMngrSiteInfoList){
				siteInfoMap.put(siteMngrVO.getSiteId(),siteMngrVO.getSiteNm());
			}
			model.addAttribute("siteInfoMap", siteInfoMap);

		}
		else{
			System.out.println("22222");

			// 기본 사이트 선택
			if( StringUtil.isEmpty(popupZone.getSiteId()) ) {
				SiteInfo siteInfo = siteInfoService.selectDefaultSiteInfo();
				popupZone.setSiteId(siteInfo.getSiteId());
			}

			// 공통코드 : 사이트 구분 SITE01
			List<CmmnDetailCode> siteSeList = cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("SITE01");
			model.addAttribute("siteSeList", siteSeList);

			// 사이트 목록
			List<SiteInfo> siteInfoList = siteInfoService.selectSiteInfoList(new SiteInfo());
			model.addAttribute("siteInfoList", siteInfoList);

			// 사이트정보 목록 MAP으로 변환
			Map<String, String> siteInfoMap = siteInfoService.selectSiteInfoListToMap(siteInfoList);
			model.addAttribute("siteInfoMap", siteInfoMap);
		}
		// 팝업존 목록
		List<PopupZone> popupZoneList = popupZoneService.selectPopupZoneList(popupZone);
		model.addAttribute("popupZoneList", popupZoneList);

		return "NeoCMS/cop/pzm/popupZoneList";

	}

	/**
	 * 팝업존 등록화면으로 이동한다.
	 * @param popupZone
	 * @param model
	 * @return "NeoCMS/cop/pzm/popupZoneRegist"
	 * @throws Exception
	 */
	@RequestMapping("/neo/addPopupZoneView.do")
	public String addPopupZoneView(@ModelAttribute("popupZone") PopupZone popupZone,
			HttpServletRequest request, ModelMap model) throws Exception {

		// 사이트 정보
		SiteInfo siteInfo = siteInfoService.selectSiteInfo(popupZone.getSiteId());
		model.addAttribute("siteInfo", siteInfo);

		// 공통코드 : 사이트 구분 SITE01
		List<CmmnDetailCode> siteSeList = cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("SITE01");
		model.addAttribute("siteSeList", siteSeList);

		//권한 인터셉터에서 사이트권한 정보를 가져온다.
		@SuppressWarnings("unchecked")
		List<SiteMngrVO> siteMngrSiteInfoList  = (List<SiteMngrVO>) request.getAttribute("siteMngrSiteInfoList");
		if(siteMngrSiteInfoList!=null && siteMngrSiteInfoList.size()!=0){
			// 사이트 목록
			model.addAttribute("siteInfoList", siteMngrSiteInfoList);
		}
		else{
			// 사이트 목록
			List<SiteInfo> siteInfoList = siteInfoService.selectSiteInfoList(new SiteInfo());
			model.addAttribute("siteInfoList", siteInfoList);
		}

		model.addAttribute("popupZone", popupZone);

		return "NeoCMS/cop/pzm/popupZoneRegist";

	}

	/**
	 * 팝업존 등록을 처리한다.
	 * @param popupZone
	 * @param bindingResult
	 * @param model
	 * @return 등록화면 호출시 : "cop/pzm/popupZoneRegist",
	 *               등록화면 처리시 : "redirect:/neo/selectPopupZoneList.do?siteId=" + popupZone.getSiteId()
	 * @throws Exception
	 */
	@RequestMapping("/neo/addPopupZone.do")
	public String addPopupZone(@ModelAttribute("popupZone") PopupZone popupZone,
			BindingResult bindingResult, HttpServletRequest request, ModelMap model) throws Exception {

		// Server-Side Validation
		beanValidator.validate(popupZone, bindingResult);

		if (bindingResult.hasErrors()) {
			// 사이트 정보
			SiteInfo siteInfo = siteInfoService.selectSiteInfo(popupZone.getSiteId());
			model.addAttribute("siteInfo", siteInfo);

			//권한 인터셉터에서 사이트권한 정보를 가져온다.
			@SuppressWarnings("unchecked")
			List<SiteMngrVO> siteMngrSiteInfoList  = (List<SiteMngrVO>) request.getAttribute("siteMngrSiteInfoList");
			if(siteMngrSiteInfoList!=null && siteMngrSiteInfoList.size()!=0){
				// 사이트 목록
				model.addAttribute("siteInfoList", siteMngrSiteInfoList);
			}
			else{
				// 공통코드 : 사이트 구분 SITE01
				List<CmmnDetailCode> siteSeList = cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("SITE01");
				model.addAttribute("siteSeList", siteSeList);

				// 사이트 목록
				List<SiteInfo> siteInfoList = siteInfoService.selectSiteInfoList(new SiteInfo());
				model.addAttribute("siteInfoList", siteInfoList);
			}

			model.addAttribute("popupZone", popupZone);
			return "NeoCMS/cop/pzm/popupZoneRegist";
		}

		popupZone.setFrstRegisterId(LoginUtil.getLoginId(request.getSession()));
		popupZoneService.insertPopupZone(popupZone);

		return "redirect:/neo/selectPopupZoneList.do?siteId=" + popupZone.getSiteId();

	}

	/**
	 * 팝업존 수정화면으로 이동한다.
	 * @param popupZone
	 * @param model
	 * @return "NeoCMS/cop/pzm/popupZoneUpdt"
	 * @throws Exception
	 */
	@RequestMapping("/neo/updatePopupZoneView.do")
	public String updatePopupZoneView(@ModelAttribute("popupZone") PopupZone popupZone,
			ModelMap model) throws Exception {

		// 사이트 정보
		SiteInfo siteInfo = siteInfoService.selectSiteInfo(popupZone.getSiteId());
		model.addAttribute("siteInfo", siteInfo);

		// 팝업존 정보
		popupZone = popupZoneService.selectPopupZone(popupZone);
		model.addAttribute("popupZone", popupZone);

		return "NeoCMS/cop/pzm/popupZoneUpdt";

	}

	/**
	 * 팝업존 수정을 처리한다.
	 * @param popupZone
	 * @param bindingResult
	 * @param model
	 * @return 등록화면 호출시 : "NeoCMS/cop/pzm/popupZoneUpdt",
	 *               등록화면 처리시 : "redirect:/neo/selectPopupZoneList.do?siteId=" + popupZone.getSiteId()
	 * @throws Exception
	 */
	@RequestMapping("/neo/updatePopupZone.do")
	public String updatePopupZone(HttpServletRequest request,
			@ModelAttribute("popupZone") PopupZone popupZone,
			BindingResult bindingResult, ModelMap model) throws Exception {

		// Server-Side Validation
		beanValidator.validate(popupZone, bindingResult);

		if (bindingResult.hasErrors()) {
			// 사이트 정보
			SiteInfo siteInfo = siteInfoService.selectSiteInfo(popupZone.getSiteId());
			model.addAttribute("siteInfo", siteInfo);

			model.addAttribute("popupZone", popupZone);
			return "NeoCMS/cop/pzm/popupZoneUpdt";
		}

		popupZone.setLastUpdusrId(LoginUtil.getLoginId(request.getSession()));

		popupZoneService.updatePopupZone(popupZone);

		return "redirect:/neo/selectPopupZoneList.do?siteId=" + popupZone.getSiteId();

	}

	/**
	 * 팝업존을 삭제한다.
	 * @param popupZone
	 * @param model
	 * @return "redirect:/neo/selectPopupZoneList.do?siteId=" + popupZone.getSiteId()
	 * @throws Exception
	 */
	@RequestMapping("/neo/deletePopupZone.do")
	public String deletePopupZone(@ModelAttribute("popupZone") PopupZone popupZone,
			ModelMap model) throws Exception {

		popupZoneService.deletePopupZone(popupZone);

		return "redirect:/neo/selectPopupZoneList.do?siteId=" + popupZone.getSiteId();

	}

}
