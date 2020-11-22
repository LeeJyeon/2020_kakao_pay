package kr.co.kakaopay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.kakaopay.domain.MasterPayDao;
import kr.co.kakaopay.mapper.MasterPayMapper;

@Service
public class MasterPayService {
	@Autowired
	private MasterPayMapper mapper;

	public int verifyToken(String deal_ymd, String token) throws Exception {
		return mapper.selectVerifyToken(deal_ymd, token);
	}

	public int masterFirst(MasterPayDao masterPayDao) throws Exception {
		return mapper.insertMasterFirst(masterPayDao);
	}

	public MasterPayDao recieveCheck(String deal_ymd, String token)throws Exception {
		return mapper.selectRecieveCheck(deal_ymd, token);
	}

	public int updateMaster(MasterPayDao masterPayDao) throws Exception{
		return mapper.updateMasterPay(masterPayDao);
	}

	public MasterPayDao queryOwnerMaster(String deal_ymd, String token) throws Exception{
		return mapper.queryOwnerMaster(deal_ymd, token);
	}

}
