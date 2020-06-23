package com.ft.simplex.controllers;

import com.ft.simplex.realize.simplex.Simplex;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    int numberOfRest = 0;
    int numberOfVar = 0;
    int [][]Fx;
    Simplex simplex;
    String result;

    @GetMapping()
    public String main (Model model) {
        model.addAttribute("Fx",Fx);
        model.addAttribute("result", result);
        return "main";
    }

    @PostMapping("/size")
    public String size (
            @RequestParam(defaultValue = "0")int rest,
            @RequestParam(defaultValue = "0")int var,
            Model model
    ) {
        if(rest != 0 && var != 0) {
            numberOfRest = rest+1;
            numberOfVar = var+1;
            Fx = new int[numberOfRest][];
            for(int i=0; i < Fx.length; i++) {
                if (i == Fx.length-1)
                    Fx[i] = new int[numberOfVar-1];
                else Fx[i] = new int[numberOfVar];
            }
        }
        return "redirect:/main";
    }

    @PostMapping("/matrix")
    public String matrix(@RequestParam double [] fx, Model model){
        double[][] temp = new double[numberOfRest][];
        for (int i = 0 ; i < temp.length; i++)
            temp[i] = new double[numberOfVar];

        for (int i = 0; i < temp.length; i++)
            for (int j = 0; j <temp[i].length; j++)
                if(i*(temp[i].length)+j < fx.length)
                    temp[i][j] = fx[i*(temp[i].length)+j];

        long begin = System.nanoTime();
        simplex = new Simplex(temp);
        result = simplex.getResult();
        long end = System.nanoTime();
        System.out.println("Ex time >>" + (end-begin));
        return "redirect:/main";
    }
}
