package kr.co.kakaopay.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.kakaopay.domain.MasterPayDao;
import kr.co.kakaopay.domain.TransPayDao;
import kr.co.kakaopay.service.MasterPayService;
import kr.co.kakaopay.service.TransPayService;
import kr.co.kakaopay.util.ErrorWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/recieve")
public class RecieveMoneyController {

	
	private ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private MasterPayService m_service;
	@Autowired
	private TransPayService t_service;
	@Autowired
	private ErrorWriter ew;

	@PostMapping
	public ResponseEntity<Map<String, String>> recieve(@RequestHeader(value = "X-USER-ID") String user_id,
			@RequestHeader("X-ROOM-ID") String room_id, @RequestParam( value = "token" ) String token )
			throws JsonProcessingException {
		
		Map<String, String> map = new HashMap<>();
		ResponseEntity<Map<String, String>> response =null;
		
		Date nowDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String strNowDate = simpleDateFormat.format(nowDate);

		MasterPayDao rsltRecieveChek;
		try {
			rsltRecieveChek = m_service.recieveCheck( strNowDate, token );
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			map.put("err_message", ew.getMessage(e));
			return new ResponseEntity<Map<String,String>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		log.info(user_id + " , "+ room_id);
		log.info(mapper.writeValueAsString(rsltRecieveChek));
	
		// 0. 없는 거래 확인
		if( rsltRecieveChek.getRoom_id() == null ) {
			map.put("message", "존재하지 않는 토큰 값입니다.");
			response = new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);
			return response;
		}
		
		// 1. 같은 방 인원인지 검증
		if( rsltRecieveChek.getRoom_id().equals(room_id) == false ) {
			map.put("message", "같은 방의 뿌리기만 받을 수 있습니다.");
			response = new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);
			return response;
		}
		// 2. 뿌린사람은 못받음
		if( rsltRecieveChek.getSnd_id().equals(user_id) == true ) {
			map.put("message", "뿌린 사람은 받을 수 없습니다.");
			response = new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);
			return response;
		}
		// 3. 10분 이상건에는 실패
		Date nowDateTime = new Date();
		SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String strNowDateTime = simpleDateTimeFormat.format(nowDateTime);
		
		if( rsltRecieveChek.getRegi_dt_10_min().compareToIgnoreCase( strNowDateTime ) < 0 ) {
			map.put("message", "10분이 지난 뿌리기 건은 받을 수 없습니다.");
			response = new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);
			return response;
		}
		
		// 4. 한번만 받을 수 있음 (trans DB check)
		int rsltAlreadyRecieveChek;
		try {
			rsltAlreadyRecieveChek = t_service.transCheckAlreadyRecieve(strNowDate, token, user_id);
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			map.put("err_message", ew.getMessage(e));
			return new ResponseEntity<Map<String,String>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		if( rsltAlreadyRecieveChek > 0 ) {
			map.put("message", "이미 받은 뿌리기건 입니다.");
			response = new ResponseEntity<Map<String, String>>(map, HttpStatus.BAD_REQUEST);
			return response;
		}
		
		// 5. trans update
		TransPayDao updTransPay = new TransPayDao();
		updTransPay.setDeal_ymd(strNowDate);
		updTransPay.setUser_id(user_id);
		updTransPay.setToken(token);
		updTransPay.setRoom_id(room_id);
		
		try {
			int rsltUpdTransPay = t_service.updateTransRecieve( updTransPay );
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			map.put("err_message", ew.getMessage(e));
			return new ResponseEntity<Map<String,String>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 6. 받은 금액 추출
		int rsltRecieveMoney;
		try {
			rsltRecieveMoney = t_service.selectRecieveMoney(strNowDate, token, user_id);
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			map.put("err_message", ew.getMessage(e));
			return new ResponseEntity<Map<String,String>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		// 7. master update
		MasterPayDao updMasterPay = new MasterPayDao();
		updMasterPay.setDeal_ymd(strNowDate);
		updMasterPay.setToken(token);
		updMasterPay.setRcv_money(rsltRecieveMoney);
		updMasterPay.setUser_id(user_id);
		
		try {
			int rsltUpdMasterPay = m_service.updateMaster( updMasterPay );
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			map.put("err_message", ew.getMessage(e));
			return new ResponseEntity<Map<String,String>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		
		// 받기 성공
		map.put("recieveMoney", String.valueOf(rsltRecieveMoney));
		map.put("message", "받기 완료" );
		
		response = new ResponseEntity<Map<String, String>>(map, HttpStatus.CREATED);
		return response;
	}
	
}

