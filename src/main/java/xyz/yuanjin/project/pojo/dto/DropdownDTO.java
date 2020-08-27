package xyz.yuanjin.project.pojo.dto;

import lombok.Data;

import java.io.File;

/**
 * 下拉框的DTO
 *
 * @author yuanjin
 */
@Data
public class DropdownDTO {
    /**
     * 值
     */
    private String value;
    /**
     * 描述
     */
    private String desc;

    public DropdownDTO(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public DropdownDTO() {
    }

    public static DropdownDTO loadFile(File file) {
        DropdownDTO dto = new DropdownDTO();
        dto.setValue(file.getAbsolutePath());
        dto.setDesc(file.getName());
        return dto;
    }
}
