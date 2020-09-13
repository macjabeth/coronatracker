package com.macjabeth.coronatracker.controllers;

import java.util.List;

import com.macjabeth.coronatracker.models.GlobalStats;
import com.macjabeth.coronatracker.services.CoronaDataService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
  @Autowired
  CoronaDataService coronaDataService;

  @GetMapping("/")
  public String home(Model model) {
    List<GlobalStats> allStats = coronaDataService.getAllStats();
    model.addAttribute("stats", allStats);
    int totalCases = allStats.stream().mapToInt(stat -> stat.getLatestTotalCases()).sum();
    model.addAttribute("totalCases", totalCases);
    int totalNewCases = allStats.stream().mapToInt(stat -> stat.getDiffFromPrevDay()).sum();
    model.addAttribute("totalNewCases", totalNewCases);
    return "home";
  }
}
