import duke.AdditionalInfo;
import duke.Command;
import duke.Deadline;
import duke.Event;
import duke.Parser;
import duke.Storage;
import duke.Task;
import duke.TaskList;
import duke.ToDo;
import duke.Ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;


public class Duke {
    private Storage storage;
    private TaskList taskList;
    private Ui ui;

    private void execute(Command command) {
        String output = "";
        int commandType = command.getCommandType();
        boolean print = true;
        if (commandType == Command.LIST) {
            output += "Here are the tasks in your list:";
            for (int i = 0; i < taskList.size(); i++) {
                Task currentTask = taskList.get(i);
                String num = Integer.toString(i + 1);
                output += "\n" + num + "." + currentTask;
            }
        } else if (commandType == Command.DONE || commandType == Command.DELETE) {
            int taskIndex = command.getAdditionalInfo().getTaskIndex();
            Task selectedTask = taskList.get(taskIndex);
            if (commandType == Command.DONE) {
                selectedTask.markAsDone();
                output += "Nice! I've marked this task as done:\n  " + selectedTask;
            } else {
                taskList.remove(taskIndex);
                output += "Noted. I've removed this task:\n  " + selectedTask;
            }
            output += "\nNow you have " + taskList.size() + " tasks in the list.";
        } else if (commandType == Command.EXIT) {
            System.out.println("in exit case");
            storage.save(taskList);
            print = false;
        } else if (commandType == Command.INVALID) {
            print = false;
        } else {
            Task newTask;
            AdditionalInfo info = command.getAdditionalInfo();
            if (commandType == Command.CREATE_TODO) {
                newTask = new ToDo(info.getDescription());
            } else if (commandType == Command.CREATE_DEADLINE) {
                newTask = new Deadline(info.getDescription(), info.getDate(), info.getTime());
            } else {
                newTask = new Event(info.getDescription(), info.getTime());
            }
            taskList.add(newTask);
            output += "Got it. I've added this task:\n  " + newTask;
            output += "\nNow you have " + taskList.size() + " tasks in the list.";
        }
        if (print) {
            ui.printMessage(output);
        }
    }

    private void run() {
        start();
        runLoopUntilExit();
        exit();
    }

    private void runLoopUntilExit() {
        Command command;
        do {
            String input = ui.getUserInput();
            command = Parser.parse(input);
            this.execute(command);
        } while (command.getCommandType() != Command.EXIT);
    }

    private void start() {
        String filePath = "./data";
        String fileName = "data.txt";
        try {
            this.ui = new Ui();
            this.storage = new Storage(filePath + "/" + fileName);
            File dir = new File(filePath);
            File file = new File(filePath, fileName);
            if (dir.exists() && file.exists()) {
                this.taskList = new TaskList(storage.load());
            } else if (dir.exists()) {
                // case where only folder exist
                storage.createFile();
                this.taskList = new TaskList();
            } else {
                // case where folder does not exist
                dir.mkdir();
                storage.createFile();
                this.taskList = new TaskList();
            }
            ui.showWelcomeMessage();
        } catch (FileNotFoundException e) {
            ui.showStartFailedMessage();
            ui.printException(e);
            System.exit(1);
        } catch (IOException e) {
            ui.printException(e);
            System.exit(1);
        }
    }

    private void exit() {
        ui.showGoodbyeMessage();
        System.exit(0);
    }

    public static void main(String[] args) {
        new Duke().run();
    }
}
