package com.Digis01FArceConsumoApiTMDB.Controller;

import com.Digis01FArceConsumoApiTMDB.ML.Usuario;
import jakarta.servlet.http.HttpSession;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

@Controller
@RequestMapping("Usuario")
public class UsuarioController {

    private RestTemplate restTemplate = new RestTemplate();
    private String BearerKey = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiJlNDVkNzFkYTBiOGQ1NDgzZjQ2YTc0YjFmMDYzZDM5MiIsIm5iZiI6MTc0NzMzNDg0NS45MTEsInN1YiI6IjY4MjYzNmJkNjRlOTQxZGY3MzRiNTYyNCIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.HRX79MlpR1QXSfR3RiYXJai9o5GekbUSo-urfRJoLEg";

    @GetMapping
    public String Index(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "layout";
    }

    @PostMapping("/login")
    public String Login(@ModelAttribute Usuario usuario, HttpSession session) {
        try {
            // Obtener el token
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", BearerKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> tokenRequest = new HttpEntity<>(headers);

            ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                    "https://api.themoviedb.org/3/authentication/token/new",
                    HttpMethod.GET,
                    tokenRequest,
                    Map.class
            );

            String requestToken = (String) tokenResponse.getBody().get("request_token");

            // Validar el login con username, password y request token
            Map<String, String> loginPayload = new HashMap<>();
            loginPayload.put("username", usuario.getUsername());
            loginPayload.put("password", usuario.getPassword());
            loginPayload.put("request_token", requestToken);

            HttpEntity<Map<String, String>> loginRequest = new HttpEntity<>(loginPayload, headers);

            ResponseEntity<Map> loginResponse = restTemplate.exchange(
                    "https://api.themoviedb.org/3/authentication/token/validate_with_login",
                    HttpMethod.POST,
                    loginRequest,
                    Map.class
            );

            // Crear una sesión con el token validado
            Map<String, String> sessionPayload = new HashMap<>();
            sessionPayload.put("request_token", requestToken);

            HttpEntity<Map<String, String>> sessionRequest = new HttpEntity<>(sessionPayload, headers);

            ResponseEntity<Map> sessionResponse = restTemplate.exchange(
                    "https://api.themoviedb.org/3/authentication/session/new",
                    HttpMethod.POST,
                    sessionRequest,
                    Map.class
            );

            String sessionId = (String) sessionResponse.getBody().get("session_id");
            session.setAttribute("sessionid", sessionId);

            // Obtener el account_id
            ResponseEntity<Map> accountResponse = restTemplate.exchange(
                    "https://api.themoviedb.org/3/account?session_id=" + sessionId,
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    Map.class
            );

            String accountId = String.valueOf(accountResponse.getBody().get("id"));
            System.out.println("Account ID obtenido: " + accountId);

            session.setAttribute("Account ID obtenido: ", accountId);

            return "redirect:/Usuario/peliculas"; // Redirige a la vista de películas populares
        } catch (Exception ex) {
            System.err.println("Error durante el login con TMDB: " + ex.getLocalizedMessage());
            ex.printStackTrace();
            return "error";
        }

    }

    @GetMapping("/peliculas")
    public String Peliculas(HttpSession session) {
        String sessionId = (String) session.getAttribute("sessionid");

        if (sessionId != null && !sessionId.isEmpty()) {
            System.out.println("Sesión creada correctamente con ID: " + sessionId);
        } else {
            System.out.println("No se encontró una sesión activa.");
        }

        return "/verFavoritas";
    }

    @GetMapping("/verFavoritas")
    public String verFavoritas(HttpSession session, Model model) {
        String sessionId = (String) session.getAttribute("sessionid");
        String accountId = (String) session.getAttribute("accountid");

        if (sessionId == null || accountId == null) {
            System.out.println("No hay sesión iniciada.");
            return "redirect:/Usuario";
        }

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", BearerKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.themoviedb.org/3/account/" + accountId + "/favorite/movies?session_id=" + sessionId,
                HttpMethod.GET,
                entity,
                Map.class
        );

        List<Map<String, Object>> peliculas = (List<Map<String, Object>>) response.getBody().get("results");

        model.addAttribute("peliculas", peliculas);
        return "favoritas"; // necesitas crear favoritas.html
    }

}
