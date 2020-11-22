package kr.co.kakaopay.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(Include.NON_NULL)
public class TransPayDao extends SprinkleInputBody {
    private String deal_ymd ;
    private String token ;
	private String user_id ;
	private String room_id ;
	private String rcv_id;
	private Integer rcv_money ;
	private Integer seq;
	private String rcv_yn;
	
	
}
