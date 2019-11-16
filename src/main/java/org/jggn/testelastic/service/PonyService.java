package org.jggn.testelastic.service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

	public void saveAll(List<Pony> ponies) throws IOException {
		ponyRepository.saveAll(ponies);
	}


	public Slice<Pony> getAllByType(Pageable p, EnumType type) {
		Instant i1 = Instant.now();
		CompletableFuture<Long> future = ponyRepository.countByType(type);
		Instant i1p = Instant.now();
		Long l = null;
		try {
			l = future.get(100, TimeUnit.MILLISECONDS);
		} catch (InterruptedException e) {
		} catch (ExecutionException e) {
		} catch (TimeoutException e) {
		}
		Instant i2 = Instant.now();
		Slice<Pony> poniesPage = ponyRepository.findAllByType(p, type);
		if (l != null) {
			return new PageImpl<>(poniesPage.getContent(), poniesPage.getPageable(), l);
		}
		return poniesPage;
	}

}
