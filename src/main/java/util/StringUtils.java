package util;
public class StringUtils{
    private  StringUtils(){}
    public static String lowerFirstCase(String str){
        char [] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
