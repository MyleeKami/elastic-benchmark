package org.jggn.testelastic.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.jggn.testelastic.configuration.ProcessException;
import org.jggn.testelastic.models.EnumType;
import org.jggn.testelastic.models.Pony;
import org.jggn.testelastic.repository.PonyRepository;
import org.jggn.testelastic.utils.PonyGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

@Service
public class PonyService {

	@Autowired
	PonyGenerator ponyGenerator;
	@Autowired
	PonyRepository ponyRepository;

	public List<Pony> generatePonies(Integer nb) {
		return ponyGenerator.generatePonies(nb);
	}

	public void saveAll(List<Pony> ponies) throws IOException, ProcessException {
		ponyRepository.saveAll(ponies);
	}


	public Slice<Pony> getAllByType(Pageable p, EnumType type) throws IOException {
		
		Slice<Pony> poniesPage = ponyRepository.findAllByType(p, type);		
		return poniesPage;
	}

}
