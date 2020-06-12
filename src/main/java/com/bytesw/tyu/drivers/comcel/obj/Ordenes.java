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
public class Ordenes {

	@NotNull
	private Long numeroOrden;
	@NotNull
	private String msisdn;
	@NotNull
	private Long centroCostos;
	@NotNull
	private String orden;
	@NotNull
	private String tipoOa;
	@NotNull
	private String codigoServicio;
	@NotNull
	private Double valorServicio;

}
