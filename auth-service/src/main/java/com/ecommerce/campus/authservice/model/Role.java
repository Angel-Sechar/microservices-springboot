
package com.ecommerce.campus.authservice.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "auth_role")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    /// Constructor
    public Role(String role){
        this.roleName = role;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name", nullable = false, unique = true, length = 50)
    private String roleName;

}