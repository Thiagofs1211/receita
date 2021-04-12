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
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.opencsv.CSVWriter;
import com.receita.receita.dto.Receita;
import com.receita.receita.service.ExecuteService;

@SpringBootApplication
@EnableAsync
public class ReceitaApplication implements CommandLineRunner {
	
	private final static Integer NUM_THREADS = 10;
	
	private static final Logger log = LoggerFactory.getLogger(ReceitaApplication.class);
	
	@Autowired
	private ExecuteService service;
	
	@Bean(name = "execute")
	public Executor execute() {
		final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(1000);
        executor.initialize();
        return executor;
	}
	
	public static void main(String[] args) throws InterruptedException {
		SpringApplication.run(ReceitaApplication.class, args);
	}
	
	@Override
	public void run(String...args) {
	    
		log.info("Começando a leitura do arquivo CSV");
		
		//Caminho do arquivo a ser processado
		String arquivoCSV = "C:\\Users\\Thiago\\Desktop\\receita.csv";
	    BufferedReader br = null;
	    String linha = "";
	    String csvDivisor = ";";
	    List<Receita> receitas = new ArrayList<>();
	    
	    try {
	    	br = new BufferedReader(new FileReader(arquivoCSV));
	    	linha = br.readLine();
	        while ((linha = br.readLine()) != null) {
	            String[] receita = linha.split(csvDivisor);
	            Receita receitaDto = new Receita();
	            receitaDto.setAgencia(receita[receita.length-4].replace("\"", ""));
	            receitaDto.setConta(receita[receita.length-3]);
	            receitaDto.setSaldo(receita[receita.length-2]);
	            receitaDto.setStatus(receita[receita.length-1].replace("\"", ""));
	            receitas.add(receitaDto);
	        }
	    } catch (FileNotFoundException e) {
	        log.error("Arquivo CSV não encontrado");
	    } catch (IOException e) {
	        log.error("Falha ao ler arquivo CSV");
	    } finally {
	        if (br != null) {
	            try {
	                br.close();
	            } catch (IOException e) {
	                log.error("Falha ao fechar arquivo");
	            }
	        }
		    log.info("Término da leitura do arquivo CSV");
	    }
		
	    List<List<Receita>> listas = new ArrayList<>();
	    int quantidadeLista = receitas.size()/NUM_THREADS;
	    for(int i = 1; i <= NUM_THREADS; i ++) {
	    	listas.add(receitas.subList((i-1)*quantidadeLista, i*quantidadeLista));
	    }
    	CompletableFuture<List<Receita>> page1 = service.executar(listas.get(0));
    	CompletableFuture<List<Receita>> page2 = service.executar(listas.get(1));
    	CompletableFuture<List<Receita>> page3= service.executar(listas.get(2));
    	CompletableFuture<List<Receita>> page4 = service.executar(listas.get(3));
    	CompletableFuture<List<Receita>> page5 = service.executar(listas.get(4));
    	CompletableFuture<List<Receita>> page6 = service.executar(listas.get(5));
    	CompletableFuture<List<Receita>> page7 = service.executar(listas.get(6));
    	CompletableFuture<List<Receita>> page8 = service.executar(listas.get(7));
    	CompletableFuture<List<Receita>> page9 = service.executar(listas.get(8));
    	CompletableFuture<List<Receita>> page10 = service.executar(listas.get(9));
    	CompletableFuture.allOf(page1, page2, page3, page4, page5,
    			page6, page7, page8, page9, page10).join();
    	
    	gravarArquivo(listas);
	}
	
	@SuppressWarnings("resource")
	public void gravarArquivo(List<List<Receita>> listaReceitas) {
		
		log.info("Começando criação do arquivo CSV de saída");
		
		//Caminho de saída do arquivo gerado
		try {
			FileWriter fw = new FileWriter(new File("C:\\Users\\Thiago\\Desktop\\tmp\\receita_processada.csv"));
			CSVWriter cw = new CSVWriter(fw);
			
			List<String[]> dados = new ArrayList<>();
			String[] headers = {"agencia;conta;saldo;status;resultado"};
			
			dados.add(headers);
			
			for(List<Receita> lista : listaReceitas) {
				for(Receita receita: lista) {
					String[] linha = {receita.getAgencia() + ";" + receita.getConta() + ";" + receita.getSaldo()
					+ ";" + receita.getStatus() + ";" + receita.getResultado()};
					dados.add(linha);
				}
			}
			
			cw.writeAll(dados);
			cw.flush();
			fw.close();
			log.info("Término da criação do arquivo CSV de saída");
		} catch (IOException e) {
			log.error("Falha na criação do arquivo de saída");
		}
	}
}
