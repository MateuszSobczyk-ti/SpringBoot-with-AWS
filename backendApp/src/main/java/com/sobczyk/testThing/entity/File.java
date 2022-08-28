package com.sobczyk.testThing.entity;

import com.sun.istack.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@NoArgsConstructor
@Entity
@Table(uniqueConstraints = {@UniqueConstraint(name="unique_name_constraints", columnNames="name")})
public class File {

    @Id
    @GeneratedValue
    private Long id;
    @NotBlank
    private String name;
    private String description;
    @NotBlank
    private String imageLink;

    public File(String name, String description, String imageLink) {
        this.name = name;
        this.description = description;
        this.imageLink = imageLink;
    }
}
