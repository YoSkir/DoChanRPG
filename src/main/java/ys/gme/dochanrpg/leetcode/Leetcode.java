package ys.gme.dochanrpg.leetcode;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yoskir
 */
public class Leetcode {

}
class Solution {
    public int[] countBits(int n) {
        int[] result=new int[n+1];
        result[0]=0;
        for(int i=1;i<=n;i++){
            result[i]=result[i&(i-1)]+1;
        }
        return result;
    }
}