import { Component, OnInit } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { ActivatedRoute } from '@angular/router';
import { Observable } from 'rxjs';
import { finalize } from 'rxjs/operators';

import SharedModule from 'app/shared/shared.module';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { ContentFormService, ContentFormGroup } from './content-form.service';
import { IContent } from '../content.model';
import { ContentService } from '../service/content.service';
import { AlertError } from 'app/shared/alert/alert-error.model';
import { EventManager, EventWithContent } from 'app/core/util/event-manager.service';
import { DataUtils, FileLoadError } from 'app/core/util/data-util.service';

@Component({
  standalone: true,
  selector: 'jhi-content-update',
  templateUrl: './content-update.component.html',
  imports: [SharedModule, FormsModule, ReactiveFormsModule],
})
export class ContentUpdateComponent implements OnInit {
  isSaving = false;
  content: IContent | null = null;

  editForm: ContentFormGroup = this.contentFormService.createContentFormGroup();

  constructor(
    protected dataUtils: DataUtils,
    protected eventManager: EventManager,
    protected contentService: ContentService,
    protected contentFormService: ContentFormService,
    protected activatedRoute: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.activatedRoute.data.subscribe(({ content }) => {
      this.content = content;
      if (content) {
        this.updateForm(content);
      }
    });
  }

  byteSize(base64String: string): string {
    return this.dataUtils.byteSize(base64String);
  }

  openFile(base64String: string, contentType: string | null | undefined): void {
    this.dataUtils.openFile(base64String, contentType);
  }

  setFileData(event: Event, field: string, isImage: boolean): void {
    this.dataUtils.loadFileToForm(event, this.editForm, field, isImage).subscribe({
      error: (err: FileLoadError) =>
        this.eventManager.broadcast(new EventWithContent<AlertError>('fileUploaderApp.error', { ...err, key: 'error.file.' + err.key })),
    });
  }

  previousState(): void {
    window.history.back();
  }

  save(): void {
    this.isSaving = true;
    const content = this.contentFormService.getContent(this.editForm);
    if (content.id !== null) {
      this.subscribeToSaveResponse(this.contentService.update(content));
    } else {
      this.subscribeToSaveResponse(this.contentService.create(content));
    }
  }

  protected subscribeToSaveResponse(result: Observable<HttpResponse<IContent>>): void {
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

  protected updateForm(content: IContent): void {
    this.content = content;
    this.contentFormService.resetForm(this.editForm, content);
  }
}
