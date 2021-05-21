package kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import kr.co.hanshinit.NeoCMS.cmm.service.CmmUseService;
import kr.co.hanshinit.NeoCMS.cmm.stereotype.Interceptor;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteItm.service.MvoteQestn;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteItm.service.MvoteQestnarItmService;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.service.MvoteRespond;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.service.MvoteRspnsService;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.service.MvoteSbjctRspns;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.service.MvoteQestnar;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.service.MvoteQestnarService;
import kr.co.hanshinit.NeoCMS.sym.cma.cdc.service.CmmnDetailCode;
import kr.co.hanshinit.NeoCMS.sym.cma.cdc.service.CmmnDetailCodeService;


@Controller
@Interceptor("staffAuthorInterceptor")
public class MvoteRespnsController {

  Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource(name = "mvoteQestnarService")
  private MvoteQestnarService mvoteQestnarService;

  /**
   * cmmUseService
   */
  @Resource(name = "cmmUseService")
  private CmmUseService cmmUseService;
  @Resource(name = "mvoteQestnarItmService")
  private MvoteQestnarItmService mvoteQestnarItmService;
  @Resource(name = "mvoteRspnsService")
  private MvoteRspnsService mvoteRspnsService;

  /**
   * cmmnDetailCodeService
   */
  @Resource(name = "cmmnDetailCodeService")
  private CmmnDetailCodeService cmmnDetailCodeService;

  @Interceptor(value = {"templateBinding"})
  @RequestMapping(value = "/{siteId}/selectMovteQestnarMngResult.do")
  public String selectMovteQestnarResult(@PathVariable("siteId") String siteId,
      @RequestParam("key") int key, MvoteQestnar mvoteQestnar, ModelMap model) throws Exception {


    MvoteQestnar getVO = mvoteQestnarService.selectMvoteQestnar(mvoteQestnar);
    // 비공개 설정시 공개 안함
    getVO.setMvoteDc(StringUtil.html2text(getVO.getMvoteDc()));

    // 설문조사 정보.
    model.addAttribute("mvoteQestnar", getVO);

    // 질문리스트
    logger.debug("\n resultList >>>\n\n");
    model.addAttribute("resultList",
        mvoteQestnarItmService.selectMvoteQestnList(mvoteQestnar.getMvoteNo()));
    logger.debug("\n <<< resultList \n\n");

    // 객관식 답변수 Map
    model.addAttribute("answerMap",
        mvoteRspnsService.selectObjctAnswerCntList(mvoteQestnar.getMvoteNo()));

    // 객관식응답 Map
    model.addAttribute("objctMap",
        mvoteRspnsService.selectObjctRspnsList(mvoteQestnar.getMvoteNo()));

    // 주관식응답 Map
    model.addAttribute("sbjctMap",
        mvoteRspnsService.selectSbjctRspnsList(mvoteQestnar.getMvoteNo()));


    return "NeoCMS/cop/mvote/qestnar/result/mvoteQestnarResultMngView";
  }

  /**
   * 모바일 투표 설문 참여자 통계 페이지
   */
  @Interceptor(value = {"templateBinding"})
  @RequestMapping(value = "/{siteId}/selectMovteQestnarMngTotResult.do")
  public String selectMovteQestnarMngTotResult(
    @PathVariable("siteId") String siteId
    , @RequestParam("key") int key
    , MvoteQestnar mvoteQestnar
    , ModelMap model
  ) throws Exception {

    // 공통코드 : 타지역
    List<CmmnDetailCode> areaList = cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("AREA");
    model.addAttribute("areaList", areaList);

    // 공통코드 : 관내 상세지역
    List<CmmnDetailCode> dtAreaList =
        cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("DTAREA");
    model.addAttribute("dtAreaList", dtAreaList);

    MvoteQestnar getVO = mvoteQestnarService.selectMvoteQestnar(mvoteQestnar);
    // 비공개 설정시 공개 안함
    getVO.setMvoteDc(StringUtil.html2text(getVO.getMvoteDc()));

    // 설문조사 정보.
    model.addAttribute("mvoteQestnar", getVO);

    // 질문리스트
    model.addAttribute("resultList",
        mvoteQestnarItmService.selectMvoteQestnList(mvoteQestnar.getMvoteNo()));

    // 주관식응답 Map
    model.addAttribute("totMap", mvoteRspnsService.selectRspnsTotList(mvoteQestnar.getMvoteNo()));

    return "NeoCMS/cop/mvote/qestnar/result/mvoteQestnarResultMngTot";
  }

