package com.example.spyfall.controller;

import com.example.spyfall.common.Spy2;
import com.example.spyfall.service.DataInputService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/spy2")
public class Spy2Controller {

    List<List<String>> keywords = new ArrayList<>();
    List<Spy2> dataWillPlay = new ArrayList<>();
    List<Spy2> dataPlayer = new ArrayList<>();
    List<Spy2> dataSpies = new ArrayList<>();
    List<Spy2> removed = new ArrayList<>();
    List<Spy2> dataMainSpy2 = new ArrayList<>();
    Spy2 dataSpy;
    Spy2 dataNotSpy;
    String image ="/spy2.png";

    Integer countnumSpies = 0;
    Integer countWhite = 0;

    @Autowired
    DataInputService dataInputService;

    @GetMapping
    String playSpy2() {
        StringBuilder result = new StringBuilder();

        result.append("<form id=\"dataForm\" style=\"font-size: 150%;\" onsubmit=\"handleSubmit(event)\">");

        result.append("<input type=\"text\" id=\"nameId\" name=\"total\" placeholder=\"Nhập tên của bạn\"")
                .append("style=\"margin: 10px 0; font-size: 250%;\"><br>");

        result.append("<div style=\"text-align: center; margin-top: 20px;\">");
        result.append("<button type=\"submit\" id=\"sendButton\" ")
                .append("style=\"font-size: 300%; padding: 15px 40px;\">Ok</button>");
        result.append("</div>");

        result.append("</form>");
        result.append("<script>");
        result.append("function handleSubmit(event) {");
        result.append("    event.preventDefault();"); // Ngăn form submit mặc định
        result.append("    var input = document.getElementById(\"nameId\");");
        result.append("    if (input.value.trim() !== '') {");
        result.append("        window.location.href = '/spy2/play/' + input.value;");
        result.append("    }");
        result.append("}");

        result.append("document.getElementById('sendButton').onclick = function() {");
        result.append("    handleSubmit(new Event('submit'));"); // Cho nút OK dùng lại
        result.append("};");
        result.append("</script>");

        result.append("<img src=\"").append(image).append("\" alt=\"QR Code\" width=\"500\" height=\"500\">");
        return result.toString();

    }

