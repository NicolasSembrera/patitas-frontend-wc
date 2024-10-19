package pe.edu.cibertec.patitas_frontend_wc_a.Controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import pe.edu.cibertec.patitas_frontend_wc_a.DTO.LogOutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.DTO.LoginRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.DTO.LoginResponseDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.client.AutenticacionClient;
import pe.edu.cibertec.patitas_frontend_wc_a.viewmodel.LoginModel;
import reactor.core.publisher.Mono;


@Controller
@RequestMapping("/login")
public class LoginController {

    @Autowired
    WebClient webClientAutenticacion;


    @GetMapping("/inicio")
    public String inicio(Model model) {
        LoginModel loginModel = new LoginModel("00", "", "");
        model.addAttribute("loginModel", loginModel);
        return "inicio";
    }

    @PostMapping("/autenticar")
    public String autenticar(@RequestParam("tipoDocumento") String tipoDocumento,
                             @RequestParam("numeroDocumento") String numeroDocumento,
                             @RequestParam("password") String password,
                             Model model) {

        // Validar campos de entrada
        if (tipoDocumento == null || tipoDocumento.trim().length() == 0 ||
                numeroDocumento == null || numeroDocumento.trim().length() == 0 ||
                password == null || password.trim().length() == 0) {

            LoginModel loginModel = new LoginModel("01", "Error: Debe completar correctamente sus credenciales", "");
            model.addAttribute("loginModel", loginModel);
            return "inicio";

        }

        try {

            // Invocar servicio de autenticación
            String endpoint = "http://localhost:8081/autenticacion/login";
            LoginRequestDTO loginRequestDTO = new LoginRequestDTO(tipoDocumento, numeroDocumento, password);
            //LoginResponseDTO loginResponseDTO = webClientAutenticacion.postForObject(endpoint, loginRequestDTO, LoginResponseDTO.class);

            Mono<LoginResponseDTO> monoLoginResponse = webClientAutenticacion.post()
                    .uri(endpoint)
                    .body(Mono.just(loginRequestDTO), LoginRequestDTO.class)
                    .retrieve()
                    .bodyToMono(LoginResponseDTO.class);

            //Recuperar resultado modo sincronico
            LoginResponseDTO loginResponseDTO = monoLoginResponse.block();

            if (loginResponseDTO.codigo().equals("00")){

                LoginModel loginModel = new LoginModel("00", "", loginResponseDTO.nombreUsuario());
                model.addAttribute("loginModel", loginModel);
                return "principal";

            } else {

                LoginModel loginModel = new LoginModel("02", "Error: Autenticación fallida", "");
                model.addAttribute("loginModel", loginModel);
                return "inicio";

            }

        } catch(Exception e) {

            LoginModel loginModel = new LoginModel("99", "Error: Ocurrió un problema en la autenticación", "");
            model.addAttribute("loginModel", loginModel);
            System.out.println(e.getMessage());
            return "inicio";

        }


    }

 }