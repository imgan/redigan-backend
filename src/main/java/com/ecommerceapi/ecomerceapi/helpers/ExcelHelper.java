package com.ecommerceapi.ecomerceapi.helpers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

import com.ecommerceapi.ecomerceapi.dto.request.AttributeItem.AttributeItemDTO;
import com.ecommerceapi.ecomerceapi.dto.request.Item.ItemDayImportDTO;
import com.ecommerceapi.ecomerceapi.dto.response.Item.ItemResImportDTO;
import com.ecommerceapi.ecomerceapi.model.AttributeItem;
import com.ecommerceapi.ecomerceapi.model.Item;
import com.ecommerceapi.ecomerceapi.model.Merchant;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.util.StringUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

public class ExcelHelper {

    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERs = { "Id", "Title", "Description", "Published","test","dwadwad","21321321","213213adwa" };
    static String SHEET = "test";

    public static boolean hasExcelFormat(MultipartFile file) {
        if (!TYPE.equals(file.getContentType())) {
            return false;
        }
        return true;
    }

    public static ByteArrayInputStream tutorialsToExcel(List<Item> items) {

        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream();) {
            Sheet sheet = workbook.createSheet(SHEET);

            // Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < HEADERs.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERs[col]);
            }

            int rowIdx = 1;
            for (Item item : items) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(item.getName());
                row.createCell(1).setCellValue(item.getDescription());
                row.createCell(2).setCellValue(item.getEnabled());
                row.createCell(3).setCellValue(item.getName());
                row.createCell(4).setCellValue(item.getDescription());
                row.createCell(5).setCellValue(item.getEnabled());
                row.createCell(6).setCellValue(item.getDescription());
                row.createCell(7).setCellValue(item.getEnabled());
            }
            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to Excel file: " + e.getMessage());
        }
    }

    public static ItemResImportDTO excelToTutorials(InputStream is, Merchant merchant) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<Item> items = new ArrayList<Item>();
            List<ItemDayImportDTO> itemDayImports = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Item item = new Item();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    item.setMerchant(merchant);
                    item.setCreatedBy("upload");
                    Date dateItem = new Date();
                    item.setCreatedDate(dateItem);
                    switch (cellIdx) {
                        case 0:
                            item.setName(currentCell.getStringCellValue());
                            break;

                        case 1:
                            item.setDescription(currentCell.getStringCellValue());
                            break;

                        case 2:
                            item.setEnabled(currentCell.getBooleanCellValue());
                            break;

                        case 3:
                            item.setMiscellaneous(currentCell.getBooleanCellValue());
                            break;

                        case 4:
                            Short typeItem;
                            if (currentCell.getStringCellValue().toLowerCase().replace(" ", "")
                                    .compareTo("specific") == 0) {
                                typeItem = (short) 2;
                            } else {
                                typeItem = (short) 1;
                            }
                            item.setType(typeItem);
                            break;

                        case 5:
                            if (currentRow.getCell(4).getStringCellValue().toLowerCase().replace(" ", "")
                                    .compareTo("specific") == 0) {
                                List<String> dayXls = Arrays.asList(currentCell.getStringCellValue().split(","));
                                List<Integer> days = new ArrayList<>();
//                                days.addAll(dayXls.stream().map(d -> Integer.valueOf(d.replace(" ", "")))
//                                        .collect(Collectors.toList()));
                                List<String> _days = new ArrayList<>();
                                _days.addAll(dayXls.stream().map(d -> d.toLowerCase().replace(" ", ""))
                                        .collect(Collectors.toList()));
                                for (String day : _days) {
                                    switch (day) {
                                        case "sun":
                                            days.add(1);
                                            break;
                                        case "mon":
                                            days.add(2);
                                            break;
                                        case "tue":
                                            days.add(3);
                                            break;
                                        case "wed":
                                            days.add(4);
                                            break;
                                        case "thu":
                                            days.add(5);
                                            break;
                                        case "fri":
                                            days.add(6);
                                            break;
                                        case "sat":
                                            days.add(7);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                                ItemDayImportDTO itemDayImportDTO = new ItemDayImportDTO();
                                itemDayImportDTO.setName(currentRow.getCell(0).getStringCellValue());
                                itemDayImportDTO.setCreatedAt(dateItem);
                                itemDayImportDTO.setItemDayList(days);
                                itemDayImports.add(itemDayImportDTO);
                            }
                            break;

                        case 6:
                            item.setAssemblyTime(((int) currentCell.getNumericCellValue()));
                            break;

                        case 7:
                            item.setMaxItem(((int) currentCell.getNumericCellValue()));
                            break;

                        case 8:
                            item.setOutStock(currentCell.getBooleanCellValue());
                            break;

                        case 9:
                            item.setPrice(((long) currentCell.getNumericCellValue()));
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }

                items.add(item);
            }
            workbook.close();
            ItemResImportDTO itemResImportDTO = new ItemResImportDTO();
            itemResImportDTO.setItems(items);
            itemResImportDTO.setItemDayImports(itemDayImports);
            return itemResImportDTO;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static ItemResImportDTO excelItemAttribute(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<Long> itemIds = new ArrayList<>();
            List<AttributeItemDTO> attributeItems = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                AttributeItemDTO attributeItem = new AttributeItemDTO();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            Long itemId = (long) currentCell.getNumericCellValue();
                            if(!itemIds.contains(itemId)) {
                                itemIds.add(itemId);
                            }
                            attributeItem.setItemId(itemId);
                            break;

                        case 1:
                            attributeItem.setName(currentCell.getStringCellValue());
                            break;

                        case 2:
                            attributeItem.setPrice((long) currentCell.getNumericCellValue());
                            break;

                        case 3:
                            attributeItem.setStock((long) currentCell.getNumericCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }
                attributeItems.add(attributeItem);
            }
            workbook.close();
            ItemResImportDTO itemResImportDTO = new ItemResImportDTO();
            itemResImportDTO.setItemIds(itemIds);
            itemResImportDTO.setAttributeItems(attributeItems);
            return itemResImportDTO;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }

    public static List<Merchant> excelMerchant(InputStream is) {
        DataFormatter formatter = new DataFormatter();
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheet(SHEET);
            Iterator<Row> rows = sheet.iterator();
            List<Merchant> merchants = new ArrayList<>();
            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                // skip header
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();

                Merchant merchant = new Merchant();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    merchant.setCreatedBy("upload");
                    merchant.setStatus(1);
                    merchant.setDeleted(false);
                    merchant.setCreatedDate(new Date());
                    switch (cellIdx) {
                        case 0:
                            merchant.setStoreName(currentCell.getStringCellValue());
                            break;

                        case 1:
                            merchant.setAddress(currentCell.getStringCellValue());
                            break;

                        case 2:
                            merchant.setCity(currentCell.getStringCellValue());
                            break;

                        case 3:
                            String postCode = formatter.formatCellValue(currentRow.getCell(3));
                            merchant.setPostalCode(postCode);
                            break;

                        case 4:
                            merchant.setOperationNumber(currentCell.getStringCellValue().replace("+", ""));
                            merchant.setPhone(currentCell.getStringCellValue().replace("+", ""));
                            break;

                        case 5:
                            List<String> availableXls = Arrays.asList(currentCell.getStringCellValue().split(","));
                            List<String> availables = new ArrayList<>();
                            List<Integer> _availables = new ArrayList<>();
                            availables.addAll(availableXls.stream().map(d -> d.toUpperCase().replace(" ", ""))
                                    .collect(Collectors.toList()));
                            for (String available : availables) {
                                switch (available) {
                                    case "M":
                                        _availables.add(1);
                                        break;
                                    case "N":
                                        _availables.add(2);
                                        break;
                                    case "E":
                                        _availables.add(3);
                                        break;
                                    default:
                                        break;
                                }
                            }
                            String availableDev = StringUtils.join(_availables, ",");
                            merchant.setAvailableDelivery(availableDev);
                            break;

                        case 6:
                            merchant.setBankName(currentCell.getStringCellValue());
                            break;

                        case 7:
                            merchant.setBankAccountName(currentCell.getStringCellValue());
                            break;

                        case 8:
                            String bankAccount = formatter.formatCellValue(currentRow.getCell(8));
                            merchant.setBankAccount(bankAccount);
                            break;

                        case 9:
                            String username = formatter.formatCellValue(currentRow.getCell(9));
                            merchant.setUsername(username);
                            break;

                        case 10:
                            String email = formatter.formatCellValue(currentRow.getCell(10));
                            merchant.setEmail(email);
                            break;

                        case 11:
                            String password = formatter.formatCellValue(currentRow.getCell(11));
                            merchant.setPassword(password);
                            break;

                        case 12:
                            merchant.setPin((int) currentCell.getNumericCellValue());
                            break;

                        default:
                            break;
                    }

                    cellIdx++;
                }

                merchants.add(merchant);
            }
            workbook.close();
            return merchants;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse Excel file: " + e.getMessage());
        }
    }
}
