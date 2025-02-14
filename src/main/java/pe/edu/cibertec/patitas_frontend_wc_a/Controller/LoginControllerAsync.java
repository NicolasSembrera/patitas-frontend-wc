package pe.edu.cibertec.patitas_frontend_wc_a.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc_a.DTO.LogOutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.DTO.LoginRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.DTO.LoginResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.client.AutenticacionClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginControllerAsync {

    @Autowired
    WebClient webClientAutenticacion;


    @PostMapping("/autenticar-async")
    public Mono<LoginResponseDTO> autenticar(@RequestBody LoginRequestDTO loginRequestDTO) {

        // validar campos de entrada
        if(loginRequestDTO.tipoDocumento() == null || loginRequestDTO.tipoDocumento().trim().length() == 0 ||
                loginRequestDTO.numeroDocumento() == null || loginRequestDTO.numeroDocumento().trim().length() == 0 ||
                loginRequestDTO.password() == null || loginRequestDTO.password().trim().length() == 0) {

            return Mono.just(new LoginResponseDTO("01", "Error: Debe completar correctamente sus credenciales", "", ""));

        }

        try {

            // consumir servicio de autenticación (Del Backend)
            return webClientAutenticacion.post()
                    .uri("/login")
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class)
                    .flatMap(response -> {

                        if(response.codigo().equals("00")) {
                            return Mono.just(new LoginResponseDTO("00", "", response.nombreUsuario(), ""));
                        } else {
                            return Mono.just(new LoginResponseDTO("02", "Error: Autenticación fallida", "", ""));
                        }

                    });

        } catch(Exception e) {

            System.out.println(e.getMessage());
            return Mono.just(new LoginResponseDTO("99", "Error: Ocurrió un problema en la autenticación", "", ""));

        }

    }

    @PostMapping("log_out")
    public Mono<Void> CerrarSesion(@RequestBody LogOutRequestDTO logOutRequestDTO) {
        try {
            return webClientAutenticacion.post()
                    .uri("/logout")
                    .accept(MediaType.APPLICATION_JSON)
                    .body(Mono.just(logOutRequestDTO), LogOutRequestDTO.class)
                    .retrieve()
                    .bodyToMono(Void.class);

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return null;
    }

}

