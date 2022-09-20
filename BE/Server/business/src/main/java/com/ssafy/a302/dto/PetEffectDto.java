package com.ssafy.a302.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PetEffectDto {
	private int petEffectNo;
	private PetDto pet;
	private EffectDto effect;
}
