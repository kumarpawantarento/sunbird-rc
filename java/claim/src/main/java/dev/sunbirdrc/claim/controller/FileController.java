package dev.sunbirdrc.claim.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.*;
import dev.sunbirdrc.claim.dto.FileDto;
import dev.sunbirdrc.claim.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files/")
@RequiredArgsConstructor
public class FileController {

    @Autowired
    FileService fileService;


    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @RequestMapping("upload")
    public ResponseEntity<String> uploadFile(
            @RequestParam MultipartFile file) throws IOException {

        FileDto fileDto = fileService.uploadFile(file);

        return ResponseEntity.ok("File uploaded successfully:"+fileDto);
    }

    @RequestMapping("download")
    @PostMapping(produces = {MediaType.APPLICATION_PDF_VALUE})
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String fileName)  {
        ByteArrayResource resource = fileService.downloadFile(fileName);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "filename=\"" + fileName + "\"");
        return ResponseEntity.ok().
                contentType(MediaType.APPLICATION_PDF).
                headers(headers).body(resource);
    }
}