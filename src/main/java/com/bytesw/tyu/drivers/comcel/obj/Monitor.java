package com.bytesw.tyu.drivers.comcel.obj;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.bytesw.tyu.drivers.comcel.config.Properties;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Monitor {
    @NonNull
    private JdbcTemplate        jdbcTemplate;
    @NonNull
    private Properties properties;
    
    private SimpleDateFormat sdf = new SimpleDateFormat("HHmmss");
  
    private SimpleDateFormat sdf1 = new SimpleDateFormat("ddMMyyyy HHmmss");

	@Autowired
	private AmqpTemplate amqpTemplate;
	
	@Value("${jsa.rabbitmq.exchange}")
	private String exchange;
	
	@Value("${jsa.rabbitmq.routingkey}")
	private String routingkey;

	// Declared queries
    private static final String GET_OAS = "SELECT O1NORD, TCNTEL, TCNCON, O1TITR, TCTIPO, TCSERV, TCVSER " + 
    										" FROM V1PPCTRL " + 
											" WHERE TCPROC = 'N'";
    
    private static final String GET_O1OASLOG = "SELECT B.O1TXT, C.O1ACTO " + 
    		"  FROM O1ORDS01 A, " + 
    		"  O1OASLOG B " + 
    		"  LEFT JOIN O1ADMM98 C ON (C.O1MERE = B.O1TXT )" + 
    		"  WHERE A.O1NORD = ? " + 
    		"  AND A.O1ENVI = 'S' " + 
    		"  AND A.O1STAT = 'N' " + 
    		"  AND B.O1NORD = A.O1NORD " + 
    		"  AND B.O1TITR = A.O1TITR " + 
    		"  AND B.O1SOR = 'R' " + 
    		" ORDER BY B.LOAZO DESC, B.LOMES DESC, B.LODIA DESC, B.LOTIME DESC " + 
    		" FETCH FIRST 1 ROW ONLY";
    
    private static final String UPDATE_V1PPCTRL = "UPDATE V1PPCTRL " + 
    		"SET TCRESP = ?, " + 
    		"TCPROC = 'S', " + 
    		"LOAZO = ?, " + 
    		"LOMES = ?, " + 
    		"LODIA = ?, " + 
    		"LOTIME = ? " + 
    		"WHERE O1NORD = ?"; 

    private static final String INSERT_V1SERDET  = "INSERT INTO V1SERDET(" +
    " LOCCOD, SULOCO, TCNTEL, TCSERV, TCVSER, TCSMOU, TCASES, TCMSES, TCDSES, TCCVEN, TCDAES, TCDMES, TCDDES, TCCVDI) " + 
	" VALUES(1, 1, ?, ?, ?, 'M', ?, ?, ?, ?, 0, 0, 0, ?)";    

    private static final String INSERT_L1SERDET = "INSERT INTO L1SERDET("
    		+ "LOCCOD, SULOCO, TCNTEL, TCSERV, TCVSER, TCSMOU, TCASES, TCMSES, TCDSES, TCCVEN, TCDAES, TCDMES, "
    		+ "TCDDES, TCCVDI, LOUSER, LODSP, LODIA, LOMES, LOAÑO, LOTIME, LOTRX, LOPGM, TRXCOD) " + 
    		"  VALUES(1, 1, ?, ?, ?, 'M', 0, 0, 0, ?, ?, ?, ?, ?, 'DRIVERJAVA', 'DRVRONETV2', ?, ?, ?, ?, ?, 'DRVRONETV2', ' ')";
    
    private static final String DELETE_V1SERDET = "DELETE FROM V1SERDET WHERE TCNTEL = ? AND TCSERV = ?";
    
    // Declared methods
    public List<Ordenes> getOrdenes() {
        try {
            return jdbcTemplate.query(
            		GET_OAS,
            		(RowMapper<Ordenes>) (rs, rowNum) -> Ordenes.builder()
                            .numeroOrden(rs.getLong("O1NORD"))
                            .msisdn(rs.getString("TCNTEL"))
                            .centroCostos(rs.getLong("TCNCON"))
                            .orden(rs.getString("O1TITR"))
                            .tipoOa(rs.getString("TCTIPO"))
                            .codigoServicio(rs.getString("TCSERV"))
                            .valorServicio(rs.getDouble("TCVSER"))
                            .build());
        }
        catch (Exception e) {
            System.out.println(e.toString());
        }
        return null;
    }

    public void monitorear(Ordenes orden) {
    	
    	Calendar calendar = Calendar.getInstance();
		
		int anho = calendar.get(Calendar.YEAR);
		int mes = calendar.get(Calendar.MONTH) + 1;
		int dia = calendar.get(Calendar.DATE);
		String tiempo = sdf.format(calendar.getTime());
		boolean update = false;
		
		try {
			System.out.println(GET_O1OASLOG);
			O1oaslog o1oaslog = jdbcTemplate.queryForObject(
					GET_O1OASLOG, 
					new Object[] { orden.getNumeroOrden() },
					 (rs, rowNum) -> O1oaslog.builder()
					 .o1txt(rs.getString("O1TXT"))
					 .o1acto(rs.getString("O1ACTO"))
					 .build());
			
			if (o1oaslog.getO1txt() != null) {
				update = 
						jdbcTemplate.update(
								UPDATE_V1PPCTRL, 
						new Object[] { 
									o1oaslog.getO1txt().trim(),
									anho,
									mes,
									dia,
									tiempo,
									orden.getNumeroOrden()}) > 0;
									
				if ("P".equalsIgnoreCase(o1oaslog.getO1acto().trim())) {
					
					calendar = Calendar.getInstance();
					anho = calendar.get(Calendar.YEAR);
					mes = calendar.get(Calendar.MONTH) + 1;
					dia = calendar.get(Calendar.DATE);
					tiempo = sdf.format(calendar.getTime());
					
					if ("alta".equalsIgnoreCase(orden.getTipoOa())) {
						boolean insertV1serdet = jdbcTemplate.update(
						INSERT_V1SERDET, 
						new Object[] { orden.getMsisdn().trim(), 
									orden.getCodigoServicio(),
									orden.getValorServicio(),
									anho,
									mes,
									dia,
									properties.getVendorCode(),
									properties.getDistributorCode()}) > 0;
					} else {
						boolean deleteV1serdet = jdbcTemplate.update(
								DELETE_V1SERDET, 
								new Object[] { orden.getMsisdn(), 
											orden.getCodigoServicio()}) > 0;
					}

					calendar = Calendar.getInstance();
					anho = calendar.get(Calendar.YEAR);
					mes = calendar.get(Calendar.MONTH) + 1;
					dia = calendar.get(Calendar.DATE);
					tiempo = sdf.format(calendar.getTime());

					boolean insertL1serdet = jdbcTemplate.update(
							INSERT_L1SERDET, 
							new Object[] { orden.getMsisdn().trim(), 
										orden.getCodigoServicio(),
										orden.getValorServicio(),
										properties.getVendorCode(),
										anho,
										mes,
										dia,
										properties.getDistributorCode(),
										dia,
										mes,
										anho,
										tiempo,
										"alta".equalsIgnoreCase(orden.getTipoOa())?"AN":("baja".equalsIgnoreCase(orden.getTipoOa())?"BS":" ")}) > 0;

				} 
				
				
				RabbitMQMessage message = RabbitMQMessage.builder()
						.msisdn(Long.parseLong(orden.getMsisdn().trim()))
						.contrato(orden.getCentroCostos())
						.tipo(orden.getTipoOa())
						.nroorden(orden.getNumeroOrden())
						.coodigosuplem(Integer.parseInt(orden.getCodigoServicio().trim()))
						.resultado(o1oaslog.getO1txt().trim())
						.timestamp(sdf1.format(calendar.getTime()))
						.build();

				System.out.println("Send msg = " + message.toString());
				amqpTemplate.convertAndSend(exchange, 
											routingkey, 
											message.toString());
				
				
			} else {
				System.out.println("No existe el registro en O1OASLOG numeroOrden["+orden.getNumeroOrden()+"]");
			}
		} catch (EmptyResultDataAccessException erdae) {
			System.out.println("No existe el registro en O1OASLOG numeroOrden["+orden.getNumeroOrden()+"]");

		}
		catch (Exception e) {
			e.printStackTrace();
		}
    	
    }
        
    public void getRabbitMqMessage() {
    	
    	Calendar calendar = Calendar.getInstance();
    	System.out.println("Send msg = " + RabbitMQMessage.builder()
		.msisdn(12345678l)
		.contrato(123456l)
		.tipo("alta")
		.nroorden(9800023233l)
		.coodigosuplem(195)
		.resultado("success")
		.timestamp(sdf1.format(calendar.getTime()))
		.build().toString());
    	System.out.println("exchange["+exchange+"]");
    	
		amqpTemplate.convertAndSend(exchange, 
				routingkey, 
				RabbitMQMessage.builder()
					.msisdn(12345678l)
					.build().toString());
		
		System.out.println("routingKey["+routingkey+"]");
    }

}
