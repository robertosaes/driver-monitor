package com.bytesw.tyu.drivers.comcel.task;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.bytesw.tyu.drivers.comcel.obj.Ordenes;
import com.bytesw.tyu.drivers.comcel.obj.Monitor;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ScheduledTask {
//    @NonNull
//    private QueueManager<Queue> queueManager;
    @NonNull
    private Monitor              monitor;

	@Scheduled(fixedRateString ="${app.timeout}", initialDelay = 10000)
	public void run() {
		
		System.out.println("["+System.currentTimeMillis()+"] Recuperando OAS");

		List<Ordenes> ordenes = monitor.getOrdenes();
				
		if (ordenes != null) {
			ordenes.forEach(orden -> monitor.monitorear(orden));
		}
		
	}
}
