package kr.co.hanshinit.NeoCMS.cop.gosi.service.impl;

import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.Resource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;
import kr.co.hanshinit.NeoCMS.cmm.util.ProfileUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.cop.gosi.service.GosiScheduleService;
import kr.co.hanshinit.NeoCMS.cop.gosi.service.GosiVO;
import kr.co.hanshinit.NeoCMS.cop.gosi.web.SoapClient;

/**
 * 굳이 공통적으로 사용하는걸 이렇게 반복해서 만든다고?
 * OOP 신경 안쓸거면 java 말고 C를 하지 왜..?
 *
 * 아무리 공통적이고 메소드 이름으로 뭐하는지 안다지만,
 * 그래도 주석 달아주는 성의는 있어야 하는거 아니냐?
 * 중복 코드 이따위로 짜는 인간은 뭐야...
 * 메모리 관리는 왜 안하고....
 */

@Service("gosiScheduler")
public class GosiScheduler extends EgovAbstractServiceImpl implements GosiScheduleService {

  Logger logger = LoggerFactory.getLogger(this.getClass());
  
  private static String URL;
  {
    if( ((String) ProfileUtil.get("-Dserver.was.roll").toLowerCase() ).indexOf("was") < 0 ) {
      //  운영환경
      URL = "http://localhost:20/stmr/websvc/std/ws";
    } else {
      //  TEST 환경
      URL = "http://172.16.16.219:3100/stmr/websvc/std/ws";
    }
    //System.out.println(URL);
  }

  private static final String CHARSET = "utf-8";
  // 연계ID
  private static final String IF_ID = "SOINN00001";
  // 요청기관코드7자리
  private static final String SRC_ORG_CD = "4040000";
  // 응답기관코드7자리
  private static final String TGT_ORG_CD = "4040000";

  @Resource(name = "gosiDAO")
  private GosiDAO gosiDAO;


  /**
   * 고시공고 갱신 메소드
   */
  public boolean insertGosiAll() throws Exception {
    long strSec = System.currentTimeMillis();
    long wstSec = 0l;

    String not_ancmt_se_code_arr[] = new String[3];

    logger.error("\t:: STEP 1 ::");

    not_ancmt_se_code_arr[0] = "01";
    not_ancmt_se_code_arr[1] = "03";
    not_ancmt_se_code_arr[2] = "04";

    if (checkTimeOut(15, not_ancmt_se_code_arr)) {
      deleteExpiredGosi(not_ancmt_se_code_arr);
      refreshGosi(not_ancmt_se_code_arr);
    } else {
      wstSec = (( System.currentTimeMillis() ) - strSec) / 1000;
      logger.error("\t :: STEP 1 FAIL -> waste time : {} Sec ::\n\n",  wstSec);
    }

    logger.error("\t :: STEP 2 ::");

    not_ancmt_se_code_arr[0] = "02";
    not_ancmt_se_code_arr[1] = "02";
    not_ancmt_se_code_arr[2] = "02";

    if (checkTimeOut(15, not_ancmt_se_code_arr)) {
      deleteExpiredGosi(not_ancmt_se_code_arr);
      refreshGosi(not_ancmt_se_code_arr);
    } else {
      wstSec = (( System.currentTimeMillis() ) - strSec) / 1000;
      logger.error("\t :: STEP 2 FAIL -> waste time : {} Sec ::\n\n",  wstSec);
    }

    logger.error("\t :: STEP 3 ::");

    not_ancmt_se_code_arr[0] = "05";
    not_ancmt_se_code_arr[1] = "05";
    not_ancmt_se_code_arr[2] = "05";
    if (checkTimeOut(15, not_ancmt_se_code_arr)) {
      deleteExpiredGosi(not_ancmt_se_code_arr);
      refreshGosi(not_ancmt_se_code_arr);
    } else {
      wstSec = (( System.currentTimeMillis() ) - strSec) / 1000;
      logger.error("\t :: STEP 3 FAIL -> waste time : {} Sec ::\n\n",  wstSec);
    }

    wstSec = (( System.currentTimeMillis() ) - strSec) / 1000;
    logger.error("\t :: insertGosiAll -> waste time : {} Sec ::\n\nEND.\n\n\n",  wstSec);

    return true;
  }

