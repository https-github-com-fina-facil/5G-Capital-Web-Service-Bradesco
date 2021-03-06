package com.bradesco;

import java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.rmi.RemoteException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.FileInputStream;

import org.springframework.stereotype.Service;

import br.com.bradesco.webta.clientepj.webservices.WSWEBTAProxy;
import br.com.bradesco.webta.clientepj.webservices.faults.WSWEBTAFault;
import br.com.bradesco.webta.security.crypto.WEBTACryptoUtil;
import br.com.bradesco.webta.security.exception.CryptoException;
import br.com.bradesco.webta.security.exception.ParameterException;
import br.com.bradesco.webta.clientepj.webservices.beans.WSSessaoTO;
import br.com.bradesco.webta.clientepj.webservices.beans.WSRetornoExTO;
import br.com.bradesco.webta.clientepj.webservices.beans.WSRemessaTO;
import com.bradesco.remessa.Remessa;
import com.bradesco.retorno.Retorno;

@Service
public class DecryptService {

  /**
   * Tamanho maximo do LL
   */
  protected static final int LL_MAX = 524288;

  /**
   * Tamanho do campo Logical Length (LL)
   */
  protected static final int TAMANHO_LL = 4;

  /**
   * Erro de leitura
   */
  protected static final int ERRO_LEITURA = -1;

  public void obterArquivoRetorno(WSWEBTAProxy ws, WSSessaoTO wssessaoto, Retorno retorno) {
    FileOutputStream fos = null;
    try {

      long offSet = 0;
      int numeroArquivo = 0;
      byte[] ll = new byte[4];

      WSRetornoExTO ret = ws.obterBlocoRetornoEx(wssessaoto.getCTRL(), numeroArquivo, offSet, 8192);

      numeroArquivo = ret.getNumeroArquivo();

      boolean existemArquivosParaReceber = true;

      while (existemArquivosParaReceber) {

        if (numeroArquivo > 0) {
          if (offSet == 0) {
            fos = new FileOutputStream(ret.getNomeLogicoArquivo());
          }

          if (ret.isFlagArquivoCriptografado()) {
            // Monta LL
            for (int i = 0; i < TAMANHO_LL; i++) {
              int offset = (TAMANHO_LL - 1 - i) * 8;
              ll[i] = (byte) ((ret.getConteudo().length >>> offset) & 0xFF);
            }

          }

          offSet += ret.getQuantidadeBytesLidos();
          String file = null;

          if (ret.getConteudo() != null) {
            file = new String(ret.getConteudo(), StandardCharsets.UTF_8);
          }

          retorno.setName(ret.getNomeLogicoArquivo());
          retorno.setFile(file);

          if (offSet <= ret.getQuantidadeBytesArquivo()) {
            if (fos != null)
              fos.close();
            numeroArquivo = 0;
            offSet = 0;
          }
        } else {
          existemArquivosParaReceber = false;

        }

      }

    } catch (WSWEBTAFault e) {
      // Implementar adequadamente o tratamento da exce????o
      System.out.println("Erro na execucao de metodo do Web Service - Codigo de erro: " + e.getCodigo()
          + " - Mensagem descritiva: " + e.getMessage1());
    } catch (IOException e) {
      System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " + e.getMessage());
    }

  }

  public String getIdClienteTransAutom(byte[] transfFileKey) {

    String idClienteTransAutom = null;

    try {

      idClienteTransAutom = WEBTACryptoUtil.getIdFromDecodedFileKey(transfFileKey);

    } catch (ParameterException e) {
      // Implementar adequadamente o tratamento da exce????o
      System.out.println(
          "Erro na passagem de parametros para metodo da API de Criptografia - Mensagem descritiva: " + e.getMessage());
    }
    return idClienteTransAutom;

  }

  public byte[] getKeyFromDecodedFileKey(byte[] transfFileKey) {

    byte[] transfKey = null;

    try {

      transfKey = WEBTACryptoUtil.getKeyFromDecodedFileKey(transfFileKey);

    } catch (ParameterException e) {
      // Implementar adequadamente o tratamento da exce????o
      System.out.println(
          "Erro na passagem de parametros para metodo da API de Criptografia - Mensagem descritiva: " + e.getMessage());
    }
    return transfKey;

  }

  public byte[] decodeKeyFile(File archive, String password) {

    byte[] transfFileKey = null;

    try {

      transfFileKey = WEBTACryptoUtil.decodeKeyFile(archive, password);

    } catch (CryptoException e) {

      // Implementar adequadamente o tratamento da exce????o
      System.out.println("Erro na execucao de metodo da API de Criptografia - Mensagem descritiva: " + e.getMessage());
    } catch (IOException e) {
      System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " + e.getMessage());
    }
    return transfFileKey;

  }

