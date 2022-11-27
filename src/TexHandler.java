import java.io.FileWriter;
import java.io.IOException;

public class TexHandler {
  public static void createTreeTex(String fileName, String content) {
    System.out.println("here");
    try {
        FileWriter myWriter = new FileWriter(fileName);
        myWriter.write(content);
        myWriter.close();
        System.out.println("The tree has been successfully created in the "+fileName+" file.");
      } catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
      }
  }
}