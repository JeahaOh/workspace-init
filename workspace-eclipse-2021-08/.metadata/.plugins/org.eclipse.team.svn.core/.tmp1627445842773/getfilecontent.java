package kr.co.hanshinit.NeoCMS.cmm.interceptor;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import kr.co.hanshinit.NeoCMS.cmm.stereotype.AccesType;
import kr.co.hanshinit.NeoCMS.cmm.stereotype.ReturnType;
import kr.co.hanshinit.NeoCMS.cmm.util.SessionUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.sym.prm.aut.service.ProgrmAuthor;
import kr.co.hanshinit.NeoCMS.sym.prm.aut.service.ProgrmAuthorService;
import kr.co.hanshinit.NeoCMS.sym.sit.smm.service.SiteMngrService;
import kr.co.hanshinit.NeoCMS.sym.sit.smm.service.SiteMngrVO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import egovframework.com.cmm.LoginVO;

/**
 * 프로그램권한을 바인딩 하기위한 인터셉터 ProgrmAuthorBindingInterceptor.java
 * @author (주)한신정보기술 개발3팀 황윤태
 * @since 2014. 6. 19.
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일           수정자     수정내용
 *  ------------- -------- ---------------------------
 *  2014. 6. 19.    황윤태     최초 생성
 *
 * </pre>
 */

@Component("progrmAuthorBinding")
public class ProgrmAuthorBindingInterceptor extends ControllerInterceptorAdapter {

	protected Logger logger = LoggerFactory.getLogger(this.getClass());

	/** progrmAuthorService */
	@Resource(name="progrmAuthorService")
	private ProgrmAuthorService progrmAuthorService;

	@Resource(name="siteMngrService")
	private SiteMngrService siteMngrService;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		HttpSession session = request.getSession();

		Map<?, ?> pathVariables = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		String siteId = (String)pathVariables.get("siteId");
		@SuppressWarnings("unused")
		String returnUrl = "";
		//권한없을 경우 returnUrl

		//System.out.println("여기로 온다구?");
		if(siteId == null){
			returnUrl ="/common/neoAuthMsgBack.do";
		}else{
			String sKey = StringUtil.nvl(request.getParameter("key"));
			if(!"".equals(sKey)){
				Integer key = Integer.parseInt(sKey);
				returnUrl ="/"+siteId+"/autReject.do?key="+key;
			}else{
				returnUrl ="/"+siteId+"/autReject.do";
			}

		}

		LoginVO loginVO = (LoginVO)SessionUtil.getSessionValue(session, "loginVO");
		logger.debug(loginVO.toString());

		String userId = loginVO.getId();
		// 로그인 되어있지만 userId가 없다면 back
		if( StringUtil.isEmpty(userId) ) {
			logger.debug("UserId is Null!");
			if( ReturnType.LOGIN == getReturnType() ) {
				response.sendRedirect("/neo/login.do");
			} else {
				response.sendRedirect("/common/neoAuthMsg.do");
			}
			return false;
		}
		logger.debug("UserId : " + userId);

		if( "ADMIN".equals(loginVO.getUserSe()) ) {
			logger.debug("ROLE_ADMIN!");
			return true;
		}

		// 최고관리자만 접근 가능하다면
		if( AccesType.REJECT == getType() ) {
			if( !"ADMIN".equals(loginVO.getUserSe()) ) {
				logger.debug("Admin is Not!");
				response.sendRedirect("/common/neoAuthMsg.do");
				return false;
			}
		} else if( AccesType.SITE == getType() ) {
			List<SiteMngrVO> siteMngrSiteInfoList = siteMngrService.selectSiteMngrListByUserId(userId);
			// 사이트 관리자가 아니라면
			if( 0 == siteMngrSiteInfoList.size() ) {
				logger.debug("Site Mngr is Not!");
				response.sendRedirect("/common/neoAuthMsg.do");
				return false;
			} else {
				// 요청 사이트ID와 비교
				boolean siteMngrChk = false;
				if( StringUtil.isEmpty(siteId) ) {
					siteMngrChk = true;
				} else {
					for( int i=0; i<siteMngrSiteInfoList.size(); i++ ) {
						logger.debug(siteId + " : " + siteMngrSiteInfoList.get(i).getSiteId());
						if( siteId.equals(siteMngrSiteInfoList.get(i).getSiteId()) ) {
							siteMngrChk = true;
							break;
						}
					}
				}
				if( !siteMngrChk ) {
					logger.debug("SITE Mngr is Not!.");
					response.sendRedirect("/common/neoAuthMsg.do");
					return false;
				} else {
					request.setAttribute("siteMngrSiteInfoList", siteMngrSiteInfoList);
					return true;
				}
			}
		} else if( AccesType.PASS == getType() ) {

			boolean progrmMngrChk = false;

			if( getProgrm() != null ) {
				ProgrmAuthor progrmAuthor = new ProgrmAuthor();
				progrmAuthor.setProgrmNo(getProgrm());
				progrmAuthor.setUserId(userId);

				progrmAuthor = progrmAuthorService.selectProgrmMngr(progrmAuthor);
				if( progrmAuthor != null ) progrmMngrChk = true;
			}
			/*
			프로그램키를 못받아와서 주석처리
			if( !progrmMngrChk ){
				logger.debug("PROGRAM Mngr is Not!.");
				response.sendRedirect("/common/neoAuthMsg.do");
				return false;
			}
			*/
		}

		return true;

	}

	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView mav) throws Exception {

		// preHandle에서 등록한 model을 mav에 등록해준다.
		mav.getModelMap().addAllAttributes((ModelMap)request.getAttribute("mav"));

	}
}
