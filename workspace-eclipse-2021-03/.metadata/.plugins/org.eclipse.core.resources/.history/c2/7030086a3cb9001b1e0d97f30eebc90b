package kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import egovframework.rte.psl.dataaccess.EgovAbstractDAO;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteItm.service.MvoteQestn;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.service.MvoteObjctRspns;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.service.MvoteRespond;
import kr.co.hanshinit.NeoCMS.cop.mvote.qestnar.mvoteRspns.service.MvoteSbjctRspns;
@Repository("mvoteRspnsDAO")
public class MvoteRspnsDAO extends EgovAbstractDAO{

	@SuppressWarnings("unchecked")
	public List<MvoteObjctRspns> selectObjctRspnsList(int mvoteNo) throws Exception
	{
		return (List<MvoteObjctRspns>)list("mvoteRspnsDAO.selectObjctRspnsList", mvoteNo);
	}

//	@SuppressWarnings("unchecked")
//	public List<MvoteObjctRspns> selectObjctAnswerCntList(int mvoteNo) throws Exception
//	{
//		//mvoteRspnsDAO.
//		return (List<MvoteObjctRspns>)list("mvoteRspnsDAO.selectObjctAnswerCntList", mvoteNo);
//	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	public Map<Integer, MvoteObjctRspns>selectObjctAnswerCntList(int mvoteNo) throws Exception
	{
		return getSqlMapClientTemplate().queryForMap("mvoteRspnsDAO.selectObjctAnswerCntList", mvoteNo, "mvoteAnswerNo");
	}



	@SuppressWarnings("unchecked")
	public List<MvoteSbjctRspns> selectSbjctRspnsList(int mvoteNo) throws Exception
	{
		return (List<MvoteSbjctRspns>)list("mvoteRspnsDAO.selectSbjctRspnsList", mvoteNo);
	}
	
	
	public void insertMvoteObjctRspns(MvoteObjctRspns mvoteObjctRspns) throws Exception
	{
		insert("mvoteRspnsDAO.insertMvoteObjctRspns", mvoteObjctRspns);
	}

	public void insertMvoteSbjctRspns(MvoteSbjctRspns mvoteSbjctRspns) throws Exception
	{
		insert("mvoteRspnsDAO.insertMvoteSbjctRspns", mvoteSbjctRspns);
	}

	public void insertMvoteRespond(MvoteRespond mvoteRespond) throws Exception
	{
		insert("mvoteRspnsDAO.insertMvoteRespond", mvoteRespond);
	}

	public int selectDuplQestnar(MvoteRespond mvoteRespond) throws Exception
	{
		return (Integer)select("mvoteRspnsDAO.selectDuplQestnar", mvoteRespond);
	}

	public int selectDuplMvoteQestnar(MvoteRespond mvoteRespond) throws Exception
	{
		return (Integer)select("mvoteRspnsDAO.selectDuplMvoteQestnar", mvoteRespond);
	}

	public int selectMvoteRespondKey() throws Exception
	{
		return (Integer)select("mvoteRspnsDAO.selectMvoteRespondKey");
	}
	
	
	
	//새로운 통계
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectAllRspnsList(int mvoteNo) throws Exception
	{
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectAllRspnsList", mvoteNo);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectPnlgrpRspnsList(int mvoteNo, int totCnt) throws Exception
	{
		MvoteRespond mvoteRespond = new MvoteRespond();
		mvoteRespond.setMvoteNo(mvoteNo);
		mvoteRespond.setTotCnt(totCnt);
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectPnlgrpSexRspnsList", mvoteRespond);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectSexRspnsList(int mvoteNo, int totCnt) throws Exception
	{
		MvoteRespond mvoteRespond = new MvoteRespond();
		mvoteRespond.setMvoteNo(mvoteNo);
		mvoteRespond.setTotCnt(totCnt);
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectSexRspnsList", mvoteRespond);
	}
	
	//새로운 통계
	@SuppressWarnings("unchecked")
	public MvoteRespond selectAgeRspnsList(int mvoteNo, int totCnt) throws Exception
	{
		MvoteRespond mvoteRespond = new MvoteRespond();
		mvoteRespond.setMvoteNo(mvoteNo);
		mvoteRespond.setTotCnt(totCnt);
		
		return (MvoteRespond)select("mvoteRspnsDAO.selectAgeRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectAreaRspnsList(int mvoteNo, int totCnt) throws Exception
	{
		MvoteRespond mvoteRespond = new MvoteRespond();
		mvoteRespond.setMvoteNo(mvoteNo);
		mvoteRespond.setTotCnt(totCnt);
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectAreaRspnsList", mvoteRespond);
	}
	
	//새로운 문항별 통계
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectQesAllRspnsList(MvoteRespond mvoteRespond) throws Exception
	{
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectQesAllRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectQesPanelRspnsList(MvoteRespond mvoteRespond) throws Exception
	{
		
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectQesPanelRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectQesNoPanelRspnsList(MvoteRespond mvoteRespond) throws Exception
	{
		
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectQesNoPanelRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectQesSexRspnsList(MvoteRespond mvoteRespond) throws Exception
	{
		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectQesSexRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectQesAgeRspnsList(MvoteRespond mvoteRespond) throws Exception
	{

		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectQesAgeRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectQesDtAreaRspnsList(MvoteRespond mvoteRespond) throws Exception
	{

		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectQesDtAreaRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectQesAreaRspnsList(MvoteRespond mvoteRespond) throws Exception
	{

		
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectQesAreaRspnsList", mvoteRespond);
	}
	
	@SuppressWarnings("unchecked")
	public List<MvoteSbjctRspns> selectSbjctRspnsDtList(MvoteRespond mvoteRespond) throws Exception
	{
		return (List<MvoteSbjctRspns>)list("mvoteRspnsDAO.selectSbjctRspnsDtList", mvoteRespond);
	}
	
	
	@SuppressWarnings("unchecked")
	public List<MvoteRespond> selectRspnsUserList(MvoteRespond mvoteRespond) throws Exception
	{
		return (List<MvoteRespond>)list("mvoteRspnsDAO.selectRspnsUserList", mvoteRespond);
	}
	
}
