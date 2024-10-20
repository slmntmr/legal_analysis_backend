package com.karataspartners.legal_analysis.controller;

import com.karataspartners.legal_analysis.service.VerbisService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/verbis")
@RestController
@AllArgsConstructor
public class VerbisController {
    private final VerbisService verbisService;

    @GetMapping("/check")
    public String checkVerbis(@RequestParam String url) {
        return verbisService.checkRecords(url);
}
}