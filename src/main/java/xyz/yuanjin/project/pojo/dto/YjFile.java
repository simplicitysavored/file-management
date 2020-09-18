package xyz.yuanjin.project.pojo.dto;

import lombok.Data;
import xyz.yuanjin.project.pojo.config.SystemConfig;

import java.io.File;
import java.util.Map;

/**
 * @author yuanjin
 */
@Data
public class YjFile {
    private String absolutePath;
    private String parent;
    private String path;

    private YjFile parentFile;

    private File sourceFile;

    public YjFile() {
    }

    public YjFile(String path) {

        Map<String, String> pathIdMap = SystemConfig.getInstance().getListenFolderPathIdMap();
        for (Map.Entry<String, String> entry : pathIdMap.entrySet()) {
            String id = entry.getValue();
            String listenPath = entry.getKey();
            if (path.startsWith(id)) {
                path = path.replace(id, listenPath);
            }
        }

        File file = new File(path);
        this.sourceFile = file;
        for (Map.Entry<String, String> entry : pathIdMap.entrySet()) {
            String id = entry.getValue();
            String listenPath = entry.getKey();
            if (file.getAbsolutePath().startsWith(listenPath)) {
                this.absolutePath = file.getAbsolutePath().replace(listenPath, id);
                this.path = file.getPath().replace(listenPath, id);
                if (file.getParent().startsWith(listenPath)) {
                    this.parent = file.getParent().replace(listenPath, id);
                }
            }
        }
    }

    public boolean isListenRoot() {
        return SystemConfig.getInstance().getListenFolderIdMap().containsKey(path);
    }

    public YjFile getParentFile() {
        if (null == parentFile) {
            synchronized (YjFile.class) {
                if (null == parentFile) {
                    if (null != sourceFile.getParent()) {
                        if (SystemConfig.getInstance().getListenFolderIdAliasMap().containsKey(this.path)) {
                            return null;
                        }
                        this.parentFile = new YjFile(sourceFile.getParent());
                    }
                }
            }
        }
        return this.parentFile;
    }

    public boolean exists() {
        return this.sourceFile.exists();
    }

    public boolean isDirectory() {
        return this.sourceFile.isDirectory();
    }

    public long lastModified() {
        return this.sourceFile.lastModified();
    }

    public String getName() {
        return this.sourceFile.getName();
    }

    public boolean canRead() {
        return sourceFile.canRead();
    }

    public boolean canWrite() {
        return sourceFile.canWrite();
    }

    public boolean isHidden() {
        return sourceFile.isHidden();
    }

    public YjFile[] listFiles() {
        File[] files = sourceFile.listFiles();
        if (null == files) {
            return null;
        }

        YjFile[] yjFiles = new YjFile[files.length];
        for (int i = 0; i < files.length; i++) {
            yjFiles[i] = new YjFile(files[i].getAbsolutePath());
        }

        return yjFiles;
    }

    public boolean isFile() {
        return sourceFile.isFile();
    }

    public Long length() {
        return sourceFile.length();
    }

    public boolean mkdirs() {
        return sourceFile.mkdirs();
    }
}
