module ys.gme.dochanrpg {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires static lombok;

    opens ys.gme.dochanrpg to javafx.fxml;
    exports ys.gme.dochanrpg;
    exports ys.gme.dochanrpg.circle;
    opens ys.gme.dochanrpg.circle to javafx.fxml;
    exports ys.gme.dochanrpg.circle.entity;
    opens ys.gme.dochanrpg.circle.entity to javafx.fxml;
    exports ys.gme.dochanrpg.data;
    opens ys.gme.dochanrpg.data to javafx.fxml;
}