  public int selectGosiListTotCnt(String not_ancmt_se_code_arr[]) throws Exception {

    StringBuilder msg = new StringBuilder();

    msg.append("<message>");
    msg.append("<body>");

    //query_id는 각마다 다름
    msg.append("<query_id>4040000SOI126</query_id>");

    //게재된 게시물만 표시
    msg.append( "<dataList><data>" + not_ancmt_se_code_arr[0] + "</data></dataList>" );
    msg.append( "<dataList><data>" + not_ancmt_se_code_arr[1] + "</data></dataList>" );
    msg.append( "<dataList><data>" + not_ancmt_se_code_arr[2] + "</data></dataList>" );
    msg.append( "<dataList><data></data></dataList>  <dataList><data></data></dataList>" );
    msg.append( "<dataList><data></data></dataList>  <dataList><data></data></dataList>" );

    msg.append("</body>");
    msg.append("</message>");

    String reqSoap = SoapClient.makeReqSoap(IF_ID, SRC_ORG_CD, TGT_ORG_CD, (SoapClient.getMsgKey()), msg.toString());
    String resSoap = SoapClient.sendHttpRequest(URL, reqSoap, CHARSET, IF_ID);

    DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
    Document doc = dBuilder.parse(new InputSource(new StringReader(resSoap)));

    NodeList nList = doc.getElementsByTagName("list");

    for (Node node = nList.item(0).getFirstChild(); node != null; node = node.getNextSibling()) {
      if (node.getNodeName().equals("tot_cnt")) {
        String totCount = node.getTextContent();
        int totCnt = 0;
        if (totCount != null && !totCount.equals("")) {
          totCnt = Integer.parseInt(totCount);
          logger.error("totCnt : {}", totCnt);
          return totCnt;
        }
      }
    }

    return 0;
  }

  public boolean refreshGosi(String not_ancmt_se_code_arr[]) throws Exception {

    int totCnt = selectGosiListTotCnt(not_ancmt_se_code_arr);
    GosiVO paramVO = new GosiVO();

    if (not_ancmt_se_code_arr[0].equals(not_ancmt_se_code_arr[1]))
      paramVO.setNot_ancmt_se_code(not_ancmt_se_code_arr[0]);
    else
      paramVO.setNot_ancmt_se_code(not_ancmt_se_code_arr[0] + "," + not_ancmt_se_code_arr[1] + ","
          + not_ancmt_se_code_arr[2]);


    StringBuilder msg = new StringBuilder();

    msg.append("<message>");
    msg.append("  <body>");

    //query_id는 각마다 다름
    msg.append("    <query_id>4040000SOI125</query_id>");

    //  게재된 게시물만 표시 >>
    msg.append( "    <dataList><data>" + not_ancmt_se_code_arr[0] + "</data></dataList>" );
    msg.append( "    <dataList><data>" + not_ancmt_se_code_arr[1] + "</data></dataList>" );
    msg.append( "    <dataList><data>" + not_ancmt_se_code_arr[2] + "</data></dataList>" );
    msg.append( "    <dataList><data></data></dataList><dataList><data></data></dataList>" );
    msg.append( "    <dataList><data></data></dataList><dataList><data></data></dataList>" );
    msg.append( "    <dataList><data>0</data></dataList>" );
    msg.append( "    <dataList><data>" + totCnt + "</data></dataList>" );
    //  << 게재된 게시물만 표시
    msg.append("  </body>");
    msg.append("</message>");

    String reqSoap = SoapClient.makeReqSoap(IF_ID, SRC_ORG_CD, TGT_ORG_CD, (SoapClient.getMsgKey()), msg.toString());
    String resSoap = SoapClient.sendHttpRequest(URL, reqSoap, CHARSET, IF_ID);
    logger.error("resSoap :\n{}\n\n", resSoap);


    DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();

    Document doc = dBuilder.parse(new InputSource(new StringReader(resSoap)));
    NodeList nList = doc.getElementsByTagName("list");

    for (int i = 0; i < nList.getLength(); i++) {
      GosiVO getVO = new GosiVO();

      if (not_ancmt_se_code_arr[0].equals(not_ancmt_se_code_arr[1]))
        getVO.setNot_ancmt_se_code(not_ancmt_se_code_arr[0]);
      else
        getVO.setNot_ancmt_se_code(not_ancmt_se_code_arr[0] + "," + not_ancmt_se_code_arr[1] + ","
            + not_ancmt_se_code_arr[2]);

      for (Node node = nList.item(i).getFirstChild(); node != null; node = node.getNextSibling()) {

        switch( node.getNodeName() ) {
          case "dep_nm"           : getVO.setDep_nm(node.getTextContent());           break;
          case "etc_pbs_yn"       : getVO.setEtc_pbs_yn(node.getTextContent());       break;
          case "not_ancmt_sj"     : getVO.setNot_ancmt_sj(node.getTextContent());     break;
          case "pbs_hop_ymd"      : getVO.setPbs_hop_ymd(node.getTextContent());      break;
          case "not_ancmt_se_nm"  : getVO.setNot_ancmt_se_nm(node.getTextContent());  break;
          case "upis_pbs_yn"      : getVO.setUpis_pbs_yn(node.getTextContent());      break;
          case "pbs_ymd"          : getVO.setPbs_ymd(node.getTextContent());          break;
          case "not_ancmt_mgt_no" : getVO.setNot_ancmt_mgt_no(node.getTextContent()); break;
          case "not_ancmt_reg_no" : getVO.setNot_ancmt_reg_no(node.getTextContent()); break;
          case "bbs_pbs_yn"       : getVO.setBbs_pbs_yn(node.getTextContent());       break;
          case "cgg_pbs_yn"       : getVO.setCgg_pbs_yn(node.getTextContent());       break;
          case "row_num"          : getVO.setRow_num(node.getTextContent());          break;
          case "sido_pbs_yn"      : getVO.setSido_pbs_yn(node.getTextContent());      break;
          case "nppe_pbs_yn"      : getVO.setNppe_pbs_yn(node.getTextContent());      break;
          case "homepage_pbs_yn"  : getVO.setHomepage_pbs_yn(node.getTextContent());  break;
          default : logger.error("node name : {}", node.getNodeName());
        }
      }
      insertGosiData(getVO);
      insertGosiFileList(getVO);
    }
    return true;
  }


