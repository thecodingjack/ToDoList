package com.jack.todolist.datamodel;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

/**
 * Created by lamkeong on 5/29/2017.
 */
public class TodoData {//creating singleton class for saving data
    private static TodoData instance = new TodoData(); //declaring and initializing instance
    private static String filename = "TodoListItems.txt";

    private ObservableList<TodoItem> todoItems;//observable list enables data binding
    private DateTimeFormatter formatter;

    public static TodoData getInstance(){
        return instance;
    }

    private TodoData(){//private constructor to make sure singleton and only one instance of the class is constructed
        formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
    }

    public ObservableList<TodoItem> getTodoItems(){//getter of todoitems list
        return todoItems;
    }


    public void addTodoItem(TodoItem item){
        todoItems.add(item);
    }

//    public void setTodoItems(List<TodoItem> todoItems){//setter of todoitemslist
//        this.todoItems = todoItems;
//    }

    public void loadTodoItems() throws IOException{
        todoItems = FXCollections.observableArrayList();//loading todoItems?
        Path path = Paths.get(filename);//setting path to filename
        BufferedReader br = Files.newBufferedReader(path);//Read from saved file?

        String input;

        try{
            while((input= br.readLine()) != null){//while there is a line
                String[] itemPieces = input.split("\t");
                String shortDescription = itemPieces[0];
                String details = itemPieces[1];
                String dateString= itemPieces[2];

                LocalDate date = LocalDate.parse(dateString,formatter);//converting string date back to local date
                TodoItem todoItem = new TodoItem(shortDescription,details,date);
                todoItems.add(todoItem);
            }
        } finally {
            if (br != null) {//if exception and br is not empty, close br
                br.close();
            }
        }
    }

    public void storeTodoItems() throws IOException{
        Path path = Paths.get(filename);
        BufferedWriter bw = Files.newBufferedWriter(path);//write to the saved file
        try{
            Iterator<TodoItem> iter = todoItems.iterator(); //creating an iterator of todoitem object type from the list
            while(iter.hasNext()){
                TodoItem item = iter.next();
                bw.write(String.format("%s\t%s\t%s\n",
                        item.getShortDescription(),
                        item.getDetails(),
                        item.getDeadline().format(formatter)));
            }
        } finally {
            if(bw != null){
                bw.close();
            }
        }
    }

    public void deleteTodoItem(TodoItem item){
        todoItems.remove(item);

    }
}

