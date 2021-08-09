package kr.co.hanshinit.NeoCMS.cmm.scheduler;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import javax.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import kr.co.hanshinit.NeoCMS.cmm.api.ApiService;
import kr.co.hanshinit.NeoCMS.cmm.util.StringUtil;
import kr.co.hanshinit.NeoCMS.cop.cnt.fbKey.service.FbApiKey;
import kr.co.hanshinit.NeoCMS.cop.cnt.fbKey.service.FbApiKeyService;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.CrawlingService;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.SnsDataService;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.SnsDataVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.blog.BlogVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.fb.FaceBookVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.insta.InstagramVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.twt.TwitterVO;
/**
 * 스케줄 실행을 위한 클래스
 * @author (주)한신정보기술 기업부설연구소 최관형
 * @since 2017.01.08
 * @version 1.0
 * @see
 *
 * <pre>
 * << 개정이력(Modification Information) >>
 *
 *  수정일      수정자    수정내용
 *  ---------- -------- ---------------------------
 *  2017.01.08 최관형	    최초 생성
 * </pre>
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration("file:src/main/resources/kr/co/hanshinit/NeoCMS/spring/context-*.xml")
public class Scheduler {

  private Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource(name = "weatherDataService")
  private ApiService weatherDataService;

  @Resource(name = "airDataService")
  private ApiService airDataService;

  @Resource(name = "snsDataService")
  private SnsDataService snsDataService;

  @Resource(name = "crawlingService")
  private CrawlingService crawlingService;
  
  @Resource(name="fbApiKeyService")
  private FbApiKeyService fbApiKeyService;


	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
	public boolean weatherData() throws Exception {

		List<HashMap<String, String>> resultMap = weatherDataService.getWeatherApiData();
		weatherDataService.insertData(resultMap);

		return true;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
	public boolean airData() throws Exception {

		List<HashMap<String, String>> resultMap = airDataService.getAirApiData();
		airDataService.insertData(resultMap);

		return true;

	}







	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
	public boolean facebookData1() throws Exception {
		//여성회관
    FbApiKey fbKey = fbApiKeyService.getApiKey("womanbcf");
    if( fbKey == null ) {
      logger.error("\n\tgetFaceBook PARAMETER IS NULL!!");
      return false;
    }
		FaceBookVO hpFacebookVO1 = crawlingService.getFaceBook(fbKey);
		List<SnsDataVO> resultList1 =  hpFacebookVO1.convertToSnsDataVO(null);
		System.out.println("facebook insert..");
		insertSns(resultList1);
		return true;
	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
	public boolean facebookData2() throws Exception {
	  // 20210518 -> 여성청소년센터로 추정.
    FbApiKey fbKey = fbApiKeyService.getApiKey("vomul.or.kr");
    if( fbKey == null ) {
      logger.error("\n\tgetFaceBook PARAMETER IS NULL!!");
      return false;
    }
		FaceBookVO hpFacebookVO2 = crawlingService.getFaceBook(fbKey);
		List<SnsDataVO> resultList2 =  hpFacebookVO2.convertToSnsDataVO(null);
		System.out.println("facebook insert..");
		insertSns(resultList2);
		return true;
	}

	//crawlBucheonyouthFB
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
  public boolean crawlBucheonyouthFB() throws Exception {
    //  부천시 청소년 수련관 (bucheonyouth)
    FbApiKey fbKey = fbApiKeyService.getApiKey("456704854497451");
    if( fbKey == null ) {
      logger.error("\n\tgetFaceBook PARAMETER IS NULL!!");
      return false;
    }
    try {
      FaceBookVO fbData = crawlingService.getFaceBook( fbKey );
      List<SnsDataVO> resultList3 =  fbData.convertToSnsDataVO(null);
      insertSns(resultList3);
      logger.error("\n\t>> BUCHEON YOUTH FB END SUCCESSFULLY");
    } catch (Exception e ) {
      StringUtil.printException(e, true);
      return false;
    }
    return true;
  }

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
	public boolean facebookData4() throws Exception {
	  
	  FbApiKey fbKey = fbApiKeyService.getApiKey("823180864790204");
    if( fbKey == null ) {
      logger.error("\n\tgetFaceBook PARAMETER IS NULL!!");
      return false;
    }

		//여성재단
		FaceBookVO hpFacebookVO = crawlingService.getFaceBook(fbKey);
		List<SnsDataVO> resultList =  hpFacebookVO.convertToSnsDataVO(null);
		System.out.println("facebook insert..");
		insertSns(resultList);
		return true;

	}

  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
  public boolean facebookData5() throws Exception {
    //  산울림 (echoyouth)
    FbApiKey fbKey = fbApiKeyService.getApiKey("2847795801970979");
    if( fbKey == null ) {
      logger.error("\n\tgetFaceBook PARAMETER IS NULL!!");
      return false;
    }
    try {
      FaceBookVO fbVO = crawlingService.getFaceBook( fbKey );
      List<SnsDataVO> fbDataList =  fbVO.convertToSnsDataVO(null);
      insertSns(fbDataList);
      logger.error("\n\t>> ECHO YOUTH FB END SUCCESSFULLY");
    } catch (Exception e ) {
      StringUtil.printException(e, true);
      return false;
    }
    return true;
  }
  
  /**
   * 소사 청소년 수련관 Facebook 데이터 처리
   */
  @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
  public boolean crawlSosayouthFB() throws Exception {
    FbApiKey fbKey = fbApiKeyService.getApiKey("1068187709949270");
    if( fbKey == null ) {
      logger.error("\n\tgetFaceBook PARAMETER IS NULL!!");
      return false;
    }
    try {
      FaceBookVO fbVO = crawlingService.getFaceBook( fbKey );
      List<SnsDataVO> fbDataList =  fbVO.convertToSnsDataVO(null);
      insertSns(fbDataList);
      logger.error("\n\t>> sosayouthFB END SUCCESSFULLY");
    } catch (Exception e ) {
      StringUtil.printException(e, true);
      return false;
    }
    return true;
  }






	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
	public boolean twitterData() throws Exception {

		TwitterVO twitterVO = crawlingService.getTwitter("140003544-gGF8SK72FzVXD3sLe0quMlmzwhYmdVhSKBURbX9M", "ptmdopeMSAmUumSqcMxs0Ce0GOtbze2ShKWrRYGZzAQ5W", "27m2ky9ytZo5CI0mSEhTFnd1q", "RKlm20v0exlRBY9d10Fave4yalHdDAWLwMls6deDt9BMJJm50S");
		List<SnsDataVO> resultList = twitterVO.convertToSnsDataVO(null);
		insertSns(resultList);

		return true;

	}

	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Exception.class, SQLException.class}, readOnly = false)
	public boolean blogData() throws Exception {

		BlogVO BlogVO = crawlingService.getBlog("bwyf9090");
		List<SnsDataVO> resultList =  BlogVO.convertToSnsDataVO(null);
		insertSns(resultList);

		return true;

	}

	public boolean instagramData() throws Exception {

		InstagramVO instagramVO = crawlingService.getInstagram("2076220629", "2076220629.844fc82.5ccf7336291e4a2681ddbfa5cff871b4");
		List<SnsDataVO> resultList =  instagramVO.convertToSnsDataVO(null);
		insertSns(resultList);

		return true;
	}

  /**
   * 소사 청소년 수련관 Instagram 데이터 처리
  public boolean sosayouthInsta() throws Exception {
    try {
      //  String userId, String accessToken
      InstagramVO instagramVO = crawlingService.getInstagram("", "");
      List<SnsDataVO> resultList = instagramVO.convertToSnsDataVO(null);
      insertSns(resultList);
      logger.error("\n\t>> sosayouthInsta END SUCCESSFULLY");
    } catch (Exception e ) {
      StringUtil.printException(e, true);
      return false;
    }
    return true;
  }
   */

  public void insertSns(List<SnsDataVO> snsResult) {
    int cnt = 0;
    for (SnsDataVO snsDataVO : snsResult) {
      try {
        SnsDataVO forSearch = new SnsDataVO();
        forSearch.setSnsUrl(snsDataVO.getSnsUrl());
        
        if (snsDataService.selectSnsDataCount(forSearch) == 0) {
          // if(snsDataVO.getSnsContents() != null){
          // snsDataVO.setSnsContents(URLEncoder.encode(snsDataVO.getSnsContents()));
          // }
          snsDataService.insertSnsData(snsDataVO);
          logger.debug("\n\t>> INSERT {}", snsDataVO.getSnsUrl());
          cnt++;
        }
        //  20210428 굳이 로그 찍을 필요가 없어시 지움.
//        else {
//          // snsDataService.updateSnsDataBySnsUrl(snsDataVO.getSnsUrl(), snsDataVO);
//          logger.debug("\n\t{} is already exist.", snsDataVO.getSnsUrl());
//        }
      } catch (Exception ex) {
        StringUtil.printException( ex );
      }
    }
    logger.debug("\n\t>> INSERT {} FACEBOOK POSTS!! ", cnt);
  }


}