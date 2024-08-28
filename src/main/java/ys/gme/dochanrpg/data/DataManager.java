package ys.gme.dochanrpg.data;

import lombok.Getter;
import ys.gme.dochanrpg.data.entity.DataCollector;

import java.util.LinkedList;
import java.util.List;

/**
 * @author yoskir
 */
@Getter
public class DataManager {
    private final DataCollector dataCollector;
    private final List<DataShow> dataShowList;

    public DataManager(){
        dataCollector=new DataCollector();
        dataShowList=new LinkedList<>();
    }

    public void calculateData(){

    }
}
