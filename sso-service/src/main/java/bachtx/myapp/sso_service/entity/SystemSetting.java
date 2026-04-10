package bachtx.myapp.sso_service.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "system_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemSetting {
    @Id
    @Column(name = "setting_key", nullable = false, unique = true)
    private String key;

    @Column(name = "setting_value")
    private String value;

    @Column(name = "description")
    private String description;
}
