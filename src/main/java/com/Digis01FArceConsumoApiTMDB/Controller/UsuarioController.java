package com.Digis01FArceConsumoApiTMDB.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping("Usuario")
public class UsuarioController {
    
    @GetMapping
    public String Index(){
        return "layout";
    }
    
    @GetMapping("/peliculas")
    public String Peliculas(){
        return "peliculas";
    }
}
