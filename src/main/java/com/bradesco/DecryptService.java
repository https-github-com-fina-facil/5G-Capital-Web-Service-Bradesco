package com.bradesco;

/* import java.io.File;
import java.io.IOException; */

import org.springframework.stereotype.Service;

/* 
import br.com.bradesco.webta.clientepj.webservices.WSWEBTAProxy;
import br.com.bradesco.webta.security.crypto.WEBTACryptoUtil;
import br.com.bradesco.webta.security.exception.CryptoException;
import br.com.bradesco.webta.security.exception.ParameterException; */

@Service
public class DecryptService {

  /**
   * Tamanho do campo Logical Length (LL)
   */
  protected static final int TAMANHO_LL = 4;

  public String check() {

    return "CEGOU AQUI";
  }

  /*
   * public static boolean obterArquivoRetorno(boolean existemArquivosParaReceber,
   * WSWEBTAProxy ws, WSSessaoTO wssessaoto) {
   * 
   * try { FileOutputStream fos = null; long offSet = 0; int numeroArquivo = 0;
   * byte[] ll = new byte[4];
   * 
   * WSRetornoExTO ret = ws.obterBlocoRetornoEx(wssessaoto.getCTRL(),
   * numeroArquivo, offSet, 8192);
   * 
   * numeroArquivo = ret.getNumeroArquivo();
   * 
   * System.out.println("número " + numeroArquivo); if (numeroArquivo > 0) { if
   * (offSet == 0) { fos = new FileOutputStream(ret.getNomeLogicoArquivo()); }
   * 
   * if (ret.isFlagArquivoCriptografado()) { // Monta LL for (int i = 0; i <
   * TAMANHO_LL; i++) { int offset = (TAMANHO_LL - 1 - i) * 8; ll[i] = (byte)
   * ((ret.getConteudo().length >>> offset) & 0xFF); } // Grava LL e dados
   * fos.write(ll); } fos.write(ret.getConteudo()); offSet +=
   * ret.getQuantidadeBytesLidos(); if (offSet <= ret.getQuantidadeBytesArquivo())
   * { if (fos != null) fos.close(); numeroArquivo = 0; offSet = 0; } } else {
   * existemArquivosParaReceber = false;
   * 
   * return existemArquivosParaReceber; }
   * 
   * } catch (WSWEBTAFault e) { // Implementar adequadamente o tratamento da
   * exceção System.out.
   * println("Erro na execucao de metodo do Web Service - Codigo de erro: " +
   * e.getCodigo() + " - Mensagem descritiva: " + e.getMessage1()); } catch
   * (IOException e) {
   * System.out.println("Erro de I/O na aplicacao - Mensagem descritiva: " +
   * e.getMessage()); }
   * 
   * return existemArquivosParaReceber; }
   */

  /*
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * y {
   * 
   * 
   * // Obtem id do cliente
   * 
   * 
   * 
   * 
   * // Obtem chave de riptografia do desafio
   * 
   * // ABRE SESSAO
   * 
   * 
   * 
   * 
   * 
   * // ENVIA DESAFIO CRIPTOGRAFADO PARA O SERVIDOR
   * 
   * 
   * 
   * ile (existemArquivosParaRece er) {
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * 
   * System.out.println("Erro na ex catch (Paramet rException e) { // Implementar
   * adeq em.out.println(
   * 
   * 
   * 
   * 
   * }
   */

}
