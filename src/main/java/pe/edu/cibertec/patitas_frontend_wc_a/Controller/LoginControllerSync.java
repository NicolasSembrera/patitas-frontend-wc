package pe.edu.cibertec.patitas_frontend_wc_a.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import pe.edu.cibertec.patitas_frontend_wc_a.DTO.LogOutRequestDTO;
import pe.edu.cibertec.patitas_frontend_wc_a.client.AutenticacionClient;

@RestController
@RequestMapping("/login")
@CrossOrigin(origins = "http://localhost:5173")
public class LoginControllerSync {

    @Autowired
    AutenticacionClient autenticacionClient;

    @PostMapping("log_out_sync")
    public void CerrarSesionFeign(@RequestBody LogOutRequestDTO logOutRequestDTO) {
        try {
            autenticacionClient.logOut(logOutRequestDTO);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
