package com.example.spyfall.controller;

import com.example.spyfall.common.DataMember;
import com.example.spyfall.common.SoiNguyenDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/ms")
public class MaSoiController {
    List<DataMember> datas = new ArrayList<>();
    List<DataMember> pls = new ArrayList<>();
    List<String> idSoi = List.of("1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11", "12", "13", "14", "15");
    List<String> idOutsider = List.of("21", "20", "22");
    List<DataMember> listShowForMember = new ArrayList<>();
    boolean checkDecreaseSoi = false;
    boolean isGameEnd = true;
    boolean isAddSoiNguyen = false;
    boolean isHaveSoiNguyen = false;
    Map<Integer, List<DataMember>> detailEachGame = new TreeMap<>(Comparator.reverseOrder());
    String image = "/qrcode.png";


    @GetMapping("/run")
    String create() {
        prepareData();
        StringBuilder result = new StringBuilder();

        result.append("<img src=\"").append(image).append("\" alt=\"QR Code\" width=\"500\" height=\"500\">");
        result.append("<form id=\"dataForm\" style=\"font-size: 150%;\">");

        // them so luong
        result.append("<label for=\"total\" style=\"width: 35%; display: inline-block; text-align: right; margin: 10px 0; font-size: 150%;\">")
                .append("Total người chơi").append(": </label>");
        result.append("<input type=\"number\" min=\"0\" value=\"0\" id=\"total\" name=\"total\" ")
                .append("style=\"margin: 10px 0; font-size: 150%;\"><br>");
        result.append("<label for=\"sum\" style=\"width: 35%; display: inline-block; text-align: right; margin: 10px 0; font-size: 150%;\">Sum: </label>");
        result.append("<input type=\"number\" id=\"sum\" readonly style=\"margin: 10px 0; font-size: 150%;\"><br>");

        result.append("<table border=\"1\" style=\"width: 90%; margin-top: 20px; border-collapse: collapse; font-size: 150%;\">");
        for (int i = 0; i < datas.size(); i += 2) {
            result.append("<tr>");
            // Cột 1 (phần tử đầu tiên trong cặp)
            addDataTableRun(result, datas.get(i));
            // Cột 2 (phần tử thứ hai trong cặp)
            if (i + 1 < datas.size()) {
                addDataTableRun(result, datas.get(i+1));
            } else {
                // Nếu không đủ phần tử cho cột thứ hai, để trống
                result.append("<td></td>");
            }
            result.append("</tr>");
        }
        // Đóng bảng
        result.append("</table>");

        result.append("<br>");
        // them check soi nguyen checkSóiNguyền
        result.append("<label style=\"font-size: 200%;\">"
                + "<input style=\"transform: scale(2); margin: 10px 0;\" type=\"checkbox\" id=\"checkSoi\" >"
                + " Check chơi sói nguyền"
                + "</label> <br>");
        result.append("<br>");
        // them check soi nguyen checkSóiNguyền
        result.append("<label style=\"font-size: 200%;\">"
                + "<input style=\"transform: scale(2); margin: 10px 0;\" type=\"checkbox\" id=\"checkCupid\" >"
                + " Cupid"
                + "</label> <br> <br> <br>");

        result.append("<button type=\"button\" id=\"sendButton\" style=\"font-size: 220%; margin-top: 20px;\">Send</button>");
        result.append("</form>");

        result.append("<script>");
        result.append("function updateSum() {");
        result.append("    var sumTotal = 0;");
        result.append("    var inputs = document.querySelectorAll('input[type=\"number\"]:not(#sum):not(#total)');"); // Chọn tất cả input số");
        result.append("    inputs.forEach(function(input) {");
        result.append("        sumTotal += parseFloat(input.value) || 0;"); // Tính tổng, nếu không phải số thì thêm 0");
        result.append("    });");
        result.append("    var checkboxes = document.querySelectorAll('input[type=\"checkbox\"]:checked');"); // Lấy tất cả checkbox đã được chọn

        // Duyệt qua các checkbox đã được chọn và cộng tổng
        result.append("    checkboxes.forEach(function(checkbox) {");
        result.append("    sumTotal += parseInt(checkbox.value);");
        result.append("    });");
        result.append("    document.getElementById('sum').value = sumTotal;"); // Cập nhật tổng vào trường sum
        result.append("}");

        result.append("document.getElementById('sendButton').onclick = function() {");
        result.append("    var formData = new URLSearchParams();");

        result.append("    var inputs = document.querySelectorAll('input[type=\"number\"]');");
        result.append("    inputs.forEach(function(input) {");
        result.append("        formData.append(input.name, input.value);");
        result.append("    });");
        result.append("    var checkboxes = document.querySelectorAll('input[type=\"checkbox\"]:checked');"); // Lấy tất cả checkbox đã được chọn
        result.append("    checkboxes.forEach(function(checkbox) {");
        result.append("        formData.append(checkbox.name, 1);");
        result.append("    });");

        result.append("    var checkbox = document.getElementById(\"checkSoi\");"
                + "    if (checkbox.checked) {"
                + "        formData.append('checkSoi', 'true');"
                + "    } else {"
                + "        formData.append('checkSoi', 'false');"
                + "    }");

        result.append("    var checkbox = document.getElementById(\"checkCupid\");"
                + "    if (checkbox.checked) {"
                + "        formData.append('checkCupid', 'true');"
                + "    } else {"
                + "        formData.append('checkCupid', 'false');"
                + "    }");

        result.append("    fetch('/ms/create?' + formData.toString(), { method: 'GET' })");
        result.append("        .then(response => response.text())");
        result.append("        .then(data => {");
        result.append("            alert('Response: ' + data);");
        result.append("            if (!data.includes('ERROR')) {");  // Kiểm tra nếu không có từ 'error' trong data");
        result.append("                window.location.href = '/ms/admin';");
        result.append("            }");
        result.append("        })");
        result.append("        .catch(error => console.error('Error:', error));");
        result.append("};");
        result.append("</script>");
        return result.toString();
    }

