package eci.cvds.armario.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "prendasUsuarios")
public class PrendaUsuario {
    @Id
    @Column(name = "id")
    @GeneratedValue(generator="uuid2")
    @GenericGenerator(name="uuid2", strategy = "uuid2")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "prenda_id", nullable = false)
    private Prenda prenda;

    @ManyToOne
    @JoinColumn(name = "USER_ID", nullable = false)
    private User user;
    public PrendaUsuario(Prenda prenda, User user) {
        this.prenda = prenda;
        this.user = user;
    }
}
