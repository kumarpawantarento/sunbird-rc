package dev.sunbirdrc.registry.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class StudentMarkSheetTable {

    public static String getTableFromJson(JSONArray jsonArray) {
        Document doc = Jsoup.parse(""); // Create an empty HTML document
        Element div1 = doc.createElement("div");
        div1.addClass("marksDetails");
        div1.id("marksDetails");
        Element attemptTable2 = doc.createElement("table");

        Element attemptRowMain = doc.createElement("tr");
        Element attemptColMain = doc.createElement("td");
        attemptColMain.text("ATTEMPT(S)");
        attemptRowMain.appendChild(attemptColMain);
        Element attemptTable = doc.createElement("table");
        Element attemptColMain2 = doc.createElement("td");
        attemptColMain2.appendChild(attemptTable);
        attemptRowMain.appendChild(attemptColMain2);

        attemptTable2.appendChild(attemptRowMain);
        div1.appendChild(attemptTable2);
        //yearsOfTheCourse = yearsOfTheCourse.replace();
        // Parse the JSON data
//        JSONArray jsonArray = new JSONArray(yearsOfTheCourse);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject course = jsonArray.getJSONObject(i);

            Element marksTable = doc.createElement("table");
            Element innerTopTable = doc.createElement("table");
            Element headerTopRow = doc.createElement("tr");
            Element topRowCell = doc.createElement("td");
            topRowCell.text("YEAR OF THE COURSE: " + course.getString("courseYear"));
            topRowCell.addClass("border-0");
            headerTopRow.appendChild(topRowCell);

            Element topRowCell2 = doc.createElement("td");
            topRowCell2.text("Exam Month Year: " + course.getString("examMonth") + " " + course.getString("examYear"));
            topRowCell2.addClass("t-right border-0");
            headerTopRow.appendChild(topRowCell2);
            innerTopTable.appendChild(headerTopRow);

            Element attemptTableMain = doc.createElement("table");
            Element attemptRow = doc.createElement("tr");
            Element attemptRow2 = doc.createElement("tr");
            Element attempttopRowCell = doc.createElement("td");
            attempttopRowCell.text(course.getString("courseYear") + "-" + course.getString("examMonth") + "-" + course.getString("examYear") + ", " + course.getString("result"));
            attemptRow.appendChild(attempttopRowCell);
            attemptTable.appendChild(attemptRow);
            attemptTable.appendChild(attemptRow2);
            marksTable.addClass("firstYear mb-10");

            Element thead = doc.createElement("thead");
            Element headerRow = doc.createElement("tr");
            Element headerRow1 = doc.createElement("tr");
            Element headerCell1 = doc.createElement("th");
            headerCell1.attr("colspan", "2");
            headerCell1.text("Subject");
            Element headerCell2 = doc.createElement("th");
            headerCell2.text("Max Ext");
            Element headerCell3 = doc.createElement("th");
            headerCell3.text("Max Int");
            Element headerCell4 = doc.createElement("th");
            headerCell4.text("Obt Ext");
            Element headerCell5 = doc.createElement("th");
            headerCell5.text("Obt Int");
            Element headerCell6 = doc.createElement("th");
            headerCell6.text("Total");
            headerRow.appendChild(headerCell1);
            headerRow.appendChild(headerCell2);
            headerRow.appendChild(headerCell3);
            headerRow.appendChild(headerCell4);
            headerRow.appendChild(headerCell5);
            headerRow.appendChild(headerCell6);
            thead.appendChild(headerRow);

            Element hcYear = doc.createElement("td");
            hcYear.addClass("border-0");
            Element hcYear2 = doc.createElement("td");
            hcYear2.addClass("border-0");
            Element hcYear3 = doc.createElement("td");
            hcYear3.addClass("border-0");
            hcYear.attr("colspan", "1");
            hcYear.text("Result: " + course.getString("result"));
            hcYear2.attr("colspan", "4");
            hcYear2.text("TOTAL MARKS OBTAINED: " + course.getString("totalMarksObtainedInWord"));
            hcYear3.attr("colspan", "2");
            hcYear3.text(course.getString("totalMarksObtained") + "/" + course.getString("maxTotal"));
            headerRow1.appendChild(hcYear);
            headerRow1.appendChild(hcYear2);
            headerRow1.appendChild(hcYear3);

            marksTable.appendChild(thead);

            Element tbody = doc.createElement("tbody");
            int j = 1;
            JSONArray subjectArray = course.getJSONArray("subject");
            for (int k = 0; k < subjectArray.length(); k++) {
                JSONObject subject = subjectArray.getJSONObject(k);

                String rn = convertToRoman(j);
                j++;

                Element row = doc.createElement("tr");
                Element rowNumb = doc.createElement("td");
                rowNumb.text(rn);
                row.appendChild(rowNumb);

                Element subjectCell = doc.createElement("td");
                subjectCell.text(subject.getString("name"));
                row.appendChild(subjectCell);

                Element maxExtCell = doc.createElement("td");
                maxExtCell.text(subject.getString("max-ext"));
                row.appendChild(maxExtCell);

                Element maxIntCell = doc.createElement("td");
                maxIntCell.text(subject.getString("max-int"));
                row.appendChild(maxIntCell);

                Element obtExtCell = doc.createElement("td");
                obtExtCell.text(subject.getString("obt-ext"));
                row.appendChild(obtExtCell);

                Element obtIntCell = doc.createElement("td");
                obtIntCell.text(subject.getString("obt-int"));
                row.appendChild(obtIntCell);

                Element totalCell = doc.createElement("td");
                totalCell.text(subject.optString("total", ""));
                row.appendChild(totalCell);

                tbody.appendChild(row);
            }

            marksTable.appendChild(tbody);
            marksTable.appendChild(headerRow1);
            div1.appendChild(innerTopTable);
            div1.appendChild(marksTable);
        }

        div1.appendChild(attemptTable2);
        doc.appendChild(div1);

        Element html = doc.getElementById("marksDetails");

        String html1 = html.html();
        html1 = html1.replace("\n","");
        return html1;
    }

    private static String convertToRoman(int num) {
        int[] values = {1000,900,500,400,100,90,50,40,10,9,5,4,1};
        String[] romanLetters = {"M","CM","D","CD","C","XC","L","XL","X","IX","V","IV","I"};
        StringBuilder roman = new StringBuilder();
        for(int i=0;i<values.length;i++)
        {
            while(num >= values[i])
            {
                num = num - values[i];
                roman.append(romanLetters[i]);
            }
        }
        return roman.toString();
    }

    public static void main(String[] args) {
        String json = "[{\n" +
                "        \"courseYear\":\"First Year\",\n" +
                "        \"examYear\":\"2020\",\n" +
                "        \"examMonth\":\"March\",\n" +
                "        \"result\":\"PASS\",\n" +
                "        \"totalMarksObtained\":\"426\",\n" +
                "        \"totalMarksObtainedInWord\":\"Four hundred and Twenty Six\",\n" +
                "        \"maxTotal\":\"500\",\n" +
                "    \"subject\":[{\"name\":\"COMMUNITY HEALTH NURSING\",\"max-ext\":\"75\",\"max-int\":\"25\",\"obt-ext\":\"46\",\"obt-int\":\"20\",\"total\":\"66\"},\n" +
                "    {\"name\":\"HEALTH PROMOTION\",\"max-ext\":\"75\",\"max-int\":\"25\",\"obt-ext\":\"40\",\"obt-int\":\"19\",\"total\":\"59\"}]\n" +
                "    },\n" +
                "    {\n" +
                "        \"courseYear\":\"Second Year\",\n" +
                "        \"examYear\":\"2021\",\n" +
                "        \"examMonth\":\"March\",\n" +
                "        \"result\":\"PASS\",\n" +
                "        \"totalMarksObtained\":\"426\",\n" +
                "        \"totalMarksObtainedInWord\":\"Four hundred and Twenty Six\",\n" +
                "        \"maxTotal\":\"500\",\n" +
                "    \"subject\":[{\"name\":\"COMMUNITY HEALTH NURSING\",\"max-ext\":\"75\",\"max-int\":\"25\",\"obt-ext\":\"46\",\"obt-int\":\"20\",\"total\":\"66\"},\n" +
                "    {\"name\":\"HEALTH PROMOTION\",\"max-ext\":\"75\",\"max-int\":\"25\",\"obt-ext\":\"40\",\"obt-int\":\"19\",\"total\":\"66\"}]\n" +
                "    },\n" +
                "    {\n" +
                "        \"courseYear\":\"Final Year\",\n" +
                "        \"examYear\":\"2022\",\n" +
                "        \"examMonth\":\"March\",\n" +
                "        \"result\":\"PASS\",\n" +
                "        \"totalMarksObtained\":\"426\",\n" +
                "        \"totalMarksObtainedInWord\":\"Four hundred and Twenty Six\",\n" +
                "        \"maxTotal\":\"500\",\n" +
                "    \"subject\":[{\"name\":\"COMMUNITY HEALTH NURSING\",\"max-ext\":\"75\",\"max-int\":\"25\",\"obt-ext\":\"46\",\"obt-int\":\"20\",\"total\":\"66\"},\n" +
                "    {\"name\":\"HEALTH PROMOTION\",\"max-ext\":\"75\",\"max-int\":\"25\",\"obt-ext\":\"40\",\"obt-int\":\"19\",\"total\":\"66\"}]\n" +
                "    }]";

       // System.out.println(getTableFromJson(json));
    }


}


