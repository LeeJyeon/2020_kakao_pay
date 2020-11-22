package kr.co.kakaopay.mapper;

import org.apache.ibatis.annotations.Mapper;

import kr.co.kakaopay.domain.MasterPayDao;

@Mapper
public interface MasterPayMapper {
	
    public int selectVerifyToken( String deal_ymd , String token ) throws Exception;
    public int insertMasterFirst( MasterPayDao masterPayDao )throws Exception;
    public MasterPayDao selectRecieveCheck( String deal_ymd , String token )throws Exception;
    public int updateMasterPay( MasterPayDao masterPayDao )throws Exception;
	public MasterPayDao queryOwnerMaster( String deal_ymd , String token )throws Exception;
}
