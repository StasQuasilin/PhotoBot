package entity;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "_user_access")
public class UserAccess {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "_id")
    private int id;
    @Basic
    @Column(name = "_chat_id")
    private long chatId;
    @Basic
    @Column(name = "_access_key")
    private String password;
}
