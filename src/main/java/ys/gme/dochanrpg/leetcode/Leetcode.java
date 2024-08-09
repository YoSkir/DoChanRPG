package ys.gme.dochanrpg.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yoskir
 */
public class Leetcode {

}
class Solution {
    public boolean wordPattern(String pattern, String s) {
        Map<String,String> strPattern=new HashMap<>();
        Map<String,String> ptStr=new HashMap<>();
        String[] strArr=s.split(" ");
        if(pattern.length()!=strArr.length){
            return false;
        }
        for(int i=0;i<pattern.length();i++){
            String pt=String.valueOf(pattern.charAt(i));
            String str=strArr[i];

            if(strPattern.containsKey(str)){
                if(!strPattern.get(str).equals(pt)){
                    return false;
                }
            }else {
                strPattern.put(str,pt);
            }

            if(ptStr.containsKey(pt)){
                if(!ptStr.get(pt).equals(str)){
                    return false;
                }
            }else {
                ptStr.put(pt,str);
            }
        }
        return true;
    }
}