  /**
   * 모바일 투표 설문 결과 통계 페이지
   */
  @Interceptor(value = {"templateBinding"})
  @RequestMapping(value = "/{siteId}/selectMovteQestnarMngResult2.do")
  public String selectMovteQestnarResult2(
    @PathVariable("siteId") String siteId
    , @RequestParam("key") int key
    , MvoteQestnar mvoteQestnar
    , MvoteRespond mvoteRespond
    , ModelMap model
  ) throws Exception {

    // 공통코드 : 패널코드
    List<CmmnDetailCode> codeList =
        cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("PNLGRP");
    model.addAttribute("codeList", codeList);

    // 공통코드 : 타지역
    List<CmmnDetailCode> areaList = cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("AREA");
    model.addAttribute("areaList", areaList);

    // 공통코드 : 관내 상세지역
    List<CmmnDetailCode> dtAreaList =
        cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("DTAREA");
    model.addAttribute("dtAreaList", dtAreaList);

    MvoteQestnar getVO = mvoteQestnarService.selectMvoteQestnar(mvoteQestnar);
    // 비공개 설정시 공개 안함
    getVO.setMvoteDc(StringUtil.html2text(getVO.getMvoteDc()));

    mvoteRespond.setMvoteNo(getVO.getMvoteNo());
    // 설문조사 정보.
    model.addAttribute("mvoteQestnar", getVO);

    // 질문리스트
    List<MvoteQestn> resultList =
        mvoteQestnarItmService.selectMvoteQestnList(mvoteQestnar.getMvoteNo());
    model.addAttribute("resultList", resultList);

    // 주관식응답 Map returnResult.put("allSexAge", totalresult); returnResult.put("pnlgrp", tempresult);
    // returnResult.put("age", tempresult2);
    Map<String, Map<String, List<MvoteRespond>>> returnMap =
        mvoteRspnsService.selectObjctRspnsTotList(mvoteRespond);

    model.addAttribute("totMap", returnMap.get("allSexAge"));
    model.addAttribute("pnlgrpMap", returnMap.get("pnlgrp"));
    model.addAttribute("noPnlgrpMap", returnMap.get("noPnlgrp"));
    model.addAttribute("dtAreaMap", returnMap.get("dtArea"));
    model.addAttribute("areaMap", returnMap.get("area"));

    return "NeoCMS/cop/mvote/qestnar/result/mvoteQestnarResultMngView2";
  }

  @RequestMapping(value = "/{siteId}/movteQestnarPopup.do")
  public String movteQestnarPopup(
    @PathVariable("siteId") String siteId
    , @RequestParam("key") int key
    , @RequestParam("mvoteNo") int mvoteNo
    , ModelMap model
  ) throws Exception {
    model.addAttribute("key", key);
    model.addAttribute("mvoteNo", mvoteNo);
    return "NeoCMS/cop/mvote/qestnar/result/mvoteQestnarPopupRedirect";
  }

