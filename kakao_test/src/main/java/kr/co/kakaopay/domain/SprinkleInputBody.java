package kr.co.kakaopay.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SprinkleInputBody {
	private Integer total_money ;
	private Integer people_num ;
	public SprinkleInputBody(Integer total_money) {
		super();
		this.total_money = total_money;
	}
	
}
