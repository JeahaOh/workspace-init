<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8"  trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="validator" uri="http://www.springmodules.org/tags/commons-validator" %>
<%@ taglib prefix="spring"    uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form"      uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt"       uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"        uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="ui"        uri="http://egovframework.gov/ctl/ui" %>
<%@ taglib prefix="c"         uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
  <meta name="decorator" content="${menuInfo.siteId}" />
  <title>${siteInfo.siteNm}</title>
</head>
<body>

  <div class="line-gutter"></div>

    <div class="row">
      <div class="col-12">
        <span class="p-total__number">
          총 <em class="em_black" data-mask="#,##0" data-mask-reverse="true">${totCnt}</em> 개
        </span>
      </div>
    </div>

  
  <form action="javascript:void(0);" data-select="checkall" data-checkallid="#check-all2" data-checkname="checkid">
    <table class="p-table p-table--bordered p-table--hover">
      <caption>FACEBOOK API KEY 관리</caption>
      <colgroup>
        <col style="width : 3%"/>
        <col style="width : 10%"/>
        <col style="width : %"/>
        <col style="width : %"/>
        <col style="width : 17%"/>
        <col style="width : 10%"/>
      </colgroup>
      <thead class="small text_center">
        <tr>
          <td scope="col" rowspan="2">관리 번호</td>
          <td scope="col">API KEY 관리 명</td>
          <td scope="col">FACEBOOK ID</td>
          <td scope="col">APP ID</td>
          <td scope="col">APP SECRET CODE</td>
          <td scope="col" rowspan="2">CTRL</td>
        </tr>
        <tr>
          <td scope="col">관리자 Email</td>
          <td scope="col">임시 토큰(Access Token)</td>
          <td scope="col">장기 토큰</td>
          <td scope="col">만료 일시</td>
        </tr>
      </thead>
      <tbody class="small text_left">
        <c:if test="${fn:length(keyList) <= 1}">
          <tr>
            <td colspan="100%" class="text_center">
              검색 결과가 없습니다.
            </td>
          </tr>
        </c:if>
        <c:forEach var="key" items="${keyList}" varStatus="status">
          <tr>
            <td scope="col" class="text_center" rowspan="2">
              ${key.keyNo}
            </td>
            <td scope="col">
              <input
                type="text"
                name="mngNm_${key.keyNo}" id="mngNm_${key.keyNo}"
                value="${key.mngNm}"
              />
            </td>
            <td scope="col">
              <input
                type="text"
                name="fbId_${key.keyNo}" id="fbId_${key.keyNo}"
                style="width: -webkit-fill-available;"
                value="${key.fbId}" disabled
              />
            </td>
            <td scope="col">
              <input
                type="text"
                name="fbAppId_${key.keyNo}" id="fbAppId_${key.keyNo}"
                style="width: -webkit-fill-available;"
                value="${key.fbAppId}" disabled
              />
            </td>
            <td scope="col">
              <input
                type="text"
                name="fbAppScrtCd_${key.keyNo}" id="fbAppScrtCd_${key.keyNo}"
                style="width: -webkit-fill-available;"
                value="${key.fbAppScrtCd}" disabled
              />
            </td>
            <td scope="col" class="small text_center">
              <button
                onclick="editApiKey('${key.keyNo}'); return false;"
                class="p-button write p-button--small "
                >수정
              </button>
              <button
                onclick="deleteApiKey('${key.keyNo}'); return false;"
                class="p-button delete p-button--small "
                >삭제
              </button>
            </td>
          </tr>
          <tr>
            <td scope="col">
              <input
                type="text"
                name="mngrEmail_${key.keyNo}" id="mngrEmail_${key.keyNo}"
                value="${key.mngrEmail}"
              />
            </td>
            <td scope="col">
              <input
                type="text"
                name="fbTmpToken_${key.keyNo}" id="fbTmpToken_${key.keyNo}"
                style="width: -webkit-fill-available;"
                value="${key.fbTmpToken}"
              />
            </td>
            <td scope="col">
              <input
                type="text"
                name="fbLngTmToken_${key.keyNo}" id="fbLngTmToken_${key.keyNo}"
                style="width: -webkit-fill-available;"
                value="${key.fbLngTmToken}" disabled
              />
            </td>
            <td scope="col" class="small text_center">
              ${key.exprTm}
            </td>
            <td scope="col" class="small text_center">
              <c:if test="${not empty key.fbLngTmToken}">
                <button
                  onclick="refreshFbData('${key.keyNo}'); return false;"
                  class="p-button edit p-button--small "
                  >SNS 데이터 갱신
                </button>
              </c:if>
            </td>
          </tr>
        </c:forEach>
        <tr id="addRowTr">
          <td colspan="100%" class="text_center">
            <button
              onclick="addApiKeyRow(); return false;"
              class="p-button write p-button--small "
              >키 추가
            </button>
          </td>
        </tr>
      </tbody>
    </table>
  </form>
  <script>


    function addApiKeyRow() {
      var html = '';
      html += '<td scope="col" class="text_center">신규</td>';
      html += '<td scope="col">';
      html += '  <input';
      html += '    type="text" class="newKey" style="width: -webkit-fill-available;"';
      html += '    name="mngNm" id="mngNm"';
      html += '  />';
      html += '</td>';
      html += '<td scope="col">'
      html += '  <input';
      html += '    type="text" class="newKey" style="width: -webkit-fill-available;"';
      html += '    name="fbId" id="fbId"';
      html += '  />';
      html += '</td>';
      html += '<td scope="col">';
      html += '  <input';
      html += '    type="text" class="newKey" style="width: -webkit-fill-available;"';
      html += '    name="fbAppId" id="fbAppId"';
      html += '  />';
      html += '</td>';
      html += '<td scope="col">';
      html += '  <input';
      html += '    type="text" class="newKey" style="width: -webkit-fill-available;"';
      html += '    name="fbAppScrtCd" id="fbAppScrtCd"';
      html += '  />';
      html += '</td>';
      html += '<td scope="col" class="small text_center">';
      html += '  <button';
      html += '    onclick="addNewApiKey(); return false;"';
      html += '    class="p-button write p-button--small"';
      html += '    >저장';
      html += '  </button>';
      html += '  <button';
      html += '    onclick="initNewApiKey(); return false;"';
      html += '    class="p-button delete p-button--small "';
      html += '    >초기화';
      html += '  </button>';
      html += '</td>';

      tr = document.createElement('tr');
      tr.id = 'newKeyRow';
      tr.innerHTML = html.trim();

      var target = document.querySelector('#addRowTr');
      target.parentNode.insertBefore(tr, target);
      target.remove();
    }

    function initNewApiKey() {
      document.getElementById('mngNm').value = ''
      document.getElementById('fbId').value = ''
      document.getElementById('fbAppId').value = '';
      document.getElementById('fbAppScrtCd').value = '';
    }

    function addNewApiKey() {
      var isReqable = true;
      var inputs = document.querySelectorAll('.newKey')
      var params = {};
      // 유효성 검사 및 데이터 가공.
      inputs.forEach(function(ele, idx, node) {
        ele.value = ele.value.trim();
        if( !ele.value || ele.value == '' ) {
          alert('값이 없습니다.');
          ele.focus();
          isReqable = false;
          return isReqable;
        } else {
          params[ele.name] = ele.value;
        }
      });

      //console.log( params );
      // 빈 값이 있다면 return
      if( !isReqable ) return false;
      else reqEditFbApiKey(params);
    }

    function editApiKey(keyNo) {
      //console.log('keyNo', keyNo);
      var tgt = document.querySelector('#mngNm_' + keyNo)
      tgt.value = tgt.value.trim();
      if( tgt.value == '' ) {
        tgt.focus();
        alert('빈 값은 넣을 수 없습니다.');
        return false;
      }
      var params = {};
      params.mngNm        = tgt.value;
      params.mngrEmail    = document.querySelector('#mngrEmail_' + keyNo).value;
      params.fbId         = document.querySelector('#fbId_' + keyNo).value;
      params.fbAppId      = document.querySelector('#fbAppId_' + keyNo).value;
      params.fbAppScrtCd  = document.querySelector('#fbAppScrtCd_' + keyNo).value;
      params.fbTmpToken   = document.querySelector('#fbTmpToken_' + keyNo).value;
      
      //console.log( params );
      reqEditFbApiKey( params );
    }

    function deleteApiKey(keyNo) {

      if( !confirm('정말 삭제 하시겠습니까?') ) return false;

      //console.log('keyNo', keyNo);

      var params = {};
      params.flag         = 'delete';
      params.fbId         = document.querySelector('#fbId_' + keyNo).value;
      params.fbAppId      = document.querySelector('#fbAppId_' + keyNo).value;
      params.fbAppScrtCd  = document.querySelector('#fbAppScrtCd_' + keyNo).value;
      
      //console.log( params );
      reqEditFbApiKey( params );
    }

    var isOnReq = false;

    function reqEditFbApiKey(params) {
      $.ajax({
        type : "POST",
        url : "./editFbApiKey.do?key=${key}",
        dataType : "json",
        data : params,
        beforeSend : function(xhr, opts) {
          if (isOnReq) {
            xhr.abort();
          }
          isOnReq = true;
          //console.log( 'isOnReq : ', isOnReq );
        }
      })
      .done(function( res ) {
        // success code
        console.log( res );
        switch( res.result ) {
          case 0 : alert('실패'); break;
          case 1 : alert('완료'); location.reload(); break;
          case 2 : alert('완료'); break;
          default : 
        }

        if( res.msg != null || params.flag == 'delete' ) location.reload();
      })
      .fail(function() {})
      .complete( function() {
        isOnReq = false;
      });
    }

    function refreshFbData(keyNo) {
      var isReqable = true;
      var params = {};
      params.keyNo        = keyNo;
      params.fbId         = document.querySelector('#fbId_' + keyNo).value.trim();
      params.fbAppId      = document.querySelector('#fbAppId_' + keyNo).value.trim();
      params.fbAppScrtCd  = document.querySelector('#fbAppScrtCd_' + keyNo).value.trim();
      params.fbTmpToken   = document.querySelector('#fbTmpToken_' + keyNo).value.trim();
      params.fbLngTmToken = document.querySelector('#fbLngTmToken_' + keyNo).value.trim();
      
      console.log( params );

      for( var i in params ) {
        //console.log( params[i] );
        if( !params[i] || params[i] == null || params[i] == '' ) {
          isReqable = false;
        }
      }

      $.ajax({
        type : "POST",
        url : "./refreshFbData.do?key=${key}",
        dataType : "json",
        data : params,
        beforeSend : function(xhr, opts) {
          if ( isOnReq || !isReqable ) {
            xhr.abort();
          }
          isOnReq = true;
          //console.log( 'isOnReq : ', isOnReq );
        }
      })
      .done(function( res ) {
        // success code
        console.log( res );

        alert( 'FACEBOOK 게시물 ' + res.result + ' 건 갱신.' );

      })
      .fail(function() {})
      .complete( function() {
        isOnReq = false;
      });
    }
  </script>
</body>
</html>