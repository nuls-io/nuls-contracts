package io.nuls.vote.contract.model;

import io.nuls.contract.sdk.Address;

import java.math.BigInteger;
import java.util.List;

public class VoteEntity {

    private Long id;
    private String title;
    private String desc;
    private VoteConfig config;
    private int status;
    private Address owner;
    private BigInteger recognizance;
    private List<VoteItem> items;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public VoteConfig getConfig() {
        return config;
    }

    public void setConfig(VoteConfig config) {
        this.config = config;
    }

    public List<VoteItem> getItems() {
        return items;
    }

    public void setItems(List<VoteItem> items) {
        this.items = items;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Address getOwner() {
        return owner;
    }

    public void setOwner(Address owner) {
        this.owner = owner;
    }

    public BigInteger getRecognizance() {
        return recognizance;
    }

    public void setRecognizance(BigInteger recognizance) {
        this.recognizance = recognizance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteEntity that = (VoteEntity) o;

        if (status != that.status) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (title != null ? !title.equals(that.title) : that.title != null) return false;
        if (desc != null ? !desc.equals(that.desc) : that.desc != null) return false;
        if (config != null ? !config.equals(that.config) : that.config != null) return false;
        if (owner != null ? !owner.equals(that.owner) : that.owner != null) return false;
        if (recognizance != null ? !recognizance.equals(that.recognizance) : that.recognizance != null) return false;
        return items != null ? items.equals(that.items) : that.items == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (desc != null ? desc.hashCode() : 0);
        result = 31 * result + (config != null ? config.hashCode() : 0);
        result = 31 * result + status;
        result = 31 * result + (owner != null ? owner.hashCode() : 0);
        result = 31 * result + (recognizance != null ? recognizance.hashCode() : 0);
        result = 31 * result + (items != null ? items.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ", \"title\": \"" + title + "\"" +
                ", \"desc\": \"" + desc + "\"" +
                ", \"config\": " + config +
                ", \"status\": " + status +
                ", \"owner\": \"" + owner + "\"" +
                ", \"recognizance\": " + recognizance +
                ", \"items\": " + items +
                '}';
    }
}
