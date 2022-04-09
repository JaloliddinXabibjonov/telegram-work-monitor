package uz.devops.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO for the {@link uz.devops.domain.Profession} entity.
 */
@Schema(description = "Роли")
public class ProfessionDTO implements Serializable {

    /**
     * Наименование
     */
    @NotNull
    @Size(min = 3, max = 128)
    @Schema(description = "Наименование", required = true)
    private String name;

    /**
     * Описание профессии
     */
    @Schema(description = "Описание профессии")
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
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
