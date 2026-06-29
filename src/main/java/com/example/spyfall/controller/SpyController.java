package com.example.spyfall.controller;

import com.example.spyfall.common.DataMember;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
public class SpyController {

    List<DataMember> dataPlay = new ArrayList<>();
    Map<String, DataMember> dataPlayMap = new HashMap();
     String data = "Bệnh viện, " +
            "Nhà hát, " +
            "Sân bay, " +
            "Trường học, " +
            "Nhà hàng, " +
            "Trung Tâm giải trí, " +
            "Trạm không gian, " +
            "Rạp xiếc, " +
            "Ngân hàng, " +
            "Trung tâm mua sắm, " +
            "Sở cảnh sát, " +
            "Khách sạn, " +
            "Bãi biển, " +
            "Nhà thờ, " +
            "Sân vận động, " +
            "Nhà tù, " +
            "Trạm xăng, " +
            "Công viên quốc gia, " +
            "Nhà ga xe lửa, " +
            "Sở thú, " +
            "Bảo tàng, " +
             "Chùa";

    String data2 = "Bệnh viện, " +
            "Sân bay, " +
            "Trường học, " +
            "Trung Tâm giải trí, " +
            "Rạp xiếc, " +
            "Ngân hàng, " +
            "Trung tâm mua sắm, " +
            "Khách sạn, " +
            "Bãi biển, " +
            "Sân vận động, " +
            "Nhà tù, " +
            "Quán cà phê, " +
            "Nhà hát, " +
            "Bảo tàng, " +
            "Thư viện, " +
            "Khu cắm trại, " +
            "Phòng gym, " +
            "Ga tàu, " +
            "Nhà thờ, " +
            "Chùa, " +
            "Nhà sách, " +
            "Rạp phim, " +
            "Trung tâm tiệc cưới, " +
            "Công viên nước, " +
            "Thủy cung, " +
            "Chợ đêm, " +
            "Hiệu thuốc, " +
            "Quán bar, " +
            "Karaoke, " +
            "Nhà kho, " +
            "Đồn công an, " +
            "Phố đi bộ, " +
            "Hồ bơi, " +
            "Nhà hàng, " +
            "Công viên, " +
            "Sở thú";

    List<DataMember> memberWillPlay = new ArrayList<>(); // la list nguoi choi

    List<DataMember> memberPlay = new ArrayList<>(); // la chung ta se choi

    List<DataMember> listItemOfSpy = new ArrayList<>();

    DataMember allMemberData = null;

    DataMember spyMemberData = null;
    Path outputPath = Paths.get(System.getProperty("user.dir"));//.resolve("/sdcard/java/spy.png");
    String image ="/spy.png";
    

    @GetMapping({"/sp", "/sp/"})
    String test(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }

        System.out.println(clientIp);
        Integer ipAssigned = null;
        try {
            if (!memberPlay.isEmpty()) {
                for (DataMember dataMember : memberPlay) {
                    if (dataMember.getIpData().equals(clientIp)) {
                        ipAssigned = memberPlay.indexOf(dataMember);
                        break;
                    }
                }
            }
            StringBuilder result = new StringBuilder();
            if (ipAssigned != null) {
                result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">").append(memberPlay.get(ipAssigned).getLocation()).append("</h1>");

            } else {
                Collections.shuffle(memberWillPlay);
                DataMember yourLocation = memberWillPlay.get(0);

                yourLocation.setIpData(clientIp);
                memberPlay.add(yourLocation);
                memberWillPlay.remove(0);
                System.out.println(yourLocation.getLocation());
                result.append("<h1 style=\"color: white; font-size: 500%; \" id=\"location\">").append(yourLocation.getLocation()).append("</h1>");
            }
            result.append("<label style=\"font-size: 350%;\">"
                    + "<input type=\"checkbox\" id=\"showTextCheckbox\" onchange=\"toggleText()\" >"
                    + " Show/Hide Location"
                    + "</label>");

            // Thêm bảng 2 cột và 2 hàng
            result.append("<table border=\"1\" style=\"width: 90%; margin-top: 20px; border-collapse: collapse; font-size: 250%;\">");