    @GetMapping("/create")
    String loadData(@RequestParam Map<String, String> params) {
        if (isGameEnd) {

            int totalPlay = Integer.parseInt(params.get("total"));
            boolean isSoiNguyen = Boolean.parseBoolean(params.get("checkSoi"));
            boolean isCupid = Boolean.parseBoolean(params.get("checkCupid"));
            int totalRole = 0;
            if (totalPlay == 0) {
                return "ERROR LỖI KHÔNG TỔNG NGƯỜI";
            }
            isGameEnd = false;
            prepareData();
            pls.clear();
            listShowForMember.clear();
            checkDecreaseSoi = false;

            for (DataMember data :datas) {
                int total = 0;
                if (params.get(data.getId()) != null) {
                    total = Integer.parseInt(params.get(data.getId())); // id la khoa chinh
                }
                if (total >= 1) {
                    listShowForMember.add(createDM(data, total));
                }
                for (int i = 0; i < total; i++) {
                    data.setIpData("play");
                    pls.add(DataMember.builder()
                            .id(data.getId())
                            .idPlayGame(String.valueOf(pls.size() + 1))
                            .location(data.getLocation())
                            .description(data.getDescription())
                            .build());
                }
                totalRole += total;
                System.out.println(data.getLocation() + ": " + total);
            }
            // Neu role nhieu hon nguoi choi thi bo so luong tuong ung ra cho can bang
            Random random = new Random();
            for (int i = 0; i < totalRole - totalPlay; i++) {
                int indexMember = random.nextInt(pls.size());
                // kiem tra so bi tru co phai la soi
                if (idSoi.contains(pls.get(indexMember).getId())) {
                    checkDecreaseSoi = true;
                }
                pls.remove(indexMember);
            }

            pls.forEach(item -> {
                if (StringUtils.pathEquals(item.getId(), "3")) { // kiem tra xem co soi nguyen ko de admin add vao data
                    isHaveSoiNguyen = true;
                }
            });

            // neu soi bi giam thi se la soi nguyen
            if (isSoiNguyen && checkDecreaseSoi) {
                for (DataMember item: pls) {
                    String name = "Sói Nguyền";
                    String description = "sói nguyền";
                    if (idSoi.contains(item.getId())) {
                        // them chuc nang nguyen
                        item.setLocation(item.getLocation() + " + " + name);
                        item.setDescription(item.getDescription() + description);
                        isHaveSoiNguyen = true;
                    }

                }
            }

            if(isCupid) {
                Collections.shuffle(pls);
                List <DataMember> cupid = pls.subList(0, 2);
                String cupid1 = cupid.get(0).getLocation();
                String cupid2 = cupid.get(1).getLocation();
                cupid.get(0).setLocation(cupid1 + "<i id=\"cupid\"> cặp đôi với " + cupid2 + "</i>");
                cupid.get(1).setLocation(cupid2 + "<i id=\"cupid\"> cặp đôi với " + cupid1 + "</i>");
                System.out.println(cupid);
            }
            return "OK load";
        } else {
            return "GAME NOT END YES";
        }

    }