  /**
   * 모바일 투표 설문 결과 통계 pdf 다운
   */
  @RequestMapping(value = "/{siteId}/selectMovteQestnarResultsAsPdf.do")
  public String selectMovteQestnarResultsAsPdf(
    @PathVariable("siteId") String siteId
    , @RequestParam("key") int key
    , MvoteQestnar mvoteQestnar
    , MvoteRespond mvoteRespond
    , ModelMap model
  ) throws Exception {

    // 설문조사 정보.
    MvoteQestnar questn = mvoteQestnarService.selectMvoteQestnar(mvoteQestnar);
    model.addAttribute("questn", questn);
    //logger.debug("\n\tquestn : {}", ToStringBuilder.reflectionToString(questn, ToStringStyle.MULTI_LINE_STYLE));

    // 공통코드 : 패널코드
    List<CmmnDetailCode> codeList =
        cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("PNLGRP");
    model.addAttribute("codeList", codeList);

    // 공통코드 : 타지역
    List<CmmnDetailCode> areaList = cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("AREA");
    model.addAttribute("areaList", areaList);

    // 공통코드 : 관내 상세지역
    List<CmmnDetailCode> dtAreaList =
        cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("DTAREA");
    model.addAttribute("dtAreaList", dtAreaList);

    //  통계 맵
    model.addAttribute("STAT_MAP", mvoteRspnsService.selectRspnsTotList(mvoteQestnar.getMvoteNo()));

    // 질문리스트
    List<MvoteQestn> qestnList =
        mvoteQestnarItmService.selectMvoteQestnList(mvoteQestnar.getMvoteNo());
    model.addAttribute("qestnList", qestnList);

    // 객관식 답변수 Map
    model.addAttribute("answerMap",
        mvoteRspnsService.selectObjctAnswerCntList(mvoteQestnar.getMvoteNo()));

    // 객관식응답 Map
    model.addAttribute("objctMap",
        mvoteRspnsService.selectObjctRspnsList(mvoteQestnar.getMvoteNo()));

    // 주관식응답 Map
    model.addAttribute("sbjctMap",
        mvoteRspnsService.selectSbjctRspnsList(mvoteQestnar.getMvoteNo()));



    mvoteRespond.setMvoteNo(mvoteQestnar.getMvoteNo());
    // 질문별 객관식 map
    Map<String, Map<String, Map<String, List<MvoteRespond>>>> allObjMap =
        new HashMap<String, Map<String, Map<String, List<MvoteRespond>>>>();

    // 질문별 주관식 map
    Map<String, List<MvoteSbjctRspns>> allSbjMap = new HashMap<String, List<MvoteSbjctRspns>>();

    for (int i = 0; i < qestnList.size(); i++) {
      mvoteRespond.setMvoteQestnNo(qestnList.get(i).getMvoteQestnNo());

      if ("QESTY02".equals(qestnList.get(i).getQestnTy())) {
        Map<String, Map<String, List<MvoteRespond>>> returnMap =
            mvoteRspnsService.selectObjctRspnsTotList(mvoteRespond);
        allObjMap.put("mvoteQestnNo_" + qestnList.get(i).getMvoteQestnNo(), returnMap);
      } else if ("QESTY01".equals(qestnList.get(i).getQestnTy())) {
        List<MvoteSbjctRspns> sbjList = mvoteRspnsService.selectSbjctRspnsDtList(mvoteRespond);
        allSbjMap.put("mvoteQestnNo_" + qestnList.get(i).getMvoteQestnNo(), sbjList);
      }
    }

    model.addAttribute("allSbjMap", allSbjMap);
    model.addAttribute("allObjMap", allObjMap);

    //model.addAttribute("rspnsUserList", mvoteRspnsService.selectRspnsUserList(mvoteRespond));

    return "NeoCMS/cop/mvote/qestnar/result/mvoteQestnarResultAsPdf";
  }


  @Interceptor(value = {"templateBinding"})
  @RequestMapping(value = "/{siteId}/selectMvoteQestnarSbjctResult.do")
  public String selectMvoteQestnarSbjctResult(@PathVariable("siteId") String siteId,
      @RequestParam("key") int key, MvoteQestnar mvoteQestnar, MvoteRespond mvoteRespond,
      ModelMap model) throws Exception {


    MvoteQestnar getVO = mvoteQestnarService.selectMvoteQestnar(mvoteQestnar);
    // 비공개 설정시 공개 안함
    getVO.setMvoteDc(StringUtil.html2text(getVO.getMvoteDc()));

    // 설문조사 정보.
    model.addAttribute("mvoteQestnar", getVO);

    model.addAttribute("mvoteRespond", mvoteRespond);

    // 질문리스트
    model.addAttribute("resultList",
        mvoteQestnarItmService.selectMvoteQestnList(mvoteQestnar.getMvoteNo()));


    // 주관식응답 Map
    model.addAttribute("sbjctList", mvoteRspnsService.selectSbjctRspnsDtList(mvoteRespond));


    return "NeoCMS/cop/mvote/qestnar/result/mvoteQestnarSbjctResult";
  }

