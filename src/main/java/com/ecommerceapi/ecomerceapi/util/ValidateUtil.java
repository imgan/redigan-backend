package com.ecommerceapi.ecomerceapi.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

public class ValidateUtil {
    public static Map validateAPI(String filePatern, Map value) throws IOException {
        Resource resource = new ClassPathResource("patern/" + filePatern);
        File file = resource.getFile();
//        String string = ResourceUtils.getURL(file, Charset.defaultCharset()).openStream();
//        String string = FileUtils.readFileToString(file, Charset.defaultCharset());
        InputStream string = ClassLoader.getSystemResourceAsStream("patern/" + filePatern);
        ObjectMapper mapper = new ObjectMapper();
        Map mapObj = mapper.readValue(string, Map.class);

        for (Object key : mapObj.keySet()) {
            String fields = String.valueOf(key);
            Map map = (Map) mapObj.get(fields);
            String type = String.valueOf(map.get("type"));
            String status = String.valueOf(map.get("status"));
            String patern = null;
            Map depend = null;
            List values = null;
            if (map.get("patern") != null) {
                patern = String.valueOf(map.get("patern"));
            }
            if (map.get("value") != null) {
                values = (List) map.get("value");
            }
            if (map.get("depend-on") != null) {
                depend = (Map) map.get("depend-on");
            }

            Object obj = getValues(value, key);

            if (status.equalsIgnoreCase("mandatory") && obj == null) {
                return errorResponse(400, fields);
            }

            if (status.equalsIgnoreCase("conditional")) {
                if (depend == null) {
                    return errorResponse(400, fields);
                }

                boolean mandatory = false;

                Object parent = getValues(value, depend.get("key"));
                List keys = null;
                if (depend.get("value") != null) {
                    keys = (List) depend.get("value");
                }

//        if (parent != null && keys != null && !keys.isEmpty()) {
                if (parent != null && keys != null && !keys.isEmpty() && !parent.equals("BLANJA")) {

//                    System.out.println("keys : " + keys);
//                    System.out.println("parent : " + parent);
                    if (keys.contains(parent)) {
                        mandatory = true;
                    }
                } else if (parent != null && keys == null) {
                    mandatory = true;
                }

                if (mandatory && obj == null) {
                    return errorResponse(400, fields);
                }
            }

            if (obj != null) {
                if (values != null && !values.isEmpty()) {
                    if (!values.contains(obj)) {
                        return errorResponse(400, fields);
                    }
                }

                if (type.equalsIgnoreCase("list")) {
                    if (!checkList(obj)) {
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("integer")) {
                    if (!checkInteger(obj)) {
                        return errorResponse(400, fields);
                    }
                } else if(type.equalsIgnoreCase("biginteger")){
                    if(!checkBigInteger(obj)){
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("double")) {
                    if (!checkDouble(obj)) {
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("base64")) {
                    if (!checkBase64(obj)) {
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("timestamp")) {
                    if (patern == null
                            || DateTimeUtil.convertStringToDateCustomized(String.valueOf(obj), patern) == null) {
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("regex")) {
                    if (patern == null || !paternValidate(patern, String.valueOf(obj))) {
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("phone")) {
                    if (!phoneNumber(String.valueOf(obj))) {
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("fixline")) {
                    if (!fixlineNumber(String.valueOf(obj))) {
                        return errorResponse(400, fields);
                    }
                } else if (type.equalsIgnoreCase("email")) {
                    if (!email(String.valueOf(obj))) {
                        return errorResponse(400, fields);
                    }
                }
            }
        }

        return null;
    }

    public static Object getValues(Map map, Object key) {
        String[] values = String.valueOf(key).split("\\.");
        Object object = null;
        for (int i = 0; i < values.length; i++) {
            if (i == 0) {
                object = map.get(values[i]);
//                System.out.println("values : " + values[i]);
//                System.out.println("object : " + object);
            } else if (object != null) {
                if (i < (values.length)) {
                    Map abc = (Map) object;
                    object = abc.get(values[i]);
                }
            } else {
                break;
            }
        }
        return object;
    }

    private static boolean checkInteger(Object value) {
        try {
            Integer.valueOf(String.valueOf(value));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean checkBigInteger(Object value) {
        try {
            BigInteger.valueOf(Long.valueOf(String.valueOf(value)));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean checkBase64(Object value) {
        try {
            return Base64.isBase64(String.valueOf(value));
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean checkList(Object value) {
        try {
            List list = (List) value;
            return !list.isEmpty();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean checkDouble(Object value) {
        try {
            Double.valueOf(String.valueOf(value));
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private static boolean paternValidate(String patern, String value) {
        Pattern pattern = Pattern.compile(patern);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    public static boolean email(String value) {
        String patern = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        return paternValidate(patern, value);
    }

    public static boolean number(String value) {
        String patern = "[0-9]+";
        return paternValidate(patern, value);
    }

    public static boolean alphaOnly(String value) {
        String patern = "[a-zA-Z]+";
        return paternValidate(patern, value);
    }

    public static boolean phoneNumber(String value) {
        //String patern = "/\\(?([0-9]{3})\\)?([ .-]?)([0-9]{3})\\2([0-9]{4})/";
        //String values =
        //return paternValidate(patern, value);

        String[] phoneNumber = value.split("-");
        if (phoneNumber.length != 2) {
            return false;
        }

        if (phoneNumber[0].length() < 1) {
            return false;
        }

        if (phoneNumber[0].length() > 3) {
            return false;
        }

        if (phoneNumber[1].length() < 1) {
            return false;
        }

        return phoneNumber[1].length() <= 12;
    }

    public static boolean fixlineNumber(String value) {
        //String patern = "/\\(?([0-9]{3})\\)?([ .-]?)([0-9]{3})\\2([0-9]{4})/";
        //String values =
        //return paternValidate(patern, value);

        String[] legalPhone = value.split("-");
        if (legalPhone.length != 3) {
            return false;
        }

        if (legalPhone[0].length() < 1) {
            return false;
        }

        if (legalPhone[0].length() > 3) {
            return false;
        }

        if (legalPhone[1].length() < 1) {
            return false;
        }

        if (legalPhone[1].length() > 5) {
            return false;
        }

        if (legalPhone[2].length() < 1) {
            return false;
        }

        return legalPhone[2].length() <= 12;
    }

    private static Map errorResponse(int codeId, String field) {
        String info = null;
        switch (codeId) {
            case 400:
                info = "invalid data format : " + field;
                break;
            case 404:
                info = "data not found : " + field;
                break;
        }

        Map resultMap = new HashMap();
        resultMap.put("code", codeId);
        resultMap.put("info", info);
        resultMap.put("data", null);
        return resultMap;
    }
}
