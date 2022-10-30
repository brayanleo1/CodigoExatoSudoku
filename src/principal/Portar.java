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
		//List<Grafo> pilha = new ArrayList<Grafo>();
		Stack<Grafo> pilha = new Stack<Grafo>();
		grafo.atualizaLista(); //Para criar a lista
		boolean para = false;
		while(!para) {
			List<List<Integer>> lista = new ArrayList<List<Integer>>(grafo.getLista());
			lista.sort(Comparator.comparing(l -> l.get(2)));
			int id = 0;
			while((Integer) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(0) != 0) {//Enquanto for um v�rtice colorido
				id+=1;
				if(id == grafo.getMatriz().size() * grafo.getMatriz().size()) {
					return grafo;
				}
			}
			if(!para) {
				if(lista.get(id).get(2) == 0) {//Se n�o houverem possibilidades para o v�rtice descolorido com menores possibilidades (0) para o vertice descolorido ent�o volta
					grafo = pilha.pop();//Pegue o �ltimo elemento da pilha
				} else {//Existem pelo menos uma possibilidade de colora��o
					if(lista.get(id).get(2) > 1) {//Se existem mais
						//Escolhemos uma cor
						int cor = 0;
						for(Integer pCor : ((List<Integer>) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1))) {
							if(cor == 0) {//Se for a cor 0
								cor = pCor; //S� atualiza
							} else if(grafo.getRestanteCor(pCor) < grafo.getRestanteCor(cor)) {//Se n�o for e a nova cor tiver menos usos restantes que a atual
								cor = pCor; //Atualize pois s�o maiores as chances de se der ruim na colora��o, dar ruim logo
							}
						}
						((List<Integer>) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1)).remove((Integer) cor); //Remova essa cor das possibilidades
						grafo.atualizaLista();
						pilha.push(grafo.copyMatriz()); //Adicione o grafo sem a possibilidade da cor a ser escolhida
						grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).set(0, cor);//Colore com a cor pega
						attVizinhos(lista.get(id).get(0), lista.get(id).get(1),grafo.getMatriz().size(),grafo, cor);//Atualize as cores dos vizinhos, desde que dele mesmo j� foi removido
					} else {//S� existe uma cor, escolha ela e pronto
						Integer cor = ((List<Integer>) grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).get(1)).get(0);//Pega a primeira cor
						grafo.getMatriz().get(lista.get(id).get(0)).get(lista.get(id).get(1)).set(0, cor);//Colore com a cor pega
						attVizinhos(lista.get(id).get(0), lista.get(id).get(1),grafo.getMatriz().size(),grafo, cor);//Atualize as cores dos vizinhos e dele mesmo
					}
				}
			}
		}
		return grafo;
	}
	
	/*
	def resSudoku(grafo, lista, maisP): #algoritmo para resolu��o do sudoku
	  #Organizar a lista de acordo com os v�rtices coloridos
	  lista = sorted(lista, key=lambda l: l[0][2]) #ordena listapor n�mero de possibilidades de um v�rtice
	  while(grafo[lista[80][0][0]][lista[80][0][1]][0] == 0):#enquanto grafo n�o for colorido/Ultimo item da lista n�o for colorido
	    id = 0
	    for i in range(81):
	      if(grafo[lista[id][0][0]][lista[id][0][1]][0] != 0): #se for um v�rtice colorido, pule ele
	        id = id + 1 #pular essa possibilidade

	    if((len(grafo[lista[id][0][0]][lista[id][0][1]][1]) == 0) and (grafo[lista[id][0][0]][lista[id][0][1]][0] == 0)): #Se o v�rtice da lista que possui a menor numero de possibilidades, possuir zero e n�o for colorido
	      #Volte o algoritmo pois esse caminho impossibilitou a colora��o da inst�ncia
	      t = len(maisP) - 1
	      grafo = maisP[t][0]
	      lista = maisP[t][1]
	      maisP.pop(t)
	    else:
	      cor = grafo[lista[id][0][0]][lista[id][0][1]][1][0] #recebendo a cor que vai colorir o v�rtice
	      grafo = attPoss(grafo, [lista[id][0]], cor)
	      lista[id][0][2] = lista[id][0][2] - 1 #diminui o tamanho da lista de possibilidades
	      if(lista[id][0][2] != 0):
	        maisP.append((grafo.copy(), lista.copy()))
	      grafo[lista[id][0][0]][lista[id][0][1]][0] = cor #colorir o v�rtice com a primeira cor da lista de possibilidades
	      grafo = attPoss(grafo, lista[id][1:], cor) #muda as possibilidades de cores das posi��es adjacentes
	      lista = attTamanho(grafo, lista) #atualiza os valores de tamanhos de possibilidades de cores da lista
	      lista = sorted(lista, key=lambda l: l[0][2]) #reordena lista
	  return grafo*/
	
	
	//n = dimens�o do sudoku nxn, lin = n�mero de inst�ncias de sudoku a serem lisdas do arquivo
	public static List<Grafo> criaGrafos(String pos, int n, int lin) { //Recebe String indicando posi��o/nome do arquivo e ent�o l� e cria a array de matrizes nxn do arquivo
	
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
	   	Grafo vetCol = new Grafo(); //Vetor de colunas, que guarda em cada posi��o as linhas
	   	for (int x = 0; x < n; x++) {
	   		List<Vector<Object>> vetLin = new ArrayList<Vector<Object>>(); //Vetor de linhas, que em cada posi��o guarda vetores com cor e possibilidades
	   		for (int y = 0; y < n; y++) {
	   				Vector<Object> vetPos = new Vector<Object>(); //Vetor de cada posi��o, que guarda cor e possibilidades
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
		
		//Adiciona linhas e colunas daquela grade espec�fica
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
		//Saiba que ele ir� repetir a verifica��o nas linhas e colunas que pertencerem � grade
		
		grafo.atualizaLista();
		grafo.atualizaRestantes();
	}
	
	public static void main(String args[]) {
		List<Grafo> teste = criaGrafos("src\\arquivosDeTestes\\teste.txt", 9, 3);
		imprimaSudoku(9, teste.get(2));
		//Stack<Grafo> pilha = new Stack<Grafo>();
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
