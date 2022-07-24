module system.ris {
    //FileUtils
    requires org.apache.commons.io;
    
    //JavaFX
    requires javafx.controls;
    requires javafx.graphics;

    //JavaX
    requires java.desktop;
    
    //SQL
    requires java.sql;
    
    //Cockroach DB
    requires java.naming;
    requires org.postgresql.jdbc;

    opens datastorage;
    exports system.ris;
}
