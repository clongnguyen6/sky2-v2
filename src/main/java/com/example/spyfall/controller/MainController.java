package com.example.spyfall.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainController {
    @GetMapping()
    String test() {

        return "<h1 style=\"font-size: 400%;\"> SPY FALL /sp</h1>" +
                "<h1 style=\"font-size: 300%;\"> /sp de tham gia choi spy</h1>" +
                "<h1 style=\"font-size: 300%;\"> /sp/2/4 de tao Spy 2 la kieu ko biet dia chi, 4 la so nguoi</h1>" +
                "<h1 style=\"font-size: 300%;\"> /sp/1/4/2 de tao Spy 1 la kieu dia chi khac, 4 la so nguoi, 2 la so spy</h1>" +
                "<h1 style=\"font-size: 300%;\"> /sp/1/4/3/1 de tao Spy 1 la kieu dia chi khac, 4 la so nguoi, 2 la so spy, 1 la so mu trang</h1>" +
                "<h1 style=\"font-size: 300%;\"> /sp/delete/4 de xoa 4</h1>" +
                "<h1 style=\"font-size: 300%;\"> /sp/add/name/id de them name va id</h1>" +
                "<br>" +
                "<h1 style=\"font-size: 400%;\"> WOLF /sp</h1>" +
                "<h1 style=\"font-size: 300%;\"> /ms/play hoac /ms/play/name de tham gia choi soi</h1>" +
                "<h1 style=\"font-size: 300%;\"> /ms/run de thiet lap soi</h1>" +
                "<h1 style=\"font-size: 300%;\"> /ms/admin /ms/admin2 quan tro soi</h1>" +

                "<br>" +
                "<h1 style=\"font-size: 400%;\"> GODUCK /gd</h1>" +
                "<h1 style=\"font-size: 300%;\"> /gd/play de tham gia choi goduck</h1>" +
                "<h1 style=\"font-size: 300%;\"> /gd/run/4/2 de choi 4 la so nguoi, 2 la so spy</h1>" +

                "<br>" +
                "<h1 style=\"font-size: 400%;\"> Spy2 /spy2</h1>" +
                "<h1 style=\"font-size: 300%;\"> /spy2 de tham gia choi </h1>" +
                "<h1 style=\"font-size: 300%;\"> /spy2/setup/{totalPlayers}/{numSpies}/{numWhite} de setup choi</h1>" +
                "<h1 style=\"font-size: 300%;\"> /spy2/removeList de xoa nguoi choi</h1>";

    }
}
