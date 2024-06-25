package org.eclipse.jakarta.hello;

import jakarta.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

@Entity
public class Task implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long taskId;

    @Version
    private Long version;
    private String inputUrl;

    @Enumerated(EnumType.STRING)
    private Status status;
    private Instant createdAt;
    private Instant updatedAt;
    private Instant completedAt;

    @ElementCollection
    @MapKeyColumn(name = "meta_key")
    @Column(name = "meta_value")
    @CollectionTable(name = "meta_data", joinColumns = @JoinColumn(name = "task_id"))
    private Map<String, String> metaData;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getInputUrl() {
        return inputUrl;
    }

    public void setInputUrl(String inputUrl) {
        this.inputUrl = inputUrl;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }


    @PrePersist
    public void onPrePersist() {
        this.createdAt = Instant.now();
        this.status = Status.NEW;
    }

    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = Instant.now();
    }

    public Map<String, String> getMetaData() {
        return metaData;
    }

    public void setMetaData(Map<String, String> metaData) {
        this.metaData = metaData;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}