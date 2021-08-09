package kr.co.hanshinit.NeoCop.cop.resveAddField.service.impl;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCop.cop.cmm.util.StaffLoginUtil;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddField;
//import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldAtchmnflService;
//import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldAtchmnflVO;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldService;
import kr.co.hanshinit.NeoCop.cop.resveAddField.service.ResveAddFieldVO;


@Service("resveAddFieldService")
public class ResveAddFieldServiceImpl extends EgovAbstractServiceImpl implements ResveAddFieldService {

	@Resource(name="resveAddFieldDAO")
	private ResveAddFieldDAO resveAddFieldDAO;

  /**
   * 통합예약 추가 정보 필드의 목록을 조회한다.
   * 
   * @param ResveAddFieldVO
   * @return List<ResveAddFieldVO> 통합예약 추가 정보 필드 목록
   * @throws Exception
   */
  public List<ResveAddField> selectResveAddFieldList(ResveAddFieldVO resveAddFieldVO)
      throws Exception {

    List<ResveAddField> list = resveAddFieldDAO.selectResveAddFieldList(resveAddFieldVO);

    // 데이터가공 구간
//    for (int i = 0; i < list.size(); i++) {
//      // @@{listLoop}
//    }
    return list;
  }

  /**
   * 통합예약 추가 정보 필드의 레코드 수를 조회 한다.
   * 
   * @param ResveAddFieldVO
   * @return int 통합예약 추가 정보 필드 레코드 수
   * @throws Exception
   */
  public int selectResveAddFieldTotCnt(ResveAddFieldVO resveAddFieldVO) throws Exception {
    return (Integer) resveAddFieldDAO.selectResveAddFieldTotCnt(resveAddFieldVO);
  }

  /**
   * 통합예약 추가 정보 필드의 상세정보를 조회한다.
   * 
   * @param ResveAddFieldVO
   * @return ResveAddField 통합예약 추가 정보 필드 의 상세정보
   * @throws Exception
   */
  public ResveAddField selectResveAddFieldData(
    HttpServletRequest request,
    ResveAddFieldVO resveAddFieldVO
  ) throws Exception {
    ResveAddField data =
        (ResveAddFieldVO) resveAddFieldDAO.selectResveAddFieldData(resveAddFieldVO);
    return data;
  }

  /**
   * 통합예약 추가 정보 필드 을(를) 등록한다.
   * 
   * @param ResveAddFieldVO
   * @return int 통합예약 추가 정보 필드의 시퀀스
   * @throws Exception
   */
  public int insertResveAddField(HttpServletRequest request, ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    resveAddFieldVO.setFrstRegisterPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    resveAddFieldVO.setFrstRegisterId(StaffLoginUtil.getLoginId(request.getSession()));
    resveAddFieldVO.setFrstRegisterIp(StringUtil.getClientIpAddr(request));
    resveAddFieldVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    resveAddFieldVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
    resveAddFieldVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

    return (Integer) resveAddFieldDAO.insertResveAddField(resveAddFieldVO);
  }

  /**
   * 통합예약 추가 정보 필드 을(를) 수정한다.
   * 
   * @param ResveAddFieldVO
   * @return int 통합예약 추가 정보 필드의 시퀀스
   * @throws Exception
   */
  public int updateResveAddField(HttpServletRequest request, ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    resveAddFieldVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    resveAddFieldVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
    resveAddFieldVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

    return (Integer) resveAddFieldDAO.updateResveAddField(resveAddFieldVO);
  }