    @GetMapping({"/play/" , "/play/{name}"})
    String test(HttpServletRequest request, @PathVariable(required = false) String name) {
        prepareData();
        StringBuilder result = new StringBuilder();
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        Integer ipAssigned = null;
        DataMember yourLocation;
        try {
            if (!pls.isEmpty()) {
                for (DataMember dataMember : pls) {
                    if (clientIp.equals(dataMember.getIpData())) {
                        ipAssigned = pls.indexOf(dataMember);
                        break;
                    }
                }
            }

            if (ipAssigned != null) {
                result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">")
                        .append(pls.get(ipAssigned).getLocation()).append("</h1>");
                if (name != null) {
                    pls.get(ipAssigned).setNameMember(name);
                }
            } else {
                if (!pls.isEmpty()) {
                    Collections.shuffle(pls);
                    pls.sort(Comparator.comparing((DataMember m) -> m.getIpData(), Comparator.nullsFirst(String::compareTo))); // sort nhung id nao chua dc chon cho len dau
                    yourLocation = pls.get(0);

                    yourLocation.setIpData(clientIp);
                    yourLocation.setNameMember(name);
                    result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">").append(yourLocation.getLocation()).append("</h1>");
                } else {
                    result.append("<h1 style=\"font-size: 500%; \" > Loading </h1>");
                }

            }
            result.append("<label style=\"font-size: 250%;\">"
                    + "<input type=\"checkbox\" id=\"showTextCheckbox\" onchange=\"toggleText()\" >"
                    + " Show/Hide Chức năng - Game " + (detailEachGame.size() + 1)
                    + "</label>");
            result.append("<button type=\"button\" id=\"showHistory\" style=\"font-size: 250%; float: right;\">History</button>");

            showDescription(listShowForMember, result, false);

            result.append("<div id=\"popupModal\" style=\"overflow: auto; resize: both; max-height: 80%; display: none; position: fixed; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 1000; background: white; border: 1px solid #ccc; box-shadow: 0px 4px 6px rgba(0, 0, 0, 0.1); width: 90%; max-width: 90%; padding: 20px;\">");
            result.append("<button id=\"closePopup\" style=\"font-size: 260%; margin-top: 20px; padding: 10px 20px; background: #ff4d4d; color: white; border: none; cursor: pointer;\">Close</button>");

            result.append("<div id=\"popupContent\">");
            result.append("</div>");
            result.append("</div>");

            result.append("<script>"
                    + "function toggleText() {"
                    + "    var checkbox = document.getElementById(\"showTextCheckbox\");"
                    + "    var cupid = document.getElementById(\"cupid\");"
                    + "    var textElement = document.getElementById(\"location\");"
                    + "    if (checkbox.checked) {"
                    + "        textElement.style.color = \"black\";"  // Chữ màu đen khi checkbox được chọn
                    + "        cupid.style.color = \"orange\";"
                    + "    } else {"
                    + "        textElement.style.color = \"white\";"  // Chữ màu trắng khi checkbox không được chọn
                    + "        cupid.style.color = \"white\";"
                    + "    }"
                    + "}"

                    + " document.getElementById('showHistory').onclick = function() {"
                    + "     const popupModal = document.getElementById('popupModal');"
                    + "     const popupContent = document.getElementById('popupContent');"
                    + "        fetch('/ms/showHistory', {"  // Chữ màu đen khi checkbox được chọn
                    + "             method: 'POST'"
                    + "        })"
                    + "        .then(response => response.text())"
                    + "        .then(responseData => {"
                    + "             popupContent.innerHTML = responseData;"
                    + "             popupModal.style.display = 'block';"
                    + "         })"
                    + "         .catch(error => {"
                    + "             console.error('Error:', error);"
                    + "         });"
                    + "};"

                    + "             document.getElementById('closePopup').onclick = function() {"
                    + "     const popupModal = document.getElementById('popupModal');"
                    + "     const popupContent = document.getElementById('popupContent');"
                    + "                     popupModal.style.display = 'none';"
                    + "             }"
                    + "</script>");

