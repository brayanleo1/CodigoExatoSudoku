package principal;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class Grafo {

	private List<List<Vector<Object>>> matriz;
	private List<List<Integer>> lista;
	private List<Integer> restantes;
	
	public Grafo() {
		matriz = new ArrayList<List<Vector<Object>>>();
		lista = new ArrayList<List<Integer>>();
		restantes = new ArrayList<Integer>();
	}
	
	public Grafo(final List<List<Vector<Object>>> matriz, final List<List<Integer>> lista, final List<Integer> restantes) {
		this.matriz = matriz;
		this.lista = lista;
		this.restantes = restantes;
	}
	
	public void setMatriz(List<List<Vector<Object>>> matriz) {
		this.matriz = matriz;
	}
	
	public List<List<Vector<Object>>> getMatriz(){
		return matriz;
	}
	
	public void addToMatriz(List<Vector<Object>> linha) {
		matriz.add(linha);
		restantes.add(linha.size());
	}
	
	public List<List<Integer>> getLista() {
		return lista;
	}
	
	public void atualizaLista() {
		if(lista.isEmpty()) {//Se lista ta vazia, cria ela
			for(int i = 0; i < matriz.size(); i++) {
				for(int j = 0; j < matriz.size(); j++) {
					List<Integer> lugar = new ArrayList<Integer>();
					lugar.add(i); //Linha
					lugar.add(j); //Coluna
					lugar.add((((List<Integer>) matriz.get(i).get(j).get(1)).size()));
					lista.add(lugar);
				}
			}
		} else {
			for(int i = 0; i < matriz.size(); i++) {
				for(int j = 0; j < matriz.size(); j++) {
					lista.get((i*matriz.size())+j).set(2, ((((List<Integer>) matriz.get(i).get(j).get(1)).size())));
				}
			}
		}	
	}
	
	public void atualizaRestante(Integer cor) {
		Integer valorAnterior = restantes.get((cor-1));
		restantes.set((cor-1), (valorAnterior-1));
	}
	
	public void atualizaRestantes() {
		for(int i = 0; i < matriz.size(); i++) {
			restantes.add(matriz.size());
		}
		
		for(int i = 0; i < matriz.size(); i++) {
			for(int j = 0; j < matriz.size(); j++) {
				Integer cor = (Integer) matriz.get(i).get(j).get(0);
				if(cor != 0) {
					atualizaRestante(cor);
				}
			}
		}
	}
	
	public boolean isVazio() {
		boolean eh = true;
		for(Integer i : restantes) {
			if(i != 0) {
				eh = false;
				break;
			}
		}
		return eh;
	}
	
	public Integer getRestanteCor(Integer cor) {
		return restantes.get(cor - 1);
	}
	
	public List<Integer> getRestantes() {
		return restantes;
	}
	
	public void setLista(List<List<Integer>> lista) {
		this.lista = lista;
	}
	
	public void setRestantes(List<Integer> restantes) {
		this.restantes = restantes;
	}
		
	public Grafo copyMatriz(){
		Grafo nG = new Grafo();
		List<List<Vector<Object>>> copia = new ArrayList<List<Vector<Object>>>();
		for(int i = 0; i < matriz.size(); i++) {
			List<Vector<Object>> cl = new ArrayList<Vector<Object>>();
			for(int j = 0; j < matriz.size(); j++) {
				Vector<Object> co = new Vector<Object>();
				co.add(Integer.parseInt((String) matriz.get(i).get(j).get(0).toString()));
				List<Integer> co2 = new ArrayList<Integer>();
				for(Integer it : ((List<Integer>) matriz.get(i).get(j).get(1))) {
					co2.add(Integer.parseInt(it.toString()));
				}
				co.add(co2);
				cl.add(co);
			}
			copia.add(cl);
		}
		nG.setMatriz(copia);
		
		List<List<Integer>> listaCop = new ArrayList<List<Integer>>();
		for(int i =0; i<lista.size(); i++) {
			List<Integer> tuplaCop = new ArrayList<Integer>();
			for(int j =0; j<3; j++) {
				tuplaCop.add(Integer.parseInt(lista.get(i).get(j).toString()));
			}
			listaCop.add(tuplaCop);
		}
		
		nG.setLista(listaCop);
		
		List<Integer> restCop = new ArrayList<Integer>();
		for(int i =0; i<restantes.size(); i++) {
			restCop.add(Integer.parseInt(restantes.get(i).toString()));
		}
		
		nG.setRestantes(restCop);
		return nG;
	}
}
