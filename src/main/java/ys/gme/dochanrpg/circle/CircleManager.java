package ys.gme.dochanrpg.circle;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import ys.gme.dochanrpg.Constant;
import ys.gme.dochanrpg.PopUpTextManager;
import ys.gme.dochanrpg.circle.entity.CircleEntity;
import ys.gme.dochanrpg.circle.entity.CircleInfo;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author yoskir
 */
public class CircleManager {

    private final int height= Constant.HEIGHT;
    private final int width=Constant.WIDTH;
    private final long actionTime=0;
    private final Random r=Constant.RANDOM;


    private final Pane pane;

    private final double closeDistance=5.0;

    private final PopUpTextManager popUpTextManager;
    private final CircleEntity circleEntity;

    private final Set<Circle> allCircles;




    public CircleManager(Pane pane){
        this.pane=pane;
        circleEntity =new CircleEntity(pane);
        popUpTextManager=new PopUpTextManager(pane);
        allCircles= circleEntity.getAllCircles();
    }

    public void start(){
        //改變標題倒數用
        Stage stage=(Stage) pane.getScene().getWindow();
        AtomicReference<LocalDateTime> startTime= new AtomicReference<>(LocalDateTime.now());

        Timeline timeline=new Timeline(new KeyFrame(Duration.millis(30),event->{
            //標題倒數
            LocalDateTime startTimeTemp=startTime.get();
            int secondsPassed=(int) ChronoUnit.SECONDS.between(startTimeTemp,LocalDateTime.now());
            stage.setTitle("兜醬你好 剩餘 "+String.valueOf(60-secondsPassed)+" 秒");

            long currentTime=System.currentTimeMillis();
            for (Circle circle : allCircles) {
                CircleInfo circleInfo = circleEntity.getCircleInfo(circle);
                //被相遇鎖定後會暫停行動
                if (!circleInfo.isLock()) {
                    if(circleInfo.isBattle()){
                        Circle loseCircle=battle(circleInfo,currentTime);
                        if(loseCircle!=null){
                            //加入待刪除清單，於這輪後刪除
                            circleEntity.addCircleToRemove(loseCircle);
                        }
                    }else if(circleInfo.isBuff()){
                        buff(circleInfo,currentTime);
                    }else {
                        move(circleInfo, currentTime);
                    }
                }
            }
            circleEntity.updateCircles();
            if(circleEntity.checkRestart()||secondsPassed>60){
                //重置倒數時間
                startTime.set(LocalDateTime.now());
                circleEntity.restart();
            }
        }));
        //60秒後重置
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.play();
    }

    /**
     * 友軍相遇
     * @param circleInfo
     */
    private void buff(CircleInfo circleInfo,long currentTime){
        long lastTime=circleInfo.getLastTime();
        long duration=circleInfo.getDuration();
        if(currentTime-lastTime>=duration){
            circleEntity.getCircleInfo(circleInfo.getEncounterCircle()).setLock(false);
            Color color=circleInfo.getColor();
            if(r.nextInt(10)<4){
                String buffStr;
                //如果圓圈總數超過1000 則停止，改為雙方增加半徑
                if(allCircles.size() >500){
                    circleEntity.plusRadius(circleInfo.getEncounterCircle(),1);
                    circleEntity.plusRadius(circleInfo.getThisCircle(),1);
                    buffStr="強化";
                }else {
                    buffStr="生成友軍";
                    circleEntity.newCircle(color,circleInfo.getX(),circleInfo.getY());
                }
                popUpTextManager.popUp(buffStr,circleInfo.getX(),circleInfo.getY()-3);
            }
            circleInfo.setBuff(false);
            circleInfo.setEncounterCircle(null);
        }
    }

    /**
     * 戰鬥
     * @param circleInfo
     * @param currentTime
     */
    private Circle battle(CircleInfo circleInfo,long currentTime){
        Circle circle=circleInfo.getThisCircle();
        long lastTime=circleInfo.getLastTime();
        long duration=circleInfo.getDuration();

        if(currentTime-lastTime>=duration){
            //勝率被半徑影響
            CircleInfo targetInfo=circleEntity.getCircleInfo(circleInfo.getEncounterCircle());
            int winRate=(int)(circleInfo.getRadius()-targetInfo.getRadius())*5;
            if(Math.abs(winRate)>49){
                winRate=winRate>0?49:-49;
            }

            Circle winCircle,loseCircle;
            if(r.nextInt(100)>50-winRate){
                winCircle=circle;
                loseCircle=circleInfo.getEncounterCircle();
            }else {
                winCircle=circleInfo.getEncounterCircle();
                loseCircle=circle;
            }
            CircleInfo winCircleInfo= circleEntity.getCircleInfo(winCircle);

            //打贏會吸取對方半徑
            //打贏同尺寸或小尺寸，半徑只會增加1
            double bonusRadius=-(winCircleInfo.getRadius()-circleEntity.getCircleInfo(loseCircle).getRadius());
            if(bonusRadius<1){
                bonusRadius=1;
            }

            //戰鬥勝利獎勵
            circleEntity.plusRadius(winCircle,bonusRadius);

            winCircleInfo.setEncounterCircle(null);
            winCircleInfo.setLock(false);
            winCircleInfo.setBattle(false);

            return loseCircle;
        }
        return null;
    }

