package uz.devops.command;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SimpleResultData<T> {

    private String message;

    private Boolean success;

    private T data;

    public SimpleResultData(String message, Boolean success) {
        this.message = message;
        this.success = success;
    }
}
