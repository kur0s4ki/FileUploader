package com.fileuploader.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Content.
 */
@Entity
@Table(name = "content")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Content implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Lob @Column(name = "data", nullable = false) private byte[] data;

  @NotNull
  @Column(name = "data_content_type", nullable = false)
  private String dataContentType;

  @JsonIgnoreProperties(value = {"content", "car"}, allowSetters = true)
  @OneToOne(fetch = FetchType.LAZY, mappedBy = "content")
  private Document document;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() { return this.id; }

  public Content id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) { this.id = id; }

  public byte[] getData() { return this.data; }

  public Content data(byte[] data) {
    this.setData(data);
    return this;
  }

  public void setData(byte[] data) { this.data = data; }

  public String getDataContentType() { return this.dataContentType; }

  public Content dataContentType(String dataContentType) {
    this.dataContentType = dataContentType;
    return this;
  }

  public void setDataContentType(String dataContentType) {
    this.dataContentType = dataContentType;
  }

  public Document getDocument() { return this.document; }

  public void setDocument(Document document) {
    if (this.document != null) {
      this.document.setContent(null);
    }
    if (document != null) {
      document.setContent(this);
    }
    this.document = document;
  }

  public Content document(Document document) {
    this.setDocument(document);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
  // setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Content)) {
      return false;
    }
    return id != null && id.equals(((Content)o).id);
  }

  @Override
  public int hashCode() {
    // see
    // https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
    return getClass().hashCode();
  }

  // prettier-ignore
  @Override
  public String toString() {
    return "Content{"
        + "id=" + getId() + ", data='" + getData() + "'"
        + ", dataContentType='" + getDataContentType() + "'"
        + "}";
  }
}
