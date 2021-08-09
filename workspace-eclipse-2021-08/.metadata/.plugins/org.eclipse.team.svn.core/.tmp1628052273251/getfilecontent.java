package kr.co.hanshinit.NeoCMS.cop.cnt.fbKey.service.impl;

import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.apache.commons.mail.SimpleEmail;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import kr.co.hanshinit.NeoCMS.cmm.service.ResponseJSON;
import kr.co.hanshinit.NeoCMS.cmm.util.DateUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.EmailUtil;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.cop.cnt.fbKey.service.FbApiKey;
import kr.co.hanshinit.NeoCMS.cop.cnt.fbKey.service.FbApiKeyService;
import kr.co.hanshinit.NeoCMS.uat.uia.service.LoginUtil;

@Repository("fbApiKeyService")
public class FbApiKeyServiceImpl implements FbApiKeyService {
  
  Logger logger = LoggerFactory.getLogger( this.getClass() );
  
  @Resource(name="fbApiKeyDAO")
  private FbApiKeyDAO keyDAO;

  // FACEBOOK API KEY의 총 갯수를 가져온다.
//  @Override
//  public int getApiKeyCnt() throws Exception {
//    return keyDAO.getApiKeyCnt();
//  }

  //  FACEBOOK API KEY 목록 조회
  @Override
  public List<FbApiKey> getApiKeyList() throws Exception {
    return keyDAO.getApiKeyList();
  }

  // FACEBOOK API KEY 추가 혹은 수정
  @Override
  public ResponseJSON upsertApiKey( HttpServletRequest req, FbApiKey key ) throws Exception {
    ResponseJSON resJSON = new ResponseJSON();
    
    String userId = LoginUtil.getLoginId(req.getSession());
    key.setFrstRegisterId(userId);
    key.setFrstRegisterIp(req.getRemoteAddr());
    key.setFrstRegisterPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    key.setLastUpdusrId(userId);
    key.setLastUpdusrIp(req.getRemoteAddr());
    key.setLastUpdusrPnttm(DateUtil.getNowDateTime("yyyyMMddHHmmss"));
    
    resJSON.setResult( keyDAO.upsertApiKey(key) );
    
    if( !StringUtil.isEmpty( key.getFbTmpToken() ) && reqFbLngTmToken(key) ) {
      keyDAO.upsertApiToken( key );
      resJSON.setMsg( key.getFbLngTmToken() );
    }
    
    return resJSON;
  }

  // FACEBOOK API KEY 조회
  @Override
  public FbApiKey getApiKey(FbApiKey key) throws Exception {
    key = keyDAO.getApiKey(key);
    
    logger.debug("\nkey : {}", ToStringBuilder.reflectionToString( key, ToStringStyle.MULTI_LINE_STYLE));
    logger.debug("\nExprTm : {}", key.getExprTm());
    
    Date exprDe = (Date) (new SimpleDateFormat("yyyy-MM-dd")).parse( key.getExprTm().split(" ")[0] );
    //  logger.debug("\t>> exprDe : {}", exprDe );
    
    //  오늘로부터 몇일 후인지 계산
    long calDate = exprDe.getTime() - System.currentTimeMillis();
    calDate = calDate / (24 * 60 * 60 * 1000);
    calDate = Math.abs( calDate );
    
    logger.debug("\t>> calDate : {}", calDate );
    
    if( calDate <= 7 && !StringUtil.isEmpty( key.getMngrEmail() ) ) {
      
      SimpleEmail email = new SimpleEmail();
      
      //  받는 주소
      email.addTo( key.getMngrEmail() );
      email.addTo("jeaha1217@hanshinit.co.kr");
      
      //  메일 제목
      email.setSubject( "[알림] FACEBOOK API KEY를 갱신해 주세요." );
      // 메일 내용
      email.setContent( "[WEB 자동 발송] FACEBOOK API KEY를 갱신해 주세요.", "text/plain; charset=euc-kr");
      
      // 발송 요청
      EmailUtil.sendEmail(email);
      
    }
    
    return key;
  }
  @Override
  public FbApiKey getApiKey( String fbId ) throws Exception  {
    FbApiKey key = new FbApiKey();
    key.setFbId( fbId );
    return this.getApiKey(key);
  }

  
  // FACEBOOK API KEY 삭제
  @Override
  public ResponseJSON deleteApiKey(FbApiKey key) throws Exception {
    ResponseJSON resJSON = new ResponseJSON();
    resJSON.setResult( keyDAO.deleteApiKey(key) );
    return resJSON;
  }
  
  
  // FACEBOOK API KEY를 받아서 임시 키로 장기 키를 요청한다.
  // 
  // 성공시 TRUE, 실패시 FALSE
  private boolean reqFbLngTmToken( FbApiKey key ) throws Exception {
    String url =
      "https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id="
        + key.getFbAppId()
        + "&client_secret=" + key.getFbAppScrtCd()
        + "&fb_exchange_token=" + key.getFbTmpToken();

    URL postUrl = new URL(url);
    
    //logger.debug("\n\tpostUrl : {}", ToStringBuilder.reflectionToString(postUrl, ToStringStyle.MULTI_LINE_STYLE));

    InputStreamReader isr;
    
    try {
      //  요청
      isr = new InputStreamReader(postUrl.openConnection().getInputStream(), "UTF-8");
      logger.debug("\n\tisr : {}", ToStringBuilder.reflectionToString(isr, ToStringStyle.MULTI_LINE_STYLE));
    } catch ( Exception e ) {
      StringUtil.printException( e );
      // 실패시 FALSE
      return false;
    }
    

    JSONObject object = (JSONObject) JSONValue.parseWithException(isr);

    logger.debug("auth : " + object.toString());
    
    //  KEY가 없다면 FALSE
    if( object.get("access_token") == null ) return false;

    //  성공시 호출시 받은 FbApiKey애 장기 토큰과 만료일을 넣어준다.
    // logger.debug("access_token : " + object.get("access_token"));
    key.setFbLngTmToken( (String) object.get("access_token") );
    key.setExprTm( String.valueOf( object.get("expires_in") ) );
    
    return true;
  }

}
