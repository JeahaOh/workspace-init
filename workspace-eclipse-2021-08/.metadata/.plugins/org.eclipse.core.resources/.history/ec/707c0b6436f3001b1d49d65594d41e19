package kr.co.hanshinit.NeoCMS.cop.ows.lcc.service.impl;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.XMLUtil;
import kr.co.hanshinit.NeoCMS.cop.ows.lcc.service.LclChCaService;
import kr.co.hanshinit.NeoCMS.cop.ows.lcc.service.LclChCaVO;
import kr.co.hanshinit.NeoCMS.sym.cma.cdc.service.CmmnDetailCode;
import kr.co.hanshinit.NeoCMS.sym.cma.cdc.service.impl.CmmnDetailCodeDAO;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import egovframework.rte.fdl.cmmn.EgovAbstractServiceImpl;


@Service("lclChCaService")
public class LclChCaServiceImpl extends EgovAbstractServiceImpl implements LclChCaService {

	@Resource(name="lclChCaDAO")
	private LclChCaDAO lclChCaDAO;

	@Resource(name="cmmnDetailCodeDAO")
	private CmmnDetailCodeDAO cmmnDetailCodeDAO;
	
	
	@Override
	public List<LclChCaVO> selectLclChCaList(LclChCaVO lclChCaVO)
			throws Exception {
		return (List<LclChCaVO>) lclChCaDAO.selectLclChCaList( lclChCaVO );
	}

	@Override
	public int selectLclChCaTotCnt(LclChCaVO lclChCaVO) throws Exception {
		return (Integer) lclChCaDAO.selectLclChCaTotCnt( lclChCaVO );
	}

	@Override
	public int selectLclChCaEvalTotCnt(LclChCaVO lclChCaVO) throws Exception {
		return (Integer) lclChCaDAO.selectLclChCaEvalTotCnt( lclChCaVO );
	}

	@Override
	public LclChCaVO selectLclChCaData(LclChCaVO lclChCaVO) throws Exception {
		return (LclChCaVO) lclChCaDAO.selectLclChCaData( lclChCaVO );
	}

	@Override 
	public void insertLclChCa(LclChCaVO lclChCaVO) throws Exception {
		lclChCaDAO.insertLclChCa(lclChCaVO);
	}

	@Override 
	public void updateLclChCa(LclChCaVO lclChCaVO) throws Exception {
		lclChCaDAO.updateLclChCa(lclChCaVO);
	}

	@Override 
	public void deleteLclChCa(LclChCaVO lclChCaVO) throws Exception {
		lclChCaDAO.deleteLclChCa(lclChCaVO);
	}

	
	
	
  @Override
  public List<LclChCaVO> getCpmsapi030(String stcode) throws Exception {

    List<LclChCaVO> arrList = new ArrayList<LclChCaVO>();

    try {

      String url =
          "http://api.childcare.go.kr/mediate/rest/cpmsapi030/cpmsapi030/request?key=27d37f4a0d754b73bee7d9b1aab676cd&arcode=" + stcode;
      URL object = new URL(url);
      System.out.println(url);
      HttpURLConnection con = (HttpURLConnection) object.openConnection();
      con.setUseCaches(false);
      con.setDoOutput(true);
      con.setAllowUserInteraction(false);
      con.setRequestMethod("GET");

      int HttpResult = con.getResponseCode();
      if (HttpResult == HttpURLConnection.HTTP_OK) {
        Document doc = XMLUtil.parseXML(con.getInputStream());

        NodeList itemList = doc.getElementsByTagName("item");
        for (int i = 0; i < itemList.getLength(); i++) {
          LclChCaVO lclChCa = new LclChCaVO();
          String crstatusname = "";
          // 첫번째 자식을 시작으로 마지막까지 다음 형제를 실행
          for (Node node = itemList.item(i).getFirstChild(); node != null; node =
              node.getNextSibling()) {
            if (node.getNodeName().equals("sigunname")) {
              lclChCa.setLccLoc(node.getTextContent());
            } else if (node.getNodeName().equals("crname")) {
              lclChCa.setLccNm(node.getTextContent());
            } else if (node.getNodeName().equals("crtypename")) {
              lclChCa.setCorpShape(node.getTextContent());
            } else if (node.getNodeName().equals("craddr")) {
              lclChCa.setLccAddr(node.getTextContent());
            } else if (node.getNodeName().equals("crtelno")) {
              lclChCa.setLccTel(node.getTextContent());
            } else if (node.getNodeName().equals("crcapat")) {
              lclChCa.setLccPersons(Integer.parseInt(node.getTextContent()));
            } else if (node.getNodeName().equals("crchcnt")) {
              lclChCa.setLccCurpers(Integer.parseInt(node.getTextContent()));
            } else if (node.getNodeName().equals("stcode")) {
              lclChCa.setApiKey(node.getTextContent());
            } else if (node.getNodeName().equals("crspec")) {
              lclChCa.setLccServ(node.getTextContent());
            } else if (node.getNodeName().equals("crcargbname")) {
              lclChCa.setArgbNm(node.getTextContent());
            } else if (node.getNodeName().equals("crstatusname")) {
              crstatusname = node.getTextContent();
            }
          }

          String frstRegisterPnttm = DateUtil.getNowDateTime("yyyyMMddHHmmss");
          lclChCa.setFrstRegisterId("neo");
          lclChCa.setFrstRegisterPnttm(frstRegisterPnttm);

          if (!"휴지".equals(crstatusname) && !"폐지".equals(crstatusname))
            insertLclChCa(lclChCa);

          // arrList.add(lclChCa);
        }

      } else {
        System.out.println(con.getResponseMessage());
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
    }

    return arrList;

  }

	
	
	
	@Override
	public void syncLclChCa(LclChCaVO lclChCaVO) throws Exception {

		deleteLclChCa( lclChCaVO );
		
		List<CmmnDetailCode> cpm01List = cmmnDetailCodeDAO.selectCmmnDetailCodeLIstByCodeId("CPM01");
		for( int i =0; i < cpm01List.size(); i++ ) {
			getCpmsapi030(cpm01List.get(i).getCode());
		}
		
	}
	
	@Override
	public  List<LclChCaVO> selectLclChCaEvalList(LclChCaVO lclChCaVO)
			throws Exception {
		return (List<LclChCaVO>) lclChCaDAO.selectLclChCaEvalList( lclChCaVO );
	}
	
}