    /**
     * 進行移動
     * @param circleInfo
     * @param currentTime
     */
    private void move(CircleInfo circleInfo,long currentTime){
        Circle circle=circleInfo.getThisCircle();
        long lastTime=circleInfo.getLastTime();
        long duration=circleInfo.getDuration();
        double xMove=circleInfo.getXMove();
        double yMove= circleInfo.getYMove();
        //判斷行動時長
        if(currentTime-lastTime>=duration){
            //設定行動時長
            duration= setDuration();
            //是否移動
            //決定移動或靜止 70移動 30靜止
            if(r.nextInt(10)<3){
                xMove=0.0;
                yMove=0.0;
            }else {
                //決定移動方向 增減幅度越大機率越小
                //60%:0~3 30%:4 10%:5~
                xMove=getMovement(xMove);
                yMove=getMovement(yMove);
            }
            circleInfo.updateInfo(xMove,yMove,currentTime,duration);
        }
        moveCircle(circle,circleInfo);
        Circle otherCircle=checkCloseCircle(circle);
        if(otherCircle!=null){
            //初次相遇的處理
            if(!circleInfo.isEncounter()){
                circleInfo.setEncounterCircle(otherCircle);
                CircleInfo otherCircleInfo=circleEntity.getCircleInfo(otherCircle);

                circleInfo.setEncounter(true);
                otherCircleInfo.setEncounter(true);
                //鎖定對方行動
                otherCircleInfo.setLock(true);

                //時間設定
                circleInfo.setDuration(actionTime);
                circleInfo.setLastTime(currentTime);
                //友軍相遇
                if(circleInfo.getColor().equals(otherCircleInfo.getColor())){
                    popUpTextManager.popUp("相遇",circle.getCenterX(),circle.getCenterY()-3);
                    circleInfo.setBuff(true);
                }
                //敵軍相遇
                else {
                    popUpTextManager.popUp("戰鬥",circle.getCenterX(),circle.getCenterY()-3);
                    circleInfo.setBattle(true);
                }
            }
            //新增相遇倒數
        }else {
            circleInfo.setEncounter(false);
        }
    }



    /**
     * 獲得隨機行動時長 1~5秒
     */
    private long setDuration(){
        return r.nextInt(500,5001);
    }
    /**
     * 獲得隨機移動值 幅度越大機率越小
     * @return
     */
    private double getMovement(double move){
        int moveRate=r.nextInt(10);
        //60%機率 移動值為-3~3
        if(moveRate<6){
            move+=r.nextDouble(6)-3;
        }
        //30%機率 移動值為-3~-4 or 3~4
        else if(moveRate<9){
            move+= r.nextDouble(3/4,1)*4*(r.nextInt(2)-1);
        }else { //10%機率 移動值為 -4~-5 or 4~5
            move+=r.nextDouble(4/5,1)*5*(r.nextInt(2)-1);
        }
        if(Math.abs(move)>5){
            return move>0?5:-5;
        }
        return move;
    }

    /**
     * 移動玩家
     * @param circle
     */
    private void moveCircle(Circle circle,CircleInfo circleInfo){
        double xMove=circleInfo.getXMove();
        double yMove=circleInfo.getYMove();

        double x=circle.getCenterX();
        double y=circle.getCenterY();

        double newX=x+xMove;
        double newY=y+yMove;

        if(newX<0||newX>width) {
            circleInfo.setXMove(-xMove);
        }
        if(newY<0||newY>height) {
            circleInfo.setYMove(-yMove);
        }
        circle.setCenterX(newX);
        circleInfo.setX(newX);
        circle.setCenterY(newY);
        circleInfo.setY(newY);

        circleInfo.getNumberText().setX(newX-circleInfo.getRadius());
        circleInfo.getNumberText().setY(newY+circleInfo.getRadius());
    }

    /**
     * 判斷圓點是否靠近
     * 要判斷最近的
     * @param circle
     */
    private Circle checkCloseCircle(Circle circle){
        double thisX=circle.getCenterX();
        double thisY=circle.getCenterY();
        double thisRadius=circle.getRadius();

        //用以判斷最近的圓圈
        double minDistance=Double.MAX_VALUE;
        Circle target=null;

        for(Circle otherCircle:circleEntity.getOtherCircle(circle)){
            CircleInfo otherCircleInfo=circleEntity.getCircleInfo(otherCircle);
            //如果其他圓圈戰鬥中 則判定為非相遇
            if(!otherCircleInfo.isBattle()&&!otherCircleInfo.isBuff()&&!otherCircleInfo.isLock()){
                double otherRadius=otherCircle.getRadius();
                double distance= circleEntity.calculateDistance(circle,otherCircle);

                if(distance<=closeDistance){
                    //取距離最近的
                    if(distance<minDistance){
                        minDistance=distance;
                        target=otherCircle;
                    }
                    //如果距離相等 取半徑大的
                    else if(distance==minDistance){
                        if(circleEntity.getCircleInfo(target).getRadius()<otherRadius){
                            target=otherCircle;
                        }
                    }
                }
            }
        }
        return target;
    }
}
