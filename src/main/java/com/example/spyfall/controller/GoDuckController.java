package com.example.spyfall.controller;

import com.example.spyfall.common.GoDuck;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/gd")
public class GoDuckController {
    Map<String, GoDuck> memberPlay = new HashMap<>();
    Map<Integer, GoDuck> memberPlayData = new HashMap<>(); // Data de show ra cho ng dung
    Map<Integer, GoDuck> spyMember = new HashMap<>(); // Danh sach spy
    Map<Integer, GoDuck> spySeeMember = new HashMap<>(); // Danh sach spy nhin thay
    Integer totalPlay = 0; // so luong ng choi
    Integer spyTotalPlay = 0; // so luong spy
    String image ="/gDuck.png";

    private void clearData() {
        spyMember.clear();
        spySeeMember.clear();
        totalPlay = 0;
        spyTotalPlay = 0;
    }

    @GetMapping({"/play"})
    String playGetName() {
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
        result.append("        window.location.href = '/gd/play/' + input.value;");
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
    public String play(HttpServletRequest request, @PathVariable String name) {
        try {

            StringBuilder result = new StringBuilder();
            String clientIp = request.getHeader("X-Forwarded-For");
            if (clientIp == null || clientIp.isEmpty()) {
                clientIp = request.getRemoteAddr();
            }
            memberPlay.values().forEach(item -> {
                if (spyMember.containsKey(item.getId())) {
                    spyMember.put(item.getId(), item);
                } else if (spySeeMember.containsKey(item.getId())) {
                    spySeeMember.put(item.getId(), item);
                }
            });
            if (memberPlay.containsKey(clientIp)) {
                GoDuck memberDataCheck = memberPlay.get(clientIp);
                memberDataCheck.setUserName(name);
            } else {
                Integer idMember = memberPlay.size() + 1;
                GoDuck member = GoDuck.builder()
                        .ipConfig(clientIp)
                        .userName(name)
                        .id(idMember)
                        .build();
                memberPlay.put(clientIp, member);
            }
            Integer idMemberPlay = memberPlay.get(clientIp).getId();
            if (spyMember.containsKey(idMemberPlay)) {
                result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">");
                for (Map.Entry<Integer, GoDuck> entry : spySeeMember.entrySet()) {
                    result.append(Objects.toString(entry.getValue(), entry.getKey() + "; "));
                }
                result.append("</h1>");
            } else {
                result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">");
                for (Map.Entry<Integer, GoDuck> entry : spyMember.entrySet()) {
                    result.append(Objects.toString(entry.getValue(), entry.getKey() + "; "));
                }
                result.append("</h1>");
            }
            if (totalPlay == 0) {
                result.append("<h1 style=\" font-size: 500%; \"> Ngồi đợi xí Ní ơi</h1>");
            } else {
                result.append("<label style=\"font-size: 250%;\">"
                        + "<input type=\"checkbox\" id=\"showTextCheckbox\" onchange=\"toggleText()\" >"
                        + " Show/Hide Location"
                        + "</label>");
                result.append("<h1 style=\" font-size: 250%; \"> Số người chơi: " + totalPlay + ". Số Sói: " + spyTotalPlay  +"</h1>");
            }

            // Thêm bảng 2 cột và 2 hàng
            result.append("<table border=\"1\" style=\"width: 90%; margin-top: 20px; border-collapse: collapse; font-size: 250%;\">");

            // Chia thành 2 hàng, mỗi hàng có 2 cột
            List<GoDuck> memberData = memberPlay.values().stream().sorted(Comparator.comparing(GoDuck::getId)).toList();
            for (int i = 0; i < memberPlay.size(); i += 2) {
                result.append("<tr>");
                // Cột 1 (phần tử đầu tiên trong cặp)
                result.append("<td>").append(memberData.get(i).getId()).append(". ").append(memberData.get(i).getUserName()).append("</td>");
                // Cột 2 (phần tử thứ hai trong cặp)
                if (i + 1 < memberData.size()) {
                    result.append("<td>").append(memberData.get(i + 1).getId()).append(". ").append(memberData.get(i + 1).getUserName()).append("</td>");
                } else {
                    // Nếu không đủ phần tử cho cột thứ hai, để trống
                    result.append("<td></td>");
                }
                result.append("</tr>");
            }

            // Đóng bảng
            result.append("</table>");

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

    @GetMapping("/run/{total}/{spy}")
    public String play(@PathVariable Integer total, @PathVariable(required = false) Integer spy) {
        clearData();
        totalPlay = total;
        if (spy == null) {
            spyTotalPlay = 1;
        } else {
            spyTotalPlay = spy;
        }
        // SO luong ng choi lay ra so spy tuong ung
        List<Integer> numbers = new ArrayList<>();
        for (int i = 1; i <= totalPlay; i++) {
            numbers.add(i);
        }
        Collections.shuffle(numbers);
        for (int i = 0; i < spyTotalPlay; i++) {
            spyMember.put(numbers.remove(0), null);
            Collections.shuffle(numbers);
        }

        for (int i = 0; i < spyTotalPlay; i++) {
            spySeeMember.put(numbers.remove(0), null);
            Collections.shuffle(numbers);
        }
        return "<h1 style=\" font-size: 500%; \"> Total: " + total + "Spy: "+ spyTotalPlay + "</h1>";
    }
}
