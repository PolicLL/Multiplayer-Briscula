package com.example.briscula.controller;

import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/items")
public class ItemController {

  @GetMapping
  public List<String> getItems() {
    return List.of("Item1", "Item2", "Item3");
  }
}
