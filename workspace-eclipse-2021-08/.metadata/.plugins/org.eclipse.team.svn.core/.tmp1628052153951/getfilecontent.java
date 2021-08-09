package kr.co.hanshinit.NeoCMS.cop.cnt.service.impl;

import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import kr.co.hanshinit.NeoCMS.cmm.util.HttpRequestUtil;
import kr.co.hanshinit.NeoCMS.cop.cnt.fbKey.service.FbApiKey;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.CrawlingService;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.blog.BlogVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.fb.FaceBookVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.insta.InstagramVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.twt.TwitterItemVO;
import kr.co.hanshinit.NeoCMS.cop.cnt.service.twt.TwitterVO;
import twitter4j.MediaEntity;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

@Repository("crawlingService")
public class CrawlingServiceImpl implements CrawlingService {

  Logger logger = LoggerFactory.getLogger(this.getClass());

	/**
	 * 트위터 데이터를 가져온다
	 */
	@Override
	public TwitterVO getTwitter(String token, String tokenSecret,
			String consumerKey, String consumerSecret) throws Exception {
		ConfigurationBuilder config = new ConfigurationBuilder();
		config.setOAuthConsumerKey(consumerKey);
		config.setOAuthConsumerSecret(consumerSecret);
		config.setOAuthAccessToken(token);
		config.setOAuthAccessTokenSecret(tokenSecret);
		config.setTweetModeExtended(true);

		TwitterFactory tf = new TwitterFactory(config.build());
		Twitter twitter = tf.getInstance();
        List<Status> list = twitter.getUserTimeline();
        TwitterVO twitterVO = new TwitterVO();
        List<TwitterItemVO> itemList = twitterVO.getData();
        for(Status status : list) {
        	TwitterItemVO twitterItemVO = new TwitterItemVO();
        	String description = status.getText();
        	String pictureUrl = null;
        	MediaEntity arrMedia[] = status.getMediaEntities();
        	//System.out.println("https://twitter.com/"+status.getUser().getScreenName()+"/status/"+status.getId());

        	for (MediaEntity mediaEntity : arrMedia) {
				if(mediaEntity.getType().equals("photo")) {
					pictureUrl = mediaEntity.getMediaURL();
					break;
				}
        	}
        	twitterItemVO.setDescription(description);
        	twitterItemVO.setPubDate(status.getCreatedAt());
        	twitterItemVO.setPictureUrl(pictureUrl);
        	twitterItemVO.setUrl("https://twitter.com/"+status.getUser().getScreenName()+"/status/"+status.getId());
        	itemList.add(twitterItemVO);
        }
        return twitterVO;
	}

  /**
   * 페이스북 데이터를 가져온다
   *
   * 20210428 API 사용하는데 기본적인 인자값 어디서 가져오는지 설명도 안하는 사람이 어딨음?
   * 변수명이라도 맞춰주던지...
   * @String fbId(facebook userId)
   *   : https://www.facebook.com/profile.php?id=100047429145288 feed id 임
   * @String clientId(앱 ID)
   * @String clientSecret(앱 시크릿 코드)
   *   : https://developers.facebook.com/ -> 내 앱 -> 해당 앱 (없으면 앱 만들기) -> 설정 -> 기본설정
   *   : 앱 ID, 앱 시크릿 코드
   * @String token(엑세스토큰)
   *   : 위 순서에서 앱이 있다면,
   *   : 도구 -> 그래프 API 탐색기 -> 에서 받는 엑세스 토큰임.
   *   : (https://developers.facebook.com/tools/explorer/)
   */
  @Override
  public FaceBookVO getFaceBook(String fbId, String clientId, String clientSecret, String token)
      throws Exception {

    // 새로운 token 생성
    String tokenNew = token;
    
    // 20210724 api 방식 변경으로 주석처리.
//    try {
//      String url =
//          "https://graph.facebook.com/oauth/access_token?grant_type=fb_exchange_token&client_id="
//              + clientId + "&client_secret=" + clientSecret + "&fb_exchange_token=" + token;
//      URL postUrl;
//      postUrl = new URL(url);
//      InputStreamReader isr =
//          new InputStreamReader(postUrl.openConnection().getInputStream(), "UTF-8");
//      JSONObject object = (JSONObject) JSONValue.parseWithException(isr);
//      // logger.debug("jObj : " + object.toString());
//      // logger.debug("access_token : " + object.get("access_token"));
//      tokenNew = (String) object.get("access_token");
//    } catch (Exception e) {
//      e.printStackTrace();
//    }


    String resultString = HttpRequestUtil
        .requestHttpOnlyGet("https://graph.facebook.com/" + fbId + "?access_token=" + tokenNew
            + "&fields=id%2Cname%2Cfeed%7Bmessage%2Cfull_picture%2Ccreated_time%2Cfrom%7D");
    System.out.println(resultString);

    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();

    JsonElement locationJsonList = gson.fromJson(resultString, JsonObject.class).get("feed");

    FaceBookVO faceBook = gson.fromJson(locationJsonList, FaceBookVO.class);
    return faceBook;
  }
  
  public FaceBookVO getFaceBook(FbApiKey fbKey) throws Exception {
    if( fbKey == null ) {
      logger.error("\n\tgetFaceBook PARAMETER IS NULL!!");
      return null;
    }
    return this.getFaceBook(
        fbKey.getFbId()
      , fbKey.getFbAppId()
      , fbKey.getFbAppScrtCd()
      , fbKey.getFbLngTmToken()
    );
  }

	/**
	 * 네이버 블로그 데이터를 가져온다(rss 이용)
	 */
	@Override
	public BlogVO getBlog(String naverId) throws Exception {
		String resultString = HttpRequestUtil.requestHttpOnlyGet("http://"+naverId+".blog.me/rss");
		System.out.println(resultString);
		JAXBContext jaxbContext = null;
		BlogVO blogVO = null;

		try {
			StringReader reader = new StringReader(resultString);
			jaxbContext = JAXBContext.newInstance(BlogVO.class);
			blogVO = (BlogVO) jaxbContext.createUnmarshaller().unmarshal(reader);
		} catch (JAXBException e) {
			e.printStackTrace();
		}
		return blogVO;
	}

  public InstagramVO getInstagram(String userId, String accessToken) throws Exception {

    String resultString = HttpRequestUtil.requestHttpOnlyGet("https://api.instagram.com/v1/users/"
        + userId + "/media/recent?access_token=" + accessToken);
    GsonBuilder gsonBuilder = new GsonBuilder();
    Gson gson = gsonBuilder.create();
    JsonObject event = gson.fromJson(resultString, JsonObject.class);
    event.remove("pagination");
    JsonElement locationJsonList = gson.fromJson(event.toString(), JsonObject.class);
    InstagramVO instagram = gson.fromJson(locationJsonList, InstagramVO.class);

    return instagram;

  }




}
