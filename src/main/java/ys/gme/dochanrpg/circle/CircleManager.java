package ys.gme.dochanrpg.circle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import lombok.Getter;
import ys.gme.dochanrpg.Constant;
import ys.gme.dochanrpg.data.DataCollector;
import ys.gme.dochanrpg.circle.entity.CircleInfo;
import ys.gme.dochanrpg.data.DataEntity;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yoskir
 */
@Getter
public class CircleManager {
    private final Pane pane;
    private final CircleList circleList;
    private final DataCollector dataCollector;
    private final CircleAction circleAction;
    private final CircleTarget circleTarget;

    private final Set<Circle> allCircles;




    public CircleManager(Pane pane){
        this.pane=pane;
        circleTarget=new CircleTarget();
        circleAction=new CircleAction(this);
        circleList =new CircleList(this);
        dataCollector=new DataCollector();
        allCircles= circleList.getAllCircles();
    }

    public void start(){
        dataCollector.setTotalRound(dataCollector.getTotalRound()+1);
        //改變標題倒數用
        Stage stage=(Stage) pane.getScene().getWindow();
        AtomicReference<LocalDateTime> startTime= new AtomicReference<>(LocalDateTime.now());

        Timeline timeline=new Timeline(new KeyFrame(Duration.millis(30),event->{
            //標題倒數
            LocalDateTime startTimeTemp=startTime.get();
            int secondsPassed=(int) ChronoUnit.SECONDS.between(startTimeTemp,LocalDateTime.now());
            stage.setTitle("兜醬你好 剩餘 "+(60-secondsPassed)+" 秒");

            long currentTime=System.currentTimeMillis();
            for (Circle circle : allCircles) {
                CircleInfo circleInfo = circleList.getCircleInfo(circle);
                //被相遇鎖定後會暫停行動
                if (!circleInfo.isLock()) {
                    if(circleInfo.isBattle()){
                        Circle loseCircle=circleAction.battle(circleInfo,currentTime);
                        if(loseCircle!=null){
                            //加入待刪除清單，於這輪後刪除
                            circleList.addCircleToRemove(loseCircle);
                        }
                    }else if(circleInfo.isBuff()){
                        circleAction.buff(circleInfo,currentTime);
                    }else {
                        circleAction.move(circleInfo, currentTime);

                        dataCollector.add(DataEntity.Var.totalMove,circleInfo.getColor());
                    }
                }
            }
            circleList.updateCircles();
            Color emptyColor= circleList.checkRestart();
            if(emptyColor!=null||secondsPassed>=60){
                //重置倒數時間
                startTime.set(LocalDateTime.now());
                circleList.restart();
                if(secondsPassed<60){
                    dataCollector.add(DataEntity.Var.win,emptyColor.equals(Constant.RED)?Constant.BLUE:Constant.RED);
                }
                dataCollector.setTotalTime(dataCollector.getTotalTime()+secondsPassed);
            }
        }));
        //60秒後重置
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

}
