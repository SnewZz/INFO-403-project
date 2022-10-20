import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

class Main{
    public static void main(String[] args) {
        if(args.length == 0){
            throw new IllegalArgumentException("At least one argument needed!");
        }
        try {
            File f = new File(args[0]);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;
            while((line = br.readLine()) != null){
                sb.append(line);
                sb.append("\n");
            }
            fr.close();    
            System.out.println("Contenu du fichier: ");
            System.out.println(sb.toString());
        } catch(IOException e){
            e.printStackTrace();
            System.exit(-1);
        } 
    }

}