package com.receita.receita.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.receita.receita.dto.Receita;

@Service
public class ExecuteService {
	
	private static final Logger log = LoggerFactory.getLogger(ExecuteService.class);
	
	@Async("execute")
	public CompletableFuture<List<Receita>> executar(List<Receita> receitas) {
		ReceitaService service = new ReceitaService();
		receitas.stream().forEach(receita -> {
			try {
				if(receita.getSaldo() == null) {
					receita.setResultado("Informações Inválidas");
				}
				if(service.atualizarConta(receita.getAgencia(), receita.getConta().replace("-", ""),
						Double.parseDouble(receita.getSaldo().replace(",", ".")), receita.getStatus())) {
					receita.setResultado("Processado com Sucesso");
				} else {
					receita.setResultado("Informações Inválidas");
				}
			} catch (RuntimeException e) {
				log.error("Erro no processamento: " + e.getMessage());
			} catch (InterruptedException e) {
				log.error("Erro no processamento: " + e.getMessage());
			}
		});
		return CompletableFuture.completedFuture(receitas);
	}
}
