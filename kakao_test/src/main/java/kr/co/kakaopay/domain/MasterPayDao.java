package kr.co.kakaopay.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class MasterPayDao extends SprinkleInputBody {
    private String deal_ymd ;
    private String token ;
	private String user_id ;
	private String room_id ;
	private String snd_id;
	private String regi_dt;
	private String regi_dt_10_min;
	private int rcv_money ;
	private String regi_time;
	
	
	
	
	

}
