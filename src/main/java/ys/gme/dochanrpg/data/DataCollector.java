package ys.gme.dochanrpg.data;

import javafx.scene.paint.Color;
import lombok.Data;
import ys.gme.dochanrpg.Constant;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yoskir
 */
@Data
public class DataCollector {
    private final Map<Color,DataEntity> dataMap=new HashMap<>();

    public void add(DataEntity.Var var,Color color){
        add(var,1,color);
    }
    public void add(DataEntity.Var var, int value,Color color){
        Map<DataEntity.Var,Integer> dataMap=this.dataMap.get(color).getDataMap();
        dataMap.put(var,dataMap.getOrDefault(var,0)+value);
    }

    public DataCollector(){
        dataMap.put(Constant.RED,new DataEntity());
        dataMap.put(Constant.BLUE,new DataEntity());
    }
    //場次相關
    private int totalRound=0, //總場數
    totalTime; //總經過時間

}