  /**
   *  고시공고 단일 상세 내용 DB Insert.
   */
  public void insertGosiData(GosiVO getVO) throws Exception {

    StringBuilder msg = new StringBuilder();

    msg.append("<message>");
    msg.append("<body>");

    //query_id는 각마다 다름
    msg.append( "<query_id>4040000SOI127</query_id>" );
    msg.append( "<dataList><data>" + getVO.getNot_ancmt_mgt_no() + "</data></dataList>" );

    msg.append("</body>");
    msg.append("</message>");
    String message = msg.toString();

    String reqSoap = SoapClient.makeReqSoap(IF_ID, SRC_ORG_CD, TGT_ORG_CD, (SoapClient.getMsgKey()), message);
    logger.error("req :\n{}", reqSoap);
    String resSoap = SoapClient.sendHttpRequest(URL, reqSoap, CHARSET, IF_ID);
    logger.error("res :\n{}-- END --\n\n", resSoap);

    DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();

    Document doc = dBuilder.parse(new InputSource(new StringReader(resSoap)));
    NodeList nList = doc.getElementsByTagName("list");

    for (Node node = nList.item(0).getFirstChild(); node != null; node = node.getNextSibling()) {

      switch( node.getNodeName() ) {
        case "not_ancmt_mgt_no"     : getVO.setNot_ancmt_mgt_no(node.getTextContent());     break;
        case "not_ancmt_se_nm"      : getVO.setNot_ancmt_se_nm(node.getTextContent());      break;
        case "not_ancmt_reg_no_nm"  : getVO.setNot_ancmt_reg_no_nm(node.getTextContent());  break;
        case "chr_id"               : getVO.setChr_id(node.getTextContent());               break;
        case "telno"                : getVO.setTelno(node.getTextContent());                break;
        case "dep_nm"               : getVO.setDep_nm(node.getTextContent());               break;
        case "not_ancmt_sj"         : getVO.setNot_ancmt_sj(node.getTextContent());         break;
        case "pbs_hop_ymd"          : getVO.setPbs_hop_ymd(node.getTextContent());          break;
        case "pbs_tlt_no"           : getVO.setPbs_tlt_no(node.getTextContent());           break;
        case "pbs_ymd"              : getVO.setPbs_ymd(node.getTextContent());              break;
        case "not_ancmt_cn"         : getVO.setNot_ancmt_cn(node.getTextContent());         break;
        default : logger.error("node name : {}", node.getNodeName());
      }

    }
    
    //logger.error("RESULT DATA : {}\n<-\n", ToStringBuilder.reflectionToString(getVO, ToStringStyle.MULTI_LINE_STYLE));

    // 저장
    gosiDAO.insertGosi(getVO);
  }

