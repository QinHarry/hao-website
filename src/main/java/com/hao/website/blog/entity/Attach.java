package com.hao.website.blog.entity;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Entity
@Data
@Table(name = "t_attach")
@EntityListeners(AuditingEntityListener.class)
public class Attach {

    public Attach() {

    }

    public Attach(User user, @NotNull String fileName, String fileType, @NotNull String fileKey) {
        this.user = user;
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileKey = fileKey;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @CreatedDate
    private Timestamp created;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fname")
    @NotNull
    private String fileName;

    @Column(name = "ftype")
    private String fileType;

    @Column(name = "fkey")
    @NotNull
    private String fileKey;
}
