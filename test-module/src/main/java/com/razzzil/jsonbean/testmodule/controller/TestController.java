package com.razzzil.jsonbean.testmodule.controller;

import com.razzzil.jsonbean.testmodule.dto.PersonDto;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/person")
public class TestController {

    @GetMapping
    public PersonDto testPerson(){
        return PersonDto.builder()
                .age(12)
                .name("Razil")
                .surname("Minneakhmetov")
                .build();
    }

    @PostMapping
    public PersonDto testPerson(@Validated PersonDto personDto){
        return personDto;
    }
}
