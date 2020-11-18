package com.bradesco;

import com.bradesco.remessa.Remessa;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class DecryptController {

  @Autowired
  DecryptService decryptService;

  @RequestMapping(value = "/retorno", method = RequestMethod.GET)
  public ResponseEntity<Object> remessa() {

    return new ResponseEntity<>(decryptService.arquivoRetorno(), HttpStatus.OK);

  }

  @RequestMapping(value = "/remessa", method = RequestMethod.POST)
  public ResponseEntity<Object> retorno(@RequestBody Remessa remessa) {

    return new ResponseEntity<Object>(decryptService.arquivoRemessa(remessa), HttpStatus.OK);

  }

}
