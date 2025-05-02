package id.co.bcaf.solvr.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {
    @GetMapping("/")
    public String index() {
        return "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: Arial, sans-serif; background-color: #f4f4f4; margin: 0; padding: 0; display: flex; justify-content: center; align-items: center; height: 100vh; }" +
                ".container { text-align: center; padding: 20px; background-color: white; border-radius: 10px; box-shadow: 0 0 15px rgba(0, 0, 0, 0.1); }" +
                ".title { color: #4CAF50; font-size: 36px; margin-bottom: 20px; }" +
                ".message { font-size: 18px; color: #555; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='title'>Selamat Datang di Aplikasi Solvr!</div>" +
                "<div class='message'>Aplikasi ini berhasil di-deploy dan siap digunakan.</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}

