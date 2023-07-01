import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize, map } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { DocumentFormService, DocumentFormGroup } from './document-form.service';
import { IDocument } from '../document.model';
import { DocumentService } from '../service/document.service';
import { IContent } from 'app/entities/content/content.model';
import { ContentService } from 'app/entities/content/service/content.service';
import { ICar } from 'app/entities/car/car.model';
import { CarService } from 'app/entities/car/service/car.service';

@Component({
  standalone: true,
  selector: 'jhi-document-update',
  templateUrl: './document-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class DocumentUpdateComponent implements OnInit {
  isSaving = false;
  document: IDocument | null = null;

  contentsCollection: IContent[] = [];
  carsSharedCollection: ICar[] = [];

  editForm: DocumentFormGroup = this.documentFormService.createDocumentFormGroup();

  constructor(
    protected documentService: DocumentService,
    protected documentFormService: DocumentFormService,
    protected contentService: ContentService,
    protected carService: CarService,
    protected activatedRoute: ActivatedRoute
  ) {}

  compareContent = (o1: IContent | null, o2: IContent | null): boolean => this.contentService.compareContent(o1, o2);

  compareCar = (o1: ICar | null, o2: ICar | null): boolean => this.carService.compareCar(o1, o2);

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ document }) => {
      this.document = document;
      if (document) {
        this.updateForm(document);
      }

      this.loadRelationshipsOptions();
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const document = this.documentFormService.getDocument(this.editForm);
    if (document.id !== null) {
      this.subscribeToSaveResponse(this.documentService.update(document));
    } else {
      this.subscribeToSaveResponse(this.documentService.create(document));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IDocument>>): void {
    result.pipe(finalize(() => this.onSaveFinalize())).subscribe({
      next: () => this.onSaveSuccess(),
      error: () => this.onSaveError(),
    });
  }

  protected onSaveSuccess(): void {
    this.previousState();
  }

  protected onSaveError(): void {
    // Api for inheritance.
  }

  protected onSaveFinalize(): void {
    this.isSaving = false;
  }

  protected updateForm(document: IDocument): void {
    this.document = document;
    this.documentFormService.resetForm(this.editForm, document);

    this.contentsCollection = this.contentService.addContentToCollectionIfMissing<IContent>(this.contentsCollection, document.content);
    this.carsSharedCollection = this.carService.addCarToCollectionIfMissing<ICar>(this.carsSharedCollection, document.car);
  }

  protected loadRelationshipsOptions(): void {
    this.contentService
      .query({ filter: 'document-is-null' })
      .pipe(map((res: HttpResponse<IContent[]>) => res.body ?? []))
      .pipe(map((contents: IContent[]) => this.contentService.addContentToCollectionIfMissing<IContent>(contents, this.document?.content)))
      .subscribe((contents: IContent[]) => (this.contentsCollection = contents));

    this.carService
      .query()
      .pipe(map((res: HttpResponse<ICar[]>) => res.body ?? []))
      .pipe(map((cars: ICar[]) => this.carService.addCarToCollectionIfMissing<ICar>(cars, this.document?.car)))
      .subscribe((cars: ICar[]) => (this.carsSharedCollection = cars));
  }
}
