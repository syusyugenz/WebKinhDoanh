package com.asm.controller.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.asm.repository.CustomerRepository;
import com.asm.repository.FavoriteRepository;
import com.asm.repository.ProductRepository;
import com.asm.service.FavoriteService;

@Controller
@RequestMapping("/admin")
public class AdminFavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @GetMapping("/favorites")
    public String showAdminFavorites(Model model) {
        List<Object[]> productsWithFavorites = favoriteService.getProductsWithFavorites();
        model.addAttribute("productsWithFavorites", productsWithFavorites);
        return "admin/favorites";
    }
}