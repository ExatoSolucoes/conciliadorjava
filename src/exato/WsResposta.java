package exato;

import java.util.ArrayList;

/**
 * Resposta de uma requisição aos webservices Exato Soluções.
 * @author Lucas Junqueira <lucas@exatosolucoes.com.br>
 */
public class WsResposta {
    
    /**
     * erro da resposta
     */
    public int e = 0;
    
    /**
     * explicação sobre o erro
     */
    public String msg = "";
    
    /**
     * eventos recebidos
     */
    public ArrayList<String> evt = new ArrayList<>();
    
    /**
     * rota requisitada
     */
    public String r = "";
    
    /**
     * data/hora da resposta
     */
    public String h = "";
    
}
