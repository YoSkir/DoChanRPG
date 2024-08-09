package ys.gme.dochanrpg.circle.entity;

import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ys.gme.dochanrpg.Constant;

import java.util.*;

/**
 * 管理所有圓圈的類
 * @author yoskir
 */
public class CircleEntity {
    private final Random r= Constant.RANDOM;
    private final Pane pane;

    private final int width=Constant.WIDTH;
    private final int height=Constant.HEIGHT;
    private final Color red=Color.PINK;
    private final Color blue=Color.SKYBLUE;
    private final Map<Color, List<Circle>> circleMap;
    private final Map<Circle, CircleInfo> circleInfoMap;

    private final Map<Circle,Circle> circle_target;

    //要刪除與新增的圓點
    private final List<Circle> circlesToRemove=new LinkedList<>();
    private final List<Circle> circlesToAdd=new LinkedList<>();

    int num=1;

    public CircleEntity(Pane pane){
        circleInfoMap=new HashMap<>();
        circleMap=new HashMap<>();
        circleMap.put(red,new LinkedList<>());
        circleMap.put(blue,new LinkedList<>());
        circle_target=new HashMap<>();
        this.pane=pane;
        setCircles();
        updateCircles();
    }

    /**
     * 重新開始
     */
    public void restart(){
        updateCircles();
        pane.getChildren().clear();
        for(List<Circle> circleList:circleMap.values()){
            circleList.clear();
        }
        circleInfoMap.clear();
        num=1;
        setCircles();
        updateCircles();
    }

    public boolean checkRestart(){
        for(List<Circle> circleList:circleMap.values()){
            if(circleList.isEmpty()){
                return true;
            }
        }
        return false;
    }

    /**
     * 刪除與新增圓點
     */
    public void updateCircles(){
        removeCircle();
        addCircle();
    }

    /**
     * 刪除待刪除圓圈
     */
    private void removeCircle(){
        for(Circle circle:circlesToRemove){
            CircleInfo circleInfo=getCircleInfo(circle);
            pane.getChildren().remove(circle);
            pane.getChildren().remove(circleInfo.getNumberText());

            circleMap.get(circleInfoMap.get(circle).getColor()).remove(circle);
            circleInfoMap.remove(circle);
            //要檢查 如果有任何圓圈把這個圓圈當作目標 那要解除
        }
        circlesToRemove.clear();
    }

    /**
     * 增加圓的半徑
     * @param circle
     * @param radius
     */
    public void plusRadius(Circle circle,double radius){
        CircleInfo circleInfo=getCircleInfo(circle);
        double newRadius=circleInfo.getRadius()+radius;
        circleInfo.setRadius(newRadius);
        circle.setRadius(newRadius);

        //增大時，於範圍內的圓全部刪除
        for(Circle otherCircle:getOtherCircle(circle)){
            if(calculateDistance(circle,otherCircle)<0){
                addCircleToRemove(otherCircle);
            }
        }
    }

    /**
     * 獲取除了 thisCircle以外的Circle資訊
     * @return
     */
    public List<Circle> getOtherCircle(Circle thisCircle){
        List<Circle> otherCircles=new LinkedList<>();
        for(Circle circle:circleInfoMap.keySet()){
            if(!circle.equals(thisCircle)){
                otherCircles.add(circle);
            }
        }
        return otherCircles;
    }

    /**
     * 計算兩圓的邊界距離
     * @param circle1
     * @param circle2
     * @return
     */
    public double calculateDistance(Circle circle1,Circle circle2){
        double x1=circle1.getCenterX();
        double x2=circle2.getCenterX();
        double y1=circle1.getCenterY();
        double y2=circle2.getCenterY();

        double xDistance=Math.abs(x1-x2);
        double yDistance=Math.abs(y1-y2);

        return Math.sqrt(xDistance*xDistance+yDistance*yDistance)-circle1.getRadius()-circle2.getRadius();
    }

    /**
     * 新增待刪除圓點
     * @param circle
     */
    public void addCircleToRemove(Circle circle){
        circlesToRemove.add(circle);
    }

    /**
     * 將待新增原點新增
     */
    private void addCircle(){
        for(Circle circle:circlesToAdd){
            circleInfoMap.put(circle,new CircleInfo(circle));

            CircleInfo circleInfo=getCircleInfo(circle);
            circleInfo.setNumber(num);
            circleInfo.getNumberText().setText(String.valueOf(num));
            num++;
            pane.getChildren().add(circle);
            pane.getChildren().add(circleInfo.getNumberText());

            circleMap.get(circleInfoMap.get(circle).getColor()).add(circle);

        }
        circlesToAdd.clear();
    }

    /**
     * 增加指定顏色圓圈
     * @param color
     */
    private void newCircle(Color color){
        newCircle(color,r.nextDouble(0,width),r.nextDouble(0,height));
    }
    public void newCircle(Color color, double x, double y){
        Circle circle =new Circle(5,color);
        circle.setCenterX(x);
        circle.setCenterY(y);

        circlesToAdd.add(circle);
    }

    /**
     * 獲得指定圓圈的資訊
     * @param circle
     * @return
     */
    public CircleInfo getCircleInfo(Circle circle){
        return circleInfoMap.get(circle);
    }

    /**
     * 獲得全部圓圈的集合
     * @return
     */
    public Set<Circle> getAllCircles(){
        return circleInfoMap.keySet();
    }

    /**
     * 設定起始圓圈
     */
    private void setCircles(){
        for(int i=0;i<100;i++){
            Color color=i%2==0?red:blue;
            newCircle(color);
        }
    }
}
