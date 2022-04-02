package conciliadorjava;

import com.google.gson.Gson;
import exato.ConciliadorExato;
import exato.WsResposta;
import exato.ConciliadorResposta;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.PrintWriter;

/**
 * Exemplo do uso do conciliador de cartões da Exato Soluções <exatosolucoes.com.br>
 * @author Lucas Junqueira <lucas@exatosolucoes.com.br>
 */
public class ConciliadorJava {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
        
        // informações de acesso ao serviço (fornecidas pela Exato Soluções)
        String URLWS = "url do serviço";
        String USWS = "nome de usuário";
        String CHWS = "chave do usuário";
        String CLCONC = "identificador do cliente";
        
        // carregando o texto da requisição
        String texto = "";
        boolean ok = true;
        try {
            texto = new String(Files.readAllBytes(Paths.get("requisicao.json")));
        } catch (IOException e) {
            ok = false;
        }
        
        // texto carregado?
        if (ok) {
            // criando o conciliador
            ConciliadorExato conc = new ConciliadorExato(URLWS, USWS);
            
            // requisitando a conciliação
            WsResposta resp = conc.requisitar(texto, CHWS, CLCONC, "v", "json");
            
            // exibindo o resultado
            System.out.println("RESULTADO");
            System.out.println(resp.msg);
            
            // caso a conciliação tenha sucesso, o resultado está no primeiro evento da resposta, em "arq"
            if (resp.e == 0) {
                // recebendo o conteúdo da resposta
                Gson gson = new Gson();
                try {
                    ConciliadorResposta cresp = gson.fromJson(resp.evt.get(0), ConciliadorResposta.class);
                    PrintWriter saida = new PrintWriter(cresp.nome);
                    saida.println(cresp.arq);
                    saida.close();
                    System.out.println("o arquivo " + cresp.nome + " com a resposta da conciliação foi gravado");
                } catch (Exception e) {
                    System.out.println("o arquivo de resposta retornou corrompido");
                }
            }
            
            // exibindo o log da requisição
            System.out.println("");
            System.out.println("LOG DE EXECUÇÃO");
            ArrayList<String> log = conc.recLog();
            for (int i=0; i<log.size(); i++) {
                System.out.println(log.get(i));
            }
            
        } else {
            // finalizando
            System.out.println("texto de requisição não encontrado");
        }
    }
    
}
