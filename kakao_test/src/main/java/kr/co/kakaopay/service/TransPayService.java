package kr.co.kakaopay.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.kakaopay.domain.TransPayDao;
import kr.co.kakaopay.mapper.TransPayMapper;

@Service
public class TransPayService {
	@Autowired
	private TransPayMapper mapper;

	public int transFirst(TransPayDao transPayDao)throws Exception {
		return mapper.insertTransFirst(transPayDao);
	}

	public int transCheckAlreadyRecieve(String deal_ymd, String token, String user_id) throws Exception {
		return mapper.selectCheckAlreadyRecieve(deal_ymd, token, user_id);
	}

	public int updateTransRecieve(TransPayDao transPayDao) throws Exception{
		return mapper.updateTransRecieve(transPayDao);
	}

	public int selectRecieveMoney(String strNowDate, String token, String user_id) throws Exception{
		return mapper.selectRecieveMoney(strNowDate, token, user_id);
	}

	public List<TransPayDao> queryOwnerTrans( String deal_ymd , String token ) throws Exception{
		return mapper.queryOwnerTrans(deal_ymd, token);
	}
}
