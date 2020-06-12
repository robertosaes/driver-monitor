package com.bytesw.tyu.drivers.comcel.obj;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class O1oaslog {

	@NotNull
	private String o1txt;
	@NotNull
	private String o1acto;

}
