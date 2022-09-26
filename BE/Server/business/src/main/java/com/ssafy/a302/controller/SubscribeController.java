package com.ssafy.a302.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ssafy.a302.serviceImpl.SubscriptionServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ssafy.a302.response.PetRes;

@Api(value="구독 컨트롤러")
@RestController
@RequestMapping(value="subscription", produces = "application/json; charset=utf8")
@RequiredArgsConstructor
public class SubscribeController {
	private final SubscriptionServiceImpl subscriptionServiceImpl;
	@ApiOperation(value = "현재 구독정보가져오기")
	@GetMapping("/now/{userId}") // 수정 예정
	public List<Map<String, Object>> getSubInfoNow(@PathVariable("userId") String userId){
		return subscriptionServiceImpl.getSubInfo(userId, 0);
	}

	@ApiOperation(value = "모든 구독정보가져오기")
	@GetMapping("/all/{userId}") // 수정 예정
	public List<Map<String, Object>> getSubInfoAll(@PathVariable("userId") String userId){
		return subscriptionServiceImpl.getSubInfo(userId, 1);
	}

	@ApiOperation(value = "구독 취소하기")
	@PatchMapping("/{subId}") // 수정 예정
	public String updateSubInfoAsCanceled(@PathVariable("subId") int subId){
		subscriptionServiceImpl.updateSubInfoAsCanceled(subId);
		return "updated";
	}

}
