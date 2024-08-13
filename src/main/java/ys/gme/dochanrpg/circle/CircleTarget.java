package ys.gme.dochanrpg.circle;

import javafx.scene.shape.Circle;

import java.util.HashMap;
import java.util.Map;

/**
 * 圓圈的行動管理類
 * @author yoskir
 */
public class CircleTarget {

    private final Map<Circle,Circle> circle_target;

    public CircleTarget(){
        circle_target=new HashMap<>();
    }

    /**
     * 獲取指定圓圈的目標
     * @param circle
     * @return
     */
    public Circle getTarget(Circle circle){
        return circle_target.getOrDefault(circle,null);
    }

    /**
     * 刪除指定圓圈的目標
     * @param circle
     */
    public void removeTarget(Circle circle){
        circle_target.remove(circle);
    }

    /**
     * 設定圓圈的目標 如果已有目標則回傳否
     * @param circle
     * @param target
     * @return
     */
    public boolean setTarget(Circle circle,Circle target){
        if(getTarget(circle)!=null){
            return false;
        }
        circle_target.put(circle,target);
        return true;
    }

    /**
     * 確認圓圈是否為他人目標
     * @param circle
     * @return
     */
    public boolean isCircleTarget(Circle circle){
        return circle_target.containsValue(circle);
    }
}
