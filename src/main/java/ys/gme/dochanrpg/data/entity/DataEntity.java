package ys.gme.dochanrpg.data.entity;

import lombok.Data;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yoskir
 */
@Getter
public class DataEntity {

    private final Map<Var,Integer> dataMap=new HashMap<>();
    
    public enum Var{
        win,
        //移動
        totalMove, //總決定移動數
        stop,//總停止數
        speed3,//3速以下數
        speed4,//3到4速數
        speed5,//4速以上
        speedMinus,//負速數
        //遭遇
        Encounter, //遇到其他人
        BeTarget, //當目標
        SetBuff, //發起強化
        Buff, //成功強化
        BuffAdd, //強化增加圓圈
        BuffBig, //強化變大
        SetBattle, //發起戰鬥
        BattleWin, //戰鬥勝利
        GiantKill, //反殺巨人
        DeadRadius //死亡時大小加總
    }










}
