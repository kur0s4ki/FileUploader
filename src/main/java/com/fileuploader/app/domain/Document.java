package com.fileuploader.app.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Document.
 */
@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Document implements Serializable {

  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @NotNull @Column(name = "title", nullable = false) private String title;

  @NotNull @Column(name = "size", nullable = false) private Long size;

  @Column(name = "mime_type") private String mimeType;

  @JsonIgnoreProperties(value = {"document"}, allowSetters = true)
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(unique = true)
  private Content content;

  @ManyToOne(optional = false)
  @NotNull
  @JsonIgnoreProperties(value = {"documents"}, allowSetters = true)
  private Car car;

  // jhipster-needle-entity-add-field - JHipster will add fields here

  public Long getId() { return this.id; }

  public Document id(Long id) {
    this.setId(id);
    return this;
  }

  public void setId(Long id) { this.id = id; }

  public String getTitle() { return this.title; }

  public Document title(String title) {
    this.setTitle(title);
    return this;
  }

  public void setTitle(String title) { this.title = title; }

  public Long getSize() { return this.size; }

  public Document size(Long size) {
    this.setSize(size);
    return this;
  }

  public void setSize(Long size) { this.size = size; }

  public String getMimeType() { return this.mimeType; }

  public Document mimeType(String mimeType) {
    this.setMimeType(mimeType);
    return this;
  }

  public void setMimeType(String mimeType) { this.mimeType = mimeType; }

  public Content getContent() { return this.content; }

  public void setContent(Content content) { this.content = content; }

  public Document content(Content content) {
    this.setContent(content);
    return this;
  }

  public Car getCar() { return this.car; }

  public void setCar(Car car) { this.car = car; }

  public Document car(Car car) {
    this.setCar(car);
    return this;
  }

  // jhipster-needle-entity-add-getters-setters - JHipster will add getters and
  // setters here

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Document)) {
      return false;
    }
    return id != null && id.equals(((Document)o).id);
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
    return "Document{"
        + "id=" + getId() + ", title='" + getTitle() + "'"
        + ", size=" + getSize() + ", mimeType='" + getMimeType() + "'"
        + "}";
  }
}
