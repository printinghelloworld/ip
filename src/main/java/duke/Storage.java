package duke;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class Storage {
    String filePath;

    public Storage(String filePath) {
        this.filePath = filePath;
    }

    // read file contents and return a String
    private static String readFileContents(String filePath) throws FileNotFoundException {
        File f = new File(filePath);
        String content = "";
        Scanner s = new Scanner(f);
        while (s.hasNext()) {
            content += s.nextLine() + System.lineSeparator();
        }
        s.close();
        return content;
    }

    private static ArrayList<Task> createTaskList(String content) {
        ArrayList<Task> list = new ArrayList<>();
        Scanner s = new Scanner(content);
        while (s.hasNext()) {
            String line = s.nextLine();
            String[] arr = line.split("\\|");
            String type = arr[0].trim();
            int status = Integer.parseInt(arr[1].trim());
            String description = arr[2].trim();
            if (type.equals("T")) {
                list.add(new ToDo(description, status == Task.DONE));
            } else {
                String additionalInfo = arr[3].trim();
                if (type.equals("D")) {
                    String dateInString = additionalInfo;
                    String time = arr[4].trim();
                    list.add(new Deadline(description, status == Task.DONE, LocalDate.parse(dateInString, Common.BASIC_FORMATTER), time));
                } else {
                    list.add(new Event(description, additionalInfo, status == Task.DONE));
                }
            }
        }
        s.close();
        return list;
    }

    /**
     * Creates an ArrayList of Tasks using the contents of the file.
     * @return An ArrayList of Tasks.
     * @throws FileNotFoundException If the file does not exist in the FilePath.
     */
    public ArrayList<Task> load() throws FileNotFoundException{
        String contentsInString = Storage.readFileContents(this.filePath);
        return createTaskList(contentsInString);
    }

    /**
     * Creates a file to so that we are able to save all the tasks in the TaskList.
     * @throws IOException If the directory of the file being created doesn't exist.
     */
    public void createFile() throws IOException {
        File file = new File(this.filePath);
        file.createNewFile();
    }

    private static void writeToFile(String filePath, String textToAdd) throws IOException {
        FileWriter fw = new FileWriter(filePath);
        fw.write(textToAdd);
        fw.close();
    }

    //TODO: Throw DukeException

    /**
     * Saves all the tasks in TaskList to a text file.
     * @param taskList TaskList containing all the existing tasks entered by user.
     */
    public void save(TaskList taskList) {
        String dataToSave = "";
        for (int i = 0; i < taskList.size(); i++) {
            Task currentTask = taskList.get(i);
            if (i == taskList.size() - 1){
                dataToSave += currentTask.toText();
            }
            else {
                dataToSave += currentTask.toText() + System.lineSeparator();
            }
        }
        try {
            writeToFile(this.filePath, dataToSave);
        } catch (IOException e) {
            System.out.println("Error saving data to disk");
            e.printStackTrace();
        }
    }
}
