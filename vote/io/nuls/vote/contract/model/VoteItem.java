package io.nuls.vote.contract.model;

public class VoteItem {

    private Long id;
    private String content;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VoteItem voteItem = (VoteItem) o;

        if (id != null ? !id.equals(voteItem.id) : voteItem.id != null) return false;
        return content != null ? content.equals(voteItem.content) : voteItem.content == null;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (content != null ? content.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "{" +
                "\"id\": " + id +
                ", \"content\": \"" + content + "\"" +
                "}";
    }
}
