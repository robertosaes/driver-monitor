package com.bytesw.tyu.drivers.comcel.obj;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RabbitMQMessage {
	
	private Long msisdn;
	private Long contrato;
	private String tipo;
	private Long nroorden;
	private Integer coodigosuplem;
	private String resultado;
	private String timestamp;
	
	@Override
	public String toString() {
		return "{ " +
				" \"msisdn\":" + msisdn + 
				", \"contrato\":" + contrato + 
				", \"tipo\":\"" + tipo + "\"" +
				", \"nroorden\":" + nroorden + 
				", \"coodigosuplem\":" + coodigosuplem + 
				", \"resultado\":\"" + resultado + "\"" + 
				", \"timestamp\":\"" + timestamp + "\"" +
				"}";
	}
	
}
