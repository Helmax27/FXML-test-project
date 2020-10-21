package sample;

import com.fazecast.jSerialComm.SerialPort;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class Controller implements Initializable {

    public void onClickMethod() {
        Button button = new Button();
        if (activePort != null) {
            for (SerialPort port : ports) {
                if (port.getSystemPortName().equals(activePort)) {
                    port.openPort(0);
                    currentport=port;
                    Connect connects = new Connect(port, outputfield);
                    connects.start();
                    outputfield.appendText("Connect\n");
                    System.out.println("Connection");
                }
            }
        } else System.out.println("Please choose port");
    }
    public void onClickSendMethod() throws InterruptedException {
        Button button=new Button();
        if (selectedCommand!=null) {
            for(String st: listcommands){
                Thread.sleep(350);
                String command = st;
                byte[] byteArray = command.getBytes();
                currentport.writeBytes(byteArray, byteArray.length);
            }
        }
    }

    private SerialPort ports[];
    private SerialPort currentport;
    private String activePort;
    private String selectedCommand;
    private ArrayList<String> listcommands;
    @FXML
    public Button connect;
    @FXML
    public Button send;
    @FXML
    public TextArea outputfield;
    @FXML
    public ComboBox comboxcommand;
    @FXML
    public ComboBox combox;
    @FXML
    public Label labelcomport;
    @FXML
    public Label labelcommand;
    @FXML
    public Label labeloutput;
    @FXML
    public ScrollPane scroll;


    @FXML
    private void SelectPort(ActionEvent actionEvent) {
        activePort = combox.getSelectionModel().getSelectedItem().toString();
    }

    @FXML
    private void SelectCommand(ActionEvent actionEvent) {
        selectedCommand = comboxcommand.getSelectionModel().getSelectedItem().toString();
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ports = SerialPort.getCommPorts();
        ArrayList<String> listPorts = new ArrayList<>();
        for (int i = 0; i < ports.length; i++) {
            listPorts.add(ports[i].getSystemPortName());
        }
        ObservableList<String> list1 = FXCollections.observableArrayList(listPorts);
        combox.setItems(list1);
        listcommands = new ArrayList<String>() {{
            add("{\"command\" : \"connection_data\",\"server_ip\":\"127.0.0.1\",\"server_port\":\"8002\"}");
            add("{\"command\" : \"get_test_suites\"}");
            add("{\"command\" : \"get_test_list\",\"suite_name\" : \"testSuite\"}");
            add("{\"command\" : \"set_global_param\",\"param\" : [\"param_name\":\"log_level\", \"param_value\":\"debug\"]}");
            add("{\"command\" : \"run_test\",\"suite_name\" : \"testSuite1\",\"test_name\" : \"test1\",\"exec_id\":\"23456\",\"parameters\" : [\"param_name\":\"mem\", \"param_value\":\"all off 2000\"]}");
            add("{\"command\" : \"get_last_test\",\"suite_name\" : \"testSuite1\",\"test_name\" : \"test1\",\"exec_id\":\"23456\"}");
            add("{\"command\" : \"async_run_test\",\"suite_name\" : \"testSuite1\",\"test_name\" : \"test1\",\"exec_id\":\"23456\",\"parameters\" : [\"param_name\":\"mem\", \"param_value\":\"all off 2000\"]}");
        }};
        ObservableList<String> list2 = FXCollections.observableArrayList(listcommands);
        comboxcommand.setItems(list2);
    }
}
