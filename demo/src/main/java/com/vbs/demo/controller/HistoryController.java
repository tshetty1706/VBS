package com.vbs.demo.controller;

import com.vbs.demo.models.History;
import com.vbs.demo.repositories.HistoryRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class HistoryController {
    @Autowired
    HistoryRepo historyRepo;

    @GetMapping("/histories")
    public List<History> historyList()
    {
        return historyRepo.findAll();
    }
}
