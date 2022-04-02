package exato;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.util.zip.GZIPOutputStream;
import java.util.Base64;

/**
 * Acesso ao conciliador de cartões da Exato Soluções.
 * @author Lucas Junqueira <lucas@exatosolucoes.com.br>
 */
public class ConciliadorExato {
    
    /**
     * acesso ao webservice
     */
    private WSExato ws;
    
    /**
     * usuário de acesso ao webservice
     */
    private String usuario;
    
    public ConciliadorExato(String url, String usuario)
    {
        // recebendo valores
        this.ws = new WSExato(url, usuario);
        this.usuario = usuario;
    }
    
    /**
     * Requisita a conciliação de registros de venda ou pagamento.
     * @param texto o texto da requisição (json ou xml)
     * @param chave a chave de 32 caracteres do usuário
     * @param cliente o identificador do cliente
     * @param tipo o tipo de requisição ("v" para venda, "p" para pagamento)
     * @param formato o formato do texto da requisição ("json" ou "xml")
     * @return objeto com a resposta, incluindo o código de erro "e" e uma mensagem explicativa "msg"
     * @throws NoSuchAlgorithmException
     * @throws IOException 
     */
    public WsResposta requisitar(String texto, String chave, String cliente, String tipo, String formato) throws NoSuchAlgorithmException, IOException
    {
        // preparando variáveis
        if ("p".equals(tipo.toLowerCase())) {
            tipo = "p";
        } else {
            tipo = "v";
        }
        if ("xml".equals(formato.toLowerCase())) {
            formato = "xml";
        } else {
            formato = "json";
        }
        
        // preparando texto da chave
        String k = this.usuario + texto.substring(0, 32) + texto.substring(texto.length() - 32);
        
        // preparado valores da requisição
        HashMap<String, String> vars;
        vars = new HashMap<>();
        vars.put("t", tipo);
        vars.put("c", cliente);
        vars.put("compreq", "s");
        vars.put("req", this.compTxt(texto));
        vars.put("forreq", formato);
        vars.put("forresp", formato);
        
        // fazendo a requisição ao webservice
        WsResposta resp = this.ws.requisitar("vdk-cartoes/conciliacao", chave, k, vars);
        
        // ajustando erro da resposta
        switch(resp.e) {
            case 1:
                resp.msg = "falha ao conectar à base de dados";
		break;
            case 2:
		resp.msg = "erro no texto da requisição";
		break;
            case 3:
		resp.msg = "erro no cabeçalho da requisição";
		break;
            case 4:
		resp.msg = "erro no cabeçalho da requisição";
		break;
            case 5:
		resp.msg = "o estabelecimento não foi localizado";
		break;
            case 6:
		resp.msg = "não há informações de adquirentes no período";
		break;
            case 7:
		resp.msg = "não há registros na requisição";
                break;
            case 8:
		resp.msg = "cliente não localizado";
		break;
	}
        
        // retornando
        return (resp);
    }
    
    /**
     * Recupera o log da última requisição.
     * @return o log da operação
     */
    public ArrayList<String> recLog()
    {
        return (this.ws.recLog());
    }
    
    /**
     * Comprime um texto usando gz/base64
     * @param original o texto original
     * @return o texto comprimido
     * @throws IOException 
     */
    private String compTxt(String original) throws IOException
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(original.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
	gzip.write(original.getBytes());
        gzip.close();
        byte[] compr = bos.toByteArray();
        bos.close();
        return(Base64.getEncoder().encodeToString(compr));
    }
}