  public Retorno arquivoRetorno() {
    Retorno retorno = new Retorno();
    WSWEBTAProxy ws = new WSWEBTAProxy();

    ws.setEndpoint("https://www.webtatransferenciadearquivos.bradesco.com.br/webta/services/WSWEBTA");

    File archive = new File("transferencia202010141728.bin");

    String idClienteTransAutom = null;

    try {

      byte[] transfFileKey = this.decodeKeyFile(archive, "123456");

      // Obtem id do cliente
      idClienteTransAutom = this.getIdClienteTransAutom(transfFileKey);

      // Obtem chave de criptografia do desafio
      byte[] transfKey = this.getKeyFromDecodedFileKey(transfFileKey);

      // ABRE SESSAO
      WSSessaoTO wssessaoto = ws.criarSessao(idClienteTransAutom);

      byte[] desafioCripto = WEBTACryptoUtil.encode(wssessaoto.getDesafio().getBytes(), transfKey);

      // ENVIA DESAFIO CRIPTOGRAFADO PARA O SERVIDOR
      ws.habilitarSessao(wssessaoto.getCTRL(), desafioCripto);

      /*
       * ------------------------- RECEBE UM ARQUIVO DE RETORNO
       * --------------------------
       */

      this.obterArquivoRetorno(ws, wssessaoto, retorno);

      // Encerra a sessao
      ws.encerrarSessao(wssessaoto.getCTRL());

      return retorno;

    }

    catch (WSWEBTAFault e) { // Implementar adequadamente o tratamento da exce????o
      System.out.println("Erro na execucao de metodo do Web Service - Codigo de erro: " + e.getCodigo()
          + " - Mensagem descritiva: " + e.getMessage1());
    }

    catch (IOException e) {
      System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " + e.getMessage());
    }

    return retorno;

  }

  public void obterReinicioTxArquivoRemessa(String remessa, String idClienteTransAutom, byte[] transfKey,
      WSWEBTAProxy ws) {

    // Inicia processo de transmissao de um arquivo em formato texto

    System.out.println("Entrou Aqui");

    try {
      // ABRE SESSAO
      WSSessaoTO wssessaoto = ws.criarSessao(idClienteTransAutom);

      byte[] desafioCripto = WEBTACryptoUtil.encode(wssessaoto.getDesafio().getBytes(), transfKey);

      // ENVIA DESAFIO CRIPTOGRAFADO PARA O SERVIDOR
      ws.habilitarSessao(wssessaoto.getCTRL(), desafioCripto);

      System.out.println(remessa);
      // Inicia processo de transmissao de um arquivo em formato criptografado
      WSRemessaTO res = ws.obterReinicioTxArquivoRemessa(wssessaoto.getCTRL(), remessa);

      System.out.println("Res" + res);

      FileInputStream fis = new FileInputStream("arquivos/" + remessa);
      long offSet = res.getQuantidadeBytesArquivo();
      int numBloco = 1;

      boolean flagUltimoBloco = false;
      byte[] blocoLido = new byte[8192];
      byte[] blocoToTx;
      int tamLido;

      // Loop de leitura e transmissao do arquivo
      while (fis.available() > 0) {
        tamLido = fis.read(blocoLido);
        if (fis.available() <= tamLido) {
          flagUltimoBloco = true;
        }
        blocoToTx = Arrays.copyOf(blocoLido, tamLido);
        if (numBloco > res.getUltimoBlocoRecebido()) {
          // Transmite demais blocos do arquivo
          res = ws.transmitirBlocoArquivoRemessa(wssessaoto.getCTRL(), remessa, blocoToTx, offSet, numBloco,
              flagUltimoBloco);
          offSet = res.getOffSet();
        }
        numBloco++;
      }

      fis.close();

    } catch (RemoteException e) { // Implementar adequadamente o tratamento da exce????o

      System.out.println("Erro na execucao de metodo do Web Service - Codigo de erro: " + e);

    } catch (IOException e) {
      System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " + e.getMessage());
    }
    ;

  }

  public String arquivoRemessa(String remessa) {

    WSWEBTAProxy ws = new WSWEBTAProxy();

    ws.setEndpoint("https://www.webtatransferenciadearquivos.bradesco.com.br/webta/services/WSWEBTA");

    File archive = new File("transferencia202010141728.bin");

    String idClienteTransAutom = null;

    try {

      byte[] transfFileKey = WEBTACryptoUtil.decodeKeyFile(archive, "123456");

      // Obtem id do cliente
      idClienteTransAutom = this.getIdClienteTransAutom(transfFileKey);

      // Obtem chave de criptografia do desafio
      byte[] transfKey = this.getKeyFromDecodedFileKey(transfFileKey);

      this.obterReinicioTxArquivoRemessa(remessa, idClienteTransAutom, transfKey, ws);

    } catch (WSWEBTAFault e) { // Implementar adequadamente o tratamento da exce????o
      System.out.println("Erro na execucao de metodo do Web Service - Codigo de erro: " + e.getCodigo()
          + " - Mensagem descritiva: " + e.getMessage1());
    }

    catch (IOException e) {
      System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " + e.getMessage());
    }

    catch (CryptoException e) {

      // Implementar adequadamente o tratamento da exce????o
      System.out.println("Erro na execucao de metodo da API de Criptografia - Mensagem descritiva: " + e.getMessage());
    }

    return remessa;
  }

  public String createFileRemessa(Remessa remessa) {

    try {
      FileWriter myWriter = new FileWriter("arquivos/" + remessa.getName());
      myWriter.write(remessa.getCnab());
      myWriter.close();
      System.out.println("Successfully wrote to the file.");

    } catch (IOException e) {
      System.out.println("An error occurred.");
      e.printStackTrace();
    }

    return remessa.getName();

  }

}