            // Chia thành 2 hàng, mỗi hàng có 2 cột
            for (int i = 0; i < dataPlay.size(); i += 2) {
                result.append("<tr>");
                // Cột 1 (phần tử đầu tiên trong cặp)
                result.append("<td>").append(dataPlay.get(i).getId()).append(". ").append(dataPlay.get(i).getLocation()).append("</td>");
                // Cột 2 (phần tử thứ hai trong cặp)
                if (i + 1 < dataPlay.size()) {
                    result.append("<td>").append(dataPlay.get(i + 1).getId()).append(". ").append(dataPlay.get(i + 1).getLocation()).append("</td>");
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

    @GetMapping({"/sp/{style}/{total}/{spy}", "/sp/{style}/{total}", "/sp/{style}/{total}/{spy}/{whiteHat}"})
    String start(@PathVariable Integer total, @PathVariable Integer style, @PathVariable(required = false) Integer spy, @PathVariable(required = false) Optional<Integer> whiteHat) {
        memberPlay.clear();
        memberWillPlay.clear();
        listItemOfSpy.clear();

        String[] datas = data2.split(",");
        for (String dataItem : datas) {
            boolean exist = dataPlay.stream()
                    .anyMatch(item -> item.getLocation().equals(dataItem));

            if (!exist) {
                int maxId = dataPlay.stream()
                        .mapToInt(o -> {
                            try {
                                return Integer.parseInt(o.getId());
                            } catch (NumberFormatException e) {
                                return 0;
                            }
                        })
                        .max()
                        .orElse(0);

                int newId = maxId + 1;
                DataMember dataMember = createDM(newId, dataItem);
                dataPlay.add(dataMember);
            }

        }

        Random random = new Random();
        int indexMember = random.nextInt(dataPlay.size());
        allMemberData = dataPlay.get(indexMember);

        listItemOfSpy = new ArrayList<>(dataPlay);
        listItemOfSpy.remove(indexMember);

        int indexSpy = random.nextInt(listItemOfSpy.size());
        spyMemberData = listItemOfSpy.get(indexSpy);

        System.out.println("data" + allMemberData.getLocation() + "spy" + spyMemberData.getLocation());
        if (spy == null) {
            for (int i = 1; i < total; i++) {
                memberWillPlay.add(createDM(total, allMemberData.getLocation()));
            }
            if (style.equals(1)) {
                memberWillPlay.add(createDM(total, spyMemberData.getLocation()));
            } else {
                memberWillPlay.add(createDM(total, "Spy đó nhìn nhìn cái gì"));
            }
        } else {
            int whiteHatNum = whiteHat.orElse(0);
            int totalWhiteHatAndSpy = whiteHatNum + spy;
            for (int i = 0; i < (total - totalWhiteHatAndSpy); i++) {
                memberWillPlay.add(createDM(total, allMemberData.getLocation()));
            }
            for (int i = 0; i < spy; i++) {
                if (style.equals(1)) {
                    memberWillPlay.add(createDM(total, spyMemberData.getLocation()));
                } else {
                    memberWillPlay.add(createDM(total, "Spy đó nhìn nhìn cái gì"));
                }
            }
            for (int i = 0; i < whiteHatNum; i++) {
                memberWillPlay.add(createDM(total, "Spy đó nhìn nhìn cái gì"));
            }
        }
        StringBuilder result = new StringBuilder();

        result.append("<table border=\"1\" style=\"width: 90%; margin-top: 20px; border-collapse: collapse; font-size: 250%;\">");

        // Chia thành 2 hàng, mỗi hàng có 2 cột
        for (int i = 0; i < dataPlay.size(); i += 2) {
            result.append("<tr>");
            // Cột 1 (phần tử đầu tiên trong cặp)
            result.append("<td>").append(dataPlay.get(i).getId()).append(". ").append(dataPlay.get(i).getLocation()).append("</td>");
            // Cột 2 (phần tử thứ hai trong cặp)
            if (i + 1 < dataPlay.size()) {
                result.append("<td>").append(dataPlay.get(i + 1).getId()).append(". ").append(dataPlay.get(i + 1).getLocation()).append("</td>");
            } else {
                // Nếu không đủ phần tử cho cột thứ hai, để trống
                result.append("<td></td>");
            }
            result.append("</tr>");
        }

        // Đóng bảng
        result.append("</table>");

        return "<h1 style=\"font-size: 500%;\"> Success</h1>" +
                "<h1 style=\"font-size: 500%;\"> 1. Spy co dia chi khac </h1>" +
                "<h1 style=\"font-size: 500%;\"> 2. Spy khong biet dia chi </h1>" +
                "<img src=\"" + image + "\" alt=\"QR Code\" width=\"500\" height=\"500\">" +
                result.toString();
    }

    @GetMapping("/sp/delete/{id}")
    String delete(@PathVariable Integer id) {
        for (DataMember item: dataPlay) {
            if (item.getId().trim().equals(id.toString())) {
                System.out.println(item.getLocation());
                dataPlay.remove(item);
                return "<h1 style=\"font-size: 500%;\"> da xoa" + item.getLocation() +"</h1>";
            }
        }
        return "<h1 style=\"font-size: 500%;\"> chua xoa</h1>";

    }

    @GetMapping("/sp/add/{name}")
    String test(@PathVariable String name) {
        int maxId = dataPlay.stream()
                .mapToInt(o -> {
                    try {
                        return Integer.parseInt(o.getId());
                    } catch (NumberFormatException e) {
                        return 0;
                    }
                })
                .max()
                .orElse(0);

        int newId = maxId + 1;
        DataMember dataMember = createDM(newId, name);
        dataPlay.add(dataMember);
        return "<h1 style=\"font-size: 500%;\"> da them</h1>";

    }


    private DataMember createDM(int id, String location) {
        return DataMember.builder()
                .id(String.valueOf(id))
                .location(location)
                .build();
    }

}
