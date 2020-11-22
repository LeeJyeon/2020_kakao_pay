package kr.co.kakaopay.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.kakaopay.domain.MasterPayDao;
import kr.co.kakaopay.domain.SprinkleInputBody;
import kr.co.kakaopay.domain.TransPayDao;
import kr.co.kakaopay.service.MasterPayService;
import kr.co.kakaopay.service.TransPayService;
import kr.co.kakaopay.util.ErrorWriter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/sprinkle")
public class SprinkleMoneyController {

	@Autowired
	private MasterPayService m_service;
	@Autowired
	private TransPayService t_service;
	@Autowired
	private ErrorWriter ew;
	
	private ObjectMapper mapper = new ObjectMapper();

	@PostMapping
	public ResponseEntity<String> sprinkle(@RequestHeader(value = "X-USER-ID") String user_id,
			@RequestHeader("X-ROOM-ID") String room_id, @RequestBody SprinkleInputBody input_body)
			throws JsonProcessingException {

		// 1. token 생성 (3자리 문자 random)
		String token = "";
		for (int i = 0; i < 3; i++) {
			char ch_1 = (char) ((Math.random() * 26) + 65);
			char ch_2 = (char) ((Math.random() * 26) + 97);
			if (Math.random() > 0.5) {
				token += ch_1;
			} else {
				token += ch_2;
			}
		}

		log.info(token);

		// 2. token ( master_pay ) DB 검증

		Date nowDate = new Date();

		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String strNowDate = simpleDateFormat.format(nowDate);

		int rslt;
		try {
			rslt = m_service.verifyToken(strNowDate, token);
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			return new ResponseEntity<String>(ew.getMessage(e),HttpStatus.INTERNAL_SERVER_ERROR);
		}

		while (rslt > 0) {
			token = "";
			for (int i = 0; i < 3; i++) {
				char ch_1 = (char) ((Math.random() * 26) + 65);
				char ch_2 = (char) ((Math.random() * 26) + 97);
				if (Math.random() > 0.5) {
					token += ch_1;
				} else {
					token += ch_2;
				}
			}
		}

		// 3. 분배금액 , 인원수 검증
		ResponseEntity<String> response = null;

		int[] money = new int[input_body.getPeople_num()];
		int left_money = input_body.getTotal_money();
		
		log.info("인원" + input_body.getPeople_num());
		
		if (input_body.getPeople_num() < 1) {
			response = new ResponseEntity<String>("뿌리는 인원값을 확인하세요.", HttpStatus.BAD_REQUEST);
			return response;
		} else if (input_body.getPeople_num() == 1) {
			money[0] = left_money;
		} else {
			if (input_body.getTotal_money() < input_body.getPeople_num() || left_money < 0) {
				response = new ResponseEntity<String>("인원에 비해 뿌리는 금액이 부족합니다.", HttpStatus.BAD_REQUEST);
				return response;
			} else {
				Random rand = new Random();
				for (int i = 0; i < input_body.getPeople_num(); i++) {
					if (i == input_body.getPeople_num() - 1) {
						money[i] = left_money;
					} else {
						money[i] = (int) (left_money * (rand.nextInt(30) + 10) / 100);
						left_money -= money[i];
					}
				}
			}
		}
		
		// 5. master <1건> , trans DB <n건> Insert
		MasterPayDao masterPayDao = new MasterPayDao();
		masterPayDao.setDeal_ymd(strNowDate);
		masterPayDao.setToken(token);
		masterPayDao.setUser_id(user_id);
		masterPayDao.setRoom_id(room_id);

		masterPayDao.setPeople_num(input_body.getPeople_num());
		masterPayDao.setTotal_money(input_body.getTotal_money());
				
		try {
			rslt = m_service.masterFirst( masterPayDao );
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			return new ResponseEntity<String>(ew.getMessage(e),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
        if ( rslt == 1 ) { 
			for( int i = 0 ; i < input_body.getPeople_num(); i ++) {
				TransPayDao transPayDao = new TransPayDao();
				transPayDao.setDeal_ymd(strNowDate);
				transPayDao.setToken(token);
				transPayDao.setUser_id(user_id);
				transPayDao.setRoom_id(room_id);
				transPayDao.setSeq( i + 1 );
				transPayDao.setRcv_money(money[i]);
				transPayDao.setRcv_yn("N");
				
				log.info( i+ "번째 금액 " + money[i]);
				try {
					rslt = t_service.transFirst( transPayDao );
				} catch (Exception e) {
					log.error(ew.getMessage(e));
					return new ResponseEntity<String>(ew.getMessage(e),HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
        }
		
		response = new ResponseEntity<String>("뿌리기 완료", HttpStatus.CREATED);
		return response;
	}
}
