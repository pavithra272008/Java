package com.example.demo.controller;

import com.example.demo.service.BeachSafetyService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    private final BeachSafetyService service;

    public PageController(BeachSafetyService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Municipal Coastal Safety & Water Quality Monitoring Portal");
        model.addAttribute("sectors", service.getSectors());
        model.addAttribute("samples", service.getAllSamples());
        return "index";
    }
}

