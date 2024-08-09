package ys.gme.dochanrpg.circle.entity;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import lombok.Data;
import lombok.Getter;

/**
 * @author yoskir
 */
@Data
public class CircleInfo {
    //資訊
    private final Circle thisCircle;
    private double radius;
    private Color color;
    private double x;
    private double y;
    private Integer number;

    //顯示
    private Text numberText;

    //相遇
    private boolean isBattle=false;
    private boolean isBuff=false;
    private boolean isEncounter=true;
    private boolean isLock=false;
    private Circle encounterCircle=null;


    //移動
    //移動方向
    private double xMove=0;
    private double yMove=0;

    //計時器
    //起始時間
    private long lastTime=System.currentTimeMillis();
    //持續時間
    private long duration=0;


    public CircleInfo(Circle circle){
        thisCircle=circle;
        radius=circle.getRadius();
        color=(Color) circle.getFill();
        x=circle.getCenterX();
        y=circle.getCenterY();
        numberText=new Text(x,y,"");
        numberText.setFont(new Font(12));
        numberText.setFill(Color.BLACK);
    }

    public void updateInfo(double xMove,double yMove,long lastTime,long duration){
        this.xMove=xMove;
        this.yMove=yMove;
        this.duration=duration;
        this.lastTime=lastTime;
    }
}
