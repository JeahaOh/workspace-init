<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ui"  uri="http://egovframework.gov/ctl/ui"%>
<%@ taglib prefix="c"   uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
  <meta name="decorator" content="${menuInfo.siteId}" />
  <title>${menuInfo.cntntsNm}[목록] -  ${siteInfo.siteNm}</title>
</head>
<body>
  <div class="educate social">
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">사회복지시설 종사자 </span>역량강화 교육</div>
        <div class="educate_sub">
          사회복지시설 업무 전반에 대한 온라인교육<br>
          민관협력시설지침관리교육
        </div>
      </div>
    </div>

    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>사회복지사를 위한 법정 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="16%">
        <col width="16%">
        <col width="16%">
      </colgroup>
      <thead>
        <tr>
          <th scope="col">번호</th>
          <th scope="col">교육과정</th>
          <th scope="col">수강인원</th>
          <th scope="col">신청상황</th>
          <th scope="col">수강신청</th>
        </tr>
      </thead>
      <tbody>
        <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
        -->
        <c:set var="eduNo" value="1" />
        <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY08'}">
            <tr>
              <td>${eduNo}</td>
              <td class="subject">
                <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
              </td>
              <td>${result.confirm }명/${result.count}명</td>
              <td class="status type${result.status}">
                <span>${ests01[result.status]}</span>
              </td>
              <td class="apply type${result.status}">
                <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
              </td>
            </tr>
            <c:set var="eduNo" value="${eduNo+1 }" />
          </c:if>
        </c:forEach>
        <c:if test="${eduNo eq '1'}">
          <tr>
            <td colspan="5">교육과정이 없습니다.</td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div>



  <div class="educate social">
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">7월에 열리는 </span>법정교육</div>
        <div class="educate_sub">
          사회복지 현장에 꼭필요한 필수법정교육<br>
          온라인으로 쉽고 간편하게
        </div>
      </div>
    </div>

    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>사회복지사를 위한 법정 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="16%">
        <col width="16%">
        <col width="16%">
      </colgroup>
      <thead>
      <tr>
        <th scope="col">번호</th>
        <th scope="col">교육과정</th>
        <th scope="col">수강인원</th>
        <th scope="col">신청상황</th>
        <th scope="col">수강신청</th>
      </tr>
      </thead>
      <tbody>
      <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
          -->
          <c:set var="eduNo" value="1" />
          <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY06'}">
      <tr>
        <td>${eduNo}</td>
        <td class="subject">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
        </td>
        <td>${result.confirm }명/${result.count}명</td>
        <td class="status type${result.status}">
          <span>${ests01[result.status]}</span>
        </td>
        <td class="apply type${result.status}">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
        </td>
      </tr>
      <c:set var="eduNo" value="${eduNo+1 }" />
      </c:if>
      </c:forEach>
        <c:if test="${eduNo eq '1'}">
        <tr>
          <td colspan="5">교육과정이 없습니다.</td>
        </tr>
        </c:if>
      </tbody>
    </table>
  </div>


  <div class="educate nursing">
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">요양보호사</span>맞춤형 역량강화교육</div>
        <div class="educate_sub">
          편안한 노후와 효도의 기쁨<br />
          사회복지종사자의 전문성 향상에 충북종합사회복지센터가 앞장서겠습니다.
        </div>
      </div>
    </div>
    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>요양보호사를 위한 법정 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="14%">
        <col width="14%">
        <col width="13%">
      </colgroup>
      <thead>
      <tr>
        <th scope="col">번호</th>
        <th scope="col">교육과정</th>
        <th scope="col">수강인원</th>
        <th scope="col">신청상황</th>
        <th scope="col">수강신청</th>
      </tr>
      </thead>
      <tbody>
        <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
        -->
        <c:set var="eduNo" value="1" />
        <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY09'}">
            <tr>
              <td>${eduNo}</td>
              <td class="subject">
                <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
              </td>
              <td>${result.confirm }명/${result.count}명</td>
              <td class="status type${result.status}">
                <span>${ests01[result.status]}</span>
              </td>
              <td class="apply type${result.status}">
                <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
              </td>
            </tr>
            <c:set var="eduNo" value="${eduNo+1 }" />
          </c:if>
        </c:forEach>
        <c:if test="${eduNo eq '1'}">
          <tr>
            <td colspan="5">교육과정이 없습니다.</td>
          </tr>
        </c:if>
      </tbody>
    </table>
  </div><!-- 요양보호사 교육 -->

  <%--
  <div class="educate social">
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">5월에 열리는 </span>일반&직무교육</div>
        <div class="educate_sub">
      사회복지 현장의 역량강화을 위한 온라인교육<br>
      현장에 필요한 직무&일반교육이 한곳에
        </div>
      </div>
    </div>
    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>사회복지사를 위한 법정 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="16%">
        <col width="16%">
        <col width="16%">
      </colgroup>
      <thead>
      <tr>
        <th scope="col">번호</th>
        <th scope="col">교육과정</th>
        <th scope="col">수강인원</th>
        <th scope="col">신청상황</th>
        <th scope="col">수강신청</th>
      </tr>
      </thead>
      <tbody>
      <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
          -->
          <c:set var="eduNo" value="1" />
          <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY07'}">
      <tr>
        <td>${eduNo}</td>
        <td class="subject">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
        </td>
        <td>${result.confirm }명/${result.count}명</td>
        <td class="status type${result.status}">
          <span>${ests01[result.status]}</span>
        </td>
        <td class="apply type${result.status}">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
        </td>
      </tr>
      <c:set var="eduNo" value="${eduNo+1 }" />
      </c:if>
      </c:forEach>
        <c:if test="${eduNo eq '1'}">
        <tr>
          <td colspan="5">교육과정이 없습니다.</td>
        </tr>
        </c:if>
      </tbody>
    </table>
  </div>
  --%>


  <%--
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">사회복지사</span>를 위한 법정 교육</div>
        <div class="educate_sub">
          살기 좋은 세상을 만들기 위한 첫걸음 <br />
          사회복지종사자의 전문선 향상에 충북종합사회복지센터가 앞장서겠습니다.
        </div>
      </div>
    </div>
    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>사회복지사를 위한 법정 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="14%">
        <col width="14%">
        <col width="13%">
      </colgroup>
      <thead>
      <tr>
        <th scope="col">번호</th>
        <th scope="col">교육과정</th>
        <th scope="col">수강인원</th>
        <th scope="col">신청상황</th>
        <th scope="col">수강신청</th>
      </tr>
      </thead>
      <tbody>
      <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
          -->
          <c:set var="eduNo" value="1" />
          <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY01'}">
      <tr>
        <td>${eduNo}</td>
        <td class="subject">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
        </td>
        <td>${result.confirm }명/${result.count}명</td>
        <td class="status type${result.status}">
          <span>${ests01[result.status]}</span>
        </td>
        <td class="apply type${result.status}">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
        </td>
      </tr>
      <c:set var="eduNo" value="${eduNo+1 }" />
      </c:if>
      </c:forEach>
        <c:if test="${eduNo eq '1'}">
        <tr>
          <td colspan="5">교육과정이 없습니다.</td>
        </tr>
        </c:if>
      </tbody>
    </table>
  </div> <!-- 사회복지사 교육-->
  

  <div class="educate life">
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">생활지원사</span>를 위한 법정 교육</div>
        <div class="educate_sub">
          노인맞춤돌봄서비스로 마음을 나누는 방법<br />
          사회복지종사자의 전문성 향상에 충북종합사회복지센터가 앞장서겠습니다.
        </div>
      </div>
    </div>
    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>생활지원사를 위한 법정 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="14%">
        <col width="14%">
        <col width="13%">
      </colgroup>
      <thead>
      <tr>
        <th scope="col">번호</th>
        <th scope="col">교육과정</th>
        <th scope="col">수강인원</th>
        <th scope="col">신청상황</th>
        <th scope="col">수강신청</th>
      </tr>
      </thead>
      <tbody>
      <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
          -->
          <c:set var="eduNo" value="1" />
          <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY03'}">
      <tr>
        <td>${eduNo}</td>
        <td class="subject">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
        </td>
        <td>${result.confirm }명/${result.count}명</td>
        <td class="status type${result.status}">
          <span>${ests01[result.status]}</span>
        </td>
        <td class="apply type${result.status}">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
        </td>
      </tr>
      <c:set var="eduNo" value="${eduNo+1 }" />
      </c:if>
      </c:forEach>
        <c:if test="${eduNo eq '1'}">
        <tr>
          <td colspan="5">교육과정이 없습니다.</td>
        </tr>
        </c:if>    
      </tbody>
    </table>
  </div><!-- 생활지원사 교육 -->
  --%>
  <%--
  <div class="educate activity">
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">활동지원사</span>를 위한 법정 교육</div>
        <div class="educate_sub">
          힘이 되는 평생 친구와 같이<br />
          사회복지종사자의 전문성 향상에 충북종합사회복지센터가 앞장서겠습니다.
        </div>
      </div>
    </div>
    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>활동지원사를 위한 법정 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="14%">
        <col width="14%">
        <col width="13%">
      </colgroup>
      <thead>
      <tr>
        <th scope="col">번호</th>
        <th scope="col">교육과정</th>
        <th scope="col">수강인원</th>
        <th scope="col">신청상황</th>
        <th scope="col">수강신청</th>
      </tr>
      </thead>
      <tbody>
      <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
          -->
          <c:set var="eduNo" value="1" />
          <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY04'}">
      <tr>
        <td>${eduNo}</td>
        <td class="subject">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
        </td>
        <td>${result.confirm }명/${result.count}명</td>
        <td class="status type${result.status}">
          <span>${ests01[result.status]}</span>
        </td>
        <td class="apply type${result.status}">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
        </td>
      </tr>
      <c:set var="eduNo" value="${eduNo+1 }" />
      </c:if>
      </c:forEach>
        <c:if test="${eduNo eq '1'}">
        <tr>
          <td colspan="5">교육과정이 없습니다.</td>
        </tr>
        </c:if>    
      </tbody>
    </table>
  </div><!-- 활동지원사 교육 -->

  <div class="educate monthly">
    <div class="educate_title">
      <div class="educate_inner">
        <div class="educate_main"><span class="main_line">이달에 오픈</span>되는 직무와 일반 교육</div>
        <div class="educate_sub">'복지충북'으로 나아가는 길, 충북종합사회복지센터가 앞장서겠습니다.</div>
      </div>
    </div>
    <table class="bbs_type2 list" data-rwdb="yes">
      <caption>이달에 오픈되는 직무와 일반 교육 - 번호, 교육과정, 수강인원, 신청상황, 수강신청 순으로 정보를 제공</caption>
      <colgroup>
        <col width="10%">
        <col>
        <col width="14%">
        <col width="14%">
        <col width="13%">
      </colgroup>
      <thead>
      <tr>
        <th scope="col">번호</th>
        <th scope="col">교육과정</th>
        <th scope="col">수강인원</th>
        <th scope="col">신청상황</th>
        <th scope="col">수강신청</th>
      </tr>
      </thead>
      <tbody>
      <!--
          td.status{
              case1 : 신청가능 .status.type1
              case2 : 신청마감 .status.type2
          }
          td.apply {
              case1 : 신청가능 .apply.type1
              case2 : 신청불가 .apply.type2
          }
          -->
          <c:set var="eduNo" value="1" />
          <c:forEach var="result" items="${eduCourseList}" varStatus="status">
          <c:if test="${result.category2 eq 'CTGY05'}">
      <tr>
        <td>${eduNo}</td>
        <td class="subject">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}">${result.title }</a>
        </td>
        <td>${result.confirm }명/${result.count}명</td>
        <td class="status type${result.status}">
          <span>${ests01[result.status]}</span>
        </td>
        <td class="apply type${result.status}">
          <a href="./courseView.do?key=${key }&amp;course=${result.course }&amp;srcYear=${eduCourseVO.srcYear }&amp;srcQuarter=${eduCourseVO.srcQuarter }&amp;srcTitle=${eduCourseVO.srcTitle}&amp;pageIndex=${eduCourseVO.pageIndex}" class="apply_text">신청하기</a>
        </td>
      </tr>
      <c:set var="eduNo" value="${eduNo+1 }" />
      </c:if>
      </c:forEach>
        <c:if test="${eduNo eq '1'}">
        <tr>
          <td colspan="5">교육과정이 없습니다.</td>
        </tr>
        </c:if>    
      </tbody>
    </table>
  </div><!-- 이달에 오픈 교육 -->
  --%>
  <script type="text/javascript">
    function fn_alertSdate( sdate ) {
      var year = sdate.substring(0, 4);
      var month = sdate.substring(4, 6);
      var date = sdate.substring(6, 8);
      var hour = sdate.substring(8, 10);
      
      alert(year+"년 "+month+"월 "+date+"일 "+hour+"시부터 수강신청이 가능합니다.");
    }

    function fn_goPage(url){
      location.href = url;
    }
    /*
    function fn_mesg(type){
      if(type == 1){
        alert("방문접수만 가능합니다.");
      }
    }
    */
  </script>
</body>
</html>