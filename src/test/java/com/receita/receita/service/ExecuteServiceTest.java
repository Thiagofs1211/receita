package com.receita.receita.service;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import com.receita.receita.dto.Receita;

@RunWith(MockitoJUnitRunner.class)
public class ExecuteServiceTest {

	@InjectMocks
	ExecuteService service;
	
	@Mock
	ReceitaService receitaService;
	
	@Test
	public void executar() throws RuntimeException, InterruptedException {
		Receita receita = new Receita();
		receita.setAgencia("1111");
		receita.setConta("11111-1");
		receita.setResultado("Processado com Sucesso");
		receita.setSaldo("100,00");
		receita.setStatus("A");
		
		List<Receita> receitas = new ArrayList<>();
		receitas.add(receita);
		
		Mockito.when(receitaService.atualizarConta(Mockito.anyString(), Mockito.anyString(), Mockito.anyDouble(), Mockito.anyString()))
			.thenReturn(true);
		
		assertNotNull(service.executar(receitas));
	}
}