  public void insertGosiFileList(GosiVO getVO) throws Exception {

    StringBuilder msg = new StringBuilder();

    msg.append("<message>");
    msg.append("<body>");

    //query_id는 각마다 다름
    msg.append( "<query_id>4040000SOI128</query_id>" );
    msg.append( "<dataList><data>" + getVO.getNot_ancmt_mgt_no() + "</data></dataList>" );

    msg.append("</body>");
    msg.append("</message>");

    String message = msg.toString();

    String reqSoap = SoapClient.makeReqSoap(IF_ID, SRC_ORG_CD, TGT_ORG_CD, (SoapClient.getMsgKey()), message);
    String resSoap = SoapClient.sendHttpRequest(URL, reqSoap, CHARSET, IF_ID);
    logger.error("resSoap : {}\n\n", resSoap);

    DocumentBuilderFactory dbFactoty3 = DocumentBuilderFactory.newInstance();
    DocumentBuilder dBuilder3 = dbFactoty3.newDocumentBuilder();

    Document doc3 = dBuilder3.parse(new InputSource(new StringReader(resSoap)));
    NodeList nList3 = doc3.getElementsByTagName("list");

    for (int i = 0; i < nList3.getLength(); i++) {
      for (Node node3 = nList3.item(i).getFirstChild(); node3 != null; node3 =
          node3.getNextSibling()) {

        switch(node3.getNodeName()) {
          case "file_nm"      :  getVO.setFile_nm(node3.getTextContent());      break;
          case "sys_file_nm"  :  getVO.setSys_file_nm(node3.getTextContent());  break;
          case "file_path"    :  getVO.setFile_path(node3.getTextContent());    break;
          default : logger.error("node name : {}", node3.getNodeName());
        }

      }
      gosiDAO.insetGosiAtchmnfl(getVO);
    }
  }


  /**
   * TN_GOSI의 만료된 데이터를 지운다.
   * TN_GOSI_ATCHMNFL에서 모든 데이터를 지운다.
   */
  public void deleteExpiredGosi(String not_ancmt_se_code_arr[]) throws Exception {
    GosiVO gosiVO = new GosiVO();
    if (not_ancmt_se_code_arr[0].equals(not_ancmt_se_code_arr[1]))
      gosiVO.setNot_ancmt_se_code(not_ancmt_se_code_arr[0]);
    else
      gosiVO.setNot_ancmt_se_code(not_ancmt_se_code_arr[0] + "," + not_ancmt_se_code_arr[1] + ","
          + not_ancmt_se_code_arr[2]);

    gosiDAO.deleteGosiList(gosiVO);
    gosiDAO.deleteGosiAtchmnfl(gosiVO);
  }


  public static Boolean checkTimeOut(long time, final String not_ancmt_se_code_arr[]) {
    ExecutorService threadPool = Executors.newCachedThreadPool();
    FutureTask<Boolean> task = new FutureTask<Boolean>(new Callable<Boolean>() {
      public Boolean call() throws Exception {

        StringBuilder msg = new StringBuilder();

        msg.append("<message>");
        msg.append("<body>");

        //query_id는 각마다 다름
        msg.append( "<query_id>4040000SOI125</query_id>" );
        msg.append( "<dataList><data>" + not_ancmt_se_code_arr[0] + "</data></dataList>" );
        msg.append( "<dataList><data>" + not_ancmt_se_code_arr[1] + "</data></dataList>" );
        msg.append( "<dataList><data>" + not_ancmt_se_code_arr[2] + "</data></dataList>" );
        msg.append( "<dataList><data></data></dataList>  <dataList><data></data></dataList>" );
        msg.append( "<dataList><data></data></dataList>  <dataList><data></data></dataList>" );
        msg.append( "<dataList><data>0</data></dataList>" );
        msg.append( "<dataList><data>10</data></dataList>" );

        msg.append("</body>");
        msg.append("</message>");

        String reqSoap = SoapClient.makeReqSoap(IF_ID, SRC_ORG_CD, TGT_ORG_CD, (SoapClient.getMsgKey()), msg.toString());
        SoapClient.sendHttpRequest(URL, reqSoap, CHARSET, IF_ID);
        //String resSoap = SoapClient.sendHttpRequest(URL, reqSoap, CHARSET, IF_ID);
        //System.out.println("resSoap==========" + resSoap);
        return true;
      }
    });

    threadPool.execute(task);
    Boolean result = false;
    try {
      try {
        result = task.get(time, TimeUnit.SECONDS);
      } catch (TimeoutException e) {
        result = false;
      }

    } catch (InterruptedException e) {
      StringUtil.printException( e );
    } catch (ExecutionException e) {
      StringUtil.printException( e );
    }

    return result;
  }
}