package com.example.first.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "system_configs")
public class SystemConfig {

    @Id
    private String configKey;

    private String configValue;
}
