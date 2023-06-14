package dev.sunbirdrc.registry.controller;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class RegistryTemplateController{

    @Value("${certificate.templateFolderPath}")
    private String templatesFolderPath;

    @RequestMapping(value = "/api/v1/templates/{fileName}", method = RequestMethod.GET)
    public String getTemplate(@PathVariable String fileName) {
        String content = "";
        try {
            File file = new ClassPathResource(templatesFolderPath + fileName).getFile();
            content = new String(Files.readAllBytes(file.toPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }
}
