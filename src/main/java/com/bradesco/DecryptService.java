package com.bradesco;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;

import org.springframework.stereotype.Service;

import br.com.bradesco.webta.clientepj.webservices.WSWEBTAProxy;
import br.com.bradesco.webta.clientepj.webservices.faults.WSWEBTAFault;
import br.com.bradesco.webta.security.crypto.WEBTACryptoUtil;
import br.com.bradesco.webta.security.exception.CryptoException;
import br.com.bradesco.webta.security.exception.ParameterException;
import br.com.bradesco.webta.clientepj.webservices.beans.WSSessaoTO;
import br.com.bradesco.webta.clientepj.webservices.beans.WSRetornoExTO;

@Service
public class DecryptService {

  /**
   * Tamanho do campo Logical Length (LL)
   */
  protected static final int TAMANHO_LL = 4;

  public boolean obterArquivoRetorno(boolean existemArquivosParaReceber, WSWEBTAProxy ws, WSSessaoTO wssessaoto) {

    try {
      FileOutputStream fos = null;
      long offSet = 0;
      int numeroArquivo = 0;
      byte[] ll = new byte[4];

      WSRetornoExTO ret = ws.obterBlocoRetornoEx(wssessaoto.getCTRL(), numeroArquivo, offSet, 8192);

      numeroArquivo = ret.getNumeroArquivo();

      System.out.println("número " + numeroArquivo);
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
          // Grava LL e dados
          fos.write(ll);
        }
        fos.write(ret.getConteudo());
        offSet += ret.getQuantidadeBytesLidos();
        if (offSet <= ret.getQuantidadeBytesArquivo()) {
          if (fos != null)
            fos.close();
          numeroArquivo = 0;
          offSet = 0;
        }
      } else {
        existemArquivosParaReceber = false;

        return existemArquivosParaReceber;
      }

    } catch (WSWEBTAFault e) {
      // Implementar adequadamente o tratamento da exceção
      System.out.println("Erro na execucao de metodo do Web Service - Codigo de erro: " + e.getCodigo()
          + " - Mensagem descritiva: " + e.getMessage1());
    } catch (IOException e) {
      System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " + e.getMessage());
    }

    return existemArquivosParaReceber;

  }

  public Decrypt arquivoRetorno() {
    Decrypt decrypt = new Decrypt();
    WSWEBTAProxy ws = new WSWEBTAProxy();

    ws.setEndpoint("https://www.webtatransferenciadearquivos.bradesco.com.br/webta/services/WSWEBTA");

    File archive = new File("transferencia202010141728.bin");

    String idClienteTransAutom = null;

    try {
      byte[] transfFileKey = WEBTACryptoUtil.decodeKeyFile(archive, "123456");

      // Obtem id do cliente
      idClienteTransAutom = WEBTACryptoUtil.getIdFromDecodedFileKey(transfFileKey);

      // Obtem chave de criptografia do desafio
      byte[] transfKey = WEBTACryptoUtil.getKeyFromDecodedFileKey(transfFileKey);

      // ABRE SESSAO
      WSSessaoTO wssessaoto = ws.criarSessao(idClienteTransAutom);

      byte[] desafioCripto = WEBTACryptoUtil.encode(wssessaoto.getDesafio().getBytes(), transfKey);

      // ENVIA DESAFIO CRIPTOGRAFADO PARA O SERVIDOR
      ws.habilitarSessao(wssessaoto.getCTRL(), desafioCripto);

      /*
       * ------------------------- RECEBE UM ARQUIVO DE RETORNO
       * --------------------------
       */

      boolean existemArquivosParaReceber = true;

      /*
       *
       * 
       * while (existemArquivosParaReceber) { existemArquivosParaReceber =
       * this.obterArquivoRetorno(existemArquivosParaReceber, ws, wssessaoto); }
       */

      decrypt.setidClienteTransAutom(idClienteTransAutom);
      decrypt.setDesafioCripto(desafioCripto);

      return decrypt;

    }

    catch (WSWEBTAFault e) { // Implementar adequadamente o tratamento da exceção
      System.out.println("Erro na execucao de metodo do Web Service - Codigo de erro: " + e.getCodigo()
          + " - Mensagem descritiva: " + e.getMessage1());
    }

    catch (IOException e) {
      System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " + e.getMessage());
    }

    catch (CryptoException e) {

      // Implementar adequadamente o tratamento da exceção
      System.out.println("Erro na execucao de metodo da API de Criptografia - Mensagem descritiva: " + e.getMessage());
    } catch (ParameterException e) {
      // Implementar adequadamente o tratamento da exceção
      System.out.println(
          "Erro na passagem de parametros para metodo da API de Criptografia - Mensagem descritiva: " + e.getMessage());
    }

    return decrypt;

  }

}
