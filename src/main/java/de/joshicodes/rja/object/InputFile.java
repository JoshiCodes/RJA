package de.joshicodes.rja.object;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public abstract class InputFile {

    public static InputFile of(InputStream inputStream) {
        return new InputFile() {
            @Override
            public String getName() {
                return "file";
            }

            @Override
            public File getFile() {
                return null;
            }

            @Override
            public InputStream getStream() {
                return inputStream;
            }
        };
    }

    public static InputFile of(File file) {
        return new InputFile() {
            @Override
            public String getName() {
                return file.getName();
            }

            @Override
            public File getFile() {
                return file;
            }
        };
    }

    public static InputFile of(String name, File file) {
        return new InputFile() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public File getFile() {
                return file;
            }
        };
    }

    abstract public String getName();
    abstract public File getFile();

    public InputStream getStream() throws FileNotFoundException {
        return new FileInputStream(getFile());
    }

}
