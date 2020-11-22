package kr.co.kakaopay.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/query")
public class QueryOwnerController {

	private ObjectMapper mapper = new ObjectMapper();
	@Autowired
	private MasterPayService m_service;
	@Autowired
	private TransPayService t_service;
	@Autowired
	private ErrorWriter ew;
	
	@GetMapping
	public ResponseEntity<Map<String, Object>> recieve(@RequestHeader(value = "X-USER-ID") String user_id,
			@RequestHeader("X-ROOM-ID") String room_id, @RequestParam(value = "token") String token)
			throws JsonProcessingException {

		ResponseEntity<Map<String, Object>> response = null;
		Map< String, Object> map = new HashMap<>();
		
		Date nowDate = new Date();
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
		String strNowDate = simpleDateFormat.format(nowDate);
		
		
		// 1. 본인 검증
		
		MasterPayDao rsltQueryOwner;
		try {
			rsltQueryOwner = m_service.queryOwnerMaster(strNowDate, token);
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			map.put("err_message", ew.getMessage(e));
			return new ResponseEntity<Map<String,Object>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if( rsltQueryOwner == null ) {
			map.put("message", "최근 7일간내용만 조회가능합니다.");
			response = new ResponseEntity<Map<String,Object>>(map,HttpStatus.BAD_REQUEST);
			return response;
		}
		
		String sndId = rsltQueryOwner.getSnd_id();
		
		if( sndId.equals(user_id) == false) {
			map.put("message", "본인의 뿌리기건만 조회가능합니다.");
			response = new ResponseEntity<Map<String,Object>>(map,HttpStatus.BAD_REQUEST);
			return response;
		}
		
		// 2. Single Output 세팅 (master table)
		
		
	    log.info(rsltQueryOwner.getRegi_time() + " " + rsltQueryOwner.getTotal_money() + " " + rsltQueryOwner.getRcv_money());
		
	    // 3. Multi Output 세팅 (trans table)
		
		List<TransPayDao> list;
		try {
			list = t_service.queryOwnerTrans(strNowDate, token);
		} catch (Exception e) {
			log.error(ew.getMessage(e));
			map.put("err_message", ew.getMessage(e));
			return new ResponseEntity<Map<String,Object>>(map,HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 조회 성공
		map.put("message", "조회 성공");
		map.put("regiTime", rsltQueryOwner.getRegi_time());
		map.put("sndTotalMoney", rsltQueryOwner.getTotal_money());
		map.put("nowRecieveMoney", rsltQueryOwner.getRcv_money());
		map.put("sndList", list);
		response = new ResponseEntity<Map<String,Object>>(map,HttpStatus.OK);
		return response;

	}
}