  @Interceptor(value = {"templateBinding"})
  @RequestMapping(value = "/{siteId}/selectMvoteQestnarResultExcel.do")
  public String selectMvoteQestnarResultExcel(@PathVariable("siteId") String siteId,
      @RequestParam("key") int key, MvoteQestnar mvoteQestnar, ModelMap model) throws Exception {

    // 공통코드 : 패널코드
    List<CmmnDetailCode> codeList =
        cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("PNLGRP");
    model.addAttribute("codeList", codeList);

    // 공통코드 : 타지역
    List<CmmnDetailCode> areaList = cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("AREA");
    model.addAttribute("areaList", areaList);

    // 공통코드 : 관내 상세지역
    List<CmmnDetailCode> dtAreaList =
        cmmnDetailCodeService.selectCmmnDetailCodeLIstByCodeId("DTAREA");
    model.addAttribute("dtAreaList", dtAreaList);

    model.addAttribute("totMap", mvoteRspnsService.selectRspnsTotList(mvoteQestnar.getMvoteNo()));

    // 질문리스트
    List<MvoteQestn> resultList =
        mvoteQestnarItmService.selectMvoteQestnList(mvoteQestnar.getMvoteNo());
    model.addAttribute("resultList", resultList);

    // 객관식 답변수 Map
    model.addAttribute("answerMap",
        mvoteRspnsService.selectObjctAnswerCntList(mvoteQestnar.getMvoteNo()));

    // 객관식응답 Map
    model.addAttribute("objctMap",
        mvoteRspnsService.selectObjctRspnsList(mvoteQestnar.getMvoteNo()));

    // 주관식응답 Map
    model.addAttribute("sbjctMap",
        mvoteRspnsService.selectSbjctRspnsList(mvoteQestnar.getMvoteNo()));

    MvoteRespond mvoteRespond = new MvoteRespond();

    mvoteRespond.setMvoteNo(mvoteQestnar.getMvoteNo());


    // 질문별 객관식 map
    Map<String, Map<String, Map<String, List<MvoteRespond>>>> allObjMap =
        new HashMap<String, Map<String, Map<String, List<MvoteRespond>>>>();

    // 질문별 주관식 map
    Map<String, List<MvoteSbjctRspns>> allSbjMap = new HashMap<String, List<MvoteSbjctRspns>>();

    for (int i = 0; i < resultList.size(); i++) {
      mvoteRespond.setMvoteQestnNo(resultList.get(i).getMvoteQestnNo());

      if ("QESTY02".equals(resultList.get(i).getQestnTy())) {
        Map<String, Map<String, List<MvoteRespond>>> returnMap =
            mvoteRspnsService.selectObjctRspnsTotList(mvoteRespond);
        allObjMap.put("mvoteQestnNo_" + resultList.get(i).getMvoteQestnNo(), returnMap);
      } else if ("QESTY01".equals(resultList.get(i).getQestnTy())) {
        List<MvoteSbjctRspns> sbjList = mvoteRspnsService.selectSbjctRspnsDtList(mvoteRespond);
        allSbjMap.put("mvoteQestnNo_" + resultList.get(i).getMvoteQestnNo(), sbjList);
      }


    }

    model.addAttribute("allSbjMap", allSbjMap);
    model.addAttribute("allObjMap", allObjMap);

    model.addAttribute("rspnsUserList", mvoteRspnsService.selectRspnsUserList(mvoteRespond));

    // 주관식응답 Map List<MvoteSbjctRspns> mvoteRspnsService.selectSbjctRspnsDtList(mvoteRespond)


    /*
     * model.addAttribute("totMap", returnMap.get("allSexAge")); model.addAttribute("pnlgrpMap",
     * returnMap.get("pnlgrp")); model.addAttribute("noPnlgrpMap", returnMap.get("noPnlgrp"));
     * model.addAttribute("dtAreaMap", returnMap.get("dtArea")); model.addAttribute("areaMap",
     * returnMap.get("area"));
     */


    // return "NeoCMS/cop/qsm/qestnarResultExcel";
    return "NeoCMS/cop/mvote/qestnar/result/mvoteQestnarResultExcelNew";
  }


}
