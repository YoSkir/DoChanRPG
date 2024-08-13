package ys.gme.dochanrpg.circle;

import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import ys.gme.dochanrpg.Constant;
import ys.gme.dochanrpg.PopUpTextManager;
import ys.gme.dochanrpg.circle.entity.CircleInfo;
import ys.gme.dochanrpg.data.DataCollector;
import ys.gme.dochanrpg.data.DataEntity;

import java.util.Random;
import java.util.Set;

/**
 * @author yoskir
 */
public class CircleAction {

    private final int height= Constant.HEIGHT;
    private final int width=Constant.WIDTH;
    private final long actionTime=0;
    private final double closeDistance=5.0;
    private final Random r=Constant.RANDOM;

    private final PopUpTextManager popUpTextManager;
    private final CircleTarget circleTarget;
    private final CircleList circleList;
    private final DataCollector dataCollector;

    private final Set<Circle> allCircles;

    public CircleAction(CircleManager circleManager){
        popUpTextManager=new PopUpTextManager(circleManager.getPane());
        circleTarget=circleManager.getCircleTarget();
        circleList =circleManager.getCircleList();
        allCircles=circleManager.getAllCircles();
        dataCollector=circleManager.getDataCollector();
    }

    /**
     * 友軍相遇狀況
     * @param circleInfo 發起圓圈的資訊
     */
    public void buff(CircleInfo circleInfo, long currentTime){
        long lastTime=circleInfo.getLastTime();
        long duration=circleInfo.getDuration();
        if(currentTime-lastTime>=duration){
            Circle circle=circleInfo.getThisCircle();
            Circle target=circleTarget.getTarget(circle);
            if(target==null){
                return;
            }
            circleList.getCircleInfo(target).setLock(false);
            Color color=circleInfo.getColor();
            if(r.nextInt(10)<4){
                String buffStr;
                //如果圓圈總數超過1000 則停止，改為雙方增加半徑
                if(allCircles.size() >500){
                    circleList.plusRadius(target,1);
                    circleList.plusRadius(circle,1);
                    buffStr="強化";
                }else {
                    buffStr="生成友軍";
                    circleList.newCircle(color,circleInfo.getX(),circleInfo.getY());
                }
                popUpTextManager.popUp(buffStr,circleInfo.getX(),circleInfo.getY()-3);
            }
            circleInfo.setBuff(false);
            circleTarget.removeTarget(circle);
        }
    }

    /**
     * 敵軍相遇時戰鬥
     * @param circleInfo 發起戰鬥的圓圈資訊
     * @param currentTime 現在時間 用於計算相遇到戰鬥的時間
     */
    public Circle battle(CircleInfo circleInfo,long currentTime){
        Circle circle=circleInfo.getThisCircle();
        long lastTime=circleInfo.getLastTime();
        long duration=circleInfo.getDuration();

        if(currentTime-lastTime>=duration){
            Circle target=circleTarget.getTarget(circle);
            if(target==null){
                return null;
            }

            //勝率被半徑影響
            CircleInfo targetInfo= circleList.getCircleInfo(target);
            int winRate=(int)(circleInfo.getRadius()-targetInfo.getRadius())*5;
            if(Math.abs(winRate)>49){
                winRate=winRate>0?49:-49;
            }

            Circle winCircle,loseCircle;
            if(r.nextInt(100)>50-winRate){
                winCircle=circle;
                loseCircle=target;
            }else {
                winCircle=target;
                loseCircle=circle;
            }
            CircleInfo winCircleInfo= circleList.getCircleInfo(winCircle);

            //打贏會吸取對方半徑
            //打贏同尺寸或小尺寸，半徑只會增加1
            double bonusRadius=-(winCircleInfo.getRadius()- circleList.getCircleInfo(loseCircle).getRadius());
            if(bonusRadius<1){
                bonusRadius=1;
            }

            //戰鬥勝利獎勵
            circleList.plusRadius(winCircle,bonusRadius);

            circleTarget.removeTarget(winCircle);
            winCircleInfo.setLock(false);
            winCircleInfo.setBattle(false);

            return loseCircle;
        }
        return null;
    }

    /**
     * 進行移動
     * @param circleInfo 要移動圓圈的資訊
     * @param currentTime 現在時間 用於計算移動持續時間
     */
    public void move(CircleInfo circleInfo,long currentTime){
        Circle circle=circleInfo.getThisCircle();
        Color color=circleInfo.getColor();
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

                dataCollector.add(DataEntity.Var.stop,color);
            }else {
                //決定移動方向 增減幅度越大機率越小
                //60%:0~3 30%:4 10%:5~
                xMove=getMovement(xMove,color);
                yMove=getMovement(yMove,color);

                if(xMove==0&&yMove==0){
                    dataCollector.add(DataEntity.Var.stop,color);
                }
            }
            circleInfo.updateInfo(xMove,yMove,currentTime,duration);
        }
        moveCircle(circle,circleInfo);
        Circle otherCircle=checkCloseCircle(circle);
        if(otherCircle!=null){
            //初次相遇的處理
            if(!circleInfo.isEncounter()){
                if(!circleTarget.setTarget(circle,otherCircle)){
                    return;
                }
                CircleInfo otherCircleInfo= circleList.getCircleInfo(otherCircle);

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
     */
    private double getMovement(double move,Color circleColor){
        int moveRate=r.nextInt(10);
        //60%機率 移動值為-3~3
        if(moveRate<6){
            move+=r.nextDouble(6)-3;

            dataCollector.add(DataEntity.Var.speed3,circleColor);
        }
        //30%機率 移動值為-3~-4 or 3~4
        else if(moveRate<9){
            move+= r.nextDouble(3.0 /4,1)*4*(r.nextInt(2)-1);

            dataCollector.add(DataEntity.Var.speed4,circleColor);
        }else { //10%機率 移動值為 -4~-5 or 4~5
            move+=r.nextDouble(4.0/5,1)*5*(r.nextInt(2)-1);

            dataCollector.add(DataEntity.Var.speed5,circleColor);
        }
        if(Math.abs(move)>5){
            return move>0?5:-5;
        }

        if(move<0){
            dataCollector.add(DataEntity.Var.speedMinus,circleColor);
        }
        return move;
    }

    /**
     * 移動玩家
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
     */
    private Circle checkCloseCircle(Circle circle){
        //用以判斷最近的圓圈
        double minDistance=Double.MAX_VALUE;
        Circle target=null;

        for(Circle otherCircle: circleList.getOtherCircle(circle)){
            CircleInfo otherCircleInfo= circleList.getCircleInfo(otherCircle);
            //如果其他圓圈戰鬥中 則判定為非相遇
            if(!otherCircleInfo.isBattle()&&!otherCircleInfo.isBuff()&&!otherCircleInfo.isLock()){
                double otherRadius=otherCircle.getRadius();
                double distance= circleList.calculateDistance(circle,otherCircle);

                if(distance<=closeDistance){
                    //取距離最近的
                    if(distance<minDistance){
                        minDistance=distance;
                        target=otherCircle;
                    }
                    //如果距離相等 取半徑大的
                    else if(distance==minDistance){
                        if(circleList.getCircleInfo(target).getRadius()<otherRadius){
                            target=otherCircle;
                        }
                    }
                }
            }
        }
        return target;
    }
}
