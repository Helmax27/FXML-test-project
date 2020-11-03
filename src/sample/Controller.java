package sample;

import com.fazecast.jSerialComm.SerialPort;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

public class Controller implements Initializable {

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
    private SerialPort ports[];
    private SerialPort currentport;
    private Connect connection;
    private String activePort;
    private String selectedCommand;
    private ArrayList<String> listcommands;
    private HashMap<String, String> commandsMap;

    public void onClickMethod() {
        Button button = new Button();
        if (activePort != null) {
            for (SerialPort port : ports) {
                if (port.getSystemPortName().equals(activePort)) {
                    port.openPort(0);
                    currentport = port;
                    connection = new Connect(port, outputfield);
                    connection.start();
                    outputfield.appendText("Connect\n");
                    System.out.println("Connection");
                }
            }
        } else System.out.println("Please choose port");
    }

    public void onClickSendMethod() throws InterruptedException {
        Button button = new Button();
        if (selectedCommand != null) {
            Thread.sleep(1000);
            String command = commandsMap.get(selectedCommand);
            byte[] byteArray = command.getBytes();
            currentport.writeBytes(byteArray, byteArray.length);
            Thread.sleep(1000);
            String answer = connection.answer;
            JsonReader reader = new JsonReader(new StringReader(answer));
            reader.setLenient(true);
            if (answer.contains("get_test_suites")) {
                answer = "{\"answer\" :\"get_test_suites\",\"status\":\"pass\",\"list\": [{\"test_name\":\"testSuite1\"}, {\"test_name\":\"testSuite2\"}]}";
            }
            Answer jsonAnswer = new Gson().fromJson(answer, Answer.class);
            List<Lists> ls = new ArrayList<>();
            List<Logs> lg = new ArrayList<>();
            List<Parameters> parametersList = new ArrayList<>();
            if (jsonAnswer != null){
                if (jsonAnswer.list != null) {
                    for (Lists lst : jsonAnswer.list) {
                        ls.add(lst);
                        if (lst.parameters != null) {
                            for (Parameters pr : lst.parameters) {
                                parametersList.add(pr);
                            }
                        }
                    }
                }
                if (jsonAnswer.logs != null) {
                    for (Logs log : jsonAnswer.logs) {
                        lg.add(log);
                    }
                }
                outputfield.appendText(jsonAnswer.toString().replace("[", "").replace("]", "").replace(", test", "test"));
                connection.answer = "";
            }


        }
    }

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
        commandsMap = new HashMap<String, String>() {{
            put("connection data", "{\"command\" : \"connection_data\",\"server_ip\":\"127.0.0.1\",\"server_port\":\"8002\"}");
            put("get test suites", "{\"command\" : \"get_test_suites\"}");
            put("get test list", "{\"command\" : \"get_test_list\",\"suite_name\" : \"testSuite\"}");
            put("set global param", "{\"command\" : \"set_global_param\",\"param\" : [\"param_name\":\"log_level\", \"param_value\":\"debug\"]}");
            put("run test", "{\"command\" : \"run_test\",\"suite_name\" : \"testSuite1\",\"test_name\" : \"test1\",\"exec_id\":\"23456\",\"parameters\" : [\"param_name\":\"mem\", \"param_value\":\"all off 2000\"]}");
            put("get last test", "{\"command\" : \"get_last_test\",\"suite_name\" : \"testSuite1\",\"test_name\" : \"test1\",\"exec_id\":\"23456\"}");
            put("async run test", "{\"command\" : \"async_run_test\",\"suite_name\" : \"testSuite1\",\"test_name\" : \"test1\",\"exec_id\":\"23456\",\"parameters\" : [\"param_name\":\"mem\", \"param_value\":\"all off 2000\"]}");
        }};
        listcommands = new ArrayList<String>() {{
            for (String command : commandsMap.keySet()) {
                add(command);
            }
        }};
        ObservableList<String> list2 = FXCollections.observableArrayList(listcommands);
        comboxcommand.setItems(list2);
    }
}
