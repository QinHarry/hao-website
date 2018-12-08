package entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 21:06 12/04/2018
 **/
@Entity
@Data
@Table(name = "t_user")
public class User {

    @Id
    @GeneratedValue
    private Integer id;

    private String username;

    private String password;

    private String email;

    private String homeUrl;

    private String displayName;

    private Integer created;

    private Integer activated;

    private Integer logged;

    private String groupName;
}
