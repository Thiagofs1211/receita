package com.receita.receita;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.context.SpringBootTest;

import com.receita.receita.dto.Receita;
import com.receita.receita.service.ExecuteService;

@SpringBootTest
@RunWith(MockitoJUnitRunner.class)
class ReceitaApplicationTests {

	@InjectMocks
	ReceitaApplication service;
	
	@Mock
	ExecuteService executeService;
	
	@Test
	void contextLoads() {
	}
	
	@Test
	public void processarReceitas() throws FileNotFoundException, InterruptedException, ExecutionException {
		
		Receita receita = new Receita();
		receita.setAgencia("1111");
		receita.setConta("11111-1");
		receita.setResultado("Processado com Sucesso");
		receita.setSaldo("100,00");
		receita.setStatus("A");
		
		List<Receita> receitas = new ArrayList<>();
		receitas.add(receita);
		
		CompletableFuture<List<Receita>> lista = CompletableFuture.completedFuture(receitas);
		
		//Caminho do arquivo teste
		Mockito.when(new BufferedReader(Mockito.any())).thenReturn(new BufferedReader(new FileReader("C:\\Users\\Thiago\\Desktop\\receita.csv")));
		Mockito.when(executeService.executar(Mockito.anyList())).thenReturn(lista);
		
		service.run(Mockito.anyString());
	}

	@Test
	public void garvarArquivo() throws IOException {
		Receita receita = new Receita();
		receita.setAgencia("1111");
		receita.setConta("11111-1");
		receita.setResultado("Processado com Sucesso");
		receita.setSaldo("100,00");
		receita.setStatus("A");
		
		List<Receita> receitas = new ArrayList<>();
		receitas.add(receita);
		
		List<List<Receita>> listasReceitas = new ArrayList<>();
		listasReceitas.add(receitas);
		
		//Caminho do arquivo teste
		Mockito.when(new FileWriter(new File(Mockito.anyString()))).thenReturn(new FileWriter(new File("C:\\Users\\Thiago\\Desktop\\tmp\\receita_processada.csv")));
		
		service.gravarArquivo(listasReceitas);
	}
}
