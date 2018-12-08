package entity;

import lombok.Data;

import javax.persistence.*;

/**
 * @program: blog
 * @description:
 * @author: Hao Qin
 * @create: 21:17 12/04/2018
 **/
@Entity
@Data
@Table(name = "t_content")
public class Content {

    @Id
    @GeneratedValue
    private Integer id;

    private String title;

    private String slug;

    private Integer created;

    private Integer modified;

    @ManyToOne
    @JoinColumn(name = "authorId")
    private User author;

    private String type;

    private String status;

    private String tags;

    private String categories;

    private String hits;

    private Integer commentsNum;

    private Boolean allowComment;

    private Boolean allowPing;

    private Boolean allowFeed;

    private String content;
}