            result.append("<img src=\"").append(image).append("\" alt=\"QR Code\" width=\"500\" height=\"500\">");
            return result.toString();
        } catch (Exception e) {
            result.append("<h1 style=\"font-size: 500%;\"> Load</h1>");
//            showDescription(datas, result, false);
            return result.toString();
        }
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
        result.append("        window.location.href = '/ms/play/' + input.value;");
        result.append("    }");
        result.append("}");

        result.append("document.getElementById('sendButton').onclick = function() {");
        result.append("    handleSubmit(new Event('submit'));"); // Cho nút OK dùng lại
        result.append("};");
        result.append("</script>");

        result.append("<img src=\"").append(image).append("\" alt=\"QR Code\" width=\"500\" height=\"500\">");
        return result.toString();
    }


    @PostMapping("/showHistory")
    String showHistory() {
        StringBuilder result = new StringBuilder();
        detailEachGame.forEach((key, value) -> {
            result.append("<h3 style=\"font-size: 200%; \" > Game ").append(key).append(": </h3>");
            value.sort(Comparator.comparing((DataMember m) -> Integer.parseInt(m.getId())));
            result.append("<table border=\"1\" style=\"border-collapse: collapse; \">");
            value.forEach(item -> {
                String color;
                if (idSoi.contains(item.getId())) {
                    color = "red";
                } else if (idOutsider.contains(item.getId())) {
                    color = "orange";
                } else {
                    color = "green";
                }
                result.append("<tr >");
                result.append("<td style=\"font-weight: bold; text-align: left; padding-right: 5px; font-size: 300%; width: 30%; color: " + color + ";\">")
                        .append(item.getLocation()).append(": ")
                        .append("</td>");
                if (item.getNameMember() == null && item.getIpData() != null) {
                    result.append("<td style=\"font-size: 300%; \"> Đã giấu tên</br> </td>");
                } else if (item.getNameMember() == null) {
                    result.append("<td style=\"font-size: 300%; \"> Chưa ai chọn</br> </td>");
                } else {
                    result.append("<td style=\"font-size: 300%; \">").append(item.getNameMember()).append("</br> </td>");
                }
                result.append("<td style=\"padding-bottom: 12%;\"/>");
                result.append("</tr>");
            });
            result.append("</table>");
        });

        return result.toString();
    }

    @PostMapping("/end")
    String endGame() {
        detailEachGame.put(detailEachGame.size() + 1, new ArrayList<>(pls));
        isGameEnd = true;
        isAddSoiNguyen = false;
        pls.clear();
        listShowForMember.clear();
        checkDecreaseSoi = false;
        return "End Game : " + detailEachGame.size();
    }

    private void prepareData() {
        if (datas.isEmpty()) {
            datas.add(createDM("1", "Sói", "Sói đó, sói nè, sói chính hiệu 9999"));
            datas.add(createDM("2", "Sói đầu đàn", "Sói có quyền quyết định cắn ai"));
            datas.add(createDM("3", "Sói nguyền", "Nguyền 1 người chơi, người đó sẽ là sói"));
            datas.add(createDM("4", "Sói đầu đàn(2)", "Sói tiên tri phải soi 2 lần"));
            datas.add(createDM("5", "Sói si đa", "1 lần duy nhất chức năng nào chạm vào bạn thì chức năng đó chết"));
            datas.add(createDM("6", "Kẻ phản bội", "Theo phe sói, biết sói là ai, sói không biết bạn"));

            datas.add(createDM("20", "Sát thủ", "Phe thứ 3, có quyền giết sói nhưng sói ko giết lại được. Nếu bị sói nguyền và là thành viên còn sống duy nhất thì mỗi đêm giết 2 người"));
            datas.add(createDM("21", "Chán đời", "Muốn chết nhưng ko muốn bị sói cắn. Khi bị treo cổ bạn sẽ win"));

            datas.add(createDM("30", "Dân", "Dân giàu nước mạnh"));
            datas.add(createDM("31", "Phù thủy", "Có 1 bình cứu và 1 bình giết. Khi đã sài phép cứu thì sẽ ko biết ai bị giết nữa"));
            datas.add(createDM("32", "Tiên tri", "Soi 1 người có phải Sói hay ko"));
            datas.add(createDM("33", "Bảo vệ", "Mỗi đêm bảo vệ 1 người, ko bảo vệ 1 người 2 đêm liên tiếp"));
            datas.add(createDM("34", "Thợ săn", "Ghim 1 người, chỉ trong đêm nếu bạn chết người đó chết theo"));
            datas.add(createDM("35", "Thanh niên cứng", "Khi bị treo cổ có quyền lật bài và giết 1 người, bạn vẫn sống như bình thường"));

            datas.add(createDM("37", "Tiên tri tập sự", "Khi tiên tri chết thì bạn là Tiên tri"));
            datas.add(createDM("38", "Câm lặng", "Chọn 1 người và cấm phép người đó. Nếu chọn sói đầu đàn thì sói sẽ ko cắn dc người, nếu sói con thì phải là sói duy nhất"));
            datas.add(createDM("39", "Thám tử", "Chọn 1 vùng 3 người, bạn sẽ biết có sói trong đó hay không"));
            datas.add(createDM("40", "Bị nguyền", "Bạn theo phe dân, nếu bị sói cắn sẽ thành sói"));
            datas.add(createDM("41", "Người bệnh", "Nếu sói/sát thủ cắn bạn, thì đêm sau sói/sát thủ không giết được ai"));
            datas.add(createDM("42", "Nhân bản", "Chọn 1 người, nếu người đó chết bạn sẽ nhận chức năng người đó"));
            datas.add(createDM("43", "Độc tài", "Duy nhất: chọn 1 người chơi, nếu không phải dân(- chán đời) người đó chết, nếu dân bạn chết"));
            datas.add(createDM("44", "Thiên thần", "1 lần duy nhất có thể ngăn chặn toàn bộ cái chết trong đêm"));
            datas.add(createDM("45", "Phù thủy già", "mỗi ngày đuổi 1 người ko phải mình ra khỏi làng"));
            datas.add(createDM("46", "Boooooom", "Duy nhất chọn 1 người chơi giao bom, mỗi đêm bạn có quyền kích nổ hoặc ko, sáng ra người cầm bom sẽ chuyển bom đi, nếu chết có quyền chọn chuyển bom hoặc ko"));

        }
    }

    private DataMember createDM(String id, String location, String description) {
        return DataMember.builder()
                .id(id)
                .location(location)
                .description(description)
                .build();
    }

    private DataMember createDM(DataMember dataMember, Integer total) {
        return DataMember.builder()
                .id(dataMember.getId())
                .location(dataMember.getLocation())
                .description(dataMember.getDescription())
                .total(String.valueOf(total))
                .build();
    }

    @GetMapping("/admin")
    String admin() {
        prepareData();
        StringBuilder result = new StringBuilder();
        result.append("<img src=\"").append(image).append("\" alt=\"QR Code\" width=\"500\" height=\"500\">");
        result.append("<h1 style=\"font-size: 400%; \">");
        for (DataMember item: pls) {
            if (!item.getId().equals("1") && !item.getId().equals("30")) {
                result.append(item.getLocation()).append(", ");
            }

        }
        result.append("</h1>");



        result.append("<table border=\"1\" style=\"border-collapse: collapse; \">");
        pls.sort(Comparator.comparing((DataMember m) -> Integer.parseInt(m.getId())));
        pls.forEach(item -> {
            result.append("<tr >");
            result.append("<td style=\"font-weight: bold; text-align: left; padding-right: 5px; font-size: 200%; width: 30%;\">")
                    .append(item.getIdPlayGame())
                    .append("-")
                    .append(item.getLocation()).append(": ")
                    .append("</td>");
            if (item.getNameMember() == null && item.getIpData() != null) {
                result.append("<td style=\"font-size: 250%; \"> Đã giấu tên</br> </td>");
            } else if (item.getNameMember() == null) {
                result.append("<td style=\"font-size: 250%; \"> Chưa ai chọn</br> </td>");
            } else {
                result.append("<td style=\"font-size: 250%; \">").append(item.getNameMember()).append("</br> </td>");
            }
            result.append("<td style=\"padding-bottom: 12%;\"/>");
            result.append("</tr>");
        });
//        Map<String, List<String>> selectData = createMapSelect(); //TODO update detail every night
        result.append("</table>");

//        result.append("<button id='addButton' type='button' style='font-size: 16px; margin-top: 20px;'>Add</button>");

        if (isHaveSoiNguyen && !isAddSoiNguyen) {
            result.append("    <select class=\"selectSoiNguyen\" style=\"margin-right: 10px; font-size: 300%;\">';");
            pls.forEach(item -> {
                String name = (item.getNameMember() == null || item.getNameMember().isEmpty()) ? "" : item.getNameMember().substring(0, 1).toUpperCase() + item.getNameMember().substring(1);
                result.append("    newRow += '<option value=\"").append(item.getIdPlayGame()).append("\">").append(item.getIdPlayGame() + "-").append(item.getLocation()).append("-").append(name).append("</option>';");

            });
            result.append("  '</select>';");

            result.append("<button id='addSoiNguyen' type='button' style='font-size: 300%; margin-top: 20px; '>Add Soi Nguyen</button>");
        }

        result.append("<div id='selectContainer'></div>");
        result.append("<button id='endGame' type='button' style='font-size: 400%; margin-top: 20px; background-color: red; color: white;'>End Game</button>");

        result.append("<script>");
        //TODO update detail every night
//        result.append("    var selectedValue = 1; ");
//        result.append("document.getElementById('addButton').addEventListener('click', function() {");
//        result.append("    var newRow = '<div style=\"margin-bottom: 10px;\">';");
//        result.append("    newRow += '<select class=\"select1\" style=\"margin-right: 10px;\">';");
//        selectData.forEach((key, value) -> {
//            result.append("    newRow += '<option value=\"").append(key).append("\">").append(key).append("</option>';");
//        });
//        result.append("    newRow += '</select>';");
//        result.append("    newRow += '<select class=\"select2\" style=\"margin-right: 10px;\">';");
//        result.append("    newRow += '<option value=\"No option\">No option</option>';");
//        result.append("    newRow += '</select>';");
//        result.append("    newRow += '<select class=\"select3\" style=\"margin-right: 10px;\">';");
//        pls.forEach(item -> {
//            String name = (item.getNameMember() == null || item.getNameMember().isEmpty()) ? "" : item.getNameMember().substring(0, 1).toUpperCase() + item.getNameMember().substring(1);
//            result.append("    newRow += '<option value=\"").append(item.getLocation()).append("-").append(name).append("\">").append(item.getLocation()).append("-").append(name).append("</option>';");
//        });
//        result.append("    newRow += '</select>';");
//
//        result.append("    newRow += '<select class=\"select4\" style=\"margin-right: 10px;\">';");
//        for (int i = 1; i < 14; i++) {
////            result.append("    newRow += '<option value=\"").append(i).append("\">").append("Đêm - " + i).append("</option>';");
//            result.append("        if (" + i + " == selectedValue) {");
//            result.append("            newRow += '<option value=\"").append(i).append("\" selected >").append("Đêm - ").append(i).append("</option>';");
//            result.append("        } else {");
//            result.append("            newRow += '<option value=\"").append(i).append("\">").append("Đêm - ").append(i).append("</option>';");
//            result.append("        }");
//        }
//
//        result.append("    newRow += '</select>';");
//
//        result.append("    newRow += '</div>';");
//        result.append("    document.getElementById('selectContainer').innerHTML += newRow;");
//
//        result.append("    var select1s = document.querySelectorAll('.select1');");
//        result.append("    var select2s = document.querySelectorAll('.select2');");
//        result.append("    var select4s = document.querySelectorAll('.select4');");
//
//        result.append("    select1s.forEach((select1, index) => {");
//        result.append("        var select2 = select2s[index];");
//        result.append("        select1.addEventListener('change', function() {");
//        result.append("            select2.innerHTML = ''; ");
//        selectData.forEach((key, value) -> {
//            result.append("            if (select1.value === '").append(key).append("') {");
//            value.forEach(item -> result.append("                select2.innerHTML += '<option value=\"").append(item).append("\">").append(item).append("</option>';"));
//            result.append("            }");
//        });
//        result.append("        });");
//        result.append("    });");
//
//        result.append("});");
        if (isHaveSoiNguyen && !isAddSoiNguyen) {
            result.append("document.getElementById('addSoiNguyen').addEventListener('click', function() {");
            result.append("    var soiNguyen = document.querySelector('.selectSoiNguyen');");
            result.append("    var data = {");
            result.append("        soiNguyen: soiNguyen.value,");
            result.append("    };");
            result.append("    fetch('/ms/soiNguyen', {");
            result.append("        method: 'POST',");
            result.append("        headers: {");
            result.append("            'Content-Type': 'application/json'");
            result.append("        },");
            result.append("        body: JSON.stringify(data)");
            result.append("    })");
            result.append("    .then(response => response.text())");
            result.append("    .then(responseData => {");
            result.append("         alert('Response: ' + responseData);");

            result.append("    })");
            result.append("    .catch(error => {");
            result.append("        console.error('Error:', error);");
            result.append("    });");
            result.append("});");
        }

        result.append("document.getElementById('endGame').addEventListener('click', function() {");
        result.append("    const userConfirmed = confirm(\"Bạn có chắc chắn muốn END GAME?\");");
        result.append("    if (userConfirmed) { ");
        result.append("         fetch('/ms/end', {");
        result.append("             method: 'POST'");
        result.append("         })");
        result.append("         .then(response => response.text())");
        result.append("         .then(responseData => {");
        result.append("                 alert('Response: ' + responseData);");
        result.append("                 window.history.back();");

        result.append("         })");
        result.append("         .catch(error => {");
        result.append("             console.error('Error:', error);");
        result.append("         });");
        result.append("    } else { ");
        result.append("         console.log(\"Người dùng đã hủy hành động.\"); ");
        result.append("    } ");
        result.append("});");
        result.append("</script>");

        return result.toString();

    }

    @PostMapping("/soiNguyen")
    String soiNguyen(@RequestBody SoiNguyenDto soiNguyen) {
        if (isAddSoiNguyen) {
            return "Soi Nguyen Da Add TRUOc DO";
        }
        isAddSoiNguyen = true;
        AtomicReference<String> soiNguyenName = new AtomicReference<>("");
        pls.forEach(item -> {
            if (StringUtils.pathEquals(item.getIdPlayGame(), soiNguyen.getSoiNguyen())) {
                item.setId("1"); //Set la soi
                item.setLocation(item.getLocation() + " - Soi");
                soiNguyenName.set(item.getLocation());
            }
        });
        return "add Soi Nguyen: " + soiNguyenName;
    }


    @GetMapping("/admin2")
    String admin2() {
        prepareData();
        StringBuilder result = new StringBuilder();
        showDescription(datas, result, true);
        return result.toString();

    }

    private void showDescription(List<DataMember> list, StringBuilder result, Boolean isAdmin) {
        result.append("<h3></h3>");
        result.append("<table border=\"1\" style=\"border-collapse: collapse; border: blue; border-inline-end: none\">");
        list.forEach(item -> {
            result.append("<tr >");
            if (idSoi.contains(item.getId())) {
                result.append("<td style=\"font-weight: bold; text-align: left; padding-right: 5px; font-size: 200%; color: red; \">");
            } else if (idOutsider.contains(item.getId())) {
                result.append("<td style=\"font-weight: bold; text-align: left; padding-right: 5px; font-size: 200%; color: orange; \">");
            } else {
                result.append("<td style=\"font-weight: bold; text-align: left; padding-right: 5px; font-size: 200%; \">");
            }

            if (isAdmin) {
                result.append(item.getId()).append(" ");
                result.append(item.getLocation());
            } else {
                result.append(item.getLocation()).append(": ").append((item.getId().equals("1") || item.getId().equals("30") || !item.getTotal().equals("1")) ? item.getTotal() : "");
            }

            result.append("</td>");
            result.append("<td style=\"font-size: 250%; \">").append(item.getDescription()).append("</br> </td>");
            result.append("<td style=\"padding-bottom: 15%;\"/>");
            result.append("</tr>");
        });
        result.append("</table>");
    }

    private void addDataTableRun(StringBuilder result, DataMember item) {
        result.append("<td style=\"height: 110px;\">");
        if (item.getId().equals("1") || item.getId().equals("30")) {
            result.append("<label style=\"margin-right: 10px; color: red; font-size: 100%;\" for=\"")
                    .append(item.getId()).append("\">")
                    .append(item.getLocation().toUpperCase()).append(": </label>");
            result.append("<input type=\"number\" min=\"0\" value=\"0\" id=\"").append(item.getId()).append("\" name=\"").append(item.getId()).append("\" ")
                    .append("style=\"font-size: 100%; width: 40%;\" oninput=\"updateSum()\"><br>");
        } else if (idSoi.contains(item.getId())){
            result.append("<label style=\"margin-right: 10px; margin-bottom: 15px; color: red; font-size: 100%;\" for=\"")
                    .append(item.getId()).append("\">")
                    .append(item.getLocation().toUpperCase()).append(": </label>");
            result.append("<input style=\"transform: scale(2); margin: 10px 0;\" type=\"checkbox\" id=\"").append(item.getId()).append("\" name=\"")
                    .append(item.getId()).append("\" value=\"1\" onclick=\"updateSum()\"><br>");
        } else if (idOutsider.contains(item.getId())){
            result.append("<label style=\"margin-right: 10px; margin-bottom: 15px; color: orange; font-size: 100%;\" for=\"")
                    .append(item.getId()).append("\">")
                    .append(item.getLocation().toUpperCase()).append(": </label>");
            result.append("<input style=\"transform: scale(2); margin: 10px 0;\" type=\"checkbox\" id=\"").append(item.getId()).append("\" name=\"")
                    .append(item.getId()).append("\" value=\"1\" onclick=\"updateSum()\"><br>");
        } else {
            result.append("<label style=\"margin-right: 10px; margin-bottom: 15px; color: green; font-size: 100%;\" for=\"")
                    .append(item.getId()).append("\">")
                    .append(item.getLocation().toUpperCase()).append(": </label>");
            result.append("<input style=\"transform: scale(2); margin: 10px 0;\" type=\"checkbox\" id=\"").append(item.getId()).append("\" name=\"")
                    .append(item.getId()).append("\" value=\"1\" onclick=\"updateSum()\"><br>");
        }

        if (item.getId().equals(idSoi.get(idSoi.size() - 1)) || item.getId().equals("21")) {
            result.append("<br>");
        }
        result.append("</td>");
    }

    private Map<String, List<String>> createMapSelect() {
        Map<String, List<String>> result = new HashMap<>();
        List<String> soi = List.of("Giết", "Nguyền", "Lây bệnh");
        List<String> soiSida = List.of("Lây covid");

        List<String> satThu = List.of("Giết");

        List<String> phuthuy = List.of("Giết", "Bảo vệ");
        List<String> baove = List.of("Bảo vệ");
        List<String> thoSan = List.of("Ghim");
        List<String> camLang = List.of("Câm lặng");
        List<String> nhanBan = List.of("Chọn");
        List<String> docTai = List.of("Giết");
        List<String> thienThan = List.of("Bảo vệ");
        List<String> bom = List.of("Giết");
        result.put("--Select--", List.of("--Select--"));
        result.put("Sói", soi);
        listShowForMember.forEach(item -> {
            if ("5".equals(item.getId())) {
                result.put(item.getLocation(), soiSida);
            } else if ("20".equals(item.getId())) {
                result.put(item.getLocation(), satThu);
            } else if ("31".equals(item.getId())) {
                result.put(item.getLocation(), phuthuy);
            } else if ("33".equals(item.getId())) {
                result.put(item.getLocation(), baove);
            } else if ("34".equals(item.getId())) {
                result.put(item.getLocation(), thoSan);
            } else if ("38".equals(item.getId())) {
                result.put(item.getLocation(), camLang);
            } else if ("42".equals(item.getId())) {
                result.put(item.getLocation(), nhanBan);
            } else if ("43".equals(item.getId())) {
                result.put(item.getLocation(), docTai);
            } else if ("44".equals(item.getId())) {
                result.put(item.getLocation(), thienThan);
            } else if ("46".equals(item.getId())) {
                result.put(item.getLocation(), bom);
            }
        });

        return result;
    }
}
