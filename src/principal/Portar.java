package principal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;


public class Portar {
	
	public static Grafo resGrafo (Grafo grafo) {
		Stack<Grafo> pilha = new Stack<Grafo>();
		grafo.atualizaLista(); //Para criar a lista
		while(true) {
			List<List<Integer>> lista = new ArrayList<List<Integer>>(grafo.getLista());
			lista.sort(Comparator.comparing(l -> l.get(2)));
			int id = 0;
			while((Integer) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(0) != 0) {//Enquanto for um vértice colorido
				id+=1;
				if(id == grafo.getMatriz().size() * grafo.getMatriz().size()) {
					return grafo;
				}
			}
			if(lista.get(id).get(2) == 0) {//Se não houverem possibilidades para o vértice descolorido com menores possibilidades (0) para o vertice descolorido então volta
				grafo = pilha.pop();//Pegue o último elemento da pilha
			} else {//Existem pelo menos uma possibilidade de coloração
				if(lista.get(id).get(2) > 1) {//Se existem mais
					//Escolhemos uma cor
					int cor = 0;
					for(Integer pCor : ((List<Integer>) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1))) {
						if(cor == 0) {//Se for a cor 0
							cor = pCor; //Só atualiza
						} else if(grafo.getRestanteCor(pCor) < grafo.getRestanteCor(cor)) {//Se não for e a nova cor tiver menos usos restantes que a atual
							cor = pCor; //Atualize pois são maiores as chances de se der ruim na coloração, dar ruim logo
						}
					}
					((List<Integer>) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1)).remove((Integer) cor); //Remova essa cor das possibilidades
					grafo.atualizaLista();
					pilha.push(grafo.copyMatriz()); //Adicione o grafo sem a possibilidade da cor a ser escolhida
					grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).set(0, cor);//Colore com a cor pega
					attVizinhos(lista.get(id).get(0), lista.get(id).get(1),grafo.getMatriz().size(),grafo, cor);//Atualize as cores dos vizinhos, desde que dele mesmo já foi removido
				} else {//Só existe uma cor, escolha ela e pronto
					Integer cor = ((List<Integer>) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1)).get(0);//Pega a primeira cor
					grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).set(0, cor);//Colore com a cor pega
					attVizinhos(lista.get(id).get(0), lista.get(id).get(1),grafo.getMatriz().size(),grafo, cor);//Atualize as cores dos vizinhos e dele mesmo
				}
			}
		}
	}
	
	//n = dimensão do sudoku nxn, lin = número de instâncias de sudoku a serem lisdas do arquivo
	public static List<Grafo> criaGrafos(String pos, int n, int lin) { //Recebe String indicando posição/nome do arquivo e então lê e cria a array de matrizes nxn do arquivo
	
	   //Ler arquivo = Criar array de strings
	   List<String> linhas = new ArrayList<String>();
	   for(int i = 0; i < lin; i++) {
		   try {
			linhas.addAll(Files.readAllLines(Paths.get(pos)));
		} catch (IOException e) {
			e.printStackTrace();
		}
	   }
	   
	   List<Integer> poss0 = new ArrayList<Integer>();
	   List<Grafo> vetMat = new ArrayList<Grafo>();
	   
	   //Cria todos os "lin" grafos
	   for (int i = 0; i < lin; i++) {
	   	Grafo vetCol = new Grafo(); //Vetor de colunas, que guarda em cada posição as linhas
	   	for (int x = 0; x < n; x++) {
	   		List<Vector<Object>> vetLin = new ArrayList<Vector<Object>>(); //Vetor de linhas, que em cada posição guarda vetores com cor e possibilidades
	   		for (int y = 0; y < n; y++) {
	   				Vector<Object> vetPos = new Vector<Object>(); //Vetor de cada posição, que guarda cor e possibilidades
	    			Integer cor = Integer.parseInt(linhas.get(i).split(",")[x+(y*n)]);
	    			vetPos.add(0, cor);
	    			if(cor == 0) {
	    				List<Integer> possN = new ArrayList<Integer>(); //para quando houver n possibilidades
	    				for(int aux = 1; aux <= n; aux++) {
	    					possN.add(aux);
	    				}
	    				vetPos.add(1, possN);
	    			} else {
	    				vetPos.add(1, poss0);
	    			}
	    			vetLin.add(vetPos);
	    		}
	   		vetCol.addToMatriz(vetLin);
	    	}
	    	vetMat.add(vetCol);
	   }
	   
	   //Atualiza todas as possibilidades dos "lin" grafos
	   for (int i = 0; i < lin; i++) {
		   	for (int x = 0; x < n; x++) {
		   		for (int y = 0; y < n; y++) {
		    		Integer cor = (Integer) vetMat.get(i).getMatriz().get(x).get(y).get(0);
		    		if(cor != 0) {
		    			attVizinhos(x, y, n, vetMat.get(i), cor);
		    		}
		    	}
		    }
		}
	  return vetMat;
	 }

	public static void imprimaSudoku(int n,Grafo grafo) {
		for (int x = 0; x < n; x++) {
			for (int y = 0; y < n; y++) {
				System.out.print(grafo.getMatriz().get(x).get(y).get(0)+""+grafo.getMatriz().get(x).get(y).get(1));
				if(y != (n-1)) {
					System.out.print("|");
				} else {
					System.out.println();
				}
			}
			for(int i = 0; i < (2*n); i++) {
				System.out.print("_");
			}
			System.out.println();
		}
	}
	
	public static void attVizinhos(int x, int y, int n, Grafo grafo, Integer cor) {
		((List<Integer>) grafo.getMatriz().get(x).get(y).get(1)).clear();
		
		//Linha e coluna
		for(int i = 0; i < grafo.getMatriz().size(); i++) {
			//Remove da lista de possibilidades se houver tal cor na lista
			((List<Integer>) grafo.getMatriz().get(i).get(y).get(1)).remove((Integer) cor);
			((List<Integer>) grafo.getMatriz().get(x).get(i).get(1)).remove((Integer) cor);
		}
		
		//Adiciona linhas e colunas daquela grade específica
		List<Integer> linhas = new ArrayList<Integer>();
		List<Integer> colunas = new ArrayList<Integer>();
		int aux = 0;
		int numl = 0;
		int numc = 0;
		while(aux < Math.sqrt(grafo.getMatriz().size())) {
			aux+=1;
			if(numl > -1) {
				linhas.add(x + numl);
				numl+=1;
			} else {
				linhas.add(x + numl);
				numl-=1;
			}
			
			if(numc > -1) {
				colunas.add(y + numc);
				numc+=1;
			} else {
				colunas.add(y + numc);
				numc-=1;
			}
			
			if((x + numl) % Math.sqrt(grafo.getMatriz().size()) == 0) {
				numl = -1;
			}
			
			if((y + numc) % Math.sqrt(grafo.getMatriz().size()) == 0) {
				numc = -1;
			}
		}
		
		//Grade
		for(Integer i : linhas) {
			for(Integer j : colunas) {
				((ArrayList<Integer>) grafo.getMatriz().get(i).get(j).get(1)).remove((Integer) cor);
			}
		}
		//Saiba que ele irá repetir a verificação nas linhas e colunas que pertencerem à grade
		
		grafo.atualizaLista();
		grafo.atualizaRestantes();
	}
	
	public static void main(String args[]) {
		List<Grafo> teste = criaGrafos("src\\arquivosDeTestes\\teste.txt", 9, 3);
		imprimaSudoku(9, teste.get(2));
		teste.set(2, resGrafo(teste.get(2)));
		System.out.println("Resolvido:");
		imprimaSudoku(9, teste.get(2));
		System.out.println("novo sudoku");
		List<Grafo> teste2 = criaGrafos("src\\arquivosDeTestes\\teste2.txt", 16, 2);
		imprimaSudoku(16, teste2.get(0));
		teste2.set(0, resGrafo(teste2.get(0)));
		System.out.println("Resolvido denovo:");
		imprimaSudoku(16, teste2.get(0));
	}
}
