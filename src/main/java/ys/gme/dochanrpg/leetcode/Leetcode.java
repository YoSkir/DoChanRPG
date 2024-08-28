package ys.gme.dochanrpg.leetcode;

import java.lang.reflect.Array;
import java.util.*;

/**
 * @author yoskir
 */
public class Leetcode {

}
class Solution {
    public int firstUniqChar(String s) {
        List<Character> noRepeatChar=new ArrayList<>();
        List<Character> repeatedChar=new ArrayList<>();
        char[] cArr=s.toCharArray();
        for(char c:cArr){
            if(!repeatedChar.contains(c)){
                if(noRepeatChar.contains(c)){
                    repeatedChar.add(c);
                    noRepeatChar.remove((Character) c);
                }else {
                    noRepeatChar.add(c);
                }
            }
        }
        return noRepeatChar.isEmpty()?-1:s.indexOf(noRepeatChar.getFirst());
    }
}


