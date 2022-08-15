package uz.devops.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.devops.domain.enumeration.CommandStatus;

import javax.persistence.*;
import java.io.Serializable;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class Button implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(length = 128, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CommandStatus status;

    private String role;

    @Column(nullable = false)
    private String command;

    private String description;


    public Button(String name, CommandStatus status, String role, String command) {
        this.name = name;
        this.status = status;
        this.role = role;
        this.command = command;
    }

    public Button(String name, CommandStatus status, String command) {
        this.name = name;
        this.status = status;
        this.command = command;
    }

}
