package com.jack.todolist;

import com.jack.todolist.datamodel.TodoData;
import com.jack.todolist.datamodel.TodoItem;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.util.Callback;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import static javafx.scene.text.FontWeight.BOLD;

public class Controller {
    //this is where you create instances of objects and parsing fxid object

    private List<TodoItem> todoItems;

@FXML
    private ListView<TodoItem> todoListView;


//    public void handleClickListView(){
//        TodoItem item = todoListView.getSelectionModel().getSelectedItem();//casting as todoitem because this method returns a default object type, or specify Element type for listview class
//        StringBuilder sb = new StringBuilder(item.getDetails());
//        sb.append("\n\n\n\n" + "Due: " + item.getDeadline());
//        itemDetailsTextArea.setText(item.getDetails());
//        deadlineLabel.setText(item.getDeadline().toString());
//    }
    @FXML
    private TextArea itemDetailsTextArea;

    @FXML
    private ToggleButton filterToggleButton;

    @FXML
    private Label deadlineLabel;

    @FXML
    private BorderPane mainBorderPane;

    @FXML
    private ContextMenu listContextMenu;

    private SortedList<TodoItem> sortedList;
    private FilteredList<TodoItem> filteredList;

    private Predicate<TodoItem> wantAllItems;
    private Predicate<TodoItem> wantTodayItems;

    public void initialize(){
//        TodoItem item1 = new TodoItem("Buy grocery", "Buy grocery for the week",
//                LocalDate.of(2017, Month.MAY, 31));
//        TodoItem item2 = new TodoItem("Get a job", "Apply for programming job",
//                LocalDate.of(2017, Month.JUNE, 15));
//        todoItems = new ArrayList<TodoItem>();
//        todoItems.add(item1);
//        todoItems.add(item2);
//
//        TodoData.getInstance().setTodoItems(todoItems);
        wantAllItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return true;
            }
        };

        wantTodayItems = new Predicate<TodoItem>() {
            @Override
            public boolean test(TodoItem todoItem) {
                return (todoItem.getDeadline().equals(LocalDate.now()));
            }
        };

        filteredList = new FilteredList<TodoItem>(TodoData.getInstance().getTodoItems(), wantAllItems);


        sortedList = new SortedList<TodoItem>(filteredList, new Comparator<TodoItem>() {
            @Override
            public int compare(TodoItem o1, TodoItem o2) {
                return o1.getDeadline().compareTo(o2.getDeadline());
            }
        });

        //todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());//import items from ArrayList to ListView
