import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static antSword.Utils.*;

public class Main {

    public static void main(String[] args) throws Exception {

        //根目录
        String baseDir = new File(Main.class.getClassLoader().getResource("").getFile()).getAbsolutePath();
        //模板目录
        String tplDir = baseDir + File.separator + "template";
        //输出模板目录
        String outputDir = new File("").getAbsolutePath() + File.separator + "dist";

        List<String> classNames = new ArrayList<>();
        File directory = new File(baseDir);
        copyDirectory(Paths.get(tplDir), Paths.get(outputDir));

        // Check if the directory exists and is a directory
        if (!directory.exists() || !directory.isDirectory()) {
            System.out.println("[-] Invalid directory path: " + baseDir);
        }

        // Recursively search for .class files in the directory
        findClassesRecursive(directory, "", classNames);

        if (classNames.isEmpty()) {
            System.out.println("[-] No class files found in the directory.");
        } else {
            System.out.println("[+] Found classes in the directory:");
            for (String className : classNames) {
                System.out.println("Start to process class: "+className);
                Class<?> clz = Class.forName(className);
                String payload = genPayload(className);
                replaceJsTemplate(clz, payload, outputDir);
                System.out.println("[+] Generate template Success! :" + className);
                System.out.println("*********************************************************************************************");
            }
        }
    }

}
