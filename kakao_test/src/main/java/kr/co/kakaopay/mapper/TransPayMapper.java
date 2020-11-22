package kr.co.kakaopay.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import kr.co.kakaopay.domain.TransPayDao;

@Mapper
public interface TransPayMapper {

	public int insertTransFirst(TransPayDao transPayDao) throws Exception;

	public int selectCheckAlreadyRecieve(String deal_ymd, String token, String user_id) throws Exception;

	public int updateTransRecieve(TransPayDao transPayDao) throws Exception;

	public int selectRecieveMoney(String deal_ymd, String token, String user_id) throws Exception;

	public List<TransPayDao> queryOwnerTrans(String deal_ymd, String token) throws Exception;

}
