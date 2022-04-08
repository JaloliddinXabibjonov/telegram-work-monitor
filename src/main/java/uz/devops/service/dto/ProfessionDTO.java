package uz.devops.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.util.Objects;
import javax.validation.constraints.*;

/**
 * A DTO for the {@link uz.devops.domain.Profession} entity.
 */
@Schema(description = "Роли")
public class ProfessionDTO implements Serializable {

    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, professionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProfessionDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            "}";
    }
}
