package antSword;

import javassist.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Base64;
import java.util.List;

public class Utils {
    public static void copyDirectory(Path sourceDir, Path targetDir) throws IOException {
        // 创建目标目录
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir);
        }

        // 遍历源目录中的文件和子目录
        Files.walkFileTree(sourceDir, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                // 构建目标文件路径
                Path targetFile = targetDir.resolve(sourceDir.relativize(file));
                // 拷贝文件
                Files.copy(file, targetFile, StandardCopyOption.REPLACE_EXISTING);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                // 构建目标子目录路径
                Path targetSubDir = targetDir.resolve(sourceDir.relativize(dir));
                // 创建目标子目录
                Files.createDirectories(targetSubDir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static void findClassesRecursive(File directory, String packageName, List<String> classNames) {
        File[] files = directory.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                // Recurse into subdirectories with updated package name
                String newPackageName = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                findClassesRecursive(file, newPackageName, classNames);
            } else if (file.getName().endsWith(".class")) {
                // Remove the ".class" extension and add the class name to the list
                String className = file.getName().replace(".class", "");
                String fullName = packageName.isEmpty() ? className : packageName + "." + className;
                if (fullName.startsWith("antSword.") || fullName.equals("Main")) { // 排除工具类
                    continue;
                }
                classNames.add(fullName);
            }
        }
    }

    public static void replaceJsTemplate(Class clz, String base64Payload, String outputDir) throws Exception {
        String packageName = clz.getPackage().getName();
        String className = clz.getSimpleName();
        String jsFileName;
        File jsFile;
        if (packageName.startsWith("database")) {
            jsFileName = packageName.replace(".", "/") + ".js";
            jsFile = new File(outputDir, jsFileName);

        } else {
            jsFileName = packageName + ".js";
            jsFile = new File(outputDir, jsFileName);
        }
        if (jsFile.exists()) {

            String jsContent = new String(Files.readAllBytes(jsFile.toPath()));
            jsContent = jsContent.replace("###" + className + "###", base64Payload);

            Path outputPath = Paths.get(outputDir, jsFileName);
            Files.write(outputPath, jsContent.getBytes());
        } else {
            System.out.println("[-] jsFile not exists! :" + jsFile.getAbsolutePath());
        }
    }

    public static String genPayload(String className) throws Exception {
        // Step 1: 创建 ClassPool
        ClassPool pool = ClassPool.getDefault();

        // Step 2: 获取源类 ClassA
        CtClass classA = pool.get(className);

        // Step 3: 新建目标类 ClassB
        CtClass classB = pool.makeClass(classA.getName());

        // Step 4: 复制 ClassA 及其继承链的方法和属性到 ClassB
        copyInheritedMembers(classA, classB);

        // Step 5: 将目标类保存到文件中或加载到内存中
        classB.getClassFile().setVersionToJava5();

        classB.writeFile("./testclass"); // 将目标类保存到文件中，也可以使用 toClass() 将目标类加载到内存中

        byte[] bytes = classB.toBytecode();
        String s = new String(Base64.getEncoder().encode(bytes));
//      System.out.println(s);

        return s;
    }

    public static void copyInheritedMembers(CtClass sourceClass, CtClass targetClass)
            throws NotFoundException, CannotCompileException {
        // 复制源类的方法
        CtMethod[] methods = sourceClass.getDeclaredMethods();
        for (CtMethod method : methods) {
            System.out.println("  [+] Copying method: " + method.getLongName());
            // 检查方法在子类中是否存在
            try {
                targetClass.getDeclaredMethod(method.getName(), method.getParameterTypes());
                System.out.println("  [-] Method: " + method.getLongName() + " 已被子类重写，跳过");
            } catch (Exception e) {
                CtMethod copiedMethod = CtNewMethod.copy(method, targetClass, null);
                targetClass.addMethod(copiedMethod);
            }
        }

        // 复制源类的属性
        CtField[] fields = sourceClass.getDeclaredFields();
        for (CtField field : fields) {
            System.out.println("  [+] Copying field: " + field);
            try {
                targetClass.getDeclaredField(field.getName());
                System.out.println("  [-] Field: " + field.getName() + " 已被子类重写，跳过");
            } catch (Exception e) {
                CtField copiedField = new CtField(field.getType(), field.getName(), targetClass);
                copiedField.setModifiers(field.getModifiers());
                targetClass.addField(copiedField);
            }
        }

        // 复制父类的方法和属性
        CtClass superClass = sourceClass.getSuperclass();
        if (superClass != null && !superClass.getName().equals("java.lang.Object")) {
            System.out.println("  [+] find superClass: " + superClass.getName());
            copyInheritedMembers(superClass, targetClass);
        }
    }
}
