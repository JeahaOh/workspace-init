package egovframework.memb.web;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import egovframework.memb.service.MembService;
import egovframework.memb.vo.Memb;
import egovframework.util.PatternTest;

/**
 * @Class Name  : MembController.java
 * @Discription : Memb Controller Class
 * @ @ 수정일 수정자 수정내용 @ --------- --------- ------------------------------- @ 2019.03.03 최초생성
 *
 * @author 제하
 * @since 2019. 03.03
 * @version 1.0
 * @see
 *
 *      Copyright (C) by JeahaOh All right reserved.
 */

@Controller
@RequestMapping("/memb")
public class MembController {

  private static final Logger logger = LoggerFactory.getLogger(MembController.class);

  @Resource(name = "membService")
  private MembService membService;

  @Resource(name = "patternTest")
  private PatternTest patternTest;
  
  /**
   * id값이 있는지 확인 한다.
   * 
   * @param id  - 확인 할 id 값
   * @return id의 존재 여부
   * @throws Exception
   */
  @RequestMapping(value="/idCheck", method= RequestMethod.POST)
  public @ResponseBody String idCheck(@RequestParam String id) throws Exception {
    logger.info(id);
    
    if(id != null && id != ""){
      if( patternTest.idTest(id) ) {
        return membService.idCheck(id);
      }
    }
    return "false";
  }
  
  /**
   * 회원 가입을 한다.
   * 예외 처리를 다시 해야함.
   * 
   * @param id  - 회원 가입 할 id
   * @param pwd - 회원 가입 할 pwd
   * @param nick- 회원 가입 할 nick
   * @return 회원 가입 결과
   * @throws Exception
   */
  @RequestMapping(value="/signUp", method= RequestMethod.POST)
  public @ResponseBody String signUp(
      @RequestParam String id,
      @RequestParam String pwd,
      @RequestParam String nick)
          throws Exception {
    logger.info("\n\t/memb/signUp {}", id);
    
    if( patternTest.pwdTest(id, pwd) ) {
      return "signUp/error";
    }
    if( membService.signUp(id, pwd, nick) ) {
      return "success";
    } else {
      return "signUp/fail";
    }
  }

  /**
   * 관리자 로그인을 한다.
   * 
   * @param id  - 로그인 할 id
   * @param pwd - memb의 비밀번호
   * @return 
   * @exception Exception
   */
  @RequestMapping(value="/adminLogin", method= RequestMethod.POST)
  public @ResponseBody String adminLogin(
      @RequestParam String id,
      @RequestParam String pwd,
      HttpSession session)
          throws Exception {
    logger.info(session.toString());

    Memb user = (Memb) membService.adminLogin(id, pwd);

    if( user == null ) {
      return "loginError";
    }

    //    System.out.println("\n\n\n\nLoginUser\n"+user.toString() + "\n\n\n");
    session.setAttribute("loginUser", user);
    return "rst/list";
  }

  /**
   * 일반 사용자 로그인을 한다.
   * 
   * @param id  - 로그인 할 id
   * @param pwd - memb의 비밀번호
   * @return 
   * @exception Exception
   */
  @ResponseBody
  @RequestMapping(value="/membLogin", method= RequestMethod.POST)
  public Memb login(
      @RequestParam String id,
      @RequestParam String pwd,
      HttpSession session)
          throws Exception {
    System.out.println(id + pwd);
    Memb user = (Memb) membService.login(id, pwd);

    if( user == null ) {
      return null;
    }

    //    System.out.println("\n\n\n\nLoginUser\n"+user.toString() + "\n\n\n");
    session.setAttribute("loginUser", user);
    logger.info("\n\t/memb/membLogin return -> {}", user.toString());
    return user;
  }
  
  /**
   * 회원 탈퇴를 기능.
   * @param id  - 탈퇴할 회원의 id
   * @param session
   * @throws Exception
   */
  @RequestMapping(value="/signOut", method=RequestMethod.POST)
  public void signOut ( @RequestParam String id, HttpSession session) throws Exception {
    logger.info(id, session);
    membService.signOut(id);
    Memb m = (Memb)session.getAttribute("loginUser");
    session.removeAttribute("loginUser");
    logger.info("\n\t/memb/signOut Recieve {}", id);
    
    logger.info("\n\t/memb/signOut session Validate {}", m.toString());
  }
}
