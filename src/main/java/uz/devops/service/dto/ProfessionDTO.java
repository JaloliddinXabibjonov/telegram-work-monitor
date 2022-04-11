package uz.devops.service.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.domain.Profession} entity.
 */
@ApiModel(description = "Роли")
public class ProfessionDTO implements Serializable {

    /**
     * Наименование
     */
    @NotNull
    @Size(min = 3, max = 128)
    @ApiModelProperty(value = "Наименование", required = true)
    private String name;

    /**
     * Описание профессии
     */
    @ApiModelProperty(value = "Описание профессии")
    private String description;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProfessionDTO)) {
            return false;
        }

        ProfessionDTO professionDTO = (ProfessionDTO) o;
        if (this.name == null) {
            return false;
        }
        return Objects.equals(this.name, professionDTO.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfessionDTO{" +
            "name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
