package ys.gme.dochanrpg.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import ys.gme.dochanrpg.data.DataShow;

import java.util.List;

/**
 * @author yoskir
 */
public class DataShowController {
    @FXML
    private TableView<DataShow> dataTable;
    @FXML
    private TableColumn<DataShow,String> title;
    @FXML
    private TableColumn<DataShow,Integer> value;



    @FXML
    private void initialize(){
        title.setCellValueFactory(new PropertyValueFactory<>("title"));
        value.setCellValueFactory(new PropertyValueFactory<>("value"));
    }

    public void setDataList(List<DataShow> dataList){
        ObservableList<DataShow> observableList= FXCollections.observableList(dataList);
        dataTable.setItems(observableList);
    }
}
