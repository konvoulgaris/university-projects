package tools;

import java.io.File;
import java.io.FileWriter;

public class Logger {
    private File file;
    private FileWriter writer;

    private boolean work = false;

    public Logger(String name, boolean work) {
        this.work = work;

        if(work) {
            file = new File(name);
    
            if(file.exists()) {
                file.delete();
            }
    
            try {
                writer = new FileWriter(file);
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void destroy() {
        if(work) {
            try {
                writer.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void write(String message) {
        if(work) {
            if(message.contains(":")) {
                message = message.split(":")[0];
            }
    
            try {
                writer.write(message + "\n");
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }
}
