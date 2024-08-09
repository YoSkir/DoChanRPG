package ys.gme.dochanrpg;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * 畫面上彈出文字的管理類
 * @author yoskir
 */
public class PopUpTextManager {
    private final Pane pane;
    private final long textDuration=500;

    public PopUpTextManager(Pane pane){
        this.pane=pane;
    }

    /**
     * 顯示彈出文字
     * @param text
     * @param x
     * @param y
     */
    public void popUp(String textStr,double x,double y){
        Text text=new Text(x,y,textStr);
        text.setFont(new Font(12));
        text.setFill(Color.WHITE);
        long startTime=System.currentTimeMillis();

        pane.getChildren().add(text);
        Timeline timeline=new Timeline(new KeyFrame(Duration.millis(30),event->{
            long currentTime=System.currentTimeMillis();
            if(currentTime-startTime>=textDuration){
                pane.getChildren().remove(text);
            }else {
                text.setY(text.getY()-2);
            }
        }));
        timeline.setCycleCount((int)textDuration/30+1);
        timeline.play();
    }
}