    @GetMapping("/play/{name}")
    String playSpy2(HttpServletRequest request, @PathVariable String name) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        System.out.println(clientIp);
        Integer ipAssigned = null;
        try {
            if (!dataPlayer.isEmpty()) {
                for (Spy2 dataMember : dataPlayer) {
                    if (dataMember.getIpConfig().equals(clientIp)) {
                        ipAssigned = dataPlayer.indexOf(dataMember);
                        if (!Objects.equals(dataMember.getUserName(), name)) {
                            dataMember.setUserName(name);
                        }
                        break;
                    }
                }
            }
            StringBuilder result = new StringBuilder();
            if (ipAssigned != null) {
                result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">").append(dataPlayer.get(ipAssigned).getKeyword()).append("</h1>");

            } else {
                Collections.shuffle(dataWillPlay);
                Spy2 yourLocation = dataWillPlay.get(0);
                yourLocation.setUserName(name);
                yourLocation.setIpConfig(clientIp);
                dataPlayer.add(yourLocation);
                dataWillPlay.remove(0);
                System.out.println(yourLocation.getKeyword());
                result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">").append(yourLocation.getKeyword()).append("</h1>");
            }
            result.append("<label style=\"font-size: 350%;\">"
                    + "<input type=\"checkbox\" id=\"showTextCheckbox\" onchange=\"toggleText()\" >"
                    + " Show/Hide Location"
                    + "</label>");

            result.append("<h2  > Số lượng Spy: ").append(countnumSpies).append("</h2>");
            result.append("<h2  > Số lượng Mũ Trắng: ").append(countWhite).append("</h2>");
            result.append("<h2  > Số người còn lại: ").append(dataPlayer.size() - removed.size()).append("</h2>");
            result.append("<br><br>");
            for (Spy2 player : removed) {
                result.append("<h1  style=\"font-size: 150%;\"> ").append(player.getUserName().toUpperCase() + " -*- " + player.getRole()).append("</h1>");
            }


            result.append("<script>"
                    + "function toggleText() {"
                    + "    var checkbox = document.getElementById(\"showTextCheckbox\");"
                    + "    var textElement = document.getElementById(\"location\");"
                    + "    if (checkbox.checked) {"
                    + "        textElement.style.color = \"black\";"  // Chữ màu đen khi checkbox được chọn
                    + "    } else {"
                    + "        textElement.style.color = \"white\";"  // Chữ màu trắng khi checkbox không được chọn
                    + "    }"
                    + "}"
                    + "</script>");
            result.append("<img src=\"").append(image).append("\" alt=\"QR Code\" width=\"500\" height=\"500\">");
            return result.toString();
        } catch (Exception e) {

            return "<h1 style=\"font-size: 500%;\"> Đợi xí nha đại ca</h1>" +
                    "<img src=\"/qrcode.png\" alt=\"QR Code\" width=\"500\" height=\"500\">";
        }
    }

    @GetMapping("/setup/{totalPlayers}/{numSpies}/{numWhite}")
    String setupSpy2(@PathVariable int totalPlayers, @PathVariable int numSpies, @PathVariable int numWhite) {
        Random random = new Random();
        dataWillPlay.clear(); // clear data
        dataMainSpy2.clear();
        if(keywords.isEmpty()) {
            keywords.addAll(dataInputService.createDataSpy2());
        }
        int dataget = random.nextInt(keywords.size());
        checkDuplicatePairs();
        for (String spydata : keywords.get(dataget)) {
            dataMainSpy2.add(Spy2.builder().keyword(spydata).build());
        }
        keywords.remove(dataget);

        countnumSpies = numSpies;
        countWhite = numWhite;

        int indexMember = random.nextInt(dataMainSpy2.size());
        dataNotSpy = dataMainSpy2.get(indexMember);

        dataSpies = new ArrayList<>(dataMainSpy2);
        dataSpies.remove(indexMember);

        int indexSpy = random.nextInt(dataSpies.size());
        dataSpy = dataSpies.get(indexSpy);

        int cnt = 1;
        for (int i = 0; i < totalPlayers - (numSpies + numWhite); i++) {
            dataWillPlay.add(createPlayer(cnt, dataNotSpy.getKeyword(), "Dân Thường"));
            cnt++;
        }
        for (int i = 0; i < numSpies; i++) {
            dataWillPlay.add(createPlayer(cnt, dataSpy.getKeyword(), "Spy"));
            cnt++;
        }

        for (int i = 0; i < numWhite; i++) {
            dataWillPlay.add(createPlayer(cnt, "Bạn là mũ trắng", "Mũ Trắng"));
            cnt++;
        }

        return "<h1 style=\"font-size: 500%;\"> Success</h1>" +
                "<h1 style=\"font-size: 500%;\">" + totalPlayers + " : so luong nguoi choi </h1>" +
                "<h1 style=\"font-size: 500%;\">" + numSpies +  " : so luong spy </h1>" +
                "<h1 style=\"font-size: 500%;\">" + numWhite + " : so luong mu trang </h1>" +
                "<img src=\"" + image + "\" alt=\"QR Code\" width=\"500\" height=\"500\">" ;
    }

    @GetMapping("/remove/{id}")
    String removePlayer(@PathVariable int id) {
        for (Spy2 player : dataPlayer) {
            if (player.getId() == id) {
                player.setRemove(true);
                removed.add(player);
            }
        }
        return "Success";
    }

    @GetMapping("/removeList")
    String listRemove () {
        StringBuilder result = new StringBuilder();
        for (Spy2 player : dataPlayer) {
            if (player.isRemove()) {
                result.append("<h1  style=\"font-size: 200%; text-decoration: line-through;\">").append(player.getId()).append(". ").append(player.getUserName()).append("</h1>");
            } else {
                result.append("<h1  style=\"font-size: 200%;\">").append(player.getId()).append(". ").append(player.getUserName()).append("</h1>");
            }
        }
        result.append("<br><br>");
        for (Spy2 player : removed) {
            result.append("<h1  style=\"font-size: 150%;\"> ").append(player.getUserName().toUpperCase() + " -*- " + player.getRole()).append("</h1>");
        }
        result.append("<input type=\"number\" id=\"numberInput\" placeholder=\"Nhập số\" style=\"margin: 10px 0; font-size: 250%;\">")
                .append("<button id=\"okButton\" style=\"font-size: 300%; padding: 15px 40px;\">OK</button>")
                .append("<script>")
                .append("document.getElementById(\"okButton\").addEventListener(\"click\", function() {")
                .append("var number = document.getElementById(\"numberInput\").value;")
                .append("if(number === \"\") { alert(\"Vui lòng nhập số!\"); return; }")
                .append("var url = \"/spy2/remove/\" + encodeURIComponent(number);")
                .append("fetch(url)")
                .append(".then(response => { if(response.ok){ alert(\"Success!\"); } else { alert(\"Error: \" + response.status); } })")
                .append(".catch(error => { alert(\"Request failed: \" + error); });")
                .append("});")
                .append("</script>");

        return result.toString();
    }

    private Spy2 createPlayer(int id, String keyword, String role) {
        return Spy2.builder()
                .id(id)
                .keyword(keyword)
                .role(role)
                .build();
    }

    // Hàm kiểm tra và báo các cặp bị lặp lại (bất kể thứ tự)
    public void checkDuplicatePairs() {

        Set<String> seen = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (List<String> pair : keywords) {
            String a = pair.get(0).trim();
            String b = pair.get(1).trim();
            String key = a.compareTo(b) < 0 ? a + "|" + b : b + "|" + a;
            if (seen.contains(key)) {
                duplicates.add(key);
            } else {
                seen.add(key);
            }
        }
        if (duplicates.isEmpty()) {
            System.out.println("Không có cặp nào bị lặp lại.");
        } else {
            System.out.println("Các cặp bị lặp lại (bất kể thứ tự):");
            for (String dup : duplicates) {
                System.out.println(dup.replace("|", " - "));
            }
        }
    }
}