//        todoListView.setItems(TodoData.getInstance().getTodoItems());//databinding enable automatic setting and updating listview
        todoListView.setItems(sortedList);
        listContextMenu = new ContextMenu();
        MenuItem deleteMenuItem = new MenuItem("Delete");
        deleteMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                deleteItem(item);
            }
        });


        listContextMenu.getItems().addAll(deleteMenuItem);
        todoListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);//set to single selection mode
        todoListView.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TodoItem>() {//listener is a property and you need to add listener before actually selecting
            @Override
            public void changed(ObservableValue<? extends TodoItem> observable, TodoItem oldValue, TodoItem newValue) {
                if (newValue != null){
                    TodoItem item = todoListView.getSelectionModel().getSelectedItem();
                    itemDetailsTextArea.setText(item.getDetails());
                    DateTimeFormatter df= DateTimeFormatter.ofPattern("M/d/yy");
                    deadlineLabel.setText(df.format(item.getDeadline()));
                }
            }
        });
        todoListView.getSelectionModel().selectFirst();

        todoListView.setCellFactory(new Callback<ListView<TodoItem>, ListCell<TodoItem>>() {
            @Override//cell factory let you customize each item on listview separately
            public ListCell<TodoItem> call(ListView<TodoItem> param) {
                ListCell<TodoItem> cell = new ListCell<TodoItem>(){
                    @Override
                    protected void updateItem(TodoItem item, boolean empty) {
                        super.updateItem(item, empty);
                        if(empty){
                            setText(null);
                        } else{
                            setText(item.getShortDescription());//customizing cell factory parse specific item field instead of object ref into listcell
                            if(item.getDeadline().equals(LocalDate.now())){
                                setTextFill(Color.RED);
                                Font f = this.getFont();
                                setFont(Font.font( f.getName(),BOLD,12));

                            }
                            if(item.getDeadline().isBefore(LocalDate.now())){
                                setTextFill(Color.DARKRED);
                            }
                        }
                    }
                };

                cell.emptyProperty().addListener( //lambda exp for not showing delete on null cell
                        (obs,wasEmpty,isNowEmpty) ->{
                            if(isNowEmpty){
                                cell.setContextMenu(null);
                            } else{
                                cell.setContextMenu(listContextMenu);
                            }
                        }
                );
                return cell;
            }
        });
    }

    @FXML
    public void shownewItemDialog(){
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.initOwner(mainBorderPane.getScene().getWindow());
        dialog.setTitle("Add New Todo Item");
        dialog.setHeaderText("Use this dialog to create a new todo item");
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(getClass().getResource("todoItemDialog.fxml"));
        try{
//            Parent root = FXMLLoader.load(getClass().getResource("todoItemDialog.fxml"));//referencing dialog fxml?
//            dialog.getDialogPane().setContent(root);//setting up dialog
            dialog.getDialogPane().setContent(fxmlLoader.load());

        }catch(IOException e){
            System.out.println("Couldn't load the dialog");
            e.printStackTrace();
            return;
        }

        dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();//this code shows the dialog pane and pass button clicked to result
        if(result.isPresent() && result.get()== ButtonType.OK){
            DialogController controller = fxmlLoader.getController(); //assign current controller to an object and cast as Dialog controller class
            controller.processResults();
//            todoListView.getItems().setAll(TodoData.getInstance().getTodoItems());//Updating the listview with the list stored in data after adding new item
            todoListView.getSelectionModel().selectLast();
        }
    }

    public void deleteItem(TodoItem item){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);//show delete confirmation
        alert.setTitle("Delete Todo Item");
        alert.setHeaderText("Delete item: " + item.getShortDescription());
        alert.setContentText("Are you sure? Press OK to confirm, or CANCEL to exit");
        Optional<ButtonType> result = alert.showAndWait();

        if(result.isPresent() && (result.get() == ButtonType.OK)){
//            TodoData.getInstance().deleteTodoItem(item);
            Alert alert1 = new Alert(Alert.AlertType.CONFIRMATION);//show delete confirmation
            alert1.setTitle("Delete Todo Item");
            alert1.setHeaderText("Delete item: " + item.getShortDescription());
            alert1.setContentText("Are you really sure? ");
            Optional<ButtonType> result1 = alert1.showAndWait();
            if(result.isPresent() && (result1.get() == ButtonType.OK)){
                TodoData.getInstance().deleteTodoItem(item);
            }
            if(result.isPresent() && (result1.get() == ButtonType.CANCEL)){
                Alert message = new Alert(Alert.AlertType.INFORMATION);
                message.setContentText("Sorry, too late bro..Entry deleted");
                message.show();
                TodoData.getInstance().deleteTodoItem(item);
            }
        }
    }

    public void handleKeyPressed(KeyEvent keyEvent){
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(selectedItem != null){
            if(keyEvent.getCode().equals(KeyCode.DELETE)){
                deleteItem(selectedItem);
            }
        }
    }

    public void handleFilterButton(){
        TodoItem selectedItem = todoListView.getSelectionModel().getSelectedItem();
        if(filterToggleButton.isSelected()){
            filteredList.setPredicate(wantTodayItems);
            if(filteredList.isEmpty()){
                itemDetailsTextArea.clear();//clear text if there is no item
                deadlineLabel.setText("");
            }else if(filteredList.contains(selectedItem)){
                todoListView.getSelectionModel().select(selectedItem);
            }else{
                todoListView.getSelectionModel().selectFirst();
            }

        }else{
            filteredList.setPredicate(wantAllItems);
            todoListView.getSelectionModel().selectFirst();
        }
    }

    public void handleExitButton(){
        Platform.exit();


    }
}
