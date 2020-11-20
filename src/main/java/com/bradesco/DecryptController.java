package com.bradesco;

import com.bradesco.remessa.Remessa;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class DecryptController {

  @Autowired
  DecryptService decryptService;

  @GetMapping("/")
  public String start() {
    return String.format("Web service Bradesco 5G!");
  }

  @RequestMapping(value = "/arquivo/retorno", method = RequestMethod.GET)
  public ResponseEntity<Object> remessa() {

    return new ResponseEntity<>(decryptService.arquivoRetorno(), HttpStatus.OK);

  }

  @GetMapping("/arquivo/remessa")
  @ResponseBody
  public ResponseEntity<Object> getFoos(@RequestParam String id) {
    return new ResponseEntity<>(decryptService.arquivoRemessa(id), HttpStatus.OK);
  }

  @RequestMapping(value = "/arquivo/criar/remessa", method = RequestMethod.POST)
  public ResponseEntity<Object> remessa(@RequestBody Remessa remessa) {

    return new ResponseEntity<Object>(decryptService.createFileRemessa(remessa), HttpStatus.OK);

  }

}
