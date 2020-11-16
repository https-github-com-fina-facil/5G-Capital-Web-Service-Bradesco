package com.bradesco;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.web.bind.annotation.RestController;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@RestController
public class DecryptController {

  @Autowired
  DecryptService decryptService;

  @RequestMapping(value = "/remessa", method = RequestMethod.GET)
  public ResponseEntity<Object> remessa() {

    return new ResponseEntity<>(decryptService.check(), HttpStatus.OK);

  }

}
