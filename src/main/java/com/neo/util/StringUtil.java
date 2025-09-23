package com.neo.util;

import lombok.experimental.UtilityClass;

import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@UtilityClass
public class StringUtil {
    public static void main(String[] args) {
        StringUtil.readFileLog("src/main/resources/static/images/banks");
    }

    public void readFileLog(String path) {
        try {
            try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(path))) {
                for (Path filePath : stream) {
                    System.out.println(filePath.getFileName().toAbsolutePath());
                }
            }
        } catch (Exception e) {
        }
    }
}
