import java.io.FileWriter;
import java.io.IOException;
/**
 * This class handle the creation of the latex file.
 */
public class TexHandler {
    /**
     * This method creates the latex file to create the parse tree.
     * @param fileName The name of the new file to create.
     * @param content The content of the latex file.
     */
  public static void createTreeTex(String fileName, String content) {
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