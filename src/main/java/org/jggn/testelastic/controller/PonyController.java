package org.jggn.testelastic.controller;

import java.io.IOException;
import java.time.Instant;
import java.util.List;

import org.jggn.testelastic.configuration.ProcessException;
import org.jggn.testelastic.models.EnumType;
import org.jggn.testelastic.models.Pony;
import org.jggn.testelastic.service.PonyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/ponies")
public class PonyController {

	@Autowired
	PonyService service;

	@GetMapping(value = "/generate")
	public List<Pony> getGeneratedPonies(@RequestParam(required = false, defaultValue = "10") Integer nb,
			@RequestParam(required = false, defaultValue = "false") boolean insert) throws IOException, ProcessException {
		List<Pony> ponies = service.generatePonies(nb);
		if (insert) {
			service.saveAll(ponies);
			return null;
		}
		return ponies;
	}

	/**
	 * Get ponies without spring-data-cassandra (no deserialization)
	 * @param pageable
	 * @param type
	 * @return
	 */
	@GetMapping("/serialized")
	public Slice<Pony> getPoniesSerialized(Pageable pageable, @RequestParam(required = true) EnumType type) {
		Instant i1 = Instant.now();
		Slice<Pony> ponies= service.getAllByType(pageable, type);
		Instant i2 = Instant.now();
		System.out.println("getPoniesSerialized "+(i2.toEpochMilli()-i1.toEpochMilli())+" ms");
		return ponies;
	}
}
