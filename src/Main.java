import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


public class Main {

    static StringBuilder log = new StringBuilder();
    static List <String> saveListToArchive = new ArrayList<>();

    public static void newFolder(String folderPath) {
        File dir = new File(folderPath);
        if (dir.mkdir())
            log.append("Директория создана: ").append(folderPath).append("\n");
        else
            log.append("Не удалось создать директорию: ").append(folderPath).append("\n");
    }

    public static void newFile(String filePath, String fileName) {
        File file = new File(filePath, fileName);
        try {
            if (file.createNewFile())
                log.append("Файл ").append(fileName).append(" создан в директории: ").append(filePath).append("\n");
            else
                log.append("Не удалось создать файл ").append(fileName).append(" в директории: ").append(filePath).append("\n");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveGame(GameProgress save, String filePath, String fileName) {
        File file = new File(filePath, fileName);
        try (FileOutputStream fos = new FileOutputStream(filePath + "/"+ fileName);
             ObjectOutputStream oos = new ObjectOutputStream(fos)) {
            oos.writeObject(save);
            saveListToArchive.add(filePath + "/"+ fileName);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static void openZip (String zipFilePath, String unpackedFilePath) {
        try (ZipInputStream zin = new ZipInputStream(new
                FileInputStream(zipFilePath))) {
            ZipEntry entry;
            String name;
            while ((entry = zin.getNextEntry()) != null) {
                name = entry.getName();
                FileOutputStream fout = new FileOutputStream(unpackedFilePath+"/"+ name);
                saveListToArchive.add(unpackedFilePath+"/"+ name);
                for (int c = zin.read(); c != -1; c = zin.read()) {
                    fout.write(c);
                }
                fout.flush();
                zin.closeEntry();
                fout.close();
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        File file = new File(zipFilePath);
        file.delete();
    }

    public static GameProgress openProgress (String saveGamePath) {
        GameProgress gameProgress = null;
        try (FileInputStream fis = new FileInputStream(saveGamePath);
             ObjectInputStream ois = new ObjectInputStream(fis)) {
            gameProgress = (GameProgress) ois.readObject();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return gameProgress;
    }




    public static void zipFiles(String zipFilePath, List<String> saveListToArchive) {
        try (ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFilePath))) {
            for (String filePath : saveListToArchive) {
                File fileToZip = new File(filePath);
                try (FileInputStream fis = new FileInputStream(fileToZip)) {
                    ZipEntry entry = new ZipEntry(fileToZip.getName());
                    zout.putNextEntry(entry);
                    byte[] buffer = new byte[fis.available()];
                    fis.read(buffer);
                    zout.write(buffer);
                    zout.closeEntry();
                }
            }
        }
        catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        for (String filePath : saveListToArchive) {
            File file = new File(filePath);
            if (!file.getPath().equals(zipFilePath)) {
                file.delete();
            }
        }
        saveListToArchive.clear();
    }



    public static void main(String[] args) {
        List <String> directoryList = new ArrayList<>(Arrays.asList("D://Games/src","D://Games/res",
                "D://Games/savegames","D://Games/temp","D://Games/src/main","D://Games/src/test",
                "D://Games/res/drawables","D://Games/res/vectors","D://Games/res/icons"));
        for (String directory : directoryList) {
            newFolder (directory);
        }
        newFile("D://Games/src/main", "Main.java");
        newFile("D://Games/src/main", "Utils.java");
        newFile("D://Games/temp", "temp.txt");
        GameProgress save1 = new GameProgress(100,2,5,34.55);
        GameProgress save2 = new GameProgress(80,3,7,55.55);
        GameProgress save3 = new GameProgress(56,4,22,134.15);
        saveGame(save1,"D://Games/savegames","save1.dat");
        saveGame(save2,"D://Games/savegames","save2.dat");
        saveGame(save3,"D://Games/savegames","save3.dat");
        zipFiles("D://Games/savegames/zip.zip",saveListToArchive);
        openZip("D://Games/savegames/zip.zip","D://Games/savegames");
        System.out.println(openProgress("D://Games/savegames/save2.dat"));
        zipFiles("D://Games/savegames/zip2.zip",saveListToArchive);

        try (FileWriter writer = new FileWriter("D://Games/temp/temp.txt", false)) {
            writer.write(String.valueOf(log));
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }

    }
}