  public void deleteResveAddField(HttpServletRequest request, ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    resveAddFieldVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    resveAddFieldVO.setLastUpdusrId(StaffLoginUtil.getLoginId(request.getSession()));
    resveAddFieldVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));
    resveAddFieldVO.setDeleteAt("Y");

    resveAddFieldDAO.deleteResveAddField(resveAddFieldVO);
  }

  /**
   * 통합예약 추가 정보 필드 을(를) 전체 조회한다.
   * 
   * @param ResveAddFieldVO
   * @return ResveAddField 통합예약 추가 정보 필드 의 전체결과
   * @throws Exception
   */
  public List<ResveAddField> selectResveAddFieldListAll(ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    List<ResveAddField> list = resveAddFieldDAO.selectResveAddFieldListAll(resveAddFieldVO);
    return list;
  }

	/**
	 * 프로그램 신청양식 추가 등록 HTML 파일 생성
	 */
	public String makeAddFieldHtml(ResveAddFieldVO resveAddFieldVO) throws Exception{
		String rtnStr = "";

		// 정보 조회
		List<ResveAddField> addFieldList = selectResveAddFieldListAll(resveAddFieldVO);

		int CNT = 0;
		for(ResveAddField forVo : addFieldList){
			rtnStr += "	<tr id='trId"+CNT+"'> ";
			rtnStr += "		<th></th>";
			rtnStr += "			<td colspan='3'>";
			rtnStr += "				<input type='text' id='bndlFieldNm"+CNT+"' name='bndlFieldNm' class='p-input p-input--auto' placeholder='필드명입력'/>";
			rtnStr += "				<input type='hidden' id='bndlSeq"+CNT+"' name='bndlSeq'/>";
			rtnStr += "				<input type='hidden' id='bndlManagerSeq"+CNT+"' name='bndlManagerSeq'/>";
			rtnStr += "				<select id='bndlFieldTy"+CNT+"' name='bndlFieldTy' class='p-input p-input--auto'>";
			rtnStr += "					<option value='text'>일반입력정보</option>";
			rtnStr += "					<option value='textarea'>입력내용 많은 경우</option>";
			rtnStr += "					<option value='calendar'>날짜입력정보</option>";
			rtnStr += "					<option value='select'>셀렉트박스</option>";
			rtnStr += "					<option value='radio'>라디오버튼</option>";
			rtnStr += "					<option value='checkbox'>체크버튼</option>";
			rtnStr += "				</select>";
			rtnStr += "				<input type='text' id='bndlFieldOption"+CNT+"' name='bndlFieldOption' class='p-input'  style='width: 300px;' placeholder='필드옵션'/>";
			rtnStr += "				<span class='p-form-checkbox'>";
			rtnStr += "					<input type='checkbox' name='essntlAtCheck' id='essntlAtCheck"+CNT+"' class='p-form-checkbox__input' value='Y' />";
			rtnStr += "					<label for='essntlAtCheck"+CNT+"' class='p-form-checkbox__label'>필수입력 여부</label>";
			rtnStr += "				</span>";
			rtnStr +="				<a href=\"javascript:fn_DeleteDBTr('"+CNT+"');\" class='p-button p-button--xsmall delete'>삭제</a>";
			rtnStr += "		</td>";
			rtnStr += "	</tr>";

			CNT++;
		}

		return rtnStr;
	}

	/**
	 * 추가 입력정보 html, script 생성
	 */
	public HashMap<String, String> makeAddFieldHtml(List<ResveAddField> list) throws Exception{
		HashMap<String, String> rtnMap = new HashMap<String, String>();
		String rtnStr = "";
		String rtnScriptStr = "function fn_AddFileCheck(){";
		String requiredYn = "";

		int cnt = 1;
		for(ResveAddField forVo : list){
			requiredYn="";

			// 필수값 체크 자바 스크립트 생성
			if("Y".equals(forVo.getEssntlAt())){
				requiredYn = "required='required'";
				if("text".equals(forVo.getFieldTy()) || "textarea".equals(forVo.getFieldTy()) || "calendar".equals(forVo.getFieldTy())  || "select".equals(forVo.getFieldTy())){
					rtnScriptStr += "";
					rtnScriptStr += "if($.trim($(\"#inputCn"+forVo.getItemSeq()+"\").val()) == \"\") { ";
					rtnScriptStr += "	alert(\""+forVo.getFieldNm()+"을 입력하지 않았습니다.\"); ";
					rtnScriptStr += "	$(\"#inputCn"+forVo.getItemSeq()+"\").val($.trim($(\"#inputCn"+cnt+"\").val())); ";
					rtnScriptStr += "    $(\"#inputCn"+forVo.getItemSeq()+"\").focus(); ";
					rtnScriptStr += "    return false; ";
					rtnScriptStr += "}";
				}else if("checkbox".equals(forVo.getFieldTy())){
					rtnScriptStr += "";
					rtnScriptStr += "if(!$(\"input:checkbox[name='inputCn"+forVo.getItemSeq()+"']\").is(\":checked\")){ ";
					rtnScriptStr += "	alert(\""+forVo.getFieldNm()+"을 선택하지 않았습니다.\"); ";
					rtnScriptStr += "    $(\"input[name='inputCn"+forVo.getItemSeq()+"']\").focus(); ";
					rtnScriptStr += "    return false; ";
					rtnScriptStr += "}";
				}else if("radio".equals(forVo.getFieldTy())){
					rtnScriptStr += "";
					rtnScriptStr += "if(!$(\"input:radio[name='inputCn"+forVo.getItemSeq()+"']\").is(\":checked\")){ ";
					rtnScriptStr += "	alert(\""+forVo.getFieldNm()+"을 선택하지 않았습니다.\"); ";
					rtnScriptStr += "    $(\"input[name='inputCn"+forVo.getItemSeq()+"']\").focus(); ";
					rtnScriptStr += "    return false; ";
					rtnScriptStr += "}";
				}
			}else{
				requiredYn = "";
			}

			// 폼 HTML 생성
			rtnStr += "<tr>";
			rtnStr += "		<th>";
			rtnStr += "			<label for=\"inputCn"+forVo.getItemSeq()+"\" class=\"p-form__label\">"+forVo.getFieldNm()+"</label>";
			if("Y".equals(forVo.getEssntlAt())){
					rtnStr += "			<span class=\"p-form__required--icon margin_l_5\"></span>";
			}
			rtnStr +="			<input type=\"hidden\" id=\"itemSeq"+forVo.getItemSeq()+"\" name=\"itemSeq"+forVo.getItemSeq()+"\" value=\""+forVo.getItemSeq()+"\" />  ";
			rtnStr += "		</th>";
			rtnStr += "		<td colspan='3'>";

			if("text".equals(forVo.getFieldTy())){
				rtnStr += "	<input type=\"text\" name=\"inputCn"+forVo.getItemSeq()+"\" id=\"inputCn"+forVo.getItemSeq()+"\" value=\""+StringUtil.strTrim(forVo.getInputCn())+"\"  class=\"p-input p-input--auto\" "+requiredYn+">";

			}else if("textarea".equals(forVo.getFieldTy())){
				rtnStr += "	<textarea name=\"inputCn"+forVo.getItemSeq()+"\" id=\"inputCn"+forVo.getItemSeq()+"\" class=\"p-input\" cols=\"70\" rows=\"5\" "+requiredYn+">"+StringUtil.strTrim(forVo.getInputCn())+"</textarea>";

			}else if("calendar".equals(forVo.getFieldTy())){
				rtnStr += "	<div class=\"p-form-group p-date\" data-date=\"datepicker\">	";
				rtnStr += "	<input type=\"text\" name=\"inputCn"+forVo.getItemSeq()+"\" id=\"inputCn"+forVo.getItemSeq()+"\" class=\"p-input\" value=\""+StringUtil.strTrim(forVo.getInputCn())+"\" placeholder=\"yyyy-mm-dd\" "+requiredYn+"  dateAutoInput=\"true\"> ";
				rtnStr += "		<div class=\"p-input__split\"> ";
				rtnStr += "			<div class=\"p-datepicker__wrap\"><button type=\"button\" class=\"p-input__item  p-date__icon\">달력 열기</button></div> ";
				rtnStr += "		</div> ";
				rtnStr += "	</div> ";

			}else if("checkbox".equals(forVo.getFieldTy())){
				String[] optionStr = forVo.getFieldOption().split("\\,");

				for(int i=0;i<optionStr.length;i++){
					String checkBoxChecked = "";

					if(!"".equals(StringUtil.strTrim(forVo.getInputCn()))){
						if(StringUtil.strTrim(forVo.getInputCn()).contains(StringUtil.strTrim(optionStr[i]))){
							checkBoxChecked = "checked=\"checked\"";
						}
					}

					rtnStr += "	<span class=\"p-form-checkbox\"> ";
					rtnStr += "		<input type=\"checkbox\" name=\"inputCn"+forVo.getItemSeq()+"\" id=\"bndlInputCnC"+cnt+"\" class=\"p-form-checkbox__input\" value=\""+StringUtil.strTrim(optionStr[i])+"\" "+checkBoxChecked+"> ";
					rtnStr += "		<label for=\"bndlInputCnC"+cnt+"\" class=\"p-form-checkbox__label\">"+optionStr[i]+"</label> ";
					rtnStr += "	</span> ";

					cnt++;
				}
			}else if("radio".equals(forVo.getFieldTy())){
				String[] optionStr = forVo.getFieldOption().split("\\,");

				for(int i=0;i<optionStr.length;i++){
					String radioChecked = "";

					if(!"".equals(StringUtil.strTrim(forVo.getInputCn()))){
						if(forVo.getInputCn().contains(optionStr[i])){
							radioChecked = "checked=\"checked\"";
						}
					}
					rtnStr += "	<span class=\"p-form-radio\"> ";
					rtnStr += "		<input type=\"radio\" name=\"inputCn"+forVo.getItemSeq()+"\" id=\"bndlInputCnR"+cnt+"\" class=\"p-form-radio__input\"  value=\""+StringUtil.strTrim(optionStr[i])+"\" "+radioChecked+"> ";
					rtnStr += "		<label for=\"bndlInputCnR"+cnt+"\" class=\"p-form-radio__label margin_r_10\">"+optionStr[i]+"</label> ";
					rtnStr += "	</span> ";

					cnt++;
				}

			}else if("select".equals(forVo.getFieldTy())){
				String[] optionStr = forVo.getFieldOption().split("\\,");

				rtnStr += "	<select name=\"inputCn"+forVo.getItemSeq()+"\" id=\"inputCn"+forVo.getItemSeq()+"\" class=\"p-input p-input--auto\" title=\"\"> ";
				for(int i=0;i<optionStr.length;i++){
					String selectedCheck = "";
					if(!"".equals(StringUtil.strTrim(forVo.getInputCn()))){
						if(forVo.getInputCn().contains(optionStr[i])){
							selectedCheck = "selected=\"selected\"";
						}
					}

					rtnStr += "	<option value=\""+StringUtil.strTrim(optionStr[i])+"\" "+selectedCheck+" >"+optionStr[i]+"</option> ";
				}
				rtnStr += "	</select> ";
			}

			rtnStr += "		</td>";
			rtnStr += "		</td>";
			rtnStr += "</tr>";

			cnt++;
		}

		rtnScriptStr += "return true;";
		rtnScriptStr += "}";

		rtnMap.put("addFieldHtml", rtnStr);
		rtnMap.put("addFieldScript", rtnScriptStr);

		return rtnMap;
	}

  /**
   * 추가 입력정보 html, script 생성
   */
  public String makeAddFieldHtmlStr(List<ResveAddField> list) throws Exception {
    StringBuilder sb = new StringBuilder();
    String rtnStr = "";

		//int cnt = 1;
		for(ResveAddField forVo : list){
			// 폼 HTML 생성
			sb.append( "<tr>" );
			sb.append( "		<th>" );
			sb.append( "			<label for=\"inputCn"+forVo.getItemSeq()+"\" class=\"p-form__label\">"+forVo.getFieldNm()+"</label>" );
			sb.append( "			<input type=\"hidden\" id=\"itemSeq"+forVo.getItemSeq()+"\" name=\"itemSeq"+forVo.getItemSeq()+"\" value=\""+forVo.getItemSeq()+"\" />  " );
			sb.append( "		</th>" );
			sb.append( "		<td colspan='3'>" );

			if("textarea".equals(forVo.getFieldTy())){
			  sb.append( StringUtil.newLineReplace(forVo.getInputCn()) );
			}else{
			  sb.append( forVo.getInputCn() );
			}

			sb.append( "		</td>" );
			sb.append( "</tr>" );

      // cnt++;
    }

    return rtnStr;
  }


  /**
   * 추가 입력정보 저장
   */
  public void insertResveInputAddField(HttpServletRequest request, ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    resveAddFieldVO.setFrstRegisterPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    resveAddFieldVO.setFrstRegisterIp(StringUtil.getClientIpAddr(request));
    resveAddFieldVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    resveAddFieldVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

    resveAddFieldDAO.insertResveInputAddField(resveAddFieldVO);
  }

  /**
   * 추가 입력정보 업데이트
   */
  public int updateResveInputAddField(HttpServletRequest request, ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    resveAddFieldVO.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    resveAddFieldVO.setLastUpdusrIp(StringUtil.getClientIpAddr(request));

    return resveAddFieldDAO.updateResveInputAddField(resveAddFieldVO);
  }

  /**
   * 추가 입력 정보 조회
   */
  public List<ResveAddField> selectResveInputAddFieldData(ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    List<ResveAddField> list = resveAddFieldDAO.selectResveInputAddFieldData(resveAddFieldVO);

    return list;
  }

  /**
   * 추가 입력 정보 조회
   */
  public List<ResveAddField> selectResveInputList(ResveAddFieldVO resveAddFieldVO)
      throws Exception {
    List<ResveAddField> list = resveAddFieldDAO.selectResveInputList(resveAddFieldVO);

    return list;
  }

  /**
   * 추가 입력 정보 삭제
   */
  public void deleteResveInputAddField(ResveAddFieldVO resveAddFieldVO) throws Exception {
    resveAddFieldDAO.deleteResveInputAddField(resveAddFieldVO);
  }

  /**
   * 사용자 추가 입력정보 조회 및 HTML 생성
   */
  @Override
  public HashMap<String, String> makeInputAddFieldHtml(List<ResveAddField> list) throws Exception {

    return null;
  }


}


